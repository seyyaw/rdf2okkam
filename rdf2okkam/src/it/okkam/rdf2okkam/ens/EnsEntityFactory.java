package it.okkam.rdf2okkam.ens;

import com.hp.hpl.jena.rdf.model.Model;

public abstract class EnsEntityFactory {
	
	abstract EnsEntity createEnsEntity(Model model) ;

}
