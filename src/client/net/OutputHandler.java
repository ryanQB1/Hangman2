package client.net;

public interface OutputHandler {
	
	public void handleEnd(String msg);
	
	public void handleScore(String msg);
	
	public void handleWord(String msg);
	
	public void stdPrint(String msg);
	
	public void newWord(String wrd);
}
