# light-rest
A lightweight, high performance Restful API framework. 轻量级，高性能RESTful API框架。

## 快速构建一组基于HTTP协议的Restful API

### 创建服务器：
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


### 创建REST处理器

#### 一个最基础的REST处理器。
```java
package sjq.light.rest.http.server;

import sjq.light.rest.http.request.Request;
import sjq.light.rest.http.response.Response;
import sjq.light.rest.http.rest.Rest;
import sjq.light.rest.http.rest.RestHandler;

@Rest(value = "/test")
public class TestHttpHandler1 extends RestHandler {

    @Override
    public Response get(Request request) {
        Response response = new Response();
        response.setBodyContent("hello-test");
        return response;
    }
    
}
```
执行命令：curl http://127.0.0.1:8080/test
返回：hello-test


#### 提取URL中的参数
Restful API经常需要解析URL中数值。light-rest对URL的匹配取值是非常灵活的。
```
// 省略import

@Rest(value = "/test/{id}")
public class TestHttpHandler2 extends RestHandler {

	@Override
	public Response get(Request request) {
		Response response = new Response();
		Map<String, String> pathParams = request.getPathParams();
		String value = pathParams.get("id");
		response.setBodyContent("hello-test:" + value);
		return response;
	}

}
```
执行命令：curl http://127.0.0.1:8080/test/123
返回：hello-test:123

#### 提取多个参数
```
package sjq.light.rest.http.server.example;

// 省略import

@Rest(value = "/test/{id}/{name}")
public class TestHttpHandler3 extends RestHandler {

	@Override
	public Response get(Request request) {
		Response response = new Response();
		Map<String, String> pathParams = request.getPathParams();
		String id = pathParams.get("id");
		String name = pathParams.get("name");
		response.setBodyContent("hello-test:" + id + "," + name);
		return response;
	}

}
```
执行命令：curl http://127.0.0.1:8080/test/123/tomcat
返回：hello-test:123,tomcat



#### 使用Post，Delete，Put等其他HTTP方法

```java
package sjq.light.rest.http.server.example;

// 省略import

@Rest(value = "/method")
public class TestHttpHandler4 extends RestHandler {

	@Override
	public Response get(Request request) {
		Response response = new Response();
		response.setBodyContent("method-get");
		return response;
	}

	@Override
	public Response post(Request request) throws Exception {
		Response response = new Response();
		response.setBodyContent("method-post");
		return response;
	}

	@Override
	public Response put(Request request) throws Exception {
		Response response = new Response();
		response.setBodyContent("method-put");
		return response;
	}

	@Override
	public Response delete(Request request) throws Exception {
		Response response = new Response();
		response.setBodyContent("method-delete");
		return response;
	}

	@Override
	public Response head(Request request) throws Exception {
		Response response = new Response();
		response.setBodyContent("method-head");
		return response;
	}

	@Override
	public Response options(Request request) throws Exception {
		Response response = new Response();
		response.setBodyContent("method-options");
		return response;
	}

}
```
执行命令：curl http://127.0.0.1:8080/method
返回：method-get

执行命令：curl -X POST http://127.0.0.1:8080/method
返回：method-post

执行命令：curl -X DELETE http://127.0.0.1:8080/method
返回：method-delete











