package exception;

public class invalidEntryException extends Exception {
private static final long serialVersionUID = 1L;
	
	public invalidEntryException(){
		System.out.println("-----");
		System.out.println("ERROR: Entry does not exists");
		System.out.println("-----");
	}
}
