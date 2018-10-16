package ww.authen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ww.cache.WwOpenSessionCache;
import ww.redis.WwRedisDAO2;

public class OpenApiContext {
	public static class PhoneValidCodeInfo implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = -5163834213694835843L;
		public String ValidCode="";
		public Date CreateDate;
		public int Times=0; //次数
		
		public PhoneValidCodeInfo(){
			
		}
	}
	public static class ImageValidCodeInfo implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = -4334904287042907673L;
		public String ValidCode="";
		public Date CreateDate;
		
		public ImageValidCodeInfo(){
			
		}
	}
	
	
	/**
	 * 
	 * @param user
	 * @param token
	 */
	public static void setToken(OpenLoginUser user,String token){
		String tokenMap_key="tokenMap_"+user.getUsername();
		WwOpenSessionCache.getInstance().setString(tokenMap_key, token);
		
		String loginUserMap_key="loginUserMap_"+token;
		WwOpenSessionCache.getInstance().setObject(loginUserMap_key, user);		
		
	}
	
	/**
	 * 
	 * @param username
	 * @return
	 */
	public static String getToken(String username){	
		String tokenMap_key="tokenMap_"+username;
		return WwOpenSessionCache.getInstance().getString(tokenMap_key);
				
	}
	
	
	public static void setUser(String token,OpenLoginUser user){		
		String loginUserMap_key="loginUserMap_"+token;
		WwOpenSessionCache.getInstance().setObject(loginUserMap_key, user);
		String tokenMap_key="tokenMap_"+user.getUsername();
		WwOpenSessionCache.getInstance().setObject(tokenMap_key, token);
	}
	
	/**
	 * 
	 * @param token
	 * @return
	 */
	public static OpenLoginUser getUser(String token){	
		String loginUserMap_key="loginUserMap_"+token;
		OpenLoginUser lu=(OpenLoginUser)WwOpenSessionCache.getInstance().getObject(loginUserMap_key);
		return lu;
	}
	
	
	
	/**
	 * 
	 * @param userName
	 */
	public static void removeUser(String userName){	
		String tokenMap_key="tokenMap_"+userName;
		String token_old=WwOpenSessionCache.getInstance().getString(tokenMap_key);
		if(!token_old.equals("")&&!token_old.isEmpty()){
			String loginUserMap_key="loginUserMap_"+token_old;
			WwOpenSessionCache.getInstance().remove(loginUserMap_key);
		}
		WwOpenSessionCache.getInstance().remove(tokenMap_key);
	}
	
	/**
	 * 
	 * @param token
	 */
	public static void removeToken(String token){
		String loginUserMap_key="loginUserMap_"+token;
		OpenLoginUser lu=(OpenLoginUser)WwOpenSessionCache.getInstance().getObject(loginUserMap_key);
		if(lu!=null){
			String tokenMap_key="tokenMap_"+lu.getUsername();
			WwOpenSessionCache.getInstance().remove(tokenMap_key);
		}
		WwOpenSessionCache.getInstance().remove(loginUserMap_key);	
	}
	
	/**
	 * 
	 * @param phoneNum
	 * @param validCode
	 */
	public static void setPhoneValidCode(String phoneNum,String validCode){
		String PhoneValidCodeMap_key="PhoneValidCodeMap_"+phoneNum;
		WwOpenSessionCache.getInstance().remove(PhoneValidCodeMap_key);
		PhoneValidCodeInfo pvci=new PhoneValidCodeInfo();
		pvci.ValidCode=validCode;
		pvci.CreateDate=new Date();
		WwOpenSessionCache.getInstance().setObject(PhoneValidCodeMap_key, pvci);
	}
	
	/**
	 * 
	 * @param phoneNum
	 * @param validCode
	 * @param times 调用次数
	 */
	public static void setPhoneValidCode(String phoneNum,String validCode,int times){
		String PhoneValidCodeMap_key="PhoneValidCodeMap_"+phoneNum;
		WwOpenSessionCache.getInstance().remove(PhoneValidCodeMap_key);
		PhoneValidCodeInfo pvci=new PhoneValidCodeInfo();
		pvci.ValidCode=validCode;
		pvci.CreateDate=new Date();
		pvci.Times=times;
		WwOpenSessionCache.getInstance().setObject(PhoneValidCodeMap_key, pvci);
	}
	
	/**
	 * 
	 * @param phoneNum
	 */
	public static PhoneValidCodeInfo getPhoneValidCode(String phoneNum){		
		String PhoneValidCodeMap_key="PhoneValidCodeMap_"+phoneNum;
		PhoneValidCodeInfo pvci=(PhoneValidCodeInfo)WwOpenSessionCache.getInstance().getObject(PhoneValidCodeMap_key);
		return pvci;
	}
	
	/**
	 * 
	 * @param phoneNum
	 */
	public static void removePhoneValidCode(String phoneNum){
		String PhoneValidCodeMap_key="PhoneValidCodeMap_"+phoneNum;
		WwOpenSessionCache.getInstance().remove(PhoneValidCodeMap_key);
	}
	
		
	/**
	 * 
	 * @param token
	 * @param validCode
	 */
	public static void setImageValidCode(String token,String validCode){
		String ImageValidCodeMap_key="ImageValidCodeMap_"+token;
		WwOpenSessionCache.getInstance().remove(ImageValidCodeMap_key);
		ImageValidCodeInfo pvci=new ImageValidCodeInfo();
		pvci.ValidCode=validCode;
		pvci.CreateDate=new Date();
		WwOpenSessionCache.getInstance().setObject(ImageValidCodeMap_key, pvci);
	}
	/**
	 * 
	 * @param token
	 * @return
	 */
	public static ImageValidCodeInfo getImageValidCode(String token){
		String ImageValidCodeMap_key="ImageValidCodeMap_"+token;
		ImageValidCodeInfo pvci=(ImageValidCodeInfo)WwOpenSessionCache.getInstance().getObject(ImageValidCodeMap_key);
		return pvci;
	}
	
	/**
	 * 
	 * @param token
	 */
	public static void removeImageValidCode(String token){
		String ImageValidCodeMap_key="ImageValidCodeMap_"+token;
		WwOpenSessionCache.getInstance().remove(ImageValidCodeMap_key);
	}	
	

}
