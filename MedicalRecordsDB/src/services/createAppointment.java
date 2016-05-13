package services;

import java.io.IOException;
import java.io.NotActiveException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.management.relation.Role;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import domain.AccessControl;
import domain.DBManager;
import domain.Record;
import exception.CantLoginException;
import exception.NotEnoughPermissionException;
import exception.PatientDoesntExistException;
import exception.UsernameAlreadyExistsException;
import exception.UsernameDoesntExistException;
import exception.missingDoctorArgumentsException;
import exception.missingPatientArgumentsException;
import exception.missingResourceIDArgumentsException;
import exception.missingResourceSpecArgumentsException;

public class createAppointment {

	public final void execute(String username, String password, String patientUname, String description) throws CantLoginException, UsernameDoesntExistException, ParserConfigurationException, SAXException, IOException, missingDoctorArgumentsException, missingResourceSpecArgumentsException, missingPatientArgumentsException, missingResourceIDArgumentsException, NotEnoughPermissionException{
		DBManager db = new DBManager();
		AccessControl accessControl = new AccessControl();
		System.out.println("user = " + username + " pass = " + password + "patient = " + patientUname + "desc = " + description);
		if (!db.tryLogin(username, password)) {
			throw new CantLoginException();
		}
		
		if(!db.usernameExists(patientUname)){
			throw new UsernameDoesntExistException();
		}
		
		String role;
		if(db.isDoctor(username)){
			role = "doctor";
		}
		else if(db.isStaff(username)){
			role = "staff";
		}
		else{
			role = "patient";
		}
		
		if(!accessControl.checkPolicies("filePath", role, "create", "entry")){
			throw new NotEnoughPermissionException();
		}
		
		try {
			db.createAppointment(username, password, patientUname, description);
		} catch (PatientDoesntExistException e) {
			
		}

	}
}
