// create single threaded pool
ExecutorService executorService = Executors.newSingleThreadExecutor()

// create n threaded pool
ExecutorService executorService = Executors.newFixedThreadPool(4);

// cached pool , use for short lived tasks as leads to thrashing , creates new threads if all existing are occupied
ExecutorService executorService = Executors.newCachedThreadPool();

// scheduled executor : for delay or interval based scheduling
ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);

scheduledExecService.scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit);
scheduledExecService.scheduleWithFixedDelay(Runnable command, long initialDelay, long period, TimeUnit unit);

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

        try {
            System.out.println(result.get());
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Error occured while executing the submitted task");
        }
        // waits for shutdown , use shutdownNoww() to immediately return resources to os
        executorService.shutdown();
    }
}
