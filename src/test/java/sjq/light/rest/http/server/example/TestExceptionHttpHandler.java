package sjq.light.rest.http.server.example;

import io.github.laplacedemon.light.rest.http.connection.IOSession;
import io.github.laplacedemon.light.rest.http.request.RestRequest;
import io.github.laplacedemon.light.rest.http.response.RestResponse;
import io.github.laplacedemon.light.rest.http.rest.Rest;
import io.github.laplacedemon.light.rest.http.rest.RestHandler;

@Rest(value = "/exception")
public class TestExceptionHttpHandler extends RestHandler {

	@Override
	public void get(RestRequest request, IOSession ioSession) throws Exception {
		String bodyContent = request.parseBodyContent();
		RestResponse response = new RestResponse();
		response.setBodyContent("content:" + bodyContent);
		throw new Exception("error!-!");
	}

}