package sjq.light.rest.http.server.example;

import java.util.Map;

import io.github.laplacedemon.light.rest.http.connection.IOSession;
import io.github.laplacedemon.light.rest.http.request.RestRequest;
import io.github.laplacedemon.light.rest.http.response.RestResponse;
import io.github.laplacedemon.light.rest.http.rest.Rest;
import io.github.laplacedemon.light.rest.http.rest.RestHandler;

@Rest(value = "/test/{id}")
public class TestHttpHandler2 extends RestHandler {

	@Override
	public void get(RestRequest request, IOSession ioSession) {
		RestResponse response = new RestResponse();
		Map<String, String> pathParams = request.getPathParams();
		String value = pathParams.get("id");
		response.setBodyContent("hello-test:" + value);
		ioSession.writeAndFlush(response);
	}

}