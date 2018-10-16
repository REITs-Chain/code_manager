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


public class OpenApp extends model.BaseEntity implements java.io.Serializable {
	public static final String _tableName = "t_open_app";
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
	private java.lang.Integer openUserId;
	private java.lang.String appName;
	private java.lang.String appId;
	private java.lang.String secure;
	private java.lang.String icon;
	private model.OpenUser openUser;


	// Constructors

	/** default constructor */
	public OpenApp() {
	}

	public java.lang.Integer getId() {
		return this.id;
	}
	public void setId(java.lang.Integer id) {
		this.id=id;
	}
	public java.lang.Integer getOpenUserId() {
		return this.openUserId;
	}
	public void setOpenUserId(java.lang.Integer openUserId) {
		this.openUserId=openUserId;
	}
	public java.lang.String getAppName() {
		return this.appName;
	}
	public void setAppName(java.lang.String appName) {
		this.appName=appName;
	}
	public java.lang.String getAppId() {
		return this.appId;
	}
	public void setAppId(java.lang.String appId) {
		this.appId=appId;
	}
	public java.lang.String getSecure() {
		return this.secure;
	}
	public void setSecure(java.lang.String secure) {
		this.secure=secure;
	}
	public java.lang.String getIcon() {
		return this.icon;
	}
	public void setIcon(java.lang.String icon) {
		this.icon=icon;
	}
	public model.OpenUser getOpenUser() {
		return this.openUser;
	}
	public void setOpenUser(model.OpenUser value) {
		this.openUser=value;
	}


}
