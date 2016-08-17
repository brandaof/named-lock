package org.brandao.concurrent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

public class NamedLockTest extends TestCase{

	public void testSimpleLock() throws InterruptedException{
		
		List<Integer> queue = new ArrayList<Integer>();
		NamedLock namedLock = new NamedLock();
		
		Serializable ref = namedLock.lock("teste");
		try{
			new Thread(new NamedLockHelper.AsyncLock(namedLock, "teste", queue)).start();
			Thread.sleep(2000);
			queue.add(1);
		}
		finally{
			namedLock.unlock(ref, "teste");
		}
		
		Thread.sleep(1000);
		
		TestCase.assertNull(namedLock.locks.get("teste"));
		TestCase.assertNull(namedLock.origins.get("teste"));
		
		TestCase.assertEquals(0, namedLock.locks.size());
		TestCase.assertEquals(0, namedLock.origins.size());
		
		TestCase.assertEquals(2, queue.size());
		TestCase.assertEquals(new Integer(1), queue.get(0));
		TestCase.assertEquals(new Integer(2), queue.get(1));
	}
	
	
	public void testMultipleLock() throws InterruptedException{
		
		List<Integer> queue = new ArrayList<Integer>();
		NamedLock namedLock = new NamedLock();
		
		Serializable ref = namedLock.lock("teste");
		try{
			new Thread(new NamedLockHelper.AsyncLock(namedLock, "teste2", queue)).start();
			Thread.sleep(2000);
			queue.add(1);
		}
		finally{
			namedLock.unlock(ref, "teste");
		}
		
		Thread.sleep(1000);
		
		TestCase.assertNull(namedLock.locks.get("teste"));
		TestCase.assertNull(namedLock.origins.get("teste"));
		
		TestCase.assertEquals(0, namedLock.locks.size());
		TestCase.assertEquals(0, namedLock.origins.size());
		
		TestCase.assertEquals(2, queue.size());
		TestCase.assertEquals(new Integer(2), queue.get(0));
		TestCase.assertEquals(new Integer(1), queue.get(1));
	}
	
	public void testSimpleTryLock() throws InterruptedException{
		
		List<Integer> queue = new ArrayList<Integer>();
		NamedLock namedLock = new NamedLock();
		
		Serializable ref = namedLock.lock("teste");
		try{
			new Thread(new NamedLockHelper.AsyncTryLock(namedLock, "teste", queue)).start();
			Thread.sleep(2000);
			queue.add(1);
		}
		finally{
			namedLock.unlock(ref, "teste");
		}
		
		Thread.sleep(1000);
		
		TestCase.assertNull(namedLock.locks.get("teste"));
		TestCase.assertNull(namedLock.origins.get("teste"));
		
		TestCase.assertEquals(0, namedLock.locks.size());
		TestCase.assertEquals(0, namedLock.origins.size());
		
		TestCase.assertEquals(2, queue.size());
		TestCase.assertEquals(new Integer(3), queue.get(0));
		TestCase.assertEquals(new Integer(1), queue.get(1));
	}
	
	public void testMultipleTryLock() throws InterruptedException{
		
		List<Integer> queue = new ArrayList<Integer>();
		NamedLock namedLock = new NamedLock();
		
		Serializable ref = namedLock.lock("teste");
		try{
			new Thread(new NamedLockHelper.AsyncTryLock(namedLock, "teste2", queue)).start();
			Thread.sleep(2000);
			queue.add(1);
		}
		finally{
			namedLock.unlock(ref, "teste");
		}
		
		Thread.sleep(1000);
		
		TestCase.assertNull(namedLock.locks.get("teste"));
		TestCase.assertNull(namedLock.origins.get("teste"));
		
		TestCase.assertEquals(0, namedLock.locks.size());
		TestCase.assertEquals(0, namedLock.origins.size());
		
		TestCase.assertEquals(2, queue.size());
		TestCase.assertEquals(new Integer(2), queue.get(0));
		TestCase.assertEquals(new Integer(1), queue.get(1));
	}

	public void testSimpleTryLockTime() throws InterruptedException{
		
		List<Integer> queue = new ArrayList<Integer>();
		NamedLock namedLock = new NamedLock();
		
		Serializable ref = namedLock.lock("teste");
		try{
			new Thread(new NamedLockHelper.AsyncTryLockTime(namedLock, "teste", queue, 3000, TimeUnit.MILLISECONDS)).start();
			Thread.sleep(2000);
			queue.add(1);
		}
		finally{
			namedLock.unlock(ref, "teste");
		}
		
		Thread.sleep(2000);
		
		TestCase.assertNull(namedLock.locks.get("teste"));
		TestCase.assertNull(namedLock.origins.get("teste"));
		
		TestCase.assertEquals(0, namedLock.locks.size());
		TestCase.assertEquals(0, namedLock.origins.size());
		
		TestCase.assertEquals(2, queue.size());
		TestCase.assertEquals(new Integer(1), queue.get(0));
		TestCase.assertEquals(new Integer(2), queue.get(1));
	}
	
	public void testMultipleTryLockTime() throws InterruptedException{
		
		List<Integer> queue = new ArrayList<Integer>();
		NamedLock namedLock = new NamedLock();
		
		Serializable ref = namedLock.lock("teste");
		try{
			new Thread(new NamedLockHelper.AsyncTryLockTime(namedLock, "teste2", queue, 3000, TimeUnit.MILLISECONDS)).start();
			Thread.sleep(2000);
			queue.add(1);
		}
		finally{
			namedLock.unlock(ref, "teste");
		}
		
		Thread.sleep(2000);
		
		TestCase.assertNull(namedLock.locks.get("teste"));
		TestCase.assertNull(namedLock.origins.get("teste"));
		
		TestCase.assertEquals(0, namedLock.locks.size());
		TestCase.assertEquals(0, namedLock.origins.size());
		
		TestCase.assertEquals(2, queue.size());
		TestCase.assertEquals(new Integer(2), queue.get(0));
		TestCase.assertEquals(new Integer(1), queue.get(1));
		
	}

	public void testSimpleTryLockTimeNotSuccess() throws InterruptedException{
		
		List<Integer> queue = new ArrayList<Integer>();
		NamedLock namedLock = new NamedLock();
		
		Serializable ref = namedLock.lock("teste");
		try{
			new Thread(new NamedLockHelper.AsyncTryLockTime(namedLock, "teste", queue, 500, TimeUnit.MILLISECONDS)).start();
			Thread.sleep(2000);
			queue.add(1);
		}
		finally{
			namedLock.unlock(ref, "teste");
		}
		
		Thread.sleep(2000);
		
		TestCase.assertNull(namedLock.locks.get("teste"));
		TestCase.assertNull(namedLock.origins.get("teste"));
		
		TestCase.assertEquals(0, namedLock.locks.size());
		TestCase.assertEquals(0, namedLock.origins.size());
		
		TestCase.assertEquals(2, queue.size());
		TestCase.assertEquals(new Integer(3), queue.get(0));
		TestCase.assertEquals(new Integer(1), queue.get(1));
	}
	
	public void testMultipleTryLockTimeNotSuccess() throws InterruptedException{
		
		List<Integer> queue = new ArrayList<Integer>();
		NamedLock namedLock = new NamedLock();
		
		Serializable ref = namedLock.lock("teste");
		try{
			new Thread(new NamedLockHelper.AsyncTryLockTime(namedLock, "teste2", queue, 500, TimeUnit.MILLISECONDS)).start();
			Thread.sleep(2000);
			queue.add(1);
		}
		finally{
			namedLock.unlock(ref, "teste");
		}
		
		Thread.sleep(2000);
		
		TestCase.assertNull(namedLock.locks.get("teste"));
		TestCase.assertNull(namedLock.origins.get("teste"));
		
		TestCase.assertEquals(0, namedLock.locks.size());
		TestCase.assertEquals(0, namedLock.origins.size());
		
		TestCase.assertEquals(2, queue.size());
		TestCase.assertEquals(new Integer(2), queue.get(0));
		TestCase.assertEquals(new Integer(1), queue.get(1));
		
	}
	
	public void testSimpleLockInterruptibly() throws InterruptedException{
		
		List<Integer> queue = new ArrayList<Integer>();
		NamedLock namedLock = new NamedLock();
		
		Serializable ref = namedLock.lock("teste");
		NamedLockHelper.AsyncLockInterruptibly task = 
				new NamedLockHelper.AsyncLockInterruptibly(namedLock, "teste", queue);
		Thread th = new Thread(task);
		try{
			th.start();
			Thread.sleep(2000);
			queue.add(1);
		}
		finally{
			namedLock.unlock(ref, "teste");
		}
		
		Thread.sleep(2000);
		
		TestCase.assertNull(namedLock.locks.get("teste"));
		TestCase.assertNull(namedLock.origins.get("teste"));
		
		TestCase.assertEquals(0, namedLock.locks.size());
		TestCase.assertEquals(0, namedLock.origins.size());
		
		TestCase.assertEquals(2, queue.size());
		TestCase.assertEquals(new Integer(1), queue.get(0));
		TestCase.assertEquals(new Integer(2), queue.get(1));
		
	}
	
	public void testMultipleLockInterruptibly() throws InterruptedException{
		
		List<Integer> queue = new ArrayList<Integer>();
		NamedLock namedLock = new NamedLock();
		
		Serializable ref = namedLock.lock("teste");
		NamedLockHelper.AsyncLockInterruptibly task = 
				new NamedLockHelper.AsyncLockInterruptibly(namedLock, "teste2", queue);
		Thread th = new Thread(task);
		try{
			th.start();
			Thread.sleep(2000);
			queue.add(1);
		}
		finally{
			namedLock.unlock(ref, "teste");
		}
		
		Thread.sleep(2000);
		
		TestCase.assertNull(namedLock.locks.get("teste"));
		TestCase.assertNull(namedLock.origins.get("teste"));
		
		TestCase.assertEquals(0, namedLock.locks.size());
		TestCase.assertEquals(0, namedLock.origins.size());
		
		TestCase.assertEquals(2, queue.size());
		TestCase.assertEquals(new Integer(2), queue.get(0));
		TestCase.assertEquals(new Integer(1), queue.get(1));
		
	}
	
	public void testSimpleLockInterruptiblyError() throws InterruptedException{
		
		List<Integer> queue = new ArrayList<Integer>();
		NamedLock namedLock = new NamedLock();
		
		Serializable ref = namedLock.lock("teste");
		NamedLockHelper.AsyncLockInterruptibly task = 
				new NamedLockHelper.AsyncLockInterruptibly(namedLock, "teste", queue);
		Thread th = new Thread(task);
		try{
			th.start();
			Thread.sleep(2000);
			th.interrupt();
			Thread.sleep(2000);
			queue.add(1);
		}
		finally{
			namedLock.unlock(ref, "teste");
		}
		
		Thread.sleep(2000);
		
		TestCase.assertNull(namedLock.locks.get("teste"));
		TestCase.assertNull(namedLock.origins.get("teste"));
		
		TestCase.assertEquals(0, namedLock.locks.size());
		TestCase.assertEquals(0, namedLock.origins.size());
		
		TestCase.assertEquals(1, queue.size());
		TestCase.assertEquals(new Integer(1), queue.get(0));
		TestCase.assertTrue(task.getError() instanceof InterruptedException);
		
	}
	
}
