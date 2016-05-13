package domain;

import java.awt.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import exception.missingResourceSpecArgumentsException;
import exception.missingDoctorArgumentsException;
import exception.missingPatientArgumentsException;
import exception.missingResourceIDArgumentsException;

public class AccessControl {

	public boolean checkPolicies(String filePath, String subject, String action, String resource)
			throws ParserConfigurationException, SAXException, IOException, missingDoctorArgumentsException, missingResourceSpecArgumentsException, missingPatientArgumentsException, missingResourceIDArgumentsException {
		
		File file = new File("/home/rui/MedicalRecordsDB/src-gen/accessControl.main.xml");
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(file);

		document.getDocumentElement().normalize();
		
		boolean result = false;
		int numPolicies = document.getElementsByTagName("xacml3:PolicyIdReference").getLength();

		// analisar policies
		for(int j = 0;j<numPolicies;j++){
			
			String policyCell = document.getElementsByTagName("xacml3:PolicyIdReference").item(j).getTextContent();
			String[] aux = policyCell.split("/");
			String policyName = aux[aux.length - 1];
			
			String path ="/home/rui/MedicalRecordsDB/src-gen/";
			file = new File(path+policyName+".xml");
			result = analysePolicy(file,documentBuilderFactory,documentBuilder,document,subject,action,resource);
			if(result){
				writeToLog(subject,action,resource,true,new Date());
				return true;
			}
		}
		writeToLog(subject,action,resource,false,new Date());
		return false;
		
	}

	public void writeToLog(String subject, String action, String resource, boolean b, Date dt) {
		String logPath = "/home/rui/MedicalRecordsDB/log.txt";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = df.format(dt);
		try {
			File f = new File(logPath);
			FileWriter fileWriter = null;
			
			if(f.exists() && !f.isDirectory()) {//append
				fileWriter = new FileWriter(logPath,true);
			}
			else{//create
				fileWriter = new FileWriter(logPath);
			}
			
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			String line = "";
			if(b){
				line = "@"+date+" || Access GRANTED :: Subject -> "+subject+"; Action -> "+action+"; Resource -> "+resource;
			}
			else{
				line = "@"+date+" || Acecss DENIED :: Subject -> "+subject+"; Action -> "+action+"; Resource -> "+resource;
			}
			
			bufferedWriter.write(line);
			bufferedWriter.newLine();
			bufferedWriter.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	private boolean analysePolicy(File file, DocumentBuilderFactory documentBuilderFactory, DocumentBuilder documentBuilder, Document document, String subject, String action, String resource) throws ParserConfigurationException, SAXException, IOException, missingDoctorArgumentsException, missingResourceSpecArgumentsException, missingPatientArgumentsException, missingResourceIDArgumentsException {
		documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilder = documentBuilderFactory.newDocumentBuilder();
		document = documentBuilder.parse(file);

		document.getDocumentElement().normalize();

		ArrayList<String> defaults = new ArrayList<String>();

		// get defaults for all rules
		defaults = getDefaults(document.getElementsByTagName("xacml3:Policy").item(0).getChildNodes());
		String Dsub = "";
		String Dact = "";
		String Dres = "";

		for (String ss : defaults) {

			if (ss.startsWith("subject")) {
				Dsub = ss.split("%%")[1];
			}
			if (ss.startsWith("action")) {
				Dact = ss.split("%%")[1];
			}
			if (ss.startsWith("resource")) {
				Dres = ss.split("%%")[1];
			}
		}

		Integer ruleCount = 0;
		// procurar filhos de Rule NodeList
		NodeList nodelist = document.getElementsByTagName("xacml3:Rule").item(ruleCount).getChildNodes();

		Element eElement = (Element) document.getElementsByTagName("xacml3:Rule").item(ruleCount);
		String effect = eElement.getAttribute("Effect");
		String permission = (effect.equals("Permit")) ? "Permit" : "Deny";
		ruleCount++;

		ArrayList<Rule> listRules = iterateNodeList(nodelist, permission, Dsub, Dact, Dres);

		// enquanto existir mais rules
		while (ruleCount < document.getElementsByTagName("xacml3:Rule").getLength()) {

			eElement = (Element) document.getElementsByTagName("xacml3:Rule").item(ruleCount);
			effect = eElement.getAttribute("Effect");
			permission = (effect.equals("Permit")) ? "Permit" : "Deny";

			listRules.addAll(
					iterateNodeList(document.getElementsByTagName("xacml3:Rule").item(ruleCount).getChildNodes(),
							permission, Dsub, Dact, Dres));
			ruleCount++;
		}

		// print the rules that we got from parsing
		

		
		// Tomar decisao
		for (int i = 0; i < listRules.size(); i++) {
			Rule rule = listRules.get(i);
			if (rule.checkArguments(subject, action, resource)) {
				return true;
			}
		}

		return false;
		
	}

	private ArrayList<String> getDefaults(NodeList nodelist) {

		ArrayList<String> reStrings = new ArrayList<String>();

		String attributeType = null;
		String attributeValue = null;

		for (int a = 0; a < nodelist.getLength(); a++) {
			if (nodelist.item(a).getNodeName().equals("xacml3:Target")) {
				NodeList nl = nodelist.item(a).getChildNodes();
				for (int i = 0; i < nl.getLength(); i++) {
					if (nl.item(i).getNodeName().equals("xacml3:AnyOf")) {
						nl = nl.item(i).getChildNodes();
						for (i = 0; i < nl.getLength(); i++) {
							if (nl.item(i).getNodeName().equals("xacml3:AllOf")) {
								nl = nl.item(i).getChildNodes();
								for (i = 0; i < nl.getLength(); i++) {
									if (nl.item(i).getNodeName().equals("xacml3:Match")) {
										NodeList newl = nl.item(i).getChildNodes();
										for (int z = 0; z < newl.getLength(); z++) {
											// encontrei attribute value
											if (newl.item(z).getNodeName().equals("xacml3:AttributeValue")) {
												attributeValue = newl.item(z).getTextContent();
											}
											// encontrei attribute designator
											if (newl.item(z).getNodeName().equals("xacml3:AttributeDesignator")) {
												Element eElement = (Element) newl.item(z);
												attributeType = eElement.getAttribute("Category");
												String[] aux = attributeType.split(":");
												attributeType = aux[aux.length - 1];

												// quando chega aqui ja tem
												// attributeValue
												String attr = attributeType + "%%" + attributeValue;
												reStrings.add(attr);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return reStrings;
	}

	private ArrayList<Rule> iterateNodeList(NodeList nodelist, String permit, String dSub, String dAct, String dRes) {
		ArrayList<Rule> rules = new ArrayList<Rule>();

		String attributeType = null;
		String attributeValue = null;
		String condition = "";

		// procurar condition
		for (int y = 0; y < nodelist.getLength(); y++) {
			if (nodelist.item(y).getNodeName().equals("xacml3:Condition")) {
				String condCategory = "";
				String condValue = "";
				NodeList nl = nodelist.item(y).getChildNodes();
				for (int i = 0; i < nl.getLength(); i++) {
					if (nl.item(i).getNodeName().equals("xacml3:Apply")) {
						NodeList newl = nl.item(i).getChildNodes();
						for (int z = 0; z < newl.getLength(); z++) {
							if (newl.item(z).getNodeName().equals("xacml3:AttributeDesignator")) {
								Element eElement = (Element) newl.item(z);

								condCategory = eElement.getAttribute("Category");
								String aux[] = condCategory.split(":");
								condCategory = aux[aux.length - 1];

								if (condCategory.contains("subject") && !condCategory.equals("subject")) {
									aux = condCategory.split("-");
									condCategory = aux[aux.length - 1];
								}

								condValue = eElement.getAttribute("AttributeId");

								if (condition.isEmpty()) {
									condition = condCategory + "/" + condValue;
								} else {
									condition += "=" + condCategory + "/" + condValue;
								}
							}
						}
					}
				}

			}
		}

		// procurar Regra
		for (int a = 0; a < nodelist.getLength(); a++) {

			if (nodelist.item(a).getNodeName().equals("xacml3:Description")) {
				//System.out.println("Rule: " + nodelist.item(a).getTextContent());
			}
			// procurar Target em filhos de Rules
			if (nodelist.item(a).getNodeName().equals("xacml3:Target")) {
				NodeList nl = nodelist.item(a).getChildNodes();
				for (int i = 0; i < nl.getLength(); i++) {
					if (nl.item(i).getNodeName().equals("xacml3:AnyOf")) {
						nl = nl.item(i).getChildNodes();
						for (i = 0; i < nl.getLength(); i++) {
							if (nl.item(i).getNodeName().equals("xacml3:AllOf")) {
								nl = nl.item(i).getChildNodes();
								for (i = 0; i < nl.getLength(); i++) {
									if (nl.item(i).getNodeName().equals("xacml3:Match")) {
										NodeList newl = nl.item(i).getChildNodes();
										for (int z = 0; z < newl.getLength(); z++) {
											String sub = "";
											String act = "";
											String res = "";

											// encontrei attribute value
											if (newl.item(z).getNodeName().equals("xacml3:AttributeValue")) {
												attributeValue = newl.item(z).getTextContent();
											}
											// encontrei attribute designator
											if (newl.item(z).getNodeName().equals("xacml3:AttributeDesignator")) {
												Element eElement = (Element) newl.item(z);
												attributeType = eElement.getAttribute("Category");
												String[] aux = attributeType.split(":");
												attributeType = aux[aux.length - 1];

												// quando chega aqui ja tem
												// attributeValue
												if (attributeType.contains("subject")) {
													sub = attributeValue;
												}
												if (attributeType.startsWith("action")) {
													act = attributeValue;
												}
												if (attributeType.startsWith("resource")) {
													res = attributeValue;
												}
												// actualizar globais

												if (dSub.isEmpty()) {
													dSub = sub;
												} else {
													if (!sub.isEmpty())
														dSub = dSub + "+" + sub;
												}

												if (dAct.isEmpty()) {
													dAct = act;
												} else {
													if (!act.isEmpty())
														dAct = dAct + "+" + act;
												}

												if (dRes.isEmpty()) {
													dRes = res;
												} else {
													if (!res.isEmpty())
														dRes = dRes + "+" + res;
												}

											}
										}
									}
								}
							}
						}
					}
				}
				rules.add(new Rule(permit, dSub, dAct, dRes, condition));
			}
		}
		return rules;
	}

}
