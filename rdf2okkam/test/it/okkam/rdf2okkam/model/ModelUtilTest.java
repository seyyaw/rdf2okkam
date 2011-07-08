package it.okkam.rdf2okkam.model;

import static org.junit.Assert.*;

import it.okkam.rdf2okkam.model.ModelUtil;
import it.okkam.rdf2okkam.parser.GetSubjects;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class ModelUtilTest {
	
	Model model = null ;
	ModelUtil parser = null ;
	GetSubjects subjects = null ;
	String inputFileName = "resources/anagrafe1.ttl";
	String[][] statment = null ;
	
	static ModelLoader loader = null ;
	static GetSubjects getsubjects;
	
	ModelUtil modelUtil=null;
	@Before
	public void setUp() throws Exception {
		loader = ModelLoader.getInstance() ;
		model=loader.getInputModel();
		
		modelUtil=new ModelUtil();
	}

	@Test
	public void testGetOkkamId() throws FileNotFoundException {
		
		String[][] okkamIds = parser.getOkkamId(statment) ;
		
	}

	@Test
	public void testModifyRDF() throws FileNotFoundException {
		loader = ModelLoader.getInstance() ;
		model=loader.getInputModel();
		//File outrdf = new File(inputFileName3);
		//GetSubjects.loadModel(inputFileName);
		//loadModel(inputFileName);	
		 getsubjects=new GetSubjects();
		Iterator it = getsubjects.getSubjects().iterator();
	//	String[][] statment = GetSubjects.getProperties(it);
		Map<String,String> bnodeokkamid=modelUtil.bnodeOkkamId(it);
		Model result=ModelFactory.createDefaultModel();
		result=ModelUtil.modifyRDF(bnodeokkamid);
		result.write(System.out,"TURTLE");
	}

}
