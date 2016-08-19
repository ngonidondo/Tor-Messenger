package onion;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class main {
	public static void main(String[] args) throws IOException{
		System.out.println(getIP("dfgf2sadsad1119.168.2.11111dasdasdasfg"));
	}
	
	public static String getIP(String message){
		Pattern pattern = Pattern.compile("(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");
		Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group();
        }
        return "0.0.0.0";
	}
}
