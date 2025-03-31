package es.caib.ripea.plugin.caib.usuari;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import es.caib.ripea.service.intf.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.ripea.plugin.RipeaAbstractPluginProperties;
import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.SistemaExternNoTrobatException;
import es.caib.ripea.plugin.usuari.DadesUsuari;
import es.caib.ripea.plugin.usuari.DadesUsuariPlugin;

public class DadesUsuariPluginLdap extends RipeaAbstractPluginProperties implements DadesUsuariPlugin {

	public DadesUsuariPluginLdap() {
		super();
	}

	public DadesUsuariPluginLdap(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}

	@Override
	public List<String> findRolsAmbCodi(String usuariCodi) throws SistemaExternException {
		
		LOGGER.debug("Consulta dels rols de l'usuari (usuariCodi=" + usuariCodi + ")");
		
		try {
		
			List<String> rolsUsuari = new ArrayList<String>();
			Hashtable<String, String> entornLdap = new Hashtable<String, String>();
			entornLdap.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			entornLdap.put(Context.PROVIDER_URL, getLdapServerUrl());
			entornLdap.put(Context.SECURITY_PRINCIPAL, getLdapPrincipal());
			entornLdap.put(Context.SECURITY_CREDENTIALS, getLdapCredentials());
			LdapContext ctx = new InitialLdapContext(entornLdap, null);
			
			try {
				String[] atributs = getLdapAtributs().split(",");
				SearchControls searchCtls = new SearchControls();
				searchCtls.setReturningAttributes(atributs);
				searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
				NamingEnumeration<SearchResult> answer = ctx.search(
						getLdapSearchBase(),
						getLdapFiltreCodi().replace("XXX", usuariCodi),
						searchCtls);
				while (answer.hasMoreElements()) {
					SearchResult result = answer.next();
						rolsUsuari = obtenirAtributComListString(
								result.getAttributes(),
								atributs[5]);
					
				}
			} finally {
				ctx.close();
			}
			
			return rolsUsuari;
			
		} catch (Exception ex) {
			throw new SistemaExternException("Error al consultar els rols de l'usuari (usuariCodi=" + usuariCodi + ")", ex);
		}
	}
	
	@Override
	public DadesUsuari findAmbCodi(String usuariCodi) throws SistemaExternException {
		
		LOGGER.debug("Consulta de les dades de l'usuari (codi=" + usuariCodi + ")");
		
		try {
			
			String filtre = getLdapFiltreCodi();
			List<DadesUsuari> usuaris = consultaUsuaris(filtre, usuariCodi);
			
			if (usuaris.size() == 1) {
				return usuaris.get(0);
			} else if(usuaris.size() > 1){
				throw new SistemaExternException(
						"La consulta d'usuari únic ha retornat més d'un resultat (" +
						"filtre=" + filtre + ", " +
						"valor=" + usuariCodi + ")");
			} else if(usuaris.size() == 0){
				usuaris = findAmbFiltre(usuariCodi);
				if (usuaris.size() == 0) {
					throw new SistemaExternNoTrobatException(
							"La consulta d'usuari únic no ha retornat cap resultat (" +
							"filtre=" + filtre + ", " +
							"valor=" + usuariCodi + ")");
				} else {
					return usuaris.get(0);
				}
			} else {
				throw new SistemaExternException("Error desconegut al consultar un usuari únic");
			}
		} catch (SistemaExternException ex) {
			throw ex;
		} catch (NamingException ex) {
			throw new SistemaExternException("Error al consultar l'usuari amb codi (codi=" + usuariCodi + ")",ex);
		}
	}

	@Override
	public List<DadesUsuari> findAmbGrup(
			String grupCodi) throws SistemaExternException {
		LOGGER.debug("Consulta dels usuaris del grup (grupCodi=" + grupCodi + ")");
		try {
			return consultaUsuaris(getLdapFiltreGrup(), grupCodi);
		} catch (NamingException ex) {
			throw new SistemaExternException(
					"Error al consultar els usuaris del grup (grupCodi=" + grupCodi + ")",
					ex);
		}
	}

	private List<DadesUsuari> consultaUsuaris(String filtre, String valor) throws NamingException {
		
		List<DadesUsuari> usuaris = new ArrayList<DadesUsuari>();
		Hashtable<String, String> entornLdap = new Hashtable<String, String>();
		entornLdap.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		entornLdap.put(Context.PROVIDER_URL, getLdapServerUrl());
		entornLdap.put(Context.SECURITY_PRINCIPAL, getLdapPrincipal());
		entornLdap.put(Context.SECURITY_CREDENTIALS, getLdapCredentials());
		LdapContext ctx = new InitialLdapContext(entornLdap, null);
		
		try {
			String[] atributs = getLdapAtributs().split(",");
			SearchControls searchCtls = new SearchControls();
			searchCtls.setReturningAttributes(atributs);
			searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<SearchResult> answer = ctx.search(getLdapSearchBase(), filtre.replace("XXX", valor), searchCtls);
			
			while (answer.hasMoreElements()) {
				SearchResult result = answer.next();
				String grup = obtenirAtributComString(result.getAttributes(), atributs[4]);
				String memberOf = obtenirAtributComString(result.getAttributes(), atributs[5]);
				boolean excloure = false;
				if (getLdapExcloureGrup() != null) {
					excloure = grup.equals(getLdapExcloureGrup());
					if (excloure && getLdapExcloureMembre() != null) {
						excloure = memberOf.contains(getLdapExcloureMembre());
					}
				}
				if (!excloure) {
					String codi 		= obtenirAtributComString(result.getAttributes(), atributs[0]);
					String nom 			= obtenirAtributComString(result.getAttributes(), atributs[1]);
					String llinatges 	= obtenirAtributComString(result.getAttributes(), atributs[2]);
					String email 		= obtenirAtributComString(result.getAttributes(), atributs[3]);
					String nif 			= obtenirAtributComString(result.getAttributes(), atributs[4]);
					DadesUsuari dadesUsuari = new DadesUsuari();
					dadesUsuari.setCodi(codi);
					dadesUsuari.setNom(nom);
					dadesUsuari.setLlinatges(llinatges);
					dadesUsuari.setEmail(email);
					dadesUsuari.setNif(nif);
					usuaris.add(dadesUsuari);
				}
			}
		} finally {
			ctx.close();
		}
		return usuaris;
	}
	
	@SuppressWarnings("rawtypes")
	private List<String> obtenirAtributComListString(Attributes atributs, String atributNom) throws NamingException {
		
		Attribute atribut = atributs.get(atributNom);
		List<String> listRols = new ArrayList<String>();		
		NamingEnumeration rols = atribut.getAll();
		
		while (rols.hasMoreElements()) {
			String rol = rols.next().toString();
			int iniciIndexRol = rol.indexOf("CN=") + 3;
			int fiIndexRol = rol.indexOf(",");
			listRols.add(rol.substring(iniciIndexRol, fiIndexRol));
		}
		
		return listRols;
	}
	
	@Override
	public List<DadesUsuari> findAmbFiltre(String filtre) throws SistemaExternException{
		LOGGER.debug("Consulta de les dades de l'usuari (filtre=" + filtre + ")");
		try {
			return consultaUsuaris(
					getLdapFiltre(), 
					filtre);
		} catch (NamingException e) {
			throw new SistemaExternException(
					"La consulta dels usuaris no ha pogut recuperar cap resultat(" +
					"filtre=" + getLdapFiltreCodi() + ", " +
					"valor=" + filtre + ")");
		}
	}

	private String obtenirAtributComString(
			Attributes atributs,
			String atributNom) throws NamingException {
		Attribute atribut = atributs.get(atributNom);
		return (atribut != null) ? (String)atribut.get() : null;
	}

	@Override
	public String getEndpointURL() {
		String endpoint = getProperty("plugin.dades.usuari.endpointName");
		if (Utils.isEmpty(endpoint)) {
			endpoint = getLdapServerUrl();
		}
		return endpoint;
	}
	
	private String getLdapServerUrl() {
		return getProperty("plugin.dades.usuari.ldap.server.url");
	}
	private String getLdapPrincipal() {
		return getProperty("plugin.dades.usuari.ldap.principal");
	}
	private String getLdapCredentials() {
		return getProperty("plugin.dades.usuari.ldap.credentials");
	}
	private String getLdapSearchBase() {
		return getProperty("plugin.dades.usuari.ldap.search.base");
	}
	private String getLdapAtributs() {
		// Exemple: cn,givenName,sn,mail,departmentNumber,memberOf
		return getProperty("plugin.dades.usuari.ldap.atributs");
	}
	private String getLdapFiltreCodi() {
		// Exemple: (&(objectClass=inetOrgPersonCAIB)(cn=XXX))
		return getProperty("plugin.dades.usuari.ldap.filtre.codi");
	}
	private String getLdapFiltre() {
		// Exemple: (&(displayName=inetOrgPersonCAIB)(cn=XXX))
		return getProperty("plugin.dades.usuari.ldap.filtre");
	}
	private String getLdapFiltreGrup() {
		// Exemple: (&(objectClass=inetOrgPersonCAIB)(memberOf=cn=XXX,dc=caib,dc=es))
		return getProperty("plugin.dades.usuari.ldap.filtre");
	}
	private String getLdapExcloureGrup() {
		return getProperty("plugin.dades.usuari.ldap.excloure.grup");
	}
	private String getLdapExcloureMembre() {
		return getProperty("plugin.dades.usuari.ldap.excloure.membre");
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(DadesUsuariPluginLdap.class);
}