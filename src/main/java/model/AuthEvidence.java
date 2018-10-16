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


public class AuthEvidence extends model.BaseEntity implements java.io.Serializable {
	public static final String _tableName = "t_auth_evidence";
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
	private java.lang.Integer userId;
	private java.lang.String orginFile;
	private java.lang.String newFile;
	private model.User user;


	// Constructors

	/** default constructor */
	public AuthEvidence() {
	}

	public java.lang.Integer getId() {
		return this.id;
	}
	public void setId(java.lang.Integer id) {
		this.id=id;
	}
	public java.lang.Integer getUserId() {
		return this.userId;
	}
	public void setUserId(java.lang.Integer userId) {
		this.userId=userId;
	}
	public java.lang.String getOrginFile() {
		return this.orginFile;
	}
	public void setOrginFile(java.lang.String orginFile) {
		this.orginFile=orginFile;
	}
	public java.lang.String getNewFile() {
		return this.newFile;
	}
	public void setNewFile(java.lang.String newFile) {
		this.newFile=newFile;
	}
	public model.User getUser() {
		return this.user;
	}
	public void setUser(model.User value) {
		this.user=value;
	}


}
