/**
 * 
 */
package es.caib.ripea.service.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.AclClassEntity;
import es.caib.ripea.persistence.entity.AclEntryEntity;
import es.caib.ripea.persistence.entity.AclObjectIdentityEntity;
import es.caib.ripea.persistence.entity.AclSidEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.repository.AclClassRepository;
import es.caib.ripea.persistence.repository.AclEntryRepository;
import es.caib.ripea.persistence.repository.AclObjectIdentityRepository;
import es.caib.ripea.persistence.repository.AclSidRepository;
import es.caib.ripea.service.intf.dto.ActualitzacioInfo;
import es.caib.ripea.service.intf.dto.PermisDto;
import es.caib.ripea.service.intf.dto.PrincipalTipusEnumDto;
import es.caib.ripea.service.intf.dto.ProgresActualitzacioDto;
import es.caib.ripea.service.permission.ExtendedPermission;

/**
 * Helper per a la gestió de permisos dins les ACLs.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PermisosHelper {

	@Autowired
	private LookupStrategy lookupStrategy;
	@Autowired
	private MutableAclService aclService;
	@Autowired
	private AclSidRepository aclSidRepository;
	@Autowired
	private AclEntryRepository aclEntryRepository;
	@Autowired
	private AclClassRepository aclClassRepository;
	@Autowired
	private AclObjectIdentityRepository aclObjectIdentityRepository;
	@Autowired
	private MessageHelper messageHelper;
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private ConfigHelper configHelper;

	public void assignarPermisUsuari(
			String userName,
			Serializable objectIdentifier,
			Class<?> objectClass,
			Permission permission) {
		assignarPermisos(
				new PrincipalSid(userName),
				objectClass,
				objectIdentifier,
				new Permission[] { permission },
				false);
	}

	public void assignarPermisRol(String roleName, Serializable objectIdentifier, Class<?> objectClass, Permission permission) {
		assignarPermisos(
				new GrantedAuthoritySid(getMapeigRol(roleName)),
				objectClass,
				objectIdentifier,
				new Permission[] { permission },
				false);
	}

	public void revocarPermisUsuari(
			String userName,
			Serializable objectIdentifier,
			Class<?> objectClass,
			Permission permission) {
		revocarPermisos(new PrincipalSid(userName), objectClass, objectIdentifier, new Permission[] { permission });
	}

	public void revocarPermisRol(String roleName, Serializable objectIdentifier, Class<?> objectClass, Permission permission) {
		revocarPermisos(
				new GrantedAuthoritySid(getMapeigRol(roleName)),
				objectClass,
				objectIdentifier,
				new Permission[] { permission });
	}

	public void mourePermisUsuari(
			String sourceUserName,
			String targetUserName,
			Serializable objectIdentifier,
			Class<?> objectClass,
			Permission permission) {
		assignarPermisos(
				new PrincipalSid(targetUserName),
				objectClass,
				objectIdentifier,
				new Permission[] { permission },
				false);
		revocarPermisos(
				new PrincipalSid(sourceUserName),
				objectClass,
				objectIdentifier,
				new Permission[] { permission });
	}

	public void mourePermisRol(
			String sourceRoleName,
			String targetRoleName,
			Serializable objectIdentifier,
			Class<?> objectClass,
			Permission permission) {
		assignarPermisos(
				new GrantedAuthoritySid(getMapeigRol(targetRoleName)),
				objectClass,
				objectIdentifier,
				new Permission[] { permission },
				false);
		revocarPermisos(
				new GrantedAuthoritySid(getMapeigRol(sourceRoleName)),
				objectClass,
				objectIdentifier,
				new Permission[] { permission });
	}

	public void filterGrantedAny(
			Collection<?> objects,
			Class<?> clazz,
			Permission[] permissions) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		filterGrantedAny(
				objects,
				new ObjectIdentifierExtractor<AbstractPersistable<Serializable>>() {
					@Override
					public Serializable getObjectIdentifier(
							AbstractPersistable<Serializable> entitat) {
						return entitat.getId();
					}
				},
				clazz,
				permissions,
				auth);
	}

	/**
	 * Obté els identificadors de tots els objectes de la classe especificada sobre
	 * els quals l'usuari actual té permisos
	 * 
	 * @param clazz Classe dels objectes a consultar
	 * @param permission Permís que es vol esbrinar si conté
	 * @return Llista dels identificadors dels objectes seleccionats
	 */
	public List<Long> getObjectsIdsWithPermission(Class<?> clazz, Permission permission) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<AclSidEntity> sids = new ArrayList<AclSidEntity>();
		AclSidEntity userSid = aclSidRepository.getUserSid(auth.getName());
		if (userSid != null) {
			sids.add(userSid);
		}
		List<String> rolesNames = new ArrayList<String>();
		for (GrantedAuthority authority : auth.getAuthorities()) {
			rolesNames.add(authority.getAuthority());
		}
		for (AclSidEntity aclSid: aclSidRepository.findRolesSid(rolesNames)) {
			if (aclSid != null) {
				sids.add(aclSid);
			}
		}
		if (!sids.isEmpty()) {
			return aclObjectIdentityRepository.findObjectsWithPermissions(
					clazz.getName(),
					sids,
					permission.getMask());
		} else {
			return new ArrayList<Long>();
		}
	}
	
	/**
	 * Obté els identificadors de tots els objectes de la classe especificada sobre
	 * els quals l'usuari actual té tots dos permisos
	 * 
	 * @param clazz Classe dels objectes a consultar
	 * @param permission1 per comporvar
	 * @param permission2 per comporvar
	 * @return Llista dels identificadors dels objectes seleccionats
	 */
	public List<Long> getObjectsIdsWithTwoPermissions(Class<?> clazz, Permission permission1, Permission permission2) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<AclSidEntity> sids = new ArrayList<AclSidEntity>();
		AclSidEntity userSid = aclSidRepository.getUserSid(auth.getName());
		if (userSid != null) {
			sids.add(userSid);
		}
		List<String> rolesNames = new ArrayList<String>();
		for (GrantedAuthority authority : auth.getAuthorities()) {
			rolesNames.add(authority.getAuthority());
		}
		for (AclSidEntity aclSid: aclSidRepository.findRolesSid(rolesNames)) {
			if (aclSid != null) {
				sids.add(aclSid);
			}
		}
		if (!sids.isEmpty()) {
			return aclObjectIdentityRepository.findObjectsWithPermissions(
					clazz.getName(),
					sids,
					permission1.getMask(),
					permission2.getMask());
		} else {
			return new ArrayList<Long>();
		}
	}

	/**
	 * Filtre un llistat d'identificadors d'objectes amb els que tenen uns
	 * determinats permisos.
	 * 
	 * @param objects                   Conjunt d'objectes que volem filtrar. El
	 *                                  resultat del mètode és aquesta llista
	 *                                  modificada.
	 * @param objectIdentifierExtractor Implementació per extreure el identificador
	 *                                  dels objectes del parametre objects
	 * @param clazz                     Classe dels objectes a consular.
	 * @param permissions               Permisos que volem seleccionar
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void filterGrantedAny(
			Collection<?> objects,
			ObjectIdentifierExtractor objectIdentifierExtractor,
			Class<?> clazz,
			Permission[] permissions,
			Authentication auth) {
		Iterator<?> it = objects.iterator();
		while (it.hasNext()) {
			Serializable objectIdentifier = objectIdentifierExtractor.getObjectIdentifier(it.next());
			if (objectIdentifier == null) {
				it.remove();
			} else if (!isGrantedAny(objectIdentifier, clazz, permissions, auth))
				it.remove();
		}
	}
	
	public void filterGrantedAny(
			Collection<?> objects,
			Class<?> clazz,
			Permission[] permissions,
			String usuariCodi) {
		
		if (usuariCodi == null) {
			filterGrantedAny(
					objects,
					clazz,
					permissions);
		} else {
			filterGrantedAny(
					objects,
					new ObjectIdentifierExtractor<AbstractPersistable<Serializable>>() {
						@Override
						public Serializable getObjectIdentifier(AbstractPersistable<Serializable> entitat) {
							return entitat.getId();
						}
					},
					clazz,
					permissions,
					usuariCodi);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void filterGrantedAny(
			Collection<?> objects,
			ObjectIdentifierExtractor objectIdentifierExtractor,
			Class<?> clazz,
			Permission[] permissions,
			String usuariCodi) {
		Iterator<?> it = objects.iterator();
		while (it.hasNext()) {
			Serializable objectIdentifier = objectIdentifierExtractor.getObjectIdentifier(it.next());
			if (objectIdentifier == null) {
				it.remove();
			} else if (!isGrantedAny(objectIdentifier, clazz, permissions, usuariCodi))
				it.remove();
		}
	}

	/**
	 * Filtre un llistat d'identificadors d'objectes amb els que tenen uns
	 * determinats permisos.
	 * 
	 * @param objects                   Conjunt d'objectes que volem filtrar. El
	 *                                  resultat del mètode és aquesta llista
	 *                                  modificada.
	 * @param objectIdentifierExtractor Implementació per extreure el identificador
	 *                                  dels objectes del parametre objects
	 * @param clazz                     Classe dels objectes a consular.
	 * @param permissions               Permisos que volem seleccionar
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void filterGrantedAnyList(
			Collection<?> objects,
			ListObjectIdentifiersExtractor objectIdentifierExtractor,
			Class<?> clazz,
			Permission[] permissions,
			Authentication auth) {
		Iterator<?> it = objects.iterator();
		while (it.hasNext()) {
			List<Serializable> objectIdentifiers = objectIdentifierExtractor.getObjectIdentifiers(it.next());
			boolean hasPermission = false;
			for (Serializable id :objectIdentifiers) {
				if (isGrantedAny(id, clazz, permissions, auth)) {
					hasPermission = true;
					break;
				}
			}
			if (!hasPermission) {
				it.remove();
			}

		}
	}

	/**
	 * Filtre un llistat d'identificadors d'objectes amb els que tenen qualsevol
	 * dels permisos especificats.
	 * 
	 * @param ids         Conjunt d'objectes que volem filtrar. El resultat del
	 *                    mètode és aquesta llista modificada.
	 * @param clazz       Classe dels objectes a consular.
	 * @param permissions Permisos que volem filtrar
	 * @param auth        Autentificació
	 */
	public void filterGrantedAny(Collection<Serializable> ids, Class<?> clazz, Permission[] permissions, Authentication auth) {
		Iterator<Serializable> it = ids.iterator();
		while (it.hasNext()) {
			Serializable objectIdentifier = it.next();
			if (!isGrantedAny(objectIdentifier, clazz, permissions, auth))
				it.remove();
		}
	}

	public boolean isGrantedAny(Serializable objectIdentifier, Class<?> clazz, Permission[] permissions, Authentication auth) {
		boolean[] granted = verificarPermisos(objectIdentifier, clazz, permissions, auth);
		for (int i = 0; i < granted.length; i++) {
			if (granted[i])
				return true;
		}
		return false;
	}
	
	public boolean isGrantedAny(Serializable objectIdentifier, Class<?> clazz, Permission[] permissions, String usuariCodi) {
		boolean[] granted = verificarPermisos(objectIdentifier, clazz, permissions, usuariCodi);
		for (int i = 0; i < granted.length; i++) {
			if (granted[i])
				return true;
		}
		return false;
	}
	
	

	/**
	 * Filtre un llistat d'identificadors d'objectes amb els que tenen tots els
	 * permisos especificats.
	 * 
	 * @param ids         Conjunt d'objectes que volem filtrar. El resultat del
	 *                    mètode és aquesta llista modificada.
	 * @param clazz       Classe dels objectes a consular.
	 * @param permissions Permisos que volem filtrar
	 * @param auth        Autentificació
	 */
	public void filterGrantedAll(
			Collection<? extends Serializable> ids,
			Class<?> clazz,
			Permission[] permissions,
			Authentication auth) {
		Iterator<? extends Serializable> it = ids.iterator();
		while (it.hasNext()) {
			Serializable objectIdentifier = it.next();
			if (!isGrantedAll(objectIdentifier, clazz, permissions, auth))
				it.remove();
		}
	}

	public void filterGrantedAll(
			Collection<? extends Persistable<? extends Serializable>> objects,
			Class<?> clazz,
			Permission[] permissions) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		filterGrantedAll(objects, new ObjectIdentifierExtractor<AbstractPersistable<Serializable>>() {
			@Override
			public Serializable getObjectIdentifier(AbstractPersistable<Serializable> entitat) {
				return entitat.getId();
			}
		}, clazz, permissions, auth);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void filterGrantedAll(
			Collection<?> objects,
			ObjectIdentifierExtractor objectIdentifierExtractor,
			Class<?> clazz,
			Permission[] permissions,
			Authentication auth) {
		Iterator<?> it = objects.iterator();
		while (it.hasNext()) {
			Serializable objectIdentifier = objectIdentifierExtractor.getObjectIdentifier(it.next());
			if (!isGrantedAll(objectIdentifier, clazz, permissions, auth))
				it.remove();
		}
	}
	
	public void filterGrantedAll(
			Collection<? extends AbstractPersistable<? extends Serializable>> objects,
			Class<?> clazz,
			Permission[] permissions,
			String usuariCodi) {
		filterGrantedAll(objects, new ObjectIdentifierExtractor<AbstractPersistable<Serializable>>() {
			@Override
			public Serializable getObjectIdentifier(AbstractPersistable<Serializable> entitat) {
				return entitat.getId();
			}
		}, clazz, permissions, usuariCodi);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void filterGrantedAll(
			Collection<?> objects,
			ObjectIdentifierExtractor objectIdentifierExtractor,
			Class<?> clazz,
			Permission[] permissions,
			String usuriCodi) {
		Iterator<?> it = objects.iterator();
		while (it.hasNext()) {
			Serializable objectIdentifier = objectIdentifierExtractor.getObjectIdentifier(it.next());
			if (!isGrantedAll(objectIdentifier, clazz, permissions, usuriCodi))
				it.remove();
		}
	}
	public boolean isGrantedAll(Serializable objectIdentifier, Class<?> clazz, Permission[] permissions) {
		return isGrantedAll(objectIdentifier, clazz, permissions, SecurityContextHolder.getContext().getAuthentication());
	}

	public boolean isGrantedAll(Serializable objectIdentifier, Class<?> clazz, Permission[] permissions, Authentication auth) {
		boolean[] granted = verificarPermisos(objectIdentifier, clazz, permissions, auth);
		boolean result = true;
		for (int i = 0; i < granted.length; i++) {
			if (!granted[i]) {
				result = false;
				break;
			}
		}
		return result;
	}
	
	public boolean isGrantedAll(Serializable objectIdentifier, Class<?> clazz, Permission[] permissions, String usuariCodi) {
		boolean[] granted = verificarPermisos(objectIdentifier, clazz, permissions, usuariCodi);
		boolean result = true;
		for (int i = 0; i < granted.length; i++) {
			if (!granted[i]) {
				result = false;
				break;
			}
		}
		return result;
	}

	public List<PermisDto> findPermisos(Serializable objectIdentifier, Class<?> objectClass) {
		Acl acl = null;
		try {
			ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
			acl = aclService.readAclById(oid);
		} catch (NotFoundException nfex) {
			return new ArrayList<PermisDto>();
		}
		return findPermisosPerAcl(acl);
	}

	/**
	 * Obté tots els permisos d'un conjunt d'objectes.
	 * 
	 * @param objectIdentifiers Conjunt d'objectes que volem consultar.
	 * @param objectClass       Classe dels objectes a consular.
	 * @return Mapa amb tots els permisos de cada objecte del conjunt consultat.
	 */
	public Map<Serializable, List<PermisDto>> findPermisos(List<Serializable> objectIdentifiers, Class<?> objectClass) {
		try {
			Map<Serializable, List<PermisDto>> resposta = new HashMap<Serializable, List<PermisDto>>();
			List<ObjectIdentity> oids = new ArrayList<ObjectIdentity>();
			for (Serializable objectIdentifier: objectIdentifiers) {
				ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
				oids.add(oid);
			}
			if (!oids.isEmpty()) {
				Map<ObjectIdentity, Acl> acls = lookupStrategy.readAclsById(oids, null);
				for (ObjectIdentity oid: acls.keySet()) {
					resposta.put(oid.getIdentifier(), findPermisosPerAcl(acls.get(oid)));
				}
			}
			return resposta;
		} catch (NotFoundException nfex) {
			return new HashMap<Serializable, List<PermisDto>>();
		}
	}

	public void updatePermis(Serializable objectIdentifier, Class<?> objectClass, PermisDto permis) {
		if (PrincipalTipusEnumDto.USUARI.equals(permis.getPrincipalTipus())) {
			assignarPermisos(
					new PrincipalSid(permis.getPrincipalNom()),
					objectClass,
					objectIdentifier,
					getPermissionsFromPermis(permis),
					true);
		} else if (PrincipalTipusEnumDto.ROL.equals(permis.getPrincipalTipus())) {
			assignarPermisos(
					new GrantedAuthoritySid(getMapeigRol(permis.getPrincipalNom())),
					objectClass,
					objectIdentifier,
					getPermissionsFromPermis(permis),
					true);
		}
	}

	public void deletePermis(Serializable objectIdentifier, Class<?> objectClass, Serializable permisId) {
		try {
			ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
			Acl acl = aclService.readAclById(oid);
			for (AccessControlEntry ace : acl.getEntries()) {
				if (permisId.equals(ace.getId())) {
					assignarPermisos(ace.getSid(), objectClass, objectIdentifier, new Permission[] {}, true);
				}
			}
		} catch (NotFoundException nfex) {
		}
	}

	public void deleteAcl(Serializable objectIdentifier, Class<?> objectClass) {
		try {
			ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
			aclService.deleteAcl(oid, true);
		} catch (NotFoundException nfex) {
		}
	}

	private List<PermisDto> findPermisosPerAcl(Acl acl) {
		List<PermisDto> resposta = new ArrayList<PermisDto>();
		if (acl != null) {
			Map<String, PermisDto> permisosUsuari = new HashMap<String, PermisDto>();
			Map<String, PermisDto> permisosRol = new HashMap<String, PermisDto>();
			for (AccessControlEntry ace : acl.getEntries()) {
				PermisDto permis = null;
				if (ace.getSid() instanceof PrincipalSid) {
					String principal = ((PrincipalSid)ace.getSid()).getPrincipal();
					permis = permisosUsuari.get(principal);
					if (permis == null) {
						permis = new PermisDto();
						permis.setId((Long)ace.getId());
						permis.setPrincipalNom(principal);
						permis.setPrincipalTipus(PrincipalTipusEnumDto.USUARI);
						permisosUsuari.put(principal, permis);
					}
				} else if (ace.getSid() instanceof GrantedAuthoritySid) {
					String grantedAuthority = ((GrantedAuthoritySid)ace.getSid()).getGrantedAuthority();
					permis = permisosRol.get(grantedAuthority);
					if (permis == null) {
						permis = new PermisDto();
						permis.setId((Long)ace.getId());
						permis.setPrincipalNom(grantedAuthority);
						permis.setPrincipalTipus(PrincipalTipusEnumDto.ROL);
						permisosRol.put(grantedAuthority, permis);
					}
				}
				if (permis != null) {
					if (ExtendedPermission.READ.equals(ace.getPermission()))
						permis.setRead(true);
					if (ExtendedPermission.WRITE.equals(ace.getPermission()))
						permis.setWrite(true);
					if (ExtendedPermission.CREATE.equals(ace.getPermission()))
						permis.setCreate(true);
					if (ExtendedPermission.DELETE.equals(ace.getPermission()))
						permis.setDelete(true);
					if (ExtendedPermission.ADMINISTRATION.equals(ace.getPermission()))
						permis.setAdministration(true);
					if (ExtendedPermission.STATISTICS.equals(ace.getPermission()))
						permis.setStatistics(true);
					if (ExtendedPermission.COMU.equals(ace.getPermission()))
						permis.setProcedimentsComuns(true);
					if (ExtendedPermission.ADM_COMU.equals(ace.getPermission()))
						permis.setAdministrationComuns(true);
					if (ExtendedPermission.DISSENY.equals(ace.getPermission()))
						permis.setDisseny(true);
				}
			}
			resposta.addAll(permisosUsuari.values());
			resposta.addAll(permisosRol.values());
		}
		return resposta;
	}

	private void assignarPermisos(
			Sid sid,
			Class<?> objectClass,
			Serializable objectIdentifier,
			Permission[] permissions,
			boolean netejarAbans) {
		ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
		MutableAcl acl = null;
		try {
			acl = (MutableAcl)aclService.readAclById(oid);
		} catch (NotFoundException nfex) {
			acl = aclService.createAcl(oid);
		}
		if (netejarAbans) {
			// Es recorren girats perque cada vegada que s'esborra un ace
			// es reorganitzen els índexos
			for (int i = acl.getEntries().size() - 1; i >= 0; i--) {
				AccessControlEntry ace = acl.getEntries().get(i);
				if (ace.getSid().equals(sid))
					acl.deleteAce(i);
			}
		}
		aclService.updateAcl(acl);
		for (Permission permission : permissions) {
			acl.insertAce(acl.getEntries().size(), permission, sid, true);
		}
		aclService.updateAcl(acl);
		cacheHelper.evictReadAclById(oid);
	}

	private void revocarPermisos(
			Sid sid,
			Class<?> objectClass,
			Serializable objectIdentifier,
			Permission[] permissions) throws NotFoundException {
		ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
		try {
			MutableAcl acl = (MutableAcl)aclService.readAclById(oid);
			List<Integer> indexosPerEsborrar = new ArrayList<Integer>();
			int aceIndex = 0;
			for (AccessControlEntry ace : acl.getEntries()) {
				if (ace.getSid().equals(sid)) {
					for (Permission p : permissions) {
						if (p.equals(ace.getPermission()))
							indexosPerEsborrar.add(aceIndex);
					}
				}
				aceIndex++;
			}
			for (Integer index : indexosPerEsborrar)
				acl.deleteAce(index);
			aclService.updateAcl(acl);
			cacheHelper.evictReadAclById(oid);
		} catch (NotFoundException nfex) {
			// Si no troba l'ACL no fa res
		}
	}

	private boolean[] verificarPermisos(
			Serializable objectIdentifier,
			Class<?> clazz,
			Permission[] permissions,
			String usuariCodi) {
		List<Sid> sids = new ArrayList<Sid>();
		sids.add(new PrincipalSid(usuariCodi));
		List<String> rolsUsuariActual = cacheHelper.findRolsAmbCodi(usuariCodi);
		for (String rol : rolsUsuariActual) {
			Sid sid = new GrantedAuthoritySid(getMapeigRol(rol));
			sids.add(sid);
		}
		return verificarPermisos(
				objectIdentifier,
				clazz,
				permissions,
				sids);
	}

	private boolean[] verificarPermisos(
			Serializable objectIdentifier,
			Class<?> clazz,
			Permission[] permissions,
			List<Sid> sids) {
		boolean[] granted = new boolean[permissions.length];
		for (int i = 0; i < permissions.length; i++)
			granted[i] = false;
			ObjectIdentity oid = new ObjectIdentityImpl(clazz, objectIdentifier);
			Acl acl = cacheHelper.readAclById(oid);
		if (acl != null) {
			List<Permission> ps = new ArrayList<Permission>();
			for (int i = 0; i < permissions.length; i++) {
				try {
					ps.add(permissions[i]);
					granted[i] = acl.isGranted(ps, sids, false);
					ps.clear();
				} catch (NotFoundException ex) {
				}
			}
		}


		return granted;
	}

	private boolean[] verificarPermisos(
			Serializable objectIdentifier,
			Class<?> clazz,
			Permission[] permissions,
			Authentication auth) {
		List<Sid> sids = getAuthSids(auth);
		return verificarPermisos(
				objectIdentifier,
				clazz,
				permissions,
				sids);
	}

	private List<Sid> getAuthSids(Authentication auth) {
		List<Sid> sids = new ArrayList<Sid>();
		sids.add(new PrincipalSid(auth.getName()));
		for (GrantedAuthority ga : auth.getAuthorities())
			sids.add(new GrantedAuthoritySid(ga.getAuthority()));
		return sids;
	}
	
	private Permission[] getPermissionsFromPermis(PermisDto permis) {
		List<Permission> permissions = new ArrayList<Permission>();
		if (permis.isRead())
			permissions.add(ExtendedPermission.READ);
		if (permis.isWrite())
			permissions.add(ExtendedPermission.WRITE);
		if (permis.isCreate())
			permissions.add(ExtendedPermission.CREATE);
		if (permis.isDelete())
			permissions.add(ExtendedPermission.DELETE);
		if (permis.isAdministration())
			permissions.add(ExtendedPermission.ADMINISTRATION);
		if (permis.isStatistics())
			permissions.add(ExtendedPermission.STATISTICS);
		if (permis.isProcedimentsComuns())
			permissions.add(ExtendedPermission.COMU);
		if (permis.isAdministrationComuns())
			permissions.add(ExtendedPermission.ADM_COMU);
		if (permis.isDisseny())
			permissions.add(ExtendedPermission.DISSENY);		
		return permissions.toArray(new Permission[permissions.size()]);
	}

	private String getMapeigRol(String rol) {
		String propertyMapeig = configHelper.getEnvironmentProperty("es.caib.ripea.mapeig.rol." + rol, null);
		if (propertyMapeig != null)
			return propertyMapeig;
		else
			return rol;
	}

    public void actualitzarPermisosOrgansObsolets(
    		List<OrganGestorEntity> obsoleteUnitats,
			List<OrganGestorEntity> organsDividits,
			List<OrganGestorEntity> organsFusionats,
			List<OrganGestorEntity> organsSubstituits,
			ProgresActualitzacioDto progres) {

		List<String> organsFusionatsProcessats = new ArrayList<>();

		int nombreUnitatsTotal = obsoleteUnitats.size();
		int nombreUnitatsProcessades = 0;

		OrganGestorEntity organOrigen = null;
		for (OrganGestorEntity unitat: obsoleteUnitats) {

			if (hasAnyPermissions(unitat)) {
				progres.addInfo(ActualitzacioInfo.builder().hasInfo(true).infoTitol(msg("unitat.synchronize.titol.permis")).infoText(msg("unitat.synchronize.info.permisos.traspas", unitat.getCodi())).build());
			} 
//			else {
//				progres.addInfo(ActualitzacioInfo.builder().hasInfo(true).infoTitol(msg("unitat.synchronize.titol.permis")).infoText(msg("unitat.synchronize.info.permisos.no", unitat.getCodi())).build());
//			}
			progres.setProgres(51 + (nombreUnitatsProcessades++ * 24)/nombreUnitatsTotal);

			organOrigen = getOrgan(organsDividits, unitat.getCodi());
			if (organOrigen != null) {
				continue;
			}

			organOrigen = getOrgan(organsFusionats, unitat.getCodi());
			if (organOrigen != null && !organsFusionatsProcessats.contains(organOrigen.getCodi())) {
				OrganGestorEntity organDesti = organOrigen.getNous().get(0);
				List<OrganGestorEntity> organsOrigen = organDesti.getAntics();
				for(OrganGestorEntity origen: organsOrigen) {
					organsFusionatsProcessats.add(origen.getCodi());
					duplicaPermisos(origen, organDesti);
				}
				
				continue;
			}

			organOrigen = getOrgan(organsSubstituits, unitat.getCodi());
			if (organOrigen != null) {
				OrganGestorEntity organDesti = organOrigen.getNous().get(0);
				duplicaPermisos(organOrigen, organDesti);
			}
		}
    }

	private OrganGestorEntity getOrgan(List<OrganGestorEntity> llista, String codi) {
		for (OrganGestorEntity organ: llista) {
			if (organ.getCodi().equals(codi))
				return organ;
		}
		return null;
	}
	
	
	private boolean hasAnyPermissions(OrganGestorEntity organOrigen) {
		Acl acl = null;
		try {
			ObjectIdentity oid = new ObjectIdentityImpl(OrganGestorEntity.class, organOrigen.getId());
			acl = aclService.readAclById(oid);
		} catch (NotFoundException nfex) {
		}
		if (acl != null && acl.getEntries() != null && !acl.getEntries().isEmpty()) {
			return true;
		} else {
			return false;
		}
	}


	private void duplicaPermisos(OrganGestorEntity organOrigen, OrganGestorEntity organDesti) {

		Acl acl = null;
		try {
			ObjectIdentity oid = new ObjectIdentityImpl(OrganGestorEntity.class, organOrigen.getId());
			acl = aclService.readAclById(oid);
		} catch (NotFoundException nfex) {
		}
		if (acl != null) {
			for (AccessControlEntry ace : acl.getEntries()) {
				assignarPermisos(
						ace.getSid(),
						OrganGestorEntity.class,
						organDesti.getId(),
						new Permission[] { ace.getPermission() },
						false);
				}
			}
		
	}

	public void eliminarPermisosOrgan(OrganGestorEntity organGestor) {
		AclClassEntity classname = aclClassRepository.findByClassname("es.caib.ripea.core.entity.OrganGestorEntity");
		AclObjectIdentityEntity objectIdentity = aclObjectIdentityRepository.findByClassnameAndObjectId(classname, organGestor.getId());
		List<AclEntryEntity> permisos = aclEntryRepository.findByAclObjectIdentity(objectIdentity);
		aclEntryRepository.deleteInBatch(permisos);
	}

	public Set<PermisDto> findPermisosObjectes(List<Long> objectsId, Permission[] permissions, Permission[] permissions2) {
		List<PermisDto> permisos = new ArrayList<PermisDto>();
		List<Integer> masks = new ArrayList<>();
		List<Integer> masks2 = new ArrayList<>();
		
		for (Permission permission : permissions) {
            masks.add(permission.getMask());
        }
		
		List<AclObjectIdentityEntity> objectsIdentityAmbPermis = new ArrayList<AclObjectIdentityEntity>();
		
		if (permissions2 == null) {
			objectsIdentityAmbPermis = aclObjectIdentityRepository.findByAclObjectIdentityInAndMaskIn(objectsId, masks);
		} else {
			for (Permission permission : permissions2) {
				masks2.add(permission.getMask());
	        }
			objectsIdentityAmbPermis = aclObjectIdentityRepository.findByAclObjectIdentityInAndMaskIn(objectsId, masks, masks2);
		}
		
		for (AclObjectIdentityEntity objectIdentity : objectsIdentityAmbPermis) {
			// Obté els permisos sobre objecte actual
			for (AclEntryEntity aclEntryEntity : objectIdentity.getEntries()) {
				PermisDto permis = new PermisDto();
				permis.setId(aclEntryEntity.getSid().getId());
				permis.setPrincipalNom(aclEntryEntity.getSid().getSid());
				permisos.add(permis);
			}
		}
		
		// Set per eliminar duplicats
		return new HashSet<PermisDto>(permisos);
	}
	
	public interface ObjectIdentifierExtractor<T> {

		public Serializable getObjectIdentifier(T object);

	}

	public interface ListObjectIdentifiersExtractor<T> {

		public List<Serializable> getObjectIdentifiers(T object);

	}

	private String msg(String codi) {
		return messageHelper.getMessage(codi);
	}
	private String msg(String codi, Object... params) {
		return messageHelper.getMessage(codi, params);
	}

}
