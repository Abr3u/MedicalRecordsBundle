package services;

import java.io.IOException;

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

public class changeEmergency {
	public final void execute(String u, String p, String d, boolean e ) throws CantLoginException, UsernameDoesntExistException, ParserConfigurationException, SAXException, IOException, missingDoctorArgumentsException, missingResourceSpecArgumentsException, missingPatientArgumentsException, missingResourceIDArgumentsException, NotEnoughPermissionException{
		DBManager db = new DBManager();
		AccessControl accessControl = new AccessControl();

		if (!db.tryLogin(u, p)) {
			throw new CantLoginException();
		}

		if (!db.usernameExists(d)) {
			throw new UsernameDoesntExistException();
		}
		String role;
		if (db.isDoctor(u)) {
			role = "doctor";
		}
		if (db.isStaff(u)) {
			role = "staff";
		} else {
			role = "patient";
		}
		
		if (!accessControl.checkPolicies("filePath", role, "create", "record")) {
			throw new NotEnoughPermissionException();
		}
		db.changeEmergencyStatus(d,e);
	}
}
