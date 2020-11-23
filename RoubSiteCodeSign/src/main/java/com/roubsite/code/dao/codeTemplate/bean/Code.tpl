package ${pack}.bean;

import com.roubsite.database.annotation.bean.KeyFields;

public class ${mode} {
<#list _allFields as value>
	<#if value.key == _keyField>
	@KeyFields
	</#if>
	private String ${value.key};
</#list>

<#list _allFields as value>
	public String get${value.key?cap_first}(){
		return this.${value.key};
	}
	
	public void set${value.key?cap_first}(String ${value.key}){
		this.${value.key}=${value.key};
	}
	
</#list>
}
