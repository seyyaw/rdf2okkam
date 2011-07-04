package org.okkam.dataset.parser;

import org.okkam.client.data.AttributesType;
import com.hp.hpl.jena.rdf.model.RDFNode;

public interface EntityBuildStrategy {
	
	public AttributesType getProperties(RDFNode subjectNode) ;

}
