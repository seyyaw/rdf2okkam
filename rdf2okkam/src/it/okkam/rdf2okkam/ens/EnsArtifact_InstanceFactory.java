package it.okkam.rdf2okkam.ens;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;


public class EnsArtifact_InstanceFactory extends EnsEntityFactory {

	
	public EnsEntity createEnsEntity(Model model, RDFNode node) {
 
		return (EnsEntity) new EnsArtifact_Instance( model, node ) ;
		
	}

}
