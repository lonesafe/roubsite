function save() {
	var param = RS.getFormData('editForm');
	$.ajax({
			url : RS.webPath + "/${groupName}/${_mode}/submitUpdate",
			type : 'post',
			data : '__paramSet__=' + param,
			dataType : 'json',
			success : function(data) {
				if (data.status == 1) {
					parent.layer.alert('提交成功', function(index) {
						parent.layer.close(index);
						window.location.href = RS.webPath + "/${groupName}/${_mode}";
					});
				} else {
					parent.layer.alert('提交失败');
				}
			},
			error : function(data) {
				parent.layer.alert('系统错误');
			}
	});
}

$(function() {
	RS.loadForm('editForm', retInfo);
	layui.form.on('submit(submit)',function(){
		save();
		return false;
	});
});
