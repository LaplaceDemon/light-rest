package sjq.light.rest.http.server.example;

import java.util.Map;

import sjq.light.rest.http.request.Request;
import sjq.light.rest.http.response.Response;
import sjq.light.rest.http.rest.Rest;
import sjq.light.rest.http.rest.RestHandler;

@Rest(value = "/test/{id}")
public class TestHttpHandler2 extends RestHandler {

	@Override
	public Response get(Request request) {
		Response response = new Response();
		Map<String, String> pathParams = request.getPathParams();
		String value = pathParams.get("id");
		response.setBodyContent("hello-test:" + value);
		return response;
	}

}