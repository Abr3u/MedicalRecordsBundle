package domain;

import exception.missingResourceSpecArgumentsException;
import exception.missingDoctorArgumentsException;
import exception.missingPatientArgumentsException;
import exception.missingResourceIDArgumentsException;

public class Rule {
	private String permit;
	private String subject;
	private String action;
	private String resource;
	private String condition;

	public Rule(String p, String s, String a, String r, String cond) {
		setPermit(p);
		setSubject(s);
		setAction(a);
		setResource(r);
		setCondition(cond);
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getPermit() {
		return permit;
	}

	public void setPermit(String permit) {
		this.permit = permit;
	}

	public boolean checkArguments(String subject2, String action2, String resource2)
			throws missingDoctorArgumentsException, missingResourceSpecArgumentsException, missingPatientArgumentsException, missingResourceIDArgumentsException {

		if (!condition.isEmpty()) {
			boolean passedCondition = false;
			if (subject2.contains(subject) && action2.contains(action) && resource2.contains(resource)) {

				String scond = "", acond = "", rcond = "";
				String aux[] = this.condition.split("=");

				for (int i = 0; i < aux.length; i++) {
					if (aux[i].startsWith("subject")) {
						scond = aux[i].split("/")[1];
					}
					if (aux[i].startsWith("action")) {
						acond = aux[i].split("/")[1];
					}
					if (aux[i].startsWith("resource")) {
						rcond = aux[i].split("/")[1];
					}
				}
				// cond envolve sujeito
				if (!scond.isEmpty()) {
					// cond envolve resource
					if (!rcond.isEmpty()) {
						aux = subject2.split("\\+");
						
						if (subject2.startsWith("doctor")) {
							//condicao envolve doctor
							// 0 - doctor; 1 - mode; 2 - spec
							if (aux.length < 3) {
								throw new missingDoctorArgumentsException();
							}
							String sc = aux[2];

							aux = resource2.split("\\+");
							// 0 - resource; 1 - spec
							if (aux.length < 2) {
								throw new missingResourceSpecArgumentsException();
							}
							String rc = aux[1];

							if (sc.equals(rc)) {
								passedCondition = true;
							}
						}
						if(subject2.startsWith("patient")){
							//condicao envolve patient
							// 0 - patient; 1 - recordID
							if (aux.length < 2) {
								throw new missingPatientArgumentsException();
							}
							String sc = aux[1];

							aux = resource2.split("\\+");
							// 0 - resource; 1 - id
							if (aux.length < 2) {
								throw new missingResourceIDArgumentsException();
							}
							String rc = aux[1];

							if (sc.equals(rc)) {
								passedCondition = true;
							}
						}
					}
				}
				if (passedCondition) {
					if (subject2.startsWith(subject) && action2.startsWith(action) && resource2.startsWith(resource)
							&& this.permit.equals("Permit")) {
						return true;
					}
				}
			}
		}
		// no condition
		else {
			if (subject2.contains("emergency")) {
				return true;
			}

			// se encaixa nesta regra
			if (subject.equals(subject2) && action.equals(action2) && resource.equals(resource2)) {
				// se for para deixar
				if (this.permit.equals("Permit")) {
					return true;
				}
				// nao deixar
				else {
					return false;
				}
			}

		}

		return false;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
}
