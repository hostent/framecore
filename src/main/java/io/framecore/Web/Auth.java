package io.framecore.Web;


import io.framecore.Tool.Md5Help;
import io.framecore.Tool.PropertiesHelp;

public class Auth {

	private String key;

	private String secret;

	public Auth(String key) {
		this.setKey(key);
		this.setSecret(PropertiesHelp.getAppConf("api.secret." + key));
	}

 
  
	public boolean checkAuth(String sign, String json) {
		if (secret == null || secret.isEmpty()) {
			// 没有配置，就是不校验
			return true;
		}

		String calcSign = Md5Help.toMD5(key + secret + json.trim());

		if (!calcSign.toLowerCase().equals(sign.toLowerCase())) {
			return false;
		}

		return true;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
}
