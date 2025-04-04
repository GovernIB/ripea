package es.caib.ripea.service.resourcehelper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.GrupEntity;
import es.caib.ripea.persistence.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.persistence.entity.MetaNodeEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.entity.resourceentity.ContingutResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.DocumentResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientOrganGestorResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.OrganGestorResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.MetaExpedientOrganGestorResourceRepository;
import es.caib.ripea.service.helper.PermisosHelper;
import es.caib.ripea.service.intf.exception.ValidationException;
import es.caib.ripea.service.permission.ExtendedPermission;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PermisosResourceHelper {

	private final MetaExpedientOrganGestorResourceRepository metaExpedientOrganGestorResourceRepository;
	private final PermisosHelper permisosHelper;
	
	/**
	 * Per tenir permis sobre un document, has de tenir permis sobre l'expedient del document. 
	 */
	public boolean comprovarPermisDocument(
			DocumentResourceEntity documentResourceEntity,
			Permission permission,
			boolean comprovarAgafatPerUsuariActual) {
		//Obtenció del expedient del document
		ExpedientResourceEntity expedientPare = documentResourceEntity.getExpedient();
		if (expedientPare==null) {
			expedientPare = getExpedientPareTop(documentResourceEntity.getPare());
		}
		return comprovarPermisExpedient(expedientPare, permission, comprovarAgafatPerUsuariActual);
	}
	
	/**
	 * Comprova permisos sobre 
	 * @param expedientResourceEntity
	 * @param permissions
	 * @param comprovarAgafatPerUsuariActual
	 * @return
	 */
	public boolean comprovarPermisExpedient(
			ExpedientResourceEntity expedientResourceEntity,
			Permission permission,
			boolean comprovarAgafatPerUsuariActual) {
		
		//Validacions inicials
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (auth==null) 
			throw new ValidationException(expedientResourceEntity.getId(), ExpedientResourceEntity.class, "No hi ha cap usuari autenticat.");
		if (permission==null)
			throw new ValidationException(expedientResourceEntity.getId(), ExpedientResourceEntity.class, "No s'ha indicat el permís a comprovar.");
		if (expedientResourceEntity==null)
			throw new ValidationException(null, ExpedientResourceEntity.class, "No s'ha indicat cap expedient per comprovar.");
		
		//Si ets administrador de Entitat ROL + PERMIS, no es necessari mirar res mes.
		boolean isAdministradorEntitat = userIsAdminEntitat(expedientResourceEntity, null);
		if (isAdministradorEntitat) return true;
		
		//Per optimitzar, es necessita tenir en una variable, per ara i per despres, la llista de organs a comprovar.
		List<OrganGestorResourceEntity> organsGestors = findOrgansGestorsPares(expedientResourceEntity.getOrganGestor(), true);
		//Si ets administrador de Organ ROL + PERMIS, no es necessari mirar res mes.
		boolean isAdministradorOrgan = userIsAdminOrgan(organsGestors, null, auth);
		if (isAdministradorOrgan) return true;
		
		//Si nomes estam comprovant permisos de lectura, no fa falta comprovar si tens l'expedient agafat.
		if (!ExtendedPermission.READ.equals(permission)  && comprovarAgafatPerUsuariActual) {
			if (expedientResourceEntity==null || expedientResourceEntity.getAgafatPer()==null || expedientResourceEntity.getAgafatPer().getCodi().equals(auth.getName())) {
				throw new ValidationException(expedientResourceEntity.getId(), DocumentResourceEntity.class, "No es té agafat l'expedient.");
			}
		}
		
		//No ets administrador de la entitat, pero al menos es te algun permis sobre ella
		boolean permisEntitat = permisosHelper.isGrantedAny(
				expedientResourceEntity.getEntitat().getId(),
				EntitatEntity.class,
				new Permission[] { ExtendedPermission.READ, ExtendedPermission.ADMINISTRATION },
				auth);
		
		if (!permisEntitat) {
			throw new ValidationException(expedientResourceEntity.getId(), DocumentResourceEntity.class, "No es té cap permís sobre la entitat.");
		}
		
		//Finalment miram si es té permis sobre l'expedient, això es, revisar si es té permis sobre el organ gestor o sobre el procediment.
		return comprovarPermisExpedient(expedientResourceEntity, organsGestors, permission, auth);
	}
	
	private boolean comprovarPermisExpedient(
			ExpedientResourceEntity expedientResourceEntity,
			List<OrganGestorResourceEntity> organsGestors,
			Permission permis,
			Authentication auth) {
		
		boolean grantedProcediment = permisosHelper.isGrantedAll(
				expedientResourceEntity.getMetaExpedient().getId(),
				MetaNodeEntity.class,
				new Permission[] { permis });

		//Per procediments comuns, els permisos ACL s'assignen sobre MetaExpedientOrganGestorEntity i no sobre OrganGestorEntity
		boolean userIsPermisProcedimentOrgan = userIsPermisProcedimentOrgan(
				expedientResourceEntity.getMetaExpedient(),
				organsGestors, //Es comprova el permis per tots els MetaExpedientOrganGestorEntity del organ actual + pares.
				auth,
				permis);
		
		boolean requereixPermisDirecte = expedientResourceEntity.getMetaExpedient().isPermisDirecte();
		//Comprobació de si requereix permis directe sobre el procediment. Ja sigui directament o a travers de un organ gestor (comuns).
		if (requereixPermisDirecte && !grantedProcediment && !userIsPermisProcedimentOrgan) {
			throw new ValidationException(expedientResourceEntity.getId(), DocumentResourceEntity.class, "El expedient requereix permis directe sobre el procediment.");
		}

		//Abans hem mirat si es tenia permis de administracio sobre els organs, en tal cas ja haurem retornat true.
		//Ara mirarem si es té el permís especificat.
		boolean userIsPermisOrgan = userIsPermisOrgan(organsGestors, auth, permis);
		//Si es tenen permisos sobre el mateix procediment o sobre el seu organ gestor, s'accepta.
		if (userIsPermisOrgan || userIsPermisProcedimentOrgan || grantedProcediment) {
			return true;
		}
		
		//Comprovam si té permisos sobre prodeciments comuns
		if (expedientResourceEntity.getMetaExpedient().isComu()) {
			//Visibilitat de expedients comuns per algun dels organs gestors actual o ascendent
			boolean userIsPermisOrganComuns = userIsPermisOrgan(organsGestors, auth, ExtendedPermission.COMU);
			//A part del permis de comuns, ha de tenir el permís que s'esta solicitant
			if (!userIsPermisOrgan && !userIsPermisOrganComuns) {
				throw new ValidationException(expedientResourceEntity.getId(), DocumentResourceEntity.class, "El procediment es comú, es requereix permis Procediments comuns al organ a part del permís requerit ("+permis.toString()+").");
			}
		}
		
		//De moment s'han superat les validacions
		//Comprovam si té permisos sobre l'expedient a traves de algun grup.
		if (expedientResourceEntity.getMetaExpedient().isGestioAmbGrupsActiva() && expedientResourceEntity.getGrup()!=null) {
			return permisosHelper.isGrantedAll(
					expedientResourceEntity.getGrup().getId(),
					GrupEntity.class,
					new Permission[] { permis });
		}
		
		return false;
	}
	
	@SuppressWarnings("rawtypes")
	private ExpedientResourceEntity getExpedientPareTop(ContingutResourceEntity contingut) {
		if (contingut.getPare()!=null) {
			while (contingut.getPare() != null) {
				contingut = contingut.getPare();
			}
		}		
		if (contingut instanceof ExpedientResourceEntity) 
			return (ExpedientResourceEntity)contingut;		
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	private boolean userIsAdminEntitat(ExpedientResourceEntity expedientPare, String rolActual) {
		boolean admin = false;
		if (rolActual != null) {
			if (rolActual.equals("IPA_ADMIN")) {
				admin = permisosHelper.isGrantedAll(
						expedientPare.getEntitat().getId(),
						EntitatEntity.class,
						new Permission[] { ExtendedPermission.ADMINISTRATION },
						SecurityContextHolder.getContext().getAuthentication());
			}
		}
		return admin;
	}
	
	@SuppressWarnings("rawtypes")
	private boolean userIsAdminOrgan(List<OrganGestorResourceEntity> organsGestors, String rolActual, Authentication auth) {
		if (rolActual != null) {
			if (rolActual.equals("IPA_ORGAN_ADMIN")) {
				for (OrganGestorResourceEntity organGestorEntity: organsGestors) {
					if (permisosHelper.isGrantedAll(
							organGestorEntity.getId(),
							OrganGestorEntity.class,
							new Permission[] { ExtendedPermission.ADMINISTRATION },
							auth)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private boolean userIsPermisOrgan(List<OrganGestorResourceEntity> organsGestors, Authentication auth, Permission permis) {
		for (OrganGestorResourceEntity organGestorEntity: organsGestors) {
			if (permisosHelper.isGrantedAll(
					organGestorEntity.getId(),
					OrganGestorEntity.class,
					new Permission[] { permis },
					auth)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean userIsPermisProcedimentOrgan(
			MetaExpedientResourceEntity procediment,
			List<OrganGestorResourceEntity> organsGestors,
			Authentication auth,
			Permission permis) {
		for (OrganGestorResourceEntity organGestorEntity: organsGestors) {
			MetaExpedientOrganGestorResourceEntity meogrr = metaExpedientOrganGestorResourceRepository.findByMetaExpedientAndOrganGestor(
					procediment,
					organGestorEntity).get();
			if (permisosHelper.isGrantedAll(
					meogrr.getId(),
					MetaExpedientOrganGestorEntity.class,
					new Permission[] { permis },
					auth)) {
				return true;
			}
		}
		return false;
	}
	
	private List<OrganGestorResourceEntity> findOrgansGestorsPares(OrganGestorResourceEntity organGestor, boolean incloureOrganActual) {
		List<OrganGestorResourceEntity> pares = new ArrayList<OrganGestorResourceEntity>();
		if (organGestor!=null) {
			if (incloureOrganActual) { pares.add(organGestor); }
			OrganGestorResourceEntity organGestorActual = organGestor;
			while (organGestorActual.getPare() != null) {
				organGestorActual = organGestorActual.getPare();
				pares.add(organGestorActual);
			}
		}
		return pares;
	}
}