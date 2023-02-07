package ${pack}.bean;

import com.roubsite.database.annotation.bean.KeyFields;

public class ${mode} {
<#list _allFields as value>
	<#if value.key == _keyField>
	@KeyFields
	</#if>
	private ${value.type} ${value.key};
</#list>

<#list _allFields as value>
	public ${value.type} get${value.key?cap_first}(){
		return this.${value.key};
	}
	
	public void set${value.key?cap_first}(${value.type} ${value.key}){
		this.${value.key}=${value.key};
	}
	
</#list>
}
