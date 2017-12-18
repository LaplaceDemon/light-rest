package sjq.light.rest.http.server.example;

import java.util.List;

import sjq.light.rest.http.request.Request;
import sjq.light.rest.http.response.Response;
import sjq.light.rest.http.rest.Rest;
import sjq.light.rest.http.rest.RestHandler;

@Rest(value = "/query-string")
public class TestHttpHandler5 extends RestHandler {

	@Override
	public Response get(Request request) {
		List<String> ids = request.getParams("id");
		Response response = new Response();
		response.setBodyContent("query-string:" + ids);
		return response;
	}
	
}