package org.okkam.dataset.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/*
 * Statements of the input model that have a blank node as subject identifier
 * are updated by a URI in place of the blank node.The updated statements are 
 * saved in the output model. The output model cannot have blank nodes, only 
 * literals or URIs are admitted as values for predicates. 
 */

public class Globalizer {
	private final String rdfNS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	Model _inModel = null ;
	Model _outModel = null ;
	
	public Globalizer(Model in, Model out){
		_inModel = in ;
		_outModel = out ;
	}
	
	/*
	 * A candidate entity is a subject node whose properties values are all 
	 * literals or URIs, not blank nodes. 
	 */
	public boolean isCandidateEntity(RDFNode bnode) {
		boolean result = true ;
		Selector selector = new SimpleSelector(bnode.asResource(), null, (RDFNode) null) ;
		StmtIterator istmt = _inModel.listStatements(selector) ;
		while(istmt.hasNext()) {
			Statement stmt = istmt.next() ;
			RDFNode object = stmt.getObject() ;
			if( object.isAnon() )
				result = false ;
		}
		
		return result ;
	}
	
	/* 
	 * Replace the subject's blank node identifier with a URI and save the statements
	 * in the output model.
	 */
	public List<Statement> globalizeId(RDFNode bnode, String uri) {
		Selector selector = new SimpleSelector(bnode.asResource(), null, (RDFNode) null) ;
		List<Statement> updatedStmtList = new ArrayList<Statement>() ;
		Resource subject = ResourceFactory.createResource(uri) ;
		StmtIterator istmt = _inModel.listStatements(selector) ;
		while(istmt.hasNext()) {
			Statement stmt = istmt.next() ;
			Property property = stmt.getPredicate() ;
			RDFNode object = stmt.getObject() ;			
			Statement updatedStatement = null ;
			if(! object.isAnon()){
				updatedStatement = ResourceFactory.createStatement(subject, property, object) ;
				updatedStmtList.add(updatedStatement) ;
			}
			else {
				// read the uriref associated to the bnode from a file
				String uriref = "http://okkam.org/ens/123" ;
				Resource resObj = ResourceFactory.createResource(uriref) ;
				updatedStatement = ResourceFactory.createStatement(subject, property, resObj) ;
			}
			
		}		
		// update the list of bnodes globalized (with associated uri)
		return updatedStmtList ;		
	}
	
	/*
	 * Returns the distinct subjects of all the statements in the rdf dataset (model).
	 * The subject resources are put in a set that cannot accept duplicates.
	 */
	public Set<RDFNode> getDistinctSubjects() {
		Set<RDFNode> subjects = new HashSet<RDFNode>() ;
		StmtIterator stmtIter = _inModel.listStatements();		
		while(stmtIter.hasNext()) {
			RDFNode subject = stmtIter.next().getSubject() ;
			if( ! rdfNS.equals(subject.asResource().getNameSpace()) )  
					subjects.add(subject) ;
		}		
		return subjects ;
	}
	
	/*
	 * Check whether the subject has already been recognized as an entity and stored
	 * in the output model 
	 */
	public boolean isSameEntity(RDFNode bnode) {
		boolean result = false ;		
		Selector selectorIn = new SimpleSelector(bnode.asResource(), null, (RDFNode) null) ;
		StmtIterator istmtIn = _inModel.listStatements(selectorIn) ;		
		while(istmtIn.hasNext()) {
			Statement stmtIn = istmtIn.next() ;
			Property propIn = stmtIn.getPredicate() ;
			RDFNode objIn = stmtIn.getObject() ;
			Selector selectorOut = new SimpleSelector(null, propIn, objIn) ;
			
			StmtIterator istmtOut = _outModel.listStatements(selectorOut) ;
			Resource subject = null ;
			while( istmtOut.hasNext() ) {
				Statement stmtOut = istmtIn.next() ;
				subject = stmtOut.getSubject() ;
				Property propOut = stmtIn.getPredicate() ;
				RDFNode objOut = stmtIn.getObject() ;
			}
		}
		
		return result ;
	}

}
