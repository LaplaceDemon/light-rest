package sjq.light.rest.http.server.example;

import sjq.light.rest.http.request.Request;
import sjq.light.rest.http.response.Response;
import sjq.light.rest.http.rest.Rest;
import sjq.light.rest.http.rest.RestHandler;

@Rest(value = "/method")
public class TestHttpHandler4 extends RestHandler {

	@Override
	public Response get(Request request) {
		Response response = new Response();
		response.setBodyContent("method-get");
		return response;
	}

	@Override
	public Response post(Request request) throws Exception {
		Response response = new Response();
		response.setBodyContent("method-post");
		return response;
	}

	@Override
	public Response put(Request request) throws Exception {
		Response response = new Response();
		response.setBodyContent("method-put");
		return response;
	}

	@Override
	public Response delete(Request request) throws Exception {
		Response response = new Response();
		response.setBodyContent("method-delete");
		return response;
	}

	@Override
	public Response head(Request request) throws Exception {
		Response response = new Response();
		response.setBodyContent("method-head");
		return response;
	}

	@Override
	public Response options(Request request) throws Exception {
		Response response = new Response();
		response.setBodyContent("method-options");
		return response;
	}

}