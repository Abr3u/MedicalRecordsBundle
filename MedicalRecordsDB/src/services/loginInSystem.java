package services;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import domain.AccessControl;
import domain.DBManager;
import exception.CantLoginException;
import exception.NotEnoughPermissionException;
import exception.SpecialtyAlreadyExistsException;
import exception.missingDoctorArgumentsException;
import exception.missingPatientArgumentsException;
import exception.missingResourceIDArgumentsException;
import exception.missingResourceSpecArgumentsException;

public class loginInSystem {
	
	public final void execute(String username, String password) throws CantLoginException{
		DBManager db = new DBManager();

		if (!db.tryLogin(username, password)) {
			throw new CantLoginException();
		}
	}
}
