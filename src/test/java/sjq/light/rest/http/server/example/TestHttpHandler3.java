package sjq.light.rest.http.server.example;

import java.util.Map;

import sjq.light.rest.http.request.Request;
import sjq.light.rest.http.response.Response;
import sjq.light.rest.http.rest.Rest;
import sjq.light.rest.http.rest.RestHandler;

@Rest(value = "/test/{id}/{name}")
public class TestHttpHandler3 extends RestHandler {

	@Override
	public Response get(Request request) {
		Response response = new Response();
		Map<String, String> pathParams = request.getPathParams();
		String id = pathParams.get("id");
		String name = pathParams.get("name");
		response.setBodyContent("hello-test:" + id + "," + name);
		return response;
	}

}