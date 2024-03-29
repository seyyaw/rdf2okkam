package it.okkam.rdf2okkam.ens;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;


public class EnsEventFactory extends EnsEntityFactory {

	
	public EnsEntity createEnsEntity(Model model, RDFNode node) {
 
		return (EnsEntity) new EnsEvent( model, node ) ;
		
	}

}
