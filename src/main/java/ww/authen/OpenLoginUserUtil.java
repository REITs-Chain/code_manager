package ww.authen;

import java.util.Date;

import ww.common.ModelDAO;
import ww.common.SqlMap;

public class OpenLoginUserUtil {
	public static OpenLoginUser getByUsername(String username){
		ModelDAO dao=new ModelDAO();
		
		SqlMap u=dao.M("t_open_user").where("phoneNum='"+ModelDAO.sqlParamString(username)+"'").selectOne();
		if(u==null){
			return null;
		}
		OpenLoginUser user=readUser(u);
		
		return user;
	}
	
	public static OpenLoginUser getById(long userId){
		ModelDAO dao=new ModelDAO();
		
		SqlMap u=dao.M("t_open_user").where("id="+userId).selectOne();
		if(u==null){
			return null;
		}
		OpenLoginUser user=readUser(u);
		
		return user;
	}
	
	private static OpenLoginUser readUser(SqlMap smUser){		
		SqlMap u=smUser;
		if(u==null){
			return null;
		}
		OpenLoginUser user=new OpenLoginUser();
		user.setId(u.getInt("id"));
		user.setType(smUser.getInt("type"));
		user.setUsername(u.getString("phoneNum"));
		user.setPassword(u.getString("password"));
		user.setRealName(u.getString("realName"));
		return user;
	}
	
//	public static void updateLoginTimes(int newTimes,int userid){
//		ModelDAO dao=new ModelDAO();		
//		dao.M("t_open_user").where("id="+userid).update("loginTimes="+newTimes);
//	}
//	
//	public static void SaveLoginLog(int userId,String ip,String location,String status){
//		ModelDAO dao=new ModelDAO();		
//		SqlMap data=new SqlMap();
//		data.put("userId", userId);
//		data.put("loginTime", ww.common.WwSystem.now());
//		data.put("ip", ip);
//		data.put("location", location);
//		data.put("status", status);
//		long res=dao.M("t_loginlog").insert(data);
//		if(res!=1){
//			System.out.println(dao.Message);
//		}
//		
//	}
	
	
}
