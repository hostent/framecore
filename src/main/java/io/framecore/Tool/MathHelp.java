package io.framecore.Tool;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;


public class MathHelp {
	
	
	public static void main(String[] args) {
		
	 
		for (int i = 0; i < 300; i++) {
			
			
			 Double d1 = getRandomDouble();
			 Double d2 = getRandomDouble();
			 
			 System.out.println(divided(d1,d2));
			    
			    
		}
	     
		
	}

	private static Double getRandomDouble() {
		double a=Math.random()*10;
	    DecimalFormat df = new DecimalFormat( "0.00" );
	    String str=df.format( a );
	    
	    return Double.valueOf(str);
	}
	
	/**
	 * +  : 加法
	 *
	 */
	public static Double plus(Double... par )
	{
		BigDecimal result = new BigDecimal(0);
		
		for (Double float1 : par) {
			
			BigDecimal item = new BigDecimal(Double.toString(float1));
			
			result = result.add(item);
			
		}
		
		return result.doubleValue();
		
	}
	
	/**
	 * -  : 减法
	 *
	 */
	public static Double subtract(Double first ,Double... par )
	{
		
		BigDecimal result = new BigDecimal(first.toString());
		
		for (Double float1 : par) {
			
			BigDecimal item = new BigDecimal(Double.toString(float1));
			
			result = result.subtract(item);
			
		}
		
		return result.doubleValue();
		
	}
	
	/**
	 * *  : 乘法
	 *
	 */
	public static Double multiplied (Double... par )
	{
		
		BigDecimal result = new BigDecimal(1);
		
		for (Double float1 : par) {
			
			BigDecimal item = new BigDecimal(Double.toString(float1));
			
			result = result.multiply(item);
			
		}
		
		return result.doubleValue();
		
	}
	
	/**
	 * //  : 除法
	 *
	 */
	public static Double divided (Double first ,Double... par )
	{
		 		
		BigDecimal result = new BigDecimal(first.toString());
		
		for (Double float1 : par) {
			
			BigDecimal item = new BigDecimal(Double.toString(float1));
			
			result = result.divide(item,4,RoundingMode.DOWN);
			
		}
		
		return result.doubleValue();
		
	}
	
 
	
}
