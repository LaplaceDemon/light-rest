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
    <version>0.1.1</version>
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

执行命令：```curl http://127.0.0.1:8080/test```<br>
响应返回：```hello-test```


#### 2.2 使用Get, Post，Delete，Put等其他HTTP方法

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
package sjq.light.rest.http.server.example;

import java.util.List;

import sjq.light.rest.http.request.Request;
import sjq.light.rest.http.response.Response;
import sjq.light.rest.http.rest.Rest;
import sjq.light.rest.http.rest.RestHandler;

@Rest(value = "/query-string")
public class TestHttpHandler5 extends RestHandler {

    @Override
    public Response get(Request request) {
        List<String> ids = request.getParams("id");
        Response response = new Response();
        response.setBodyContent("query-string:" + ids);
        return response;
    }
    
}
```

执行命令: ```curl  "http://127.0.0.1:8080/query-string?id=1&id=2&id=100"```<br>
响应返回：```query-string:[1, 2, 100]```


#### 2.4 获取请求体中的内容

```java
package sjq.light.rest.http.server.example;

import sjq.light.rest.http.request.Request;
import sjq.light.rest.http.response.Response;
import sjq.light.rest.http.rest.Rest;
import sjq.light.rest.http.rest.RestHandler;

@Rest(value = "/content")
public class TestHttpHandler6 extends RestHandler {

    @Override
    public Response post(Request request) {

        String bodyContent = request.getBodyContent();
        Response response = new Response();
        response.setBodyContent("content:" + bodyContent);
        return response;
    }

}
```

执行命令：```curl -X POST -d "helloworld" "http://127.0.0.1:8080/content"```<br>
响应返回：```content:helloworld```


#### 2.5 提取URL中的参数
Restful API经常需要解析URL中数值。light-rest对URL的匹配取值是非常灵活的。
```java
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

执行命令：```curl http://127.0.0.1:8080/test/123```<br>
响应返回：```hello-test:123```


#### 2.6 提取多个URL中的参数
```java
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

执行命令：```curl http://127.0.0.1:8080/test/123/tomcat```<br>
响应返回：```hello-test:123,tomcat```















