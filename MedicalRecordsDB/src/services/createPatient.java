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
import exception.UsernameAlreadyExistsException;
import exception.UsernameDoesntExistException;
import exception.missingDoctorArgumentsException;
import exception.missingPatientArgumentsException;
import exception.missingResourceIDArgumentsException;
import exception.missingResourceSpecArgumentsException;

public class createPatient {

	public final void execute(String username, String password, Boolean emergency, String newUsername, String first,
			String last, String add)
					throws NotEnoughPermissionException, CantLoginException, UsernameAlreadyExistsException,
					ParserConfigurationException, SAXException, IOException, missingDoctorArgumentsException,
					missingResourceSpecArgumentsException, missingPatientArgumentsException,
					missingResourceIDArgumentsException, UsernameDoesntExistException {
		DBManager db = new DBManager();
		AccessControl accessControl = new AccessControl();

		if (!db.tryLogin(username, password)) {
			throw new CantLoginException();
		}

		if (!db.usernameExists(newUsername)) {
			throw new UsernameDoesntExistException();
		}

		String role;
		if (db.isDoctor(username)) {
			if (emergency) {
				role = "doctor+emergency";
			} else {
				role = "doctor+regular";
			}
		}
		if (db.isStaff(username)) {
			role = "staff";
		} else {
			role = "patient";
		}
		
		if (!accessControl.checkPolicies("filePath", role, "create", "record")) {
			throw new NotEnoughPermissionException();
		}

		try {
			db.createPatientProfile(newUsername, first, last, add);
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Something went wrong while processing your request");
		}

	}
}
