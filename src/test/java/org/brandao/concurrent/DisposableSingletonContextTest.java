package org.brandao.concurrent;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import org.brandao.concurrent.DisposableSingletonContext.ObjectHandler;

public class DisposableSingletonContextTest extends TestCase{

	public void testCreate() throws InterruptedException{
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

		Thread.sleep(2000);
		
		a = context.getBean("A", "teste");
		
		Object newAHandler = ((ObjectHandler) Proxy.getInvocationHandler(a)).object;

		TestCase.assertTrue(aHandler != newAHandler);
		
	}

	public void testCreateConcurrent() throws InterruptedException{
		
		final List<Object> list = new ArrayList<Object>();
		
		final DisposableSingletonContext<String> context = 
				new DisposableSingletonContext<String>();
		context.registryBeanDefinition("teste", new ObjectFactory() {
			
			public void destroy(Object instance) {
			}
			
			public Object createInstance() {
				return new Object();
			}
			
		});
		
		list.add(context.getBean("A", "teste"));
		list.add(context.getBean("B", "teste"));
		
		Thread th = new Thread(){
			
			public void run(){
				list.add(context.getBean("A", "teste"));
				list.add(context.getBean("B", "teste"));
			}
			
		};
		
		th.start();
		
		Thread.sleep(1000);
		
		Object reference1 = ((ObjectHandler) Proxy.getInvocationHandler(list.get(0))).object;
		Object reference2 = ((ObjectHandler) Proxy.getInvocationHandler(list.get(1))).object;
		Object reference3 = ((ObjectHandler) Proxy.getInvocationHandler(list.get(2))).object;
		Object reference4 = ((ObjectHandler) Proxy.getInvocationHandler(list.get(3))).object;
		
		TestCase.assertEquals(
				reference1,
				reference3
		);

		TestCase.assertEquals(
				reference2,
				reference4
		);
		
		list.clear();
		
		Runtime.getRuntime().gc();

		Thread.sleep(2000);
		
		Object a = context.getBean("A", "teste");
		
		Object reference = ((ObjectHandler) Proxy.getInvocationHandler(a)).object;

		TestCase.assertTrue(reference1 != reference);
		
	}
	
}