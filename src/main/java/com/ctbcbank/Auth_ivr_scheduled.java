package com.ctbcbank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@PropertySource(value = { "classpath:application.properties" })
public class Auth_ivr_scheduled {
	@Autowired
	private Authow05 authow05;
	@Autowired
	private Authpq25 authpq25;
	@Autowired
	private Authpq26 authpq26;
	@Autowired
	private Authpq27 authpq27;
	@Autowired
	private Authpq28 authpq28;
	
	@Scheduled(cron="${auth.cron}")
	public void run() {
		authpq25.start();
		authpq26.start();
		authpq27.start();
		authpq28.start();
		authow05.start();
	}
}
