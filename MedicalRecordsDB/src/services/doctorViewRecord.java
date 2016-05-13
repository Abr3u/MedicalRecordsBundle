package services;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import domain.AccessControl;
import domain.DBManager;
import domain.Record;
import exception.CantLoginException;
import exception.DoctorViewRecordException;
import exception.NotEnoughPermissionException;
import exception.PatientDoesntExistException;
import exception.missingDoctorArgumentsException;
import exception.missingPatientArgumentsException;
import exception.missingResourceIDArgumentsException;
import exception.missingResourceSpecArgumentsException;

public class doctorViewRecord {

	public final String execute(String username, String password, String patient, boolean emergency) throws ParserConfigurationException, SAXException, IOException, missingDoctorArgumentsException, missingResourceSpecArgumentsException, missingPatientArgumentsException, missingResourceIDArgumentsException, DoctorViewRecordException, PatientDoesntExistException, NotEnoughPermissionException, CantLoginException {
		
		DBManager db = new DBManager();
		if (!db.isDoctor(username)) {
			throw new DoctorViewRecordException();
		}

		if(db.getRecordByUsername(patient).equals(null)){
			throw new PatientDoesntExistException();
		}
		
		if(!db.tryLogin(username, password)){
			throw new CantLoginException();
		}
		
		String spec = db.getSpecialtyByDoctor(username);
		
		AccessControl accessControl = new AccessControl();
		boolean granted;
		String entries = new String();
		
		if (!emergency) {
			granted = accessControl.checkPolicies("filepath", "doctor+regular+"+spec, "view", "record+"+spec);
			if(!granted){
				throw new NotEnoughPermissionException();
			}
			//db.viewPatientRecordBySpecialty(patient, spec);
			entries = db.getPatientRecordBySpecialty(patient, spec);
		}
		else {// EMERGENCY PRINT ALL THE STUFF
			granted = accessControl.checkPolicies("filepath", "doctor+emergency", "view", "record");
			if(!granted){
				//em principio nao entra aqui, mas fica para o futuro
				throw new NotEnoughPermissionException();
			}
			Record rec = db.getRecordByUsername(patient);
			System.out.println("----------------Record----------------");
			System.out.println("ID -> " + rec.getId());
			System.out.println("Patient Name -> " + rec.getPatientName());
			System.out.println("Creation Date -> " + rec.getCreationDate());
			//db.printEntriesByRecord(rec);// print entries of the Patient Record
			entries = db.getEntriesByRecord(rec);
		}
		return entries;
	}
}
