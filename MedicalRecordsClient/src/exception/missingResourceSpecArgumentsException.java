package exception;

public class missingResourceSpecArgumentsException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public missingResourceSpecArgumentsException(){
		System.out.println("-----");
		System.out.println("ERROR: Missing arguments for Resource");
		System.out.println("Usage: record+specialty");
		System.out.println("-----");
	}
	
	@Override
	public String getMessage(){
		String erro = new String();
		erro += "-----\n";
		erro += "ERROR: Missing arguments for Resource\n";
		erro += "Usage: record+specialty\n";
		erro += "-----";
		return erro;
	}
	
	
}
