package br.ufes.inf.nemo.backend.checkontouml;

import java.io.IOException;

import org.eclipse.emf.ecore.resource.Resource;

import RefOntoUML.parser.SyntacticVerificator;
import RefOntoUML.util.RefOntoUMLResourceUtil;;

public class Main {

	public static void main(String[] args) {
		if(args.length < 1) {
			System.out.println("Informe o nome do arquivo do modelo a ser verificado!");
			return;
		}
		
		try {
			String modelFilePath = args[0];
			
			// Modelo a partir do local de um arquivo .refontouml
			Resource resource = RefOntoUMLResourceUtil.loadModel(modelFilePath);
			RefOntoUML.Package model = (RefOntoUML.Package) resource.getContents().get(0);
		
			// Verificação sintática do modelo
			SyntacticVerificator verificator = new SyntacticVerificator();
			verificator.run(model);
			System.out.println(verificator.getResult());
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}