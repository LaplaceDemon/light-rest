package sjq.light.rest.http.request;

import java.util.List;
import java.util.Map;

public class Request {
    private String url;
    private Map<String, String> pathParams;
    private Map<String, List<String>> queryParams;
    private Map<String, String> headMap;
    private String bodyContent;

    public Request(String url, Map<String, String> headMap ,Map<String, String> pathParams, Map<String, List<String>> queryParams, String bodyContent) {
        super();
        this.url = url;
        this.headMap = headMap;
        this.pathParams = pathParams;
        this.queryParams = queryParams;
        this.bodyContent = bodyContent;
    }

    public String getBodyContent() {
        return bodyContent;
    }

    public String getHead(String head) {
        if (this.headMap == null) {
            return null;
        }
        return this.headMap.get(head);
    }

    public Map<String, String> getHeadMap() {
        return headMap;
    }

    public List<String> getParams(String param) {
        if (this.queryParams == null) {
            return null;
        }
        return this.queryParams.get(param);
    }

    public Map<String, String> getPathParams() {
        return pathParams;
    }

    public String getPathParams(String param) {
        if (this.pathParams == null) {
            return null;
        }
        return this.pathParams.get(param);
    }

    public Map<String, List<String>> getQueryParams() {
        return queryParams;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}