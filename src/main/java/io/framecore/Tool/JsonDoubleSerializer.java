package io.framecore.Tool;

import java.io.IOException;
import java.math.BigDecimal;
 

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class JsonDoubleSerializer extends JsonSerializer<Double> {

	@Override
	public void serialize(Double value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		if(value==null)
		{
			gen.writeNull();
		}
		
		BigDecimal b = new BigDecimal(String.valueOf(value)).setScale(2, BigDecimal.ROUND_DOWN);
				 
        gen.writeNumber(b);	
	}

}