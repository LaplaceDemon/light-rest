package sjq.light.rest.http.server.example;

import sjq.light.rest.http.request.Request;
import sjq.light.rest.http.response.Response;
import sjq.light.rest.http.rest.Rest;
import sjq.light.rest.http.rest.RestHandler;

@Rest(value = "/exception")
public class TestExceptionHttpHandler extends RestHandler {

	@Override
	public Response get(Request request) throws Exception {

		String bodyContent = request.getBodyContent();
		Response response = new Response();
		response.setBodyContent("content:" + bodyContent);
		throw new Exception("error!-!");
//		return response;
	}

}