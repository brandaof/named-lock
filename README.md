# Java named lock

### 1 Quick Reference.

The named-lock is a utility for acquiring named locks.

#### 1.1 Named factory.

Main class

```java
 public class Test {

    public static void main(String[] args) throws InterruptedException{
        NamedLockFactory lockFactory = new NamedLockFactory();
        
        System.out.println("start test");

        Lock lock = lockFactory.getLock("lock_name");
        lock.lock();
        try{
            Task task = new Task(lockFactory);
            task.start();
            Thread.sleep(1000);
            System.out.println("1");
        }
        finally{
            lock.unlock();
        }

        Thread.sleep(1000);
        System.out.println("end test");
        
    }

}
```

Task class

```java
 public class Task extends Thread{

    private NamedLockFactory lockFactory;

    public Task(NamedLockFactory lockFactory){
        this.lockFactory = lockFactory;
    }

    public void run(){

        Lock lock = lockFactory.getLock("lock_name");
        lock.lock();
        try{
            System.out.println("2");
        }
        finally{
            lock.unlock();
        }

    }

}
```

output:
```
start test
1
2
end test
```

#### 1.2 Named lock.

```java
NamedLock namedLock = new NamedLock()
Serializable refLock = namedLock.lock("lock_name");
try{
   // manipulate protected state
}
finally{
  namedLock.unlock(refLock, "lock_name");
}
```
