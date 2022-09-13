package io.framecore.Tool;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.framecore.Frame.Result;

 

public class ServletHelp {
	
	public static void outPutWithOutNull(HttpServletResponse httpServletResponse ,Object top) throws JsonProcessingException, IOException {
		
		Result result =Result.succeed(top);
		
		ObjectMapper map = JsonHelp.getJack();
		map.setSerializationInclusion(Include.NON_NULL);
			 		
		String json = map.writeValueAsString(result);
		
		httpServletResponse.setContentType("application/json;charset=UTF-8");

		try (PrintWriter out = httpServletResponse.getWriter()) {

			out.write(json);

		}

		httpServletResponse.flushBuffer();
	}

}
