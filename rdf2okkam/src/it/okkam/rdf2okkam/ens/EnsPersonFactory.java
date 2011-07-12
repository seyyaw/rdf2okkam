package it.okkam.rdf2okkam.ens;

import com.hp.hpl.jena.rdf.model.Model;


public class EnsPersonFactory extends EnsEntityFactory {

	@Override
	EnsEntity createEnsEntity(Model model) {
 
		return (EnsEntity) new EnsPerson( model ) ;
	}

}
