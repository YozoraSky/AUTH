package com.ctbcbank.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "auth.authpq26")
@PropertySource(value = { "classpath:authpq26.properties" })
public class Authpq26Properties {
	private String remotePath;
	private String localPath;
	private String insertSql;
	private String statusSql;
	private String checkworkdaySql;
	
	public String getRemotePath() {
		return remotePath;
	}
	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}
	public String getLocalPath() {
		return localPath;
	}
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
	public String getInsertSql() {
		return insertSql;
	}
	public void setInsertSql(String insertSql) {
		this.insertSql = insertSql;
	}
	public String getStatusSql() {
		return statusSql;
	}
	public void setStatusSql(String statusSql) {
		this.statusSql = statusSql;
	}
	public String getCheckworkdaySql() {
		return checkworkdaySql;
	}
	public void setCheckworkdaySql(String checkworkdaySql) {
		this.checkworkdaySql = checkworkdaySql;
	}
}
