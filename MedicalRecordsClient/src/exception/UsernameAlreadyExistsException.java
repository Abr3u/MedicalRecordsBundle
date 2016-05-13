package exception;

public class UsernameAlreadyExistsException extends Exception {

private static final long serialVersionUID = 1L;
	
	public UsernameAlreadyExistsException(){
		System.out.println("-----");
		System.out.println("ERROR: Can't create a new user with that username");
		System.out.println("-----");
	}
	
	@Override
	public String getMessage(){
		String erro = new String();
		erro += "-----\n";
		erro += "ERROR: Can't create a new user with that username\n";
		erro += "-----";
		return erro;
	}
	
}
