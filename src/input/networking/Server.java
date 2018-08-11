package input.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public static final int portNumber=58285;
	
	/*
	 * Client Server Interaction:
	 * 
	 * Server:
	 * Create socket and listen
	 * 	-When someone comes, start them on a new thread, if we have room
	 * 
	 * Server Thread:
	 * Update consists of:
	 * 	-Read input
	 * 	-Send image
	 * 
	 * Client Thread:
	 * Update consists of:
	 * 	-Send input
	 * 	-Read image
	 */
	
	private int maxPlayers;
	private volatile int curPlayers;
	private ServerSocket serverSocket;
	
	//package
	volatile boolean shouldCloseFlag;

	public Server(int maxPlayers) {
		this.maxPlayers=maxPlayers;
		curPlayers=0;
		shouldCloseFlag=false;
		
		new Thread(null, null, "Server Main Thread", 1<<18) {
			public void run() {
				runServer();
			}
		}.start();
	}
	
	private void runServer() {
		try {
			System.out.println("Starting server on port "+portNumber+"...");
			serverSocket=new ServerSocket(portNumber);
			while (!shouldCloseFlag) {
				if (curPlayers>=maxPlayers) {
					Thread.sleep(100);
				}
				else {
					System.out.println("Waiting for client to join...");
					Socket socket=serverSocket.accept();
					System.out.println("Someone joined!");
					new ServerThread(socket, this);
					curPlayers++;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void closeServer() {
		shouldCloseFlag=true;
		try {
			Thread.sleep(500);
			serverSocket.close();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void onPlayerLeft() {
		curPlayers--;
	}
	
}
