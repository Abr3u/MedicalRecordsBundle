package exception;

public class DoctorViewRecordException extends Exception {

private static final long serialVersionUID = 1L;
	
	public DoctorViewRecordException(){
		System.out.println("-----");
		System.out.println("ERROR: Username does belong to a Doctor");
		System.out.println("-----");
	}
	
}
