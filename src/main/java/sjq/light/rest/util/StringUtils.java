package sjq.light.rest.util;

public class StringUtils {
	/**
	 * 是否为非空字符。
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}
	
	/**
	 * 是否为空字符。
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (str == null || str.length() == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static String prefixFill(String str0, char c, int length) {
		if (str0.length() < length) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < (length - str0.length()); i++) {
				sb.append(c);
			}
			sb.append(str0);
			str0 = sb.toString();
		}
		return str0;
	}
	
	public static String suffixFill(String str0, char c, int length) {
		if (str0.length() < length) {
			StringBuilder sb = new StringBuilder(str0);
			for (int i = 0; i < (length - str0.length()); i++) {
				sb.append(c);
			}
			str0 = sb.toString();
		}
		return str0;
	}
}
