package sjq.light.rest.http.server.example;

import java.util.List;

import io.github.laplacedemon.light.rest.http.connection.IOSession;
import io.github.laplacedemon.light.rest.http.request.RestRequest;
import io.github.laplacedemon.light.rest.http.response.RestResponse;
import io.github.laplacedemon.light.rest.http.rest.Rest;
import io.github.laplacedemon.light.rest.http.rest.RestHandler;

@Rest(value = "/query-string")
public class TestHttpHandler5 extends RestHandler {

	@Override
	public void get(RestRequest request, IOSession ioSession) {
		List<String> ids = request.getParamList("id");
		RestResponse response = new RestResponse();
		response.setBodyContent("query-string:" + ids);
		ioSession.writeAndFlush(response);
	}
	
}