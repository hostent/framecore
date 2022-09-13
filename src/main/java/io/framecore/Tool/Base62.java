package io.framecore.Tool;

public class Base62 {
	
	public static void main(String[] args) {
		
		System.out.println(encoding(211210172725L));
	}
	
	

	
    public static String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String encoding(long num) {
        if(num < 1)
            throw new IllegalArgumentException("num must be greater than 0.");

        StringBuilder sb = new StringBuilder();
        for (; num > 0; num /= 62) {
            sb.append(ALPHABET.charAt((int) (num % 62)));
        }

        return sb.toString();
    }

    public static long decoding(String str) {
        if(str==null || str.trim().length() == 0 ){
            throw new IllegalArgumentException("str must not be empty.");
        }

        long result = 0;
        for (int i = 0; i < str.length(); i++) {
            result += ALPHABET.indexOf(str.charAt(i)) * Math.pow(62, i);
        }

        return result;
    }

}
 