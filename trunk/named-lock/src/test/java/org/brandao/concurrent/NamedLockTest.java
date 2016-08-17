package org.brandao.concurrent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class NamedLockTest extends TestCase{

	public void testSimpleLock() throws InterruptedException{
		
		final List<Integer> result = new ArrayList<Integer>();
		
		final NamedLock namedLock = new NamedLock();
		
		Thread th = new Thread(){
			
			public void run(){
				try{
					Thread.sleep(2000);
					Serializable ref = namedLock.lock("teste");
					try{
						result.add(2);
					}
					finally{
						namedLock.unlock(ref, "teste");
					}
					
				}
				catch(Throwable e){
					e.printStackTrace();
				}
			}
			
		};
		
		th.start();
		Serializable ref = namedLock.lock("teste");
		try{
			Thread.sleep(5000);
			result.add(1);
		}
		finally{
			namedLock.unlock(ref, "teste");
		}
		
		Thread.sleep(2000);
		
		TestCase.assertEquals(2, result.size());
		TestCase.assertEquals(new Integer(1), result.get(0));
		TestCase.assertEquals(new Integer(2), result.get(0));
	}
	
	
	public void testMultipleLock() throws InterruptedException{
		
		final List<Integer> result = new ArrayList<Integer>();
		
		final NamedLock namedLock = new NamedLock();
		
		Thread th = new Thread(){
			
			public void run(){
				try{
					Thread.sleep(2000);
					Serializable ref = namedLock.lock("teste2");
					try{
						result.add(2);
					}
					finally{
						namedLock.unlock(ref, "teste2");
					}
					
				}
				catch(Throwable e){
					e.printStackTrace();
				}
			}
			
		};
		
		th.start();
		Serializable ref = namedLock.lock("teste");
		try{
			Thread.sleep(5000);
			result.add(1);
		}
		finally{
			namedLock.unlock(ref, "teste");
		}
		
		Thread.sleep(2000);
		
		TestCase.assertEquals(2, result.size());
		TestCase.assertEquals(new Integer(2), result.get(0));
		TestCase.assertEquals(new Integer(1), result.get(0));
	}
	
	public void testSimpleTryLock() throws InterruptedException{
		
		final List<Integer> result = new ArrayList<Integer>();
		
		final NamedLock namedLock = new NamedLock();
		
		Thread th = new Thread(){
			
			public void run(){
				try{
					Thread.sleep(2000);
					Serializable ref = namedLock.tryLock("teste");
					try{
						if(ref == null)
							result.add(3);
						else
							result.add(2);
					}
					finally{
						namedLock.unlock(ref, "teste");
					}
					
				}
				catch(Throwable e){
					e.printStackTrace();
				}
			}
			
		};
		
		th.start();
		Serializable ref = namedLock.tryLock("teste");
		try{
			Thread.sleep(5000);
			result.add(1);
		}
		finally{
			namedLock.unlock(ref, "teste");
		}
		
		Thread.sleep(2000);
		
		TestCase.assertEquals(2, result.size());
		TestCase.assertEquals(new Integer(3), result.get(0));
		TestCase.assertEquals(new Integer(1), result.get(0));
	}
	
}
