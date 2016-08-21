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

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * É a base para uma fábrica de bloqueios. 
 * Permite obter bloqueios a partir de uma chave. Somente existe um bloqueador
 * assocuado à chave. 
 *  
 * @author Brandão
 *
 * @param <T> Tipo da chave associada a um bloqueador.
 */
public abstract class LockFactory<T> {

	/**
	 * Controla as instâncias dos bloqueadores.
	 */
	private final DisposableSingletonContext<Object> disposableSingletonContext;
	
	/**
	 * Cria uma nova instância da fábrica.
	 */
	public LockFactory(){
		disposableSingletonContext = new DisposableSingletonContext<Object>();
		disposableSingletonContext.registryBeanDefinition("lock", 			new LockObjectFactory());
		disposableSingletonContext.registryBeanDefinition("readWriteLock", 	new ReadWriteLockObjectFactory());
	}
	
	/**
	 * Obtém o bloqueador do tipo {@link Lock} associado à chave.
	 * @param key chave associada ao bloqueador do tipo {@link Lock}.
	 * @return instância do bloqueador do tipo {@link Lock} associado à chave.
	 */
	public Lock getLock(T key){
		return (Lock)disposableSingletonContext.getBean(key, "lock");
	}

	/**
	 * Obtém o bloqueador do tipo {@link ReadWriteLock} associado à chave.
	 * @param key chave associada ao bloqueador do tipo {@link ReadWriteLock}.
	 * @return instância do bloqueador do tipo {@link ReadWriteLock} associado à chave.
	 */
	public ReadWriteLock getReadWriteLock(T key){
		return (ReadWriteLock)disposableSingletonContext.getBean(key, "readWriteLock");
	}
	
}
