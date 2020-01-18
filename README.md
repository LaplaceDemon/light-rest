# light-rest
A lightweight, high performance Restful API framework.

轻量级，高性能RESTful API框架。

Tomcat慢，SpringMVC重。构建Restful API真的不需要那么复杂。



## Maven下载依赖

下载当前最新版本：

```xml
<dependency>
    <groupId>io.github.laplacedemon</groupId>
    <artifactId>light-rest</artifactId>
    <version>0.4.0</version>
</dependency>
```






## 快速构建一组基于HTTP协议的Restful API

### 1 创建服务器：
```java
package sjq.light.rest.http.server;

import sjq.light.rest.http.request.Request;
import sjq.light.rest.http.response.Response;
import sjq.light.rest.http.rest.Rest;
import sjq.light.rest.http.rest.RestHandler;

public class TestHttpServer {

    public static void main(String[] args) throws Exception {
        try (HttpRestServer httpRestServer = new HttpRestServer(8080)) {
            // 注册REST处理器所在package
            httpRestServer.scanRestPackage("sjq.light.rest.http.server");
            httpRestServer.start();
        }
    }
}
```


### 2 创建REST处理器

#### 2.1 一个最基础的REST处理器。
```java
package io.github.laplacedemon.light.rest.http;

import ...

@Rest(value = "/test")
public class TestHttpHandler1 extends RestHandler {

    @Override
    public void get(RestRequest request, IOSession ioSession) {
    	RestResponse response = new RestResponse();
        response.setBodyContent("hello-test");
        ioSession.writeAndFlush(response);
    }
    
}
```

执行命令：```curl http://127.0.0.1:8080/test```<br>
响应返回：```hello-test```


#### 2.2 使用Get, Post，Delete，Put等其他HTTP方法

```java
package sjq.light.rest.http.server.example;

// 省略import

@Rest(value = "/method")
public class TestHttpHandler4 extends RestHandler {

	@Override
	public void get(RestRequest request, IOSession ioSession) {
		RestResponse response = new RestResponse();
		response.setBodyContent("method-get");
		ioSession.writeAndFlush(response);
	}

	@Override
	public void post(RestRequest request, IOSession ioSession) throws Exception {
		RestResponse response = new RestResponse();
		response.setBodyContent("method-post");
		ioSession.writeAndFlush(response);
	}

	@Override
	public void put(RestRequest request, IOSession ioSession) throws Exception {
		RestResponse response = new RestResponse();
		response.setBodyContent("method-put");
		ioSession.writeAndFlush(response);
	}

	@Override
	public void delete(RestRequest request, IOSession ioSession) throws Exception {
		RestResponse response = new RestResponse();
		response.setBodyContent("method-delete");
		ioSession.writeAndFlush(response);
	}

	@Override
	public void head(RestRequest request, IOSession ioSession) throws Exception {
		RestResponse response = new RestResponse();
		response.setBodyContent("method-head");
		ioSession.writeAndFlush(response);
	}

	@Override
	public void options(RestRequest request, IOSession ioSession) throws Exception {
		RestResponse response = new RestResponse();
		response.setBodyContent("method-options");
		ioSession.writeAndFlush(response);
	}

}
```

发起Get请求：<br>
执行命令：```curl http://127.0.0.1:8080/method```<br>
响应返回：```method-get```

发起Post请求：<br>
执行命令：```curl -X POST http://127.0.0.1:8080/method```<br>
响应返回：```method-post```

发起Delete请求：<br>
执行命令：```curl -X DELETE http://127.0.0.1:8080/method```<br>
响应返回：```method-delete```



#### 2.3 提取查询字符串中的参数

```java
package io.github.laplacedemon.light.rest.http;

import ...

@Rest(value = "/query-string")
public class TestHttpHandler5 extends RestHandler {

	@Override
	public void get(RestRequest request, IOSession ioSession) {
		List<String> ids = request.getParamList("id");
		RestResponse response = new RestResponse();
		response.setBodyContent("query-string:" + ids);
		ioSession.writeAndFlush(response);
	}
	
}
```

执行命令: ```curl  "http://127.0.0.1:8080/query-string?id=1&id=2&id=100"```<br>
响应返回：```query-string:[1, 2, 100]```


#### 2.4 获取请求体中的内容

```java
package io.github.laplacedemon.light.rest.http;

import ...

@Rest(value = "/content")
public class TestHttpHandler6 extends RestHandler {

	@Override
	public void post(RestRequest request, IOSession ioSession) throws IOException {
		String bodyContent = request.parseBodyContent();
		RestResponse response = new RestResponse();
		response.setBodyContent("content:" + bodyContent);
		ioSession.writeAndFlush(response);
	}

}
```

执行命令：```curl -X POST -d "helloworld" "http://127.0.0.1:8080/content"```<br>
响应返回：```content:helloworld```


#### 2.5 提取URL中的参数
Restful API经常需要解析URL中数值。light-rest对URL的匹配取值是非常灵活的。
```java
package io.github.laplacedemon.light.rest.http;

import ...

@Rest(value = "/test/{id}")
public class TestHttpHandler2 extends RestHandler {

	@Override
	public void get(RestRequest request, IOSession ioSession) {
		RestResponse response = new RestResponse();
		Map<String, String> pathParams = request.getPathParams();
		String value = pathParams.get("id");
		response.setBodyContent("hello-test:" + value);
		ioSession.writeAndFlush(response);
	}

}
```

执行命令：```curl http://127.0.0.1:8080/test/123```<br>
响应返回：```hello-test:123```


#### 2.6 提取多个URL中的参数
```java
package io.github.laplacedemon.light.rest.http;

import ...

@Rest(value = "/test/{id}/{name}")
public class TestHttpHandler3 extends RestHandler {

	@Override
	public void get(RestRequest request, IOSession ioSession) {
		RestResponse response = new RestResponse();
		Map<String, String> pathParams = request.getPathParams();
		String id = pathParams.get("id");
		String name = pathParams.get("name");
		response.setBodyContent("hello-test:" + id + "," + name);
		ioSession.writeAndFlush(response);
	}

}
```

执行命令：```curl http://127.0.0.1:8080/test/123/tomcat```<br>
响应返回：```hello-test:123,tomcat```



### 3 IoC 容器

light-rest 核心提供了一个简易的容器工厂，用于实现依赖注入。实现非常简单，只需要在server启动前向工厂注册，这样RestHander就可以直接使用这些注入的数值了。

```java
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
```

```java
package io.github.laplacedemon.light.rest.ioc;

import ...

@Rest(value = "/ioc/text")
public class TestStringHandler extends RestHandler {
	
	@Component
	private String testString;

	@Override
	public void get(RestRequest request, IOSession ioSession) throws Exception {
		ioSession.writeTextAndFlush(testString);
	}
	
}
```

```java
package io.github.laplacedemon.light.rest.ioc;

import ...

@Rest(value = "/ioc/texts")
public class TestStringsHandler extends RestHandler {
	
	@Component
	private String[] testStrings;

	@Override
	public void get(RestRequest request, IOSession ioSession) throws Exception {
		ioSession.writeTextAndFlush(Arrays.toString(testStrings));
	}
	
}

```



### 4 静态文件服务器

light-rest提供静态资源访问服务，以方便构建一个完整的Web服务。

例：以下代码的静态资源放置于当前项目resources/static目录下。访问URL的前缀为 `/` 。

```java
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
```

```java
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
```











