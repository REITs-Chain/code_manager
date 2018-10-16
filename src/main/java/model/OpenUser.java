package model;

import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;


public class OpenUser extends model.BaseEntity implements java.io.Serializable {
	public static final String _tableName = "t_open_user";
	public static final String _keyName = "id";

	// Fields
	//常用验证方式
	/*
	@Max(value=100,message="不能大于100")
	@Min(value=0,message="不能小于0")
	@NotNull(message="不能为null")
	@NotBlank(message="不能为空！")
	@NotEmpty(message="不能为空！")
	@Past(message="不能大于今天！")
	@Future(message="不能小于今天！")
	@Email(message="电子邮箱地址格式 不正确！")
	@Range(min=0,max=100,message="只能输入0至100之间的数字！")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Pattern(regexp="",message="输入的格式不正确！")*/

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private java.lang.Integer id;
	private java.lang.String phoneNum;
	private java.lang.String password;
	private java.lang.Integer type;
	private java.lang.String realName;
	private java.lang.String idNum;
	private java.lang.String photo1;
	private java.lang.String photo2;
	private java.lang.String linkMan;
	private java.lang.String linkPhone;
	private java.lang.Integer status;
	private java.util.List<model.OpenApp> openApps;


	// Constructors

	/** default constructor */
	public OpenUser() {
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
	public java.lang.String getIdNum() {
		return this.idNum;
	}
	public void setIdNum(java.lang.String idNum) {
		this.idNum=idNum;
	}
	public java.lang.String getPhoto1() {
		return this.photo1;
	}
	public void setPhoto1(java.lang.String photo1) {
		this.photo1=photo1;
	}
	public java.lang.String getPhoto2() {
		return this.photo2;
	}
	public void setPhoto2(java.lang.String photo2) {
		this.photo2=photo2;
	}
	public java.lang.String getLinkMan() {
		return this.linkMan;
	}
	public void setLinkMan(java.lang.String linkMan) {
		this.linkMan=linkMan;
	}
	public java.lang.String getLinkPhone() {
		return this.linkPhone;
	}
	public void setLinkPhone(java.lang.String linkPhone) {
		this.linkPhone=linkPhone;
	}
	public java.lang.Integer getStatus() {
		return this.status;
	}
	public void setStatus(java.lang.Integer status) {
		this.status=status;
	}
	public java.util.List<model.OpenApp> getOpenApps() {
		return this.openApps;
	}
	public void setOpenApps(java.util.List<model.OpenApp> value) {
		this.openApps=value;
	}


}
