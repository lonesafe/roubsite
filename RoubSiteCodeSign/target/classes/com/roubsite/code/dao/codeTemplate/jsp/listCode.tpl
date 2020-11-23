<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8" />
<title>RoubSite后台管理</title>
<meta name="keywords" content="" />
<meta name="description" content="" />
<meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0">
<script src="{$__WEBPATH__}/static/common/js/roubSite.js"></script>
<script>
	RS.webPath = '{$__WEBPATH__}';
</script>
<script src="{$__WEBPATH__}/static/common/js/import.js"></script>
</head>
<body>
	<!--主要内容begin-->
	<script src="{$__WEBPATH__}/static/${groupName}/${_mode}/js/list${mode}.js"></script>
	<div class="layui-fluid">
		<div class="layui-card">
			<div class="layui-card-header">角色管理</div>
			<div class='layui-card-body'>
				<div class="layui-row layui-col-space15">
					<div class="layui-col-md12">
						<form id="searchForm" class="layui-form" onsubmit="return false;">
							<div class="layui-form-item">
							<#list _searchFields as value>
								<div class="layui-inline">
									<span class="layui-form-label">${value.name}</span>
									<div class="layui-input-inline">
										<input type="text" class="layui-input" name="${value.key}" id="${value.key}" placeholder="${value.name}">
									</div>
								</div>
							</#list>
								<div class="layui-inline">
									<button id="button" class="layui-btn layui-btn-primary" onclick="query()">查询</button>
								</div>
							</div>
						</form>
						<table id="table" lay-filter='table'>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div id="toolbar" class="col-xs-12">
		<div class="col-xs-12">
			<div class='pull-right'>
				<button onclick="add()" class="layui-btn">增加</button>
				<button onclick="del()" class="layui-btn layui-btn-warm">删除</button>
				<button onclick="edit()" class="layui-btn layui-btn-danger">修改</button>
			</div>
		</div>
	</div>
	<!--主要内容end-->
</body>
</html>

