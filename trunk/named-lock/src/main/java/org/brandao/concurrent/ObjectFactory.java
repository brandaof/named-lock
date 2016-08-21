package org.brandao.concurrent;

public interface ObjectFactory {

	Object createInstance();
	
	void destroy(Object instance);
	
}
