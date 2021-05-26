// create single threaded pool
ExecutorService executorService = Executors.newSingleThreadExecutor()

// create n threaded pool
ExecutorService executorService = Executors.newFixedThreadPool(4);

// cached pool , use for short lived tasks as leads to thrashing , creates new threads if all existing are occupied
ExecutorService executorService = Executors.newCachedThreadPool();

// scheduled executor : for delay or interval based scheduling
ScheduledExecutorService scheduledExecService = Executors.newScheduledThreadPool(4);

scheduledExecService.scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit);
scheduledExecService.scheduleWithFixedDelay(Runnable command, long initialDelay, long period, TimeUnit unit);

--------------------------------------------------------------------
Callable -> returns a Future 
Runnable -> return void/null  
  
// Callable example 
public class Task implements Callable<String> {

    private String message;

    public Task(String message) {
        this.message = message;
    }

    @Override
    public String call() throws Exception {
        return "Hello " + message + "!";
    }
}
// executor using this callable
public class ExecutorExample {
    public static void main(String[] args) {

      Task task = new Task("World");

      ExecutorService executorService = Executors.newFixedThreadPool(4);
      Future<String> result = executorService.submit(task);
      //  using lambda can be done as follows 
      //  executorService.submit(() -> System.out.println("I'm Runnable task."));
      //  Future<Integer> result = executor.submit(() -> { System.out.println("I'm Callable task."); return 1 + 1;});


      try {
          System.out.println(result.get());
      } catch (InterruptedException | ExecutionException e) {
          System.out.println("Error occured while executing the submitted task");
      }
      // waits for shutdown , use shutdownNoww() to immediately return resources to os
      executorService.shutdown();
        
      // list of callable eg
      List<Callable<Integer>> listOfCallable = Arrays.asList(() -> 1, () -> 2, () -> 3);
      List<Future<Integer>> futures = executor.invokeAll(listOfCallable);
      
    }
}

--------------------------------------------------------------------
// ScheduleExecutor eg
public class ScheduledExecutorCallable {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        private int count = 0;
        ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
        Callable<Integer> task2 = () -> { return ++count};

        //run this task after 5 seconds, nonblock for task3, returns a future
        ScheduledFuture<Integer> schedule = ses.schedule(task2, 5, TimeUnit.SECONDS);
        // init Delay = 5, repeat the task every 1 second
        ScheduledFuture<Integer> scheduledFuture = ses.scheduleAtFixedRate(task2, 5, 1, TimeUnit.SECONDS);
        // to cancel the future. 
        while(1) { Thread.sleep(1000); if(count ==  5){ scheduledFuture.cancel(true);} }
        
        // block and get the result
        System.out.println(schedule.get());
        ses.shutdown();
    }
}

--------------------------------------------------------------------
//  While writing Thread Safe programs , use volatile field for reading and locks for writing . it makes sure that the threads always see the current values
// Atomic :  An operation is atomic when you can safely perform the operation in parallel on multiple threads without using the synchronized keyword or locks 
// Internally, the atomic classes make heavy use of compare-and-swap (CAS), an atomic instruction directly supported by most modern CPUs. 
// Those instructions usually are much faster than synchronizing via locks

// eg using atomic operations, outputs 2000
AtomicInteger atomicInt = new AtomicInteger(0);

IntStream.range(0, 1000)
    .forEach(i -> {
        Runnable task = () ->
            atomicInt.updateAndGet(n -> n + 2);
            // 0 + 2 , 2 + 2 , 4 + 2  ...
        executor.submit(task);
    }); 

--------------------------------------------------------------------
// ThreadLocal usage 
class RunnableDemo implements Runnable {
   int counter;
   ThreadLocal<Integer> threadLocalCounter = new ThreadLocal<Integer>();

   public void run() {     
      counter++;

      if(threadLocalCounter.get() != null) {
         threadLocalCounter.set(threadLocalCounter.get().intValue() + 1);
      } else {
         threadLocalCounter.set(0);
      }
      System.out.println("Counter: " + counter);
      System.out.println("threadLocalCounter: " + threadLocalCounter.get());
   }
}

RunnableDemo commonInstance = new RunnableDemo();
Thread t1 = new Thread(commonInstance);
Thread t2 = new Thread(commonInstance);
// counter will increase but threadlocalcounter will not
--------------------------------------------------------------------
 // lock demo . similar to synchornized block but can lock unlock across methods , also provides option to time
 //  ReentrantLock class allows a thread to lock a method even if it already have the lock on other method
 private final Lock queueLock = new ReentrantLock();
 queueLock.lock();
 queueLock.unlock();
 private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
 // is no thread has acquired readWriteLock , multiple can acquire readLock(). only 1 write lock
 lock.readLock().lock();
 lock.writeLock().lock();
