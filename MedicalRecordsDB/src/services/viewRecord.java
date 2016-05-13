package services;

import domain.DBManager;
import domain.Record;
import exception.CantLoginException;

public class viewRecord {

	public final String execute(String username, String password) throws CantLoginException {
		DBManager db = new DBManager();
		if (!db.tryLogin(username, password)) {
			throw new CantLoginException();
		}

		Record rec = db.getRecordByUsername(username);
		System.out.println("----------------Record----------------");
		System.out.println("ID -> " + rec.getId());
		System.out.println("Patient Name -> " + rec.getPatientName());
		System.out.println("Creation Date -> " + rec.getCreationDate());

		//db.printEntriesByRecord(rec);
		return db.getEntriesByRecord(rec);

	}
}
