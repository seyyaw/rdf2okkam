package it.okkam.rdf2okkam.parser;

import org.okkam.client.data.AttributesType;
import org.okkam.client.data.ProfileType;

import com.hp.hpl.jena.rdf.model.RDFNode;

public interface EntityBuildStrategy {
	
	public ProfileType getProperties(RDFNode subjectNode) ;

}
