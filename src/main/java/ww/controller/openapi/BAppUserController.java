package ww.controller.openapi;

import java.util.Date;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import model.OpenUser;
import ww.authen.ApiLoginContext;
import ww.authen.LoginUser;
import ww.authen.OpenApiContext;
import ww.authen.OpenLoginUser;
import ww.authen.OpenLoginUserUtil;
import ww.common.DbModel;
import ww.common.FileUploadUtil;
import ww.common.FileUploadUtil.UploadResult;
import ww.common.SqlList;
import ww.common.SqlMap;
import ww.common.WwLog;
import ww.common.WwSystem;
import ww.controller.BaseController;
import ww.service.admin.LoginlogService;
import ww.service.admin.OpenUserService;

@CrossOrigin(origins = "*", maxAge = 3600)  //跨域post请求
@Controller
@RequestMapping(value="/openapi/bappuser")
public class BAppUserController extends BaseController {
	
	@Autowired
	private OpenUserService openUserService;
	
	@Autowired
	private LoginlogService loginlogService;
	
	//获取企业用户信息、app列表
	@ResponseBody
	@RequestMapping(value="/getsecure")
	public JSONObject getSecure(String token,String appId,
			HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		OpenLoginUser luser=OpenApiContext.getUser(token);
		if(luser==null){
			result.put("success", false);	
			result.put("code", 101);
			result.put("message", "无效token");
			return result;
		}
		DbModel dao=new DbModel();
		SqlMap selectOne = dao.table("t_open_app").where("appId=:id").bind("id",appId).selectOne("appId,secure");
		if (selectOne==null) {
			result.put("success", false);	
			result.put("code", 2);
			result.put("message", "查询失败");
			return result;
		}
		result.put("data", selectOne);
		result.put("success", true);
		result.put("code", 0);
		result.put("message", "查询成功");
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value="/addOpenApp")
	public JSONObject addOpenApp(String token,
			HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		DbModel dao=new DbModel();
		OpenLoginUser luser=OpenApiContext.getUser(token);
		if(luser==null){
			result.put("success", false);	
			result.put("code", 101);
			result.put("message", "无效token");
			return result;
		}
		String appName=WwSystem.ToString(request.getParameter("appName"));
		if (appName==null) {
			result.put("success", false);	
			result.put("code", 1);
			result.put("message", "请输入正确的名称");
			return result;
		}
		SqlMap selectOne2 = dao.table("t_open_app").where("appName=:appName").bind("appName", appName).selectOne();
		if (selectOne2!=null) {
			result.put("success", false);	
			result.put("code", 5);
			result.put("message", "app名称不能重复");
			return result;
		}
		
		SqlMap selectOne = dao.table("t_open_user").where("id=:id").bind("id", luser.getId()).selectOne("status");
		//SqlMap selectOne = dao.table("t_open_user").where("id=:id").bind("id", 3).selectOne("status");
		if (selectOne.getInt("status")!=2) {
			result.put("success", false);	
			result.put("code", 3);
			result.put("message", "认证通过后，才可以添加");
			return result;
		}
		
		UploadResult ures= FileUploadUtil.updateOneImage(request, "/public/file_list/icons/", "icon");
		if(!ures.success){
			result.put("success", false);
			result.put("code", 4);
			result.put("message", "文件上传失败！");
			WwLog.getLogger(this).error("文件上传失败:"+ures.message);
			return result;
		}
		String fileName=ures.newFileName;
		
		String appId=WwSystem.UUID();
		String secure=WwSystem.UUID()+"_"+WwSystem.UUID();
		
		SqlMap data=new SqlMap();
		data.put("openUserId", luser.getId());
		data.put("appName", appName);
		data.put("appId", appId);
		data.put("secure", secure);
		data.put("icon", fileName);
		long insert = dao.table("t_open_app").insert(data);
		if (insert<0) {
			result.put("success", false);
			result.put("code", 2);
			result.put("message", "添加信息失败");
			return result;
		}
		result.put("success", true);
		result.put("code", 0);
		result.put("message", "添加成功");
		return result;
	}
	
	
	//获取企业用户信息、app列表
	@ResponseBody
	@RequestMapping(value="/getopenuserinfo")
	public JSONObject getOpenUserInfo(String token,
			HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		OpenLoginUser luser = OpenApiContext.getUser(token);
		if(luser==null){
			result.put("success", false);	
			result.put("code", 101);
			result.put("message", "无效token");
			return result;
		}
		DbModel dao=new DbModel();
		SqlMap selectOne = dao.table("t_open_user").where("id=:id").bind("id",luser.getId()).selectOne("id,phoneNum,realName,idNum,photo1,type,status");
		if (selectOne==null) {
			result.put("success", false);	
			result.put("code", 1);
			result.put("message", "请注册后，再登陆");
			return result;
		}
		
		result.put("data", selectOne);
		
		String rootUrl=WwSystem.getFullRoot(request);
		String  img=rootUrl+"/public/file_list/icons/";
		SqlList select = null;
		if (selectOne.getInt("status")==2) {
			//select=dao.table("t_open_app").where("openUserId=:openUserId").bind("openUserId", luser.getId()).select();
			select=dao.table("t_open_app").where("openUserId=:openUserId").bind("openUserId", luser.getId()).select("appId,appName,concat('"+img+"',icon) as icon");
		}
		
		result.put("list", select);
		result.put("success", true);
		result.put("code", 0);
		result.put("message", "成功");
		return result;
	}
	
	//base64--修改用户信息
	@ResponseBody
	@RequestMapping(value="/updateuser",method=RequestMethod.POST)
	public JSONObject updateUser(String token,String realName,String idNum,String photo1,int type,
			//Integer type,String photo2,
			HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
				
		OpenLoginUser luser=OpenApiContext.getUser(token);
		if(luser==null){
			result.put("success", false);	
			result.put("code", 101);
			result.put("message", "无效token");
			return result;
		}	
				
		if(realName==null){
			result.put("success", false);
			result.put("code", 2);
			result.put("message", "请填写企业名称或真实姓名");
			
			return result;
		}
		
		OpenUser openUser = openUserService.getById(luser.getUserid());
		openUser.setRealName(realName);
		openUser.setIdNum(idNum);
		openUser.setPhoto1(photo1);
		openUser.setType(type);
		
		if(!openUserService.Save(openUser)){
			result.put("success", false);
			result.put("code", 3);
			result.put("message", "保存信息失败");
			
			return result;
		}
		
		result.put("success", true);
		result.put("code", 0);
		result.put("message", "成功");
		
		return result;
	}
	//实名 -----带文件上传的修改用户信息
	@ResponseBody
	@RequestMapping(value="/updateuser2",method=RequestMethod.POST)
	public JSONObject updateUser2(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		
		String token=WwSystem.ToString(request.getParameter("token"));
		
		OpenLoginUser luser=OpenApiContext.getUser(token);
		if(luser==null){
			result.put("success", false);	
			result.put("code", 101);
			result.put("message", "已过期请重新登录");
			return result;
		}		
		
		String realName=WwSystem.ToString(request.getParameter("realName"));
		String idNum=WwSystem.ToString(request.getParameter("idNum"));
		DbModel dao=new DbModel();
		if(realName==null || idNum==null){
			result.put("success", false);
			result.put("code", 2);
			result.put("message", "必须填写");
			
			return result;
		}
		
		SqlMap selectOne = dao.table("t_open_user").where("idNum=:idNum").bind("idNum", idNum).selectOne();
		if (selectOne!=null) {
			result.put("success", false);
			result.put("code", 5);
			result.put("message", "证件号已被使用，请更换");
			
			return result;
		}
		//int type=ww.common.WwSystem.ToInt(request.getParameter("type"));
		
		
		UploadResult ures= FileUploadUtil.updateOneImage(request, "/public/file_list/busers/", "file");
		if(!ures.success){
			result.put("success", false);
			result.put("code", 4);
			result.put("message", "文件上传失败！");
			WwLog.getLogger(this).error("文件上传失败:"+ures.message);
			return result;
		}
		OpenUser openUser=openUserService.getById(luser.getUserid());
		String fileName=ures.newFileName;
		openUser.setRealName(realName);
		openUser.setIdNum(idNum);
		openUser.setPhoto1(fileName);
		openUser.setType(1);//0-个人    1-企业
		//openUser.setType(type);//0-个人    1-企业
		
		if(!openUserService.Save(openUser)){
			result.put("success", false);
			result.put("code", 3);
			result.put("message", "保存信息失败");
			
			return result;
		}
		
		result.put("success", true);
		result.put("code", 0);
		result.put("message", "请等待审核");
		
		return result;
	}
	//不带文件上传的修改用户信息
	@ResponseBody
	@RequestMapping(value="/updateuser3",method=RequestMethod.POST)
	public JSONObject updateUser3(String token,String realName,String idNum,
			//Integer type,String photo1,String photo2,
			HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
				
		LoginUser luser=ApiLoginContext.getUser(token);
		if(luser==null){
			result.put("success", false);	
			result.put("code", 101);
			result.put("message", "无效token");
			return result;
		}	
				
		if(realName==null){
			result.put("success", false);
			result.put("code", 2);
			result.put("message", "请填写企业名称或真实姓名");
			
			return result;
		}
		
		OpenUser openUser=openUserService.getById(luser.getUserid());
		openUser.setRealName(realName);//data.getNickName());
		openUser.setIdNum(idNum);//data.getGender());
		
		if(!openUserService.Save(openUser)){
			result.put("success", false);
			result.put("code", 3);
			result.put("message", "保存信息失败");
			
			return result;
		}
		
		result.put("success", true);
		result.put("code", 0);
		result.put("message", "成功");
		
		return result;
	}
	/**************非action*******************************/
	
	
	
	
	
}
