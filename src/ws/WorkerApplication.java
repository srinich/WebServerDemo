package ws;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class WorkerApplication implements Runnable {

	private Socket clientSocket = null;
	String res = "HTTP/1.1 200 OK\n\n"
	             + "<html><body>Welcome to my WebServer. This response is from the Worker Application</body></html>";


	public WorkerApplication(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
/**
 * THis is my worker application. I'm simply responding back with a success message.
 * More logic can be added here.
 */
	public void run() {
		try {
	        BufferedReader in = null;
	        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			OutputStream out = clientSocket.getOutputStream();
			try {
				while (in.readLine().length()!= 0) {
					 /** I need this while loop even though I don't read the input.
					 	 Without this, the NIO is not writing to output stream!
					 **/
				}
			} catch (Exception e) {
				System.out.println("Error reading input from socket stream");
			}
			out.write(res.getBytes());
			out.flush();
			in.close();
			System.out.println("Request processed by worker: " + "\n*******************");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}