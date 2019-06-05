package net.menthor.backend.ontouml2als;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import RefOntoUML.parser.OntoUMLParser;
import net.menthor.ontouml2alloy.OntoUML2AlloyOptions;

public class Main {
	public static void main(String[] args) {
		runWithParams();
	}
	
	public static void runWithParams() {
		Properties prop = readParameters();
		String modelFilePath = prop.getProperty("MODEL_FILE_PATH");
		String alloyOutPath = prop.getProperty("ALLOY_OUT_PATH");
		
		OntoUML2AlloyOptions ontoUmlOptions = new OntoUML2AlloyOptions();
		ontoUmlOptions.weakSupplementation = toBooleanParameter(prop.getProperty("WEAK_SUPPLEMENTATION"));
		ontoUmlOptions.relatorConstraint = toBooleanParameter(prop.getProperty("RELATOR_CONSTRAINT"));
		ontoUmlOptions.identityPrinciple = toBooleanParameter(prop.getProperty("IDENTITY_PRINCIPLE"));
		ontoUmlOptions.antiRigidity = toBooleanParameter(prop.getProperty("ANTIRIGIDITY_VISUALIZATION"));
		
		try {
			String oclTextPath = prop.getProperty("OCL_TEXT_PATH");
			String oclText = "";
			if(! oclTextPath.isEmpty()) {
				FileReader fr = new FileReader(oclTextPath);
				BufferedReader br = new BufferedReader(fr);
				
				while(br.ready()) {
					oclText += br.readLine();
					oclText += System.lineSeparator();
				}
				
				br.close();
				fr.close();
			}
		
		
			OntoUMLParser refparser = new OntoUMLParser(modelFilePath);
			
			String tmpFolder = "tmp";
			String modelName = "model";
			
			AlloyGenerator alloyGen = new AlloyGenerator(refparser, ontoUmlOptions, tmpFolder, modelName, oclText);
			
			// Possíveis axiomas inválidos
			if(ontoUmlOptions.weakSupplementationInvalid) 
				System.err.println(ontoUmlOptions.weakSupplementationInvalidMsg);
			if(ontoUmlOptions.relatorConstraintInvalid)
				System.err.println(ontoUmlOptions.relatorConstraintInvalidMsg);
			if(ontoUmlOptions.identityPrincipleInvalid)
				System.err.println(ontoUmlOptions.identityPrincipleInvalidMsg);
			
			alloyGen.generateAlloy(refparser, alloyOutPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void runWithArgs(String[] args) {
		if(args.length < 3) {
			System.out.println("Número insuficiente de parâmetros de entrada!");
			return;
		}
		
		String modelFilePath = args[0];
		String alsOutPath = args[1];
		String axiomsFilePath = args[2];
		String oclText = "";
		
		try {
			OntoUMLParser refparser = new OntoUMLParser(modelFilePath);
			
			FileReader fr = new FileReader(axiomsFilePath);
			BufferedReader br = new BufferedReader(fr);
			Set<String> axioms = new TreeSet<>();
			
			while(br.ready()) {
				String axiom = br.readLine();
				axioms.add(axiom.trim().toLowerCase());
			}
			
			OntoUML2AlloyOptions ontoUmlOptions = new OntoUML2AlloyOptions();
			ontoUmlOptions.weakSupplementation = axioms.contains("weak supplementation");
			ontoUmlOptions.relatorConstraint = axioms.contains("relator constraint");
			ontoUmlOptions.antiRigidity = axioms.contains("anti rigidity") || axioms.contains("anti-rigidity");
			ontoUmlOptions.identityPrinciple = axioms.contains("identity principle");
			
			// Axiomas escolhidos
			System.out.println("Chosen axioms:");
			if(ontoUmlOptions.weakSupplementation) System.out.println("Weak supplementation");
			if(ontoUmlOptions.relatorConstraint) System.out.println("Relator constraint");
			if(ontoUmlOptions.antiRigidity) System.out.println("Anti-Rigidity");
			if(ontoUmlOptions.identityPrinciple) System.out.println("Identity principle");
			System.out.println();
			
			br.close();
			fr.close();
			
			if(args.length > 3) {
				String oclTextPath = args[3];
				fr = new FileReader(oclTextPath);
				br = new BufferedReader(fr);
				
				while(br.ready()) {
					oclText += br.readLine();
					oclText += System.lineSeparator();
				}
				
				br.close();
				fr.close();
			}
			
			String tmpFile = "/home/jbatista/Documentos/toclparser";
			String modelName = "model";
			AlloyGenerator alloyGen = new AlloyGenerator(refparser, ontoUmlOptions, tmpFile, modelName, oclText);
			
			// Possíveis axiomas inválidos
			if(ontoUmlOptions.weakSupplementationInvalid) 
				System.err.println(ontoUmlOptions.weakSupplementationInvalidMsg);
			if(ontoUmlOptions.relatorConstraintInvalid)
				System.err.println(ontoUmlOptions.relatorConstraintInvalidMsg);
			if(ontoUmlOptions.identityPrincipleInvalid)
				System.err.println(ontoUmlOptions.identityPrincipleInvalidMsg);
			
			alloyGen.generateAlloy(refparser, alsOutPath);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void defaultParameters() {
		// Setting default parameters
		Properties prop = new Properties();
		prop.put("MODEL_FILE_PATH", "example.refontouml");
		prop.put("ALLOY_OUT_PATH", "out.als");
		prop.put("OCL_TEXT_PATH", "");
		prop.put("WEAK_SUPPLEMENTATION", "ON");
		prop.put("RELATOR_CONSTRAINT", "ON");
		prop.put("IDENTITY_PRINCIPLE", "ON");
		prop.put("ANTIRIGIDITY_VISUALIZATION", "OFF");
		
		OutputStream output;
		try {
			output = new FileOutputStream("config.properties");
			String comments = "\'ON\' to turn on the axiom. \'OFF\' to turn off the axiom." + System.lineSeparator()
				+ "Let OCL_TEXT_PATH's value empty, to indicate an empty ocl text.";
			prop.store(output, comments);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Properties readParameters() {
		Properties prop = new Properties();
		try {
			InputStream input = new FileInputStream("config.properties");
			prop.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;
	}
	
	public static boolean toBooleanParameter(String value) {
		switch(value.toUpperCase()) {
			case "ON":
				return true;
			default:
				return false;
		}
	}
}
