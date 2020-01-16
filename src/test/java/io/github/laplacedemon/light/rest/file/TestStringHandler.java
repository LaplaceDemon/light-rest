package io.github.laplacedemon.light.rest.file;

import io.github.laplacedemon.light.rest.http.connection.IOSession;
import io.github.laplacedemon.light.rest.http.request.RestRequest;
import io.github.laplacedemon.light.rest.http.rest.Rest;
import io.github.laplacedemon.light.rest.http.rest.RestHandler;

@Rest(value = "/text")
public class TestStringHandler extends RestHandler {

	@Override
	public void get(RestRequest request, IOSession ioSession) throws Exception {
		ioSession.writeTextAndFlush("hello world!");
	}
	
}
