package org.brandao.concurrent;

import java.lang.reflect.Proxy;

import junit.framework.TestCase;
import org.brandao.concurrent.DisposableSingletonContext.ObjectHandler;

public class DisposableSingletonContextTest extends TestCase{

	public void testCreate(){
		DisposableSingletonContext<String> context = 
				new DisposableSingletonContext<String>();
		context.registryBeanDefinition("teste", new ObjectFactory() {
			
			public void destroy(Object instance) {
			}
			
			public Object createInstance() {
				return new Object();
			}
			
		});
		
		Object a  = context.getBean("A", "teste");
		Object aa = context.getBean("A", "teste");
		Object b = context.getBean("B", "teste");
		Object bb = context.getBean("B", "teste");
		
		Object aHandler = ((ObjectHandler) Proxy.getInvocationHandler(a)).object;
		Object aaHandler = ((ObjectHandler) Proxy.getInvocationHandler(aa)).object;
		Object bHandler = ((ObjectHandler) Proxy.getInvocationHandler(b)).object;
		Object bbHandler = ((ObjectHandler) Proxy.getInvocationHandler(bb)).object;
		
		TestCase.assertEquals(aHandler, aaHandler);
		TestCase.assertEquals(bHandler, bbHandler);
		
		a = null;
		b = null;
		aa = null;
		bb = null;
		
		Runtime.getRuntime().gc();

		a = context.getBean("A", "teste");
		
		Object newAHandler = ((ObjectHandler) Proxy.getInvocationHandler(a)).object;

		TestCase.assertTrue(aHandler != newAHandler);
		
	}
	
}