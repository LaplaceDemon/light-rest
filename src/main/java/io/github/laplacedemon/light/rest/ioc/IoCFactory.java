package io.github.laplacedemon.light.rest.ioc;

import java.util.concurrent.ConcurrentHashMap;

public class IoCFactory {
	private final ConcurrentHashMap<Class<?>, Object> iocClazzValueMap;
	
	public IoCFactory() {
		iocClazzValueMap = new ConcurrentHashMap<>();
	}
	
	public void register(final Object obj) {
		Class<? extends Object> clazz = obj.getClass();
		this.iocClazzValueMap.put(clazz, obj);
	}
	
	public <T> void register(Class<? super T> clazz, final T obj) {
		this.iocClazzValueMap.put(clazz, obj);
	}
	
	public Object get(Class<?> clazz) {
		return this.iocClazzValueMap.get(clazz);
	}
}
