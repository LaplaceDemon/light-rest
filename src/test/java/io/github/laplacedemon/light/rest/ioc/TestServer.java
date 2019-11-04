package io.github.laplacedemon.light.rest.ioc;

import io.github.laplacedemon.light.rest.http.server.HttpRestServer;

public class TestServer {
	
	public TestServer() {
	}
	
	public void start() throws Exception {
		try (HttpRestServer httpRestServer = new HttpRestServer(8080)) {
			
			String str = "helloworld";
			httpRestServer.iocFactory().register(str);
			
			httpRestServer.scanRestPackage("io.github.laplacedemon.light.rest.ioc");
			httpRestServer.start();
		}
	}
	
	public static void main(String[] args) throws Exception {
		new TestServer().start();
	}
	
}
