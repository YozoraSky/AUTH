package com.ctbcbank.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "auth.authow05")
@PropertySource(value = { "classpath:authow05.properties" })
public class Authow05Properties{
	private String remotePath;
	private String localPath;
	private String checkSql;
	private String checkworkdaySql;
	private String updateSql;
	private String insertSql;
	private String statusSql;
	
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
	public String getCheckSql() {
		return checkSql;
	}
	public void setCheckSql(String checkSql) {
		this.checkSql = checkSql;
	}
	public String getCheckworkdaySql() {
		return checkworkdaySql;
	}
	public void setCheckworkdaySql(String checkworkdaySql) {
		this.checkworkdaySql = checkworkdaySql;
	}
	public String getUpdateSql() {
		return updateSql;
	}
	public void setUpdateSql(String updateSql) {
		this.updateSql = updateSql;
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
}
