package ww.controller.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONObject;

import model.BankcardVerif;
import model.Bonusaddress;
import model.Loginlog;
import model.User;
import model.UserRisk;
import ww.authen.ApiLoginContext;
import ww.authen.ApiLoginContext.PhoneValidCodeInfo;
import ww.authen.LoginUser;
import ww.common.DbModel;
import ww.common.FileUploadUtil;
import ww.common.FileUploadUtil.UploadResult;
import ww.common.Md5Encrypt;
import ww.common.ModelDAO;
import ww.common.SmsUtil_heng;
import ww.common.SqlList;
import ww.common.SqlMap;
import ww.common.WwLog;
import ww.common.WwSystem;
import ww.controller.BaseController;
import ww.faceverify.FaceVerify;
import ww.idcard.IDCard;
import ww.service.admin.BankcardVerifService;
import ww.service.admin.BonusaddressService;
import ww.service.admin.LoginlogService;
import ww.service.admin.UserRiskService;
import ww.service.admin.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)  //跨域post请求
@Controller
@RequestMapping(value="/api/vipuser")
public class VipUserController extends BaseController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private LoginlogService loginlogService;
	
	@Autowired
	private BonusaddressService bonusaddressService;
	
	@Autowired
	private BankcardVerifService bankcardService;
	
	@Autowired
	private UserRiskService userRiskService;
	
	@Autowired
	private BankcardVerifService bankverifService;
	
	//申请列表--用户申请加入群组
	@ResponseBody
	@RequestMapping(value="/applyGroupList")
	public JSONObject applyGroupList(String token,Integer groupId,
			HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		
		LoginUser luser=ApiLoginContext.getUser(token);
		if(luser==null){
			result.put("success", false);	
			result.put("code", 101);
			result.put("message", "无效token");
			return result;
		}
		
		String rootUrl=WwSystem.getFullRoot(request);
		String  img=rootUrl+"public/file_list/images/";
		
		DbModel dao=new DbModel();//申请人ID 头像    内容   群组名称  
		String sql=" SELECT  a.`id`, a.applyUserId,u.nickName, a.content,g.`name` as groupName, concat('"+img+"',u.avatar) as avatar  FROM t_group_apply a  ";
			   sql+=" LEFT JOIN t_user u  ON a.`applyUserId`=u.`id`  ";
			   sql+=" LEFT JOIN t_group   g ON g.`id`=a.`groupId` ";
			   sql+=" WHERE  a.groupId="+groupId+"  AND a.`status`=2  and a.groupHeadId="+luser.getUserid();
		//申请列表
		SqlList list = dao.sql(sql).query();
		if (list==null) {
			result.put("success", false);	
			result.put("code", 1);
			result.put("message", "没有申请记录");
			return result;
		}
		result.put("success", true);	
		result.put("code", 0);
		result.put("data", list);
		result.put("message", "查询成功");
		return result;
	}
	
	
	
	
	//创建社区群
	@ResponseBody
	@RequestMapping(value="/createGroup")
	public JSONObject createGroup(String token,String groupName,Integer minStarLevel,String describe,String icon,Integer communityId,
			HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		
		LoginUser luser=ApiLoginContext.getUser(token);
		if(luser==null){
			result.put("success", false);	
			result.put("code", 101);
			result.put("message", "无效token");
			return result;
		}
		if (groupName==null||groupName.isEmpty()) {
			result.put("success", false);	
			result.put("code", 1);
			result.put("message", "群名不能为空，请重新输入。");
			return result;
		}
		if (communityId==null) {
			communityId=0;
		}

		DbModel dao=new DbModel();
		
		//保存信息
		SqlMap data=new SqlMap();
		data.put("createPerson", luser.getUsername());
		data.put("createTime", ww.common.WwSystem.now());
		data.put("name", groupName);
		data.put("icon", icon);
		data.put("status", 2);//0-success 1-false 2-审核中
		data.put("synchron", 1);//0-已同步  1-未同步
		data.put("minStarLevel", minStarLevel);
		data.put("`describe`", describe);
		data.put("communityId", communityId);
		
		long res = dao.table("t_group").insert(data);
		
		if(res==-1){
			result.put("success", false);	
			result.put("code", 3);
			result.put("message", "添加群组失败。");
			WwLog.getLogger(this).error("(3)添加群组失败:"+dao.Message);
			return result;
		}
		result.put("success", true);	
		result.put("code", 0);
		result.put("message", "请等待管理员审核。");
		return result;
	}
	
	//获取社区群
	@ResponseBody
	@RequestMapping(value="/getAllGroup")
	public JSONObject getAllGroup(String token,String communityId,Integer assetId,
			HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		
		LoginUser luser=ApiLoginContext.getUser(token);
		if(luser==null){
			result.put("success", false);	
			result.put("code", 101);
			result.put("message", "无效token");
			return result;
		}
		DbModel dao1=new DbModel();
		SqlMap selectOne = dao1.table("t_user").where("id=:userId").bind("userId", luser.getUserid()).selectOne("id,avatar,nickName,walletAddress");
		if (selectOne==null) {
			result.put("success", false);	
			result.put("code", 1);
			result.put("message", "获取发送人信息失败。");
			return result;
		}
		//获取钱包地址
		String rootAddress=selectOne.getString("walletAddress");
		SqlList list = dao1.sql("call p_getbalancebyaddr('"+rootAddress+"',"+assetId+")").query();
		long balance=0;
		if(list.size()>0){
			balance=list.get(0).getLong("amount");
		}
		String sqlGrade=" SELECT  t1.grade FROM t_equity t1  WHERE t1.lrange<="+balance+" AND t1.hrange >"+balance;
		SqlList query2 = dao1.sql(sqlGrade).query();
		int grade=0;
		if(query2.size()>0){
			grade=query2.get(0).getInt("grade");
		}
		result.put("grade", grade);
		String sql="";
		//1--全部群,0--我的关注群
		sql+=" select g.id,g.name,g.icon,g.minStarLevel,u.id AS createPerson,";
		sql+=" CASE WHEN m.groupId IS NOT NULL THEN 0 ELSE 1 END AS isFollow ";
		sql+=" from t_group g  left join t_user_mygroup m on g.id=m.groupId and  m.userId="+luser.getUserid();
		sql+=" LEFT JOIN  t_user u ON u.phoneNum=g.createPerson ";
		sql+=" where g.communityId="+communityId+" and g.`status`=0 and g.`synchron`=0";
		SqlList query = dao1.sql(sql).query();
		if (query==null) {
			result.put("success", false);	
			result.put("code", 2);
			result.put("message", "获取信息失败。");
			return result;
		}
	//	query.get(0).put("userCount", 1);
		
		
		//获取社区的资产资料
		 SqlMap select = dao1.table("t_asset").where("id=:id").bind("id", assetId).selectOne("produtIntroUrl,issueDatum");
		if (select==null) {
			result.put("success", false);	
			result.put("code", 3);
			result.put("message", "该资产未发行");
			return result;
		}
		
		result.put("assetData", select);
		result.put("data", query);
		result.put("success", true);	
		result.put("code", 0);
		result.put("message", "已获取社区全部群。");
		
		return result;
	}

	
	//获取手机验证码 为兼容以前的前端代码
	@ResponseBody
	@RequestMapping(value="/getphonevalidcode")
	public JSONObject getPhoneValidCode(String phoneNum,
			HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		
		PhoneValidCodeInfo pvc= ApiLoginContext.getPhoneValidCode(phoneNum);
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
		
		String phoneValidCode=SmsUtil_heng.getNewVerifyNum();
		boolean b=SmsUtil_heng.sendVerifyNum_Reg(phoneNum, phoneValidCode);
		if(!b){
			result.put("success", false);	
			result.put("code", 1);
			result.put("message", "发送短信失败:"+SmsUtil_heng.message);
			
			return result;
		}
		
		ApiLoginContext.setPhoneValidCode(phoneNum, phoneValidCode,times);

		
		result.put("success", true);	
		result.put("code", 0);
		result.put("message", "短信已发送");	
		
		return result;
	}

	//验证并添加银行卡（补充开户行及所在地）
	@ResponseBody
	@RequestMapping(value="/setBankCard")
	public JSONObject setBankCard(String token,String bankCardNum,String openBankName,String bankAddress,
			HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		
		LoginUser luser=ApiLoginContext.getUser(token);
		if(luser==null){
			result.put("success", false);	
			result.put("code", 101);
			result.put("message", "无效token");
			return result;
		}
		if (bankCardNum==null || bankCardNum.isEmpty()) {
			result.put("success", false);	
			result.put("code", 0);
			result.put("message", "请输入卡号！");
			return result;
		}
		if (openBankName==null || openBankName.isEmpty()) {
			result.put("success", false);	
			result.put("code", 1);
			result.put("message", "请输入开户行！");
			return result;
		}
		if (bankAddress==null || bankAddress.isEmpty()) {
			result.put("success", false);	
			result.put("code", 2);
			result.put("message", "请输入开户行所在地！");
			return result;
		}
		String idcard = luser.getIdNum();
		String realName = luser.getRealName();
		
		//验证银行卡号是否已添加
		//ModelDAO dao=new ModelDAO();
		//SqlMap selectOne = dao.M("t_bankcard_info").where("bankCardNum = '"+bankCardNum+"'").selectOne();
		DbModel dao=new DbModel();
		SqlMap selectOne = dao.table("t_bankcard_info").where("bankCardNum=:bankCardNum").bind("bankCardNum", bankCardNum).selectOne();
		if (selectOne!=null) {
			result.put("success", false);	
			result.put("code", 1);
			result.put("message", "你已添加该银行卡，请重新输入！");
			return result;
		}
		
		IDCard idc=new IDCard();
		
		//调用接口验证银行卡号
		int code= idc.bankcard_verify(realName,bankCardNum,idcard);
		if (code==1000) {
			
			//获取银行卡信息
			JSONObject bankcardInfo = idc.getBankcardInfo(bankCardNum);
			String bankname = bankcardInfo.getJSONObject("data").getString("bankname");
			
			//保存信息
			SqlMap data=new SqlMap();
			data.put("userId", luser.getUserid());
			data.put("verifTime", ww.common.WwSystem.now());
			data.put("bankCardNum", bankCardNum);
			data.put("realName", realName);
			data.put("bankName", bankname);
			data.put("openBankName", openBankName);
			data.put("bankAddress", bankAddress);
			long res=dao.table("t_bankcard_info").insert(data);
			if(res!=1){
				System.out.println(dao.Message);
			}
			result.put("success", true);	
			result.put("code", 0);
			result.put("message", "银行卡验证通过。");
		}else if (code==1001) {
			result.put("success", false);	
			result.put("code", 1001);
			result.put("message", "卡号姓名不匹配!");
		}else if (code==1002) {
			result.put("success", false);	
			result.put("code", 1002);
			result.put("message", "无法认证!");
		}else if (code==1103) {
			result.put("success", false);	
			result.put("code", 1103);
			result.put("message", "银行卡号不合法!");
		}else {
			result.put("success", false);	
			result.put("code", 2000);
			result.put("message", "服务器内部错误，请刷新后，重新验证。");
		}
		return result;
	}

	//获取手机验证码
	@ResponseBody
	@RequestMapping(value="/getPhoneVerifyNum",method=RequestMethod.POST)
	public JSONObject getPhoneVerifyNum(String token,
			HttpServletRequest request,
			HttpServletResponse response){
		
		JSONObject result=new JSONObject();
		
		LoginUser user=ApiLoginContext.getUser(token);
		if(user==null){
			result.put("success", false);	
			result.put("code", 101);
			result.put("message", "无效token");
			return result;
		}
		
		//获取验证码
		String validCode = SmsUtil_heng.getNewVerifyNum();
		//获取手机号码
		String phoneNum = user.getUsername();
		int times=0;
		if (times>10) {
			result.put("success", false);	
			result.put("code", 2);
			result.put("message", "请求次数过多，休息一下！");
			return result;
		}
		//存放手机号、手机验证码以及次数
		ApiLoginContext.setPhoneValidCode(phoneNum, validCode, times);
		//发送手机验证码
		SmsUtil_heng.sendVerifyNum_common(phoneNum, validCode);
		result.put("success", true);
		result.put("code", 0);
		result.put("message", "验证码已发送。");
		return result;
	}	
	
	//设置转账密码
	@ResponseBody
	@RequestMapping(value="/transPassword",method=RequestMethod.POST)
	public JSONObject transPassword(String token,
			String verifyNum,
			String transPassword,
			HttpServletRequest request,
			HttpServletResponse response){
		
		JSONObject result=new JSONObject();
		//验证token
		LoginUser user=ApiLoginContext.getUser(token);
		if(user==null){
			result.put("success", false);	
			result.put("code", 101);
			result.put("message", "无效token");
			return result;
		}
		//获取手机号码
		String phoneNum = user.getUsername();
		if (phoneNum==null) {
			result.put("success", false);	
			result.put("code", 2);
			result.put("message", "未绑定手机");
			return result;
		}
		//取出手机验证码
		PhoneValidCodeInfo phoneValidCode = ApiLoginContext.getPhoneValidCode(phoneNum);
		if (!phoneValidCode.ValidCode.equals(verifyNum)) {
			result.put("success", false);	
			result.put("code", 1);
			result.put("message", "手机验证码错误");
			return result;
		}
		ModelDAO dao=new ModelDAO();
		String en_pw_trans="";
		if(transPassword==null||transPassword.isEmpty())
			en_pw_trans=""; //空表现不使用转账验证码
		else
			en_pw_trans=Md5Encrypt.md5(transPassword);
		//修改密码
		dao.M("t_user").where("id="+user.getUserid()).update("transPassword='"+en_pw_trans+"'");
		result.put("success", true);
		result.put("code", 0);
		result.put("message", "交易密码设置成功");
		return result;
	}
	//获取邀请奖励
	@ResponseBody
	@RequestMapping(value="/getuseraes")
	public JSONObject getUserAes(String token,
			HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		
		LoginUser luser=ApiLoginContext.getUser(token);
		if(luser==null){
			result.put("success", false);	
			result.put("code", 101);
			result.put("message", "无效token");
			return result;
		}
		
		result.put("success", true);	
		result.put("code", 0);
		result.put("aesKey", luser.getAesKey());
		result.put("aesIv", luser.getAesIv());
		result.put("message", "");
		return result;
	}
	
	//登出
	//@CrossOrigin(origins = "*", maxAge = 3600)  //跨域post请求
	@ResponseBody	
	@RequestMapping(value={"/dologinout"},method=RequestMethod.GET)
	public JSONObject DoLoginOut(String token,
			HttpServletRequest request,
			HttpServletResponse response){
		
		JSONObject result=new JSONObject();		
		if(token==null||token.isEmpty()){
			result.put("success", false);
			result.put("code", 101);
			result.put("message", "没有token");
			
			return result;
		}
		
		ApiLoginContext.removeToken(token);
		
		result.put("success", true);
		result.put("code", 0);
		result.put("message", "成功");
		
		return result;
	}
	
	//实名认证
	@ResponseBody
	@RequestMapping(value="/realnameauth",method=RequestMethod.POST)
	public JSONObject realNameAuth(String token,User data,
			HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		if(data==null){
			result.put("success", false);
			result.put("code", 2);
			result.put("message", "提交数据格式异常");
			
			return result;
		}	
		
		LoginUser luser=ApiLoginContext.getUser(token);
		if(luser==null){
			result.put("success", false);	
			result.put("code", 101);
			result.put("message", "无效token");
			return result;
		}
		
		//certificationStatus=2已认证
		List<User> list=userService.getList("where certificationStatus=2 and idNum='"+data.getIdNum()+"'");
		if(list.size()>0){
			result.put("success", false);
			result.put("code", 6);
			result.put("message", data.getIdNum()+"此身份证号码已经被认证！");
			
			return result;
		}
		
		User saveUser=userService.getById(luser.getUserid());
		//验证区号
		String areaNum= luser.getAreaNum();
		if (areaNum==null||areaNum.isEmpty()) {
			areaNum="+86";
		}
		if (areaNum.equals("+86")) {
			//实名认证1--身份证认证
			IDCard idc=new IDCard();
			int resNum=idc.idcard_verify(data.getRealName(),data.getIdNum());
			if(resNum!=1000){
				result.put("success", false);
				result.put("code", 4);
				result.put("message", "身份证认证失败："+idc.message);
				
				saveUser.setStatus(1); ////0-未实名认证，1-实名认证中，2-已实名认证
				saveUser.setCertificationStatus(-1);//0-未认证 1-认证中 2-已实名认证 -1-认证失败
				userService.Save(saveUser);
				
				return result;
			}
			saveUser.setStatus(2); ////0-未实名认证，1-实名认证中，2-已实名认证
			saveUser.setCertificationStatus(2);//0-未认证 1-认证中 2-已实名认证 -1-认证失败
		}else {
			saveUser.setStatus(1); ////0-未实名认证，1-实名认证中，2-已实名认证
			saveUser.setCertificationStatus(1);//0-未认证 1-认证中 2-已实名认证 -1-认证失败
		}
		//实名认证2--手机认证
//		String phoneNum=luser.getUsername();
//		int resNum2=idc.phone_verify(data.getRealName(),data.getIdNum(),phoneNum);
//		if(resNum2!=1000){
//			result.put("success", false);
//			result.put("code", 5);
//			result.put("message", "手机认证失败："+idc.message);
//			
//			saveUser.setStatus(1); //0-未实名认证，1-实名认证中，2-已实名认证
//			saveUser.setCertificationStatus(-1);//0-未认证 1-认证中 2-已实名认证 -1-认证失败
//			userService.Save(saveUser);
//			
//			return result;
//		}		
		//保存其他信息       idCardFront 证件正面,idCardBack 正面反面,manPic 手持证件照
		saveUser.setRealName(data.getRealName());
		saveUser.setIdType(data.getIdType());
		saveUser.setIdNum(data.getIdNum());
		saveUser.setIdPhoto1(data.getIdPhoto1());
		saveUser.setIdPhoto2(data.getIdPhoto2());
		saveUser.setPhoto(data.getPhoto());
		
		if(!userService.Save(saveUser)){
			result.put("success", false);
			result.put("code", 5);
			result.put("message", "保存信息失败");
			
			return result;
		}
		
		//更新会话中的用户信息
		luser.setRealName(saveUser.getRealName());
		luser.setNickName(saveUser.getNickName());
		luser.setIdNum(saveUser.getIdNum());
		
		result.put("success", true);
		result.put("code", 0);
		result.put("message", "成功");
		
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
			result.put("code", 2);
			result.put("message", "省份证件1图片大小不能超过4M");
			
			return result;
		}
		MultipartFile mFile2 = multipartRequest.getFile("idPhoto2");
		if(mFile2.getSize()>1024*1024*10){
			result.put("success", false);
			result.put("code", 3);
			result.put("message", "省份证件2图片大小不能超过4M");
			
			return result;
		}
		MultipartFile mFile = multipartRequest.getFile("photo");
		if(mFile.getSize()>1024*1024*10){
			result.put("success", false);
			result.put("code", 4);
			result.put("message", "手持证件图片大小不能超过4M");
			
			return result;
		}
		
		FileUploadUtil.UploadResult res1=FileUploadUtil.updateOneImage(request, "/private/images/", "idPhoto1",0); 
		if(!res1.success){
			result.put("success", false);
			result.put("code", 5);
			result.put("message", res1.message);
			return result;
		}
		String filename1=res1.newFileName;
		
		//保存图片2
		FileUploadUtil.UploadResult res2=FileUploadUtil.updateOneImage(request, "/private/images/", "idPhoto2",0);
		if(!res2.success){
			result.put("success", false);
			result.put("code", 6);
			result.put("message", res2.message);
			return result;
		}
		String filename2=res2.newFileName;
		
		//保存图片3
		FileUploadUtil.UploadResult res=FileUploadUtil.updateOneImage(request, "/private/images/", "photo",0);
		if(!res.success){
			result.put("success", false);
			result.put("code", 7);
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
	
	//更新会员图片信息（不是上传图片）
	@ResponseBody
	@RequestMapping(value="/updateuserimage",method=RequestMethod.POST)
	public JSONObject updateUserImage(User data,String token, HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		if(data==null){
			result.put("success", false);
			result.put("code", 2);
			result.put("message", "提交数据格式异常");
			
			return result;
		}
		
		LoginUser luser=ApiLoginContext.getUser(token);
		if(luser==null){
			result.put("success", false);	
			result.put("code", 101);
			result.put("message", "无效token");
			return result;
		}
		
		User user=userService.getById(luser.getUserid());

		user.setIdPhoto1(data.getIdPhoto1());
		user.setIdPhoto2(data.getIdPhoto2());
		user.setPhoto(data.getPhoto());
		
		if(!userService.Save(user)){
			result.put("success", false);
			result.put("code",3);
			result.put("message", "保存信息失败");
			
			return result;
		}
		
		result.put("success", true);
		result.put("code", 0);
		result.put("message", "成功");
		
		return result;
	}
	
	//修改密码
	@ResponseBody
	@RequestMapping(value="/updatepassword",method=RequestMethod.POST)
	public JSONObject updatePassword(String token,
			String oldPassword,
			String newPassword, 
			HttpServletRequest request,
			HttpServletResponse response){
		
		JSONObject result=new JSONObject();
		
		LoginUser user=ApiLoginContext.getUser(token);
		if(user==null){
			result.put("success", false);	
			result.put("code", 101);
			result.put("message", "无效token");
			return result;
		}
		
		String en_pw_old=Md5Encrypt.md5(oldPassword);
		if(!user.getPassword().equals(en_pw_old)){
			result.put("success", false);
			result.put("code", 2);
			result.put("message", "旧密码验证错误");
			return result;
		}
		
		ModelDAO dao=new ModelDAO();
		String en_pw_new=Md5Encrypt.md5(newPassword);
		//修改密码
		dao.M("t_user").where("id="+user.getUserid()).update("password='"+en_pw_new+"'");
		
		result.put("success", true);
		result.put("code", 0);
		result.put("message", "修改密码成功");
		
		return result;
	}
		
	//获取用户信息
	@ResponseBody
	@RequestMapping(value="/getuserinfo")
	public JSONObject getUserInfo(String token,String assetId,
			HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		
		LoginUser luser=ApiLoginContext.getUser(token);
		if(luser==null){
			result.put("success", false);	
			result.put("code", 101);
			result.put("message", "无效token");
			return result;
		}
		DbModel dao=new DbModel();
		User user=userService.getById(luser.getUserid());
		user.setMessages(null);//防止递归嵌套
		user.setOrganInfos(null);//防止递归嵌套
		user.setReportLosses(null);//防止递归嵌套
		user.setGroups(null);//防止递归嵌套
		JSONObject data=(JSONObject)JSONObject.toJSON(user);
		String rootAddress = data.getString("walletAddress");
		
		/*SqlMap selectOne = dao.table("t_user").where("id=:userId").bind("userId", userId).selectOne("id,avatar,nickName,walletAddress");
		if (selectOne==null) {
			result.put("success", false);	
			result.put("code", 1);
			result.put("message", "获取发送人信息失败。");
			return result;
		}
		String rootAddress=selectOne.getString("walletAddress");*/
		
		SqlList list = dao.sql("call p_getbalancebyaddr('"+rootAddress+"',"+assetId+")").query();
		long balance=0;
		if(list.size()>0){
			balance=list.get(0).getLong("amount");
		}
		//获取用户等级
		String sqlGrade=" SELECT  t1.grade FROM t_equity t1  WHERE t1.lrange<="+balance+" AND t1.hrange >"+balance;
		SqlList query2 = dao.sql(sqlGrade).query();
		int grade=0;
		if(query2.size()>0){
			grade=query2.get(0).getInt("grade");
		}
		result.put("grade", grade);
		
		ModelDAO dao1=new ModelDAO();
		SqlMap sm=dao1.M("t_bankcard_info").where("userId="+luser.getUserid()).selectOne();
		if(sm==null){
			data.put("bankCardNum","");
			data.put("bankName","");
		}else{
			data.put("bankCardNum",sm.getString("bankCardNum"));
			data.put("bankName",sm.getString("bankName"));
		}
		
		String avatar = WwSystem.getFullRoot(request)+"public/file_list/heads/" + user.getAvatar();
		data.put("avatar", avatar);
		
		result.put("success", true);	
		result.put("code", 0);
		result.put("message", "成功");
		result.put("data", data);
		return result;
	}
	
	//获取个人资产
//	@ResponseBody
//	@RequestMapping(value="/getasset")
//	public JSONObject getAsset(String token,HttpServletRequest request,HttpServletResponse response){
//		JSONObject result=new JSONObject();
//		
//		LoginUser luser=ApiLoginContext.getUser(token);
//		if(luser==null){
//			result.put("success", false);	
//			result.put("code", 101);
//			result.put("message", "无效token");
//			return result;
//		}
//		
//		ModelDAO dao=new ModelDAO();
//		
//		StringBuffer sql=new StringBuffer();
//		sql.append("select v1.id,v1.sname,v1.`name`,v1.name_en,v1.circulation as total, ifnull(b.amount,0) as holdings from t_asset v1");
//		sql.append(" left join v_balance b on v1.id=b.assetid and b.userId="+luser.getUserid());
//
//		SqlList list=dao.query(sql.toString());
//		
//		JSONObject data=new JSONObject();
//		data.put("assets", list);	
//				
//		result.put("success", true);	
//		result.put("code", 0);
//		result.put("message", "成功");	
//		result.put("data", data);
//		
//		return result;
//	}
	
	//base64--修改用户信息
	@ResponseBody
	@RequestMapping(value="/updateuser",method=RequestMethod.POST)
	public JSONObject updateUser(String token,String nickName,
			Integer gender,String qq,String weixin,String avatar,
			HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
				
		LoginUser luser=ApiLoginContext.getUser(token);
		if(luser==null){
			result.put("success", false);	
			result.put("code", 101);
			result.put("message", "无效token");
			return result;
		}	
				
		if(nickName==null){
			result.put("success", false);
			result.put("code", 2);
			result.put("message", "没有昵称");
			
			return result;
		}
		
		User saveUser=userService.getById(luser.getUserid());
		saveUser.setAvatar(avatar);
		saveUser.setNickName(nickName);//data.getNickName());
		saveUser.setGender(gender);//data.getGender());
		saveUser.setQq(qq);
		saveUser.setWeixin(weixin);
		
		if(!userService.Save(saveUser)){
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
	//带文件上传的修改用户信息
	@ResponseBody
	@RequestMapping(value="/updateuser2",method=RequestMethod.POST)
	public JSONObject updateUser2(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		
		String token=request.getParameter("token");
		
		LoginUser luser=ApiLoginContext.getUser(token);
		if(luser==null){
			result.put("success", false);	
			result.put("code", 101);
			result.put("message", "已过期请重新登录");
			return result;
		}		
		
		String nickName=request.getParameter("nickName");
		Integer gender = WwSystem.ToInt(request.getParameter("gender"));
		String qq=request.getParameter("qq");
		String weixin=request.getParameter("weixin");
		if(nickName==null){
			result.put("success", false);
			result.put("code", 2);
			result.put("message", "没有昵称");
			
			return result;
		}
		
		UploadResult ures= FileUploadUtil.updateOneImage(request, "/public/file_list/heads/", "file");
		if(!ures.success){
			result.put("success", false);
			result.put("code", 4);
			result.put("message", "文件上传失败！");
			WwLog.getLogger(this).error("文件上传失败:"+ures.message);
			return result;
		}
		User saveUser=userService.getById(luser.getUserid());
		String fileName=ures.newFileName;
		saveUser.setAvatar(fileName);
		saveUser.setNickName(nickName);//data.getNickName());
		saveUser.setGender(gender);//data.getGender());
		saveUser.setQq(qq);
		saveUser.setWeixin(weixin);
		
		if(!userService.Save(saveUser)){
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
	//不带文件上传的修改用户信息
	@ResponseBody
	@RequestMapping(value="/updateuser3",method=RequestMethod.POST)
	public JSONObject updateUser3(String token,String nickName,
			Integer gender,String qq,String weixin,
			HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
				
		LoginUser luser=ApiLoginContext.getUser(token);
		if(luser==null){
			result.put("success", false);	
			result.put("code", 101);
			result.put("message", "无效token");
			return result;
		}	
				
		if(nickName==null){
			result.put("success", false);
			result.put("code", 2);
			result.put("message", "没有昵称");
			
			return result;
		}
		
		User saveUser=userService.getById(luser.getUserid());
		saveUser.setNickName(nickName);//data.getNickName());
		saveUser.setGender(gender);//data.getGender());
		saveUser.setQq(qq);
		saveUser.setWeixin(weixin);
		
		if(!userService.Save(saveUser)){
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
	//更换手机号码
	@ResponseBody
	@RequestMapping(value="/updatephonenumber",method=RequestMethod.POST)
	public JSONObject updatePhoneNumber(String token,String phoneNumber,String phoneValidCode,String password,
			HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		if(phoneNumber==null||phoneValidCode==null||password==null){
			result.put("success", false);
			result.put("code", 2);
			result.put("message", "提交参数不对");
			
			return result;
		}
		
		LoginUser luser=ApiLoginContext.getUser(token);
		if(luser==null){
			result.put("success", false);	
			result.put("code", 101);
			result.put("message", "无效token");
			return result;
		}
		
		//手机验证码验证
		PhoneValidCodeInfo pvc=ApiLoginContext.getPhoneValidCode(phoneNumber);		
		if(pvc==null||phoneValidCode==null){
			result.put("success", false);
			result.put("code", 3);
			result.put("message", "没有验证码");
			
			return result;
		}		
		if(!pvc.ValidCode.equals(phoneValidCode)){
			result.put("success", false);
			result.put("code", 4);
			result.put("message", "验证码错误");
			
			return result;
		}
		
		ApiLoginContext.removePhoneValidCode(phoneNumber);
		
		//密码验证
		String md5PW=Md5Encrypt.md5(password);
		if(!luser.getPassword().equals(md5PW)){
			result.put("success", false);
			result.put("code", 5);
			result.put("message", "密码错误");
			
			return result;
		}
		
		
		User saveUser=userService.getById(luser.getUserid());	
		saveUser.setPhoneNum(phoneNumber);
		
		if(!userService.Save(saveUser)){
			result.put("success", false);
			result.put("code", 6);
			result.put("message", "保存记录失败");
			
			return result;
		}
		
		result.put("success", true);
		result.put("code", 0);
		result.put("message", "成功");
		
		return result;
	}
	
	//获取登录日志
	@ResponseBody
	@RequestMapping(value="/getloginlog")
	public JSONObject getLoginLog(String token,HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		
		LoginUser luser=ApiLoginContext.getUser(token);
		if(luser==null){
			result.put("success", false);	
			result.put("code", 101);
			result.put("message", "无效token");
			return result;
		}
		
		List<Loginlog> us= loginlogService.getMapper().getList2("userId="+luser.getUserid(), "loginTime desc", 0,10);
		
		result.put("success", true);	
		result.put("code", 0);
		result.put("message", "成功");
		result.put("data", us);
		return result;
	}
	
	//获取分红地址
	@ResponseBody
	@RequestMapping(value="/getbonusaddress")
	public JSONObject getBonusaddress(String token,HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		
		LoginUser luser=ApiLoginContext.getUser(token);
		if(luser==null){
			result.put("success", false);	
			result.put("code", 101);
			result.put("message", "无效token");
			return result;
		}
		
		List<Bonusaddress> list= bonusaddressService.getMapper().getByUserId(luser.getUserid());
		
		result.put("success", true);	
		result.put("code", 0);
		result.put("message", "成功");
		
		JSONObject data=new JSONObject();
		data.put("bonus", list);
		
		result.put("data", data);
		
		return result;
	}
	
	//更新分红地址（一个用户只能有一个分红地址）
	@ResponseBody
	@RequestMapping(value="/updatebonusaddress",method=RequestMethod.POST)
	public JSONObject updateBonusaddress(String token,Integer id,String address,
			String phoneNum,String phoneValidCode,String source,
			HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		
		LoginUser luser=ApiLoginContext.getUser(token);
		if(luser==null){
			result.put("success", false);	
			result.put("code", 101);
			result.put("message", "无效token");
			return result;
		}		
		
		//验证手机验证码
		PhoneValidCodeInfo pvc= ApiLoginContext.getPhoneValidCode(phoneNum);
		if(pvc==null){
			result.put("success", false);	
			result.put("code", 3);
			result.put("message", "没有验证码");
			return result;
		}
		
		if(!pvc.ValidCode.equals(phoneValidCode)){
			result.put("success", false);	
			result.put("code", 4);
			result.put("message", "验证码错误");
			
			return result;
		}
		
		ApiLoginContext.removePhoneValidCode(phoneNum);
		
		if(source==null)
			source="";
		
		if(id==null){
			id=0;
		}
		
		Bonusaddress ba= bonusaddressService.getMapper().getById(id);
		if(id==null||id<=0){
			ba=new Bonusaddress();		
			ba.setType("BTC");
		}
		ba.setUserId(luser.getUserid());	
		ba.setAddress(address);
		ba.setSource(source);
		boolean b=bonusaddressService.Save(ba);
		if(!b){
			result.put("success", false);	
			result.put("code", 2);
			result.put("message", "保存失败");
			return result;
		}
		
		
		result.put("success", true);	
		result.put("code", 0);
		result.put("message", "成功");
		
		return result;
	}
	
	//银行卡认证
	@ResponseBody
	@RequestMapping(value="/bankcardverif",method=RequestMethod.POST)
	public JSONObject bankCardVerif(String token,String bankcardNum,
			HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		if(token==null||bankcardNum==null){
			result.put("success", false);
			result.put("code", 2);
			result.put("message", "提交数据格式异常");
			
			return result;
		}	
		
		LoginUser luser=ApiLoginContext.getUser(token);
		if(luser==null){
			result.put("success", false);	
			result.put("code", 101);
			result.put("message", "无效token");
			return result;
		}
		
		if(luser.getIdNum()==null||luser.getIdNum().isEmpty()){
			result.put("success", false);	
			result.put("code", 6);
			result.put("message", "请先进行实名验证，在进行本次操作。");
			return result;
		}
		
		//银行卡认证证
		IDCard idc=new IDCard();
		int resNum=idc.bankcard_verify(luser.getRealName(), bankcardNum, luser.getIdNum());
		if(resNum!=1000){
			result.put("success", false);
			result.put("code", 3);
			result.put("message", idc.message);
			
			return result;
		}
		
		//获取银行信息
		String bankName="";
		JSONObject obj=idc.getBankcardInfo(bankcardNum);
		if(obj!=null&&obj.getBoolean("success")){
			bankName=obj.getJSONObject("data").getString("bankname");
		}
		
		//保存银行卡验证状态
		User user=userService.getById(luser.getUserid());
		user.setBankCardVerifStatus(1);		
		if(!userService.Save(user)){
			result.put("success", false);
			result.put("code", 4);
			result.put("message", "保存银行卡验证状态失败");
			
			return result;
		}
		//保存银行验证信息
		List<BankcardVerif> bv_list=bankverifService.getMapper().getByUserId(luser.getUserid());
		BankcardVerif bv=null;
		if(bv_list.size()>0){
			bv=bv_list.get(0);
		}else{
			bv=new BankcardVerif();
			bv.setUserId(luser.getUserid());
		}
		bv.setBankCardNum(bankcardNum);
		bv.setName(luser.getRealName());
		bv.setVerifTime(WwSystem.now());
		bv.setIdNumber(luser.getIdNum());
		bv.setBankName(bankName);
		if(!bankverifService.Save(bv)){
			result.put("success", false);
			result.put("code", 5);
			result.put("message", "保存银行验证信息失败");
			
			return result;
		}
		
		result.put("success", true);
		result.put("code", 0);
		result.put("message", "成功");
		
		return result;
	}
	
	
	//获取银行卡认证信息
	@ResponseBody
	@RequestMapping(value="/getbankcardverif")
	public JSONObject getBankCardVerif(String token,HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		
		LoginUser luser=ApiLoginContext.getUser(token);
		if(luser==null){
			result.put("success", false);	
			result.put("code", 101);
			result.put("message", "无效token");
			return result;
		}
				
		List<BankcardVerif> list=bankcardService.getMapper().getByUserId(luser.getUserid());
		if(list==null||list.size()<=0){
			result.put("success", false);	
			result.put("code", 2);
			result.put("message", "没有银行卡认证");
			return result;
		}
		
		result.put("success", true);	
		result.put("code", 0);
		result.put("message", "成功");
		result.put("data", list.get(0));
		return result;
	}
	
	//保存风险评估信息
	@ResponseBody
	@RequestMapping(value="/updateriskinfo",method=RequestMethod.POST)
	public JSONObject updateRiskInfo(String token,Integer score, Integer type,
			HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		if(token==null||score==null||type==null){
			result.put("success", false);
			result.put("code", 2);
			result.put("message", "提交数据格式异常");
			
			return result;
		}	
		
		LoginUser luser=ApiLoginContext.getUser(token);
		if(luser==null){
			result.put("success", false);	
			result.put("code", 101);
			result.put("message", "无效token");
			return result;
		}
		
		List<UserRisk> list=userRiskService.getMapper().getByUserId(luser.getUserid());
		UserRisk ur=null;
		if(list.size()>0){
			ur=list.get(0);
			ur.setUserId(luser.getUserid());
		}else{
			ur=new UserRisk();
			ur.setUserId(luser.getUserid());
		}
		ur.setStatus(1);
		ur.setScore(score);
		ur.setType(type);
		ur.setTime(WwSystem.now());
		
		if(!userRiskService.Save(ur)){
			result.put("success", false);
			result.put("code", 3);
			result.put("message", "保存信息失败");
			
			return result;
		}
		
		result.put("success", true);
		result.put("code", 0);
		result.put("message", "注册成功");
		
		return result;
	}
	
	//获取邀请码
	@ResponseBody
	@RequestMapping(value="/getinvitecode")
	public JSONObject getInviteCode(String token,HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		
		LoginUser luser=ApiLoginContext.getUser(token);
		if(luser==null){
			result.put("success", false);	
			result.put("code", 101);
			result.put("message", "无效token");
			return result;
		}
				
		User user=userService.getById(luser.getUserid());
		
		String myInviteCode=user.getInviteCode();
		if(myInviteCode==null||myInviteCode.isEmpty()){
			Integer code_id=user.getId()+10000000;
			myInviteCode=Integer.toHexString(code_id);
			
			user.setInviteCode(myInviteCode);
			if(!userService.Save(user)){
				result.put("success", false);	
				result.put("code", 2);
				result.put("message", "保存信息失败");
				return result;
			}
		}
		
		JSONObject data=new JSONObject();
		data.put("inviteCode", myInviteCode);
		
		result.put("success", true);	
		result.put("code", 0);
		result.put("message", "成功");
		result.put("data", data);
		return result;
	}
	
	
	//获取「我的邀请」列表(old)
	@ResponseBody
	@RequestMapping(value="/getinvitelist")
	public JSONObject getInviteList(String token,Integer pageIndex,Integer pageSize,
			HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		
		LoginUser luser=ApiLoginContext.getUser(token);
		if(luser==null){
			result.put("success", false);	
			result.put("code", 101);
			result.put("message", "无效token");
			return result;
		}
				
		User user=userService.getById(luser.getUserid());
		String inviteCode=user.getInviteCode();
		if(inviteCode==null||inviteCode.isEmpty()){
			result.put("success", false);	
			result.put("code", 2);
			result.put("message", "没有邀请码");
			return result;
		}
		
		ModelDAO dao=new ModelDAO();
		JSONObject data=new JSONObject();
		
		String sql="select phoneNum,status,regTime from t_user where parentInviteCode='"+inviteCode+"'";
		long[] tt=dao.countPages(sql, pageSize);
		data.put("pageCount", tt[1]);
		data.put("pageIndex", pageIndex+1);		

		SqlList list=dao.queryByPaging(sql,pageIndex,pageSize);		
		data.put("list", list);
		
		result.put("success", true);
		result.put("code", 0);
		result.put("data", data);
		result.put("message", "成功");
		return result;
	}
	
	//获取邀请奖励
	@ResponseBody
	@RequestMapping(value="/getinviterewards")
	public JSONObject getInviteRewards(String token,
			HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		
		LoginUser luser=ApiLoginContext.getUser(token);
		if(luser==null){
			result.put("success", false);	
			result.put("code", 101);
			result.put("message", "无效token");
			return result;
		}
				
		User user=userService.getById(luser.getUserid());
		String inviteCode=user.getInviteCode();
		if(inviteCode==null||inviteCode.isEmpty()){
			result.put("success", false);	
			result.put("code", 2);
			result.put("message", "没有邀请码");
			return result;
		}
		
		ModelDAO dao=new ModelDAO();
		JSONObject data=new JSONObject();
		JSONObject level1=new JSONObject();
		JSONObject level2=new JSONObject();
		data.put("level1", level1);
		data.put("level2", level2);
		
		String sql="select count(*) as people from t_user where parentInviteCode='"+inviteCode+"'";

		SqlList list=dao.query(sql);	
		int people=list.get(0).getInt("people");
		long reward=people*100;
		level1.put("people", people);
		level1.put("reward", reward);
		
		String sql2="select count(*) as people from t_user where parentInviteCode in ";
		sql2+="(select inviteCode as people from t_user where parentInviteCode='"+inviteCode+"')";

		SqlList list2=dao.query(sql2);	
		int people2=list2.get(0).getInt("people");
		long reward2=people2*50;
		level2.put("people", people2);
		level2.put("reward", reward2);
		
		
		
		result.put("success", true);
		result.put("code", 0);
		result.put("data", data);
		result.put("message", "成功");
		return result;
	}
	
	//活体认证
	@ResponseBody
	@RequestMapping(value="/faceverify")
	public JSONObject faceVerify(String token,String idcardImage,String manImage,
			HttpServletRequest request,HttpServletResponse response){		
		JSONObject result=new JSONObject();
		
		LoginUser luser=ApiLoginContext.getUser(token);
		if(luser==null){
			result.put("success", false);	
			result.put("code", 101);
			result.put("message", "会话过期，请重新登录");
			return result;
		}
		
		// 生成订单标识, 唯一
		String trans_id=UUID.randomUUID().toString();
		//创建时间 年月日时分秒 例: 20170213195356
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String trade_date = formatter.format(new Date());
        
        FaceVerify fv=new FaceVerify();
        
        //保存记录
      	if(!fv.saveImages(luser.getUserid(), trans_id, trade_date, manImage, idcardImage)){
      		result.put("success", false);	
			result.put("code", 10);
			result.put("message", "认证异常，请与客服联系。");
			
			System.out.println("保存图片资料异常:"+fv.saveError);
			WwLog.getLogger(this).error("保存图片资料异常:"+fv.saveError);
			return result;
      	}
				
		//User user=userService.getById(luser.getUserid());	
		ModelDAO dao=new ModelDAO();
		User saveUser=userService.getById(luser.getUserid());
		
		if(idcardImage.indexOf(",")>0){
			//"data:image/jpeg;base64,"; //去掉23个前缀字符
			idcardImage=idcardImage.split(",")[1];
		}
		if(manImage.indexOf(",")>0){
			//"data:image/jpeg;base64,"; //去掉23个前缀字符
			manImage=manImage.split(",")[1];
		}
		
		if(!idcardImage.startsWith("/")){
			idcardImage="/"+idcardImage;
		}
		if(!manImage.startsWith("/")){
			manImage="/"+manImage;
		}				
		
		//身份证照片识别
		JSONObject fvRes1=fv.ocrIdcard(trans_id, trade_date, idcardImage);
		if(fvRes1.getBoolean("success")){
			//身份证验证认证成功，继续往下
			
			//保存记录
			fv.saveOcrResult(luser.getUserid(),fvRes1.toJSONString(), 1);
		}else{
			String message=fvRes1.getString("message");
			result.put("success", false);
			result.put("code", 11);
			result.put("message","身份证识别失败！");			
			
			//修改用户状态
			saveUser.setStatus(-3); //2-已身份证验证   3-已活体让整   -3活体失败
			saveUser.setCertificationStatus(-3);
			saveUser.setCertificationTime(saveUser.getNow());
			userService.Save(saveUser);
			
			//保存记录
			fv.saveOcrResult(luser.getUserid(),fvRes1.toJSONString(), -1);
			
			WwLog.getLogger(this).error(fvRes1.toString());
			return result;			
		}
		
		//活体认证
		JSONObject fvRes2=fv.faceToIdcard(trans_id, trade_date, manImage, idcardImage);
		if(fvRes2.getBoolean("success")){
			//认证成功，继续往下	
			
			//保存记录
			fv.saveFaceResult(luser.getUserid(), fvRes2.toJSONString(), 2);
			
			//修改用户状态
			saveUser.setStatus(3); //2-已身份证验证   3-已活体让整
			saveUser.setCertificationStatus(3);
			saveUser.setCertificationTime(saveUser.getNow());
			userService.Save(saveUser);
		}else{
			String message=fvRes2.getString("message");
			result.put("success", false);
			result.put("code", 11);
			result.put("message","活体人脸认证失败！");
			
			//修改用户状态
			saveUser.setStatus(-3); //2-已身份证验证   3-已活体让整   -3活体失败
			saveUser.setCertificationStatus(-3);
			saveUser.setCertificationTime(saveUser.getNow());
			userService.Save(saveUser);
			
			//保存记录
			fv.saveFaceResult(luser.getUserid(), fvRes2.toJSONString(), -2);
			
			WwLog.getLogger(this).error(fvRes2.toString());
			return result;			
		}

		result.put("success", true);
		result.put("code", 0);
		result.put("message","活体认证通过");
		return result;
	}
	
	/**************非action*******************************/
	
	
	
	
	
}
