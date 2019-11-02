package sjq.light.rest.http.server.example;

import io.github.laplacedemon.light.rest.http.connection.IOSession;
import io.github.laplacedemon.light.rest.http.request.RestRequest;
import io.github.laplacedemon.light.rest.http.response.RestResponse;
import io.github.laplacedemon.light.rest.http.rest.Rest;
import io.github.laplacedemon.light.rest.http.rest.RestHandler;

@Rest(value = "/test")
public class TestHttpHandler1 extends RestHandler {

    @Override
    public void get(RestRequest request, IOSession ioSession) {
    	RestResponse response = new RestResponse();
        response.setBodyContent("hello-test");
        ioSession.writeAndFlush(response);
    }
    
}