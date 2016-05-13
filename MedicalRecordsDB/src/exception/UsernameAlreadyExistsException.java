package exception;

public class UsernameAlreadyExistsException extends Exception {

private static final long serialVersionUID = 1L;
	
	public UsernameAlreadyExistsException(){
		System.out.println("-----");
		System.out.println("ERROR: Can't create a new user with that username");
		System.out.println("-----");
	}
	
}
