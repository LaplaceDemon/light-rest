package sjq.light.rest.http.server.example;

import sjq.light.rest.http.server.HttpRestServer;

public class TestHttpServer {

	public static void main(String[] args) throws Exception {
		testHttpRestServer();
	}
	
	public static void testHttpRestServer() throws Exception {
		try (HttpRestServer httpRestServer = new HttpRestServer(8080)) {
			httpRestServer.scanRestPackage("sjq.light.rest.http.server.example");
			httpRestServer.start();
		}
	}
}