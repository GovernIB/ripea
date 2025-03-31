package es.caib.ripea.plugin.caib.usuari;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.fundaciobit.pluginsib.userinformation.SearchUsersResult;
import org.fundaciobit.pluginsib.userinformation.UserInfo;
import org.fundaciobit.pluginsib.userinformation.ldap.LdapUserInformationPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.usuari.DadesUsuari;
import es.caib.ripea.plugin.usuari.DadesUsuariPlugin;
import es.caib.ripea.service.intf.utils.Utils;

/**
 * Implementació del plugin de consulta de dades d'usuaris emprant el plugin de LDAP. Les propietats necessàries són les següents a partir
 * de es.caib.distribucio.pluginib.dades.usuari.pluginsib.userinformation.ldap. :
 * 
 * - serverurl: Url del servidor de keycloak
 * - realm: Realm del keycloak.7
 * - client_id: Client ID del keycloak.
 * - client_id_for_user_autentication: Client ID per autenticació del keycloak.
 * - password_secret: Secret del client de keycloak.
 * - mapping.administrationID: Mapeig del administrationID de keycloak.
 * - debug: Activar el debug del plugin de keycloak.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesUsuariPluginLdapCaib extends LdapUserInformationPlugin implements DadesUsuariPlugin {

	public DadesUsuariPluginLdapCaib(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}

	public DadesUsuariPluginLdapCaib(String propertyKeyBase) {
		super(propertyKeyBase);
	}
	
	@Override
	public DadesUsuari findAmbCodi(
			String usuariCodi) throws SistemaExternException {
		LOGGER.debug("Consulta de les dades de l'usuari LDAP CAIB (usuariCodi=" + usuariCodi + ")");
		try {
			UserInfo userInfo = getUserInfoByUserName(usuariCodi);
			return toDadesUsuari(userInfo);
		} catch (Exception ex) {
			throw new SistemaExternException(
					"Error al consultar l'usuari amb codi " + usuariCodi,
					ex);
		}
	}

	@Override
	public List<DadesUsuari> findAmbGrup(
			String grupCodi) throws SistemaExternException {
		LOGGER.debug("Consulta dels usuaris del grup LDAP CAIB (grupCodi=" + grupCodi + ")");
		try {
			List<DadesUsuari> dadesUsuaris = new ArrayList<>();
			UserInfo[] usersInfo = this.getUserInfoByRol(grupCodi);
			if (usersInfo != null) {
				for (int i = 0; i < usersInfo.length; i++) {
					dadesUsuaris.add(toDadesUsuari(usersInfo[i]));
				}
			}
			return dadesUsuaris;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"Error al consultar els usuaris del grup LDAP CAIB " + grupCodi,
					ex);
		}
	}

	private DadesUsuari toDadesUsuari(UserInfo userInfo) {
		if (userInfo != null) {
			DadesUsuari dadesUsuari = new DadesUsuari();
			dadesUsuari.setCodi(userInfo.getUsername());
			dadesUsuari.setNomSencer(userInfo.getFullName());
			dadesUsuari.setNom(userInfo.getName());
			dadesUsuari.setLlinatges(userInfo.getSurname1() + (userInfo.getSurname2() != null ? " " + userInfo.getSurname2() : ""));
			dadesUsuari.setNif(userInfo.getAdministrationID());
			dadesUsuari.setEmail(userInfo.getEmail());
			return dadesUsuari;
		} else {
			return null;
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(DadesUsuariPluginLdapCaib.class);

	@Override
	public String getEndpointURL() {
		String endpoint = getProperty("plugin.dades.usuari.endpointName");
		if (Utils.isEmpty(endpoint)) {
			endpoint = getProperty("plugin.dades.usuari.pluginsib.userinformation.ldap.host_url");
		}
		return endpoint;
	}

	@Override
	public List<DadesUsuari> findAmbFiltre(String filtre) throws SistemaExternException {

		try {
			List<DadesUsuari> resultat = new ArrayList<DadesUsuari>();
			SearchUsersResult sur = getUsersByPartialValuesOr(filtre, filtre, filtre, filtre, filtre);
			if (sur!=null) {
				for (UserInfo userInfo: sur.getUsers()) {
					resultat.add(toDadesUsuari(userInfo));
				}
			}
			return resultat;
		} catch (Exception ex) {
			if (ex.getMessage().contains("Do not implemented")) {
				return null;
			} else {
				throw new SistemaExternException("Error al consultar els usuaris per filtre " + filtre, ex);
			}
		}
	}

	@Override
	public List<String> findRolsAmbCodi(String usuariCodi) throws SistemaExternException {
		return findRolsAmbCodi(usuariCodi);
	}
}