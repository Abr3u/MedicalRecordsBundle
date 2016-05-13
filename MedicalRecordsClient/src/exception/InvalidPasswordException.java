package exception;

public class InvalidPasswordException extends Exception{

private static final long serialVersionUID = 1L;
	
	public InvalidPasswordException(){
		System.out.println("-----");
		System.out.println("ERROR: New Password must have 6-12 characters, and must contain a number, an upper case and a lower case");
		System.out.println("-----");
	}
	
	@Override
	public String getMessage(){
		String erro = new String();
		erro += "-----\n";
		erro += "ERROR: New Password must have 6-12 characters, and must contain a number, an upper case and a lower case \n";
		erro += "-----";
		return erro;
	}
}
