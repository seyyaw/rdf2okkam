package org.okkam.application;

import static org.junit.Assert.*;

import it.okkam.rdf2okkam.controller.ApplicationController;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.okkam.client.data.AttributeType;
import org.okkam.client.data.AttributesType;
import org.okkam.dataset.parser.EntityBuilderTest;
import org.okkam.dataset.parser.Globalizer;
import org.okkam.mockups.Mockup;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.FileManager;

public class ApplicationManagerTest {
	
	private final String ensNS = "http://models.okkam.org/ENS-core-vocabulary.owl#";
	private final String ENS_LOCATION = ensNS + "location" ;
	private final String rdfNS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	private final String RDF_TYPE = rdfNS + "type" ;
	String modelFileName = "resources/dataset_out.ttl" ;
	final String RDF_SYNTAX = "TURTLE" ;
	String baseUri = null ;
	Model _model = null ;
	Model _outModel = null ;
	ApplicationController manager = null ;
	Globalizer global = null ;
	
	private static Log log = LogFactory.getLog(ApplicationManagerTest.class);

	@Before
	public void setUp() throws Exception {
		
		// use the FileManager to find the input file
		InputStream inModelStream = FileManager.get().open( modelFileName );
		if (inModelStream == null) {		    
			log.error("File: " + modelFileName + " not found") ;
			System.exit(0);
		}

		// Create the input model. Models different from the default one import also the 
		// rdf and rdf-schema axioms. 
		_model = ModelFactory.createDefaultModel();
		
		// read the RDF/TURTLE file
		_model.read(inModelStream, baseUri, "TURTLE");
		
		// create the output model
		_outModel = ModelFactory.createDefaultModel() ;	
		
		global = new Globalizer(_model, _outModel) ;
		
	}

	@Test
	public void testCreateEntity() {
		log.info("----------testCreateEntity--------------") ;
		
		manager = new ApplicationController(_model) ;
		
		Set<RDFNode> distSubjs = global.getDistinctSubjects() ;
		Map<String, String> bnodeOkkamId = new HashMap<String, String>() ;
		Iterator<RDFNode> idistSubj = distSubjs.iterator() ;
		while(idistSubj.hasNext()) {
			RDFNode distSubj = idistSubj.next() ;
						
			String okkamid = manager.createEntity(distSubj) ;
			bnodeOkkamId.put(distSubj.toString(), okkamid) ;
			
			log.info("New entity's okkam id: " + okkamid) ;		
		}
			
		Mockup mockup = new Mockup() ;
		
		Model output = mockup.modifyRDF(bnodeOkkamId) ;
		
	}

}
