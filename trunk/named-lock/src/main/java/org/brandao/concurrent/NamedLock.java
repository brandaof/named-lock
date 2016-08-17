/*
 * jBRGates http://namedlock.brandao.org/
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

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Provê operações de bloqueio baseado em nomes.
 * Ele tenta oferecer quase todos os recursos encontrados
 * em {@link Lock}.
 * 
 * <p>Para iniciar um bloqueio, deve-se usar o método {@link #lock(String)},
 * {@link #lockInterruptibly(String)} ou {@link #tryLock(String)}, e
 * para desbloquear deve-se usar o método {@link #unlock(Serializable, String)}</p>
 * 
 * <p>Os métodos de bloqueio provêem uma referência que deve ser usado no método de
 * desbloqueio</p>
 * 
 * <pre>
 * ex:
 * 
 *    NamedLock namedLock = ...;
 *    Serializable refLock = namedLock.lock("nome_do_lock");
 *    try{
 *       //ações protegidas pelo bloqueio.
 *    }
 *    finally{
 *       namedLock.unlock(refLock, "nome_do_lock");
 *    }
 *    
 * </pre>
 * 
 * @author Brandao
 *
 */
public class NamedLock {

	/**
	 * A referência de um bloqueio é necessário para se evitar problemas de sincronização na liberação 
	 * da instância de um {@link Lock}.
	 * <p>Um contador não pode ser usado porque assim não seria possível identificar a origem e fazer
	 * a liberação da instância do {@link Lock} de forma segura.</p> 
	 * Usando a estratégia de armazenar a referência consome-se mais memória em relação ao uso de um
	 * contador, mas nesse caso o mais importante é a confiabilidade.
	 */
	protected Map<String,Set<UUID>> origins;

	/**
	 * Lock associado a um determinado nome.
	 */
	protected Map<String,Lock> locks;

	/**
	 * Bloqueio global usado para sincronizar a aquisição e liberação dos bloqueios reais.
	 */
	protected Lock _lock;
	
	/**
	 * Cria uma nova instância.
	 */
	public NamedLock(){
		this.origins = new HashMap<String, Set<UUID>>();
		this.locks   = new HashMap<String, Lock>();
		this._lock   = new ReentrantLock();
	}
	
	/**
	 * Adquire um bloqueio com um determinado nome.
	 * 
	 * <p>Um uso típico desse método seria:
	 * <pre>
	 *     NamedLock namedLock = ...;
	 *     Serializable lockRef = namedLock.lock("nome_do_lock");
	 *     try{
	 *         //ações protegidas pelo bloqueio.
	 *     }
	 *     finally{
	 *         namedLock.unlock(lockRef, "nome_do_lock");
	 *     }
	 * </pre>
	 * </p>
	 * @param lockName nome do bloqueio.
	 * @return identificação única do bloqueio associado ao nome.
	 */
	public Serializable lock(String lockName){
		
		UUID ref  = UUID.randomUUID();
		Lock lock = this.getLock(ref, lockName);
		
		lock.lock();
		
		return ref;
	}

	/**
	 * Adquire um bloqueio com um determinado nome a menos que a thread atual seja interrompida.
	 * <p>Um uso típico desse método seria:
	 * <pre>
	 *     NamedLock namedLock = ...;
	 *     Serializable lockRef = namedLock.lockInterruptibly("nome_do_lock");
	 *     try{
	 *         //ações protegidas pelo bloqueio.
	 *     }
	 *     finally{
	 *         namedLock.unlock(lockRef, "nome_do_lock");
	 *     }
	 * </pre>
	 * @param lockName nome do bloqueio.
	 * @return identificação única do bloqueio associado ao nome.
	 * @throws InterruptedException Lançada se a thread atual for interrompida enquanto se está tentando
	 * obter o bloqueio.
	 */
    public Serializable lockInterruptibly(String lockName) throws InterruptedException{
		UUID ref  = UUID.randomUUID();
		Lock lock = this.getLock(ref, lockName);
		
		try{
			lock.lockInterruptibly();
		}
		catch(InterruptedException e){
			this.releaseLock(ref, lockName);
			throw e;
		}
		catch(Throwable e){
			this.releaseLock(ref, lockName);
			throw new InterruptedException("bug: " + e.toString());
		}
		
		return ref;
    }

    /**
     * Tenta adquirir o bloqueio somente se ele estiver livre no momento da invocação.
	 * <p>Um uso típico desse método seria:
	 * <pre>
	 *     NamedLock namedLock = ...;
	 *     Serializable lockRef;
	 *     if((lockRef = namedLock.tryLock("nome_do_lock")) != null){
	 *         try{
	 *             //ações protegidas pelo bloqueio.
	 *          }
	 *     	    finally{
	 *     	        namedLock.unlock(lockRef, "nome_do_lock");
	 *          }
	 *     }
	 *     else{
	 *         //ações alternativas.
	 *     }
	 * </pre>
	 * @param lockName nome do bloqueio.
	 * @return identificação única do bloqueio associado ao nome.
     */
	public Serializable tryLock(String lockName){
		
		UUID ref  = UUID.randomUUID();
		Lock lock = this.getLock(ref, lockName);
		
		if(lock.tryLock()){
			return ref;
		}
		else{
			this.releaseLock(ref, lockName);
			return null;
		}
	}
    
	/**
     * Tenta adquirir o bloqueio somente se ele estiver livre dentro de um determinado 
     * prazo de tempo e a thread atual não for interrompida.
	 * <p>Um uso típico desse método seria:
	 * <pre>
	 *     NamedLock namedLock = ...;
	 *     Serializable lockRef;
	 *     if((lockRef = namedLock.tryLock("nome_do_lock", 1200, TimeUnit.MILLISECONDS)) != null){
	 *         try{
	 *             //ações protegidas pelo bloqueio.
	 *          }
	 *     	    finally{
	 *     	        namedLock.unlock(lockRef, "nome_do_lock");
	 *          }
	 *     }
	 *     else{
	 *         //ações alternativas.
	 *     }
	 * </pre>
	 * @param lockName nome do bloqueio.
	 * @param time tempo máximo de espera para adquirir o bloqueio.
	 * @param unit unidade de tempo do argumento {@code time}.
	 * @return identificação única do bloqueio associado ao nome ou <code>null</code> se 
	 * o bloqueio não for obtido dentro do prazo determinado.
	 * @throws InterruptedException Lançada se a thread atual for interrompida enquanto se está tentando
	 * obter o bloqueio.
	 */
	public Serializable tryLock(String lockName, long time, TimeUnit unit) throws InterruptedException{
		
		UUID ref  = UUID.randomUUID();
		Lock lock = this.getLock(ref, lockName);
		
		try{
			if(lock.tryLock(time, unit)){
				return ref;
			}
			else{
				this.releaseLock(ref, lockName);
				return null;
			}
		}
		catch(InterruptedException e){
			try{
				this.releaseLock(ref, lockName);
			}
			catch(Throwable x){
				throw new InterruptedException("bug: " + x.toString());
			}
			
			throw e;
		}
	}
	
	/**
	 * Libera o bloqueio com um determinado nome e referência.
	 * @param ref identificação única do bloqueio associado ao nome.
	 * @param lockName nome do bloqueio.
	 */
	public void unlock(Serializable ref, String lockName){
		Lock lock = this.locks.get(lockName);
		
		if(lock == null){
			throw new IllegalStateException("lock not found: " + lockName + ": " + ref);
		}
		
		lock.unlock();
		
		this.releaseLock(ref, lockName);
	}
	
	private Lock getLock(UUID ref, String lockName){
		_lock.lock();
		try{
			Set<UUID> originSet = this.origins.get(lockName);
			Lock lock = this.locks.get(lockName);
			
			if(originSet == null){
				originSet = new HashSet<UUID>();
				this.origins.put(lockName, originSet);
				
				if(lock != null){
					throw new IllegalStateException("bug!");
				}
				
				lock = new ReentrantLock();
				this.locks.put(lockName, lock);
			}
			originSet.add(ref);
			return lock;
		}
		finally{
			_lock.unlock();
		}
	}
	
	private void releaseLock(Serializable ref, String lockName){
		_lock.lock();
		try{
			Set<UUID> originSet = this.origins.get(lockName);
			
			if(originSet == null){
				throw new IllegalStateException("invalid lock reference: " + lockName + ": " + ref);
			}
			
			if(!originSet.remove(ref)){
				throw new IllegalStateException("lock reference not found: " + lockName + ": " + ref);
			}

			if(originSet.isEmpty()){
				 if(this.locks.remove(lockName) != null){
				 	if(this.origins.remove(lockName) == null){
						 throw new IllegalStateException("origins can not be empty: " + lockName + ": " + ref);
				 	}
				 }
				 else{
					 throw new IllegalStateException("lock not found: " + lockName + ": " + ref);
				 }
			}
			
		}
		finally{
			_lock.unlock();
		}
	}

}
