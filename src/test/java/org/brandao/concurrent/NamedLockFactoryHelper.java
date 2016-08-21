package org.brandao.concurrent;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class NamedLockFactoryHelper {

	public static class AsyncLock extends Thread{
		
		private NamedLockFactory namedLock;
		
		private String lockName;
		
		private boolean error;
		
		private List<Integer> queue;
		
		public AsyncLock(NamedLockFactory namedLock, 
				String lockName, List<Integer> queue){
			this.namedLock = namedLock;
			this.lockName  = lockName;
			this.queue     = queue;
		}
		
		public void run(){
			try{
				Lock lock = this.namedLock.getLock(this.lockName);
				lock.lock();
				try{
					this.queue.add(2);
				}
				finally{
					lock.unlock();
				}
				this.error = false;
			}
			catch(Throwable e){
				error = true;
				e.printStackTrace();
			}
		}

		public boolean isError() {
			return error;
		}
		
	}
	
	public static class AsyncLockInterruptibly extends Thread{
		
		private NamedLockFactory namedLock;
		
		private String lockName;
		
		private Throwable error;
		
		private List<Integer> queue;
		
		public AsyncLockInterruptibly(NamedLockFactory namedLock, 
				String lockName, List<Integer> queue){
			this.namedLock     = namedLock;
			this.lockName      = lockName;
			this.queue         = queue;
		}
		
		public void run(){
			try{
				Lock lock = this.namedLock.getLock(this.lockName);
				lock.lockInterruptibly();
				try{
					this.queue.add(2);
				}
				finally{
					lock.unlock();
				}
			
				this.error = null;
			}
			catch(Throwable e){
				error = e;
				e.printStackTrace();
			}
		}

		public Throwable getError() {
			return error;
		}
		
	}	
	public static class AsyncTryLock extends Thread{
		
		private NamedLockFactory namedLock;
		
		private String lockName;
		
		private boolean error;
		
		private List<Integer> queue;
		
		public AsyncTryLock(NamedLockFactory namedLock, 
				String lockName, List<Integer> queue){
			this.namedLock = namedLock;
			this.lockName  = lockName;
			this.queue     = queue;
		}
		
		public void run(){
			try{
				Lock lock = this.namedLock.getLock(this.lockName);
				lock.tryLock();
				if(!lock.tryLock())
					this.queue.add(3);
				else{
					try{
						this.queue.add(2);
					}
					finally{
						lock.unlock();
					}
				}
				
				this.error = false;
			}
			catch(Throwable e){
				this.error = true;
				e.printStackTrace();
			}
		}

		public boolean isError() {
			return error;
		}
		
	}

	public static class AsyncTryLockTime extends Thread{
		
		private NamedLockFactory namedLock;
		
		private String lockName;
		
		private boolean error;
		
		private List<Integer> queue;
		
		private long time;
		
		private TimeUnit unit;
		
		public AsyncTryLockTime(NamedLockFactory namedLock, 
				String lockName, List<Integer> queue, long time, TimeUnit unit){
			this.namedLock = namedLock;
			this.lockName  = lockName;
			this.queue     = queue;
			this.time      = time;
			this.unit      = unit;
		}
		
		public void run(){
			try{
				Lock lock = this.namedLock.getLock(this.lockName);
				if(!lock.tryLock(this.time, this.unit))
					this.queue.add(3);
				else{
					try{
						this.queue.add(2);
					}
					finally{
						lock.unlock();
					}
				}
				
				this.error = false;
			}
			catch(Throwable e){
				this.error = true;
				e.printStackTrace();
			}
		}

		public boolean isError() {
			return error;
		}
		
	}
	
}
