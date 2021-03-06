<%@ page language="java" import="java.util.*,ww.common.WwSystem" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
  <head>
  	<base href="<%=WwSystem.getRoot(request)%>">
    <title>用户群关系</title>
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
					用户群关系
					<small>用户群关系面板</small>
				</h1>
			</section>
			<!-- /content Header -->
			<!-- Main content -->
			<section class="content">
				<!-- panel -->
              	<div class="box box-primary">
                	<!-- panel-header -->
                	<div class="box-header with-border">
				        <h3 class="box-title pull-left"  style="margin-right: 20px;margin-top: 8px">用户群关系管理</h3>
                  		<div class="btn-group btn-group-sm pull-left">
						  <a type="button" class="btn btn-success" href="admin/UserGroup/edit">新增</a>
						  <a type="button" class="btn btn-warning" href="admin/UserGroup/list">刷新</a>
						</div>
						<form id="table_form" action="admin/UserGroup/list" method="post">
							<input type="hidden" name="page" value="1"/>					
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
                                    <th></th>
                                    <th>用户ID</th>
                                    <th>群ID</th>
                                    <th>成员类型：0--普通成员，1--干事，2--组长</th>

						        </tr>
						    </thead>
						    <tbody>
						    	<c:forEach var="item" items="${list}">
						        <tr >
						            <td style="width:130px">
						            	<div class="btn-group btn-group-xs" style="margin: 0;">
										 <a class="btn btn-primary" href="javascript:onEdit(${item.id})"><i class="am-icon-edit" style="margin-right:4px;"></i>修改</a>
										 <a class="btn btn-info" href="javascript:onView(${item.id})"><i class="am-icon-edit"></i>查看</a>
										 <a class="btn btn-danger" href="javascript:onDelete(${item.id});"><i class="am-icon-remove" style="margin-right:4px;"></i>删除</a>
										</div>
						            </td>
						            <td style="width:30px"><input type="checkbox"></td>
                                    <td>${item.id}</td>
                                    <td>${item.userId}</td>
                                    <td>${item.groupId}</td>
                                    <td>${item.type}</td>

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
   			//初始化表格分页
   			initTablePaging("table_list","table_form",${page},${pageRows},${allRows});
   		});	
   		
   		function onNew(){   			
   			var url="admin/UserGroup/edit";		
   			window.location=url;
   		}
   		function onEdit(id){
   			var url="admin/UserGroup/edit?";
   			url+="id="+id;			
   			window.location=url;
   		}  
   		function onView(id){
   			var url="admin/UserGroup/view?";
   			url+="id="+id; 			
   			window.location=url;
   		}  
	    
	    //删除记录
	    function onDelete(id){	    	
	    	var result =confirm("你确定要删除吗?");
	    	if(result ==true){
	    		doAjaxDelete(id);	    		
	    	}
	    	return false;//取消a链接的跳转
	    }
	    //执行删除
	    function doAjaxDelete(id){
	    	$.ajax({
			    url:'admin/admin/UserGroup/delete',
			    type:'POST', //GET
			    async:true,    //或false,是否异步
			    data:{
			        id:id
			    },
			    timeout:30000,    //超时时间
			    dataType:'json',    //返回的数据格式：json/xml/html/script/jsonp/text
			    success:function(data,textStatus,jqXHR){
			    	if(data.code==0){ //成功
			    		alert(data.message);
			    		$("#table_form").submit();
			    	}else{//失败
			    		alert(data.message);
			    	}
			    },
			    error:function(xhr,textStatus){
			        alert("网络或配置异常！");
			        console.log(xhr);
			        console.log(textStatus);
			    }
			});
	    }
	    
	    function onCheck(me){
	    	var v=me.checked;
	    	if(v==true){
	    		$("#table_list input[type='checkbox']").prop("checked",'checked');
	    	}else{
	    		$("#table_list input[type='checkbox']").removeAttr("checked");
	    	}
	    }
    
    </script>    
  </body>
</html>
