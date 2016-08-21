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
	 * Obtém a instância do bloqueador do tipo {@link Lock} associado à chave.
	 * @param key chave associada ao bloqueador do tipo {@link Lock}.
	 * @return instância do bloqueador do tipo {@link Lock} associado à chave.
	 */
	public Lock getLock(T key){
		return (Lock)disposableSingletonContext.getBean(key, "lock");
	}

	/**
	 * Obtém a instância do bloqueador do tipo {@link ReadWriteLock} associado à chave.
	 * @param key chave associada ao bloqueador do tipo {@link ReadWriteLock}.
	 * @return instância do bloqueador do tipo {@link ReadWriteLock} associado à chave.
	 */
	public ReadWriteLock getReadWriteLock(T key){
		return (ReadWriteLock)disposableSingletonContext.getBean(key, "readWriteLock");
	}
	
}
