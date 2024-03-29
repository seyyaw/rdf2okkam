package it.okkam.rdf2okkam.ens.client;
/*
 * prova
 */
import static org.junit.Assert.*;

import it.okkam.rdf2okkam.ens.client.EnsQuery;
import it.okkam.rdf2okkam.ens.client.OkkamClient;
import it.okkam.rdf2okkam.ens.client.QueryResponse;
import it.okkam.rdf2okkam.ens.client.ServiceClient;
import it.okkam.rdf2okkam.model.ModelLoader;
import it.okkam.rdf2okkam.parser.RdfUtil;
import it.okkam.rdf2okkam.parser.VocabConstants;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.okkam.client.data.AttributesType;
import org.okkam.core.data.api.SemanticType;


import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;



public class CopyOfServiceClientTest extends TestCase {
	private final String rdfNS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	private static final String filename = "resources/anagrafe1.ttl";
	private static final String confpath = "conf";
	ModelLoader loader = null ;
	RdfUtil parser = null;
	OkkamClient okkam = null;
	ServiceClient client = null;
	AttributesType attributesType = null;
	EnsQuery query = null ;
	
	public CopyOfServiceClientTest(String testName) {
		super(testName) ;
	}

	private static Log log = LogFactory.getLog(CopyOfServiceClientTest.class);
	
	
	
	@Before
	public void setUp() throws Exception {
		
		loader = ModelLoader.getInstance() ;		
		parser = new RdfUtil(loader.getInputModel());
		okkam = new OkkamClient(confpath) ;
		client = new ServiceClient(okkam) ;	
		query = new EnsQuery() ; 
	    		
	}


	@Test
	public void testCreateNewEntity(){
		log.info("----------------Test createNewEntities()-----------------") ;
		Set<RDFNode> subjects = parser.getDistinctSubjects() ;
		Iterator<RDFNode> i = subjects.iterator();
		boolean ignoreDuplicates = false ;
		while(i.hasNext()){
			RDFNode subject = i.next();
			System.out.println("subject " + subject.toString()) ;
			Property typep = loader.getInputModel().getProperty(rdfNS + "type") ;
			String semanticType = "location" ;
			System.out.println("subject: " + subject.toString() + ", sem. type: " + semanticType) ;
			attributesType = parser.listSubjectProperties(subject);
			String okkamid = client.createNewEntity(semanticType, attributesType, ignoreDuplicates);
			log.info("Identifier for " + subject.asResource().getURI() + ": " + okkamid);
			
		}
	}
	
	@Test
	public void testFindEntity() {
		log.info("----------------Test testFindEntity()-----------------") ;
		//AttributesType attrsType = parser.listSubjectProperties( getSubjectFromModel() ) ;
		List<String> queries = new ArrayList<String>() ;
		queries.add("addis") ;
		String semType = "http://models.okkam.org/ENS-core-vocabulary.owl#location" ;
		String queryStr = query.getQuery(queries, semType) ;
		int count = 0 ;
		List<QueryResponse> entities = client.findEntity(queryStr) ;
		Iterator<QueryResponse> iresponse = entities.iterator() ;
		while(iresponse.hasNext()) {
			count++ ;
			QueryResponse response = iresponse.next() ;
			log.info(count + ") Okkam id = " + response.getId() + ", name = " + response.getName()) ;
		}
		
		
		
	}
	
	@Test
	public void testFindEntityByOkkamId() {
		log.info("----------------Test testFindEntityByOkkamId()-----------------") ;
		String okkamid = "http://www.okkam.org/ens/id32609f1e-5dc9-44e3-8387-06e0806e2e01" ;
		Map<String, String> entities = client.findEntityByOkkamId(okkamid) ;
		Set<String> keys = entities.keySet() ;
		Iterator<String> ikey = keys.iterator() ;
		while(ikey.hasNext()) {
			String key = ikey.next() ;
			String value = entities.get(key) ;
			System.out.println("key = " + key + ", value = " + value) ;
		}
		
	}
	
	@Test
	public void testDeleteEntity() {
		log.info("----------------Test testDeleteEntity()-----------------") ;
		//List<String> okkamids = getUrisFromFile() ;
		List<String> queries = new ArrayList<String>() ;
		queries.add("folgaria") ;
		
		String queryStr = query.getQuery(queries, SemanticType.LOCATION) ;
		int count = 0 ;
		List<QueryResponse> entities = client.findEntity(queryStr) ;
		Iterator<QueryResponse> iresponse = entities.iterator() ;
		while(iresponse.hasNext()) {
			count++ ;
			QueryResponse response = iresponse.next() ;
			client.deleteEntity(response.getId()) ;
			log.info(count + ") Deleted entity = " + response.getId() + ", name = " + response.getName()) ;
		}
		
		
		
		
	}
	
	private RDFNode getSubjectFromModel() {
		RDFNode subject = null ;		
		
		Property subj1Predicate = loader.getInputModel().getProperty(VocabConstants.ensNS + "location_name") ;		
		Selector selector1 = new SimpleSelector(null, subj1Predicate, "FOLGARIA" ) ;
		StmtIterator isubj1 = loader.getInputModel().listStatements(selector1) ;
		Statement stmt = isubj1.next() ;
		subject = stmt.getSubject() ;
		return subject ;
	}
	
	private List<String> getUrisFromFile() {
		List<String> result = new ArrayList<String>() ;
		String uriFileName = "resources/test/bnode_uri_03.txt" ;
		BufferedReader in = null ;
		try {			
			in = new BufferedReader( new FileReader( uriFileName ) ) ;
			String line ;
			while( (line = in.readLine()) != null ) {
				String [] ids = line.split(",") ;
				String bnode = ids[0] ;
				String uri = ids[1] ;
				result.add(uri.trim()) ;
			}
			
			in.close() ;
		}
		catch(FileNotFoundException fnfe) {
			log.info( fnfe.getMessage() ) ;
		}
		catch(IOException ioe) {
			log.info( ioe.getMessage() ) ;
		}
		
		return result ;
		
		
	}

}
