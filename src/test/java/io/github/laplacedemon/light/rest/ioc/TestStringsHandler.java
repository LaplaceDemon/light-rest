package io.github.laplacedemon.light.rest.ioc;

import java.util.Arrays;

import io.github.laplacedemon.light.rest.http.connection.IOSession;
import io.github.laplacedemon.light.rest.http.request.RestRequest;
import io.github.laplacedemon.light.rest.http.rest.Rest;
import io.github.laplacedemon.light.rest.http.rest.RestHandler;
import io.github.laplacedemon.light.rest.ioc.Component;

@Rest(value = "/ioc/texts")
public class TestStringsHandler extends RestHandler {
	
	@Component
	private String[] testStrings;

	@Override
	public void get(RestRequest request, IOSession ioSession) throws Exception {
		ioSession.writeTextAndFlush(Arrays.toString(testStrings));
	}
	
}
