package io.github.laplacedemon.light.rest.ioc;

import java.util.concurrent.ConcurrentHashMap;

public class IoCFactory {
	private final ConcurrentHashMap<Class<?>, Object> iocMap;
	
	public IoCFactory() {
		iocMap = new ConcurrentHashMap<>();
	}
	
	public void register(final Object obj) {
		Class<? extends Object> clazz = obj.getClass();
		this.iocMap.put(clazz, obj);
	}
	
	public Object get(Class<?> clazz) {
		return this.iocMap.get(clazz);
	}
}
