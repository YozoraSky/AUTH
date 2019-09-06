package com.ctbcbank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.ctbcbank.tools.ConsoleProgressBar;
import com.ctbcbank.tools.FTPTools;

@Component
public abstract class Auth {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private void cmd_EXE(String fileName, String directory, String deCompressKey) throws Exception {
		Runtime runtime = Runtime.getRuntime();
		Process process;
		String[] cmd = { directory + fileName + ".EXE", "-y", "-g" + deCompressKey, "-w" + directory + fileName };
		process = runtime.exec(cmd);
		new Thread() {
			@Override
			public void run() {
				try {
					BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(),"big5"));
					String line = null;
					while ((line = in.readLine()) != null) {
						logger.info(line);
					}
					in.close();
				} catch (IOException e) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					logger.error("cmd_EXE inputStream error msg : " + sw.toString());
				}
			}
		}.start();
		new Thread() {
			@Override
			public void run() {
				try {
					BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream(),"big5"));
					String line = null;
					while ((line = err.readLine()) != null) {
						logger.info(line);
					}
				} catch (IOException e) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					logger.error("cmd_EXE inputStream error msg : " + sw.toString());
				}
			}
		}.start();
		int i = process.waitFor();
		if(i==0)
			logger.info(fileName + ".exe excute success");
		else
			logger.info(fileName + ".exe excute fail : " + i);
	}

	public void execute(FTPTools ftp, String remotePath, String directory, String deCompressKey) throws Exception {
		File file = new File(directory);
		if(!file.exists())
			file.mkdirs();
		String fileName = getFileName();
		logger.info(ftp.login());
		ftp.downloadFile(remotePath, fileName + ".EXE", directory + fileName + ".EXE");
		File filepath = new File(directory + fileName + ".EXE");
		if (filepath.exists()) {
			logger.info(String.valueOf(filepath.setExecutable(true, false)));
			logger.info(String.valueOf(filepath.setWritable(true, false)));
			logger.info(String.valueOf(filepath.setReadable(true, false)));
		}
		logger.info(fileName + ".exe downLoad success");
		cmd_EXE(fileName, directory, deCompressKey);
//		取得檔案的行數
		File f = new File(directory + fileName);
		long fileLength = f.length();
		LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(f));
		lineNumberReader.skip(fileLength);
		ConsoleProgressBar.init(lineNumberReader.getLineNumber());
		lineNumberReader.close();
		
		FileInputStream fileInputStream = new FileInputStream(directory + fileName);
		InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "big5");
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		long time = System.currentTimeMillis();
		batchUpdate(bufferedReader);
		logger.info("batchUpdate time : " + (System.currentTimeMillis() - time));
		bufferedReader.close();
		ftp.logout();
	}
	
	public String getWeekDays(Date date) {
		String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
		Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
        	w = 0;
        return weekDays[w];
	}
	
	public abstract void batchUpdate(BufferedReader bufferedReader) throws Exception;

	public abstract String getFileName();
	
	public abstract void start();
}
