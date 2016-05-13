package exception;

public class invalidEntryException extends Exception{

private static final long serialVersionUID = 1L;
	
	public invalidEntryException(){
		System.out.println("-----");
		System.out.println("ERROR: Entry does not exist");
		System.out.println("-----");
	}
	
	@Override
	public String getMessage(){
		String erro = new String();
		erro += "-----\n";
		erro += "ERROR: Entry does not exist\n";
		erro += "-----";
		return erro;
	}
}
