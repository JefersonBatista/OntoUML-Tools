package net.menthor.backend.ontouml2als;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.eclipse.ocl.ParserException;
import org.eclipse.uml2.uml.Constraint;

import RefOntoUML.parser.OntoUMLParser;
import net.menthor.ontouml2alloy.OntoUML2AlloyOptions;
import net.menthor.tocl.parser.TOCLParser;
import net.menthor.tocl.tocl2alloy.TOCL2AlloyOption;

public class AlloyGenerator {
	private AlloySpec alloySpec = new AlloySpec();
    private OntoUML2AlloyOptions refOptions;
	private TOCL2AlloyOption oclOptions;
	
	public AlloyGenerator(OntoUMLParser refparser, OntoUML2AlloyOptions refOptions, String tmpFile, String modelName, String oclText) {
		TOCLParser toclparser = new TOCLParser(refparser, tmpFile, modelName);
		String oclEmbeddedText = refparser.getAllConstraintsAsText();
		String finalOclDoc = oclText+"\n"+oclEmbeddedText;
		try {
			toclparser.parseTemporalOCL(finalOclDoc);
		} catch (ParserException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.oclOptions = new TOCL2AlloyOption(toclparser);
		
		// Inserindo os axiomas
		this.refOptions = refOptions;
		this.refOptions.check(refparser);
	}
	
	public AlloyGenerator(OntoUMLParser refparser, OntoUML2AlloyOptions refOptions, String tmpFile, String modelName) {
		this(refparser, refOptions, tmpFile, modelName, "");
	}
	
	public AlloyGenerator(OntoUMLParser refparser, OntoUML2AlloyOptions refOptions, TOCL2AlloyOption oclOptions) {
		this.oclOptions = oclOptions;
		
		// Inserindo os axiomas
		this.refOptions = refOptions;
		this.refOptions.check(refparser);
	}
	
	public AlloySpec getAlloySpec() {
		return alloySpec;
	}
	
	/** run the transformation to alloy */
	public String generateAlloy(OntoUMLParser refparser, String outFilePath) {
		String logMessage = "";
		try {
			alloySpec.setAlloyPath(outFilePath);
			runOntouml(refparser);
			logMessage = runOcl(refparser);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return logMessage;
	}
	
	private void runOntouml(OntoUMLParser refparser) throws Exception {		
		alloySpec.setDomainModel(refparser,refOptions);
		alloySpec.transformDomainModel();	
	}
	
	private String runOcl(OntoUMLParser refparser) {						
		String logMessage = "";
		try {
			logMessage = alloySpec.transformConstraints(refparser, oclOptions.getParser(),oclOptions);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return logMessage;
	}
	
	// Método apenas para exibir restrições OCL
	public static void showTOCL2AlloyOption(TOCL2AlloyOption options) {
		System.out.println();
		System.out.println(options.getConstraintList());
		System.out.println("******************* Show TOCL2Alloy Option **************************");
		
		List<Constraint> constraints = options.getConstraintList();
		for(Constraint ct : constraints) {
			System.out.println("Constraint: " + ct.getLabel() + "-" + ct.getLabel() + "-" + ct.getQualifiedName());
			System.out.println("Constraint type: " + options.getConstraintType(ct));
			System.out.println("TransformationType" + options.getTransformationType(ct));
			System.out.println("CommandScope: " + options.getCommandScope(ct));
			System.out.println("CommandBitwidth: " + options.getCommandBitwidth(ct));
			System.out.println("WorldScope: " + options.getWorldScope(ct));
			System.out.println();
		}
		
		System.out.println("**************************** End ************************************");
		System.out.println();
	}
}
