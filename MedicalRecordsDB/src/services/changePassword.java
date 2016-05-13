package services;

import domain.DBManager;
import domain.Record;
import exception.CantLoginException;
import exception.DifferentPasswordException;
import exception.InvalidPasswordException;

public class changePassword {

	public final void execute(String username, String password, String newPassword, String newPassword2) throws CantLoginException, DifferentPasswordException, InvalidPasswordException {
		DBManager db = new DBManager();

		if (!db.tryLogin(username, password)) {
			throw new CantLoginException();
		}
		db.changePassword(username, newPassword, newPassword2);
	}
}
