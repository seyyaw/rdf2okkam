package it.okkam.rdf2okkam.parser;

import it.okkam.rdf2okkam.ens.client.ServiceClient;
import it.okkam.rdf2okkam.exception.SameNodeException;
import it.okkam.rdf2okkam.model.ModelLoader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.okkam.client.data.AttributeMetadataAccessControlType;
import org.okkam.client.data.AttributeMetadataGeneralType;
import org.okkam.client.data.AttributeMetadataProvenanceType;
import org.okkam.client.data.AttributeMetadataType;
import org.okkam.client.data.AttributeType;
import org.okkam.client.data.AttributesType;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class RdfUtil {
	
	private final String ensOntology = "resources/ENS-core-vocabulary.owl";
	
	private final String ensNS = "http://models.okkam.org/ENS-core-vocabulary.owl#";
	
	private final String taxNS = "http://localhost/TaxOntology.owl#" ;
	
	private final String TAX_PREFIX = "tax" ;
	
	private final String ENS_PREFIX = "ens";
	
	private final String rdfNS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	
	private final String RDF_PREFIX = "rdf" ;
	
	private String baseUri = null;
	
	private String rdfDatasetFileName = null;
	
	private Model outputModel = null;
	
	private OntModel ensModel = null;
	
	private Model _model = null ;
	
	private static Log log = LogFactory.getLog(RdfUtil.class);
		
	
	public RdfUtil(Model inmodel, Model outmodel){	
		
		_model = inmodel ;
		
		outputModel = outmodel ;
		
		
		
		loadEnsOntology() ;

	}

	
	/*
	 * Returns subject's list of properties
	 */
	public AttributesType listProperties(String subjectUri){
		AttributesType attributes =  new AttributesType();
		Resource subject = _model.getResource(subjectUri);
		StmtIterator i = subject.listProperties();	
		String prefix = "";
		while(i.hasNext()){		
			Statement stmt = i.next();
			AttributeType attribute = new AttributeType();
			Property predicate = stmt.getPredicate();
			//System.out.println("Predicate namespace: " + predicate.getNameSpace());
			if( ensNS.equals( predicate.getNameSpace() )){
				prefix = ENS_PREFIX;
			}
			QName name = new QName(predicate.getURI(),predicate.getLocalName(), prefix);
			attribute.setName(name);			
			String value = stmt.getObject().toString();
			attribute.setValue(value);
			
			// Set access control metadata to send or not attributes values to the public node
			// Set to "private" to not send the attributes values.
			// attribute.getMetadata().getAccessControl().setDisplayable("private");			
			
			attributes.getAttributes().add(attribute);	
			
		}
		
		return attributes;		
	}
	
	/*
	 * Returns subject's list of properties
	 */
	public AttributesType listSubjectProperties(RDFNode subjectNode){
		AttributesType attributes =  new AttributesType();
		Resource subject = subjectNode.asResource() ;
		
		//System.out.println("subject as resource: " + subject) ;
		Selector selector = new SimpleSelector(subject, null, (RDFNode)null) ;
		StmtIterator i = _model.listStatements(selector) ;	
		
		while(i.hasNext()){	
			String prefix = "";
			Statement stmt = i.next();			
			AttributeType attribute = new AttributeType();
			Property predicate = stmt.getPredicate();
			//System.out.println("predicate: " + predicate.getURI());
			if( ensNS.equals( predicate.getNameSpace() )){
				prefix = ENS_PREFIX ;
			}
			if( taxNS.equals( predicate.getNameSpace() )){
				prefix = TAX_PREFIX ;
			}
			if( rdfNS.equals( predicate.getNameSpace() )){
				prefix = RDF_PREFIX ;
			}
			QName name = new QName(predicate.getURI(), predicate.getLocalName() , prefix);
			attribute.setName(name);			
			RDFNode object = stmt.getObject() ;
			String value = "" ;
			if(object.isLiteral())
				value = object.asLiteral().getString() ;
			if(object.isResource())
				value = object.asResource().getLocalName() ;
			//System.out.println("value: " + value) ;
			attribute.setValue(value);
			
			// Set access control metadata to send or not attributes values to the public node
			// Set to "private" to not send the attributes values.
			// attribute.getMetadata().getAccessControl().setDisplayable("private");			
			
			attributes.getAttributes().add(attribute);	
			
		}
		
		return attributes;		
	}
	
	
	/*
	 * Returns true if all statements about the subject are not blank nodes 
	 */
	public boolean allPropertiesWithoutBNodes(RDFNode subject){
		boolean noBlankNodes = true ;		
		StmtIterator i = subject.asResource().listProperties();			
		while(i.hasNext()){		
			Statement stmt = i.next();			
			Property predicate = stmt.getPredicate();									
			Node object = stmt.getObject().asNode();
			if(object.isBlank()) {
				noBlankNodes = false ;
			}
		}
		
		return noBlankNodes;		
	}
	
	
	public List<QName> listEnsDatatypeProperties(){
		ArrayList<QName> properties = new ArrayList<QName>();				
		ExtendedIterator<DatatypeProperty> idataprop = ensModel.listDatatypeProperties();	
		while(idataprop.hasNext()){			
			DatatypeProperty property = idataprop.next();
			QName q = new QName(property.getURI(),property.getLocalName(), ENS_PREFIX);			
			properties.add(q);						
		}
		
		return properties;		
	}
	
	public QName getQName(String Uri){
		log.debug("Inside getQNmae() method") ;
		return new QName(Uri, "local_part", "ens");
	}
	
	/*
	 * Returns the subjects of type passed as argument
	 */
	public List<Resource> listSubjectsByType(String type){
		ArrayList<Resource> subjects = new ArrayList<Resource>();
		Property rdfType = _model.getProperty(rdfNS + "type");				
		Resource typeResource = ensModel.getResource(ensNS + type); 
		StmtIterator i = _model.listStatements(new SimpleSelector(null, rdfType, typeResource));
		while(i.hasNext()){						
			subjects.add(i.next().getSubject());			
		}
		return subjects;
	}
	
	public List<RDFNode> listSubjects() {
		ArrayList<RDFNode> subjects = new ArrayList<RDFNode>() ;
		List<QName> ensTypes = listEnsClasses() ;
		Property rdfType = _model.getProperty(rdfNS + "type");
		Iterator<QName> i = ensTypes.iterator() ;
		while(i.hasNext()) {
			QName q = i.next() ;
			Resource typeResource = ensModel.getResource(ensNS + q.getLocalPart());
			Selector selector = new SimpleSelector(null, rdfType, typeResource) ;
			StmtIterator stmtIterator = _model.listStatements(selector) ;
			while(stmtIterator.hasNext()) {
				RDFNode subject = stmtIterator.next().getSubject() ;							
				subjects.add(subject) ;
				
			}
		}
		
		
		return subjects ;
	}
	
	public QName getType(String uri){
		Resource resource = _model.getResource( uri );
		Property rdfType = _model.getProperty(rdfNS + "type");
		Statement stmt = resource.getProperty(rdfType);
		Resource type = stmt.getObject().asResource();
		QName qtype = new QName(type.getURI(), type.getLocalName(), "rdf");
		return qtype;
		
	}
	
	public QName getType(Resource resource){		
		Property rdfType = _model.getProperty(rdfNS + "type");
		Statement stmt = resource.getProperty(rdfType);
		Resource type = stmt.getObject().asResource();
		QName qtype = new QName(type.getURI(), type.getLocalName(), "rdf");
		return qtype;
		
	}
	
	
	public List<QName> listEnsClasses(){
		
		ArrayList<QName> ensClassesList = new ArrayList<QName>();
		OntClass thing = ensModel.getOntClass(ensNS + "Thing"); 
		ExtendedIterator<OntClass> iclass = ensModel.listNamedClasses();
		
		while(iclass.hasNext()){
			OntClass ensClass = iclass.next();
			if( ! "Thing".equals( ensClass.getLocalName() ) ){
				QName q = new QName(ensClass.getURI(), ensClass.getLocalName(), ENS_PREFIX);
				ensClassesList.add(q);
			}
		}
		
		return ensClassesList;
		
	}
	
	/*
	 * Returns all the subjects that don't have blank nodes as properties values.
	 */
	public Set<RDFNode> getSubjectsWithoutBNodes() {
		log.info("getSubjectsWithoutBlankNodes()") ;
		Set<RDFNode> subjects = new HashSet<RDFNode>() ;
		if(getDistinctSubjects().size() > 0) {
			Iterator<RDFNode> subjectIter = getDistinctSubjects().iterator() ;			
			while(subjectIter.hasNext()) {
				RDFNode subject = subjectIter.next() ;
				Selector selector = new SimpleSelector(subject.asResource(), null, (RDFNode) null) ;
				StmtIterator objIter = _model.listStatements(selector) ;
				boolean isBNode = false ;
				while(objIter.hasNext()) {
					RDFNode object = objIter.nextStatement().getObject() ;
					if(object.isAnon()) {
						isBNode = true ;
					}
				}
				if(!isBNode) 
					subjects.add(subject) ;			
			}
		}
		else {
			log.error("No distinct subjects") ;
		}
		
		return subjects ;
	}
	
	/*
	 * Returns the distinct subjects of all the statements in the rdf dataset (model).
	 * The subject resources are put in a set that cannot accept duplicates. Distinct means
	 * that there are not more than one node with the same identifier (URI or BNode). It does
	 * not mean that they refers to different entities.
	 */
	public Set<RDFNode> getDistinctSubjects() {
		Set<RDFNode> subjects = new HashSet<RDFNode>() ;
		StmtIterator stmtIter = _model.listStatements();		
		while(stmtIter.hasNext()) {
			RDFNode subject = stmtIter.next().getSubject() ;
			if( ! rdfNS.equals(subject.asResource().getNameSpace()) )  
					subjects.add(subject) ;
		}		
		
		return subjects ;
	}
	
	/*
	 * Select a subset of distinct subject nodes that are not objects in any statements
	 */
	public Set<RDFNode> getStrictSubjects() {
		log.info("getStrictSubject()") ;
		Set<RDFNode> strictSubjects = new HashSet<RDFNode>() ;
		
		if(getDistinctSubjects().size() > 0) {
			Iterator<RDFNode> subjectIter = getDistinctSubjects().iterator() ;			
			while(subjectIter.hasNext()) {
				RDFNode subject = subjectIter.next() ;
				Selector selector = new SimpleSelector(null, null, subject.asResource()) ;
				StmtIterator objIter = _model.listStatements(selector) ;
				if( ! objIter.hasNext() )
							strictSubjects.add(subject) ;
			}
		}
		else {
			log.error("No distinct subjects") ;
		}
		
		return strictSubjects ;
	}
	
	/*
	 * Select a subset of distinct subject nodes that are objects in some statements. This
	 * method is the complement of getStrictSubjects().
	 */
	public Set<RDFNode> getNotStrictSubjects() {
		log.info("getNotStrictSubject()") ;
		
		Set<RDFNode> notStrictSubjects = new HashSet<RDFNode>() ;
		
		if(getDistinctSubjects().size() > 0) {
			Iterator<RDFNode> subjectIter = getDistinctSubjects().iterator() ;			
			while(subjectIter.hasNext()) {
				RDFNode subject = subjectIter.next() ;
				Selector selector = new SimpleSelector(null, null, subject.asResource()) ;
				StmtIterator objIter = _model.listStatements(selector) ;
				if( objIter.hasNext() )
							notStrictSubjects.add(subject) ;
			}
		}
		else {
			log.error("No distinct subjects") ;
		}
		
		return notStrictSubjects ;	
		
		
	}
	
	public Set<RDFNode> getFullDistinctSubjects() {
		Set<RDFNode> result = new HashSet<RDFNode>() ;
		result = getDistinctSubjects() ;
		
		return result ;
	}
	
	/*
	 * Get all the subjects that have the same properties values of
	 * the subject argument.
	 */
	public Set<RDFNode> getDuplicateSubjects(RDFNode subject) {
		Set<RDFNode> duplicates = new HashSet<RDFNode>() ;
		
		Iterator<RDFNode> i  = getDistinctSubjects().iterator() ;
		while(i.hasNext()) {
			RDFNode distSubject = i.next() ;
			
			if( ! distSubject.equals(subject) ) {
					if( compareSubjects(subject, distSubject) )
						duplicates.add(distSubject) ;
			}
		}
						
		return duplicates ;
	}
	
	/*
	 * Changes the blank node identifier (local) with a URI (global). It removes
	 * the blank node as subject or object in statements with the URI. The updated
	 * statements are written into the output model while the input one is preserved.
	 * No blank nodes are used in the output model for they would be changed each time
	 * the model is updated. 
	 */
	public void globalizeIdentifier(RDFNode bnode, String uriReference) {
		Model resultModel = null ;
		
		Selector selector = null ;
		StmtIterator istmt = null ;
				
		List<Statement> updatedStmt = new ArrayList<Statement>() ;
		
		Resource resource = ResourceFactory.createResource(uriReference) ;
		// remove the blank node as subject in statements
		selector = new SimpleSelector(bnode.asResource(), null, (RDFNode) null) ;
		istmt = _model.listStatements(selector) ;
		while(istmt.hasNext()) {
			Statement bnodeStmt = istmt.next() ;					
			Statement stmt = ResourceFactory.createStatement(resource, bnodeStmt.getPredicate(), bnodeStmt.getObject() ) ;
			updatedStmt.add(stmt) ;			
		}
		
		// remove the blank node as object in statements if the subject is not 
		// itself a blank node
		selector = new SimpleSelector(null , null, bnode) ;
		istmt = _model.listStatements(selector) ;
		while(istmt.hasNext()) {
			Statement bnodeStmt = istmt.next() ;	
			if( ! bnodeStmt.getSubject().isAnon() ) {
				Statement stmt = ResourceFactory.createStatement(bnode.asResource(), bnodeStmt.getPredicate(), resource ) ;
				updatedStmt.add(stmt) ;
			}
		}
		
		
		
		outputModel.add( updatedStmt ) ;
		
	}
	
	
	/*
	 * Remove duplicates of the node passed as argument from the model. 
	 */
//	public Model removeDuplicates(RDFNode subjectNode) {
//		
//		List<Statement> removedStmt = new ArrayList<Statement>() ;
//		Set<RDFNode> duplicates = getDuplicateSubjects(subjectNode) ;
//		
//		Iterator<RDFNode> iduplicates = duplicates.iterator() ;
//		
//		log.info("Number of " + subjectNode + " duplicates: " + duplicates.size()) ;
//		
//		while(iduplicates.hasNext()) {
//			RDFNode subject = iduplicates.next() ;		
//			Resource subjResource = subject.asResource() ;
//			Selector selector = new SimpleSelector(subjResource, null, (RDFNode) null) ;
//			StmtIterator istmt = _model.listStatements(selector) ;
//			while(istmt.hasNext()) {
//				Statement stmt = istmt.next() ;
//				removedStmt.add(stmt) ;
//			}						
//						 			
//		}	
//		
//		
//		return _model.remove(removedStmt) ;
//	}
	
	/*
	 * Compare two RDF subject nodes by their properties. If the subjects are of 
	 * same type compare their properties. Two entities are the same if they have 
	 * the same value for all their properties. Objects that are blank nodes are not 
	 * taken into account in the comparison.
	 */
	public boolean compareSubjects(RDFNode subj1, RDFNode subj2) {
		boolean isSamePropertyValue = false ;
		boolean isSameEntity = true ;
		Property typep = _model.getProperty(rdfNS + "type") ;
		
		//Check the subjects' type
		RDFNode type1 = subj1.asResource().getProperty(typep).getObject() ;
		String type1Uri = type1.asResource().getURI() ;
		RDFNode type2 = subj2.asResource().getProperty(typep).getObject() ;
		String type2Uri = type2.asResource().getURI() ;
		// If the nodes are the same return true
		if( subj1.toString().equals(subj2.toString()) )
			return true ;
			//throw new SameNodeException(subj1.toString() + " is the same node as " + subj2.toString()) ;
		// If the nodes are of different types return false
		if( ! type1Uri.equals(type2Uri) ) 
			return false ;
		
		// The nodes are different but of the same type so let's see their
		// properties. Only properties that have URI or literal values are
		// taken into account
		StmtIterator isubj1props = subj1.asResource().listProperties() ;		
		while(isubj1props.hasNext()) {
			Statement subj1stmt = isubj1props.next() ;
			Property property1 = subj1stmt.getPredicate() ;	
			RDFNode object1 = subj1stmt.getObject() ;
			Selector selector = new SimpleSelector(subj2.asResource(), property1, (RDFNode) null) ;
			StmtIterator isubj2props =  _model.listStatements(selector);
			while(isubj2props.hasNext()) {								
				Statement subj2stmt = isubj2props.next() ;
				Property property2 = subj2stmt.getPredicate() ;
				if( (property2.getURI()).equals(property1.getURI()) ) {					
					RDFNode object2 = subj2stmt.getObject() ;
					if( (!object1.isAnon()) && (!object2.isAnon()) ) {
						if( ( object1.toString() ).equals( object2.toString() ) ) 
							isSamePropertyValue = true ;						
						else
							isSamePropertyValue = false ;
						
						isSameEntity = isSameEntity && isSamePropertyValue ;
					}
				}								
				
			}
		}
		return isSameEntity ;
	}
	
	/*
	 * Check if the subject node from the input model has the same properties values
	 * of some of the entities stored in the output model. If so the RDF node is a duplicate
	 * of another node.
	 */
	public boolean isEntity(RDFNode node) {
		boolean isSamePropertyValue = false ;
		boolean isEntity = true ;
		Property typep = _model.getProperty(rdfNS + "type") ;
		String typepUri = typep.getURI() ;
		
		//Check the subjects' type
		RDFNode nodeType = node.asResource().getProperty(typep).getObject() ;
		System.out.println("Node type: " + nodeType) ;
		//Look for entities of the same type in the output model
		Selector typeSelector = new SimpleSelector(null, typep, (RDFNode) null) ;
		StmtIterator ires = outputModel.listStatements() ;
		Set<Statement> resources = new HashSet<Statement>() ;
		while(ires.hasNext()) {
			Statement res = ires.next() ;
			RDFNode entityType = res.getProperty(typep).getObject() ;
			System.out.println(res.toString()) ;
			if( nodeType.equals(entityType) ) {
				resources.add(res) ;
			}
		}								
			
		// If there are not entities of the same type return false
		if( resources.size() == 0 ) 
			return false ;
		
		// There are entities of the same type as the node so let's see their
		// properties. Only properties that have URI or literal values are
		// taken into account
		
		Iterator<Statement> ientities = resources.iterator() ;
		while(ientities.hasNext()) {
			StmtIterator inodeProps = node.asResource().listProperties() ;		
			while(inodeProps.hasNext()) {
				Statement nodeStmt = inodeProps.next() ;
				Property nodeProperty = nodeStmt.getPredicate() ;	
				RDFNode nodeObject = nodeStmt.getObject() ;
				Selector selector = new SimpleSelector(node.asResource(), nodeProperty, (RDFNode) null) ;
				StmtIterator ientityProps = outputModel.listStatements(selector);
				while(ientityProps.hasNext()) {								
					Statement entityStmt = ientityProps.next() ;
					Property entityProperty = entityStmt.getPredicate() ;
					if( (nodeProperty.getURI()).equals(entityProperty.getURI()) ) {					
						RDFNode entityObject = entityStmt.getObject() ;
						if( (!nodeObject.isAnon()) && (!entityObject.isAnon()) ) {
							if( ( nodeObject ).equals( entityObject.toString() ) ) 
								isSamePropertyValue = true ;						
							else
								isSamePropertyValue = false ;
							
							isEntity = isEntity && isSamePropertyValue ;
						}
					}								
					
				}
			}
		}
		
		return isEntity ;
	}
	
	/*
	private void loadRdfDataset(){
		
		 // use the FileManager to find the input file
		InputStream in = FileManager.get().open( rdfDatasetFileName );
		if (in == null) {		    
			System.out.println("File: " + rdfDatasetFileName + " not found");
			System.exit(0);
		}
		
		// create an empty model
		dataModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF);
		
		// read the RDF/TURTLE file
		dataModel.read(in, baseUri, "TURTLE");
		
	}
	*/
	
	private void loadEnsOntology(){
		
		// create an empty model
		ensModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF);
		
		OntDocumentManager dm = ensModel.getDocumentManager();
		dm.addAltEntry( ensNS, "file:" + ensOntology );
		ensModel.read( ensNS );	
		
		ExtendedIterator<Ontology> iOntologies = ensModel.listOntologies();
		while(iOntologies.hasNext()){
			Ontology ont = iOntologies.next();
			//System.out.println("ENS Ontology: " + ont.getURI());
		}
		
	}
	
	

}
