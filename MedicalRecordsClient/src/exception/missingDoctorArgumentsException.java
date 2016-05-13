package exception;

public class missingDoctorArgumentsException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public missingDoctorArgumentsException(){
		System.out.println("-----");
		System.out.println("ERROR: Missing arguments for Subject");
		System.out.println("Usage: doctor+mode+specialty");
		System.out.println("-----");
	}
	
	@Override
	public String getMessage(){
		String erro = new String();
		erro += "-----\n";
		erro += "ERROR: Missing arguments for Subject\n";
		erro += "Usage: doctor+mode+specialty\n";
		erro += "-----";
		return erro;
	}

}
