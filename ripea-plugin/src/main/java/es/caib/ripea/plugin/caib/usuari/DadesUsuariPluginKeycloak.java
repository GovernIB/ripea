package es.caib.ripea.plugin.caib.usuari;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.NotFoundException;

import org.fundaciobit.pluginsib.userinformation.RolesInfo;
import org.fundaciobit.pluginsib.userinformation.SearchUsersResult;
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

import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.usuari.DadesUsuari;
import es.caib.ripea.plugin.usuari.DadesUsuariPlugin;

/**
 * Implementació del plugin de consulta de dades d'usuaris emprant el plugin de Keycloak. Les propietats necessàries són les següents a partir
 * de es.caib.ripea.plugin.dades.usuari.pluginsib.userinformation.keycloak. :
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
		LOGGER.debug("Consulta dels usuaris amb filtre (filtre=" + filtre + ")");
		List<DadesUsuari> resultat = new ArrayList<DadesUsuari>();
		try {
			List<UserInfo> usuaris = internalSearchUsers(filtre);
			if (usuaris != null && usuaris.size()>0) {
				for (UserInfo ui: usuaris) {
					resultat.add(toDadesUsuari(ui));
				}
			}
		} catch (Exception ex) {
			throw new SistemaExternException("Error al consultar els usuaris amb filtre (filtre=" + filtre + ")", ex);
		}
		return resultat;
	}
	

	@Override
	public List<String> findRolsAmbCodi(String usuariCodi) throws SistemaExternException {
		
		List<String> resultat = new ArrayList<String>();
		
		try {
			RolesInfo ri = getRolesByUsername(usuariCodi);
			if (ri!=null && ri.getRoles()!=null) {
				return Arrays.asList(ri.getRoles());
			}
		} catch (Exception ex) {
			log.error("Error al consultar els rols del usuari "+usuariCodi, ex);
		}
		
		return resultat;
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
	
	private List<UserInfo> internalSearchUsers(String filter) {
		List<UserInfo> usernamesFound = new ArrayList<UserInfo>();
		try {
			SearchUsersResult sur1 = this.getUsersByPartialNameOrPartialSurnames(filter);
			if (sur1!=null && sur1.getUsers()!=null)
				usernamesFound.addAll(sur1.getUsers());
		} catch (Exception ex) {
			log.warn("No s'han obtingut usuaris per nom parcial: " + filter);
		}
		try {
			SearchUsersResult sur2 = this.getUsersByPartialUserName(filter);
			if (sur2!=null && sur2.getUsers()!=null) {
				for (UserInfo ui: sur2.getUsers()) {
					boolean found = false;
					for (UserInfo unf: usernamesFound) {
						if (ui.getUsername().equals(unf.getUsername())) {
							found = true;
						}
					}
					if (!found) {
						usernamesFound.add(ui);
					}
				}
			}
		} catch (Exception ex) {
			log.warn("No s'han obtingut usuaris per codi parcial: " + filter);
		}
		return usernamesFound;
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
