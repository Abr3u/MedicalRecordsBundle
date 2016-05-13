package exception;

public class SpecialtyAlreadyExistsException extends Exception {

private static final long serialVersionUID = 1L;
	
	public SpecialtyAlreadyExistsException(){
		System.out.println("-----");
		System.out.println("ERROR: Specialty already exists");
		System.out.println("-----");
	}
	
}
