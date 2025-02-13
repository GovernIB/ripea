/**
 * 
 */
package es.caib.ripea.plugin.caib.usuari;

import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.usuari.DadesUsuari;
import es.caib.ripea.plugin.usuari.DadesUsuariPlugin;
import org.fundaciobit.pluginsib.userinformation.UserInfo;
import org.fundaciobit.pluginsib.userinformation.keycloak.KeyCloakUserInformationPlugin;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotFoundException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementació del plugin de consulta de dades d'usuaris emprant el plugin de Keycloak. Les propietats necessàries són les següents a partir
 * de es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.keycloak. :
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
public class DadesUsuariPluginKeycloak extends KeyCloakUserInformationPlugin implements DadesUsuariPlugin {

	public DadesUsuariPluginKeycloak(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}

	public DadesUsuariPluginKeycloak(String propertyKeyBase) {
		super(propertyKeyBase);
	}
	
	@Override
	public DadesUsuari findAmbCodi(
			String usuariCodi) throws SistemaExternException {
		LOGGER.debug("Consulta de les dades de l'usuari (usuariCodi=" + usuariCodi + ")");
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
		LOGGER.debug("Consulta dels usuaris del grup (grupCodi=" + grupCodi + ")");
		try {
			Collection<UserRepresentation> usuaris = internalGetUserNamesByRol(grupCodi);
			if (usuaris != null) {
				return usuaris.stream().
						map(ur -> DadesUsuari.builder().
									codi(ur.getUsername()).
									nom(ur.getFirstName() + (ur.getLastName() != null ? " " + ur.getLastName() : "")).
									build()).
						collect(Collectors.toList());
			} else {
				return Arrays.asList(new DadesUsuari[0]);
			}
		} catch (Exception ex) {
			throw new SistemaExternException(
					"Error al consultar els usuaris del grup " + grupCodi,
					ex);
		}
	}

	@Override
	public List<DadesUsuari> findAmbFiltre(String filtre) throws SistemaExternException {
		return List.of();
	}

	@Override
	public List<String> findRolsAmbCodi(String usuariCodi) throws SistemaExternException {
		return List.of();
	}

	@Override
	public String[] getUsernamesByRol(String rol) throws Exception {
		Collection<UserRepresentation> users = internalGetUserNamesByRol(rol);
		if (users != null) {
			return users.stream().
					map(ur -> ur.getUsername()).
					toArray(String[]::new);
		} else {
			return new String[0];
		}
	}

	private Collection<UserRepresentation> internalGetUserNamesByRol(String rol) {
		Set<UserRepresentation> usernamesClientApp = null;
		Set<UserRepresentation> usernamesClientPersons = null;
		Set<UserRepresentation> usersRealm = null;
		try {
			String appClient = this.getPropertyRequired("pluginsib.userinformation.keycloak.client_id");
			usernamesClientApp = this.getUsernamesByRolOfClient(rol, appClient);
		} catch (Exception ex) {
			log.warn("No s'han obtingut usuaris per client d'aplicació: " + ex.toString(), (this.isDebug() ? ex : null));
		}
		try {
			String personsClient = this.getPropertyRequired("pluginsib.userinformation.keycloak.client_id_for_user_autentication");
			usernamesClientPersons = this.getUsernamesByRolOfClient(rol, personsClient);
		} catch (Exception ex) {
			log.warn("No s'han obtingut usuaris per client de persones: " + ex.toString(), (this.isDebug() ? ex : null));
		}
		try {
			usersRealm = this.getUsernamesByRolOfRealm(rol);
		} catch (Exception ex) {
			log.warn("No s'han obtingut usuaris per realm: " + ex.toString(), (this.isDebug() ? ex : null));
		}
		if (usernamesClientApp == null && usernamesClientPersons == null && usersRealm == null) {
			return null;
		}
		Map<String, UserRepresentation> users = new HashMap<>();
		if (usernamesClientApp != null) {
			usernamesClientApp.stream().forEach(u -> {
				users.put(u.getUsername(), u);
			});
		}
		if (usernamesClientPersons != null) {
			usernamesClientPersons.stream().forEach(u -> {
				users.put(u.getUsername(), u);
			});
		}
		if (usersRealm != null) {
			usersRealm.stream().forEach(u -> {
				users.put(u.getUsername(), u);
			});
		}
		return users.values();
	}

	private Set<UserRepresentation> getUsernamesByRolOfRealm(String rol) throws Exception {
		RolesResource roleres = this.getKeyCloakConnectionForRoles();
		try {
			return roleres.get(rol).getRoleUserMembers();
		} catch (NotFoundException var7) {
			return null;
		}
	}

	private Set<UserRepresentation> getUsernamesByRolOfClient(String rol, String client) throws Exception {
		Keycloak keycloak = this.getKeyCloakConnection();
		ClientsResource clientsApi = keycloak.realm(this.getPropertyRequired("pluginsib.userinformation.keycloak.realm")).clients();
		List<ClientRepresentation> crList = clientsApi.findByClientId(client);
		if (crList == null || crList.isEmpty()) {
			return null;
		}
		ClientResource c = clientsApi.get((crList.get(0)).getId());
		RolesResource rrs = c.roles();
		try {
			RoleResource rr = rrs.get(rol);
			return rr.getRoleUserMembers();
		} catch (NotFoundException var13) {
			return null;
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

	private static final Logger LOGGER = LoggerFactory.getLogger(DadesUsuariPluginKeycloak.class);

	@Override
	public String getEndpointURL() {
		return "";
	}
}
