package test.number;

import alipay.manage.util.HttpUtils;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class IP {
  public static void main(String[] args) throws UnknownHostException {
      String url = "https://pay5.sg123.ws/return/160000186154147387";
      Map<String, Object> map = new HashMap<>();
      map.put("2", "adsdsa");
      map.put("3", "adsdsa");
      map.put("4", "adsdsa");
      map.put("25", "adsdsa");
      String s = HttpUtils.postHttps(url, map);
      System.out.println("响应：" + s);




	 /*String ww = "ExSuW32d8a5yTG1OoWCiwgl_p_B9e2pe-r9mqwErk-aq5LjKiF591KOXwY3eCIKLvwjovOl3Y9WLcb5i_QuvZL63k0FbK8micWjZUJ3Pfgbj3W35a07pJsT8ugnzcGy4sJXqzlGZDUnjawNLCktuQlLOjX3N_6hqoxxgsiKzXRoXg06nHg1zAYcOP599y0KUQL4Y-XXxg2A21AFJq5fZGKrDdUp-Bkrctq-9uyeh65JyqU0_uMZ8ZSlWeHwgNoHMLywKq1MCxyzDz3iUIQJ6HM5TRPHH4lpV5KaOXOSukrxI7F3r7mKVLsfwy6InONWK5rKQoKQYzyItcgSs7Jpllyb4lD2mubq4Z051a0UzPy2JGknPYWVRpQmeXdtUv6RRa14T1XjMN_WDDP4LIKINLwMsH_Td00_SFghmHDsqG_UI-ZivSe6_Azk0de9myAo8LgpY-meOqM2l07q3mkHRkYBbnkqL2FticOfr-2l6CQDN7bfKreKhTuepmrEvSiiU";
	 String key = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJDG1RcVgQ8E56SdxGGJV8h5mfh/SyeMCH6M2Qzj13UMoCZ6eduPewC+IvXRbJJGhKN0Tuv8eVCEVOsKl8LG5+GsZiWzCLJ1kbP5Nm71cd88Zl/tZuqlQtWh9JSVmSPXPJH6+B1jOQjpKght7/POgo1bL/vgqUFq/6gViRNDAwbZAgMBAAECgYATwp+5C91WoHPBeysfqPBLU5WZXj5ywmEybKT/ZmW0+ykst1DEj/vY6Rq6dIAeIvPdn3VaLlacj8NLZWG7WwyxAv9GCb9PwcjUAVJRcr+0dD9GRaRoaibs/JxfB6gDYsRCgzpxEyYS11XH0aPvXKGVeq8bvQ7WNJiGSnFJAsF0gQJBAM0XsXHUY+Ty9o+0Us/uhHTD5FlP7I2m7DarqfILexXGPfZ1Vk2hZNI+d6yNH0G8FTdWKL0+9NN/ylj/+wPExRECQQC0tnSu8xGpBABKj71FgVEbnJKBRHeQt2HkIlHA6mJ5EMAKqlIoTh5A+QpuzDTlH+4hQiFSVaDkWaJLh6aZNYVJAkAqYoMhaytZ3XxnJnUW08w79V/ztTjF7YFTqxZsF0NQtK+Jh6ZWVMxpRs+ksWWMSDp6PSweMCyYqQhAbGaL2NaBAkANWqpWv1mfvs7ys5qOB1diDfj5moCnbhXK0QpvkiKCxc0bPKjjl5o+vOibdXW3NNvZE1T5C6DfHwySPkz+Z2kZAkBGDy+1F2nfHogfZs+waFlpaMUFc24Fa6rNpLR3AV1ty2mlzk9Mcq+WaaXWhreom81h43G70WHViEBGX5aWcUfk";
	 String kke = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAI/IERFGosb9qdT7SfvSv+avAcdoEaAJsGdnB1Dfa1Bt8fLKz5h8CoyvEXe8GuJu5WFg6REOWOalUB+Su5HMB9WEgNeJTzXRxUVSEGdtDjngmxeM1Ol7ogl2LDqI/VEHyhV7TJdrOdeih8R09kAopxi2gPYt5FGbUgvNl5wBlM7ZAgMBAAECgYBQALCOflFAjBCds509A81HnIVO4Tc9Z2bCgm9troNV43dG81aDsDFmilsb1ozXBYGYp4XHucX5wG5OwySfQXnxlKltZkdGkoQ5A8wZFZpc3sIlmq3bdh4lZv8KoliMSXvOggd/KDOhe6eTRAs4GS2lPkEaUzmIfvTrjptX7MIfCQJBAP4IuTiMiCSGQ6H/91m6IqC6TY13IaKWPFah+MqwmrAwX6732HiyW1eA0PYY30EOe2/GdkgkjTHaRZrBVfpApCsCQQCQ5OrqVJhZyLDXsVJIoUgzZyr7u88JZ30rMMeKEyfqVje3QZmzOq+zRS+ly6woqVj/ht5jNz1W04LV8Ftlw8MLAkBtmW/oEOGP+IlndFrQUHkuJRLejN5rGixQKDOBp24lw8kUYQP5FuJHv2DoPFTd6ZSh5dsURwjP0F8lZHJmtMrfAkABHUGRO8VQK3LwtbRkSpk7i/eQqAkXkWwtANsH/M25x5/2ENMVxcXEIywSolZYmbTNgkQDj1Excb731jldtvuxAkAxf61vjwenZFsimMdq3HzJzaUb4biNhqfts4+CpiZMr1FLPuH6oBMV5YAt91avYPNbb1sz75244AAdzcJXFBDg";



	 Map<String, Object> paramMap = RSAUtils.getDecodePrivateKey(ww, key);
	 System.out.println(paramMap.toString());*/
	 /* Integer money_order = new BigDecimal("2900.00").intValue();
	  System.out.println(money_order.toString());*/
  }
}
