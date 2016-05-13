package domain;

import java.io.IOException;
import java.rmi.*;

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

public interface MedRecDBInterface extends Remote{
	
	void addSpecialty(String username, String pass, String specialty) throws RemoteException, ParserConfigurationException, SAXException, IOException, missingDoctorArgumentsException, missingResourceSpecArgumentsException, missingPatientArgumentsException, missingResourceIDArgumentsException, NotEnoughPermissionException, CantLoginException, SpecialtyAlreadyExistsException;
	void changePassword(String username, String pass, String newPass, String newPass2) throws RemoteException, CantLoginException, InvalidPasswordException, DifferentPasswordException;
	void createAppointment(String docUsername, String pass, String patientUname, String description) throws RemoteException, CantLoginException, UsernameDoesntExistException, ParserConfigurationException, SAXException, IOException, missingDoctorArgumentsException, missingResourceSpecArgumentsException, missingPatientArgumentsException, missingResourceIDArgumentsException, NotEnoughPermissionException;
	void createDoctor(String username, String pass, String newUsername, String spec) throws RemoteException, ParserConfigurationException, SAXException, IOException, missingDoctorArgumentsException, missingResourceSpecArgumentsException, missingPatientArgumentsException, missingResourceIDArgumentsException, NotEnoughPermissionException, CantLoginException, UsernameDoesntExistException, invalidSpecialtyException;
	void createPatient(String username, String password, boolean emergency, String newUsername, String first, String last, String add) throws RemoteException, NotEnoughPermissionException, CantLoginException, UsernameAlreadyExistsException, ParserConfigurationException, SAXException, IOException, missingDoctorArgumentsException, missingResourceSpecArgumentsException, missingPatientArgumentsException, missingResourceIDArgumentsException, UsernameDoesntExistException;
	void createStaff(String username, String pass, String newUsername) throws RemoteException, ParserConfigurationException, SAXException, IOException, missingDoctorArgumentsException, missingResourceSpecArgumentsException, missingPatientArgumentsException, missingResourceIDArgumentsException, NotEnoughPermissionException, CantLoginException, UsernameDoesntExistException;
	String createUser(String username, String pass, String newUsername) throws RemoteException, NotEnoughPermissionException, CantLoginException, UsernameAlreadyExistsException, ParserConfigurationException, SAXException, IOException, missingDoctorArgumentsException, missingResourceSpecArgumentsException, missingPatientArgumentsException, missingResourceIDArgumentsException;
	String doctorViewRecord(String username, String pass, String patientUname, boolean emergency) throws RemoteException, ParserConfigurationException, SAXException, IOException, missingDoctorArgumentsException, missingResourceSpecArgumentsException, missingPatientArgumentsException, missingResourceIDArgumentsException, DoctorViewRecordException, PatientDoesntExistException, NotEnoughPermissionException, CantLoginException;
	String viewRecord(String username, String pass) throws RemoteException, CantLoginException;
	void loginInSystem(String username, String pass) throws RemoteException, CantLoginException;
	String getUserType(String username) throws RemoteException;
	boolean checkDoctorSign (String username, String pass, String docName, String patientName) throws RemoteException, ParserConfigurationException, SAXException, IOException, missingDoctorArgumentsException, missingResourceSpecArgumentsException, missingPatientArgumentsException, missingResourceIDArgumentsException, CantLoginException, UsernameDoesntExistException, NotEnoughPermissionException, invalidEntryException;
	boolean checkRecord (String username, String pass, String patientName) throws RemoteException, ParserConfigurationException, SAXException, IOException, missingDoctorArgumentsException, missingResourceSpecArgumentsException, missingPatientArgumentsException, missingResourceIDArgumentsException, CantLoginException, UsernameDoesntExistException, NotEnoughPermissionException, invalidEntryException;
	void changeEmergencyStatus(String username,String password,String d, boolean e) throws CantLoginException, UsernameDoesntExistException, ParserConfigurationException, SAXException, IOException, missingDoctorArgumentsException, missingResourceSpecArgumentsException, missingPatientArgumentsException, missingResourceIDArgumentsException,NotEnoughPermissionException;
	String getEmergencyMode(String username);
}
