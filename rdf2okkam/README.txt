The purpose of the project code is to parse the RDF data coming
from Google-Refine using the RDF Extensions that contains statements
about entities to be created in the ENS.

The steps are as follows:
1) Load the ENS ontology (http://models.okkam.org/ENS-meta-core-vocabulary.owl#)
2) Load the RDF data
3) Select the rdf:type(s) that are used in the ENS ontology (person, location, event,..)
4) For each type get all the instances
5) For each instance get all its properties, create an entity object and populate
   the entity's attributes
6) Call the ENS WS to create entities for the entities objects
   
The 4th step must be detailed since the property can be set using the ENS ontology
or can be simply tags. The ENS property will form the default attributes of the entities.
  