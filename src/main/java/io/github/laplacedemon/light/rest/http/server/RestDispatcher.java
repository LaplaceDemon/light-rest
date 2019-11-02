package io.github.laplacedemon.light.rest.http.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.github.laplacedemon.light.rest.http.rest.MatchAction;
import io.github.laplacedemon.light.rest.http.rest.Rest;
import io.github.laplacedemon.light.rest.http.rest.RestHandler;
import io.github.laplacedemon.light.rest.http.url.URLParser;
import io.github.laplacedemon.light.rest.http.url.params.MatchParams;
import io.github.laplacedemon.light.rest.util.PackageScanner;

public class RestDispatcher {
	
	private static RestDispatcher restDispatcher = new RestDispatcher();
	
	public static RestDispatcher createDispatcher(String packageName){
		restDispatcher.scanRestHandler(packageName);
		return restDispatcher;
	}
	
	private RestDispatcher(){}
	
	private Map<URLParser,RestHandler> uriToRESTHandlerMapper = new HashMap<>();
	
	private void scanRestHandler(String pachageName) {
		PackageScanner packageScanner = new PackageScanner();
		try {
			List<Class<?>> classList = packageScanner.scan(pachageName);
			for(Class<?> clazz : classList){
				boolean isRestHandler = clazz.isAnnotationPresent(Rest.class);
				if(isRestHandler) {
					Rest restAnnotation = clazz.getAnnotation(Rest.class);
					String urlTemplate = restAnnotation.value();
					URLParser urlParse = URLParser.parse(urlTemplate);					
					RestHandler restHandler = (RestHandler)clazz.newInstance();
					this.register(urlParse, restHandler);
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
		
	public void register(URLParser urlParse,RestHandler basicRESTHandler){
		uriToRESTHandlerMapper.put(urlParse, basicRESTHandler);
	}
	
	public MatchAction findBasicRESTHandler(String uri) {
		for(Entry<URLParser, RestHandler> entry : uriToRESTHandlerMapper.entrySet()){
			URLParser parser = entry.getKey();
			MatchParams matchPathParams = parser.matchPathParams(uri);
			if(matchPathParams.isMatch()){
				return new MatchAction(uri, matchPathParams, entry.getValue());
			}
		}
		
		return null;
	}
	
}
