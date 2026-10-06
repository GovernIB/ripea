package es.caib.ripea.service.resourcehelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.AclSidEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.GrupEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.persistence.entity.MetaNodeEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.repository.AclEntryRepository;
import es.caib.ripea.persistence.repository.AclEntryRepository.AclEntrySidProjection;
import es.caib.ripea.persistence.repository.AclSidRepository;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.persistence.repository.GrupRepository;
import es.caib.ripea.persistence.repository.MetaExpedientOrganGestorRepository;
import es.caib.ripea.persistence.repository.MetaNodeRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.service.helper.MessageHelper;
import es.caib.ripea.service.helper.PermisosHelper;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.ExtendedPermissionEnum;
import es.caib.ripea.service.intf.dto.PermisDto;
import es.caib.ripea.service.intf.dto.PrincipalTipusEnumDto;
import es.caib.ripea.service.intf.model.AclSidResource.ClassType;
import es.caib.ripea.service.intf.model.UsuariResource.PermisDetall;
import es.caib.ripea.service.intf.model.UsuariResource.PermisosEntitatResum;
import es.caib.ripea.service.intf.model.UsuariResource.PermisosResum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Visor de permisos d'un usuari (manteniment d'usuaris del superusuari).
 *
 * Mostra els permisos ACL tal com estan desats, atorgats directament a l'usuari o a algun dels seus
 * rols. No calcula permisos efectius (herència d'òrgans, procediments comuns, etc.).
 *
 * L'entitat sempre arriba per paràmetre: el superusuari no treballa amb entitat actual.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UsuariPermisosResourceHelper {

	/** Límit d'elements d'una clàusula IN a Oracle. */
	private static final int MIDA_LOT_IN = 1000;

	private final AclSidRepository aclSidRepository;
	private final AclEntryRepository aclEntryRepository;
	private final EntitatRepository entitatRepository;
	private final OrganGestorRepository organGestorRepository;
	private final GrupRepository grupRepository;
	private final MetaNodeRepository metaNodeRepository;
	private final MetaExpedientOrganGestorRepository metaExpedientOrganGestorRepository;
	private final PermisosHelper permisosHelper;
	private final MessageHelper messageHelper;

	public PermisosResum getPermisosResum(String usuariCodi) {
		SidsUsuari sidsUsuari = findSidsUsuari(usuariCodi);
		List<PermisObjecte> permisos = findPermisos(sidsUsuari);

		Map<Long, PermisosEntitatResum> entitats = new HashMap<>();
		int numOrfes = 0;
		for (PermisObjecte permis : permisos) {
			if (permis.isOrfe()) {
				numOrfes++;
				continue;
			}
			PermisosEntitatResum entitatResum = entitats.computeIfAbsent(permis.getEntitatId(), id -> {
				PermisosEntitatResum resum = new PermisosEntitatResum();
				resum.setEntitatId(id);
				return resum;
			});
			if (ClassType.ENTITY.equals(permis.getTipus())) {
				entitatResum.setAdministrador(entitatResum.isAdministrador() || permis.getPermisos().contains(ExtendedPermissionEnum.ADMINISTRATION));
				entitatResum.setAdministradorLectura(entitatResum.isAdministradorLectura() || permis.getPermisos().contains(ExtendedPermissionEnum.ADMINISTRATION_READ));
				entitatResum.setUsuari(entitatResum.isUsuari() || permis.getPermisos().contains(ExtendedPermissionEnum.READ));
			} else {
				entitatResum.setNumPermisos(entitatResum.getNumPermisos() + 1);
			}
		}
		// Les entitats on només hi ha permisos sobre objectes no s'han carregat encara
		Map<Long, EntitatEntity> entitatsEntity = findAllByIdEnLots(entitatRepository, entitats.keySet());
		for (PermisosEntitatResum entitatResum : entitats.values()) {
			EntitatEntity entitat = entitatsEntity.get(entitatResum.getEntitatId());
			if (entitat != null) {
				entitatResum.setEntitatCodi(entitat.getCodi());
				entitatResum.setEntitatNom(entitat.getNom());
			}
		}

		PermisosResum resum = new PermisosResum();
		resum.setRols(new ArrayList<>(sidsUsuari.getRolsVisibles()));
		resum.setRolsError(sidsUsuari.isRolsError());
		resum.setEntitats(entitats.values().stream()
				.sorted(Comparator.comparing(PermisosEntitatResum::getEntitatNom, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
				.collect(Collectors.toList()));
		resum.setNumOrfes(numOrfes);
		return resum;
	}

	/**
	 * Permisos de l'usuari sobre una entitat i els seus objectes o, si {@code orfes}, sobre
	 * objectes que ja no existeixen.
	 */
	public List<PermisDetall> getPermisosDetall(String usuariCodi, Long entitatId, boolean orfes) {
		SidsUsuari sidsUsuari = findSidsUsuari(usuariCodi);
		return findPermisos(sidsUsuari).stream()
				.filter(p -> orfes ? p.isOrfe() : (!p.isOrfe() && Objects.equals(entitatId, p.getEntitatId())))
				.sorted(Comparator
						.comparing((PermisObjecte p) -> p.getTipus().ordinal())
						.thenComparing(p -> p.getObjecteNom() != null ? p.getObjecteNom() : "", String.CASE_INSENSITIVE_ORDER)
						.thenComparing(p -> p.getOrgan() != null ? p.getOrgan() : "", String.CASE_INSENSITIVE_ORDER)
						.thenComparing(p -> p.getPrincipal().ordinal(), Comparator.reverseOrder())
						.thenComparing(PermisObjecte::getSid))
				.map(this::toPermisDetall)
				.collect(Collectors.toList());
	}

	/** Tots els permisos de l'usuari (directes i dels seus rols), inclosos els d'objectes inexistents. Els usa el simulador de permisos. */
	public List<PermisDetall> getTotsPermisosDetall(String usuariCodi) {
		return findPermisos(findSidsUsuari(usuariCodi)).stream()
				.map(this::toPermisDetall)
				.collect(Collectors.toList());
	}

	/**
	 * Revoca tots els permisos del SID indicat sobre l'objecte. El SID ha de ser l'usuari o un dels
	 * seus rols, i un permís de rol només es pot revocar si l'objecte ja no existeix (si no, es
	 * revocaria a tots els usuaris amb aquest rol).
	 */
	public void revocarPermis(String usuariCodi, ClassType tipus, Long objectId, Long sidId) {
		SidsUsuari sidsUsuari = findSidsUsuari(usuariCodi);
		AclSidEntity sid = sidsUsuari.getSids().stream()
				.filter(s -> s.getId().equals(sidId))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException(
						messageHelper.getMessage("usuari.manteniment.permisos.revocar.error.sid")));
		if (!sid.isPrincipal() && !isOrfe(tipus, objectId)) {
			throw new IllegalArgumentException(messageHelper.getMessage("usuari.manteniment.permisos.revocar.error.rol"));
		}
		PermisDto permis = new PermisDto();
		permis.setPrincipalTipus(sid.isPrincipal() ? PrincipalTipusEnumDto.USUARI : PrincipalTipusEnumDto.ROL);
		permis.setPrincipalNom(sid.getSid());
		// Un PermisDto sense cap permís marcat esborra les entrades del SID sobre l'objecte
		permisosHelper.updatePermis(objectId, getObjectClass(tipus), permis);
		log.info("Permisos revocats (usuari={}, tipus={}, objectId={}, sid={}, principal={})",
				usuariCodi, tipus, objectId, sid.getSid(), permis.getPrincipalTipus());
	}

	private SidsUsuari findSidsUsuari(String usuariCodi) {
		List<AclSidEntity> sids = new ArrayList<>();
		AclSidEntity sidUsuari = aclSidRepository.getUserSid(usuariCodi);
		if (sidUsuari != null) {
			sids.add(sidUsuari);
		}
		// Rols del plugin d'usuaris (nom original i mapejat) més "tothom", que l'aplicació afegeix a tots els usuaris
		Set<String> rolsAcl = new TreeSet<>();
		Set<String> rolsVisibles = new TreeSet<>();
		boolean rolsError = false;
		try {
			rolsAcl.addAll(permisosHelper.findRolsAclUsuari(usuariCodi));
		} catch (Exception ex) {
			log.warn("No s'han pogut consultar els rols de l'usuari " + usuariCodi + " per al visor de permisos", ex);
			rolsError = true;
		}
		rolsAcl.add(BaseConfig.ROLE_USER);
		if (!rolsAcl.isEmpty()) {
			sids.addAll(aclSidRepository.findRolesSid(new ArrayList<>(rolsAcl)));
		}
		rolsVisibles.addAll(rolsAcl);
		return new SidsUsuari(sids, rolsVisibles, rolsError);
	}

	private List<PermisObjecte> findPermisos(SidsUsuari sidsUsuari) {
		if (sidsUsuari.getSids().isEmpty()) {
			return new ArrayList<>();
		}
		List<Long> sidIds = sidsUsuari.getSids().stream().map(AclSidEntity::getId).collect(Collectors.toList());
		// Una fila per (objecte, SID) amb totes les màscares
		Map<String, PermisObjecte> permisos = new LinkedHashMap<>();
		for (AclEntrySidProjection entrada : aclEntryRepository.findBySidIdIn(sidIds)) {
			ClassType tipus = toClassType(entrada.getClassname());
			ExtendedPermissionEnum permisEnum = toPermissionEnum(entrada.getMask());
			if (tipus == null || permisEnum == null) {
				log.debug("Entrada ACL ignorada al visor de permisos (classe={}, màscara={})", entrada.getClassname(), entrada.getMask());
				continue;
			}
			String clau = tipus + "-" + entrada.getObjectId() + "-" + entrada.getSidId();
			PermisObjecte permis = permisos.computeIfAbsent(clau, k -> new PermisObjecte(
					clau,
					tipus,
					entrada.getObjectId(),
					entrada.getSidId(),
					entrada.getSid(),
					Boolean.TRUE.equals(entrada.getPrincipal()) ? PrincipalTipusEnumDto.USUARI : PrincipalTipusEnumDto.ROL));
			permis.getPermisos().add(permisEnum);
		}
		resoldreObjectes(permisos.values());
		return new ArrayList<>(permisos.values());
	}

	/** Completa cada permís amb l'entitat i el nom de l'objecte; si l'objecte no existeix queda com a orfe. */
	private void resoldreObjectes(Collection<PermisObjecte> permisos) {
		Map<ClassType, Set<Long>> idsPerTipus = new HashMap<>();
		for (PermisObjecte permis : permisos) {
			idsPerTipus.computeIfAbsent(permis.getTipus(), t -> new TreeSet<>()).add(permis.getObjectId());
		}
		Map<ClassType, Map<Long, ObjecteInfo>> infoPerTipus = new HashMap<>();
		for (Map.Entry<ClassType, Set<Long>> entry : idsPerTipus.entrySet()) {
			infoPerTipus.put(entry.getKey(), findObjectes(entry.getKey(), entry.getValue()));
		}
		for (PermisObjecte permis : permisos) {
			ObjecteInfo info = infoPerTipus.get(permis.getTipus()).get(permis.getObjectId());
			if (info != null) {
				permis.setEntitatId(info.getEntitatId());
				permis.setObjecteCodi(info.getCodi());
				permis.setObjecteNom(info.getNom());
				permis.setOrgan(info.getOrgan());
			} else {
				permis.setOrfe(true);
			}
		}
	}

	private Map<Long, ObjecteInfo> findObjectes(ClassType tipus, Collection<Long> ids) {
		switch (tipus) {
		case ENTITY:
			return toInfo(findAllByIdEnLots(entitatRepository, ids),
					e -> new ObjecteInfo(e.getId(), e.getCodi(), e.getNom(), null));
		case ORGAN:
			return toInfo(findAllByIdEnLots(organGestorRepository, ids),
					o -> new ObjecteInfo(getEntitatId(o.getEntitat()), o.getCodi(), o.getNom(), null));
		case GRUP:
			return toInfo(findAllByIdEnLots(grupRepository, ids),
					g -> new ObjecteInfo(getEntitatId(g.getEntitat()), g.getCodi(), g.getDescripcio(), null));
		case MET_NOD:
			return toInfo(findAllByIdEnLots(metaNodeRepository, ids),
					m -> new ObjecteInfo(getEntitatId(m.getEntitat()), m.getCodi(), m.getNom(), null));
		case MET_EXP_ORG:
			return toInfo(findAllByIdEnLots(metaExpedientOrganGestorRepository, ids),
					meo -> {
						MetaExpedientEntity metaExpedient = meo.getMetaExpedient();
						OrganGestorEntity organ = meo.getOrganGestor();
						return new ObjecteInfo(
								getEntitatId(metaExpedient.getEntitat()),
								metaExpedient.getCodi(),
								metaExpedient.getNom(),
								organ != null ? organ.getCodiINom() : null);
					});
		default:
			return new HashMap<>();
		}
	}

	private boolean isOrfe(ClassType tipus, Long objectId) {
		switch (tipus) {
		case ENTITY: return !entitatRepository.existsById(objectId);
		case ORGAN: return !organGestorRepository.existsById(objectId);
		case GRUP: return !grupRepository.existsById(objectId);
		case MET_NOD: return !metaNodeRepository.existsById(objectId);
		case MET_EXP_ORG: return !metaExpedientOrganGestorRepository.existsById(objectId);
		default: return false;
		}
	}

	private PermisDetall toPermisDetall(PermisObjecte permis) {
		PermisDetall detall = new PermisDetall();
		detall.setId(permis.getId());
		detall.setTipus(permis.getTipus());
		detall.setObjectId(permis.getObjectId());
		detall.setObjecteCodi(permis.getObjecteCodi());
		detall.setObjecteNom(permis.getObjecteNom());
		detall.setOrgan(permis.getOrgan());
		detall.setSidId(permis.getSidId());
		detall.setSid(permis.getSid());
		detall.setPrincipal(permis.getPrincipal());
		detall.setPermisos(new ArrayList<>(permis.getPermisos()));
		detall.setRevocable(PrincipalTipusEnumDto.USUARI.equals(permis.getPrincipal()) || permis.isOrfe());
		return detall;
	}

	private static <E> Map<Long, ObjecteInfo> toInfo(Map<Long, E> entitats, Function<E, ObjecteInfo> mapper) {
		Map<Long, ObjecteInfo> resultat = new HashMap<>();
		entitats.forEach((id, entitat) -> resultat.put(id, mapper.apply(entitat)));
		return resultat;
	}

	private static <E extends Persistable<Long>> Map<Long, E> findAllByIdEnLots(
			JpaRepository<E, Long> repository,
			Collection<Long> ids) {
		Map<Long, E> resultat = new HashMap<>();
		List<Long> llista = new ArrayList<>(ids);
		for (int i = 0; i < llista.size(); i += MIDA_LOT_IN) {
			for (E entity : repository.findAllById(llista.subList(i, Math.min(i + MIDA_LOT_IN, llista.size())))) {
				resultat.put(entity.getId(), entity);
			}
		}
		return resultat;
	}

	private static Long getEntitatId(EntitatEntity entitat) {
		return entitat != null ? entitat.getId() : null;
	}

	private static ClassType toClassType(String classname) {
		if (EntitatEntity.class.getName().equals(classname)) return ClassType.ENTITY;
		if (OrganGestorEntity.class.getName().equals(classname)) return ClassType.ORGAN;
		if (GrupEntity.class.getName().equals(classname)) return ClassType.GRUP;
		if (MetaNodeEntity.class.getName().equals(classname)) return ClassType.MET_NOD;
		if (MetaExpedientOrganGestorEntity.class.getName().equals(classname)) return ClassType.MET_EXP_ORG;
		return null;
	}

	private static Class<?> getObjectClass(ClassType tipus) {
		switch (tipus) {
		case ENTITY: return EntitatEntity.class;
		case ORGAN: return OrganGestorEntity.class;
		case GRUP: return GrupEntity.class;
		case MET_NOD: return MetaNodeEntity.class;
		case MET_EXP_ORG: return MetaExpedientOrganGestorEntity.class;
		default: throw new IllegalArgumentException("Tipus d'objecte no suportat: " + tipus);
		}
	}

	private static ExtendedPermissionEnum toPermissionEnum(Integer mask) {
		for (ExtendedPermissionEnum permis : ExtendedPermissionEnum.values()) {
			if (permis.getCodi().equals(mask)) {
				return permis;
			}
		}
		return null;
	}

	@Getter
	@RequiredArgsConstructor
	private static class SidsUsuari {
		private final List<AclSidEntity> sids;
		private final Set<String> rolsVisibles;
		private final boolean rolsError;
	}

	@Getter
	@RequiredArgsConstructor
	private static class ObjecteInfo {
		private final Long entitatId;
		private final String codi;
		private final String nom;
		private final String organ;
	}

	@Getter
	@Setter
	@RequiredArgsConstructor
	private static class PermisObjecte {
		private final String id;
		private final ClassType tipus;
		private final Long objectId;
		private final Long sidId;
		private final String sid;
		private final PrincipalTipusEnumDto principal;
		private final Set<ExtendedPermissionEnum> permisos = EnumSet.noneOf(ExtendedPermissionEnum.class);
		private Long entitatId;
		private String objecteCodi;
		private String objecteNom;
		private String organ;
		private boolean orfe;
	}
}
