import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.io.File;

public class invokeWorkers implements Runnable
{
    static int secondLevelThreads = 3;
    static File arr[];
    public invokeWorkers(File a[])            // Constructor to get arguments from the main thread
    {
        arr=a;
    }
    int index=-1;
    public invokeWorkers(int a)            // Constructor to get arguments from the main thread
    {
        index =a;

    }

    ExecutorService executorService = Executors.newFixedThreadPool(secondLevelThreads) ;
    
    public void run()
    {
        int j=index;
        for(int i=0; i < secondLevelThreads &&j<arr.length; i++)
        {
            Runnable runnableTask = new sendQuery(arr[j].getName())  ;    //  Pass arg, if any to constructor sendQuery(arg)
            executorService.submit(runnableTask) ;
            j++;
        }

        if(j<arr.length)
        {
            sendQuery s = new sendQuery(arr[j].getName());      // Send queries from current thread
            s.run();
        }
        

        executorService.shutdown()  ;
        try
        {
            if (!executorService.awaitTermination(8, TimeUnit.SECONDS))
            {
                executorService.shutdownNow();
            } 
        } 
        catch (InterruptedException e)
        {
            executorService.shutdownNow();
        }
    }
}
    
