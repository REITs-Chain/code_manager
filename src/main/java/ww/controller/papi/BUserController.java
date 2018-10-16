package ww.controller.papi;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.support.odps.udf.JSONContains;
import com.taobao.api.internal.parser.json.JsonConverter;

import ww.authen.OpenApiContext;
import ww.authen.OpenApiContext.ImageValidCodeInfo;
import ww.authen.OpenApiContext.PhoneValidCodeInfo;
import ww.authen.OpenLoginUser;
import ww.authen.OpenLoginUserUtil;
import ww.common.DbModel;
import ww.common.FileUploadUtil;
import ww.common.Md5Encrypt;
import ww.common.ModelDAO;
import ww.common.SmsUtil_heng;
import ww.common.SmsUtil_hk;
import ww.common.SmsUtil_yunpian;
import ww.common.SqlMap;
import ww.common.ValidateCode;
import ww.common.WwSystem;
import ww.controller.BaseController;

@CrossOrigin(origins = "*", maxAge = 3600)  //跨域post请求
@Controller
@RequestMapping(value="/papi/buser")
public class BUserController extends BaseController {
	
	
	//获取图片验证码
	@ResponseBody
	@RequestMapping("/getvalidcode")
	public JSONObject getValidcode(HttpServletRequest request,HttpServletResponse response) throws Exception{  
		ValidateCode vCode = new ValidateCode(100,46,4,10); 
		String validCode=vCode.createNewCode();
		String imageToken=WwSystem.UUID();
		OpenApiContext.setImageValidCode(imageToken, validCode);
		
		//大于5分钟后清除缓存
		//ApiLoginContext.clearImageValidCode();
		
		JSONObject result=new JSONObject();
		result.put("success", true);
		result.put("code", 0);
		result.put("message", "成功");
		result.put("token", imageToken);
		result.put("imageurl", "api/login/validcodeimage?img="+imageToken);
		
		return result;
	}
	
	//访问验证图片URL
	@RequestMapping("/validcodeimage")
	public void ValidcodeImage(String img,HttpServletRequest request,HttpServletResponse response) throws Exception{  
		String imageToken=img;
		response.setContentType("image/jpeg");  
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);  
		ValidateCode vCode = new ValidateCode(100,46,4,50);  
		
	    ImageValidCodeInfo vcinfo=OpenApiContext.getImageValidCode(imageToken);
	    if(vcinfo==null){
	    	throw new Exception("无效的token");
	    }
		
		vCode.createImage(vcinfo.ValidCode);
		
		//System.out.print(token+":"+vCode.getCode()); 
		
		vCode.write(response.getOutputStream()); 
	      
	} 
	
	//登录
	//@CrossOrigin(origins = "*", maxAge = 3600)  //跨域post请求
	@ResponseBody	
	@RequestMapping(value={"/dologin"},method=RequestMethod.POST)
	public JSONObject DoLogin(String imageToken, //注意这个token不是登录的token，而是获取图片验证码的token
			HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();		
		
		String username=WwSystem.ToString(request.getParameter("username"));
		String password=WwSystem.ToString(request.getParameter("password"));
		String validateCode=WwSystem.ToString(request.getParameter("validateCode"));
		
		OpenLoginUser user=OpenLoginUserUtil.getByUsername(username);
		if (user==null) {
			result.put("success", false);
			result.put("code", 1);
			result.put("message", "该用户不存在");
			return result;
		}
		//验证码比较
		ImageValidCodeInfo vcinfo=OpenApiContext.getImageValidCode(imageToken);
	    if(vcinfo==null){
	    	result.put("success", false);
			result.put("code", 2);
			result.put("message", "无效token，非法登录");
			return result;
	    }
		if(!vcinfo.ValidCode.equalsIgnoreCase(validateCode)){
			result.put("success", false);
			result.put("code", 3);
			result.put("message", "验证码输入错误");
			return result;
		}
			
		//密码比较
		String en_pw=Md5Encrypt.md5(password);
		if(!user.getPassword().equals(en_pw)){
			result.put("success", false);
			result.put("code", 4);
			result.put("message", "用户名或密码错误");
			return result;
		}
						
		result.put("success", true);
		result.put("code", 0);
		result.put("message", "登录成功");
		
		//make login token
		String loginToken=Md5Encrypt.md5(user.getUsername()+user.getPassword()+(new Date()).getTime());
		OpenApiContext.setToken(user, loginToken);
		result.put("token", loginToken);
		
		result.put("id", user.getUserid());
		result.put("realName", user.getRealName());
		return result;
	}
	
	//注册
	@ResponseBody
	@RequestMapping(value="/register",method=RequestMethod.POST)
	public JSONObject register(String phoneNum,String phoneValidCode, String password,
			HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		if(phoneNum==null||phoneValidCode==null||password==null){
			result.put("success", false);
			result.put("code", 1);
			result.put("message", "提交数据格式异常");
			return result;
		}	
		DbModel dao=new DbModel();
		SqlMap selectOne = dao.table("t_open_user").where("phoneNum=:phoneNum").bind("phoneNum", phoneNum).selectOne();
		if (selectOne!=null) {
			result.put("success", false);
			result.put("code", 6);
			result.put("message", "该手机号已注册，请更换手机号或请使用该号码登陆。");
			return result;
		}
		//手机验证码验证
		PhoneValidCodeInfo pvc=OpenApiContext.getPhoneValidCode(phoneNum);		
		if(pvc==null||phoneValidCode==null){
			result.put("success", false);
			result.put("code", 3);
			result.put("message", "没有验证码");
			return result;
		}		
		if(!pvc.ValidCode.equals(phoneValidCode)){
			result.put("success", false);
			result.put("code", 2);
			result.put("message", "验证码错误");
			return result;
		}
		
		OpenApiContext.removePhoneValidCode(phoneNum);
		//加密密码
		String en_pw=Md5Encrypt.md5(password);
		
		SqlMap data=new SqlMap();
		data.put("phoneNum", phoneNum);
		data.put("type", 1);
		data.put("password", en_pw);
		data.put("status", 1);
		long insert = dao.table("t_open_user").insert(data);
		if (insert<0) {
			result.put("success", false);
			result.put("code", 5);
			result.put("message", "保存注册信息失败");
			return result;
		}
		
		result.put("success", true);
		result.put("code", 0);
		result.put("message", "注册成功");
		return result;
	}
	
	//忘记密码
	@ResponseBody
	@RequestMapping(value="/forgetpassword",method=RequestMethod.POST)
	public JSONObject ForgetPassword(String phoneNum,String password,String phoneValidCode, 
			HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		DbModel dao=new DbModel();
		SqlMap selectOne = dao.table("t_open_user").where("phoneNum=:phoneNum").bind("phoneNum", phoneNum).selectOne();
		if (selectOne==null) {
			result.put("success", false);
			result.put("code", 1);
			result.put("message", "该用户不存在");
			return result;
		}
		
		//手机验证码验证
		PhoneValidCodeInfo pvc=OpenApiContext.getPhoneValidCode(phoneNum);	
		if(pvc==null||phoneValidCode==null){
			result.put("success", false);
			result.put("code", 2);
			result.put("message", "没有验证码");
			
			return result;
		}	
		pvc.Times++;
		if(pvc.Times>3){
			result.put("success", false);
			result.put("code", 3);
			result.put("message", "你已超过三次");
			
			return result;
		}
		if(!pvc.ValidCode.equals(phoneValidCode)){
			result.put("success", false);
			result.put("code", 4);
			result.put("message", "验证码错误");
			
			return result;
		}
		OpenApiContext.removePhoneValidCode(phoneNum);

	    String en_pw=Md5Encrypt.md5(password);
		SqlMap data=new SqlMap();
		data.put("password", en_pw);
		//修改密码
		int update = dao.table("t_open_user").where("phoneNum=:phoneNum").bind("phoneNum", phoneNum).update(data);
		if (update<0) {
			result.put("success", false);
			result.put("code", 5);
			result.put("message", "修改密码失败");
			return result;
		}
		
		result.put("success", true);
		result.put("code", 0);
		result.put("message", "修改密码成功");
		
		return result;
	}
	
	//获取短信验证码
	@ResponseBody
	@RequestMapping(value="/getregvalidcode")
	public JSONObject getRegValidCode(String phoneNum,
			HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		//String aa = request.getParameter("a");
		PhoneValidCodeInfo pvc= OpenApiContext.getPhoneValidCode(phoneNum);;
		int times=0;
		if(pvc!=null){
			times=pvc.Times+1;
			if(times>10){
				result.put("success", false);	
				result.put("code", 2);
				result.put("message", "此号码已达到最大次数");
				
				return result;
			}
		}
		String verifyNum = SmsUtil_yunpian.getNewVerifyNum();
		boolean b = SmsUtil_yunpian.singleSend_NRC(phoneNum, verifyNum);
		if(!b){
			result.put("success", false);	
			result.put("code", 1);
			result.put("message", "发送短信失败:"+SmsUtil_yunpian.message);
			//result.put("phoneValidCode", phoneValidCode);
			
			return result;
		}
		OpenApiContext.setPhoneValidCode(phoneNum, verifyNum,times);

		
		result.put("success", true);	
		result.put("code", 0);
		result.put("message", "短信已发送");
		//result.put("phoneValidCode", phoneValidCode);		
		
		return result;
	}
	
	//上传图片
	@ResponseBody
	@RequestMapping(value="/uploadimage")
	public JSONObject uploadImage(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		response.setHeader("Access-Control-Allow-Origin", "*");
		
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		//保存图片1
		MultipartFile mFile1 = multipartRequest.getFile("idPhoto1");
		if(mFile1.getSize()>1024*1024*10){
			result.put("success", false);
			result.put("code", 1);
			result.put("message", "省份证件1图片大小不能超过4M");
			
			return result;
		}
		MultipartFile mFile2 = multipartRequest.getFile("idPhoto2");
		if(mFile2.getSize()>1024*1024*10){
			result.put("success", false);
			result.put("code", 2);
			result.put("message", "省份证件2图片大小不能超过4M");
			
			return result;
		}
		MultipartFile mFile = multipartRequest.getFile("photo");
		if(mFile.getSize()>1024*1024*10){
			result.put("success", false);
			result.put("code", 3);
			result.put("message", "手持证件图片大小不能超过4M");
			
			return result;
		}
		
		FileUploadUtil.UploadResult res1=FileUploadUtil.updateOneImage(request, "/private/images/", "idPhoto1",0); 
		if(!res1.success){
			result.put("success", false);
			result.put("code", 4);
			result.put("message", res1.message);
			return result;
		}
		String filename1=res1.newFileName;
		
		//保存图片2
		FileUploadUtil.UploadResult res2=FileUploadUtil.updateOneImage(request, "/private/images/", "idPhoto2",0);
		if(!res2.success){
			result.put("success", false);
			result.put("code", 4);
			result.put("message", res2.message);
			return result;
		}
		String filename2=res2.newFileName;
		
		//保存图片3
		FileUploadUtil.UploadResult res=FileUploadUtil.updateOneImage(request, "/private/images/", "photo",0);
		if(!res.success){
			result.put("success", false);
			result.put("code", 4);
			result.put("message", res.message);
			return result;
		}
		String filename=res.newFileName;
		
		
		result.put("success", true);
		result.put("code", 0);
		result.put("message", "成功");
		result.put("idPhoto1", filename1);
		result.put("idPhoto2", filename2);
		result.put("photo", filename);
		
		
		return result;
	}
	
	
	
	
	
	
}
