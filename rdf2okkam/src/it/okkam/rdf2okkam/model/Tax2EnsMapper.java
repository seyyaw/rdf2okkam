package it.okkam.rdf2okkam.model;

import it.okkam.rdf2okkam.controller.ApplicationController;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

	private static Log log = LogFactory.getLog(Tax2EnsMapper.class);
	
	/*
	 * Make inferences to map the domain ontology terms to the ENS ones. The inferred
	 * statement are added to the input model.
	 */
	public void startInference(Model model) {
		
		//Start the inferences using a rule reasoner
		log.info( "Started the reasoning process to map tax ontology terms to the ENS ontology terms." ) ;
		Reasoner reasoner = new GenericRuleReasoner(ModelLoader.getInstance().getRules());	    
		InfModel inf = ModelFactory.createInfModel(reasoner, model);
		ModelLoader.getInstance().getInputModel().add(inf) ;
		log.info( "Added inferred statements to the input model based on the rules." ) ;
		
	}
	

}
