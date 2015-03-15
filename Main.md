# Introduction #

The purpose of the project is to reconcile entities identifiers between those available in a datasource as URI or blank nodes and those in an entity name system (ENS). In order to do reconciliation the dataset schema (column names) is mapped to an ontology. For each subject in the statements all the properties that have URIs or literals as values (object) are sent to the ENS as an entity description. If the description match with an entity its identifier is sent back and the model updated substituting the blan-node/temporary URI identifiers.


# Details #
The project uses Jena as a library to manipulate the statements in the dataset. The input dataset format is RDF/TURTLE