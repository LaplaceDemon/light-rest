package io.github.laplacedemon.light.rest.http;

import java.util.Map;

import io.github.laplacedemon.light.rest.http.connection.IOSession;
import io.github.laplacedemon.light.rest.http.request.RestRequest;
import io.github.laplacedemon.light.rest.http.response.RestResponse;
import io.github.laplacedemon.light.rest.http.rest.Rest;
import io.github.laplacedemon.light.rest.http.rest.RestHandler;

@Rest(value = "/test/{id}/{name}")
public class TestHttpHandler3 extends RestHandler {

	@Override
	public void get(RestRequest request, IOSession ioSession) {
		RestResponse response = new RestResponse();
		Map<String, String> pathParams = request.getPathParams();
		String id = pathParams.get("id");
		String name = pathParams.get("name");
		response.setBodyContent("hello-test:" + id + "," + name);
		ioSession.writeAndFlush(response);
	}

}