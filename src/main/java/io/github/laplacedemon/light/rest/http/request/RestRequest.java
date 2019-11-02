package io.github.laplacedemon.light.rest.http.request;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;

public class RestRequest {
    private String url;
    private Map<String, String> pathParams;
    private Map<String, List<String>> queryParams;
    private HttpRequest httpRequest;
    
    public RestRequest(
    		final String url,
    		final Map<String, String> pathParams, 
    		final Map<String, List<String>> queryParams,
    		final HttpRequest httpRequest
    ) {
        super();
        this.url = url;
        this.pathParams = pathParams;
        this.queryParams = queryParams;
        this.httpRequest = httpRequest;
    }
    
    public ByteBuffer parseBodyContentBytes() throws IOException {
    	final ByteBuffer resultByteBuffer;
        if (httpRequest instanceof FullHttpRequest) {
            FullHttpRequest fullHttpRequest = (FullHttpRequest) httpRequest;

            // http body
            ByteBuf contentByteBuf = fullHttpRequest.content();
            String contentEncoding = fullHttpRequest.headers().get("Content-Encoding");
            
            if (contentEncoding != null && contentEncoding.equalsIgnoreCase("gzip")) {
                byte[] bytes = ByteBufUtil.getBytes(contentByteBuf);
                byte[] decompressBytes = null;
                
                // GZIP decompress
                if (bytes == null || bytes.length == 0) {
                	resultByteBuffer = ByteBuffer.allocate(0);
                	return resultByteBuffer;
                }
                
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                try {
                    GZIPInputStream ungzip = new GZIPInputStream(in);
                    byte[] buffer = new byte[1024];
                    int n;
                    while ((n = ungzip.read(buffer)) >= 0) {
                        out.write(buffer, 0, n);
                    }
                    
                    decompressBytes = out.toByteArray();
                    resultByteBuffer = ByteBuffer.wrap(decompressBytes);
                } catch (IOException e) {
                    throw e;
                }
            } else {
            	byte[] array = contentByteBuf.array();
            	resultByteBuffer = ByteBuffer.wrap(array);
            }
        } else {
        	resultByteBuffer = ByteBuffer.allocate(0);
        }
    	
        return resultByteBuffer;
    }

    public String parseBodyContent() throws IOException {
    	final String bodyContent;
        if (httpRequest instanceof FullHttpRequest) {
            FullHttpRequest fullHttpRequest = (FullHttpRequest) httpRequest;

            // http body
            ByteBuf contentByteBuf = fullHttpRequest.content();
            String contentEncoding = fullHttpRequest.headers().get("Content-Encoding");
            
            // GZIP decompress
            if (contentEncoding != null && contentEncoding.equalsIgnoreCase("gzip")) {
                byte[] bytes = ByteBufUtil.getBytes(contentByteBuf);
                byte[] decompressBytes = null;
                
                if (bytes == null || bytes.length == 0) {
                    bodyContent = "";
                    return bodyContent;
                }
                
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                try {
                    GZIPInputStream ungzip = new GZIPInputStream(in);
                    byte[] buffer = new byte[256];
                    int n;
                    while ((n = ungzip.read(buffer)) >= 0) {
                        out.write(buffer, 0, n);
                    }
                    decompressBytes = out.toByteArray();
                    bodyContent = new String(decompressBytes);
                } catch (IOException e) {
                	throw e;
                }
            } else {
            	bodyContent = contentByteBuf.toString(Charset.forName("UTF-8"));
            }
        } else {
        	return "";
        }
    	
        return bodyContent;
    }

    public String getHeader(String head) {
        HttpHeaders headers = httpRequest.headers();
        if (headers.size() > 0) {
        	return headers.get(head);
        } else {
        	return null;
        }
    }

    public Map<String, String> copyHeadersMap() {
        HttpHeaders headers = httpRequest.headers();
        Map<String,String> headersMap = new HashMap<>();
        if (headers.size() > 0) {
	    	for (Entry<String, String> entry : headers.entries()) {
	    		headersMap.put(entry.getKey(), entry.getValue());
	        }
        }
        
        return headersMap;
    }

    public List<String> getParamList(String param) {
        if (this.queryParams == null) {
            return null;
        }
        return this.queryParams.get(param);
    }
    
    public String getParam(String param) {
        List<String> paramList = this.queryParams.get(param);
        if(paramList == null || paramList.isEmpty()) {
            return null;
        }
        
        return paramList.get(0);
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

	@Override
	public String toString() {
		return "Request [url=" + url + ", pathParams=" + pathParams + ", queryParams=" + queryParams + ", httpRequest="
				+ httpRequest + "]";
	}
	
}