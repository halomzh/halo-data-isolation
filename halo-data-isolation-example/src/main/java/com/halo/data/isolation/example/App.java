package com.halo.data.isolation.example;

import com.halo.data.isolation.annotation.DataIsolationContext;
import com.halo.data.isolation.example.dao.TUserDao;
import com.halo.data.isolation.example.entity.TUser;
import com.halo.data.isolation.holder.DataIsolationInfo;
import com.halo.data.isolation.utils.DataIsolationContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author shoufeng
 */

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
