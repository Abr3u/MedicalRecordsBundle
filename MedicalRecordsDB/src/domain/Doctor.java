package domain;

public class Doctor extends User {

	private String specialty;
	
	public Doctor(String u, String p,String spec) {
		super(u, p);
		this.setSpecialty(spec);
	}

	public String getSpecialty() {
		return specialty;
	}

	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}

	
	
}
