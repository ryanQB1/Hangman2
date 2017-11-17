package Common;

public enum MsgType {
	
	//The client guesses a letter or a word
	GUESS,
	
	//the client requests the current score
	SCORE,
	
	//the server returns the correct word and ends the game
	END,
	
	//The client is given positions of the letter
	WORD,
	
	//A New empty word is given
	NEW,
	
	//The client wants to disconnect
	DISCONNECT;
	
	
}
