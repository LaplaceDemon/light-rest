package io.github.laplacedemon.light.rest.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtils {
	private ExceptionUtils() {
	}
	
	/**
	 * 输出异常栈。
	 * @param throwable
	 * @return
	 */
	public static String parseExceptionStackTrace(Throwable throwable) {
		// 根据StringWriter源码，可以看到StringWriter的close()方法本身没有什么作用。所以不需要考虑关闭StringWriter。
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		throwable.printStackTrace(writer);
		StringBuffer buffer = stringWriter.getBuffer();
		return buffer.toString();
	}
	
}
