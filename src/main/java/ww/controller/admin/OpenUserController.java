package ww.controller.admin;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSONObject;

import ww.common.DbModel;
import ww.common.ListSearchState;
import ww.common.ModelDAO;
import ww.common.SqlMap;
import ww.common.WwLog;
import ww.common.WwSystem;
import ww.security.common.MD5;
import model.Admin;
import model.OpenUser;
import ww.service.admin.OpenUserService;

@Controller
@RequestMapping(value="/admin/OpenUser")
public class OpenUserController extends ww.controller.BaseController {
	
	@Autowired
	private OpenUserService service;
	
	
	//ajax
	@ResponseBody
	@RequestMapping(value="/check")
	public JSONObject check(java.lang.Integer id,java.lang.Integer pass,
			HttpServletRequest request,HttpServletResponse response){	
		JSONObject result=new JSONObject();
		if(id==null){
			result.put("success",false);
			result.put("msg","id无效！");			
			return result;
		}
		Admin admin=(Admin)request.getSession().getAttribute("admin");
				
		DbModel dao=new DbModel();
		SqlMap data=new SqlMap();
		data.put("status", pass);
		//int update = dao.table("t_open_user").where("id=:id").bind("id", id).update(data);
		
		try{
			dao.table("t_open_user").where("id=:id").bind("id", id).update(data);
		}catch(Exception e){
			result.put("success",false);
			result.put("msg","更新t_open_user表异常："+e.getMessage());
			WwLog.getLogger(this).error("t_open_user更新失败:"+dao.Message+" 审核人："+admin.getFname());
			return result;
		}
		WwLog.getLogger(this).info("审核人："+admin.getFname());
		result.put("success",true);
		result.put("msg","审核成功");			
		return result;
	}
	
	@RequestMapping(value="/list")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response){		
		ModelAndView mv=new ModelAndView();
		
		/*ListSearchState searchState=new ListSearchState(request,"OpenUser");
		searchState.saveStates();//保存状态
		*/		
		int page=WwSystem.ToInt(request.getParameter("page"));//第几页
        int pageRows=WwSystem.ToInt(request.getParameter("pageRows"));//每页显示行数
        String query=WwSystem.ToString(request.getParameter("query"));//搜索条件
        
        query=DbModel.sqlParamString(query);//非常重要，防sql注入
        String status=WwSystem.ToString(request.getParameter("status"));//搜索条件
        if(page<=0)
            page=1;
        if(pageRows<=0)
            pageRows=20;
            
        int beginRow=(page-1)*pageRows;
        
        if(status.isEmpty())
        	status="0";
        
        
        String where=" where status="+status;
        
        //请根据实际搜索条件更改代码
        if(!query.isEmpty()){
        	where+=" and phoneNum='"+query+"'";
        }

        String parkSql=where + " limit "+beginRow+","+pageRows;
    	List<OpenUser> list=service.getList(parkSql);
    	int allRows=service.getCount(where);

    	String rootUrl=WwSystem.getFullRoot(request);
		String  imgUrl=rootUrl+"public/file_list/busers/";
    	mv.addObject("list",list); //列表数据
    	mv.addObject("page",page); //第几页
    	mv.addObject("pageRows",pageRows); //每页显示行数
    	mv.addObject("allRows",allRows); //查询总行数	
    	mv.addObject("query",query); //查询总行数
    	mv.addObject("status",status);
    	mv.addObject("imgUrl",imgUrl);
		
		mv.setViewName(getListViewName());
		return mv;
	}
	
	@RequestMapping(value="/edit")
	public ModelAndView edit(java.lang.Integer id,HttpServletRequest request,HttpServletResponse response){	
		ModelAndView mv=new ModelAndView(getEditViewName());
		OpenUser data=null;
		if(id!=null)			
			data=service.getById(id);
		if(data==null)
			data=new OpenUser();		

		mv.addObject("data",data);		
		String rootUrl=WwSystem.getFullRoot(request);
		String  imgUrl=rootUrl+"public/file_list/busers/";
    	mv.addObject("imgUrl",imgUrl);
		return mv;
	}
	
	@RequestMapping(value="/save")
	public ModelAndView save(@Valid @ModelAttribute("data") OpenUser data,BindingResult result,
			HttpServletRequest request,HttpServletResponse response)throws Exception{		
		ModelAndView mv=null;		
		//如果有验证错误 返回到form页面
        if(result.hasErrors()){
        	mv= new ModelAndView(getEditViewName());
        	mv.addObject("data",data);
        	mv.addObject("error","验证错误！");
        	return mv;
        }
        
        OpenUser saveData=service.getById(data.getId());
		if(saveData==null){
			saveData=new OpenUser();
		}	
		//保存request提交的字段，其他字段为数据库已经存在的值
	    saveData.mergeProperty(data, request);	
				
		if(service.Save(saveData)){
			mv=new ModelAndView("redirect:/admin/OpenUser/list");
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
		OpenUser data=null;
		if(id!=null)
			data=service.getById(id);
		if(data==null)
			data=new OpenUser();
		
		mv.addObject("data",data);	
		String rootUrl=WwSystem.getFullRoot(request);
		String  imgUrl=rootUrl+"/public/file_list/busers/";
    	mv.addObject("imgUrl",imgUrl);
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
		return "admin/OpenUserEdit";
	}
	
	private String getViewViewName(){
		return "admin/OpenUserView";
	}
	
	private String getListViewName(){
		return "admin/OpenUserList";
	}
	
}
