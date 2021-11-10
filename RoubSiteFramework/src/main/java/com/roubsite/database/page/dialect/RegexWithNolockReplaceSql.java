
package com.roubsite.database.page.dialect;

/**
 * 正则处理 with(nolock)，转换为一个 table_PAGEWITHNOLOCK
 */
public class RegexWithNolockReplaceSql implements ReplaceSql {

	// with(nolock)
	protected String WITHNOLOCK = ", PAGEWITHNOLOCK";

	@Override
	public String replace(String sql) {
		return sql.replaceAll("((?i)\\s*(\\w+)\\s*with\\s*\\(\\s*nolock\\s*\\))", " $2_PAGEWITHNOLOCK");
	}

	@Override
	public String restore(String sql) {
		return sql.replaceAll("\\s*(\\w*?)_PAGEWITHNOLOCK", " $1 WITH(NOLOCK)");
	}
}
