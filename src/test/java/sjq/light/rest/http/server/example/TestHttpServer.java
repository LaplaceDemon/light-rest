package sjq.light.rest.http.server.example;

import org.junit.Test;

import sjq.light.rest.http.server.HttpRestServer;

public class TestHttpServer {

	@Test
	public void testHttpRestServer() throws Exception {
		try (HttpRestServer httpRestServer = new HttpRestServer(8080)) {
			httpRestServer.scanRestPackage("sjq.light.rest.http.server.example");
			httpRestServer.start();
		}
	}
}