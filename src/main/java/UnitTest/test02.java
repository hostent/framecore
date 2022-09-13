package UnitTest;

public class test02 {
	
	public static void main(String[] args) {
		
		
		Double p=30000.3123d;
		
		
		Double iper=(3.65/365)/24;
		
		
		Double ihole=1d;
		
		for (int i = 0; i < 1; i++) {
			ihole=ihole*(1+iper);
		}
		
		Double F=p*ihole;  //复利，年
		
		System.out.println(F);     
		
		Double F1=p*iper*1+p;   //不复利 ,年
		
		System.out.println(F1);  
		
	}

}
