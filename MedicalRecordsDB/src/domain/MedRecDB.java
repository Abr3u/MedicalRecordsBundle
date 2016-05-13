package domain;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import exception.DifferentPasswordException;
import exception.DoctorViewRecordException;
import exception.InvalidPasswordException;
import exception.PatientDoesntExistException;
import exception.SpecialtyAlreadyExistsException;
import exception.UsernameDoesntExistException;
import exception.invalidSpecialtyException;
import exception.missingResourceSpecArgumentsException;
import exception.CantLoginException;
import exception.NotEnoughPermissionException;
import exception.UsernameAlreadyExistsException;
import exception.missingDoctorArgumentsException;
import exception.missingPatientArgumentsException;
import exception.missingResourceIDArgumentsException;
import services.*;

public class MedRecDB{

	public MedRecDB() throws RemoteException {}
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, InterruptedException, missingPatientArgumentsException, missingResourceIDArgumentsException, AlreadyBoundException {
		
		System.setProperty("javax.net.ssl.keyStore", "/home/rui/MedicalRecordsDB/keystore");
	    System.setProperty("javax.net.ssl.keyStorePassword", "banana");
	    System.setProperty("javax.net.ssl.trustStore", "/home/rui/MedicalRecordsDB/truststore");
	    System.setProperty("javax.net.ssl.trustStorePassword", "password");
		
		Impl impl = new Impl(9999);
		
		LocateRegistry.createRegistry(9999, new SslRMIClientSocketFactory(), new SslRMIServerSocketFactory(null, null, true));
        System.out.println("RMI registry running on port " + 9999);             

        Registry registry = LocateRegistry.getRegistry("193.136.167.25", 9999, new SslRMIClientSocketFactory());

        registry.bind("MedicalRecordsDatabase", impl);

		
		/*Registry registry;
		try{
			registry = LocateRegistry.getRegistry(8095);
			registry.rebind("MedicalRecordsDatabase", impl);
		}
		catch (ConnectException e){
			registry = LocateRegistry.createRegistry(8095);
			registry.rebind("MedicalRecordsDatabase", impl);
		}*/
		System.out.println("Server running");
	}

	
}



