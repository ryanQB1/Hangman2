package client.controller;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;
import client.net.ServerConnection;
import client.net.OutputHandler;

public class Controller {
	
	private final ServerConnection serverConnection = new ServerConnection();
	
	public void connect(String host, int port, OutputHandler outputHandler) {
        CompletableFuture.runAsync(() -> {
            try {
                serverConnection.connect(host, port, outputHandler);
            } catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        }).thenRun(() -> outputHandler.stdPrint("Connected to " + host + ":" + port));
    }
	
	public void disconnect() throws IOException {
        serverConnection.disconnect();
    }
	
	public void makeGuess(String GuessedString) throws IOException {
		serverConnection.makeGuess(GuessedString);
	}
	
	public void giveScore() throws IOException {
		serverConnection.GiveScore();
	}
	
	public void newGame() throws IOException {
		serverConnection.newGame();
	}
}
