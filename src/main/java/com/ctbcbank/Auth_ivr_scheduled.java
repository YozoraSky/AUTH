package com.ctbcbank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
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
	
	@Scheduled(cron="${auth.authow05.cron}")
	public void runAuthow05() {
		authow05.start();
	}
	
	@Scheduled(cron="${auth.authpq25.cron}")
	public void runAuthpq25() {
		authpq25.start();
	}
	
	@Scheduled(cron="${auth.authpq26.cron}")
	public void runAuthpq26() {
		authpq26.start();
	}
	
	@Scheduled(cron="${auth.authpq27.cron}")
	public void runAuthpq27() {
		authpq27.start();
	}
	
	@Scheduled(cron="${auth.authpq28.cron}")
	public void runAuthpq28() {
		authpq28.start();
	}
}
