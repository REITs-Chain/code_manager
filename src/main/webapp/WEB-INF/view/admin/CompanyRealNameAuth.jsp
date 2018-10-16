<%@ page language="java" import="java.util.*,ww.common.WwSystem" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html>
<head>
<base href="<%=WwSystem.getRoot(request)%>">
<title>文章</title>
<meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
<jsp:include page="common/main_style.jsp"></jsp:include>
<!-- Date Picker -->
<link href="public/adminLTE/plugins/datepicker/datepicker3.css" rel="stylesheet" type="text/css" />
<!-- Datetime Picker -->
<link href="public/adminLTE/plugins/bootstrap-datetimepicker/css/bootstrap-datetimepicker.min.css" rel="stylesheet" type="text/css" />
<!-- Daterange picker -->
<link href="public/adminLTE/plugins/daterangepicker/daterangepicker-bs3.css" rel="stylesheet" type="text/css" />
<!-- iCheck for checkboxes and radio inputs -->
<link href="public/adminLTE/plugins/iCheck/all.css" rel="stylesheet" type="text/css" />
<!-- Bootstrap time Picker -->
<link href="public/adminLTE/plugins/timepicker/bootstrap-timepicker.min.css" rel="stylesheet" type="text/css" />
<!-- Select2 -->
<link href="public/adminLTE/plugins/select2/select2.min.css" rel="stylesheet" type="text/css" />
<style type="text/css">
.wwerror {
	color: red;
	font-size: 11px;
}
</style>
<script type="text/javascript">
	var rootUrl = "<%=WwSystem.getRoot(request)%>";
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
					企业用户<small>企业用户面板</small>
				</h1>
			</section>
			<!-- /content Header -->
			<!-- Main content -->
			<section class="content">
				<!-- panel -->
				<div class="box box-primary">
					<!-- panel-header -->
					<div class="box-header with-border">
						<h3 class="box-title">企业用户编辑</h3>
						<a type="button" class="btn btn-success" href="javascript:history.go(-1);">返回</a>
					</div>
					<!-- /panel-header -->
					<!-- panel-body -->
					<form:form commandName="data" id="form1" action="admin/User/save2" method="post" class="form-horizontal" enctype="multipart/form-data">
						<div class="box-body">
							<c:if test="${error!=null&&error!=''}">
								<label for="error_id" class="col-sm-2 control-label"></label>
								<div class="form-group wwerror">
									<div class="col-sm-6 input-group">${error}</div>
								</div>
							</c:if>
							<div class="form-group" >
								<label for="id" class="col-sm-2 control-label">企业用户ID</label>
								<div class="col-sm-6 input-group">
									<input type="text" id="id" name="id" value="${one.getInt('id')}" class="form-control pull-right" readonly="readonly" ww-integer />
								</div>
							</div> 
							<div class="form-group" >
								<label for="phoneNum" class="col-sm-2 control-label">联系人</label>
								<div class="col-sm-6 input-group">
									<input type="text" id="phoneNum" name="phoneNum" value="${one.getInt('phoneNum')}" class="form-control pull-right"  ww-string />
								</div>
							</div> 
							<div class="form-group" >
								<label for="id" class="col-sm-2 control-label">真实姓名</label>
								<div class="col-sm-6 input-group">
									<input type="text" id="realName" name="realName" value="${one.getInt('realName')}" class="form-control pull-right"  ww-string />
								</div>
							</div> 
							 <div class="form-group">
								<label for="name" class="col-sm-2 control-label">公司名称</label>
								<div class="col-sm-6 input-group">
									<input type="text" id="company" name="company" value="${one.getString('company')}" class="form-control pull-right" ww-string />
								</div>
							</div>
							<div class="form-group">
								<label for="walletAddress" class="col-sm-2 control-label">公司地址</label>
								<div class="col-sm-6 input-group">
									<input type="text" id="walletAddress" name="walletAddress" value="${one.getString('walletAddress')}" class="form-control pull-right" ww-string />
								</div>
							</div>

							<div class="form-group">
								<label for="photo" class="col-sm-2 control-label">文件</label>
								<div class="col-sm-6 input-group">
									<input type="hidden" name="aeId" value="${select[0].id}"/>
									<input type="text"  id="orginFile" name="orginFile"  value="${select[0].orginFile}" readonly="readonly"  class="form-control pull-right" /> 
									<input type="file" id="orginFile" name="orginFile" multiple="multiple"/> 
								</div>
								<c:forEach var="select" items="${select}" begin="1">
									<label for="photo" class="col-sm-2 control-label">文件</label>
									
									<div class="col-sm-6 input-group">
										<input type="hidden" name="aeId" value="${select.id}"/>
										<input type="text"  id="orginFile" name="orginFile"  value="${select.orginFile}" readonly="readonly"  class="form-control pull-right" /> 
										<input type="file" id="orginFile" name="orginFile" multiple="multiple"/> 
										<!-- <input type="file" id="file_file1" name="file_file1"  onchange="WW.AjaxUploadMater('file_file1','file1')" /> -->
									</div>
								</c:forEach>
							</div>


						<div class="box-footer">
							<button type="submit" id="wwsubmit" class="btn btn-primary">提交</button>
						</div>
					</form:form>
					<!-- /panel-body -->
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
	<!-- datepicker -->
	<script src="public/adminLTE/plugins/datepicker/bootstrap-datepicker.js" type="text/javascript"></script>
	<script src="public/adminLTE/plugins/datepicker/locales/bootstrap-datepicker.zh-CN.js" type="text/javascript"></script>
	<!-- daterangepicker -->
	<script src="public/adminLTE/plugins/daterangepicker/moment-cn.min.js" type="text/javascript"></script>
	<script src="public/adminLTE/plugins/daterangepicker/daterangepicker-cn.js" type="text/javascript"></script>
	<!-- datetimepicker -->
	<script src="public/adminLTE/plugins/bootstrap-datetimepicker/js/bootstrap-datetimepicker.min.js" type="text/javascript"></script>
	<script src="public/adminLTE/plugins/bootstrap-datetimepicker/js/locales/bootstrap-datetimepicker.zh-CN.js" type="text/javascript"></script>
	<!-- bootstrap time picker -->
	<script src="public/adminLTE/plugins/timepicker/bootstrap-timepicker.min.js" type="text/javascript"></script>
	<!-- iCheck 1.0.1 -->
	<script src="public/adminLTE/plugins/iCheck/icheck.min.js" type="text/javascript"></script>
	<!-- Select2 -->
	<script src="public/adminLTE/plugins/select2/select2.full.min.js" type="text/javascript"></script>
	<!-- InputMask -->
	<script src="public/adminLTE/plugins/input-mask/jquery.inputmask.js" type="text/javascript"></script>
	<script src="public/adminLTE/plugins/input-mask/jquery.inputmask.date.extensions.js" type="text/javascript"></script>
	<script src="public/adminLTE/plugins/input-mask/jquery.inputmask.extensions.js" type="text/javascript"></script>
	<script src="public/adminLTE/plugins/input-mask/jquery.inputmask.numeric.extensions.js" type="text/javascript"></script>
	<script src="public/adminLTE/plugins/input-mask/jquery.inputmask.phone.extensions.js" type="text/javascript"></script>
	<script src="public/adminLTE/plugins/input-mask/jquery.inputmask.regex.extensions.js" type="text/javascript"></script>
	<script src="public/js/wwinputmask.js" type="text/javascript"></script>
	<script src="public/js/frame.js" type="text/javascript"></script>
	<!-- <script type="text/javascript" charset="utf-8" src="public/ueditor1_4_3/ueditor.config.js"></script>
	<script type="text/javascript" charset="utf-8" src="public/ueditor1_4_3/ueditor.all.min.js"></script>
	<script type="text/javascript" charset="utf-8" src="public/ueditor1_4_3/lang/zh-cn/zh-cn.js"></script> 
	<script type="text/javascript" src="public/js/ajaxfileupload.js"></script>-->

	<script type="text/javascript">
		var state="${state}";
    	if(state=="2"){
    		$("form input").prop("readonly", true);
    		$("select").prop("disabled", "disabled");
    		$("input[type=file]").hide();
    		//$(".btn btn-primary").hide();
    		$("#wwsubmit").hide();
    	}
    	if(state=="1"){
    		//$("select").prop("disabled", "disabled");
    		$("input[type=file]").hide();
    		//$(".btn btn-primary").hide();
    		//$("#wwsubmit").hide();
    	}
    </script>    
</body>
</html>
