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
 *    NamedLock namedLock = new NamedLock();
 *    Serializable lock = namedLock.lock("meu_identificador");
 *    try{
 *       //instruções protegidas pelo bloqueio
 *    }
 *    finally{
 *       namedLock.unlock(lock, "meu_identificador");
 *    }
 *    
 * </pre>
 * 
 * @author Brandao
 *
 */
public class NamedLock {

	/**
	 * A referência de um bloqueio é necessário para se evitar problemas de sincronia na liberação 
	 * da instância do Lock.
	 * Um contador não pode ser usado porque assim não seria possível identificar a origem e fazer
	 * a liberação da instância do Lock de forma segura. 
	 */
	private Map<String,Set<UUID>> origins;

	/**
	 * Lock associado a um determinado nome.
	 */
	private Map<String,Lock> locks;

	/**
	 * Lock global usado para sincronizar a aquisição e liberação dos Locks reais.
	 */
	private Lock _lock;
	
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
	 * Adquire um bloqueio com um determinado nome a menos que a thread atual for interrompida.
	 * @param lockName nome do bloqueio.
	 * @return identificação única do bloqueio associado ao nome.
	 * @throws InterruptedException Lançada se a thread atual for interrompida enquanto se está tentando
	 * obter o bloqueio.
	 */
    public Serializable lockInterruptibly(String lockName) throws InterruptedException{
		UUID ref  = UUID.randomUUID();
		Lock lock = this.getLock(ref, lockName);
		
		lock.lockInterruptibly();
		
		return ref;
    }

    /**
     * Tenta adquirir o bloqueio somente se ele estiver livre no momento da invocação.
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
	 * @param lockName nome do bloqueio.
	 * @param time tempo mázimo de espera para adquirir o bloqueio.
	 * @param unit unidade de tempo do argumento {@code time}.
	 * @return identificação única do bloqueio associado ao nome ou <code>null</code> se 
	 * o bloqueio não for.
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
			
			if(originSet.isEmpty() && this.locks.remove(lockName) == null){
				throw new IllegalStateException("lock not found: " + lockName + ": " + ref);
			}
			else{
				this.origins.remove(lockName);
			}
		}
		finally{
			_lock.unlock();
		}
	}

}
