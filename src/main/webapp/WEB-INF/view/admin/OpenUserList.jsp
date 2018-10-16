<%@ page language="java" import="java.util.*,ww.common.WwSystem" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
  <head>
  	<base href="<%=WwSystem.getRoot(request)%>">
    <title>开放平台用户</title>
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
				        <h3 class="box-title pull-left"  style="margin-right: 20px;margin-top: 8px">开放平台用户管理</h3>
                  		<div class="btn-group btn-group-sm pull-left">
						  <a type="button" class="btn btn-success" href="admin/OpenUser/edit">新增</a>
						  <a type="button" class="btn btn-warning" href="admin/OpenUser/list">刷新</a>
						</div>
						<form id="table_form" action="admin/OpenUser/list" method="post">
							<input type="hidden" name="page" value="1"/>					
							<input type="hidden" name="pageRows" value="0"/>
							
							<div class="pull-right" style="width:200px;margin-bottom: 3px;">
								<div class="input-group">
									<select id="status" name="status" class="form-control" >
									  	<option value="0">未提交</option>
									  	<option value="1">已注册</option>						  						  	
									  	<option value="2">已实名</option>
									  	<option value="-1">注册失败</option>	
									  	<option value="-2">实名失败</option>
									</select>	
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
                                    <th>ID</th>
                                    <th>手机号</th>
                                    <!-- <th>用户类型</th> -->
                                    <th>公司名</th>
                                    <!-- <th>证件号码(身份证号或运业执照号码)</th> -->
                                    <th>证件号码</th>
                                    <th>证件照片</th>
                                    <!-- <th>证件照片2</th> -->
                                    <th>联系人</th>
                                    <th>联系人电话</th>
                                    <!-- <th>状态(0-未提交 1-已注册 2-已实名 -1-注册失败 -2-实名失败)</th> -->
                                    <th>状态</th>
									<th>后台处理</th>
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
                                    <td>${item.phoneNum}</td>
                                    <%-- <td>${item.type}</td> --%>
                                    <td>${item.realName}</td>
                                    <td>${item.idNum}</td>
                                    <td>
                                    	<img src="${imgUrl}${item.photo1}" alt="证件照片" height="150" width="150"/>
                                    </td>
                                    <%-- <td>${item.photo2}</td> --%>
                                    <td>${item.linkMan}</td>
                                    <td>${item.linkPhone}</td>
                                    <c:if test="${item.status==0}">
                                    	<td>未提交</td>
                                    </c:if>
                                    <c:if test="${item.status==1}">
                                    	<td>已注册</td>
                                    </c:if>
                                    <c:if test="${item.status==2}">
                                    	<td>已实名</td>
                                    </c:if>
                                    <c:if test="${item.status==-1}">
                                    	<td>注册失败</td>
                                    </c:if>
                                    <c:if test="${item.status==-2}">
                                    	<td>实名失败</td>
                                    </c:if>
                                    <%-- <c:if test="${item.status==-3}">
                                    	<td>其他问题</td>
                                    </c:if> --%>
                                    <td style="width:150px">
                                    	<div class="btn-group btn-group-xs" style="margin: 0;">
	                                    	<c:if test="${item.status==0 }">
												<a class="btn btn-primary" href="#" onclick="check(${item.id},2);return false;"><i class="am-icon-edit" style="margin-right:4px;"></i>审核通过</a>
												<a class="btn btn-danger" href="#" onclick="check(${item.id},-2);return false;"><i class="am-icon-remove" style="margin-right:4px;"></i>审核失败</a>
											</c:if>
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
    	
    	function check(id,status){
   			var url="admin/OpenUser/check"
   			var a=confirm("你确定执行审核吗？");
	   		if(a==true)
	   		{
	   			$.ajax({
	                type: "POST",
	                url: url,
	                data: {
	                	id:id, 
	                	pass:status
	                },
	                dataType: "json",
	                success: function(data){
	                	if(data.success){                		
	                		$('#table_form').submit(); //刷新
	                		alert("完成审核");
	                	}else{
	                		alert(data.msg);
	                	}
	                },
	                error : function(res) {  
	                	// view("异常！");  
	                	alert(JSON.stringify(res));  
	                }  
	            });
	   		}
   		}
   		
   		$(function(){ 	
   			//初始化表格分页
   			initTablePaging("table_list","table_form",${page},${pageRows},${allRows});
   			$("#status").val(${status});
   			
   		    //模态窗口
   			$('#myModal').modal('hide');
   		});	
   		
   		function onNew(){   			
   			var url="admin/OpenUser/edit";		
   			window.location=url;
   		}
   		function onEdit(id){
   			var url="admin/OpenUser/edit?";
   			url+="id="+id;			
   			window.location=url;
   		}  
   		function onView(id){
   			var url="admin/OpenUser/view?";
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
			    url:'admin/admin/OpenUser/delete',
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
