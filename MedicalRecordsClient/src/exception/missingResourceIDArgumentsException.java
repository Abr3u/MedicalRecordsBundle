package exception;

public class missingResourceIDArgumentsException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public missingResourceIDArgumentsException(){
		System.out.println("-----");
		System.out.println("ERROR: Missing arguments for Resource");
		System.out.println("Usage: record+ID");
		System.out.println("-----");
	}
	
	@Override
	public String getMessage(){
		String erro = new String();
		erro += "-----\n";
		erro += "ERROR: Missing arguments for Resource\n";
		erro += "Usage: record+ID\n";
		erro += "-----";
		return erro;
	}
	
}
