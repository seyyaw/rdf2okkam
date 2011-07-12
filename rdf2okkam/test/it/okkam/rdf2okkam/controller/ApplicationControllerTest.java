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
	
	
	String modelFileName = "resources/test/dataset_out.ttl" ;
	final String RDF_SYNTAX = "TURTLE" ;
	
	ApplicationController controller = null ;
	Globalizer global = null ;
	Tax2EnsMapper mapper = null ;
	
	private static Log log = LogFactory.getLog(ApplicationControllerTest.class);

	@Before
	public void setUp() throws Exception {		
		
		controller = new ApplicationController() ;
		
		global = new Globalizer(ModelLoader.getInstance().getInputModel(), 
									ModelLoader.getInstance().getOutputModel()) ;
		mapper = new Tax2EnsMapper() ;
		
		mapper.startInference( ModelLoader.getInstance().getInputModel() ) ;
		
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
		Map<String, String> bnodeOkkamId = new HashMap<String, String>() ;
		Iterator<RDFNode> idistSubj = distSubjs.iterator() ;
		while(idistSubj.hasNext()) {
			RDFNode distSubj = idistSubj.next() ;
						
			String okkamid = controller.createEntity(distSubj) ;
			bnodeOkkamId.put(distSubj.toString(), okkamid) ;
			
			log.info("New entity's okkam id: " + okkamid) ;		
		}
		
		printUris(bnodeOkkamId) ;
		
		ModelUtil util = new ModelUtil() ;
		util.modifyRDF(bnodeOkkamId);		
		
	}
	
	private void printUris(Map<String, String> bnodeOkkamId) {
		log.info("----printing bnodes - uri pairs on file ----" ) ;
		String uriFileName = "resources/test/bnode_uri_04.txt" ;
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
