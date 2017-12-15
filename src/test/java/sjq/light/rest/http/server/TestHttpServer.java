package sjq.light.rest.http.server;

import org.junit.Test;

public class TestHttpServer {
	
	@Test
	public void testHttpRestServer() throws Exception {
		try(HttpRestServer httpRestServer = new HttpRestServer(7055)) {
			httpRestServer.scanRestPackage("sjq.light.rest.http.server");
			httpRestServer.start();
		}
	}
}