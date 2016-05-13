package exception;

public class PatientDoesntExistException extends Exception {

private static final long serialVersionUID = 1L;
	
	public PatientDoesntExistException(){
		System.out.println("-----");
		System.out.println("ERROR: The patient name provided doesn't exist");
		System.out.println("-----");
	}
	
	@Override
	public String getMessage(){
		String erro = new String();
		erro += "-----\n";
		erro += "ERROR: The patient name provided doesn't exist\n";
		erro += "-----";
		return erro;
	}
	
}
