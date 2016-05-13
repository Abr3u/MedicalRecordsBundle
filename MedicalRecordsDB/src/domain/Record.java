package domain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Record {

	private Integer id;
	private String patientName;
	private String creationDate;

	// data em string
	public Record(String i, String pn, String cd) {
		this.setId(Integer.parseInt(i));
		this.setPatientName(pn);
		this.setCreationDate(cd);

	}

	// data em Date object
	public Record(String i, String pn, Date creationDate) {
		this.setId(Integer.parseInt(i));
		this.setPatientName(pn);

		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String aux = df.format(creationDate);
		this.setCreationDate(aux);

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
}
