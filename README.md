# light-rest
A lightweight, high performance Restful API framework

快速构建一组基于HTTP协议的Restful API

创建服务器：
```java
package sjq.light.rest.http.server;

import sjq.light.rest.http.request.Request;
import sjq.light.rest.http.response.Response;
import sjq.light.rest.http.rest.Rest;
import sjq.light.rest.http.rest.RestHandler;

public class TestHttpServer {

	public static void main(String[] args) throws Exception {
		try (HttpRestServer httpRestServer = new HttpRestServer(8080)) {
			httpRestServer.scanRestPackage("sjq.light.rest.http.server");
			httpRestServer.start();
		}
	}
}
```

创建REST处理器
```java
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
```


调用方法，执行命令：curl 

