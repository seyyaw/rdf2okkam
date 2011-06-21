package org.okkam.service.client;

//import it.okkam.test.ens.util.EnsConfig;
//import it.okkam.test.ens.util.EnsTestCredentials;
//import it.okkam.test.utility.EntityEvolutionResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

//import javax.servlet.ServletContext;
//import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.okkam.client.data.AttributeType;
import org.okkam.client.data.Entity;
import org.okkam.client.exception.OkkamClientException;
import org.okkam.client.util.XMLEntityConverter;
import org.okkam.core.data.api.MatchingCandidate;
import org.okkam.core.ws.data.EntityValidationReport;
import org.okkam.core.ws.data.NewEntityResultClient;
import org.okkam.core.ws.secured.OkkamCoreException;
import org.okkam.proxyclient.ProxyManager;
import org.okkam.service.client.EnsClient;

public class OkkamClient {
	private EnsClient entityManager;
	private String sessionId;
	private String processId;


	private byte[] userCertificate;
	private EnsConfig config;
	
	private HashMap<String, String> lockTracker;

	private boolean clientInitialized;

	private EnsCredentials credentials;
	String username = "usertest";
	String password = "changeit";
	String okkamPointer = "http://localhost:8080/okkam-core/WebServices"; // "http://193.1.101.88/okkam-core/WebServices";

	public OkkamClient(String path) {
		
		
		
		String userCertificateDataFolderPath = path;
		credentials = new EnsCredentials();

		try {
			config = new EnsConfig(userCertificateDataFolderPath);
			setSecurityProxyConnection(config);
			credentials.init(config.getUsername(), config.getPassword(), userCertificateDataFolderPath, config.getPointer());
			entityManager = new EnsClient(credentials);
			clientInitialized = true;
		} catch (OkkamClientException e) {
			clientInitialized = false;
			e.printStackTrace();
		} catch (IOException e) {
			clientInitialized = false;
			e.printStackTrace();
		}
		if (clientInitialized)
			init();
	}
	/*
	public OkkamClient(EnsTestCredentials credentials, EnsConfig config) {
		
		String path = "./conf/";
		//credentials = new EnsTestCredentials();

		try {
			//config = new EnsConfig(path);
			setSecurityProxyConnection(config);
			credentials.init(config.getUsername(), config.getPassword(), path,
					config.getPointer());
			entityManager = new EnsClient(credentials);
			clientInitialized = true;
		} catch (OkkamClientException e) {
			clientInitialized = false;
			e.printStackTrace();
		}
		if (clientInitialized)
			init();
	}
	*/
	
	private void setSecurityProxyConnection(EnsConfig conf) {
		if (conf.getProxyUrl() != null && !conf.getProxyUrl().equals("")) {

			ProxyManager proxyManager = ProxyManager.getInstance();
			proxyManager.setProxyHTTPHost(conf.getProxyUrl());
			proxyManager.setProxyHTTPPort(conf.getProxyPort());
			proxyManager.setProxyRequired(true);
			if (conf.getProxyUser() != null && !conf.getProxyUser().equals("")) {
				System.out.println("setting HTTP proxy with authentication.");
				proxyManager.setProxyAuthRequired(true);
				proxyManager.setProxyUser(conf.getProxyUser());
				proxyManager.setProxyPassword(conf.getProxyPassword());
			} else {
				proxyManager.setProxyAuthRequired(false);
				System.out
						.println("setting HTTP proxy without authentication.");
			}
			proxyManager.configureProxy();
		}
	}

	private void init() {
		lockTracker = new HashMap<String, String>();

	}

	
	public void unlockEntity(String oid,String ticket) {

	
			ticket = lockTracker.get(oid);
			lockTracker.remove(oid);
			// unlock entity in the ENS
			try {
				entityManager.unlockEntity(oid, ticket);
			} catch (IllegalArgumentException e) {

			} catch (OkkamClientException e) {

			} catch (OkkamCoreException e) {

			}
	
	}

	public EntityValidationReport validateEntity(Entity entity, boolean force) {
		EntityValidationReport report = null;
		try {
			report = entityManager.validateEntity(entity, force);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OkkamClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (org.okkam.core.ws.OkkamCoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return report;
	}

	public String createNewEntity(String certificate) {
		String result = null;

		try {
			 result = entityManager.createNewEntity(certificate);

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			result = null;
		} catch (OkkamClientException e) {
			e.printStackTrace();
			result = null;
		} catch (OkkamCoreException e) {
			e.printStackTrace();
			result = null;
		}
		return result;
	}
	
	public String lockEntity(String oid) {

		try {
			String ticket = entityManager.lockEntity(oid);
			lockTracker.put(oid, ticket);
			return ticket;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		} catch (OkkamClientException e) {
			e.printStackTrace();
			return null;
		} catch (OkkamCoreException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean lockEntities(ArrayList<String> oids) {

		try {
			ArrayList<String> tickets = new ArrayList<String>(entityManager
					.lockEntities(oids));
			for (int i = 0; i < tickets.size(); i++) {
				lockTracker.put(oids.get(i), tickets.get(i));
			}
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		} catch (OkkamClientException e) {
			return false;
		} catch (OkkamCoreException e) {
			return false;
		}
	}

	/*
	public EntityEvolutionResult mergeEntities(LinkedList<String> oids, Entity mergedEntity,LinkedList<String> tkts) {
		String[] tickets = new String[tkts.size()];
		String[] oidList = new String[oids.size()];
		int i=0;
		for (String oid : oids) {
			tickets[i]=tkts.get(i);
			oidList[i]=oid;
			i++;
		}

		EntityEvolutionResult result =new EntityEvolutionResult();
		try {
			NewEntityResultClient response = entityManager.mergeEntities(oidList, mergedEntity,
					tickets);
			result.getOid().add(response.getNewEntityURI());
			result.setMessage("Merging successfully executed.");
			result.setSuccessfulEvolution(true);
			return result;
		} catch (OkkamClientException e1) {
			// handle exception
			result.setMessage(e1.getMessage());
			result.setSuccessfulEvolution(false);
			return result;
		} catch (OkkamCoreException e1) {
			result.setMessage(e1.getMessage());
			result.setSuccessfulEvolution(false);
			return result;
		}
	}
	*/
	/*
	public EntityEvolutionResult splitEntity(String oid, Entity splitEntity1,
			Entity splitEntity2, String ticket) {
		EntityEvolutionResult result = new EntityEvolutionResult();

		ArrayList<Entity> splitEntities = new ArrayList<Entity>();
		splitEntities.add(splitEntity1);
		splitEntities.add(splitEntity2);

		try {
			NewEntityResultClient[] ensResult=entityManager.splitEntity(oid, splitEntities, ticket);
			result.getOid().add(ensResult[0].getNewEntityURI());
			result.getOid().add(ensResult[1].getNewEntityURI());
			result.setSuccessfulEvolution(true);
			result.setMessage("Split successfully executed.");
			return result;
		} catch (OkkamClientException ex1) {
			// handle exception
			result.setSuccessfulEvolution(false);
			result.setMessage(ex1.getMessage());
			return result;
		} catch (OkkamCoreException ex1) {
			result.setSuccessfulEvolution(false);
			result.setMessage(ex1.getMessage());
			return result;
		}
	}
	*/
	/*
	public EntityEvolutionResult deleteEntity(String oid,String ticket) {
	
		EntityEvolutionResult result = new EntityEvolutionResult();
		try {
			entityManager.deleteEntity(oid, ticket);
			result.setSuccessfulEvolution(true);
			result.setMessage("entity successfully deleted!");
			return result;
		} catch (IllegalArgumentException e) {
			result.setSuccessfulEvolution(false);
			result.setMessage(e.getMessage());
			return result;
		} catch (OkkamClientException e) {
			result.setSuccessfulEvolution(false);
			result.setMessage(e.getMessage());
			return result;
		} catch (OkkamCoreException e) {
			result.setSuccessfulEvolution(false);
			result.setMessage(e.getMessage()+" - "+e.getFaultInfo().getCode() +" "+e.getLocalizedMessage());
			return result;
		}
	}
	*/
	
	public ArrayList<MatchingCandidate> inquireWithNoConversion(String query){
		ArrayList<MatchingCandidate> clientResult = null;
		try {
			
			clientResult = new ArrayList<MatchingCandidate>(entityManager
					.findEntity(query));
			
		} catch (Exception e) {
			// error one management
			e.printStackTrace();
	
		}
		return clientResult;
	}
	
	public ArrayList<Entity> inquireOkkam(String labels) {

		ArrayList<Entity> result = new ArrayList<Entity>();
		ArrayList<MatchingCandidate> clientResult = null;

		// inquire okkam
		// AnnotatedQuery query = composeAnnotatedQuery(labels);
		/*String query = "QUERY {\"" + labels
				+ "\"} METADATA {matchingModule=gl}";*/
		String query = labels;
		System.out.println(query);

		try {
			long start = System.currentTimeMillis();
			clientResult = new ArrayList<MatchingCandidate>(entityManager.findEntity(query));
			System.out.println("Query Time: "
					+ (System.currentTimeMillis() - start) + " ms");
		} catch (Exception e) {
			// error one management
			e.printStackTrace();
	
		}

		if (clientResult != null && clientResult.size() != 0) {

			System.out.println("result not null! " + clientResult.size()
					+ " entities found");
			ArrayList<Entity> wrappedResult = new ArrayList<Entity>();
			
			for (MatchingCandidate candidate : clientResult) {
				/*
				 * GetEntityResult res = entityManager.getEntity(candidate
				 * .getOid(), sessionMetadata, authKey); Entity entity =
				 * res.getEntity(); System.out.println(entity.getOid()); System
				 * .out.println(entity.getProfile().getSemanticType());
				 * OkkamEntity okkamEntity = entity2OkkamEntity(entity, false);
				 */
				Entity okkamEntity;
				XMLEntityConverter converter = new XMLEntityConverter();
				try {
					okkamEntity = converter
							.xmlToEntity(candidate.getXML());
					wrappedResult.add(okkamEntity);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			result = wrappedResult;

		} 
		return result;

	}

	public ArrayList<Entity> updateEntityOnOkkam(
			Entity e,
			String okkamId, String ticket,boolean force) {
		ArrayList<Entity> result = new ArrayList<Entity>();

		
		EntityValidationReport report = validateEntity(e, force);
		String certificate = null;
		if (report != null) {
			certificate = report.getCertificate();
		} else {
			return null;
		}
		if (certificate != null && !"".equals(certificate)) {
			try {
				entityManager.updateEntity(ticket, certificate);
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			XMLEntityConverter entityConverter = new XMLEntityConverter();
			ArrayList<Entity> duplicates = new ArrayList<Entity>();
			for (String entityXML : report.getCandidateDuplicates()) {
				Entity ent = null;
				try {
					ent = entityConverter.xmlToEntity(entityXML);
				} catch (JAXBException ex) {
					ex.printStackTrace();
					
				}
				duplicates.add(ent);
			}
		}

		return result;

	}

	public void updateEntityOnOkkam(String ticket,String certificate) {

				try {
					entityManager.updateEntity(ticket, certificate);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OkkamClientException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OkkamCoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
	}
	
	public Entity getEntity(String oid) {

		Entity result = null;
		try {
			result = entityManager.getEntity(oid);

			
		} catch (OkkamCoreException e) {
			e.printStackTrace();
		} catch (OkkamClientException e) {
			e.printStackTrace();
		}
		return result;
	}

	public ArrayList<QName> getDefaultAttributes(String type) {
		ArrayList<QName> defaults = null;
		Entity e = null;
		try {
			e = entityManager.getTypeTemplate(type);
		} catch (OkkamClientException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (OkkamCoreException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		if (e != null) {
			defaults = new ArrayList<QName>();
			for (AttributeType atr : e.getProfile().getAttributes()
					.getAttributes()) {
				QName qName = atr.getName();
				String namespace = atr.getName().getNamespaceURI();
				// System.out.println("namespace: "+namespace);
				//String prefix = atr.getName().getPrefix();
				// System.out.println("prefix: "+prefix);
				//String label;
				//if(namespace.endsWith("/") || namespace.endsWith("#")){
				//	label = namespace+atr.getName().getLocalPart();
				//}else{
				//	label = namespace+"#"+atr.getName().getLocalPart();
				//}
				 
				// System.out.println("name: "+label);
				// df.setLabel(prefix + ":" + label);

				defaults.add(qName);
			}
		}
		return defaults;
	}
	
	public List<String> getOidsByAlternativeId(String oid) {
		ArrayList<String> result=null;
		try {
			return entityManager.getOidsByAlternativeId(oid);
		} catch (OkkamClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OkkamCoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public boolean isClientInitialized() {
		return clientInitialized;
	}

	public void setClientInitialized(boolean clientInitialized) {
		this.clientInitialized = clientInitialized;
	}

	public byte[] getUserCertificate() {
		return userCertificate;
	}

	public void setUserCertificate(byte[] userCertificate) {
		this.userCertificate = userCertificate;
	}

	public EnsConfig getConfig() {
		return config;
	}



}
