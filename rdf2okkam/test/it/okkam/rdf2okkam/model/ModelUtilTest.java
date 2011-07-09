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
	GetSubjects subjects = null ;
	
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
		
		
	}

	@Test
	public void testModifyRDF() throws FileNotFoundException {
		loader = ModelLoader.getInstance() ;
		model=loader.getInputModel();	
		 getsubjects=new GetSubjects();
		Iterator it = getsubjects.getSubjects().iterator();
		Map<String,String> bnodeokkamid=modelUtil.bnodeOkkamId(it);
		Model result=ModelFactory.createDefaultModel();
		result=ModelUtil.modifyRDF(bnodeokkamid);
		result.write(System.out,"TURTLE");
	}

}
