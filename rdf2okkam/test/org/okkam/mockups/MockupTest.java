package org.okkam.mockups;

import static org.junit.Assert.*;

import it.okkam.rdf2okkam.model.ModelUtil;
import it.okkam.rdf2okkam.parser.GetSubjects;

import java.io.FileNotFoundException;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;

public class MockupTest {
	
	Model model = null ;
	ModelUtil parser = null ;
	GetSubjects subjects = null ;
	String inputFileName = "resources/anagrafe1.ttl";
	String[][] statment = null ;
	
	@Before
	public void setUp() throws Exception {
		parser = new ModelUtil() ;
		parser.loadModel(inputFileName) ;
		subjects = new GetSubjects() ;
		subjects.loadModel(inputFileName) ;
		Iterator it = subjects.getSubjects(inputFileName).iterator();
		statment = subjects.getProperties(it);
	}

	@Test
	public void testGetOkkamId() throws FileNotFoundException {
		
		String[][] okkamIds = parser.getOkkamId(statment) ;
		
	}

	@Test
	public void testModifyRDF() {
		fail("Not yet implemented");
	}

}
