package com.veewap.util;

import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TCUtil {

	private static final String SMSUrl = "http://gw.api.taobao.com/router/rest";
	private static final String AppKey = "23431001";
	private static final String Secret = "98e14940c8fd01bbee6ad029f3dd6d85";

	static String path = "";


	private TCUtil() {
	}


	public static Map<String, Object> mapToHashMap(Map<String, String[]> reqMap) {
		Map<String, Object> tempMap = new HashMap<String, Object>();
		Set<Entry<String, String[]>> set = reqMap.entrySet();  
        Iterator<Entry<String, String[]>> it = set.iterator();  
        while (it.hasNext()) {  
            Entry<String, String[]> entry = it.next();  
//            System.out.println("KEY:"+entry.getKey());  
            for (String str : entry.getValue()) {  
//                System.out.println(str);  
                tempMap.put(entry.getKey(), str);
            }  
        } 
		return tempMap;
	}


	public static String getDefaultPath() {
		return path;
	}

	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern.compile("^(1[0-9][0-9])\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	public static String getNowTimeString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(new Date());
	}

	public static String TaobaoClientSMS(String phone, String signName, String paramStr, String templateCode) {
		TaobaoClient client = new DefaultTaobaoClient(SMSUrl, AppKey, Secret);
		AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
		// req.setExtend("123456");
		req.setSmsType("normal");
		req.setSmsFreeSignName(signName);
		req.setSmsParamString(paramStr);
		req.setRecNum(phone);
		req.setSmsTemplateCode(templateCode);
		try {
			AlibabaAliqinFcSmsNumSendResponse rsp = client.execute(req);
			rsp.getMsg();
			return null;
		} catch (ApiException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	public static String getVMCity(String cityName) {
		if (cityName.indexOf("未设定") == 0) {
			return "";
		}
		cityName = cityName.replaceAll(" ", "").replaceAll("　", "");
		if (cityName.contains("|未设定|")) { // 中间市名称部分未设定，取省名代替
			cityName = cityName.substring(0, cityName.indexOf("|"));
		}
		if (cityName.contains("|")) { // 有间隔表示按照 省|市|区 的方式排列，取中间数据
			cityName = cityName.substring(cityName.indexOf("|") + 1);
			if (cityName.contains("|"))
				cityName = cityName.substring(0, cityName.indexOf("|"));
		}
		if (cityName.contains("市")) {
			cityName = cityName.substring(0, cityName.indexOf("市"));
		}
		return cityName;
	}

	public static String getOverdueDateString(int day) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		long time = new Date().getTime() - day * 24 * 3600 * 1000;
		return dateFormat.format(new Date(time));
	}

}
