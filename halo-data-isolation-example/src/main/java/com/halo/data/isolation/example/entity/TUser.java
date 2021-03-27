package com.halo.data.isolation.example.entity;

import java.io.Serializable;

/**
 * (TUser)实体类
 *
 * @author makejava
 * @since 2021-03-28 00:03:05
 */
public class TUser implements Serializable {
	private static final long serialVersionUID = 996985942970770335L;
	/**
	 * 主键
	 */
	private Integer id;
	/**
	 * 用户名
	 */
	private String userName;
	/**
	 * 邮件
	 */
	private String email;
	/**
	 * 经纬度
	 */
	private String location;
	/**
	 * 标记a
	 */
	private String tagA;
	/**
	 * 标记a
	 */
	private String tagB;
	/**
	 * 标记a
	 */
	private String tagC;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getTagA() {
		return tagA;
	}

	public void setTagA(String tagA) {
		this.tagA = tagA;
	}

	public String getTagB() {
		return tagB;
	}

	public void setTagB(String tagB) {
		this.tagB = tagB;
	}

	public String getTagC() {
		return tagC;
	}

	public void setTagC(String tagC) {
		this.tagC = tagC;
	}

}
