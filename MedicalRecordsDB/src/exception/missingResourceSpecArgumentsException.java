package exception;

public class missingResourceSpecArgumentsException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public missingResourceSpecArgumentsException(){
		System.out.println("-----");
		System.out.println("ERROR: Missing arguments for Resource");
		System.out.println("Usage: record+specialty");
		System.out.println("-----");
	}
	
}
