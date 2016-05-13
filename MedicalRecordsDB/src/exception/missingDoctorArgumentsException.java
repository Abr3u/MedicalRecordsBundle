package exception;

public class missingDoctorArgumentsException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public missingDoctorArgumentsException(){
		System.out.println("-----");
		System.out.println("ERROR: Missing arguments for Subject");
		System.out.println("Usage: doctor+mode+specialty");
		System.out.println("-----");
	}

}
