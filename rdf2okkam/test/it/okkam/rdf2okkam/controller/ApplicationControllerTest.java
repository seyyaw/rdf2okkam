package it.okkam.rdf2okkam.controller;

import static org.junit.Assert.*;

import it.okkam.rdf2okkam.controller.ApplicationController;
import it.okkam.rdf2okkam.ens.EntityBuilderTest;
import it.okkam.rdf2okkam.model.ModelLoader;
import it.okkam.rdf2okkam.model.ModelUtil;
import it.okkam.rdf2okkam.parser.Globalizer;

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

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.FileManager;

public class ApplicationControllerTest {
	
	private final String ensNS = "http://models.okkam.org/ENS-core-vocabulary.owl#";
	private final String ENS_LOCATION = ensNS + "location" ;
	private final String rdfNS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	private final String RDF_TYPE = rdfNS + "type" ;
	String modelFileName = "resources/dataset_out.ttl" ;
	private final String inputDatasetFileName = "resources/test/anagrafe1.ttl" ;
	private final String outputDatasetFileName = "resources/test/anagrafe1_out.ttl" ;
	final String RDF_SYNTAX = "TURTLE" ;
	String baseUri = null ;
	ModelLoader loader = null ;
	ApplicationController manager = null ;
	Globalizer global = null ;
	
	private static Log log = LogFactory.getLog(ApplicationControllerTest.class);

	@Before
	public void setUp() throws Exception {
		
		loader = ModelLoader.getInstance() ;
		
		global = new Globalizer(loader.getInputModel(), loader.getOutputModel()) ;
		
	}

	@Test
	public void testCreateEntity() {
		log.info("----------testCreateEntity--------------") ;
		
		manager = new ApplicationController() ;
		
		Set<RDFNode> distSubjs = global.getDistinctSubjects() ;
		Map<String, String> bnodeOkkamId = new HashMap<String, String>() ;
		Iterator<RDFNode> idistSubj = distSubjs.iterator() ;
		while(idistSubj.hasNext()) {
			RDFNode distSubj = idistSubj.next() ;
						
			String okkamid = manager.createEntity(distSubj) ;
			bnodeOkkamId.put(distSubj.toString(), okkamid) ;
			
			log.info("New entity's okkam id: " + okkamid) ;		
		}
			
		ModelUtil mockup = new ModelUtil() ;
		
		Model output = mockup.modifyRDF(bnodeOkkamId) ;
		
	}

}
