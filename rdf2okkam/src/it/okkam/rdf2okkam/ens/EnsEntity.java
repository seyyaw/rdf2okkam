package it.okkam.rdf2okkam.ens;

import it.okkam.rdf2okkam.ens.client.EnsQuery;

import org.okkam.client.data.AttributesType;
import org.okkam.client.data.ProfileType;

import com.hp.hpl.jena.rdf.model.RDFNode;

public interface EnsEntity {
	
	public String getSemanticType() ;
	
	public AttributesType getAttributesType() ;
	
	public ProfileType getProfile() ;
	
	public String getQuery() ;

}
