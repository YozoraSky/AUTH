package com.ctbcbank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ctbcbank.Authow05;
import com.ctbcbank.Authpq25;


@Component
public class Auth_ivr implements CommandLineRunner {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
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

	public void run(String... args) {
		if (args.length != 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("--help")) {
					logger.info("-f fileType");
					logger.info("fileType=0>>PQ26");
					logger.info("fileType=1>>PQ25");
					logger.info("fileType=2>>PQ28");
					logger.info("fileType=3>>PQ27");
					logger.info("fileType=4>>OW05");
					logger.info("e.g. xxx.jar -f 4");
					break;
				}
				if (args[i].equals("-f")) {
					switch (Integer.parseInt(args[i + 1])) {
						case 0:authpq26.start();break;
						case 1:authpq25.start();break;
						case 2:authpq28.start();break;
						case 3:authpq27.start();break;
						case 4:authow05.start();break;
					}
				}
			}
		}
	}
}