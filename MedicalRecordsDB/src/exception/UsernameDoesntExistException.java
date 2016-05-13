package exception;

public class UsernameDoesntExistException extends Exception {

private static final long serialVersionUID = 1L;
	
	public UsernameDoesntExistException(){
		System.out.println("-----");
		System.out.println("ERROR: The username you provided doesn't exist");
		System.out.println("-----");
	}
	
}
