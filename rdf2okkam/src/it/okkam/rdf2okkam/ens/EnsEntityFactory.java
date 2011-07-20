package it.okkam.rdf2okkam.ens;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;

public abstract class EnsEntityFactory {
	
	public abstract EnsEntity createEnsEntity(Model model, RDFNode node) ;

}
