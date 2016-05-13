package services;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import domain.AccessControl;
import domain.DBManager;
import exception.CantLoginException;
import exception.NotEnoughPermissionException;
import exception.UsernameDoesntExistException;
import exception.missingDoctorArgumentsException;
import exception.missingPatientArgumentsException;
import exception.missingResourceIDArgumentsException;
import exception.missingResourceSpecArgumentsException;

public class checkRecordIntegrity {
	public final boolean execute(String username, String password,String patientName) throws CantLoginException, UsernameDoesntExistException, NoSuchAlgorithmException, ParserConfigurationException, SAXException, IOException, missingDoctorArgumentsException, missingResourceSpecArgumentsException, missingPatientArgumentsException, missingResourceIDArgumentsException, NotEnoughPermissionException{
		DBManager db = new DBManager();
		AccessControl accessControl = new AccessControl();

		if (!db.tryLogin(username, password)) {
			throw new CantLoginException();
		}
		
		if (!db.usernameExists(patientName)) {
			throw new UsernameDoesntExistException();
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
		
		if (!accessControl.checkPolicies("filePath", role, "create", "record")) {
			throw new NotEnoughPermissionException();
		}
		
		return db.checkRecordIntegrity(patientName);
	}
}
