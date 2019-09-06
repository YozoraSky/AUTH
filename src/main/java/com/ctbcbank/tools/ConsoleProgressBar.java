package com.ctbcbank.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleProgressBar {
	private static Logger logger = LoggerFactory.getLogger("consoleBar");
	private static String start = "% [                    ]";
	private static int max;
	private static int min;
	private static int precentDigit = 3;
	
	public static void init(int max) {
//		避免掉 min = currentLines * maxGrid/max;中 max當母不可為0的錯誤
		if(max==0)max=1;
		setMax(max);
		System.out.print("  0" + start);
	}
	
	public static void printConsoleProgressBar(int currentLines) {
		int maxGrid = start.subSequence(start.indexOf("[")+1, start.indexOf("]")).length();
		for(int i=0;i<start.length()+precentDigit;i++) {
			logger.info("\b");
		}
		min = currentLines * maxGrid/max;
		int precent = (int)(min * (100.0 / maxGrid));
		for(int i=0;i<3-String.valueOf(precent).length();i++)
			System.out.print(" ");
		logger.info(precent + "%");
		logger.info(" [");
		for(int i=0;i<min;i++) {
			logger.info("=");
		}
		if(min<maxGrid && min>0) {
			min += 1;
			logger.info(">");
		}
		for(int i=0;i<maxGrid-min;i++) {
			logger.info(" ");
		}
		logger.info("]");
	}
	
	public static void ok() {
		logger.info(" ok\n");
	}

	public static int getMax() {
		return max;
	}

	public static void setMax(int max) {
		ConsoleProgressBar.max = max;
	}

	public static int getMin() {
		return min;
	}

	public static void setMin(int min) {
		ConsoleProgressBar.min = min;
	}
}
