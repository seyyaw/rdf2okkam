package org.okkam.mapping;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.util.FileManager;

/*
 * This program loads two models containing rdf statements in two different ontologies
 * merges the models and then loads a mapping rule to map the location name where a 
 * person live in the first ontology to the residence in the second ontology. 
 * After the rule has fired the inferred statements are
 * added to the model. The updated model is saved in a file.
 * 
 * The program needs the following two files as input
 * people.owl, containing the statements expressed using the terms of the two ontologies
 * people.rules, containing the mapping rule
 * 
 * @author Luigi Selmi 2011 June 18th Trento, Italy
 */
public class Tax2EnsMapper {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		String onto1 = "resources/anagrafe.ttl";		
		String baseUri = null ;
		String rdfNS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#" ;
		String taxNS = "http://localhost/TaxOntology.owl#";
		String ensNS = "http://models.okkam.org/ENS-core-vocabulary.owl#" ;
		Model model1 = ModelFactory.createDefaultModel() ;
		Model model2 = ModelFactory.createDefaultModel() ;
		
		
		InputStream in1 = FileManager.get().open( onto1 );
		if (in1 == null) {
		    throw new IllegalArgumentException(
		                                 "File: " + onto1 + " not found");
		}
		

		//Load the onto1 model 
		model1.read(in1, baseUri,"TURTLE");				
		
		//Load the mapping rules
		List<Rule> rules = Rule.rulesFromURL("file:resources/mapping.rules");		
		
		//Start the inferences using a rule reasoner
		Reasoner reasoner = new GenericRuleReasoner(rules);	    
		InfModel inf = ModelFactory.createInfModel(reasoner, model1);
	    
		StmtIterator i = inf.listStatements() ;
		ArrayList<Statement> infStmts = new ArrayList<Statement>() ;
	    while(i.hasNext()){
	    	Statement stmt = i.nextStatement(); 
	    	//Save the inferred statement 
	    	infStmts.add(stmt) ;
	        
	    }
	    
	    //Save the mapped model 
	    inf.write(new PrintWriter("resources/dataset_out.ttl"),"TURTLE");

	    
	    if(infStmts.size() > 0) {
	    	Iterator<Statement> iter = infStmts.iterator() ; 
	    	while(iter.hasNext()) {
	    		Statement infStatement = iter.next() ;
	    		System.out.println("Inf. statement: " + infStatement.toString());
	    	}
	    	
	    }

	}

}
