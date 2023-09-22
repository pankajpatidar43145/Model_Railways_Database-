import java.util.concurrent.ExecutorService ;
import java.util.concurrent.Executors   ;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;
import java.io.IOException  ;
import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;





class sendQuery implements Runnable 
{   /**********************/
     int sockPort = 7008 ;
    /*********************/
    String s;
    sendQuery(String a)
    {
     // Red args if any
     s=a;
    }   
    
    @Override
    public void run()
    {
        try 
        {
            //Creating a client socket to send query requests
            Socket socketConnection = new Socket("localhost", sockPort) ;
            String inputfile = "./Input/" + s;
            String outputfile = "./Output/" + s ;

            OutputStreamWriter outputStream = new OutputStreamWriter(socketConnection.getOutputStream());
            BufferedWriter bufferedOutput = new BufferedWriter(outputStream);
            InputStreamReader inputStream = new InputStreamReader(socketConnection.getInputStream());
            BufferedReader bufferedInput = new BufferedReader(inputStream);
            PrintWriter printWriter = new PrintWriter(bufferedOutput,true);
            File queries = new File(inputfile); 
            File output = new File(outputfile); 
            FileWriter filewriter = new FileWriter(output);
            Scanner queryScanner = new Scanner(queries);
            String query = "";
            while(queryScanner.hasNextLine())
            {
                query = queryScanner.nextLine();
                printWriter.println(query);
            }


            System.out.println("Transfering file --- " + s );

            String result;
            while( (result = bufferedInput.readLine()) != null)
            {
                filewriter.write(result + "\n");
            }    
            filewriter.close();
            queryScanner.close();
            printWriter.close();
            socketConnection.close();
        } 
        catch (IOException e1)
        {
            e1.printStackTrace();
        }   
    }
}


    

public class client
{

    static Lock lock  = new ReentrantLock();
    static File arr[];
    static int Index=-1;

    public static void main(String args[])throws IOException
    {

        String maindirpath = "./Input/";
        // File object
        File maindir = new File(maindirpath);
        
        arr=maindir.listFiles();
        int thread_in_secondlevel=4;

        int firstLevelThreads =  Runtime.getRuntime().availableProcessors();
        ExecutorService executorService =Executors.newFixedThreadPool(firstLevelThreads);
        int i=0;
        Runnable r = new invokeWorkers(arr); 

        while(i<arr.length)
        {
            Runnable runnableTask = new invokeWorkers(i);    //  Pass arg, if any to constructor sendQuery(arg)
            executorService.submit(runnableTask) ;
            i+=thread_in_secondlevel;
        }

        try
        {    // Wait for 8 sec and then exit the executor service
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS))
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
