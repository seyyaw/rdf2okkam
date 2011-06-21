package org.okkam.service.client;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.okkam.client.data.AccessControlMetadataType;
import org.okkam.client.data.AlternativeIdsType;
import org.okkam.client.data.AttributeMetadataType;
import org.okkam.client.data.AttributeType;
import org.okkam.client.data.AttributesType;
import org.okkam.client.data.Entity;
import org.okkam.client.data.EquivalentOidsType;
import org.okkam.client.data.MetadataType;
import org.okkam.client.data.ProfileType;
import org.okkam.client.data.ProvenanceMetadataType;
import org.okkam.client.data.ReferencesType;
import org.okkam.core.data.api.SemanticType;
import org.okkam.core.ws.data.EntityValidationReport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;



public class ServiceClient {
	
	private OkkamClient _okkamClient;
	private ArrayList<String> entityTypes;
	private String namespaceUri = "http://okkamprivatenode.org";
	private String entityType = SemanticType.PERSON;
	private int entityTypeIndex = 6; //index value for person
	private static Log log = LogFactory.getLog(ServiceClient.class);
	

	
	public ServiceClient(OkkamClient okkamclient){		

		log.info("Starting service client.");

		_okkamClient = okkamclient;
		
		entityTypes = new ArrayList<String>();
		entityTypes.add(SemanticType.ARTIFACT_INSTANCE);
		entityTypes.add(SemanticType.ARTIFACT_TYPE);
		entityTypes.add(SemanticType.EVENT);
		entityTypes.add(SemanticType.LOCATION);
		entityTypes.add(SemanticType.ORGANIZATION);
		entityTypes.add(SemanticType.OTHER);
		entityTypes.add(SemanticType.PERSON);
		
	}
	
	
	/*
	 * An entity has
	 * - a profile
	 * - equivalent ids
	 * - alternative ids
	 * A profile has
	 * - semantic type
	 * - references
	 * - a list of attributes
	 * 
	 * Creates a new entity in the ENS and returns the identifier. The boolean argument enable the client 
	 * to create a new entity also in presence of entities that are considered the same by the ENS.  
	 */
	public String createNewEntity(String entityType, AttributesType attributeList, boolean ignoreDuplicates) {
		
		log.debug("Creating a new entity");
		
		//THE ENTITY OBJECT IS THE WRAPPER OF ALL THE ENTITY'S INFORMATION
		Entity entity = new Entity();
		
		// CREATE AN ALTERNATIVE IDS SET (IF ANY)
		AlternativeIdsType alts = new AlternativeIdsType();
		
		// CREATE AN EQUIVALENT IDS SET (IF ANY)   
		EquivalentOidsType eq = new EquivalentOidsType();
		
		// ENTITY PROFILE CONTAINS MOST OF THE ENTITY DESCRIPTION
		ProfileType profile = new ProfileType();
		
		// CREATE AN EXTERNAL REFERENCE SET (IF ANY)
		ReferencesType refs = new ReferencesType();  

		// SET THE SEMANTIC TYPE OF THE ENTITY 		
		String semantic_type = entityType; 
		
		
		if (attributeList.getAttributes().size() > 0) {

		    System.out.println("Adding attributes");

		    profile.setAttributes(attributeList);

		}
		 

		if (entityType != null && !"".equals(semantic_type)) {

		    System.out.println("Adding semantic type");

		    profile.setSemanticType(semantic_type);

		}
		
		if (refs.getReferences().size() > 0) {

		    System.out.println("Adding references");

		    profile.setReferences(refs);

		}

		 

		if (alts.getAlternativeIds().size() > 0) {

		    System.out.println("Adding alternatives");

		    entity.setAlternativeIds(alts);

		}

		 

		if (eq != null && eq.getEquivalentOids().size() > 0) {

		    System.out.println("Adding equivalents");

		    entity.setEquivalentOids(eq);

		}

		// ADD PROFILE TO ENTITY OBJECT

		entity.setProfile(profile);
		
		
		
		
		// ENTITY VALIDATION
		EntityValidationReport report = null;
		// Force check for double entities ie ignore duplicates
		//(true: do not check for double entities, ignore duplicates)
		//boolean ignoreDuplicates = false;
		
		try {
			
			 report = _okkamClient.validateEntity(entity, ignoreDuplicates);
			 
		}
		catch(Throwable t){
		
			t.printStackTrace();
			log.error("Cannot create a new entity. ");
			
		}
		
		assertNotNull(report);
		
		// IF THE VALIDATION WAS POSITIVE, A VALIDATION CERTIFICATE IS RETURNED

		String certificate = report.getCertificate();
		
		log.debug("Certificate: " + certificate);
		
		String okkamId = "";

		if(certificate != null){

		    // IF THE CERTIFICATE IS NOT NULL, THE VALIDATION IS POSITIVE AND THE ENTITY CAN BE CREATED

		     okkamId = _okkamClient.createNewEntity(certificate);
		     
		     log.debug("New entity: " + okkamId);
		    

		} 
		else {

		    log.debug("See validateEntity for more information about how to manage entity validation report.");

		}
		
		return okkamId;
	}
	
	

}
