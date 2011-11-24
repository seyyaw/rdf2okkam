package it.okkam.rdf2okkam.controller;

import static org.junit.Assert.*;

import it.okkam.rdf2okkam.controller.ApplicationController;
import it.okkam.rdf2okkam.ens.EntityBuilderTest;
import it.okkam.rdf2okkam.model.ModelLoader;
import it.okkam.rdf2okkam.model.ModelUtil;
import it.okkam.rdf2okkam.model.Tax2EnsMapper;
import it.okkam.rdf2okkam.parser.Globalizer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

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

public class ApplicationControllerTest extends TestCase {
	
	
	String modelFileName = "resources/models/anagrafe.ttl" ;
	final String RDF_SYNTAX = "TURTLE" ;
	
	ApplicationController controller = null ;
	Globalizer global = null ;
	Tax2EnsMapper mapper = null ;
	
	private static Log log = LogFactory.getLog(ApplicationControllerTest.class);
	
	public ApplicationControllerTest(String testName) {
		super(testName) ;
	}

	@Before
	public void setUp() throws Exception {		
		
		controller = new ApplicationController() ;
		
		global = new Globalizer(ModelLoader.getInstance().getInputModel(), 
									ModelLoader.getInstance().getOutputModel()) ;
		
		mapper = new Tax2EnsMapper() ;
		
		//mapper.startInference( ModelLoader.getInstance().getInputModel() ) ;
		
	}

//	@Test
//	public void testCreateEntity() {
//		log.info("----------testCreateEntity--------------") ;						
//		Set<RDFNode> distSubjs = global.getDistinctSubjects() ;
//		RDFNode distSubj = distSubjs.iterator().next() ;
//								
//		String okkamid = controller.createEntity(distSubj) ;
//		
//		log.info("New entity's okkam id: " + okkamid) ;		
//	
//			
//		//ModelUtil mockup = new ModelUtil() ;		
//		//Model output = mockup.modifyRDF(bnodeOkkamId) ;
//		
//	}
	
	@Test
	public void testCreateEntities() {
		log.info("----------testCreateEntities--------------") ;		
		Set<RDFNode> distSubjs = global.getDistinctSubjects() ;
		int numOfSubjs = distSubjs.size();
		Map<String, String> bnodeOkkamId = new HashMap<String, String>() ;
		Iterator<RDFNode> idistSubj = distSubjs.iterator() ;
		int countSubjs = 0;
		while(idistSubj.hasNext()) {
			RDFNode distSubj = idistSubj.next() ;
						
			String okkamid = controller.createEntity(distSubj) ;
			//bnodeOkkamId.put(distSubj.toString(), okkamid) ;
			countSubjs++;
			log.info("New entity's okkam id: " + okkamid) ;		
		}
		log.info("Number of subjects: " + numOfSubjs + ", entities created: " + countSubjs) ;
		/*
		log.info("--------------Entering printUri() method---------------") ;
		printUris(bnodeOkkamId) ;
		log.info("--------------End printUri() method---------------") ;
		
		final long startTime = System.currentTimeMillis();
		final long endTime;
		
		ModelUtil util = new ModelUtil() ;
		log.info("--------------Entering modifyRDF() method---------------") ;
		util.modifyRDF(bnodeOkkamId);
		log.info("--------------End of modifyRDF() method---------------") ;
		
		endTime = System.currentTimeMillis();
		final long duration = endTime - startTime;
		System.out.println("Model Creation took " + duration + " milliseconds");
		*/
	}
	
	private void printUris(Map<String, String> bnodeOkkamId) {
		log.info("----printing bnodes - uri pairs on file ----" ) ;
		String uriFileName = "resources/test/bnode_uri_03.txt" ;
		PrintWriter out = null ;
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter( uriFileName )));
			if(out != null ) {
				Set<String> keys = bnodeOkkamId.keySet() ;
				Iterator<String> ikey = keys.iterator() ;
				while(ikey.hasNext()) {
					String key = ikey.next() ;
					String uri = bnodeOkkamId.get(key) ;
					out.write(key + ", " + uri + "\n" ) ;
				}
			}
					
			out.close() ;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
