package com.ctbcbank;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ctbcbank.Authow05;
import com.ctbcbank.Authpq25;

@Component
public class Auth_ivr implements CommandLineRunner {
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

	public void run(String... args) throws ParseException {
		String dateFormat = "((^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(10|12|0?[13578])([-\\/\\._])"
				+ "(3[01]|[12][0-9]|0?[1-9])$)|(^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])"
				+ "(11|0?[469])([-\\/\\._])(30|[12][0-9]|0?[1-9])$)|(^((1[8-9]\\d{2})|([2-9]\\d{3}))"
				+ "([-\\/\\._])(0?2)([-\\/\\._])(2[0-8]|1[0-9]|0?[1-9])$)|(^([2468][048]00)([-\\/\\._])"
				+ "(0?2)([-\\/\\._])(29)$)|(^([3579][26]00)([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([1][89][0][48])"
				+ "([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([2-9][0-9][0][48])([-\\/\\._])(0?2)([-\\/\\._])(29)$)"
				+ "|(^([1][89][2468][048])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([2-9][0-9][2468][048])([-\\/\\._])"
				+ "(0?2)([-\\/\\._])(29)$)|(^([1][89][13579][26])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|"
				+ "(^([2-9][0-9][13579][26])([-\\/\\._])(0?2)([-\\/\\._])(29)$))";
		String date = StringUtils.EMPTY;

		Options options = new Options();
		options.addOption("h", "help", false, "Print this usage information");
		options.addOption("d", "date", true, "detailLog start time");
		options.addOption("f", "fileType", true, "fileType");

		CommandLineParser parser = new DefaultParser();
		CommandLine commandLine = parser.parse(options, args);

		if (args.length != 0) {
			if (commandLine.hasOption("h")) {
				System.out.println("說明:" + options.getOption("h").getDescription());
				System.out.println("選項:");
				System.out.println("  -d date 指定日期(須符合YYYY/MM/DD的格式)");
				System.out.println("  -f 指定欲直行的授權批次檔案類型");
				System.out.println("  -f 0  執行authpq26");
				System.out.println("  -f 1  執行authpq25");
				System.out.println("  -f 2  執行authpq28");
				System.out.println("  -f 3  執行authpq27");
				System.out.println("  -f 4  執行authow05");
				System.out.println("範例:");
				System.out.println("  e.g. xxx.jar -d date -f fileType 依照指定日期(date)執行指定檔案");
				System.out.println("  e.g. xxx.jar -d -f all 依照指定日期(date)執行所有檔案");
				System.exit(0);
			}
			if (commandLine.hasOption("d")) {
				date = commandLine.getOptionValue("d");
				if (date.matches(dateFormat)) {
					date = date.replaceAll("[-\\/\\._]", "-");
				} else {
					System.out.println("Date format is invaild!");
					System.out.println("Input -h to get help");
					System.exit(0);
				}
			}
			if (commandLine.hasOption("f")) {
				if (!date.equals(StringUtils.EMPTY)) {
					String fileType = commandLine.getOptionValue("f");
					switch (fileType) {
					case "1":
						authpq25.start(date);
						break;
					case "0":
						authpq26.start(date);
						break;
					case "3":
						authpq27.start(date);
						break;
					case "2":
						authpq28.start(date);
						break;
					case "4":
						authow05.start(date);
						break;
					case "all":
						authpq25.start(date);
						authpq26.start(date);
						authpq27.start(date);
						authpq28.start(date);
						authow05.start(date);
						break;
					default:
						System.out.println("FileType is not available!");
						System.out.println("Input -h to get help");
					}
					System.exit(0);
				} else {
					System.out.println("Date is empty!");
					System.out.println("Input -h to get help");
					System.exit(0);
				}
			}
			else {
				System.out.println("FileType is empty!");
				System.out.println("Input -h to get help");
				System.exit(0);
			}
		}
	}
}