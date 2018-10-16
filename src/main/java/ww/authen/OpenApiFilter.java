package ww.authen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import ww.common.FileUploadUtil;
import ww.common.ModelDAO;
import ww.common.Multipart_form_data;
import ww.common.WwLog;
import model.Admin;

public class OpenApiFilter implements Filter {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {	
		HttpServletRequest req=(HttpServletRequest)request;
		HttpServletResponse res=(HttpServletResponse)response;
		//HttpSession session=req.getSession();
		
		//ModelDAO dao=new ModelDAO();
		String token=req.getParameter("token");	
		//验证是否登录
//		String token=req.getParameter("token");	
		if(token==null){
			String content_type=request.getContentType();		
			System.out.print(content_type);
			if(FileUploadUtil.isMultipartContent(req)){
				/*CommonsMultipartResolver cmr=new CommonsMultipartResolver();
				MultipartHttpServletRequest mreq=cmr.resolveMultipart(req);*/
				MultipartHttpServletRequest mreq=FileUploadUtil.resolverRequest(req);
				token=mreq.getParameter("token");
				request=mreq;
				//chain.doFilter(request, response);
			}
		}
		
		if(token==null || token.isEmpty() || OpenApiContext.getUser(token)==null){//未登陆
			//session.setAttribute("gourl_admin", req.getServletPath());
			//RequestDispatcher dispatcher=request.getRequestDispatcher("/admin/login");
			//dispatcher.forward(request, response);
			//res.sendRedirect("admin/login");
			res.addHeader("Access-Control-Allow-Origin", "*"); //本次回复允许跨域
			response.setContentType("application/json;charset=utf-8");//("text/html;charset=utf-8");("application/json;charset=utf-8")
			response.getWriter().write("{\"success\":false,\"code\":101,\"message\":\"已过期请重新登录！\"}");
			WwLog.getLogger(this).info("{\"success\":false,\"code\":101,\"message\":\"已过期请重新登录！\"}");
		}else if(!token.equals(OpenApiContext.getToken(OpenApiContext.getUser(token).getUsername()))){//未登陆
			res.addHeader("Access-Control-Allow-Origin", "*"); //本次回复允许跨域
			response.setContentType("application/json;charset=utf-8");//("text/html;charset=utf-8");
			response.getWriter().write("{\"success\":false,code:101,\"message\":\"已过期请重新登录。\"}");
			WwLog.getLogger(this).info("{\"success\":false,\"code\":101,\"message\":\"已过期请重新登录。\"}");
		}else{//已登陆			
			chain.doFilter(request, response);
		}
				

	}
	

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}	

}
