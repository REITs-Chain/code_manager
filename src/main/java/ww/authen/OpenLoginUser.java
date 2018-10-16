package ww.authen;

import java.io.Serializable;

public class OpenLoginUser implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4678277158713299355L;
	
	private java.lang.Integer id;
	private java.lang.String phoneNum;
	private java.lang.String password;
	private java.lang.Integer type;
	private java.lang.String realName;
	
	public int getUserid() {
		return id;
	}
	public void setUserid(int userid) {
		this.id = userid;
	}
	public String getUsername() {
		return phoneNum;
	}
	public void setUsername(String username) {
		this.phoneNum = username;
	}

	public java.lang.Integer getId() {
		return this.id;
	}
	public void setId(java.lang.Integer id) {
		this.id=id;
	}
	public java.lang.String getPhoneNum() {
		return this.phoneNum;
	}
	public void setPhoneNum(java.lang.String phoneNum) {
		this.phoneNum=phoneNum;
	}
	public java.lang.String getPassword() {
		return this.password;
	}
	public void setPassword(java.lang.String password) {
		this.password=password;
	}
	public java.lang.Integer getType() {
		return this.type;
	}
	public void setType(java.lang.Integer type) {
		this.type=type;
	}
	public java.lang.String getRealName() {
		return this.realName;
	}
	public void setRealName(java.lang.String realName) {
		this.realName=realName;
	}
	
	
	
}
