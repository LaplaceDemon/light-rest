package sjq.light.rest.http.server.example;

import sjq.light.rest.http.request.Request;
import sjq.light.rest.http.response.Response;
import sjq.light.rest.http.rest.Rest;
import sjq.light.rest.http.rest.RestHandler;

@Rest(value = "/content")
public class TestHttpHandler6 extends RestHandler {

	@Override
	public Response post(Request request) {

		String bodyContent = request.getBodyContent();
		Response response = new Response();
		response.setBodyContent("content:" + bodyContent);
		return response;
	}

}