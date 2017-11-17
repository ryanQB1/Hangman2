package server.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class wordComp {
	private final Dictionary dic = new Dictionary();
	private final String MSG_DELIM = "#";
	
	private String chosenWord;
	private char[] currentWord;
	private int guessesLeft = 0;
	private boolean dicMade = false;
	private boolean gameStarted = false;
	private int wins = 0;
	private int totGames = 0;
	
	public String newGame() {
		if(!dicMade) {
			makeDic();
			dicMade = true;
		}
		if(guessesLeft!=0) return "#ERROR";
		if(gameStarted) return "#ERROR";
		chosenWord = dic.givWord().toLowerCase();
		guessesLeft = chosenWord.length();
		currentWord = new char[chosenWord.length()];
		for(int a = 0; a < chosenWord.length() ; a++) {
			currentWord[a] = '-';
		}
		
		gameStarted = true;
		return new String(currentWord);
	}
	
	public String getScoreInfo() {
		return (totGames + MSG_DELIM + wins);
	}
	
	public String guessLetter(String letter) {
		boolean corr = false;
		String winOrLose;
		char[] chosenWord1 = chosenWord.toCharArray();
		char lett = (letter.toLowerCase()).charAt(0);
		for(int i = 0; i < chosenWord1.length ; i++) {
			if(chosenWord1[i]==lett) {
				currentWord[i]=lett;
				corr = true;
			}
		}
		if(corr){
                    return "CORRECT" + MSG_DELIM + ( new String(currentWord)) + MSG_DELIM + guessesLeft;
                }
                else{
                    guessesLeft--;
                    return "INCORRECT" + MSG_DELIM + (new String(currentWord)) + MSG_DELIM + guessesLeft;
                }
	}
	
	public String endGame() {
		String ff;
		totGames++;
		if(completeWord()) {
			wins++;
			ff="W" + MSG_DELIM + chosenWord;
		}
		else {
			ff="L" + MSG_DELIM + chosenWord;
		}
		chosenWord = null;
		currentWord = null;
		guessesLeft = 0;
		gameStarted = false;
		return ff;
	}
	
	public String guessWord(String guessw) {
		if(guessw.equals(chosenWord)) {
			currentWord = chosenWord.toCharArray();
			return "CORRECT" + MSG_DELIM + (chosenWord) + MSG_DELIM + guessesLeft;
		}
                guessesLeft--;
		return "INCORRECT" + MSG_DELIM + (new String(currentWord)) + MSG_DELIM + guessesLeft;
	}
	
	public int getGuessesLeft() {
		return guessesLeft;
	}
	
	private void makeDic() {
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader("words.txt"));
			String sCurrentLine;

			while ((sCurrentLine = reader.readLine()) != null) {
				dic.addWord(sCurrentLine);
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}finally {
			try {
				if(reader!=null) {
					reader.close();
				}
			}
			catch(IOException ex){
				ex.printStackTrace();
			}
		}
	}
	
	public boolean completeWord() {
                if(currentWord==null) return false;
		for(char f : currentWord) {
			if(f=='-') return false;
		}
		return true;
	}
        
        public boolean gamStarted(){
            return gameStarted;
        }
}