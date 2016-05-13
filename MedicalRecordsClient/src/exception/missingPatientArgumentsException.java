package exception;

public class missingPatientArgumentsException extends Exception {

private static final long serialVersionUID = 1L;
	
	public missingPatientArgumentsException(){
		System.out.println("-----");
		System.out.println("ERROR: Missing arguments for Subject");
		System.out.println("Usage: patient+recordID");
		System.out.println("-----");
	}
	
	@Override
	public String getMessage(){
		String erro = new String();
		erro += "-----\n";
		erro += "ERROR: Missing arguments for Subject\n";
		erro += "Usage: patient+recordID\n";
		erro += "-----";
		return erro;
	}
	
}
