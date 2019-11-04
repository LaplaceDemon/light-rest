package io.github.laplacedemon.light.rest.http;

import io.github.laplacedemon.light.rest.http.connection.IOSession;
import io.github.laplacedemon.light.rest.http.request.RestRequest;
import io.github.laplacedemon.light.rest.http.response.RestResponse;
import io.github.laplacedemon.light.rest.http.rest.Rest;
import io.github.laplacedemon.light.rest.http.rest.RestHandler;

@Rest(value = "/method")
public class TestHttpHandler4 extends RestHandler {

	@Override
	public void get(RestRequest request, IOSession ioSession) {
		RestResponse response = new RestResponse();
		response.setBodyContent("method-get");
		ioSession.writeAndFlush(response);
	}

	@Override
	public void post(RestRequest request, IOSession ioSession) throws Exception {
		RestResponse response = new RestResponse();
		response.setBodyContent("method-post");
		ioSession.writeAndFlush(response);
	}

	@Override
	public void put(RestRequest request, IOSession ioSession) throws Exception {
		RestResponse response = new RestResponse();
		response.setBodyContent("method-put");
		ioSession.writeAndFlush(response);
	}

	@Override
	public void delete(RestRequest request, IOSession ioSession) throws Exception {
		RestResponse response = new RestResponse();
		response.setBodyContent("method-delete");
		ioSession.writeAndFlush(response);
	}

	@Override
	public void head(RestRequest request, IOSession ioSession) throws Exception {
		RestResponse response = new RestResponse();
		response.setBodyContent("method-head");
		ioSession.writeAndFlush(response);
	}

	@Override
	public void options(RestRequest request, IOSession ioSession) throws Exception {
		RestResponse response = new RestResponse();
		response.setBodyContent("method-options");
		ioSession.writeAndFlush(response);
	}

}