package org.brandao.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import junit.framework.TestCase;

public class NamedLockFactoryTest  extends TestCase{

	public void testSimpleLock() throws InterruptedException{
		
		List<Integer> queue = new ArrayList<Integer>();
		NamedLockFactory namedLock = new NamedLockFactory();
		
		Lock lock = namedLock.getLock("teste");
		lock.lock();
		try{
			new Thread(new NamedLockFactoryHelper.AsyncLock(namedLock, "teste", queue)).start();
			Thread.sleep(2000);
			queue.add(1);
		}
		finally{
			lock.unlock();
		}
		
		Thread.sleep(1000);
		
		TestCase.assertEquals(2, queue.size());
		TestCase.assertEquals(new Integer(1), queue.get(0));
		TestCase.assertEquals(new Integer(2), queue.get(1));
	}
	
	
	public void testMultipleLock() throws InterruptedException{
		
		List<Integer> queue = new ArrayList<Integer>();
		NamedLockFactory namedLock = new NamedLockFactory();
		
		Lock lock = namedLock.getLock("teste");
		lock.lock();
		try{
			new Thread(new NamedLockFactoryHelper.AsyncLock(namedLock, "teste2", queue)).start();
			Thread.sleep(2000);
			queue.add(1);
		}
		finally{
			lock.unlock();
		}
		
		Thread.sleep(1000);
		
		TestCase.assertEquals(2, queue.size());
		TestCase.assertEquals(new Integer(2), queue.get(0));
		TestCase.assertEquals(new Integer(1), queue.get(1));
	}
	
	public void testSimpleTryLock() throws InterruptedException{
		
		List<Integer> queue = new ArrayList<Integer>();
		NamedLockFactory namedLock = new NamedLockFactory();
		
		Lock lock = namedLock.getLock("teste");
		lock.lock();
		try{
			new Thread(new NamedLockFactoryHelper.AsyncTryLock(namedLock, "teste", queue)).start();
			Thread.sleep(2000);
			queue.add(1);
		}
		finally{
			lock.unlock();
		}
		
		Thread.sleep(1000);
		
		TestCase.assertEquals(2, queue.size());
		TestCase.assertEquals(new Integer(3), queue.get(0));
		TestCase.assertEquals(new Integer(1), queue.get(1));
	}
	
	public void testMultipleTryLock() throws InterruptedException{
		
		List<Integer> queue = new ArrayList<Integer>();
		NamedLockFactory namedLock = new NamedLockFactory();
		
		Lock lock = namedLock.getLock("teste");
		lock.lock();
		try{
			new Thread(new NamedLockFactoryHelper.AsyncTryLock(namedLock, "teste2", queue)).start();
			Thread.sleep(2000);
			queue.add(1);
		}
		finally{
			lock.unlock();
		}
		
		Thread.sleep(1000);
		
		TestCase.assertEquals(2, queue.size());
		TestCase.assertEquals(new Integer(2), queue.get(0));
		TestCase.assertEquals(new Integer(1), queue.get(1));
	}

	public void testSimpleTryLockTime() throws InterruptedException{
		
		List<Integer> queue = new ArrayList<Integer>();
		NamedLockFactory namedLock = new NamedLockFactory();
		
		Lock lock = namedLock.getLock("teste");
		lock.lock();
		try{
			new Thread(new NamedLockFactoryHelper.AsyncTryLockTime(namedLock, "teste", queue, 3000, TimeUnit.MILLISECONDS)).start();
			Thread.sleep(2000);
			queue.add(1);
		}
		finally{
			lock.unlock();
		}
		
		Thread.sleep(2000);
		
		TestCase.assertEquals(2, queue.size());
		TestCase.assertEquals(new Integer(1), queue.get(0));
		TestCase.assertEquals(new Integer(2), queue.get(1));
	}
	
	public void testMultipleTryLockTime() throws InterruptedException{
		
		List<Integer> queue = new ArrayList<Integer>();
		NamedLockFactory namedLock = new NamedLockFactory();
		
		Lock lock = namedLock.getLock("teste");
		lock.lock();
		try{
			new Thread(new NamedLockFactoryHelper.AsyncTryLockTime(namedLock, "teste2", queue, 3000, TimeUnit.MILLISECONDS)).start();
			Thread.sleep(2000);
			queue.add(1);
		}
		finally{
			lock.unlock();
		}
		
		Thread.sleep(2000);
		
		TestCase.assertEquals(2, queue.size());
		TestCase.assertEquals(new Integer(2), queue.get(0));
		TestCase.assertEquals(new Integer(1), queue.get(1));
		
	}

	public void testSimpleTryLockTimeNotSuccess() throws InterruptedException{
		
		List<Integer> queue = new ArrayList<Integer>();
		NamedLockFactory namedLock = new NamedLockFactory();
		
		Lock lock = namedLock.getLock("teste");
		lock.lock();
		try{
			new Thread(new NamedLockFactoryHelper.AsyncTryLockTime(namedLock, "teste", queue, 500, TimeUnit.MILLISECONDS)).start();
			Thread.sleep(2000);
			queue.add(1);
		}
		finally{
			lock.unlock();
		}
		
		Thread.sleep(2000);
		
		TestCase.assertEquals(2, queue.size());
		TestCase.assertEquals(new Integer(3), queue.get(0));
		TestCase.assertEquals(new Integer(1), queue.get(1));
	}
	
	public void testMultipleTryLockTimeNotSuccess() throws InterruptedException{
		
		List<Integer> queue = new ArrayList<Integer>();
		NamedLockFactory namedLock = new NamedLockFactory();
		
		Lock lock = namedLock.getLock("teste");
		lock.lock();
		try{
			new Thread(new NamedLockFactoryHelper.AsyncTryLockTime(namedLock, "teste2", queue, 500, TimeUnit.MILLISECONDS)).start();
			Thread.sleep(2000);
			queue.add(1);
		}
		finally{
			lock.unlock();
		}
		
		Thread.sleep(2000);
		
		TestCase.assertEquals(2, queue.size());
		TestCase.assertEquals(new Integer(2), queue.get(0));
		TestCase.assertEquals(new Integer(1), queue.get(1));
		
	}
	
	public void testSimpleLockInterruptibly() throws InterruptedException{
		
		List<Integer> queue = new ArrayList<Integer>();
		NamedLockFactory namedLock = new NamedLockFactory();
		
		NamedLockFactoryHelper.AsyncLockInterruptibly task = 
				new NamedLockFactoryHelper.AsyncLockInterruptibly(namedLock, "teste", queue);
		Thread th = new Thread(task);

		Lock lock = namedLock.getLock("teste");
		lock.lock();
		try{
			th.start();
			Thread.sleep(2000);
			queue.add(1);
		}
		finally{
			lock.unlock();
		}
		
		Thread.sleep(2000);
		
		TestCase.assertEquals(2, queue.size());
		TestCase.assertEquals(new Integer(1), queue.get(0));
		TestCase.assertEquals(new Integer(2), queue.get(1));
		
	}
	
	public void testMultipleLockInterruptibly() throws InterruptedException{
		
		List<Integer> queue = new ArrayList<Integer>();
		NamedLockFactory namedLock = new NamedLockFactory();
		
		NamedLockFactoryHelper.AsyncLockInterruptibly task = 
				new NamedLockFactoryHelper.AsyncLockInterruptibly(namedLock, "teste2", queue);
		Thread th = new Thread(task);
		
		Lock lock = namedLock.getLock("teste");
		lock.lock();
		try{
			th.start();
			Thread.sleep(2000);
			queue.add(1);
		}
		finally{
			lock.unlock();
		}
		
		Thread.sleep(2000);
		
		TestCase.assertEquals(2, queue.size());
		TestCase.assertEquals(new Integer(2), queue.get(0));
		TestCase.assertEquals(new Integer(1), queue.get(1));
		
	}
	
	public void testSimpleLockInterruptiblyError() throws InterruptedException{
		
		List<Integer> queue = new ArrayList<Integer>();
		NamedLockFactory namedLock = new NamedLockFactory();
		
		NamedLockFactoryHelper.AsyncLockInterruptibly task = 
				new NamedLockFactoryHelper.AsyncLockInterruptibly(namedLock, "teste", queue);
		Thread th = new Thread(task);
		
		Lock lock = namedLock.getLock("teste");
		lock.lock();
		try{
			th.start();
			Thread.sleep(2000);
			th.interrupt();
			Thread.sleep(2000);
			queue.add(1);
		}
		finally{
			lock.unlock();
		}
		
		Thread.sleep(2000);
		
		TestCase.assertEquals(1, queue.size());
		TestCase.assertEquals(new Integer(1), queue.get(0));
		TestCase.assertTrue(task.getError() instanceof InterruptedException);
		
	}
	
}
