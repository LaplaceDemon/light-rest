package io.github.laplacedemon.light.rest.ioc;

import io.github.laplacedemon.light.rest.http.connection.IOSession;
import io.github.laplacedemon.light.rest.http.request.RestRequest;
import io.github.laplacedemon.light.rest.http.rest.Rest;
import io.github.laplacedemon.light.rest.http.rest.RestHandler;
import io.github.laplacedemon.light.rest.ioc.Component;

@Rest(value = "/ioc/text")
public class TestStringHandler extends RestHandler {
	
	@Component
	private String testString;

	@Override
	public void get(RestRequest request, IOSession ioSession) throws Exception {
		ioSession.writeTextAndFlush(testString);
	}
	
}
