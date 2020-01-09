package io.github.laplacedemon.light.rest.ioc;

import io.github.laplacedemon.light.rest.http.server.HttpRestServer;

public class TestServer {

	public TestServer() {
	}

	public void start() throws Exception {
		try (HttpRestServer httpRestServer = new HttpRestServer(8080)) {
			// Ioc factory
			IoCFactory iocFactory = httpRestServer.iocFactory();
			
			// register string
			String str = "helloworld";
			iocFactory.register(str);

			// register string array
			String[] strs = new String[3];
			strs[0] = "hello";
			strs[1] = "world";
			strs[2] = "!!!";
			iocFactory.register(strs);
			
			// scan package
			httpRestServer.scanRestPackage("io.github.laplacedemon.light.rest.ioc");
			
			// start
			httpRestServer.start();
		}
	}

	public static void main(String[] args) throws Exception {
		new TestServer().start();
	}

}
