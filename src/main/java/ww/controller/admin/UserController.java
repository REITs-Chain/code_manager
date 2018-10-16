package ww.controller.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.function.UnaryOperator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import ww.common.DbModel;
import ww.common.FileUploadUtil;
import ww.common.WwLog;
import ww.common.WwSystem;
import ww.common.FileUploadUtil.UploadResult;
import ww.common.SqlList;
import ww.common.SqlMap;
import model.Article;
import model.User;
import ww.service.admin.UserService;

@Controller
@RequestMapping(value = "/admin/User")
public class UserController extends ww.controller.BaseController {

	@Autowired
	private UserService service;
	
	
	@RequestMapping(value = "/delCompUser")
	public ModelAndView delCompUser(java.lang.Integer id,HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView(getListCompanyName());
		if (id == null) {
			mv.addObject("msg", "id无效！");
			mv.addObject("success", false);
			return mv;
		}
		String path = request.getSession().getServletContext().getRealPath("/private/compMaterials/");
		DbModel dao=new DbModel();
		SqlList select = dao.table("t_auth_evidence").where("userId=:id").bind("id", id).select("id,newFile");
		for (int i = 0; i < select.size(); i++) {
			boolean delFile = UserController.delFile(path, select.get(i).getString("newFile"));
			if (!delFile) {
				WwLog.getLogger(this).error(select.get(i)+"文件删除失败");
			}
		}
		int delete2 = dao.table("t_auth_evidence").where("userId=:id").bind("id", id).Delete();
		if (delete2<0) {
			mv.addObject("msg", "文件删除失败！");
			mv.addObject("success", false);
			return mv;
		}
		
		int delete = dao.table("t_user").where("id=:id").bind("id", id).Delete();
		if (delete<0) {
			mv.addObject("msg", "用户删除失败！");
			mv.addObject("success", false);
			return mv;
		}
		mv.addObject("msg", "删除成功！");
		mv.addObject("success", true);
		mv.setViewName("redirect:/admin/User/companyList");
		return mv;
	}
	//添加文件
	@RequestMapping("/editMater")
	public ModelAndView editMater(HttpServletRequest request, HttpServletResponse response, Integer userId)throws Exception {
		ModelAndView mv = new ModelAndView(getEditMaterName());
		mv.addObject("userId", userId);
		return mv;
	}
		
	//添加文件
	@RequestMapping("/addMater")
	public ModelAndView addMater(HttpServletRequest request, HttpServletResponse response, Integer userId,MultipartFile[] orginFile,String savePath)throws Exception {
		ModelAndView mv = new ModelAndView();
		DbModel dao=new DbModel();
		if(savePath==null||savePath.isEmpty())
		{
			savePath="/private/compMaterials/";//默认保存路径
		}
		for (int i = 0; i < orginFile.length; i++) {
		    UploadResult updateFile = UserController.updateFile(request, savePath, orginFile[i]);
		    SqlMap data = new SqlMap();
			data.put("userId", userId);
			data.put("orginFile", updateFile.srcFileName);
			data.put("newFile", updateFile.newFileName);

			long ae = dao.table("t_auth_evidence").insert(data);
			if (ae < 0) {
				mv.addObject("message", "认证存证添加失败");
				mv.addObject("success", false);
				mv.setViewName(getEditMaterName());
				return mv;
			}
		}
		
        mv.addObject("message", "添加成功");
		mv.addObject("success", true);
		mv.setViewName("redirect:/admin/User/getMaterList?id="+userId);
		return mv;
	}
	// 删除文件
	@RequestMapping("/delMater")
	public ModelAndView delMater(HttpServletRequest request, HttpServletResponse response, String  file)throws Exception {
		ModelAndView mv = new ModelAndView();
		DbModel dao = new DbModel();
		SqlMap selectOne = dao.table("t_auth_evidence").where("newFile=:newFile").bind("newFile", file).selectOne("userId");
		int delete = dao.table("t_auth_evidence").where("newFile=:newFile").bind("newFile", file).Delete();
		if (delete<0) {
			mv.addObject("message", "删除失败");
			mv.addObject("success", false);
			mv.setViewName("redirect:/admin/User/getMaterList?id="+selectOne.getInt("userId"));
			return mv;
		}
		String path = request.getSession().getServletContext().getRealPath("/private/compMaterials/");
		/*File file = new File(path+newFile);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
            	mv.addObject("message", "删除成功");
        		mv.addObject("success", true);
        		return mv;
            } 
        } */
		if (!UserController.delFile(path,file)) {
			mv.addObject("message", "删除失败");
			mv.addObject("success", false);
			mv.setViewName("redirect:/admin/User/getMaterList?id="+selectOne.getInt("userId"));
			return mv;
		}
		mv.addObject("message", "删除成功");
		mv.addObject("success", true);
		mv.setViewName("redirect:/admin/User/getMaterList?id="+selectOne.getInt("userId"));
		return mv;
	}
	

	// 资料列表
	@RequestMapping("/getMaterList")
	public ModelAndView getMaterList(HttpServletRequest request, HttpServletResponse response, Integer id)throws Exception {
		ModelAndView mv = new ModelAndView();
		DbModel dao = new DbModel();
		
		String sql1=" select id,orginFile,newFile,userId from  t_auth_evidence  where userId="+id;
		SqlList query2 = dao.sql(sql1).query();
		mv.addObject("message", "查询成功");
		mv.addObject("success", true);
		mv.addObject("list", query2);
		mv.setViewName(getListMater());
		return mv;
	}

	// 资料查看
	@RequestMapping("/devMater")
	public void devMater(HttpServletRequest request, HttpServletResponse response, String file) throws Exception {
		request.setCharacterEncoding("UTF-8");
		String downLoad = WwSystem.getRootPath() + "/private/compMaterials/" + file;
		// response.setContentType("application/pdf");
		FileInputStream in = new FileInputStream(new File(downLoad));
		OutputStream out = response.getOutputStream();
		byte[] b = new byte[1024];
		while ((in.read(b)) != -1) {
			out.write(b);
		}
		out.flush();
		in.close();
		out.close();
	}

	@RequestMapping(value = "/edit2")
	public ModelAndView edit2(java.lang.Integer id,Integer state,
			HttpServletRequest request,HttpServletResponse response) {
		ModelAndView mv = new ModelAndView(getEditCompanyName());
		/*User data = null;
		if (id != null)
			data = service.getById(id);
		if (data == null)
			data = new User();*/
		if (state == null)
			state = 0;
		DbModel dao=new DbModel();
		SqlMap queryToFirst=null;
		SqlList select=null;
		if (id!=null) {
			String sql=" select u.id,u.walletAddress,u.realName,u.company,u.certificationTime,u.phoneNum  from t_user u   ";
			   sql+=" where u.type=1 and  u.id="+id;
			//list = dao.sql(sql).query();
			queryToFirst = dao.sql(sql).queryToFirst();
			if (queryToFirst==null) {
				mv.addObject("state", state);
				mv.addObject("message", "信息不存在");
				mv.addObject("success", false);
				return mv;
			}
			select = dao.table("t_auth_evidence").where("userId=:userId").bind("userId", id).select("id,newFile,orginFile");
			if (select==null) {
				mv.addObject("state", state);
				mv.addObject("message", "文件不存在");
				mv.addObject("success", false);
				return mv;
			}
		}
		
		mv.addObject("state", state);
		mv.addObject("select", select);
		mv.addObject("one", queryToFirst);
		return mv;
	}
	
	// 企业用户及存证 保存
	@RequestMapping(value = "/save2")
	public ModelAndView save2(MultipartFile[] orginFile,String savePath,HttpServletRequest request, HttpServletResponse response) throws Exception {
		//DefaultMultipartHttpServletRequest multipartRequest = (DefaultMultipartHttpServletRequest) request;
		// 得到上传的文件
		//MultipartFile mFile1 = multipartRequest.getFiles(file);
		if(savePath==null||savePath.isEmpty())
		{
			savePath="/private/compMaterials/";//默认保存路径
		}
		
		ModelAndView mv = new ModelAndView();
		String compName = WwSystem.ToString(request.getParameter("company"));// 公司名称
		String realName = WwSystem.ToString(request.getParameter("realName"));// 公司实名
		String phoneNum = WwSystem.ToString(request.getParameter("phoneNum"));// 手机号码
		String compAddress = WwSystem.ToString(request.getParameter("walletAddress"));// 公司地址
		int id = WwSystem.ToInt(request.getParameter("id"));// 公司地址
		int aeId = WwSystem.ToInt(request.getParameter("aeId"));// 公司地址
		
		DbModel dao = new DbModel();
		SqlMap data1 = new SqlMap();
		data1.put("realName", realName);
		data1.put("company", compName);
		data1.put("phoneNum", phoneNum);
		data1.put("walletAddress", compAddress);
		 
		if (Integer.valueOf(id)==null||id==0) {
			data1.put("type", 1);
			data1.put("status", 2);
			data1.put("certificationStatus", 2);
			data1.put("certificationTime", WwSystem.now());
			long l= dao.table("t_user").insert(data1);
			if (l < 0) {
				mv.addObject("message", "企业用户添加失败");
				mv.addObject("success", false);
				mv.setViewName(getEditCompanyName());
				return mv;
			}
			for (int i = 0; i < orginFile.length; i++) {
				/*if(file[i].getSize()>0){
					// 得到上传服务器的路径
					String path = request.getSession().getServletContext().getRealPath(savePath);			
					path=path.replaceAll("\\\\", "/");//主要这里是正则表达式\\\\代表一个\
				    // 得到上传的文件的文件名
					String filename = file[i].getOriginalFilename();
					String exname = filename.substring(filename.lastIndexOf(".")+1);//截取文件类型
				    
					String newfilename = "file_"+WwSystem.UUID()+"."+exname;//修改文件名
				    String pathfile = path + "/"+newfilename;
				    pathfile=pathfile.replaceAll("//", "/");
				    try {
						FileCopyUtils.copy(file[i].getBytes(), new File(pathfile));
					} catch (IOException e) {
						e.printStackTrace();
						e.getMessage();
					}*/
				    UploadResult updateFile = UserController.updateFile(request, savePath, orginFile[i]);
				    SqlMap data = new SqlMap();
					data.put("userId", l);
					data.put("orginFile", updateFile.srcFileName);
					data.put("newFile", updateFile.newFileName);

					long ae = dao.table("t_auth_evidence").insert(data);
					if (ae < 0) {
						mv.addObject("message", "认证存证添加失败");
						mv.addObject("success", false);
						mv.setViewName(getEditCompanyName());
						return mv;
					}
				}
		}else{
			int u = dao.table("t_user").where("id=:id").bind("id", id).update(data1);
			if (u<0) {
				mv.addObject("message", "企业用户修改失败");
				mv.addObject("success", false);
				mv.setViewName(getEditCompanyName());
				return mv;
			}
			if (aeId!=0) {
				SqlMap selectOne = dao.table("t_auth_evidence").where("id=:id").bind("id", aeId).selectOne("userId,newFile");
				if (selectOne==null) {
					mv.addObject("message", "文件不存在");
					mv.addObject("success", false);
					mv.setViewName(getEditCompanyName());
					return mv;
				}
				String path = request.getSession().getServletContext().getRealPath("/private/compMaterials/");
				if (!UserController.delFile(path,selectOne.getString("newFile"))) {
					mv.addObject("message", "删除失败");
					mv.addObject("success", false);
					mv.setViewName(getEditCompanyName());
					return mv;
				}
			}
			
			
			if(savePath==null||savePath.isEmpty())
			{
				savePath="/private/compMaterials/";//默认保存路径
			}
			    UploadResult updateFile = UserController.updateFile(request, savePath, orginFile[0]);
			    SqlMap data = new SqlMap();
				//data.put("userId", id);
				data.put("orginFile", updateFile.srcFileName);
				data.put("newFile", updateFile.newFileName);

				long ae = dao.table("t_auth_evidence").where("id=:id").bind("id", aeId).update(data);
						
				if (ae < 0) {
					mv.addObject("message", "认证存证修改失败");
					mv.addObject("success", false);
					mv.setViewName(getEditCompanyName());
					return mv;
				}
		}
		mv.addObject("message", "添加成功");
		mv.addObject("success", true);
		mv.setViewName("redirect:/admin/User/companyList");
		return mv;
	}

	// 企业用户列表
	@RequestMapping(value = "/companyList")
	public ModelAndView companyList(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();


		int page = WwSystem.ToInt(request.getParameter("page"));// 第几页
		int beginRow = WwSystem.ToInt(request.getParameter("beginRow"));// 每页开始行
		int pageRows = WwSystem.ToInt(request.getParameter("pageRows"));// 每页显示行数
		String query = WwSystem.ToString(request.getParameter("query"));// 搜索条件

		if (page <= 0)
			page = 1;
		if (beginRow <= 0)
			beginRow = 0;
		if (pageRows <= 0)
			pageRows = 20;
		DbModel dao=new DbModel();
		/*String sql=" select u.id,u.phoneNum,u.realName,u.walletAddress,e.compName,e.file1,e.file2,e.file3,e.file4,e.file5  ";
			   sql+=" from t_user u left join t_auth_evidence e on u.phoneNum=e.phone ";
			   sql+=" where u.type=1 ";*/
		
		//String sql=" select u.id,u.walletAddress,u.realName,u.company,u.certificationTime,e.orginFile,e.id as aeId from t_user u left join t_auth_evidence e on u.id=e.userId where u.type=1 ";
		String sql=" select u.id,u.walletAddress,u.realName,u.company,u.certificationTime from t_user u where u.type=1 ";
		// 请根据实际搜索条件更改代码
		if (!query.isEmpty()) {
			query=query.replaceAll("'", ""); //防注入 
			sql+= " and u.phoneNum='" + query + "' or u.realName='" + query + "' or u.company='" + query + "' or u.walletAddress='"+ query +"'";
		}
		long[] tt=dao.countPages(sql, pageRows);
		SqlList list = dao.sql(sql).query();
		
		/*String parkSql = where + " limit " + beginRow + "," + pageRows;
		List<User> list = service.getList(parkSql);
		int allRows = service.getCount(where);*/

		mv.addObject("list", list); // 列表数据
		mv.addObject("page", page); // 第几页
		mv.addObject("pageRows", pageRows); // 每页显示行数
		mv.addObject("allRows", tt[0]); // 查询总行数
		mv.addObject("query", query); // 查询总行数

		mv.setViewName(getListCompanyName());
		return mv;
	}

	@RequestMapping(value = "/list")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();

		// test
		// String num=SmsUtil.getNewVerifyNum();
		// boolean b=SmsUtil.sendVerifyNum_Reg("18787008828", num);

		// test
		// IDCard idc=new IDCard();
		// int res=idc.idcard_verify("杨毓旺", "532424197702040315");

		int page = WwSystem.ToInt(request.getParameter("page"));// 第几页
		int beginRow = WwSystem.ToInt(request.getParameter("beginRow"));// 每页开始行
		int pageRows = WwSystem.ToInt(request.getParameter("pageRows"));// 每页显示行数
		String query = WwSystem.ToString(request.getParameter("query"));// 搜索条件

		if (page <= 0)
			page = 1;
		if (beginRow <= 0)
			beginRow = 0;
		if (pageRows <= 0)
			pageRows = 20;

		String where = "";

		// 请根据实际搜索条件更改代码
		if (!query.isEmpty()) {
			where = " where phoneNum='" + query + "' or realName='" + query + "'";
		}

		String parkSql = where + " limit " + beginRow + "," + pageRows;
		List<User> list = service.getList(parkSql);
		int allRows = service.getCount(where);

		mv.addObject("list", list); // 列表数据
		mv.addObject("page", page); // 第几页
		mv.addObject("pageRows", pageRows); // 每页显示行数
		mv.addObject("allRows", allRows); // 查询总行数
		mv.addObject("query", query); // 查询总行数

		mv.setViewName(getListViewName());
		return mv;
	}

	@RequestMapping(value = "/edit")
	public ModelAndView edit(java.lang.Integer id, Integer state, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView(getEditViewName());
		User data = null;
		if (id != null)
			data = service.getById(id);
		if (data == null)
			data = new User();

		if (state == null)
			state = 0;

		mv.addObject("state", state);
		mv.addObject("data", data);
		return mv;
	}

	@RequestMapping(value = "/save")
	public ModelAndView save(@Valid @ModelAttribute("data") User data, BindingResult result, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = null;
		// 如果有验证错误 返回到form页面
		if (result.hasErrors()) {
			mv = new ModelAndView(getEditViewName());
			mv.addObject("data", data);
			mv.addObject("error", "验证错误！");
			return mv;
		}

		User saveData = service.getById(data.getId());
		if (saveData == null) {
			saveData = new User();
		}
		// 保存request提交的字段，其他字段为数据库已经存在的值
		saveData.mergeProperty(data, request);

		if (service.Save(saveData)) {
			mv = new ModelAndView("redirect:/admin/User/list");
		} else {
			mv = new ModelAndView(getEditViewName());
			mv.addObject("error", "保存失败！");
			mv.addObject("data", saveData);
		}

		return mv;
	}

	@RequestMapping(value = "/view")
	public ModelAndView view(java.lang.Integer id, HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView(getViewViewName());
		User data = null;
		if (id != null)
			data = service.getById(id);
		if (data == null)
			data = new User();

		mv.addObject("data", data);
		return mv;
	}

	@RequestMapping(value = "/confirm")
	public ModelAndView confirm(java.lang.Integer id, HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView("admin/UserConfirm");
		User data = null;
		if (id != null)
			data = service.getById(id);
		if (data == null)
			data = new User();

		mv.addObject("data", data);
		return mv;
	}

	@RequestMapping(value = "/delete")
	public ModelAndView delete(java.lang.Integer id, HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView("forward:/admin/User/list");
		if (id == null) {
			mv.addObject("msg", "id无效！");
			mv.addObject("success", "false");
			return mv;
		}
		if (service.Delete(id)) {
			mv.addObject("msg", "删除成功！");
			mv.addObject("success", "true");
		} else {
			mv.addObject("msg", "删除失败！");
			mv.addObject("success", "false");
		}

		return mv;
	}

	@RequestMapping(value = "/pass")
	public ModelAndView pass(java.lang.Integer id, HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();

		User user = service.getById(id);
		if (user == null) {
			mv.addObject("error", "异常：找不到用户！");
			mv.addObject("data", new User());
			mv.setViewName("redirect:/admin/User/UserConfirm");
			return mv;
		}

		user.setCertificationStatus(2);

		if (!service.Save(user)) {
			mv.addObject("error", "保存失败");
			mv.addObject("data", user);
			mv.setViewName("redirect:/admin/User/UserConfirm");
			return mv;
		}

		mv.setViewName("redirect:/admin/User/list");
		return mv;
	}

	@RequestMapping(value = "/deny")
	public ModelAndView deny(java.lang.Integer id, HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();

		User user = service.getById(id);
		if (user == null) {
			mv.addObject("error", "异常：找不到用户！");
			mv.addObject("data", new User());
			mv.setViewName("redirect:/admin/User/UserConfirm");
			return mv;
		}

		user.setCertificationStatus(0);

		if (!service.Save(user)) {
			mv.addObject("error", "保存失败");
			mv.addObject("data", user);
			mv.setViewName("redirect:/admin/User/UserConfirm");
			return mv;
		}

		mv.setViewName("redirect:/admin/User/list");
		return mv;
	}

	 public static boolean delFile(String path,String filename){
         File file=new File(path+"/"+filename);
         if(file.exists()&&file.isFile()){
             file.delete();
         	 return true;
         }
        	 return false;
     }
	public static UploadResult updateFile(HttpServletRequest request,String savePath,MultipartFile file){
		if(savePath==null||savePath.isEmpty())
		{
			savePath="/private/compMaterials/";//默认保存路径
		}
		if(file.getSize()>0){
			// 得到上传服务器的路径
			String path = request.getSession().getServletContext().getRealPath(savePath);			
			path=path.replaceAll("\\\\", "/");//主要这里是正则表达式\\\\代表一个\
		    // 得到上传的文件的文件名
			String filename = file.getOriginalFilename();
			String exname = filename.substring(filename.lastIndexOf(".")+1);//截取文件类型
		    
		    String newfilename = WwSystem.UUID()+"_"+filename;//修改文件名
		    String pathfile = path + "/"+newfilename;
		    pathfile=pathfile.replaceAll("//", "/");
		    try {
				FileCopyUtils.copy(file.getBytes(), new File(pathfile));
			} catch (IOException e) {
				e.printStackTrace();
				return new UploadResult(false,e.getMessage());
			}
		    return new UploadResult(true,filename, newfilename, exname,file.getSize());
		}
		
		return new UploadResult(false, "上传的文件没有数据");
	}
	
	
	private String getListMater() {
		return "admin/CompanyMaterList";
	}

	private String getListCompanyName() {
		return "admin/CompanyRealNameList";
	}

	private String getEditCompanyName() {
		return "admin/CompanyRealNameAuth";
	}

	private String getEditMaterName() {
		return "admin/CompanyMaterEdit";
	}
	
	private String getEditViewName() {
		 return "admin/UserEdit";
		//return "admin/CompanyRealNameAuth";
	}

	private String getViewViewName() {
		return "admin/UserView";
	}

	private String getListViewName() {
		return "admin/UserList";
	}

	private String getGroupUserName() {
		return "admin/GroupJoinUser";
	}

}
