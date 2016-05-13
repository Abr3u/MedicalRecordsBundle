package exception;

public class invalidSpecialtyException extends Exception {

private static final long serialVersionUID = 1L;
	
	public invalidSpecialtyException(){
		System.out.println("-----");
		System.out.println("ERROR: The specialty provided doesn't exist");
		System.out.println("-----");
	}
	
}
