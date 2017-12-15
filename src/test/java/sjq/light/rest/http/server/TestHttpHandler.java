package sjq.light.rest.http.server;

import sjq.light.rest.http.request.Request;
import sjq.light.rest.http.response.Response;
import sjq.light.rest.http.rest.Rest;
import sjq.light.rest.http.rest.RestHandler;

@Rest(value = "/test")
public class TestHttpHandler extends RestHandler {

    @Override
    public Response get(Request request) {
        Response response = new Response();
        response.setBodyContent("hello-test");
        return response;
    }
    
}