package it.okkam.rdf2okkam.ens;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import it.okkam.rdf2okkam.model.ModelLoader;
import it.okkam.rdf2okkam.parser.GetSubjects;
import it.okkam.rdf2okkam.parser.VocabConstants;

import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class EntityTypeSelectorTest {
	
	//String modelFileName = "resources/models/anagrafe.ttl" ;
	final String RDF_SYNTAX = "TURTLE" ;
	Model _model = null ;
	EntityTypeSelector selector = null;
	EnsEntityFactory factory = null;

	@Before
	public void setUp() throws Exception {
		
		_model = ModelLoader.getInstance().getInputModel();
		selector = new EntityTypeSelector(_model);
	}

	@Test
	public void testSelectEntityFactory() {
		RDFNode node = getNode();
		factory = selector.selectEntityFactory(node);
		EnsEntity entity = factory.createEnsEntity(_model, node);
		System.out.println("Type of entity: " + entity.getSemanticType());
		
		
	}
	
	private RDFNode getNode(){
		
		RDFNode node = null;
		
		
		Set<RDFNode> subjects = new HashSet<RDFNode>() ;
		StmtIterator stmtIter = _model.listStatements();		
		while(stmtIter.hasNext()) {
			RDFNode subject = stmtIter.next().getSubject() ;
			if( ! VocabConstants.rdfNS.equals(subject.asResource().getNameSpace()) )  
					subjects.add(subject) ;
		}		
			
		
		node = subjects.iterator().next();
		
		return node;
		
	}

	

}
