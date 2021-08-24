package potatocoin;

public class GnocciGangException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GnocciGangException() {
		
	}
	
	public void printStackTrace() {
		System.out.println("Mindesten einer von den Membern is ein Loser und nicht in der Gnocci-Gang");
	}

}
