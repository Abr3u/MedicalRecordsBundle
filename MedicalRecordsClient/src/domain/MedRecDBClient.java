package domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream.GetField;
import java.net.URL;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Scanner;

import javax.rmi.ssl.SslRMIClientSocketFactory;

import domain.MedRecDBInterface;
import exception.CantLoginException;
import exception.InvalidPasswordException;
import exception.UsernameDoesntExistException;

import java.net.URL;
import java.net.URLClassLoader;

public class MedRecDBClient {

	public static String[] getCredentials() throws UsernameDoesntExistException, InvalidPasswordException, IOException {
		String username = new String();
		String password = new String();
		InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader entradaScanner = new BufferedReader(isr);
		System.out.println("***** LOGIN MEDICAL RECORDS DATABASE *****");
		System.out.println("Insert your");
		System.out.print("Username: ");
		username = entradaScanner.readLine();

		if (username.isEmpty())
			throw new UsernameDoesntExistException();
		System.out.print("Password: ");

		password = entradaScanner.readLine();

		if (password.isEmpty())
			throw new InvalidPasswordException();
		String[] userAndPass = new String[2];
		userAndPass[0] = username;
		userAndPass[1] = password;
		return userAndPass;
	}

	public static String[] mudaPassword() throws IOException {
		String[] inputs = new String[3];
		InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader entradaScanner = new BufferedReader(isr);
		System.out.print("Actual Password: ");
		inputs[0] = entradaScanner.readLine();
		System.out.print("New Password: ");
		inputs[1] = entradaScanner.readLine();
		System.out.print("Confirm your new Password: ");
		inputs[2] = entradaScanner.readLine();
		return inputs;

	}

	public static void inicioNovoMenu() {
		System.out.println("**********************");
		System.out.println("| Enter 0 to go back |");
		System.out.println("**********************");
	}

	public static void menu() {
		System.out.println("********** MEDICAL RECORDS DATABASE **********");
		System.out.println("**********************************************");
		System.out.println("|           0. Exit                          |");
		System.out.println("|           1. Check Patient Record          |");
		System.out.println("|           2. Create Appointment            |");
		System.out.println("|           3. Create User                   |");
		System.out.println("|           4. Create Patient                |");
		System.out.println("|           5. Create Doctor                 |");
		System.out.println("|           6. Create Staff                  |");
		System.out.println("|           7. Check Doctor Signature        |");
		System.out.println("|           8. Check Record Integrity        |");
		System.out.println("|           9. Change Emergency Status       |");
		System.out.println("|          10. Add Specialty                 |");
		System.out.println("|          11. Change Password               |");
		System.out.println("**********************************************");

		System.out.print("Option: ");
	}
	
	public static String enterPassword(String username) throws IOException{
		String password = new String();
		InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader entradaScanner = new BufferedReader(isr);
		System.out.print("Please retype your password: ");
		password = entradaScanner.readLine();
		return password;
	}

	public static void main(String arg[]) throws Exception {

		MedRecDBInterface remote = null;

		// System.setProperty("java.security.policy","file:C:/Users/Rui/Documents/GitHub/MedicalRecordsDB_Client/src/domain/client.policy");
		// System.setSecurityManager(new RMISecurityManager());

		System.setProperty("javax.net.ssl.keyStore",
				"C:/Users/Rui/Documents/GitHub/MedicalRecordsDB_Client/src/keystore");
		System.setProperty("javax.net.ssl.keyStorePassword", "banana");
		System.setProperty("javax.net.ssl.trustStore",
				"C:/Users/Rui/Documents/GitHub/MedicalRecordsDB_Client/src/truststore");
		System.setProperty("javax.net.ssl.trustStorePassword", "password");

		Registry registry = LocateRegistry.getRegistry("193.136.167.25", 9999, new SslRMIClientSocketFactory());

		remote = (MedRecDBInterface) registry.lookup("MedicalRecordsDatabase");

		/*
		 * try { Registry registry =
		 * LocateRegistry.getRegistry("193.136.167.82", 1099); remote =
		 * (MedRecDBInterface)registry.lookup("MedicalRecordsDatabase");
		 * System.out.println("Cliente comunica com server"); } catch
		 * (ConnectException e) { throw new ConnectException(
		 * "Cliente não encontra server"); }
		 */

		int opcao = 999;
		String[] nameAndPass;
		String username;
		String password;
		String patientName;
		//Scanner entradaScanner = new Scanner(System.in);
		InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader entradaScanner = new BufferedReader(isr);
		String type;
		String[] inputsNovaPassword;
		String records = null;
		String description;
		String newUsername;

		while (true) {
			try {
				nameAndPass = getCredentials();
				username = nameAndPass[0];
				password = nameAndPass[1];
				remote.loginInSystem(username, password);
				do {
					try {
						menu();
						patientName = new String();
						opcao = Integer.parseInt(entradaScanner.readLine());
						if (opcao == 0)
							break;
						inicioNovoMenu();

						switch (opcao) {
						case 1: // CHECK PATIENT RECORD
							password = enterPassword(username);
							if (password.equals("0"))
								break;
							type = remote.getUserType(username);
							if (type.equals("doctor")) {
								System.out.print("Patient Username: ");
								patientName = entradaScanner.readLine();
								if (patientName.equals("0"))
									break;
								System.out.println();
								String emergency = remote.getEmergencyMode(username);
								boolean e = false;
								if (emergency.equals("true"))
									e = true;
								records = remote.doctorViewRecord(username, password, patientName, e);
							} else {
								records = remote.viewRecord(username, password);
							}

							System.out.println(records);
							break;

						case 2: // CREATE APPOINTMENT
							password = enterPassword(username);
							if (password.equals("0"))
								break;
							System.out.print("Patient Username: ");
							patientName = entradaScanner.readLine();
							if (patientName.equals("0"))
								break;
							System.out.print("Description: ");
							description = entradaScanner.readLine();
							if (description.equals("0"))
								break;
							remote.createAppointment(username, password, patientName, description);
							break;

						case 3: // CREATE USER
							System.out.print("New Username to be created: ");
							newUsername = entradaScanner.readLine();
							if (newUsername.equals("0"))
								break;
							String newPass = remote.createUser(username, password, newUsername);
							System.out.println(newUsername + "'s Password: " + newPass);
							break;

						case 4: // CREATE PATIENT
							password = enterPassword(username);
							if (password.equals("0"))
								break;
							System.out.print("Username of the Patient: ");
							newUsername = entradaScanner.readLine();
							if (newUsername.equals("0"))
								break;
							System.out.print("First Name: ");
							String first = entradaScanner.readLine();
							if (first.equals("0"))
								break;
							System.out.print("Last Name: ");
							String last = entradaScanner.readLine();
							if (last.equals("0"))
								break;
							System.out.print("Address: ");
							String address = entradaScanner.readLine();
							if (address.equals("0"))
								break;
							remote.createPatient(username, password, false, newUsername, first, last, address);
							break;

						case 5: // CREATE DOCTOR
							password = enterPassword(username);
							if (password.equals("0"))
								break;
							System.out.print("Username of the Doctor: ");
							newUsername = entradaScanner.readLine();
							if (newUsername.equals("0"))
								break;
							System.out.print("Specialty: ");
							String spec = entradaScanner.readLine();
							if (spec.equals("0"))
								break;
							remote.createDoctor(username, password, newUsername, spec);
							break;

						case 6: // CREATE STAFF
							password = enterPassword(username);
							if (password.equals("0"))
								break;
							System.out.print("Username of the Staff Member: ");
							newUsername = entradaScanner.readLine();
							if (newUsername.equals("0"))
								break;
							remote.createStaff(username, password, newUsername);
							break;

						case 7: // CHECK SIGNATURE
							password = enterPassword(username);
							if (password.equals("0"))
								break;
							System.out.print("Doctor Name: ");
							newUsername = entradaScanner.readLine();
							if (newUsername.equals("0"))
								break;
							System.out.print("Patient Name: ");
							String entryStr = entradaScanner.readLine();
							if (entryStr.equals("0"))
								break;
							if(remote.checkDoctorSign(username, password, newUsername, entryStr))
								System.out.println("All the entries of " + entryStr + " created by doctor "+newUsername+ " are correctly signed");
							else
								System.out.println("Not all the entries of " + entryStr + " created by doctor "+newUsername+ " are correctly signed");
							break;
							
						case 8: // CHECK RECORD
							password = enterPassword(username);
							if (password.equals("0"))
								break;
							System.out.print("Patient Name: ");
							String p = entradaScanner.readLine();
							if (p.equals("0"))
								break;
							if(remote.checkRecord(username, password, p))
								System.out.println("The "+p+"'s Record is correctly stored");
							else
								System.out.println("The "+p+"'s Record is not correctly stored");
							break;
							
						case 9: // CHANGE EMERGENCY
							password = enterPassword(username);
							if (password.equals("0"))
								break;
							System.out.print("Doctor Name: ");
							String d = entradaScanner.readLine();
							if (d.equals("0"))
								break;
							System.out.println("To Emergency? [y/n] : ");
							String e = entradaScanner.readLine();
							boolean t;
							if(e.equals("y"))t = true;
							else t=false;
							remote.changeEmergencyStatus(username,password,d,t);
							break;
						
						case 10: // ADD SPECIALTY
							System.out.print("New specialty: ");
							if (password.equals("0"))
								break;
							String specialty = entradaScanner.readLine();
							if (specialty.equals("0"))
								break;
							remote.addSpecialty(username, password, specialty);
							break;
						
						case 11: // CHANGE PASSWORD
							inputsNovaPassword = mudaPassword();
							remote.changePassword(username, inputsNovaPassword[0], inputsNovaPassword[1],
									inputsNovaPassword[2]);
							System.out.println("Password changed with success!");
							break;
												}
					} catch (Exception e) {
						System.err.println(e.getMessage());
						if (e.toString().contains("CantLogin"))
							opcao = 0;
					}
				} while (opcao != 0);
			} catch (CantLoginException e) {
				System.err.println(e.getMessage());
			}
		}
	}
}