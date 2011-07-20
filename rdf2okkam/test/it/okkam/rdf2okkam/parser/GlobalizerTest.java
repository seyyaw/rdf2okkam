package it.okkam.rdf2okkam.parser;

import static org.junit.Assert.*;

import it.okkam.rdf2okkam.model.ModelLoader;
import it.okkam.rdf2okkam.model.Tax2EnsMapper;
import it.okkam.rdf2okkam.parser.Globalizer;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;


public class GlobalizerTest extends TestCase {
	
	//String inputModelFileName = "resources/test/anagrafe1.ttl" ;
	//String outputModelFileName = "resources/test/datasetout.ttl" ;
	final String RDF_SYNTAX = "TURTLE" ;
	String baseUri = null ;
	Model _inModel = null ;
	Model _outModel = null ;
	Globalizer global = null ;
	Tax2EnsMapper mapper = null ;

	private static Log log = LogFactory.getLog(GlobalizerTest.class);
	
	public GlobalizerTest(String testName) {
		super(testName) ;
	}
	
	@Before
	public void setUp() throws Exception {
		
		global = new Globalizer(ModelLoader.getInstance().getInputModel(), 
				ModelLoader.getInstance().getOutputModel()) ;
		
		mapper = new Tax2EnsMapper() ; //makes mapping inferences		
		mapper.startInference( ModelLoader.getInstance().getInputModel() ) ;	
		
		
	}

	@Test
	public void testIsCandidateEntity() {
		log.debug("----------testIsCandidateEntity--------------") ;
		Set<RDFNode> subjects = global.getDistinctSubjects() ;
		Iterator<RDFNode> isubject = subjects.iterator() ;
		while(isubject.hasNext()) {
			Resource subject = isubject.next().asResource() ;			
			if( global.isCandidateEntity(subject) ) 
				log.debug(subject + " is a candidate entity") ;
			else
				log.debug(subject + " is not a candidate entity") ;			
		}
	}
	
	@Test
	public void testGetDistinctSubjects() {
		log.info("----------testGetDistinctSubjects--------------") ;
		Set<RDFNode> subjects = global.getDistinctSubjects() ;
		Iterator<RDFNode> isubject = subjects.iterator() ;
		while(isubject.hasNext()) {
			RDFNode subject = isubject.next() ;
			log.debug(subject) ;
		}
	}

	@Test
	public void testGlobalizeId() throws FileNotFoundException {
		log.info("----------testGlobalizeId--------------") ;
		Set<RDFNode> distSubjs = global.getDistinctSubjects() ;
		Iterator<RDFNode> idistSubj = distSubjs.iterator() ;
		int count = 1 ;
		while(idistSubj.hasNext()) {			
			RDFNode subj = idistSubj.next() ;
			if(global.isCandidateEntity(subj)) { 
				log.debug(subj + " is a candidate entity") ;
				_outModel.add( global.globalizeId(subj, "http://okkam.org/ens/" + count) ) ;
				count++ ;
			}
			else
				log.debug(subj + " is not a candidate entity") ;
			}
			
		_outModel.write(System.out, RDF_SYNTAX) ;
		
		
	}

}
