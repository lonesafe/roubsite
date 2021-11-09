package com.roubsite.database.page.dialect;

import com.roubsite.database.page.Dialect;
import com.roubsite.database.page.parser.CountSqlParser;

public abstract class AbstractDialect implements Dialect {
    //处理SQL
    protected CountSqlParser countSqlParser = new CountSqlParser();

    @Override
    public String getCountSql(String sourceSql) {
        return countSqlParser.getSmartCountSql(sourceSql);
    }
}
