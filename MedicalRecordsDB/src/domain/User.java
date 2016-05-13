package domain;

public class User {
	private String username;
	private String pass;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}
	
	public User(String u, String p){
		this.username=u;
		this.pass=p;
	}
	
	public boolean logIn(){
		DBManager dbmg = new DBManager();
		return dbmg.tryLogin(this.username, this.pass);
	}

}
