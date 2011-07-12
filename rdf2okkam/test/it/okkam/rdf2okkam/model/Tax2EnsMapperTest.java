package it.okkam.rdf2okkam.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class Tax2EnsMapperTest {

	Tax2EnsMapper mapper = null ;
	@Before
	public void setUp() throws Exception {
		mapper = new Tax2EnsMapper() ; 
	}

	@Test
	public void testStartInference() {
		mapper.startInference(ModelLoader.getInstance().getInputModel()) ;
		ModelLoader.getInstance().getInputModel().write(System.out, "TURTLE") ;
	}

}
