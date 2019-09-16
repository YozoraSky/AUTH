package com.ctbcbank.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "auth.authpq26")
public class Authpq26Properties {
	private String remotePath;
	private String downloadPath;
	private String backupPath;
	private String insertSql;
	private String statusSql;
	private String checkworkdaySql;
	
	public String getRemotePath() {
		return remotePath;
	}
	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}
	public String getDownloadPath() {
		return downloadPath;
	}
	public void setDownloadPath(String downloadPath) {
		this.downloadPath = downloadPath;
	}
	public String getBackupPath() {
		return backupPath;
	}
	public void setBackupPath(String backupPath) {
		this.backupPath = backupPath;
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
