package org.brandao.concurrent;

interface ObjectFactory {

	Object createInstance();
	
	void destroy(Object instance);
	
}
