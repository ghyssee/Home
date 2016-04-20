package be.home.common.security;

public class Encrypt {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//System.out.println(encrypt(55333338383935|4265746131303730));
		//System.out.println(decrypt("Dbg|44434b45525346|4379726f32323036"));
		//55333338383935|416c70686131303430|
		//44434b45525346|5846445f32323036
		//434f5254565241|4d6f3134436f616e3137|
		//55333439353634|506f7374313236
		//System.out.println(decrypt("44323230366672"));
		//44574954574552|5665726f6e69633039
		//434f5254565241|4d6f3134436f616e3231
		System.out.println(decrypt("434f5254565241"));
		System.out.println(decrypt("4d6f3134436f616e3231"));

	}

	private static String encrypt(String unencrypted) {

		String encrypted = "";

		StringBuffer sb = new StringBuffer(unencrypted);

		for (int i = 0; i < sb.length(); i++) {
			char kar = sb.charAt(i);
			String karStr = Integer.toHexString(kar);
			if (karStr.length() < 2) {
				karStr = padLeft(karStr, 2, '0');
			}
			encrypted += karStr;
		}

		return encrypted;
	}

	private static String decrypt(String encrypted) {

		String txtInHex = encrypted;
		byte[] txtInByte = new byte[txtInHex.length() / 2];
		int j = 0;
		for (int i = 0; i < txtInHex.length(); i += 2) {
			txtInByte[j++] = Byte.parseByte(txtInHex.substring(i, i + 2), 16);
		}
		String txt = new String(txtInByte);

		return txt;
	}

	public static String padLeft(String input, int totalLength, char kar) {

		int currentLength = input.length();
		int appendLength = totalLength - currentLength;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < appendLength; i++) {
			sb.append(kar);
		}
		sb.append(input);

		return sb.toString();

	}

}
