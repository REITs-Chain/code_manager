<%@ page language="java" import="java.util.*,ww.common.WwSystem" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
  <head>
  	<base href="<%=WwSystem.getRoot(request)%>">
    <title>会员用户</title>
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <jsp:include page="common/main_style.jsp"></jsp:include>    
    <style type="text/css">
    	.hidcol{
    		display: none;
    	}
    </style>
	<script type="text/javascript">
    	var rootUrl="<%=WwSystem.getRoot(request)%>";
    </script> 
    <script type="text/javascript">
    function onDelete(id){
    	var result =confirm("你确定要删除吗?");
    	if(result ==true){
    		return true;
    	}else{
    		return false;
    	}
    }
    function onCheck(me){
    	var v=me.checked;
    	if(v==true){
    		var list=$("#table_list input[type='checkbox']");
    		$.each(list,function(){
    			this.checked="checked";
    		});
    	}else{
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
					会员用户
					<small>会员用户面板</small>
				</h1>
			</section>
			<!-- /content Header -->
			<!-- Main content -->
			<section class="content">
				<!-- panel -->
              	<div class="box box-primary">
                	<!-- panel-header -->
                	<div class="box-header with-border">
				        <h3 class="box-title pull-left"  style="margin-right: 20px;margin-top: 8px">会员用户管理</h3>
                  		<div class="btn-group btn-group-sm pull-left">
						  <!-- <a type="button" class="btn btn-success" href="admin/User/edit">新增</a> -->
						  <a type="button" class="btn btn-warning" href="admin/User/list">刷新</a>
						</div>
						<form id="table_form" action="admin/User/list" method="post">
							<input type="hidden" name="page" value="1"/>
							<input type="hidden" name="beginRow" value="0"/>					
							<input type="hidden" name="pageRows" value="0"/>
							
							<div class="pull-right" style="width:200px;margin-bottom: 3px;">
								<div class="input-group">
					            	<input type="text" name="query" value="${query }" class="form-control" placeholder="搜索..." />
					            	<span class="input-group-addon" onclick="$('#table_form').submit()"><i class="fa fa-search"></i></span>
					            </div>
				            </div>					
						</form>
                	</div>
                	<!-- /panel-header -->
                	<!-- panel-body -->
                	<div class="box-body">
                		<table id="table_list" class="table table-striped table-bordered table-condensed">
		                    <thead>
						        <tr>
						        	<th>操作</th>
								    <th><input type="checkbox" onclick="onCheck(this)"></th>
                                    <th>会员ID</th>
                                    <th>手机号码</th>
                                    <th>昵称</th>
                                    <th>真实姓名</th>
                                    <th>性别</th>
                                    <th>身份类型</th>
                                    <th>身份证号码</th>
                                    <th>认证状态</th>
                                    <th>钱包锁定</th>
									<th>操作</th>
						        </tr>
						    </thead>
						    <tbody>
						    	<c:forEach var="item" items="${list}">
						        <tr >
						            <td style="width:130px">
						            	<div class="btn-group btn-group-xs" style="margin: 0;">
										 <%-- <a class="btn btn-primary" href="admin/User/edit?id=${item.id}&state=1"><i class="am-icon-edit" style="margin-right:4px;"></i>修改</a> --%>
										 <a class="btn btn-info" href="admin/User/view?id=${item.id}"><i class="am-icon-edit"></i>查看</a>
										 <%-- <a class="btn btn-danger" href="admin/User/delete?id=${item.id}" onclick="return onDelete(${item.id})"><i class="am-icon-remove" style="margin-right:4px;"></i>删除</a> --%>
										</div>
						            </td>
						            <td style="width:30px"><input type="checkbox"></td>
                                    <td>${item.id}</td>
                                    <td>${item.phoneNum}</td>
                                    <td>${item.nickName}</td>
                                    <td>${item.realName}</td>
                                    <td>${item.gender==1?'男':item.gender==2?'女':'保密'}</td>
                                    <td>${item.idType}</td>
                                    <td>${item.idNum}</td>
                                    <td>${item.getEnumName('CerStatusEnum','certificationStatus') }</td>
                                    <td>${item.walletLock==1?'已锁定':'未锁定'}</td>
                                    <%-- <c:if test="${item.certificationStatus==0||item.certificationStatus==null}" >
                                    	<td>认证失败</td>
                                    </c:if>
                                    <c:if test="${item.certificationStatus==1}" >
                                    	<td>认证中</td>
                                    </c:if>
                                    <c:if test="${item.certificationStatus==2}" >
                                    	<td>已认证</td>
                                    </c:if> --%>
									<td style="width:80px">
						            	<div class="btn-group btn-group-xs" style="margin: 0;">
						            	 <c:if test="${item.certificationStatus!=2 }">
						            	 	<a class="btn btn-success" href="admin/User/confirm?id=${item.id}"><i class="am-icon-edit" style="margin-right:4px;">实名确认</i></a>
						            	 </c:if>
										 <%-- <a class="btn btn-primary" href="admin/Walletaddress/list?userId=${item.id }"><i class="am-icon-edit" style="margin-right:4px;"></i>钱包地址</a> --%>										 
										</div>
						            </td>
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
    <script type="text/javascript">
   		$(function(){ 	
   			initTablePaging("table_list","table_form",${page},${pageRows},${allRows});
   		});	
    
    </script>    
  </body>
</html>
