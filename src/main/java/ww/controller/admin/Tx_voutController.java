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
import org.springframework.web.servlet.ModelAndView;

import ww.common.WwSystem;
import model.Tx_vout;
import ww.service.admin.Tx_voutService;

@Controller
@RequestMapping(value="/admin/Tx_vout")
public class Tx_voutController extends ww.controller.BaseController {
	
	@Autowired
	private Tx_voutService service;
	
	@RequestMapping(value="/list")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response){		
		ModelAndView mv=new ModelAndView();
		
		int page=WwSystem.ToInt(request.getParameter("page"));//第几页
        int beginRow=WwSystem.ToInt(request.getParameter("beginRow"));//每页开始行
        int pageRows=WwSystem.ToInt(request.getParameter("pageRows"));//每页显示行数
        String query=WwSystem.ToString(request.getParameter("query"));//搜索条件
        
        if(page<=0)
            page=1;
        if(beginRow<=0)
            beginRow=0;
        if(pageRows<=0)
            pageRows=20;
        
        String where="";
        /*
        //请根据实际搜索条件更改代码
        if(!query.isEmpty()){
        	where=" where fname='"+query+"'";
        }*/

        String parkSql=where + " limit "+beginRow+","+pageRows;
    	List<Tx_vout> list=service.getList(parkSql);
    	int allRows=service.getCount(where);

    	mv.addObject("list",list); //列表数据
    	mv.addObject("page",page); //第几页
    	mv.addObject("pageRows",pageRows); //每页显示行数
    	mv.addObject("allRows",allRows); //查询总行数	
    	mv.addObject("query",query); //查询总行数		
		
		mv.setViewName(getListViewName());
		return mv;
	}
	
	@RequestMapping(value="/edit")
	public ModelAndView edit(java.lang.Long id,Integer state,HttpServletRequest request,HttpServletResponse response){	
		ModelAndView mv=new ModelAndView(getEditViewName());
		Tx_vout data=null;
		if(id!=null)			
			data=service.getById(id);
		if(data==null)
			data=new Tx_vout();
		
		if(state==null)
			state=0;
		
		mv.addObject("state", state);	
		mv.addObject("data",data);		
		return mv;
	}
	
	@RequestMapping(value="/save")
	public ModelAndView save(@Valid @ModelAttribute("data") Tx_vout data,BindingResult result,
			HttpServletRequest request,HttpServletResponse response)throws Exception{		
		ModelAndView mv=null;		
		//如果有验证错误 返回到form页面
        if(result.hasErrors()){
        	mv= new ModelAndView(getEditViewName());
        	mv.addObject("data",data);
        	mv.addObject("error","验证错误！");
        	return mv;
        }
        
        Tx_vout saveData=service.getById(data.getId());
		if(saveData==null){
			saveData=new Tx_vout();
		}	
		//保存request提交的字段，其他字段为数据库已经存在的值
	    saveData.mergeProperty(data, request);	
				
		if(service.Save(saveData)){
			mv=new ModelAndView("redirect:/admin/Tx_vout/list");
		}else{			
			mv=new ModelAndView(getEditViewName());
			mv.addObject("error","保存失败！");
			mv.addObject("data",saveData);
		}
		
		return mv;
	}
	
	@RequestMapping(value="/view")
	public ModelAndView view(java.lang.Long id,HttpServletRequest request,HttpServletResponse response){	
		ModelAndView mv=new ModelAndView(getViewViewName());
		Tx_vout data=null;
		if(id!=null)
			data=service.getById(id);
		if(data==null)
			data=new Tx_vout();
		
		mv.addObject("data",data);		
		return mv;
	}
	
	@RequestMapping(value="/delete")
	public ModelAndView delete(java.lang.Long id,HttpServletRequest request,HttpServletResponse response){	
		ModelAndView mv=new ModelAndView("forward:/admin/Tx_vout/list");
		if(id==null){
			mv.addObject("msg","id无效！");
			mv.addObject("success","false");
			return mv;
		}
		if(service.Delete(id)){
			mv.addObject("msg","删除成功！");
			mv.addObject("success","true");
		}else{
			mv.addObject("msg","删除失败！");
			mv.addObject("success","false");
		}
		
		return mv;
	}
	
	private String getEditViewName(){
		return "admin/Tx_voutEdit";
	}
	
	private String getViewViewName(){
		return "admin/Tx_voutView";
	}
	
	private String getListViewName(){
		return "admin/Tx_voutList";
	}
	
}
