package domain;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.channels.NonWritableChannelException;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import com.mysql.jdbc.util.Base64Decoder;

import exception.DifferentPasswordException;
import exception.InvalidPasswordException;
import exception.PatientDoesntExistException;

public class DBManager {

	private final String driver = "com.mysql.jdbc.Driver";
	private final String URL = "yourDataBaseURLHere";
	private final String user = "yourDBusername";
	private final String pass = "DBpassword";
	private final String rootUsername = "greg";

	public boolean checkRecordIntegrity(String pname) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String q = "SELECT r_id,patient_name,creation_date FROM records WHERE patient_name = ?";
		ArrayList<String> res = doQuery(q, 1, new ArrayList<String>(Arrays.asList(pname)));

		String linha = "";
		for (int i = 0; i < res.size(); i++) {
			if (i != res.size() - 1) {
				linha += res.get(i);
			}
		}

		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(linha.getBytes("UTF-8"));
		byte[] digest = md.digest();
		linha = String.format("%064x", new java.math.BigInteger(1, digest));

		String qaux = "SELECT r_hash FROM records WHERE patient_name = ?";
		res = doQuery(qaux, 1, new ArrayList<String>(Arrays.asList(pname)));
		String aux = res.get(0);
		if (aux.equals(linha)) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * ver se staff � staff ver se o login esta certo ver se paciente ja existe
	 * marcar compromisso
	 */

	public void createAppointment(String docUsername, String docPassword, String patientUname, String description)
			throws PatientDoesntExistException {
		if (!this.getRecordByUsername(patientUname).equals(null)) {
			Record aux = this.getRecordByUsername(patientUname);
			int recordId = aux.getId();
			String spec = getSpecialtyByDoctor(docUsername);
			addEntry(recordId, description, docUsername, spec);
			String lastEntry = this.getLastEntry();
			doctorSignDocument(docUsername, docPassword, lastEntry);
		} else {
			throw new PatientDoesntExistException();
		}
	}

	public void doctorSignDocument(String docUsername, String docPassword, String lastEntry) {
		if (this.tryLogin(docUsername, docPassword)) {
			sign(docUsername, lastEntry);
		}
	}

	public void generateKeyPair(String docUsername) {
		try {
			KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
			keygen.initialize(1024);
			KeyPair keypair = keygen.generateKeyPair();
			PrivateKey privateKey = keypair.getPrivate();
			PublicKey publicKey = keypair.getPublic();
			Writer writer = null;

			try {
				writer = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream("privateKeys/" + docUsername + ".txt"), "utf-8"));
				writer.write(Base64.getEncoder().encodeToString(privateKey.getEncoded()));
			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				try {
					writer.close();
				} catch (Exception ex) {
					/* ignore */}
			}
			String query = "UPDATE doctors SET publicKey = ? WHERE d_username = ?";
			String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
			ArrayList<String> res = doQuery(query, 2,
					new ArrayList<String>(Arrays.asList(publicKeyString, docUsername)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static byte[] toDecodedBase64ByteArray(byte[] base64EncodedByteArray) {
		return DatatypeConverter.parseBase64Binary(new String(base64EncodedByteArray, Charset.forName("UTF-8")));
	}

	public void sign(String docUsername, String lastEntry) {
		String strSignature = null;
		try {
			String privateKeyStr = readFromFile("privateKeys/" + docUsername + ".txt");
			byte[] privateKeyBytes = toDecodedBase64ByteArray(privateKeyStr.getBytes());
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			KeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
			PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

			Signature signer = Signature.getInstance("SHA1withRSA");
			signer.initSign(privateKey);
			signer.update(docUsername.getBytes());
			byte[] signature = signer.sign();
			strSignature = Base64.getEncoder().encodeToString(signature);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String query = "UPDATE entry SET signature = ? WHERE entry_id = ?";
		ArrayList<String> res = doQuery(query, 2, new ArrayList<String>(Arrays.asList(strSignature, lastEntry)));
	}

	public String readFromFile(String path) {
		BufferedReader br = null;
		String everything = null;
		try {
			br = new BufferedReader(new FileReader(path));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			everything = sb.toString();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return everything;
	}

	public boolean checkSign(String docName, String patientName) {
		try {
			String query = "SELECT publicKey FROM doctors WHERE d_username = ?";
			ArrayList<String> res = doQuery(query, 1, new ArrayList<String>(Arrays.asList(docName)));
			String publicKeyStr = res.get(0);
			String query2 = "SELECT signature FROM records r join entry e WHERE r.patient_name = ? AND e.doc_name = ?";

			ArrayList<String> res2 = doQuery(query2, 2, new ArrayList<String>(Arrays.asList(patientName, docName)));
			boolean signatureOk = true;
			for (String s : res2) {
				if (!s.startsWith("--")) {
					byte[] signatureInBase64 = toDecodedBase64ByteArray(s.getBytes());

					KeyFactory keyFactory = KeyFactory.getInstance("RSA");
					byte[] publicKeyBytes = toDecodedBase64ByteArray(publicKeyStr.getBytes());
					KeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
					PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

					Signature signer = Signature.getInstance("SHA1withRSA");
					signer.initVerify(publicKey);
					signer.update(docName.getBytes());
					signatureOk = signer.verify(signatureInBase64) && signatureOk;
				}
			}
			if (signatureOk)
				return true;
			else
				return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public String getLastEntry() {
		String query = "SELECT entry_id FROM entry ORDER BY entry_id DESC LIMIT 1";
		ArrayList<String> res = doQuery(query, 0, new ArrayList<String>());
		if (!res.isEmpty()) {
			return res.get(0);
		}
		return null;
	}

	public String createUserProfile(String patientUname) throws NoSuchAlgorithmException, NoSuchProviderException {
		String alphabet = "abcdefghijklmnopqrstuvwxyz";
		String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		SecureRandom random = new SecureRandom();
		String passNumeros = "" + random.nextInt(10);
		for (int i = 0; i < 3; i++) {
			passNumeros = passNumeros + random.nextInt(10);
		}

		int indice;
		char charLetra;
		String passMinuscula = "";
		for (int i = 0; i < 4; i++) {
			indice = random.nextInt(26);
			charLetra = alphabet.charAt(indice);
			passMinuscula = passMinuscula + charLetra;
		}
		String passMaiuscula = "";
		for (int i = 0; i < 4; i++) {
			indice = random.nextInt(26);
			charLetra = ALPHABET.charAt(indice);
			passMaiuscula = passMaiuscula + charLetra;
		}

		String pass = passNumeros + passMinuscula + passMaiuscula;
		pass = shuffle(pass);

		SecureRandom rand = SecureRandom.getInstance("SHA1PRNG", "SUN");
		byte[] newSalt = new byte[8];
		rand.nextBytes(newSalt);
		// String salt = String.valueOf(newSalt);
		String salt = new String(printHexBinary(newSalt));
		String hashPass = hash(pass, salt);
		String query = "INSERT INTO users (username, password, saltValue) " + "VALUES (?, ?, ?)";
		ArrayList<String> res = doQuery(query, 3, new ArrayList<String>(Arrays.asList(patientUname, hashPass, salt)));
		return pass;
	}

	public String shuffle(String input) {
		List<Character> characters = new ArrayList<Character>();
		for (char c : input.toCharArray()) {
			characters.add(c);
		}
		StringBuilder output = new StringBuilder(input.length());
		while (characters.size() != 0) {
			int randPicker = (int) (Math.random() * characters.size());
			output.append(characters.remove(randPicker));
		}
		return output.toString();
	}

	public void createPatientProfile(String patientUname, String first, String last, String add)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String query = "INSERT INTO patients (p_username,first_name,last_name,address) " + "VALUES (?, ?, ?, ?)";
		ArrayList<String> res = doQuery(query, 4, new ArrayList<String>(Arrays.asList(patientUname, first, last, add)));
		createRecord(patientUname);
	}

	public void createRecord(String patientName) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String query = "INSERT INTO records (patient_name,r_hash) " + "VALUES (?,?)";
		ArrayList<String> res = doQuery(query, 2, new ArrayList<String>(Arrays.asList(patientName, "hash")));

		// actualizar coluna r_hash
		String qaux = "SELECT r_id,patient_name,creation_date FROM records WHERE patient_name = ?";
		res = doQuery(qaux, 1, new ArrayList<String>(Arrays.asList(patientName)));

		String linha = "";
		for (int i = 0; i < res.size(); i++) {
			if (i != res.size() - 1) {
				linha += res.get(i);
			}
		}

		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(linha.getBytes("UTF-8"));
		byte[] digest = md.digest();
		linha = String.format("%064x", new java.math.BigInteger(1, digest));

		query = "UPDATE records SET r_hash = ? WHERE patient_name = ?";
		res = doQuery(query, 2, new ArrayList<String>(Arrays.asList(linha, patientName)));

	}

	public void createStaffProfile(String staffUname) {
		String query = "INSERT INTO staff (s_username)" + "VALUES (?)";
		ArrayList<String> res = doQuery(query, 1, new ArrayList<String>(Arrays.asList(staffUname)));
	}

	public void createDoctorProfile(String doctorUname, String specialty) {
		String query = "INSERT INTO doctors (d_username,specialty)" + "VALUES (?, ?)";
		ArrayList<String> res = doQuery(query, 2, new ArrayList<String>(Arrays.asList(doctorUname, specialty)));
		this.generateKeyPair(doctorUname);
	}

	public void addEntry(Integer id, String description, String docName, String spec) {
		String query = "INSERT INTO entry (record_id, description, doc_name, specialty) " + "VALUES (?, ?, ?, ?)";
		ArrayList<String> res = doQuery(query, 4, new ArrayList(Arrays.asList(id, description, docName, spec)));
	}

	public String selectDoctorBySpecialty(String spec) {
		String query = "SELECT d_username FROM doctors WHERE specialty = ?";
		ArrayList<String> res = doQuery(query, 1, new ArrayList<String>(Arrays.asList(spec)));
		if (!res.isEmpty()) {
			return res.get(0);
		}
		return null;
	}

	public boolean isStaff(String u) {
		String query = "SELECT s_username FROM staff WHERE s_username = ?";
		ArrayList<String> res = doQuery(query, 1, new ArrayList<String>(Arrays.asList(u)));
		if (!res.isEmpty()) {
			return true;
		}
		return false;
	}

	public void printEntriesByRecord(Record r) {
		String query = "select entry_Date,doc_name,specialty,description from records r join entry e where r.r_id = e.record_id and r.r_id=?";
		ArrayList<String> res = doQuery(query, 1, new ArrayList<String>(Arrays.asList(r.getId().toString())));
		if (!res.isEmpty()) {
			boolean flag = false;
			for (int i = 0; i < res.size(); i++) {
				if (i == 0) {
					System.out.println("----------");
					System.out.println("Entry #" + res.get(i));
					i = 1;
				}

				if (res.get(i).startsWith("----")) {
					System.out.println(res.get(i));
					flag = true;
				} else {
					if (flag) {
						System.out.println("Entry #" + res.get(i));
						flag = false;
					} else {
						System.out.println(res.get(i));
					}
				}
			}
		}
	}

	public String getEntriesByRecord(Record r) {
		String entries = new String();
		String query = "select entry_Date,doc_name,specialty,description from records r join entry e where r.r_id = e.record_id and r.r_id=?";
		ArrayList<String> res = doQuery(query, 1, new ArrayList<String>(Arrays.asList(r.getId().toString())));
		if (!res.isEmpty()) {
			entries += "----------------Record----------------\n";
			entries += "ID -> " + r.getId() + "\n";
			entries += "Patient Name -> " + r.getPatientName() + "\n";
			entries += "Creation Date -> " + r.getCreationDate() + "\n";
			boolean flag = false;
			for (int i = 0; i < res.size(); i++) {
				if (i == 0) {
					entries += "----------\n";
					entries += "Entry #" + res.get(i) + "\n";
					// System.out.println("----------");
					// System.out.println("Entry #" + res.get(i));
					i = 1;
				}

				if (res.get(i).startsWith("----")) {
					entries += res.get(i) + "\n";
					// System.out.println(res.get(i));
					flag = true;
				} else {
					if (flag) {
						entries += "Entry #" + res.get(i) + "\n";
						// System.out.println("Entry #" + res.get(i));
						flag = false;
					} else {
						entries += res.get(i) + "\n";
						// System.out.println(res.get(i));
					}
				}
			}
		}
		return entries;
	}

	public Record getRecordByUsername(String u) {
		String query = "select patient_name,r_id,creation_date from records r where patient_name=?";
		ArrayList<String> res = doQuery(query, 1, new ArrayList<String>(Arrays.asList(u)));
		if (!res.isEmpty()) {
			// res[0]-patient name; res[1]-record id; res[2]-creation date
			Record record = new Record(res.get(1), res.get(0), res.get(2));
			return record;
		}
		return null;
	}

	public void viewPatientRecordBySpecialty(String patientName, String spec) {
		String query = "select r.r_id,r.patient_name,entry_Date,doc_name,specialty,description from records r join entry e where r.r_id = e.record_id and r.patient_name=? and e.specialty = ?";
		ArrayList<String> res = doQuery(query, 2, new ArrayList<String>(Arrays.asList(patientName, spec)));
		if (!res.isEmpty()) {
			boolean flag = false;
			for (int i = 0; i < res.size(); i++) {
				if (i == 0) {
					System.out.println("Record #" + res.get(i));
					System.out.println("Patient -> " + res.get(1));
					System.out.println("----------");
					i = 2;
				}

				if (res.get(i).startsWith("----")) {
					System.out.println(res.get(i));
					flag = true;
				} else {
					if (flag) {
						System.out.println("Entry #" + res.get(i + 2));
						flag = false;
						i = i + 2;
					} else {
						if (i == 2) {
							System.out.println("Entry #" + res.get(i));
						} else {
							System.out.println(res.get(i));
						}
					}
				}
			}
		}
	}

	public String getPatientRecordBySpecialty(String patientName, String spec) {
		String entries = new String();
		String query = "select r.r_id,r.patient_name,entry_Date,doc_name,specialty,description from records r join entry e where r.r_id = e.record_id and r.patient_name=? and e.specialty = ?";
		ArrayList<String> res = doQuery(query, 2, new ArrayList<String>(Arrays.asList(patientName, spec)));
		if (!res.isEmpty()) {
			boolean flag = false;
			for (int i = 0; i < res.size(); i++) {
				if (i == 0) {
					entries += "Record #" + res.get(i) + "\n";
					entries += "Patient -> " + res.get(1) + "\n";
					entries += "----------" + "\n";
					// System.out.println("Record #" + res.get(i));
					// System.out.println("Patient -> " + res.get(1));
					// System.out.println("----------");
					i = 2;
				}

				if (res.get(i).startsWith("----")) {
					entries += res.get(i) + "\n";
					// System.out.println(res.get(i));
					flag = true;
				} else {
					if (flag) {
						entries += "Entry #" + res.get(i + 2) + "\n";
						// System.out.println("Entry #" + res.get(i + 2));
						flag = false;
						i = i + 2;
					} else {
						if (i == 2) {
							entries += "Entry #" + res.get(i) + "\n";
							// System.out.println("Entry #" + res.get(i));
						} else {
							entries += res.get(i) + "\n";
							// System.out.println(res.get(i));
						}
					}
				}
			}
		}
		return entries;
	}

	public User getUserByUsername(String u) {
		String query = "SELECT username,password FROM users WHERE username = ?";
		ArrayList<String> res = doQuery(query, 1, new ArrayList<String>(Arrays.asList(u)));
		if (!res.isEmpty()) {
			User user = new User(res.get(0), res.get(1));
			return user;
		}
		return null;

	}

	public String getSpecialtyByDoctor(String doctorName) {
		String query = "SELECT specialty FROM doctors WHERE d_username = ?";
		ArrayList<String> res = doQuery(query, 1, new ArrayList<String>(Arrays.asList(doctorName)));
		String especialidade = res.get(0);
		return especialidade;
	}

	public boolean tryLogin(String user, String password) {
		String query = "SELECT password, saltValue FROM users WHERE username = ?";
		ArrayList<String> res = doQuery(query, 1, new ArrayList<String>(Arrays.asList(user)));

		if (!res.isEmpty()) {
			String hashPass = hash(password, res.get(1));
			if (res.get(0).equals(hashPass)) {
				return true;

			}
		}
		return false;

	}

	public String hash(String password, String salt) {
		byte[] bytePass = null;
		try {
			String ultimate = password + salt;
			bytePass = ultimate.getBytes();
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(bytePass);
			bytePass = messageDigest.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return new String(printHexBinary(bytePass));
	}

	// q tem de ser na forma parametrizada
	public ArrayList<String> doQuery(String q, int numArgs, ArrayList args) {

		ResultSet rs = null;
		ArrayList<String> res = new ArrayList<String>();

		try {

			Class.forName(driver);
			Connection conn = DriverManager.getConnection(URL, user, pass);

			// create the java statement
			PreparedStatement st = conn.prepareStatement(q);

			// populate parameters
			for (int i = 1; i <= numArgs; i++) {
				if (args.get(i - 1) instanceof Integer) {
					st.setInt(i, (int) args.get(i - 1));
				} else {
					st.setString(i, (String) args.get(i - 1));// indice
																// parameter
																// comeca em
					// 1; indice get comeca em 0
				}
			}

			if (q.startsWith("SELECT") || q.startsWith("select")) {
				// execute the query, and get a java resultset
				rs = st.executeQuery();

				ResultSetMetaData metadata = rs.getMetaData();
				int numberOfColumns = metadata.getColumnCount();

				while (rs.next()) {
					int i = 1;
					while (i <= numberOfColumns) {
						res.add(rs.getString(i++));
					}
					res.add("----------");
				}
			} else {
				st.executeUpdate();
			}

			st.close();
		} catch (Exception e) {
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
		}

		return res;
	}

	public boolean isDoctor(String u) {
		String query = "SELECT d_username FROM doctors WHERE d_username = ?";
		ArrayList<String> res = doQuery(query, 1, new ArrayList<String>(Arrays.asList(u)));
		if (!res.isEmpty()) {
			return true;
		}
		return false;
	}

	public void changePassword(String username, String newPassword, String newPassword2)
			throws InvalidPasswordException, DifferentPasswordException {
		int tamanhoPassword = newPassword.length();
		String hashPass = null;
		if (tamanhoPassword >= 6 && tamanhoPassword <= 12) {
			if (newPassword.matches(".*[A-Z].*")) {
				if (newPassword.matches(".*[a-z].*")) {
					if (newPassword.matches(".*\\d.*")) {
						System.out.println("Confirme a nova password: ");
						if (newPassword.equals(newPassword2)) {
							String query1 = "SELECT saltValue FROM users WHERE username = ?";
							ArrayList<String> resultado = doQuery(query1, 1,
									new ArrayList<String>(Arrays.asList(username)));
							hashPass = hash(newPassword, resultado.get(0));
							String query = "UPDATE users SET password = ? WHERE username = ?";
							ArrayList<String> res = doQuery(query, 2,
									new ArrayList<String>(Arrays.asList(hashPass, username)));
							System.out.println("Password alterada com sucesso!");
							return;
						} else {
							throw new DifferentPasswordException();
						}
					}
				}
			}
		}
		throw new InvalidPasswordException();
	}

	public boolean usernameExists(String u) {
		String query = "SELECT username FROM users WHERE username = ?";
		ArrayList<String> res = doQuery(query, 1, new ArrayList<String>(Arrays.asList(u)));
		if (!res.isEmpty()) {
			return true;
		}
		return false;
	}

	public boolean isRoot(String username) {
		if (username.equals(rootUsername)) {
			return true;
		}
		return false;
	}

	public boolean isSpecialty(String spec) {
		String query = "SELECT * FROM specialties WHERE spec_name = ?";
		ArrayList<String> res = doQuery(query, 1, new ArrayList<String>(Arrays.asList(spec)));
		if (!res.isEmpty()) {
			return true;
		}
		return false;
	}

	public boolean entryExists(String entryArg) {
		String query = "SELECT * FROM entry WHERE entry_id = ?";
		ArrayList<String> res = doQuery(query, 1, new ArrayList<String>(Arrays.asList(entryArg)));
		if (!res.isEmpty())
			return true;
		return false;

	}

	public void createSpecialty(String name) {
		String query = "INSERT INTO specialties (spec_name)" + "VALUES (?)";
		ArrayList<String> res = doQuery(query, 1, new ArrayList<String>(Arrays.asList(name)));
	}

	public String getUserType(String username) {
		String type = new String();
		String queryDoctor = "SELECT d_username FROM doctors WHERE d_username = ?";
		ArrayList<String> resDoctor = doQuery(queryDoctor, 1, new ArrayList<String>(Arrays.asList(username)));
		if (!resDoctor.isEmpty())
			return "doctor";

		String queryStaff = "SELECT s_username FROM staff WHERE s_username = ?";
		ArrayList<String> resStaff = doQuery(queryStaff, 1, new ArrayList<String>(Arrays.asList(username)));
		if (!resStaff.isEmpty())
			return "staff";

		String queryPatient = "SELECT p_username FROM staff WHERE p_username = ?";
		ArrayList<String> resPatient = doQuery(queryPatient, 1, new ArrayList<String>(Arrays.asList(username)));
		if (!resPatient.isEmpty())
			return "patient";

		return "nothing";
	}

	public void changeEmergencyStatus(String d, boolean e) {
		String emerg = "";
		if(e) emerg = "true";
		else emerg = "false";
		System.out.println("Estou no changeEmergency "+d+" para "+emerg);
		String q = "UPDATE doctors SET emergency = ? WHERE d_username = ?";
		ArrayList<String> resPatient = doQuery(q, 2, new ArrayList<String>(Arrays.asList(emerg,d)));
		
	}

	public String getEmergencyMode(String username) {
		String q = "SELECT emergency FROM doctors WHERE d_username = ?";
		ArrayList<String> res = doQuery(q, 1, new ArrayList<String>(Arrays.asList(username)));
		if (!res.isEmpty()){
			return res.get(0);
		}
		return "";
	}
}
