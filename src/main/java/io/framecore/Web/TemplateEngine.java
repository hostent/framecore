package io.framecore.Web;

import java.io.IOException;
import java.io.StringWriter;

import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;


import io.framecore.Frame.Log;
import io.framecore.Tool.PropertiesHelp;

public class TemplateEngine {

	public static String raw(String path, Map<String, Object> dict) {

		String encoding="";
		String pathSub="";
		String suffix="";
		try {
			encoding = PropertiesHelp.getApplicationConf("spring.velocity.charset");
		 
			pathSub = PropertiesHelp.getApplicationConf("spring.velocity.resource-loader-path")
					.replace("classpath:/", "");
		 
			suffix = PropertiesHelp.getApplicationConf("spring.velocity.suffix");
		} catch (IOException e) {

			Log.logError(e);
			
			return "模板引擎配置错误";
		}

		VelocityEngine ve = new VelocityEngine();
		
		ve.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH,PropertiesHelp.getRootPath()+"/");
		
		ve.setProperty("file.resource.loader.class", org.apache.velocity.runtime.resource.loader.FileResourceLoader.class.getName());

		ve.init();

		Template t = ve.getTemplate(pathSub + path + suffix, encoding);
		//Template t = ve.getTemplate(path + suffix, encoding);
		VelocityContext ctx = new VelocityContext();

		if(dict!=null)
		{
			for (String key : dict.keySet()) {

				ctx.put(key, dict.get(key));
			}

		}
		
		// ctx.put("name", "velocity");

		StringWriter sw = new StringWriter();

		t.merge(ctx, sw);

		return sw.toString();
	}

}
