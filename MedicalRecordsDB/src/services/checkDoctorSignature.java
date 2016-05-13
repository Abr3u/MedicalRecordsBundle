package services;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import domain.AccessControl;
import domain.DBManager;
import exception.CantLoginException;
import exception.NotEnoughPermissionException;
import exception.UsernameDoesntExistException;
import exception.invalidEntryException;
import exception.invalidSpecialtyException;
import exception.missingDoctorArgumentsException;
import exception.missingPatientArgumentsException;
import exception.missingResourceIDArgumentsException;
import exception.missingResourceSpecArgumentsException;

public class checkDoctorSignature {
	public final boolean execute(String username, String password, String docName, String patientName) throws ParserConfigurationException, SAXException, IOException, missingDoctorArgumentsException, missingResourceSpecArgumentsException, missingPatientArgumentsException, missingResourceIDArgumentsException, NotEnoughPermissionException, CantLoginException,UsernameDoesntExistException, invalidEntryException{
		DBManager db = new DBManager();
		AccessControl accessControl = new AccessControl();

		if (!db.tryLogin(username, password)) {
			throw new CantLoginException();
		}

		if (!db.usernameExists(docName)) {
			throw new UsernameDoesntExistException();
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
		System.out.println("Servico de checkSign");
		return db.checkSign(docName, patientName);
	}
}
