package com.ctbcbank;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

import com.ctbcbank.properties.AuthProperties;
import com.ctbcbank.properties.Authpq26Properties;
import com.ctbcbank.tools.ConsoleProgressBar;
import com.ctbcbank.tools.FTPTools;

@Component
public class Authpq26 extends Auth{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	AuthProperties authProperties;
	@Autowired
	Authpq26Properties authpq26Properties;
	
	@Override
	public void start() {
		long currentTime = System.currentTimeMillis();
		Date today = new Date(currentTime);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String password = new String(Base64.getDecoder().decode(authProperties.getPassword()));
		FTPTools ftp = new FTPTools(authProperties.getHost(), authProperties.getUsername(), password, 21);
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("SDate", sdf.format(today) + '%');
			List<Map<String,Object>> list = namedParameterJdbcTemplate.queryForList(authpq26Properties.getCheckworkdaySql(), params);
			boolean done = false;
			for(Map<String, Object> map : list) {
//				Authpq26屬於類別0
				if((Short)map.get("FileType") == 0) {
					done = true;
//					mode = 1 當日須轉入, mode = 0 當日不須轉入
					if((Short)map.get("Mode") == 1) {
						logger.info("工作日---Authpq26授權批次");
						execute(ftp, authpq26Properties.getRemotePath(), authpq26Properties.getDownloadPath(), authpq26Properties.getBackupPath(), authProperties.getDeCompressKey());
						break;
					}
					else {
						logger.info("休假日");
						break;
					}
				}
			}
//			若當天找不到類別0，則按照預設日去執行(星期一~星期五要執行，六日不用)
			if(!done) {
				done = true;
				if(!getWeekDays(today).equals("星期六") && !getWeekDays(today).equals("星期日")) {
					logger.info("未找到類別0，按照預設 (星期一~星期五) 日期執行Authpq26授權批次");
					execute(ftp, authpq26Properties.getRemotePath(), authpq26Properties.getDownloadPath(), authpq26Properties.getBackupPath(), authProperties.getDeCompressKey());
				}
				else {
					logger.info("未找到類別0，且不屬於預設 (星期一~星期五) 日期");
				}
			}
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			logger.error(sw.toString());
		}
	}
	
	@Override
	public void batchUpdate(BufferedReader bufferedReader) throws Exception {
		List<MapSqlParameterSource> batchArgsForInsert = new ArrayList<MapSqlParameterSource>();
		Map<String, Object> params = new HashMap<String, Object>();
		String line;
		int insertCount = 0;
		while ((line = bufferedReader.readLine()) != null) {
			byte[] temp = line.getBytes("big5");
//			帳單日
			String stmt_date = new String(Arrays.copyOfRange(temp, 0, 7), "big5");
//			特店代號
			String merch_nbr = new String(Arrays.copyOfRange(temp, 7, 16), "big5");
//			機器編號
			String edc_nbr = new String(Arrays.copyOfRange(temp, 16, 24), "big5");
//			批號
			String batch_nbr = new String(Arrays.copyOfRange(temp, 24, 36), "big5");
//			期數
			String inst_time = new String(Arrays.copyOfRange(temp, 36, 38), "big5");
//			原因碼
			String reason_code = new String(Arrays.copyOfRange(temp, 38, 41), "big5");
//			請款金額
			String amt = new String(Arrays.copyOfRange(temp, 41, 50), "big5");
//			正負號(請款金額)
			String amt_s = new String(Arrays.copyOfRange(temp, 50, 51), "big5");
//			請款手續費
			String dis = new String(Arrays.copyOfRange(temp, 51, 58), "big5");
//			正負號(請款手續費)
			String diss = new String(Arrays.copyOfRange(temp, 58, 59), "big5");
//			調整金額
			String adj_amt = new String(Arrays.copyOfRange(temp, 59, 67), "big5");
//			正負號(調整金額)
			String adj_amt_s = new String(Arrays.copyOfRange(temp, 67, 68), "big5");
//			調整手續費
			String adj_dis = new String(Arrays.copyOfRange(temp, 68, 75), "big5");
//			正負號(調整手續費)
			String adj_dis_s = new String(Arrays.copyOfRange(temp, 75, 76), "big5");
//			請款上傳日期(YYYMMDD)
			String settle_date = new String(Arrays.copyOfRange(temp, 76, 83), "big5");
//			請款筆數
			String settle_cnt = new String(Arrays.copyOfRange(temp, 83, 86), "big5");
//			子店代號
			String ori_merch = new String(Arrays.copyOfRange(temp, 86, 95), "big5");
//			銀行別
			String bank_nbr = new String(Arrays.copyOfRange(temp, 95, 98), "big5");
//			交易類別
			String tran_type = new String(Arrays.copyOfRange(temp, 98, 99), "big5");
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("STMTDate", stmt_date);
			parameters.addValue("MerchNBR", merch_nbr);
			parameters.addValue("EDCNBR", edc_nbr);
			parameters.addValue("BatchNBR", batch_nbr);
			parameters.addValue("INSTTime", inst_time);
			parameters.addValue("ReasonCode", reason_code);
			parameters.addValue("AMT", amt);
			parameters.addValue("AMTS", amt_s);
			parameters.addValue("DIS", dis);
			parameters.addValue("DISS", diss);
			parameters.addValue("ADJAMT", adj_amt);
			parameters.addValue("ADJAMTS", adj_amt_s);
			parameters.addValue("ADJDIS", adj_dis);
			parameters.addValue("ADJDISS", adj_dis_s);
			parameters.addValue("SettleDate", settle_date);
			parameters.addValue("SettleCNT", settle_cnt);
			parameters.addValue("ORIMerch", ori_merch);
			parameters.addValue("BankNBR", bank_nbr);
			parameters.addValue("TranType", tran_type);
			parameters.addValue("BatchFile", getFileName());
			batchArgsForInsert.add(parameters);
			while (batchArgsForInsert.size() >= 1000) {
				namedParameterJdbcTemplate.batchUpdate(authpq26Properties.getInsertSql(),batchArgsForInsert.toArray(new MapSqlParameterSource[0]));
				insertCount += batchArgsForInsert.size();
				ConsoleProgressBar.printConsoleProgressBar(insertCount);
				batchArgsForInsert.clear();
			}
		}
		if(!batchArgsForInsert.isEmpty()) {
			namedParameterJdbcTemplate.batchUpdate(authpq26Properties.getInsertSql(), batchArgsForInsert.toArray(new MapSqlParameterSource[0]));
			insertCount += batchArgsForInsert.size();
			ConsoleProgressBar.printConsoleProgressBar(insertCount);
			batchArgsForInsert.clear();
		}
		ConsoleProgressBar.ok();
		logger.info("Authpq26 insert count : " + insertCount);
		params.put("FilePath", getFileName());
		params.put("SuccessCount", insertCount);
		params.put("FailCount", "0");
		namedParameterJdbcTemplate.update(authpq26Properties.getStatusSql(), params);
	}
	
	@Override
	public String getFileName() {
		long time = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
		String fileName = "PQ26" + sdf.format(new Date(time));
		return fileName;
	}
}
