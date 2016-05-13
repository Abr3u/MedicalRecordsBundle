package exception;

public class NotEnoughPermissionException extends Exception {

private static final long serialVersionUID = 1L;
	
	public NotEnoughPermissionException(){
		System.out.println("-----");
		System.out.println("ERROR: You don't have enough permissions to perform that action");
		System.out.println("-----");
	}
	
	@Override
	public String getMessage(){
		String erro = new String();
		erro += "-----\n";
		erro += "You don't have enough permissions to perform that action\n";
		erro += "-----";
		return erro;
	}
	
	
}
