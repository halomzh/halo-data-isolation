# halo-data-isolation
## 介绍

halo-data-isolation是基于mybatis的数据隔离器，适用于接口响应结果与当前登陆人拥有权限相关的场景，比如多租户场景。

使用方式主要分三种：数据隔离拦截器统一数据隔离（一般为指定名称的AbstractDataIsolationHandlerInterceptor的子类），@DataIsolationContext注解在方法层进行数据隔离，DataIsolationContextUtils工具对指定代码块进行数据隔离

## 开始

### 建表、初始化数据

```sql
/*
 Navicat Premium Data Transfer

 Source Server         : 本地
 Source Server Type    : MySQL
 Source Server Version : 50726
 Source Host           : 127.0.0.1:3306
 Source Schema         : demo

 Target Server Type    : MySQL
 Target Server Version : 50726
 File Encoding         : 65001

 Date: 28/03/2021 00:05:41
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户名',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '邮件',
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '经纬度',
  `tag_a` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '标记a',
  `tag_b` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '标记a',
  `tag_c` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '标记a',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES (1, '张三', 'zhangsan@163.com', '地址1', '1', '2', '3');
INSERT INTO `t_user` VALUES (2, '李四', 'lisi@163.com', '地址2', '2', '3', '4');
INSERT INTO `t_user` VALUES (3, '王五', 'wangwu@163.com', '地址2', '3', '3', '4');
INSERT INTO `t_user` VALUES (4, '赵六', 'zhaoliu@163.com', '地址2', '4', '4', '4');
INSERT INTO `t_user` VALUES (5, '孙七', 'sunqi@163.com', '地址2', '5', '5', '5');

SET FOREIGN_KEY_CHECKS = 1;
```

### 添加依赖

```xml
<dependencies>
    <dependency>
        <groupId>org.example</groupId>
        <artifactId>halo-data-isolation-starter-spring</artifactId>
        <version>${project.version}</version>
        <exclusions>
            <exclusion>
                <groupId>javax.servlet</groupId>
                <artifactId>servlet-api</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>
</dependencies>
```

### 配置application.yml

```yaml
server:
  port: 8899
spring:
  application:
    name: example-app
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://localhost:3306/demo?useUnicode=true&characterEncoding=utf8
    username: root
    password: 123456
halo:
  data:
    isolation:
      enable: true #是否开启数据隔离器
      handler-interceptor-name: dataIsolationHandlerInterceptor #指定数据隔离拦截器名称


mybatis:
  mapper-locations: classpath:mapper/*.xml
```

### 编写指定的数据隔离拦截器

```java
@Component(value = "dataIsolationHandlerInterceptor")
public class DataIsolationHandlerInterceptor extends AbstractDataIsolationHandlerInterceptor {

   @Override
   protected DataIsolationInfo getDataIsolationInfo(HttpServletRequest httpServletRequest) {
      //理论上是要从当前登陆人的信息中获取的，因为只是个demo我就写死数据了
      DataIsolationInfo dataIsolationInfo = new DataIsolationInfo();
      dataIsolationInfo.setEnableIsolation(true);
      dataIsolationInfo.addFieldNameIncludeValue("tagA", "1");

      return dataIsolationInfo;
   }

}
```

### 编写dao

```java
@Mapper
public interface TUserDao {

   @Select("select * from t_user limit 0,10")
   List<TUser> selectAll();
}
```

### 编写controller

```java
@SpringBootApplication
@RestController
@RequestMapping("/example")
@Slf4j
public class App {

   @Autowired
   private TUserDao tUserDao;

   public static void main(String[] args) {
      SpringApplication.run(App.class, args);
   }

   /**
    * 数据拦截开启情况下，服务调用
    *
    * @return 查询结果
    */
   @GetMapping("/get/1")
   public Object get1() {
      List<TUser> tUsers = tUserDao.selectAll();

      return tUsers;
   }

   /**
    * 利用DataIsolationContextUtils关闭数据隔离器
    *
    * @return 查询结果
    */
   @GetMapping("/get/2")
   public Object get2() {
      DataIsolationContextUtils.quit();
      List<TUser> tUsers = tUserDao.selectAll();

      return tUsers;
   }

   /**
    * 对于指定代码块使用指定拦截信息
    *
    * @return 查询结果
    */
   @GetMapping("/get/3")
   public Object get3() {
      DataIsolationInfo dataIsolationInfo = new DataIsolationInfo();
      dataIsolationInfo.setEnableIsolation(true);
      dataIsolationInfo.addFieldNameIncludeValue("tagB", "3");
      DataIsolationContextUtils.enter(dataIsolationInfo);
      List<TUser> tUsers = tUserDao.selectAll();
      DataIsolationContextUtils.quit();
      return tUsers;
   }

   /**
    * 使用@DataIsolationContext判定该方法是否收数据隔离器管理，默认开启
    *
    * @return 查询结果
    */
   @GetMapping("/get/4")
   @DataIsolationContext
   public Object get4() {
      List<TUser> tUsers = tUserDao.selectAll();

      return tUsers;
   }

   /**
    * 使用@DataIsolationContext主动排除该方法，该方法将不受数据隔离器管理
    *
    * @return 查询结果
    */
   @GetMapping("/get/5")
   @DataIsolationContext(enableDataIsolation = false)
   public Object get5() {
      List<TUser> tUsers = tUserDao.selectAll();

      return tUsers;
   }

}
```

### 运行结果

![image-20210328120806739](https://raw.githubusercontent.com/halomzh/pic/master/20210328120815.png)

![image-20210328120851214](https://raw.githubusercontent.com/halomzh/pic/master/20210328120853.png)![image-20210328120926869](https://raw.githubusercontent.com/halomzh/pic/master/20210328120928.png)![](https://raw.githubusercontent.com/halomzh/pic/master/20210328121040.png)

![image-20210328121102211](https://raw.githubusercontent.com/halomzh/pic/master/20210328121103.png)

![image-20210328121151507](https://raw.githubusercontent.com/halomzh/pic/master/20210328121152.png)![image-20210328121212159](https://raw.githubusercontent.com/halomzh/pic/master/20210328121213.png)![image-20210328121239605](https://raw.githubusercontent.com/halomzh/pic/master/20210328121240.png)![image-20210328121302007](https://raw.githubusercontent.com/halomzh/pic/master/20210328121303.png)