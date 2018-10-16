<%@ page language="java" import="java.util.*,ww.common.WwSystem" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html>
  <head>
  	<base href="<%=WwSystem.getRoot(request)%>">
    <title>开放平台用户</title>
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <jsp:include page="common/main_style.jsp"></jsp:include>
    <!-- iCheck for checkboxes and radio inputs -->
    <style type="text/css">
    	.form-control{
    		border: none 0px;
    	}
    </style>
	<script type="text/javascript">
    	var rootUrl="<%=WwSystem.getRoot(request)%>";
    </script> 
  </head>
  <body class="skin-blue sidebar-mini">
	<!-- wrapper -->
	<div class="wrapper">
		<jsp:include page="common/main_top.jsp"></jsp:include>
		<jsp:include page="common/main_menu.jsp"></jsp:include>      
		
		<!-- Content Wrapper-->
		<div class="content-wrapper">
			<!-- Content Header-->
			<section class="content-header">
				<h1>
					开放平台用户
					<small>开放平台用户面板</small>
				</h1>
			</section>
			<!-- /content Header -->
			<!-- Main content -->
			<section class="content">
				<!-- panel -->
              	<div class="box box-primary">
                	<!-- panel-header -->
                	<div class="box-header with-border">
                  		<h3 class="box-title">开放平台用户查看</h3>
				<a type="button" class="btn btn-success" href="javascript:history.go(-1);">返回</a>
                	</div>
                	<!-- /panel-header -->
                	<!-- panel-body -->
                	<form:form commandName="data" id="form1" action="" method="post" class="form-horizontal">
                  		<div class="box-body">					
							
					<div class="form-group">
						<label for="id" class="col-sm-2 control-label">ID</label>
						<div class="col-sm-6 input-group">
							<span class="form-control">${data.id}</span>
						</div>
					</div>
					<div class="form-group">
						<label for="phoneNum" class="col-sm-2 control-label">手机号码</label>
						<div class="col-sm-6 input-group">
							<span class="form-control">${data.phoneNum}</span>
						</div>
					</div>
					<%-- <div class="form-group">
						<label for="password" class="col-sm-2 control-label">密码</label>
						<div class="col-sm-6 input-group">
							<span class="form-control">${data.password}</span>
						</div>
					</div> --%>
					<div class="form-group">
						<label for="type" class="col-sm-2 control-label">用户类型</label>
						<div class="col-sm-6 input-group">
							<span class="form-control">${data.type}</span>
						</div>
					</div>
					<div class="form-group">
						<label for="realName" class="col-sm-2 control-label">公司名</label>
						<div class="col-sm-6 input-group">
							<span class="form-control">${data.realName}</span>
						</div>
					</div>
					<div class="form-group">
						<label for="idNum" class="col-sm-2 control-label">证件号码</label>
						<div class="col-sm-6 input-group">
							<span class="form-control">${data.idNum}</span>
						</div>
					</div>
					<div class="form-group">
						<label for="photo1" class="col-sm-2 control-label">证件照片</label>
						<div class="col-sm-6 input-group">
							<%-- <span class="form-control">${data.photo1}</span> --%>
							<img src="${imgUrl}${data.photo1}" alt="证件照片" height="200" width="300"/>
						</div>
					</div>
					<%-- <div class="form-group">
						<label for="photo2" class="col-sm-2 control-label">证件照片2</label>
						<div class="col-sm-6 input-group">
							<span class="form-control">${data.photo2}</span>
						</div>
					</div> --%>
					<div class="form-group">
						<label for="linkMan" class="col-sm-2 control-label">联系人</label>
						<div class="col-sm-6 input-group">
							<span class="form-control">${data.linkMan}</span>
						</div>
					</div>
					<div class="form-group">
						<label for="linkPhone" class="col-sm-2 control-label">联系人电话</label>
						<div class="col-sm-6 input-group">
							<span class="form-control">${data.linkPhone}</span>
						</div>
					</div>
					<div class="form-group">
						<label for="status" class="col-sm-2 control-label">状态</label>
						<div class="col-sm-6 input-group">
							<span class="form-control">${data.status}</span>
						</div>
					</div>

							
						</div>
						<!-- /panel-body -->
						<!-- panel-footer -->
                  		<div class="box-footer">
                  		</div>
                  		<!-- /panel-footer -->
                	</form:form>
                	
                </div>
			  <!-- /panel -->
			  
			</section>
			<!-- /Main content -->
		</div>
		<!-- /Content Wrapper -->
	</div>
	<!-- /wrapper -->
	
	<jsp:include page="common/main_footer.jsp"></jsp:include>
	<jsp:include page="common/main_right.jsp"></jsp:include>      
	
	<jsp:include page="common/main_script.jsp"></jsp:include>
    <!-- iCheck 1.0.1 -->
    <script src="public/adminLTE/plugins/iCheck/icheck.min.js" type="text/javascript"></script>

	
    <script type="text/javascript">
    </script>    
  </body>
</html>
