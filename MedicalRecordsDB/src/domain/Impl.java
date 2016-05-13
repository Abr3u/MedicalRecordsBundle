package domain;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import exception.CantLoginException;
import exception.DifferentPasswordException;
import exception.DoctorViewRecordException;
import exception.InvalidPasswordException;
import exception.NotEnoughPermissionException;
import exception.PatientDoesntExistException;
import exception.SpecialtyAlreadyExistsException;
import exception.UsernameAlreadyExistsException;
import exception.UsernameDoesntExistException;
import exception.invalidEntryException;
import exception.invalidSpecialtyException;
import exception.missingDoctorArgumentsException;
import exception.missingPatientArgumentsException;
import exception.missingResourceIDArgumentsException;
import exception.missingResourceSpecArgumentsException;
import services.addSpecialty;
import services.changePassword;
import services.checkDoctorSignature;
import services.changeEmergency;
import services.checkRecordIntegrity;
import services.createAppointment;
import services.createDoctor;
import services.createPatient;
import services.createStaff;
import services.createUser;
import services.doctorViewRecord;
import services.loginInSystem;
import services.viewRecord;

public class Impl extends UnicastRemoteObject implements MedRecDBInterface {

	public Impl(int port) throws RemoteException, IllegalArgumentException {
		super(port, new SslRMIClientSocketFactory(), new SslRMIServerSocketFactory(null, null, true));
	}

	@Override
	public void addSpecialty(String username, String pass, String specialty) throws ParserConfigurationException,
			SAXException, IOException, missingDoctorArgumentsException, missingResourceSpecArgumentsException,
			missingPatientArgumentsException, missingResourceIDArgumentsException, NotEnoughPermissionException,
			CantLoginException, SpecialtyAlreadyExistsException {
		addSpecialty service = new addSpecialty();
		service.execute(username, pass, specialty);
	}

	@Override
	public void changePassword(String username, String pass, String newPass, String newPass2)
			throws RemoteException, CantLoginException, DifferentPasswordException, InvalidPasswordException {
		changePassword service = new changePassword();
		service.execute(username, pass, newPass, newPass2);
	}

	@Override
	public void createAppointment(String docUsername, String pass, String patientUname, String description)
			throws CantLoginException, UsernameDoesntExistException, ParserConfigurationException, SAXException,
			IOException, missingDoctorArgumentsException, missingResourceSpecArgumentsException,
			missingPatientArgumentsException, missingResourceIDArgumentsException, NotEnoughPermissionException {
		createAppointment service = new createAppointment();
		service.execute(docUsername, pass, patientUname, description);
	}

	@Override
	public void createDoctor(String username, String pass, String newUsername, String spec)
			throws ParserConfigurationException, SAXException, IOException, missingDoctorArgumentsException,
			missingResourceSpecArgumentsException, missingPatientArgumentsException,
			missingResourceIDArgumentsException, NotEnoughPermissionException, CantLoginException,
			UsernameDoesntExistException, invalidSpecialtyException {
		createDoctor service = new createDoctor();
		service.execute(username, pass, newUsername, spec);

	}

	@Override
	public void createPatient(String username, String password, boolean emergency, String newUsername, String first,
			String last, String add)
					throws NotEnoughPermissionException, CantLoginException, UsernameAlreadyExistsException,
					ParserConfigurationException, SAXException, IOException, missingDoctorArgumentsException,
					missingResourceSpecArgumentsException, missingPatientArgumentsException,
					missingResourceIDArgumentsException, UsernameDoesntExistException {
		createPatient service = new createPatient();
		service.execute(username, password, emergency, newUsername, first, last, add);

	}

	@Override
	public void createStaff(String username, String pass, String newUsername) throws ParserConfigurationException,
			SAXException, IOException, missingDoctorArgumentsException, missingResourceSpecArgumentsException,
			missingPatientArgumentsException, missingResourceIDArgumentsException, NotEnoughPermissionException,
			CantLoginException, UsernameDoesntExistException {
		createStaff service = new createStaff();
		service.execute(username, pass, newUsername);
	}

	@Override
	public String createUser(String username, String pass, String newUsername) throws NotEnoughPermissionException,
			CantLoginException, UsernameAlreadyExistsException, ParserConfigurationException, SAXException, IOException,
			missingDoctorArgumentsException, missingResourceSpecArgumentsException, missingPatientArgumentsException,
			missingResourceIDArgumentsException {
		createUser service = new createUser();
		return service.execute(username, pass, newUsername);
	}

	@Override
	public String doctorViewRecord(String username, String pass, String patientUname, boolean emergency)
			throws ParserConfigurationException, SAXException, IOException, missingDoctorArgumentsException,
			missingResourceSpecArgumentsException, missingPatientArgumentsException,
			missingResourceIDArgumentsException, DoctorViewRecordException, PatientDoesntExistException,
			NotEnoughPermissionException, CantLoginException {
		doctorViewRecord service = new doctorViewRecord();
		return service.execute(username, pass, patientUname, emergency);

	}

	@Override
	public String viewRecord(String username, String pass) throws RemoteException, CantLoginException {
		viewRecord service = new viewRecord();
		return service.execute(username, pass);
	}

	@Override
	public void loginInSystem(String username, String pass) throws RemoteException, CantLoginException {
		loginInSystem service = new loginInSystem();
		service.execute(username, pass);
	}

	@Override
	public String getUserType(String username) throws RemoteException {
		DBManager dbManager = new DBManager();
		return dbManager.getUserType(username);
	}

	@Override
	public boolean checkDoctorSign(String username, String pass, String docName, String entry)
			throws CantLoginException, UsernameDoesntExistException, invalidEntryException,
			NotEnoughPermissionException, ParserConfigurationException, SAXException, IOException,
			missingDoctorArgumentsException, missingResourceSpecArgumentsException, missingPatientArgumentsException,
			missingResourceIDArgumentsException {
		checkDoctorSignature service = new checkDoctorSignature();
		return service.execute(username, pass, docName, entry);
	}

	@Override
	public boolean checkRecord(String username, String pass, String patientName) throws NoSuchAlgorithmException, CantLoginException, UsernameDoesntExistException, ParserConfigurationException, SAXException, IOException, missingDoctorArgumentsException, missingResourceSpecArgumentsException, missingPatientArgumentsException, missingResourceIDArgumentsException, NotEnoughPermissionException {
		checkRecordIntegrity service = new checkRecordIntegrity();
		return service.execute(username, pass, patientName);
	}
	
	@Override
	public void changeEmergencyStatus(String username,String password,String docName, boolean e) throws CantLoginException, UsernameDoesntExistException, ParserConfigurationException, SAXException, IOException, missingDoctorArgumentsException, missingResourceSpecArgumentsException, missingPatientArgumentsException, missingResourceIDArgumentsException, NotEnoughPermissionException{
		
		changeEmergency service = new changeEmergency();
		service.execute(username,password,docName,e);
	}
	
	@Override
	public String getEmergencyMode(String username){
		DBManager dbManager = new DBManager();
		return dbManager.getEmergencyMode(username);
	}

}
