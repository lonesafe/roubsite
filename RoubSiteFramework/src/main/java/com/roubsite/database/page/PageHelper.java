package com.roubsite.database.page;

import com.roubsite.database.page.dialect.AbstractDialect;

public class PageHelper extends AbstractDialect {

	public static void main(String[] args) {
		System.out.println(new PageHelper().getCountSql(
				"SELECT WEBSITE_ID, DOMAIN, ORIGIN_TYPE, IS_GZIP, IS_STATIC_CACHE, CACHE_TIME, IS_SSL, IS_REWRITE_TO_SSL, CERT_PATH, KEY_PATH, USER_ID, STATUS, LAST_UPDATE_TIME, "
						+ "( SELECT GROUP_CONCAT( CONCAT( IF ( ORIGIN_PORT = \"443\", \"https://\", \"http://\" ), ORIGIN_IP, \":\", ORIGIN_PORT ) ) FROM CDN_DOMAIN_ORIGIN "
						+ "WHERE CDN_DOMAIN_ORIGIN.WEBSITE_ID = CDN_DOMAIN.WEBSITE_ID ) ORIGIN FROM CDN_DOMAIN WHERE USER_ID = ?"));
	}
}
