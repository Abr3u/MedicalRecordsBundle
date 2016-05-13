package exception;

public class CantLoginException extends Exception {

private static final long serialVersionUID = 1L;
	
	public CantLoginException(){
		System.out.println("-----");
		System.out.println("ERROR: The credentials you provided weren't correct");
		System.out.println("-----");
	}
	
}
