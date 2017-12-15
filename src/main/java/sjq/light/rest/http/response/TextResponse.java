package sjq.light.rest.http.response;

import java.util.Map;

public class TextResponse extends Response {
	
	public TextResponse() {
		super();
		Map<String, String> headMap = this.getHeadMap();
		headMap.put("Content-Type", "text/plain");
	}
}
