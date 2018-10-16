<%@ page language="java" import="java.util.*,ww.common.WwSystem" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html>
  <head>
  	<base href="<%=WwSystem.getRoot(request)%>">
    <title>交易_vout</title>
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
					交易_vout
					<small>交易_vout面板</small>
				</h1>
			</section>
			<!-- /content Header -->
			<!-- Main content -->
			<section class="content">
				<!-- panel -->
              	<div class="box box-primary">
                	<!-- panel-header -->
                	<div class="box-header with-border">
                  		<h3 class="box-title">交易_vout查看</h3>
				<a type="button" class="btn btn-success" href="admin/Tx_vout/list">返回</a>
                	</div>
                	<!-- /panel-header -->
                	<!-- panel-body -->
                	<form:form commandName="data" id="form1" action="" method="post" class="form-horizontal">
                  		<div class="box-body">					
							
					<div class="form-group">
						<label for="id" class="col-sm-2 control-label"></label>
						<div class="col-sm-6 input-group">
							<span class="form-control">${data.id}</span>
						</div>
					</div>
					<div class="form-group">
						<label for="parentid" class="col-sm-2 control-label"></label>
						<div class="col-sm-6 input-group">
							<span class="form-control">${data.parentid}</span>
						</div>
					</div>
					<div class="form-group">
						<label for="userId" class="col-sm-2 control-label"></label>
						<div class="col-sm-6 input-group">
							<span class="form-control">${data.userId}</span>
						</div>
					</div>
					<div class="form-group">
						<label for="rootAddress" class="col-sm-2 control-label"></label>
						<div class="col-sm-6 input-group">
							<span class="form-control">${data.rootAddress}</span>
						</div>
					</div>
					<div class="form-group">
						<label for="assetid" class="col-sm-2 control-label"></label>
						<div class="col-sm-6 input-group">
							<span class="form-control">${data.assetid}</span>
						</div>
					</div>
					<div class="form-group">
						<label for="value" class="col-sm-2 control-label"></label>
						<div class="col-sm-6 input-group">
							<span class="form-control">${data.value}</span>
						</div>
					</div>
					<div class="form-group">
						<label for="vout_n" class="col-sm-2 control-label"></label>
						<div class="col-sm-6 input-group">
							<span class="form-control">${data.vout_n}</span>
						</div>
					</div>
					<div class="form-group">
						<label for="type" class="col-sm-2 control-label"></label>
						<div class="col-sm-6 input-group">
							<span class="form-control">${data.type}</span>
						</div>
					</div>
					<div class="form-group">
						<label for="address" class="col-sm-2 control-label"></label>
						<div class="col-sm-6 input-group">
							<span class="form-control">${data.address}</span>
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
