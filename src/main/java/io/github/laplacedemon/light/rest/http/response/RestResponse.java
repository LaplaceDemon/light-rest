package io.github.laplacedemon.light.rest.http.response;

import java.util.HashMap;
import java.util.Map;

public class RestResponse {
	private int status = 204;
	private String bodyContent = "";
	private Map<String, String> headMap = new HashMap<>();

	public String getBodyContent() {
		return bodyContent;
	}

	public void setBodyContent(String bodyContent) {
		this.bodyContent = bodyContent;
	}

	public String getContentType() {
		return this.headMap.get("Content-Type");
	}

	public void setContentType(String contentType) {
		this.headMap.put("Content-Type", contentType);
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setHead(String head, String value) {
		this.headMap.put(head, value);
	}

	public Map<String, String> getHeadMap() {
		return headMap;
	}

}
