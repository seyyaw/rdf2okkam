package it.okkam.rdf2okkam.ens;

import org.okkam.client.data.ProfileType;

import com.hp.hpl.jena.rdf.model.RDFNode;

public interface EnsEntity {
	
	public String getSemanticType() ;
	
	public ProfileType getProperties(RDFNode subjectNode) ;

}
