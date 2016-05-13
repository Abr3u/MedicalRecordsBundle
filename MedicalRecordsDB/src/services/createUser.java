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
import exception.UsernameAlreadyExistsException;
import exception.missingDoctorArgumentsException;
import exception.missingPatientArgumentsException;
import exception.missingResourceIDArgumentsException;
import exception.missingResourceSpecArgumentsException;

public class createUser {

	public final String execute(String username, String password, String newUsername)
			throws NotEnoughPermissionException, CantLoginException, UsernameAlreadyExistsException, ParserConfigurationException, SAXException, IOException, missingDoctorArgumentsException, missingResourceSpecArgumentsException, missingPatientArgumentsException, missingResourceIDArgumentsException {
		DBManager db = new DBManager();
		AccessControl accessControl = new AccessControl();
		
		String pass = "";
		if (!db.tryLogin(username, password)) {
			throw new CantLoginException();
		}
		
		if(db.usernameExists(newUsername)){
			throw new UsernameAlreadyExistsException();
		}
		
		String role;
		if(db.isDoctor(username)){
			role = "doctor";
		}
		if(db.isStaff(username)){
			role = "staff";
		}
		else{
			role = "patient";
		}
		
		if(!accessControl.checkPolicies("filePath", role, "create", "profile")){
			throw new NotEnoughPermissionException();
		}
		
		try {
			pass = db.createUserProfile(newUsername);
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			System.out.println("Something went wrong while processing your request");
		}
		return pass;
	}
}
