package server.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.net.Socket;
import Common.MessageException;
import Common.MsgType;
import Common.Message;
import server.controller.*;

public class ClientHandler implements Runnable{
    private final Socket clientSocket;
    
    private boolean connected = false;
    private ObjectInputStream fromClient;
    private ObjectOutputStream toClient;
    private Controller contr = new Controller();
	
	ClientHandler(Socket mclientsocket){
		this.clientSocket = mclientsocket;
		connected = true;
	}
	
	@Override
	public void run() {
        try {
            fromClient = new ObjectInputStream(clientSocket.getInputStream());
            toClient = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
        while (connected) {
            try {
            	String body;
                Message msg = (Message) fromClient.readObject();
                switch (msg.getType()) {
                    case GUESS:
                        body = contr.checkWordOrLetter(msg.getBody());
                        sendMsg(MsgType.WORD,body);
                        if((contr.gotWord() || !contr.enoughGuessesLeft()) && contr.gamStarted()) {
                        	body = contr.endGame();
                        	sendMsg(MsgType.END,body);
                                body = contr.giveScore();
                                sendMsg(MsgType.SCORE,body);
                        }
                        break;
                    case SCORE:
                        body = contr.giveScore();
                        sendMsg(MsgType.SCORE,body);
                        break;
                    case DISCONNECT:
                        removeMe();
                        break;
                    case NEW :
                    	body = contr.newGame();
                    	sendMsg(MsgType.NEW,body);
                    	break;
                    default:
                        throw new MessageException("Received corrupt message: " + msg);
                }
            } catch (IOException | ClassNotFoundException e) {
                removeMe();
                throw new MessageException(e);
            }
        }
    }
	
	private void removeMe() {
		try {
            clientSocket.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        connected = false;
	}
	
	private void sendMsg(MsgType type, String msgBody) throws UncheckedIOException {
        try {
            Message msg = new Message(type, msgBody);
            toClient.writeObject(msg);
            toClient.flush();
            toClient.reset();
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }
}
