package org.brandao.concurrent;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NamedLockHelper {

	public static class AsyncLock extends Thread{
		
		private NamedLock namedLock;
		
		private String lockName;
		
		private boolean error;
		
		private List<Integer> queue;
		
		public AsyncLock(NamedLock namedLock, 
				String lockName, List<Integer> queue){
			this.namedLock = namedLock;
			this.lockName  = lockName;
			this.queue     = queue;
		}
		
		public void run(){
			try{
				Serializable ref = this.namedLock.lock(this.lockName);
				try{
					this.queue.add(2);
				}
				finally{
					this.namedLock.unlock(ref, this.lockName);
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
		
		private NamedLock namedLock;
		
		private String lockName;
		
		private Throwable error;
		
		private List<Integer> queue;
		
		public AsyncLockInterruptibly(NamedLock namedLock, 
				String lockName, List<Integer> queue){
			this.namedLock     = namedLock;
			this.lockName      = lockName;
			this.queue         = queue;
		}
		
		public void run(){
			try{
				Serializable ref = namedLock.lockInterruptibly(this.lockName);
				try{
					this.queue.add(2);
				}
				finally{
					namedLock.unlock(ref, this.lockName);
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
		
		private NamedLock namedLock;
		
		private String lockName;
		
		private boolean error;
		
		private List<Integer> queue;
		
		public AsyncTryLock(NamedLock namedLock, 
				String lockName, List<Integer> queue){
			this.namedLock = namedLock;
			this.lockName  = lockName;
			this.queue     = queue;
		}
		
		public void run(){
			try{
				Serializable ref = namedLock.tryLock(this.lockName);
				if(ref == null)
					this.queue.add(3);
				else{
					try{
						this.queue.add(2);
					}
					finally{
						namedLock.unlock(ref, this.lockName);
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
		
		private NamedLock namedLock;
		
		private String lockName;
		
		private boolean error;
		
		private List<Integer> queue;
		
		private long time;
		
		private TimeUnit unit;
		
		public AsyncTryLockTime(NamedLock namedLock, 
				String lockName, List<Integer> queue, long time, TimeUnit unit){
			this.namedLock = namedLock;
			this.lockName  = lockName;
			this.queue     = queue;
			this.time      = time;
			this.unit      = unit;
		}
		
		public void run(){
			try{
				Serializable ref = namedLock.tryLock(this.lockName, this.time, this.unit);
				if(ref == null)
					this.queue.add(3);
				else{
					try{
						this.queue.add(2);
					}
					finally{
						namedLock.unlock(ref, this.lockName);
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
