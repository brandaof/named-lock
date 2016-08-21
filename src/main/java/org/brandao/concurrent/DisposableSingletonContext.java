/*
 * Named Lock http://namedlock.brandao.org/
 * Copyright (C) 2006-2016 Afonso Brandao. (afonso.rbn@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.brandao.concurrent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class DisposableSingletonContext<T> {

	private ConcurrentMap<String,ObjectFactory> factoryList = new ConcurrentHashMap<String, ObjectFactory>();

	private ConcurrentMap<T,Object> beans = new ConcurrentHashMap<T, Object>();
	
	private ConcurrentMap<T,Set<Object>> references = new ConcurrentHashMap<T, Set<Object>>();
	
	private Object getUniqueThreadReference(Thread thread){
		return thread.getId();
	}
	
	public void registryBeanDefinition(String name, ObjectFactory factory){
		if(factoryList.containsKey(name))
			throw new IllegalArgumentException(name);
		
		this.factoryList.put(name, factory);
	}
	
	public synchronized Object getBean(T alias, String beanName){
		
		Object bean = this.beans.get(alias);
		
		if(bean == null){
			bean = this.create(alias, beanName);
		}
		
		Thread currentThread  = Thread.currentThread();
		Object reference      = getUniqueThreadReference(currentThread);
		ObjectHandler handler = new ObjectHandler(alias, beanName, reference, bean);
		
		Set<Object> referenceSet = this.references.get(alias);
		
		if(referenceSet == null){
			referenceSet = this.createReferenceSet(alias);
		}
		
		referenceSet.add(reference);
		
		Object scoped =
				Proxy.newProxyInstance(
					currentThread.getContextClassLoader(), 
					bean.getClass().getInterfaces(), handler);
		
		
		return scoped;
	}
	
	private synchronized Object create(T alias, String beanName){
		Object bean = this.beans.get(alias);
		
		if(bean == null){
			ObjectFactory factory = this.factoryList.get(beanName);
			
			if(factory == null){
				throw new IllegalStateException("bean definition not found: " + beanName);
			}
			
			bean = factory.createInstance();
			this.beans.put(alias, bean);
		}
		
		return bean;
	}
	
	private synchronized Set<Object> createReferenceSet(T name){
		Set<Object> referenceSet = references.get(name);
		
		if(referenceSet == null){
			referenceSet = new HashSet<Object>();
			references.put(name, referenceSet);
		}
		
		return referenceSet;
	}
	
	synchronized void destroy(ObjectHandler handler){
		
		T alias                   = handler.alias;
		String beanName           = handler.beanName;
		Object reference          = handler.reference;
		Set<Object> referencesSet = references.get(alias);

		if(referencesSet != null && referencesSet.contains(reference)){
			referencesSet.remove(reference);
			
			if(referencesSet.isEmpty()){
				Object bean = handler.object;
				
				ObjectFactory factory = this.factoryList.get(beanName);
				
				if(factory == null){
					throw new IllegalStateException("bean definition not found: " + beanName);
				}
				
				factory.destroy(bean);
				this.references.remove(alias);
				this.beans.remove(alias);
			}
		}
		
		handler.object = null;
	}
	
	public class ObjectHandler
		implements InvocationHandler {

	public Object reference;
	
	public Object object;

	public T alias;
	
	public String beanName;
	
	public ObjectHandler(T alias, String beanName, Object reference, Object object) {
		this.reference = reference;
		this.object    = object;
		this.alias 	   = alias;
		this.beanName  = beanName;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		try{
			return method.invoke(object, args);
		}
		catch(InvocationTargetException e){
			throw e.getTargetException();
		}
	}

	protected void finalize() throws Throwable{
		try{
			destroy(this);
		}
		finally{
			super.finalize();
		}
	}
}	
	public interface ObjectFactory{
		
		Object createInstance();
		
		void destroy(Object instance);
		
	}
	
}
