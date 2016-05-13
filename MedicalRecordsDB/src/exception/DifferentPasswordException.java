package exception;

public class DifferentPasswordException extends Exception{

	private static final long serialVersionUID = 1L;

	
	public DifferentPasswordException(){
		System.out.println("-----");
		System.out.println("ERROR:  The two passwords were different");
		System.out.println("-----");
	}
}
