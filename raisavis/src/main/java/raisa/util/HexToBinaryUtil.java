package raisa.util;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

/**
 * Convert ascii hex-file to binary file (e.g. hex jpeg to binary jpeg)
 */
public class HexToBinaryUtil {

	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		FileInputStream fis = new FileInputStream("testi.txt");
		BufferedReader buf = new BufferedReader(new InputStreamReader(fis, US_ASCII));
		String line = buf.readLine();
		StringBuffer picHex = new StringBuffer();
		while (line != null) {
			picHex.append(line);
			line = buf.readLine();
		}
		FileOutputStream fow = new FileOutputStream("testi.jpg");
		fow.write(hexStringToByteArray(picHex.toString()));
		fow.close();
		buf.close();
	}

}
