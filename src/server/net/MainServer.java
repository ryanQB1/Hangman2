package server.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class MainServer {
	
	private static final int LINGER_TIME = 500000;
    private static final int TIMEOUT_HALF_HOUR = 1800000;
    
    private int portNo = 8080;
	
	public static void main(String[] args) {
        MainServer server = new MainServer();
        server.parseArguments(args);
        server.serve();
    }
	
	private void serve() {
        try {
			ServerSocket listeningSocket = new ServerSocket(portNo);
            while (true) {
                Socket clientSocket = listeningSocket.accept();
                startHandler(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Server failure.");
        }
    }
	
	private void startHandler(Socket clientSocket) throws SocketException {
        clientSocket.setSoLinger(true, LINGER_TIME);
        clientSocket.setSoTimeout(TIMEOUT_HALF_HOUR);
        ClientHandler handler = new ClientHandler(clientSocket);
        Thread handlerThread = new Thread(handler);
        handlerThread.setPriority(Thread.MAX_PRIORITY);
        handlerThread.start();
    }
	
	private void parseArguments(String[] args) {
        if (args.length > 0) {
            try {
                portNo = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number, using default.");
            }
        }
    }
}
