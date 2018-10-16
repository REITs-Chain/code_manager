<%@ page language="java" import="java.util.*,ww.common.WwSystem"
	pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html>
<head>
<base href="<%=WwSystem.getRoot(request)%>">
<title>会员用户</title>
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport">
<jsp:include page="common/main_style.jsp"></jsp:include>
<style type="text/css">
.hidcol {
	display: none;
}
</style>
<script type="text/javascript">
	var rootUrl = "<%=WwSystem.getRoot(request)%>";
</script>
<script type="text/javascript">
	function onDelete(id) {
		var result = confirm("你确定要删除吗?");
		if (result == true) {
			return true;
		} else {
			return false;
		}
	}
	function onCheck(me) {
		var v = me.checked;
		if (v == true) {
			var list = $("#table_list input[type='checkbox']");
			$.each(list, function() {
				this.checked = "checked";
			});
		} else {
			//$("#test1").removeAttr("checked");
			$("#table_list input[type='checkbox']").removeAttr("checked");
		}
	}
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
					企业用户 <small>企业存证资料</small>
				</h1>
			</section>
			<!-- /content Header -->
			<!-- Main content -->
			<section class="content">
				<!-- panel -->
				<div class="box box-primary">
					<!-- panel-header -->
					<div class="box-header with-border">
						<h3 class="box-title pull-left"
							style="margin-right: 20px;margin-top: 8px">存证资料管理</h3>
						<div class="btn-group btn-group-sm pull-left">
							<!-- <a type="button" class="btn btn-success" href="admin/User/edit">新增</a> -->
							<a type="button" class="btn btn-warning"
								href="admin/User/editMater?userId=${list.get(0).getInt('userId')}">添加</a>
						</div>
						<%-- <form id="table_form" action="admin/User/companyList" method="post">
							<input type="hidden" name="page" value="1"/>
							<input type="hidden" name="beginRow" value="0"/>					
							<input type="hidden" name="pageRows" value="0"/>
							
							<div class="pull-right" style="width:200px;margin-bottom: 3px;">
								<div class="input-group">
					            	<input type="text" name="query" value="${query}" class="form-control" placeholder="搜索..." />
					            	<span class="input-group-addon" onclick="$('#table_form').submit()"><i class="fa fa-search"></i></span>
					            </div>
				            </div>					
						</form> --%>
					</div>
					<!-- /panel-header -->
					<!-- panel-body -->
					<div class="box-body">
						<table id="table_list"
							class="table table-striped table-bordered table-condensed">
							<thead>
								<tr>
									<th>操作</th>
									<th>ID</th>
									<th colspan="5">文件</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="item" items="${list}">
									<tr>
										<td style="width:130px">
											<div class="btn-group btn-group-xs" style="margin: 0;">
												<a class="btn btn-primary" href="admin/User/devMater?file=${item.newFile}&state=1"><i class="am-icon-edit" style="margin-right:4px;"></i>查看</a> 
												<a class="btn btn-primary" href="admin/User/delMater?file=${item.newFile}&state=1"><i class="am-icon-edit" style="margin-right:4px;"></i>删除</a> 
											</div>
										</td>
										<td>${item.id}</td>
										<td>${item.orginFile}</td>
										<td>${item.userId}</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
						<ul id="paging" class="pagination">
						</ul>
					</div>
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
	<script src="public/js/wwcommon.js" type="text/javascript"></script>
	<!-- <script type="text/javascript">
   		$(function(){ 	
   			initTablePaging("table_list","table_form",${page},${pageRows},${allRows});
   		});	
    
    </script>     -->
</body>
</html>
