package io.github.laplacedemon.light.rest.http.response;

import java.util.Map;

public class JSONResponse extends RestResponse {

	public JSONResponse() {
		super();
		Map<String, String> headMap = this.getHeadMap();
		headMap.put("Content-Type", "application/json");
	}
	
	public JSONResponse(String bodyContent) {
		this();
		this.setBodyContent(bodyContent);
	}
	
}
