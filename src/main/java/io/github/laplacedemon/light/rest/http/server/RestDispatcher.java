package io.github.laplacedemon.light.rest.http.server;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.github.laplacedemon.light.rest.http.rest.MatchAction;
import io.github.laplacedemon.light.rest.http.rest.Rest;
import io.github.laplacedemon.light.rest.http.rest.RestHandler;
import io.github.laplacedemon.light.rest.http.url.URLParser;
import io.github.laplacedemon.light.rest.http.url.params.MatchParams;
import io.github.laplacedemon.light.rest.ioc.Component;
import io.github.laplacedemon.light.rest.ioc.IoCFactory;
import io.github.laplacedemon.light.rest.util.PackageScannerUtils;

public class RestDispatcher {
	private IoCFactory ioCFactory;
	
	public static RestDispatcher createDispatcher(final String packageName, final IoCFactory ioCFactory) {
		RestDispatcher restDispatcher = new RestDispatcher();
		restDispatcher.ioCFactory = ioCFactory;
		restDispatcher.scanRestHandler(packageName);
		return restDispatcher;
	}
	
	private RestDispatcher(){}
	
	private Map<URLParser,RestHandler> uriToRESTHandlerMapper = new HashMap<>();
	
	private void scanRestHandler(String pachageName) {
		try {
			List<Class<?>> classList = PackageScannerUtils.scan(pachageName);
			for(Class<?> clazz : classList) {
				boolean isRestHandler = clazz.isAnnotationPresent(Rest.class);
				if(isRestHandler) {
					Rest restAnnotation = clazz.getAnnotation(Rest.class);
					String urlTemplate = restAnnotation.value();
					URLParser urlParse = URLParser.parse(urlTemplate);
					RestHandler restHandler = (RestHandler)clazz.newInstance();
					
					Field[] fields = clazz.getDeclaredFields();
					for (Field field : fields) {
						Component annotation = field.getAnnotation(Component.class);
						if (annotation != null) {
							Class<?> fieldType = field.getType();
							Object value = this.ioCFactory.get(fieldType);
							field.setAccessible(true);
							field.set(restHandler, value);
						}
					}
					
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
