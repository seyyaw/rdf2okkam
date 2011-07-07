package it.okkam.rdf2okkam.parser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.okkam.client.data.AttributesType;
import org.okkam.client.data.ProfileType;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;


public class EntityBuildContext {
	
	private static Log log = LogFactory.getLog(EntityBuildContext.class);

	private EntityBuildStrategy strategy = null ;
	
	private Model _model ;
	
	public EntityBuildContext(Model model){
		_model = model ;
	}
	
	public void setLocationEntityBuilding() {

		this.strategy = new Location( _model ) ;
	}
	
	public void setPersonEntityBuilding() {

		this.strategy = new Person( _model ) ;
	}
	
	public ProfileType getProperties(RDFNode subjectNode) {
		ProfileType profile = null ;
		if ( strategy != null){
			profile = strategy.getProperties( subjectNode );
		}
		else {
			log.error("Set the entity type: person, location, event, artifact_type, artifact_instance, other.");
		}
		
		return profile ;
	}
}
