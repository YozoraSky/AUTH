package com.ctbcbank;

import java.io.BufferedReader;
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
import com.ctbcbank.properties.Authow05Properties;
import com.ctbcbank.tools.ConsoleProgressBar;
import com.ctbcbank.tools.FTPTools;

@Component
public class Authow05 extends Auth {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	AuthProperties authProperties;
	@Autowired
	Authow05Properties authow05Properties;

	@Override
	public void checkForExecution(Date date) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String password = new String(Base64.getDecoder().decode(authProperties.getPassword()));
		FTPTools ftp = new FTPTools(authProperties.getHost(), authProperties.getUsername(), password, 21);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("SDate", sdf.format(date) + '%');
		List<Map<String, Object>> list = namedParameterJdbcTemplate
				.queryForList(authow05Properties.getCheckworkdaySql(), params);
		boolean done = false;
		for (Map<String, Object> map : list) {
//				Authow05屬於類別4
			if ((Short) map.get("FileType") == 4) {
				done = true;
//					mode = 1 當日須轉入, mode = 0 當日不須轉入
				if ((Short) map.get("Mode") == 1) {
					logger.info("工作日---開始執行Authow05授權批次");
					execute(ftp, authow05Properties.getRemotePath(), authow05Properties.getDownloadPath(),
							authow05Properties.getBackupPath(), authProperties.getDeCompressKey());
					break;
				} else {
					logger.info("休假日");
					break;
				}
			}
		}
//			若當天找不到類別4，則按照預設日去執行(星期一~星期五要執行，六日不用)
		if (!done) {
			done = true;
			if (!getWeekDays(date).equals("星期六") && !getWeekDays(date).equals("星期日")) {
				logger.info("未找到類別4，按照預設 (星期一~星期五) 日期執行Authow05授權批次!");
				execute(ftp, authow05Properties.getRemotePath(), authow05Properties.getDownloadPath(),
						authow05Properties.getBackupPath(), authProperties.getDeCompressKey());
			} else {
				logger.info("未找到類別4，且不屬於預設 (星期一~星期五) 日期");
			}
		}
	}

	@Override
	public void setFileName(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
		this.fileName = authow05Properties.getName() + sdf.format(date);
	}

	@Override
	public void batchUpdate(BufferedReader bufferedReader) throws Exception {
		List<MapSqlParameterSource> batchArgsForUpdate = new ArrayList<MapSqlParameterSource>();
		List<MapSqlParameterSource> batchArgsForInsert = new ArrayList<MapSqlParameterSource>();
		Map<String, Object> params = new HashMap<String, Object>();
		String line;
		int insertCount = 0;
		int updateCount = 0;
		int consoleBarTotalCount = 0;
		while ((line = bufferedReader.readLine()) != null) {
			byte[] temp = line.getBytes("big5");
//			商店類別代碼
			String rec_type = new String(Arrays.copyOfRange(temp, 0, 1), "big5");
//			merch_org + merch_nbr = 商店代碼
			String merch_org = new String(Arrays.copyOfRange(temp, 1, 4), "big5");
			String merch_nbr = new String(Arrays.copyOfRange(temp, 4, 14), "big5");
//			商店名稱
			String business_name = new String(Arrays.copyOfRange(temp, 14, 54), "big5");
//			POS機型1
			String pos_type_1 = new String(Arrays.copyOfRange(temp, 54, 59), "big5");
//			POS機型2
			String pos_type_2 = new String(Arrays.copyOfRange(temp, 59, 64), "big5");
//			EDC機型1
			String edc_type_1 = new String(Arrays.copyOfRange(temp, 64, 69), "big5");
//			EDC機型2
			String edc_type_2 = new String(Arrays.copyOfRange(temp, 69, 74), "big5");
//			統編末四碼
			String corp_no_4 = new String(Arrays.copyOfRange(temp, 74, 78), "big5");
//			商店狀態(是否解約)
			String status = new String(Arrays.copyOfRange(temp, 78, 79), "big5");
//			聯絡電話-區碼
			String contact_phone_area = new String(Arrays.copyOfRange(temp, 79, 82), "big5");
//			聯絡電話
			String contact_phone_no = new String(Arrays.copyOfRange(temp, 82, 90), "big5");
//			聯絡電話-分機
			String contact_phone_ext = new String(Arrays.copyOfRange(temp, 90, 95), "big5");
//			trvl_flag
			String trvl_flag = new String(Arrays.copyOfRange(temp, 95, 96), "big5");
//			主機異動日期
			String maint_date = new String(Arrays.copyOfRange(temp, 96, 104), "big5");
//			String chechSql = authProperties.getAuthow05CheckSql().replace("@MerchantNo", merch_org + merch_nbr);
			params.put("MerchantNo", merch_org + merch_nbr);
			List<Map<String, Object>> list = namedParameterJdbcTemplate.queryForList(authow05Properties.getCheckSql(),
					params);
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("StoreName", business_name);
			parameters.addValue("StoreNo", corp_no_4);
			parameters.addValue("Status", status);
			parameters.addValue("EDC1", edc_type_1);
			parameters.addValue("EDC2", edc_type_2);
			parameters.addValue("POS1", pos_type_1);
			parameters.addValue("POS2", pos_type_2);
			parameters.addValue("ContactPhoneArea", contact_phone_area);
			parameters.addValue("ContactPhone", contact_phone_no);
			parameters.addValue("ContactPhoneExt", contact_phone_ext);
			parameters.addValue("HostModifyDate", maint_date);
			parameters.addValue("TRVLFlag", trvl_flag);
			parameters.addValue("MerchantNo", merch_org + merch_nbr);
			if (!list.isEmpty()) {
				batchArgsForUpdate.add(parameters);
				while (batchArgsForUpdate.size() >= 1000) {
					namedParameterJdbcTemplate.batchUpdate(authow05Properties.getUpdateSql(),
							batchArgsForUpdate.toArray(new MapSqlParameterSource[0]));
					updateCount += batchArgsForUpdate.size();
					consoleBarTotalCount += updateCount;
					ConsoleProgressBar.printConsoleProgressBar(consoleBarTotalCount);
					batchArgsForUpdate.clear();
				}
			} else {
				batchArgsForInsert.add(parameters);
				while (batchArgsForInsert.size() >= 1000) {
					namedParameterJdbcTemplate.batchUpdate(authow05Properties.getInsertSql(),
							batchArgsForInsert.toArray(new MapSqlParameterSource[0]));
					insertCount += batchArgsForInsert.size();
					consoleBarTotalCount += insertCount;
					ConsoleProgressBar.printConsoleProgressBar(consoleBarTotalCount);
					batchArgsForInsert.clear();
				}
			}
		}
		if (!batchArgsForUpdate.isEmpty()) {
			namedParameterJdbcTemplate.batchUpdate(authow05Properties.getUpdateSql(),
					batchArgsForUpdate.toArray(new MapSqlParameterSource[0]));
			updateCount += batchArgsForUpdate.size();
			consoleBarTotalCount += updateCount;
			ConsoleProgressBar.printConsoleProgressBar(consoleBarTotalCount);
			batchArgsForUpdate.clear();
		}
		if (!batchArgsForInsert.isEmpty()) {
			namedParameterJdbcTemplate.batchUpdate(authow05Properties.getInsertSql(),
					batchArgsForInsert.toArray(new MapSqlParameterSource[0]));
			insertCount += batchArgsForInsert.size();
			consoleBarTotalCount += insertCount;
			ConsoleProgressBar.printConsoleProgressBar(consoleBarTotalCount);
			batchArgsForInsert.clear();
		}
		ConsoleProgressBar.ok();
		logger.info("Authow05 update count : {}", updateCount);
		logger.info("Authow05 insert count : {}", insertCount);
		params.clear();
		params.put("FilePath", getFileName());
		params.put("SuccessCount", updateCount + insertCount);
		params.put("FailCount", "0");
		namedParameterJdbcTemplate.update(authow05Properties.getStatusSql(), params);
	}
}
