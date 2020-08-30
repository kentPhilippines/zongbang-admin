package alipay.manage.api.channel.util.shenfu;

import cn.hutool.core.util.ObjectUtil;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class payUtil {
    public static final String KEY = "dtfysghxiazAIGY2BNEWDPOIL51";
    public static final String APPID = "202006032221186176";
    public static final String URL = "http://api.shishengclub.com/gateway/bankgateway/pay";
    public static final String D_PAY_URL = "http://api.shishengclub.com/gateway/pay";
    public static final String KEY01 = "789okjhy789okjuy7890plkju890olkju789okjhy789ok";
    public static final String APPID01 = "202008272343214049";
    public static final String NOTIFY = "/shenFu02-notfiy";
    public static Map<String, String> ipMap = new HashMap();

    static {
        ipMap.put("47.52.243.5", "47.52.243.5");
        ipMap.put("47.91.232.22", "47.91.232.22");
        ipMap.put("47.57.115.27", "47.57.115.27");
        ipMap.put("47.244.12.113", "47.244.12.113");
    }

    public static String createParam(Map<String, Object> map) {
        try {
            if (map == null || map.isEmpty())
                return null;
            Object[] key = map.keySet().toArray();
            Arrays.sort(key);
            StringBuffer res = new StringBuffer(128);
            for (int i = 0; i < key.length; i++)
                if (ObjectUtil.isNotNull(map.get(key[i])))
                    res.append(key[i] + "=" + map.get(key[i]) + "&");
            String rStr = res.substring(0, res.length() - 1);
            return rStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String md5(String a) {
        String c = "";
        MessageDigest md5;
		   	String result="";
			try {
				md5 = MessageDigest.getInstance("md5");
				md5.update(a.getBytes("utf-8"));
				byte[] temp;
				temp=md5.digest(c.getBytes("utf-8"));
				for (int i=0; i<temp.length; i++)
					result+=Integer.toHexString((0x000000ff & temp[i]) | 0xffffff00).substring(6);
			} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			}
			return result;
	    }
	public static String createParam(HashMap<String, String> decodeParamMap) {
		try {
			if (decodeParamMap == null || decodeParamMap.isEmpty())
				return null;
			Object[] key = decodeParamMap.keySet().toArray();
			Arrays.sort(key);
			StringBuffer res = new StringBuffer(128);
			for (int i = 0; i < key.length; i++)
				if (ObjectUtil.isNotNull(decodeParamMap.get(key[i])))
					res.append(key[i] + "=" + decodeParamMap.get(key[i]) + "&");
			String rStr = res.substring(0, res.length() - 1);
			return rStr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
