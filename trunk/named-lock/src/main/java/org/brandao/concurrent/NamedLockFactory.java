package org.brandao.concurrent;

import java.util.concurrent.locks.ReadWriteLock;

/**
 * Provê bloqueadores baseados em nomes.
 * Os bloqueadores providos são dos tipos {@link Lock} e {@link ReadWriteLock}.
 * 
 * <pre>
 * ex:
 * 
 *    NamedLockFactory lockFactory = ...;
 *    
 *    Lock lock = lockFactory.getLock("nome_do_lock");
 *    
 *    lock.lock();
 *    
 *    try{
 *       //ações protegidas pelo bloqueio.
 *    }
 *    finally{
 *      lock.unlock();
 *    }
 * 
 * ex2:
 * 
 *    NamedLockFactory lockFactory = ...;
 *    
 *    ReadWriteLock lock = lockFactory.getReadWriteLock("nome_do_lock");
 *    
 *    Lock readLock = lock.readLock();
 *    readLock.lock();
 *    
 *    try{
 *       //ações protegidas pelo bloqueio.
 *    }
 *    finally{
 *      readLock.unlock();
 *    }
 * 
 * </pre>
 * 
 * @author Brandao
 *
 */
public class NamedLockFactory 
	extends LockFactory<String>{

}
