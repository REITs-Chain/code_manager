package ww.controller.admin;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.ibatis.annotations.ResultType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSONObject;

import ww.common.DbModel;
import ww.common.ListSearchState;
import ww.common.ModelDAO;
import ww.common.SqlList;
import ww.common.SqlMap;
import ww.common.WwLog;
import ww.common.WwSystem;

import ww.security.common.MD5;
import model.Admin;
import model.Group;
import model.User;

import ww.service.admin.GroupService;
import ww.service.admin.UserService;

@Controller
@RequestMapping(value="/admin/Group")
public class GroupController extends ww.controller.BaseController {
	
	@Autowired
	private GroupService service;
	
	//查看星级
	/*@ResponseBody
	@RequestMapping(value="/viewGrade")
	public JSONObject viewGrade(Integer id,Integer userId,
			HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		if(id==null){
			result.put("success",false);
			result.put("msg","id无效！");			
			return result;
		}
		DbModel dao=new DbModel();
		String sql="";
		sql+="select t2.id from t_group t1 ";
		sql+=" left join t_asset_community t2 on t1.communityId=t2.id";
		sql+=" where t1.id="+id;
		SqlList query = dao.sql(sql).query();
		int assetId = query.get(0).getInt("id");
		
		SqlMap selectOne = dao.table("t_user")
							  .where("id=:userId")
							  .bind("userId", userId)
							  .selectOne("id,avatar,nickName,walletAddress");
		if (selectOne==null) {
			result.put("success", false);	
			result.put("message", "获取发送人信息失败。");
			return result;
		}
		String rootAddress=selectOne.getString("walletAddress");
		
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
		result.put("success",true);
		result.put("msg","星级成功！");	
		return result;
	}*/
	
	@ResponseBody
	@RequestMapping(value="/viewGrade")
	public ModelAndView viewGrade(Integer id,Integer userId,
			HttpServletRequest request,HttpServletResponse response){
		ModelAndView result=new ModelAndView(getListViewName());
		if(id==null){
			result.addObject("success",false);
			result.addObject("msg","id无效！");			
			return result;
		}
		DbModel dao=new DbModel();
		String sql="";
		sql+="select t2.id from t_group t1 ";
		sql+=" left join t_asset_community t2 on t1.communityId=t2.id";
		sql+=" where t1.id="+id;
		SqlList query = dao.sql(sql).query();
		int assetId = query.get(0).getInt("id");
		
		SqlMap selectOne = dao.table("t_user")
							  .where("id=:userId")
							  .bind("userId", userId)
							  .selectOne("id,avatar,nickName,walletAddress");
		if (selectOne==null) {
			result.addObject("success", false);	
			result.addObject("message", "获取发送人信息失败。");
			return result;
		}
		String rootAddress=selectOne.getString("walletAddress");
		
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
		result.addObject("grade", grade); 
		result.addObject("success",true);
		result.addObject("msg","星级成功！");	
		return result;
	}
	//查看群组用户
	@ResponseBody
	@RequestMapping(value="/viewUser")
	public ModelAndView viewUser(Integer id, 
			HttpServletRequest request,HttpServletResponse response){
		ModelAndView mv=new ModelAndView();
		int page=WwSystem.ToInt(request.getParameter("page"));//第几页
        int beginRow=WwSystem.ToInt(request.getParameter("beginRow"));//每页开始行
        int pageRows=WwSystem.ToInt(request.getParameter("pageRows"));//每页显示行数
        
        if (id!=null) {
			request.getSession().setAttribute("groupId", id);
		}else{
			id=(Integer) request.getSession().getAttribute("groupId");
		}
        if(page<=0)
            page=1;
        if(beginRow<=0)
            beginRow=0;
        if(pageRows<=0)
            pageRows=20;
        DbModel dao=new DbModel();
     
        SqlMap groupInfo = dao.table("t_group").where("id=:id").bind("id", id).selectOne("name,communityId");
        String groupName = groupInfo.getString("name");
        String sql1="SELECT * FROM t_user_group g LEFT JOIN t_user u  ON g.`userId`=u.`id`";
        sql1+="  WHERE g.`groupId`="+id;
        //获取总页数和总条数
        long[] countPages = dao.table("t_user")
        					   .countPages(sql1, pageRows);
        long totalRows = countPages[0];
        long pages=countPages[1];
        SqlList userList = dao.sql(sql1).query();
    	
        mv.addObject("list",userList); //列表数据
    	mv.addObject("page",page); //第几页
    	mv.addObject("groupId",id); //群组ID
    	mv.addObject("groupName",groupName); //群组名称
    	mv.addObject("pageRows",pageRows); //每页显示行数
    	mv.addObject("allRows",totalRows); //查询总行数	
    	mv.addObject("query",pages); //查询总行数		
    	mv.setViewName(addGroupMember());
		return mv;
	}
	
	@RequestMapping(value="/list")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response){		
		ModelAndView mv=new ModelAndView();
		
		ListSearchState searchState=new ListSearchState(request,"Group");
		searchState.saveStates();//保存状态
		
		int page=WwSystem.ToInt(searchState.getParameterWithState("page"));//第几页
        int pageRows=WwSystem.ToInt(searchState.getParameterWithState("pageRows"));//每页显示行数
        String query=WwSystem.ToString(searchState.getParameterWithState("query"));//搜索条件
        
        query=DbModel.sqlParamString(query);//非常重要，防sql注入
        
        if(page<=0)
            page=1;
        if(pageRows<=0)
            pageRows=20;
            
        int beginRow=(page-1)*pageRows;
        
        HttpSession session = request.getSession();
		Admin u = (Admin) session.getAttribute("admin");
		if (u==null) {
			mv.addObject("success", false);
			mv.addObject("code",1);
			mv.addObject("message","请登录！");
			return mv;
		} 
		String where="";
		if (!u.getFlevel().equals("A")) {
			where+=" where communityId in (select c.id from t_admin_asset t inner join t_asset_community c on t.assetId=c.assetId where t.adminId="+u.getFid()+")";
		}
        /*
        //请根据实际搜索条件更改代码
        if(!query.isEmpty()){
        	where=" where fname='"+query+"'";
        }*/

        String parkSql=where + " limit "+beginRow+","+pageRows;
    	List<Group> list=service.getList(parkSql);
    	int allRows=service.getCount(where);

    	DbModel dao=new DbModel();
    	for (int i = 0; i < list.size(); i++) {
			SqlMap selectOne = dao.table("t_user").where("phoneNum=:phoneNum").bind("phoneNum", list.get(i).getCreatePerson()).selectOne("realName");
			if (selectOne!=null) {
				list.get(i).setCreatePerson(selectOne.getString("realName"));
			}
		}
    	/*查询资产名称 资产简称
    	SqlMap sqlMap = dao.table("t_asset").where("id=:id").bind("id", list.get(0).getCommunityId()).selectOne("sname,name");
    	*/
    	mv.addObject("list",list); //列表数据
    	mv.addObject("page",page); //第几页
    	mv.addObject("pageRows",pageRows); //每页显示行数
    	mv.addObject("allRows",allRows); //查询总行数	
    	mv.addObject("query",query); //查询总行数		
		
		mv.setViewName(getListViewName());
		return mv;
	}
	
	@RequestMapping(value="/edit")
	public ModelAndView edit(java.lang.Integer id,Integer state,HttpServletRequest request,HttpServletResponse response){	
		ModelAndView mv=new ModelAndView(getEditViewName());
		Group data=null;
		if(id!=null)			
			data=service.getById(id);
		if(data==null)
			data=new Group();
		
		if(state==null)
			state=0;
		
		mv.addObject("state", state);	
		mv.addObject("data",data);		
		return mv;
	}
	
	@RequestMapping(value="/save")
	public ModelAndView save(@Valid @ModelAttribute("data") Group data,BindingResult result,
			HttpServletRequest request,HttpServletResponse response)throws Exception{		
		ModelAndView mv=null;		
		//如果有验证错误 返回到form页面
        if(result.hasErrors()){
        	mv= new ModelAndView(getEditViewName());
        	mv.addObject("data",data);
        	mv.addObject("error","验证错误！");
        	return mv;
        }
        
        Group saveData=service.getById(data.getId());
		if(saveData==null){
			saveData=new Group();
		}	
		//保存request提交的字段，其他字段为数据库已经存在的值
	    saveData.mergeProperty(data, request);	
				
		if(service.Save(saveData)){
			mv=new ModelAndView("redirect:/admin/Group/list");
		}else{			
			mv=new ModelAndView(getEditViewName());
			mv.addObject("error","保存失败！");
			mv.addObject("data",saveData);
		}
		
		return mv;
	}
	
	@RequestMapping(value="/view")
	public ModelAndView view(java.lang.Integer id,HttpServletRequest request,HttpServletResponse response){	
		ModelAndView mv=new ModelAndView(getViewViewName());
		Group data=null;
		if(id!=null)
			data=service.getById(id);
		if(data==null)
			data=new Group();
		
		mv.addObject("data",data);		
		return mv;
	}
	
	@ResponseBody
	@RequestMapping(value="/delete")
	public JSONObject delete(java.lang.Integer id,HttpServletRequest request,HttpServletResponse response){		
		JSONObject result=new JSONObject();
		
		if(id==null){
			result.put("code",1);
			result.put("message","id无效！");
			WwLog.getLogger(this).error("id==null无效");
			return result;
		}
		
		if(service.Delete(id)){
			result.put("code",0);
			result.put("message","删除成功。");
		}else{
			result.put("code",2);
			result.put("message","删除失败！");
			WwLog.getLogger(this).error("id=="+id+"删除失败!");
		}
		
		return result;
	}
	
	private String getEditViewName(){
		return "admin/GroupEdit";
	}
	
	private String getViewViewName(){
		return "admin/GroupView";
	}
	
	private String getListViewName(){
		return "admin/GroupList";
	}
	
	private String addGroupMember(){
		return "admin/GroupJoinUser";
	}
	
}
