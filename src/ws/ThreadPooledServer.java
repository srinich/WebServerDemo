package ws;

/**
 * References:
 * 1. https://howtodoinjava.com/core-java/multi-threading/java-thread-pool-executor-example/
 * 2. https://www.ibm.com/developerworks/java/library/j-thread/index.html
 * 
 * 
 */
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPooledServer implements Runnable{

    private int serverPort; // set by constructor
    private ServerSocket serverSocket = null; // Initialized from incoming connection 
    private boolean isServerStopped = false;
    private ExecutorService threadPool ; // set inside constructor
    /**
     * 
     * @param port
     * Constructor accepts port number. This is the port to run the application
     */
    public ThreadPooledServer(int port){
    	threadPool =  new ThreadPoolExecutor(1000, // core thread pool size
							 			    	1000, // maximum thread pool size
							 			    	1, // time to wait before resizing pool
							 			    	TimeUnit.SECONDS, 
							 			    	new ArrayBlockingQueue<Runnable>(1000, true),
							 			    	new ThreadPoolExecutor.CallerRunsPolicy());	
        this.serverPort = port;
    }

    /**
     * Run method accepts the connection and submits a runnable task to the thread executor pool
     */
    public void run(){
		getSocket();
        while(!isServerStopped()){
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            	System.out.println("Accepted Client Connection");
            } catch (Exception e) {
                System.out.println("Server has been stopped!");
            }
            this.threadPool.submit(new WorkerApplication(clientSocket));
        }
        this.threadPool.shutdown();
        System.out.println("Server has been Stopped.") ;

    }
/**
 * Util Methods
 */
	private void getSocket() {
		try {
			this.serverSocket = new ServerSocket(this.serverPort);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    private synchronized boolean isServerStopped() {
        return this.isServerStopped;
    }
/**
 * Though not used in this project, this method must be used to stop the server.
 */
    public synchronized void stopServer(){
        this.isServerStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error closing server", e);
        }
    }
    
/**
 * 
 One critical aspect of the ThreadPoolExecutor class, and of the executors in general, is that you have to end it explicitly. 
 If you don’t do this, the executor will continue its execution and the program won’t end. 
 If the executor doesn’t have tasks to execute, it continues waiting for new tasks and 
 it doesn’t end its execution. A Java application won’t end until all its non-daemon threads 
 finish their execution, so, if you don’t terminate the executor, your application will never end. 
 **/
    private static boolean isValidPort(String port) {
    	int portNumber;
    	try {
    		portNumber= Integer.parseInt(port);
			if (portNumber>1024 && portNumber <65536) 
				return true;
		} catch (NumberFormatException e) {
	    	return false;
		}
    	return false;
    }
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		System.out.println("Welcome to my WebServer. Please enter the port to run the server:");
		String port =  scanner.nextLine();
		while(! isValidPort(port)){
			System.out.println(port + " is not a valid port. Please enter the port to run the server:");
			port =  scanner.nextLine();
		}
		System.out.println("Starting HTTP server on port:" + ". The server can be accessed using URL: " + "http://localhost:"+port);
		System.out.println("Note: This Server will shutdown in 60 seconds");

		scanner.close();
		ThreadPooledServer server = new ThreadPooledServer(Integer.parseInt(port));
		new Thread(server).start();

		try {
		    Thread.sleep(60 * 1000);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		System.out.println("Stopping Server");
		server.stopServer();
	}
}