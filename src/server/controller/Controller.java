package server.controller;

import server.model.wordComp;

public class Controller {
	
	private final wordComp wrdHandle = new wordComp();
	
	public String giveScore() {
		return wrdHandle.getScoreInfo();
	}
	
	public String endGame() {
		return wrdHandle.endGame();
	}
	
	public String newGame() {
		return wrdHandle.newGame();
	}
	
	public String checkWordOrLetter(String toCheck) {
                if(!wrdHandle.gamStarted()) return "#GAMENOTSTARTED";
                if(toCheck==null) return "#NULL";
		if(toCheck.length()==1) return wrdHandle.guessLetter(toCheck);
		if(toCheck.length()>1) return wrdHandle.guessWord(toCheck);
		return "#ERROR";
	}
	
	public boolean gotWord() {
		return wrdHandle.completeWord();
	}
        
        public boolean enoughGuessesLeft() {
            return (wrdHandle.getGuessesLeft()>0);
        }
        
        public boolean gamStarted() {
            return wrdHandle.gamStarted();
        }
}
