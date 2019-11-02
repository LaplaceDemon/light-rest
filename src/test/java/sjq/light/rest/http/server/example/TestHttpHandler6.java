package sjq.light.rest.http.server.example;

import java.io.IOException;

import io.github.laplacedemon.light.rest.http.connection.IOSession;
import io.github.laplacedemon.light.rest.http.request.RestRequest;
import io.github.laplacedemon.light.rest.http.response.RestResponse;
import io.github.laplacedemon.light.rest.http.rest.Rest;
import io.github.laplacedemon.light.rest.http.rest.RestHandler;

@Rest(value = "/content")
public class TestHttpHandler6 extends RestHandler {

	@Override
	public void post(RestRequest request, IOSession ioSession) throws IOException {
		String bodyContent = request.parseBodyContent();
		RestResponse response = new RestResponse();
		response.setBodyContent("content:" + bodyContent);
		ioSession.writeAndFlush(response);
	}

}