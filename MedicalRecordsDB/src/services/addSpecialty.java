package services;

import java.io.IOException;
import java.io.NotActiveException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import domain.AccessControl;
import domain.DBManager;
import domain.Record;
import exception.CantLoginException;
import exception.NotEnoughPermissionException;
import exception.SpecialtyAlreadyExistsException;
import exception.UsernameAlreadyExistsException;
import exception.UsernameDoesntExistException;
import exception.invalidSpecialtyException;
import exception.missingDoctorArgumentsException;
import exception.missingPatientArgumentsException;
import exception.missingResourceIDArgumentsException;
import exception.missingResourceSpecArgumentsException;

public class addSpecialty {

	public final void execute(String username, String password, String spec) throws ParserConfigurationException, SAXException, IOException, missingDoctorArgumentsException, missingResourceSpecArgumentsException, missingPatientArgumentsException, missingResourceIDArgumentsException, NotEnoughPermissionException, CantLoginException, SpecialtyAlreadyExistsException{
		DBManager db = new DBManager();
		AccessControl accessControl = new AccessControl();

		if (!db.tryLogin(username, password)) {
			throw new CantLoginException();
		}
		
		if(db.isSpecialty(spec)){
			throw new SpecialtyAlreadyExistsException();
		}
		
		String role;
		if (db.isDoctor(username)) {
			role = "doctor";
		}
		if (db.isStaff(username)) {
			role = "staff";
		} else {
			role = "patient";
		}
		
		if (!accessControl.checkPolicies("filePath", role, "create", "specialty")) {
			throw new NotEnoughPermissionException();
		}

		db.createSpecialty(spec);
	}
}
