package client.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import Common.Message;
import Common.MsgType;
import Common.MessageException;

public class ServerConnection {
	
	private static final int TIMEOUT_HALF_HOUR = 1800000;
    private static final int TIMEOUT_HALF_MINUTE = 30000;
    
    private Socket socket;
    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;
    private boolean connected;
    
    public void connect(String host, int port, OutputHandler broadcastHandler) throws IOException {
    	socket = new Socket();
    	socket.connect(new InetSocketAddress(host, port), TIMEOUT_HALF_MINUTE);
    	socket.setSoTimeout(TIMEOUT_HALF_HOUR);
    	connected = true;
    	toServer = new ObjectOutputStream(socket.getOutputStream());
    	fromServer = new ObjectInputStream(socket.getInputStream());
    	new Thread(new Listener(broadcastHandler)).start();
    }
    
    public void disconnect() throws IOException {
    	sendMsg(MsgType.DISCONNECT,null);
    	socket.close();
    	socket = null;
    	connected = false;
    }
    
    private void sendMsg(MsgType type, String body) throws IOException{
    	Message msg = new Message(type, body);
    	toServer.writeObject(msg);
    	toServer.flush();
    	toServer.reset();
    }
    
    private class Listener implements Runnable {
    	private final OutputHandler outputHandler;
    	
    	private Listener(OutputHandler outputHandler) {
            this.outputHandler = outputHandler;
        }
    	
    	 @Override
         public void run() {
             try {
                 for (;;) {
                	 Message msg = (Message) fromServer.readObject();
                	 switch(msg.getType()) {
                	 case SCORE :
                		 outputHandler.handleScore(extractMsgBody(msg));
                		 break;
                	 case END :
                		 outputHandler.handleEnd(extractMsgBody(msg));
                		 break;
                	 case WORD :
                		 outputHandler.handleWord(extractMsgBody(msg));
                		 break;
                	 case NEW :
                		 outputHandler.newWord(extractMsgBody(msg));
                		 break;
                	 default :
                		 outputHandler.stdPrint("Unusual packet recieved from server");
                	 }
                 }
             } catch (IOException | ClassNotFoundException connectionFailure) {
                 if (connected) {
                     outputHandler.stdPrint("Lost connection.");
                 }
             }
         }
    	 
    	 private String extractMsgBody(Message msg) {
             if (!(msg.getType() == MsgType.SCORE || msg.getType() == MsgType.WORD || msg.getType() == MsgType.NEW || msg.getType() == MsgType.END)) throw new MessageException("Received wrong message: " + msg);
             return msg.getBody();
         }
    }
    
    public void makeGuess(String guessed) throws IOException{
    	sendMsg(MsgType.GUESS,guessed);
    }
    
    public void GiveScore() throws IOException{
    	sendMsg(MsgType.SCORE,"");
    }
    
    public void newGame() throws IOException {
    	sendMsg(MsgType.NEW,"");
    }
}
