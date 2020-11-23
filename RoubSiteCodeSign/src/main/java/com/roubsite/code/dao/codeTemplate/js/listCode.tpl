var table;
$(function() {
	table = dataTable.init('table', {
			url: RS.webPath + '/${groupName}/${_mode}/query',
			toolbar: '#toolbar',
			cols: [ [ {
					checkbox : true,
					fixed : true
				},<#list _allFields as value>
				{
					field : '${value.key}',
					title : '${value.name}',
					fixed : 'left'
				},</#list>  ] ]
	});
});
function query() {
	table.search({
	<#list _searchFields as value>
		${value.key} : $('#${value.key}').val(),
	</#list>
	});
}
function add() {
	window.location.href = RS.webPath + '/${groupName}/${_mode}/insert';
}
function del() {
	parent.layer.confirm("是否要删除选中的项目？", {
			icon : 3,
			title : '提示',
	}, function(index) {
		var selections = table.getAllSelected();
		if (selections.length < 1) {
			parent.layer.alert("您未选择任何一条记录");
			return;
		}
		var ids = [];
		for (var i = 0; i < selections.length; i++) {
			ids[i] = selections[i].${_keyField}
		}
		$.ajax({
			url:RS.webPath + '/${groupName}/${_mode}/del',
			type : 'post',
			dataType : 'json',
			data: '__paramSet__=' + JSON.stringify(ids),
			success:function(ret){
				if(ret.status==0){
					parent.layer.alert('删除失败');
				}else{
					parent.layer.alert('删除成功');
				}
				query();
			},error:function(){
				parent.layer.alert('删除失败');
				query();
			}
		});
	});
}
function edit() {
	var selections = table.getAllSelected();
	if (selections.length  != 1) {
		parent.layer.alert("只能选择一个项目进行编辑");
		return;
	}
	var id = selections[0].${_keyField};
	window.location.href = RS.webPath + '/${groupName}/${_mode}/update?id=' + id;
}