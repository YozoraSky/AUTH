package com.ctbcbank.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "auth")
@PropertySource(value = { "classpath:application.properties" })
public class AuthProperties {
	private String deCompressKey;
	private String host;
	private String username;
	private String password;
	
	public String getDeCompressKey() {
		return deCompressKey;
	}
	public void setDeCompressKey(String deCompressKey) {
		this.deCompressKey = deCompressKey;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
