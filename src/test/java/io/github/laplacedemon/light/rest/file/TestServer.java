package io.github.laplacedemon.light.rest.file;

import io.github.laplacedemon.light.rest.http.server.HttpRestServer;

public class TestServer {

    public TestServer() {
    }

    public void start() throws Exception {
        try (HttpRestServer httpRestServer = new HttpRestServer(8090, "/", "")) {
            // scan package
            httpRestServer.scanRestPackage("io.github.laplacedemon.light.rest.file");
            
            // start
            httpRestServer.start();
        }
    }

    public static void main(String[] args) throws Exception {
        new TestServer().start();
    }

}
