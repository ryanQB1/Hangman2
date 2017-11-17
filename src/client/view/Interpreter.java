package client.view;

import java.util.Scanner;
import client.controller.Controller;
import client.net.OutputHandler;

public class Interpreter implements Runnable {
	
	private static final String PROMPT = "> ";
    private final Scanner console = new Scanner(System.in);
    private final ThreadSafeStdOut outMgr = new ThreadSafeStdOut();
    
    private boolean receivingCmds = false;
    private Controller contr;
    
    public void start() {
        if (receivingCmds) {
            return;
        }
        receivingCmds = true;
        contr = new Controller();
        new Thread(this).start();
    }
    
    @Override
    public void run() {
        while (receivingCmds) {
            try {
                InputParser cmdLine = new InputParser(readNextLine());
                switch (cmdLine.getCmd()) {
                    case QUIT:
                        receivingCmds = false;
                        contr.disconnect();
                        break;
                    case CONNECT:
                        contr.connect(cmdLine.getParam(0),
                                      Integer.parseInt(cmdLine.getParam(1)),
                                      new ConsoleOutput());
                        break;
                    case GUESS:
                    	contr.makeGuess(cmdLine.getParam(0));
                        break;
                    case SCORE:
                    	contr.giveScore();
                    	break;
                    case NEW:
                    	contr.newGame();
                    	break;
                    case NO_COMM:
                        outMgr.println(PROMPT + "Invalid Command");
                        break;
                }
            } catch (Exception e) {
                outMgr.println("Operation failed");
            }
        }
    }
    
    private String readNextLine() {
        outMgr.print(PROMPT);
        return console.nextLine();
    }
    
    private class ConsoleOutput implements OutputHandler {
    	
        @Override
        public void stdPrint(String msg) {
            outMgr.println(msg);
            outMgr.print(PROMPT);
        }
        
        @Override
        public void handleEnd(String msg) {
        	String[] end1 = msg.split("#");
        	if(end1[0].equals("L")) outMgr.print("Aww, you lost!");
        	if(end1[0].equals("W")) outMgr.print("Congratz, you won!");
        	outMgr.println(" The word was " + end1[1]);
        	outMgr.print(PROMPT);
        }
        
        @Override
        public void handleScore(String msg) {
        	String[] sc1 = msg.split("#");
        	outMgr.println("You have played " + sc1[0] + " games and you have won " + sc1[1] + " times");
                outMgr.print(PROMPT);
        }
        
        @Override
        public void handleWord(String msg) {
            if(msg.startsWith("#")){
                switch (msg) {
                    case "#ERROR":
                        outMgr.println("Oops, a unknown error occured");
                        break;
                    case "#GAMENOTSTARTED":
                        outMgr.println("Start the game first with 'new' please");
                        break;
                    case "#NULL":
                        outMgr.println("Can't make an empty guess!");
                        break;
                    default:
                        break;
                }
                outMgr.print(PROMPT);
                return;
            }
            String[] msg2 = msg.split("#");
            outMgr.println("Your guess was " + msg2[0] + " Word: " + msg2[1] + " You have " + msg2[2] + " tries left");
            outMgr.print(PROMPT);
        }
        
        @Override
        public void newWord(String wrd) {
        	outMgr.println("Game Started:" + wrd + "(" + wrd.length() + " letters)");
                outMgr.print(PROMPT);
        }
    }
}
