<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8" />
<title>RoubSite后台管理</title>
<meta name="keywords" content="" />
<meta name="description" content="" />
<meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0">
<script src="{$__WEBPATH__}/static/common/js/roubSite.js"></script>
<script>RS.webPath = '{$__WEBPATH__}';</script>
<script src="{$__WEBPATH__}/static/common/js/import.js"></script>
</head>
<body>
<!--主要内容begin-->
<script>
var retInfo={$retInfo};
</script>
<script src="{$__WEBPATH__}/static/${groupName}/${_mode}/js/update${mode}.js"></script>
	<div class="layui-fluid">
		<div class="layui-card">
			<div class="layui-card-header">修改</div>
			<div class='layui-card-body'>
				<form class="layui-form" id='editForm' onsubmit="return false;">
				<#list _allFields as value>
					<#if value_index%2 == 0>
					<div class="layui-row layui-col-space10 layui-form-item">
					</#if>
						<div class="layui-col-lg6">
							<label for="${value.key}" class="layui-form-label">${value.name}</label>
							<div class="layui-input-block">
								<input type="text" class="layui-input" id="${value.key}" name="${value.key}" placeholder="${value.name}">
							</div>
						</div>
					<#if (value_index%2!=0 || !value_has_next)>
					</div>
					</#if>
				</#list>
					<div class="layui-form-item">
						<div class="layui-input-block">
							<button type="submit" class="layui-btn" lay-submit lay-filter="submit">提交</button>
							<button type="submit" class="layui-btn layui-btn-danger" onclick="history.back();">返回</button>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
<!--主要内容end-->
</body>
</html>
