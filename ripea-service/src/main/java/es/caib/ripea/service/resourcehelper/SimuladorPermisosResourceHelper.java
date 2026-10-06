package es.caib.ripea.service.resourcehelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.ExpedientPeticioEntity;
import es.caib.ripea.persistence.entity.GrupEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.persistence.repository.ExpedientOrganPareRepository;
import es.caib.ripea.persistence.repository.ExpedientPeticioRepository;
import es.caib.ripea.persistence.repository.ExpedientRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.ConversioTipusHelper;
import es.caib.ripea.service.helper.ExpedientHelper;
import es.caib.ripea.service.helper.ExpedientPeticioHelper;
import es.caib.ripea.service.helper.MessageHelper;
import es.caib.ripea.service.helper.MetaExpedientHelper;
import es.caib.ripea.service.helper.MetaExpedientHelper.PermisosProcediments;
import es.caib.ripea.service.helper.OrganGestorHelper;
import es.caib.ripea.service.helper.PermisosHelper;
import es.caib.ripea.service.helper.PermisosPerAnotacions;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.ExtendedPermissionEnum;
import es.caib.ripea.service.intf.dto.PermisosPerExpedientsDto;
import es.caib.ripea.service.intf.model.AclSidResource.ClassType;
import es.caib.ripea.service.intf.model.ContingutResource;
import es.caib.ripea.service.intf.model.UsuariResource.PermisDetall;
import es.caib.ripea.service.intf.model.UsuariResource.SimulacioComprovacio;
import es.caib.ripea.service.intf.model.UsuariResource.SimulacioEstat;
import es.caib.ripea.service.intf.model.UsuariResource.SimulacioPermisosForm;
import es.caib.ripea.service.intf.model.UsuariResource.SimulacioPermisosResultat;
import es.caib.ripea.service.intf.model.UsuariResource.SimulacioRecurs;
import es.caib.ripea.service.intf.resourceservice.ExpedientPeticioResourceService;
import es.caib.ripea.service.intf.resourceservice.ExpedientResourceService;
import es.caib.ripea.service.permission.ExtendedPermission;
import lombok.extern.slf4j.Slf4j;

/**
 * Simulador de permisos del superusuari: explica per quina via un usuari, amb un rol concret, veu (o no)
 * un expedient o una anotació al llistat REACT.
 *
 * No reimplementa cap regla de permisos:
 * - Les llistes de cada via surten dels mateixos mètodes que el llistat (ExpedientHelper.findPermisosPerExpedients,
 *   ExpedientPeticioHelper.findPermisosPerAnotacions, MetaExpedientHelper.findPermisosProcediments).
 * - Cada via i restricció s'avalua amb el mateix fragment de filtre que compon el llistat
 *   (ExpedientPermisosFiltreHelper, AnotacioPermisosFiltreHelper) o amb la mateixa consulta de procediments.
 * - El veredicte final és la consulta real del llistat (findPage del servei), i es contrasta amb el desglose.
 *
 * Tot el càlcul es fa amb la identitat de l'usuari simulat (codi, rols de Keycloak, tothom i el rol triat) i
 * amb l'entitat, el rol i l'òrgan simulats al context de ConfigHelper; tot es restaura en acabar.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Component
public class SimuladorPermisosResourceHelper {

	/** Rols de l'aplicació que tenen llistat d'expedients o d'anotacions. */
	public static final List<String> ROLS_SIMULABLES = List.of(
			BaseConfig.ROLE_ADMIN,
			BaseConfig.ROLE_ADMIN_LECTURA,
			BaseConfig.ROLE_ORGAN_ADMIN,
			BaseConfig.ROLE_DISSENY,
			BaseConfig.ROLE_USER);

	private static final String NAMED_QUERY_SENSE_PERMISOS = "WITHOUT_PERMISION_CHECK";
	private static final String NAMED_QUERY_LLISTAT_ANOTACIONS = "LLISTAT_ANOTACIONS";
	/** Valor buit d'un paràmetre de l'element (el front el mostra com a "(sense ...)"); null vol dir que no s'informa. */
	private static final String BUIT = "";
	/** Màxim de files de permisos que s'adjunten a cada comprovació (el total va a nombreObjectes). */
	private static final int MAX_PERMISOS_COMPROVACIO = 100;

	@Autowired private ExpedientHelper expedientHelper;
	@Autowired private ExpedientPeticioHelper expedientPeticioHelper;
	@Autowired private MetaExpedientHelper metaExpedientHelper;
	@Autowired private OrganGestorHelper organGestorHelper;
	@Autowired private PermisosHelper permisosHelper;
	@Autowired private ConfigHelper configHelper;
	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Autowired private MessageHelper messageHelper;
	@Autowired private ExpedientPermisosFiltreHelper expedientPermisosFiltreHelper;
	@Autowired private AnotacioPermisosFiltreHelper anotacioPermisosFiltreHelper;
	@Autowired private UsuariPermisosResourceHelper usuariPermisosResourceHelper;
	@Autowired private EntitatRepository entitatRepository;
	@Autowired private ExpedientRepository expedientRepository;
	@Autowired private ExpedientPeticioRepository expedientPeticioRepository;
	@Autowired private ExpedientOrganPareRepository expedientOrganPareRepository;
	@Autowired private OrganGestorRepository organGestorRepository;
	// Els serveis de recursos s'injecten tard: el simulador els crida per obtenir la consulta real del llistat
	@Lazy @Autowired private ExpedientResourceService expedientResourceService;
	@Lazy @Autowired private ExpedientPeticioResourceService expedientPeticioResourceService;

	// =========================================================================================
	// Opcions del formulari
	// =========================================================================================

	/** Rols simulables que té l'usuari: els de l'aplicació que té a Keycloak, més tothom (l'afegeix l'aplicació). */
	public List<String> findRolsSimulables(String usuariCodi) {
		Set<String> rolsUsuari = findRolsKeycloak(usuariCodi);
		return ROLS_SIMULABLES.stream()
				.filter(rol -> BaseConfig.ROLE_USER.equals(rol) || rolsUsuari.contains(rol))
				.collect(Collectors.toList());
	}

	/** Òrgans que l'usuari podria seleccionar a la capçalera amb el rol (els mateixos que calcula la capçalera). */
	public List<OrganGestorEntity> findOrgansSeleccionables(String usuariCodi, String rol, Long entitatId) {
		EntitatEntity entitat = entitatRepository.findById(entitatId).orElse(null);
		if (entitat == null || !isRolAmbOrgan(rol)) {
			return new ArrayList<>();
		}
		return ambIdentitat(usuariCodi, rol, null, null,
				() -> organGestorHelper.findAmbEntitatPermis(entitat, permisOrganCapcalera(rol)));
	}

	/** Entitat de l'element triat (null si no n'hi ha). */
	public Long findEntitatElement(SimulacioRecurs recurs, Long expedientId, Long anotacioId) {
		if (SimulacioRecurs.EXPEDIENT.equals(recurs) && expedientId != null) {
			return expedientRepository.findById(expedientId).map(e -> e.getEntitat().getId()).orElse(null);
		}
		if (SimulacioRecurs.ANOTACIO.equals(recurs) && anotacioId != null) {
			return expedientPeticioRepository.findById(anotacioId)
					.map(a -> a.getRegistre() != null && a.getRegistre().getEntitat() != null ? a.getRegistre().getEntitat().getId() : null)
					.orElse(null);
		}
		return null;
	}

	public static boolean isRolAmbOrgan(String rol) {
		return BaseConfig.ROLE_ORGAN_ADMIN.equals(rol) || BaseConfig.ROLE_DISSENY.equals(rol);
	}

	// =========================================================================================
	// Simulació
	// =========================================================================================

	public SimulacioPermisosResultat simular(String usuariCodi, SimulacioPermisosForm form) {
		if (form == null || form.getRol() == null || form.getRecurs() == null) {
			throw new IllegalArgumentException(messageHelper.getMessage("usuari.simulador.error.parametres"));
		}
		String rol = form.getRol();
		SimulacioRecurs recurs = form.getRecurs();
		Long expedientId = SimulacioRecurs.EXPEDIENT.equals(recurs) && form.getExpedient() != null ? form.getExpedient().getId() : null;
		Long anotacioId = SimulacioRecurs.ANOTACIO.equals(recurs) && form.getAnotacio() != null ? form.getAnotacio().getId() : null;
		Long elementId = expedientId != null ? expedientId : anotacioId;

		Long entitatId = elementId != null ? findEntitatElement(recurs, expedientId, anotacioId) : toLong(form.getEntitat());
		EntitatEntity entitat = entitatId != null ? entitatRepository.findById(entitatId).orElse(null) : null;
		if (entitat == null) {
			throw new IllegalArgumentException(messageHelper.getMessage("usuari.simulador.error.entitat"));
		}
		OrganGestorEntity organ = null;
		if (isRolAmbOrgan(rol)) {
			Long organId = toLong(form.getOrgan());
			organ = organId != null ? organGestorRepository.findById(organId).orElse(null) : null;
			if (organ == null) {
				throw new IllegalArgumentException(messageHelper.getMessage("usuari.simulador.error.organ"));
			}
		}

		Context ctx = new Context();
		ctx.usuariCodi = usuariCodi;
		ctx.rol = rol;
		ctx.entitat = entitat;
		ctx.organ = organ;
		ctx.elementId = elementId;
		ctx.rolsKeycloak = findRolsKeycloak(usuariCodi);
		ctx.permisos = usuariPermisosResourceHelper.getTotsPermisosDetall(usuariCodi);

		SimulacioPermisosResultat resultat = new SimulacioPermisosResultat();
		resultat.setUsuariCodi(usuariCodi);
		resultat.setRol(rol);
		resultat.setRecurs(recurs);
		resultat.setEntitatNom(entitat.getNom());
		resultat.setOrganNom(organ != null ? organ.getCodi() + " - " + organ.getNom() : null);
		resultat.setAmbElement(elementId != null);
		resultat.setRequisits(new ArrayList<>());
		resultat.setVies(new ArrayList<>());
		resultat.setRestriccions(new ArrayList<>());

		ambIdentitat(usuariCodi, rol, entitat, organ, () -> {
			afegirRequisitsComuns(ctx, resultat);
			if (SimulacioRecurs.EXPEDIENT.equals(recurs)) {
				simularExpedient(ctx, resultat);
			} else {
				simularAnotacio(ctx, resultat);
			}
			return null;
		});
		return resultat;
	}

	private void afegirRequisitsComuns(Context ctx, SimulacioPermisosResultat resultat) {
		// Rol disponible: el rol ha de venir de Keycloak (tothom l'afegeix l'aplicació a tothom)
		boolean rolDisponible = BaseConfig.ROLE_USER.equals(ctx.rol) || ctx.rolsKeycloak.contains(ctx.rol);
		resultat.getRequisits().add(comprovacio("ROL_KEYCLOAK", rolDisponible ? SimulacioEstat.OK : SimulacioEstat.KO, params("rol", ctx.rol)));

		// Permís sobre l'entitat que exigeix el rol per poder-lo triar a la capçalera (RolHelper.getRolsUsuariActual)
		Permission permisEntitat = permisEntitatRol(ctx.rol);
		SimulacioComprovacio entitatComprovacio;
		if (permisEntitat == null) {
			entitatComprovacio = comprovacio("PERMIS_ENTITAT", SimulacioEstat.NO_APLICA, params("rol", ctx.rol));
		} else {
			boolean granted = permisosHelper.isGrantedAny(
					ctx.entitat.getId(),
					EntitatEntity.class,
					new Permission[] { permisEntitat },
					SecurityContextHolder.getContext().getAuthentication());
			entitatComprovacio = comprovacio("PERMIS_ENTITAT", granted ? SimulacioEstat.OK : SimulacioEstat.KO,
					params("rol", ctx.rol, "entitat", ctx.entitat.getNom(), "permis", nomPermis(permisEntitat)));
		}
		ambPermisos(entitatComprovacio, "ENTITAT", permisosSobre(ctx, ClassType.ENTITY, List.of(ctx.entitat.getId())), false,
				permisEntitat != null ? new ExtendedPermissionEnum[] { ExtendedPermissionEnum.valueOf(nomPermis(permisEntitat)) } : new ExtendedPermissionEnum[0]);
		resultat.getRequisits().add(entitatComprovacio);

		// Òrgan seleccionable a la capçalera (només rols amb òrgan)
		if (ctx.organ != null) {
			List<OrganGestorEntity> organs = organGestorHelper.findAmbEntitatPermis(ctx.entitat, permisOrganCapcalera(ctx.rol));
			boolean seleccionable = organs.stream().anyMatch(o -> o.getId().equals(ctx.organ.getId()));
			SimulacioComprovacio organComprovacio = comprovacio("ORGAN_CAPCALERA", seleccionable ? SimulacioEstat.OK : SimulacioEstat.KO,
					params("organ", descripcio(ctx.organ), "permis", nomPermis(permisOrganCapcalera(ctx.rol))));
			ambPermisos(organComprovacio, "ORGAN_CAPCALERA", permisosSobre(ctx, ClassType.ORGAN, List.of(ctx.organ.getId())), false, permisOrganCapcaleraEnum(ctx.rol));
			resultat.getRequisits().add(organComprovacio);
		}
	}

	// =========================================================================================
	// Expedients
	// =========================================================================================

	private void simularExpedient(Context ctx, SimulacioPermisosResultat resultat) {
		ExpedientEntity expedient = ctx.elementId != null ? expedientRepository.findById(ctx.elementId).orElse(null) : null;
		if (expedient != null) {
			resultat.setElementId(expedient.getId());
			resultat.setElementDescripcio(expedient.getNumero() + " - " + expedient.getNom());
			resultat.setProcedimentDescripcio(descripcio(expedient.getMetaExpedient()));
		}

		// Veredicte: consulta real del llistat
		Filter filtreElement = ctx.elementId != null ? FilterBuilder.equal("id", ctx.elementId) : null;
		try {
			resultat.setTotalReal(expedientResourceService.findPage(
					null,
					filtreElement != null ? filtreElement.generate() : null,
					null,
					null,
					PageRequest.of(0, 1)).getTotalElements());
		} catch (Exception ex) {
			log.debug("Simulador de permisos: error a la consulta real d'expedients", ex);
			resultat.setErrorConsulta(missatge(ex));
		}

		PermisosPerExpedientsDto llistes = expedientHelper.findPermisosPerExpedients(ctx.entitat.getId(), ctx.rol, ctx.organ != null ? ctx.organ.getId() : null);
		Filter filtreBase = FilterBuilder.and(filtreElement, FilterBuilder.equal(ContingutResource.Fields.esborrat, "0"));

		boolean aplicaVies = expedientPermisosFiltreHelper.isAplicaVies(ctx.rol);
		boolean rolAmbOrgan = isRolAmbOrgan(ctx.rol);

		// Objectes de l'expedient que decideixen cada via (només amb element). Les files d'organpare es llegeixen
		// com a llista (no el Set de l'entitat) per detectar també les duplicades.
		Long procedimentId = expedient != null ? expedient.getMetaExpedient().getId() : null;
		List<MetaExpedientOrganGestorEntity> filesOrganpare = expedient != null
				? expedientOrganPareRepository.findMetaExpedientOrganGestorByExpedientId(expedient.getId())
				: new ArrayList<>();
		List<Long> parellesIds = filesOrganpare.stream().map(MetaExpedientOrganGestorEntity::getId).distinct().collect(Collectors.toList());
		List<Long> organsParelles = filesOrganpare.stream()
				.filter(p -> p.getOrganGestor() != null)
				.map(p -> p.getOrganGestor().getId())
				.distinct()
				.collect(Collectors.toList());
		Long grupId = expedient != null && expedient.getGrup() != null ? expedient.getGrup().getId() : null;

		if (expedient != null) {
			resultat.getRequisits().add(comprovacioOrganpare(ctx, expedient, filesOrganpare));
		}

		if (!aplicaVies) {
			// Administrador d'entitat (i de lectura): veu tots els expedients de l'entitat
			SimulacioComprovacio admin = comprovacioComptada("EXP_ADMIN_ENTITAT", comptarExpedients(filtreBase), null, params("entitat", ctx.entitat.getNom()));
			resultat.getVies().add(admin);
		} else {
			boolean comu = expedient != null && expedient.getMetaExpedient().getOrganGestor() == null;
			boolean permisDirecte = expedient != null && expedient.getMetaExpedient().isPermisDirecte();

			SimulacioComprovacio via1 = viaExpedient(ctx, "EXP_VIA1_PROCEDIMENT", true,
					expedientPermisosFiltreHelper.filtreVia1Procediments(llistes), filtreBase,
					llistes.getIdsMetaExpedientsPermesos(),
					params());
			ambPermisos(via1, expedient != null ? "PROCEDIMENT" : "PROCEDIMENTS",
					permisosSobre(ctx, ClassType.MET_NOD, idsObjectes(ctx, procedimentId, llistes.getIdsMetaExpedientsPermesos())), false, ExtendedPermissionEnum.READ);
			resultat.getVies().add(via1);

			SimulacioComprovacio via2 = viaExpedient(ctx, "EXP_VIA2_ORGAN", rolAmbOrgan,
					expedientPermisosFiltreHelper.filtreVia2Organs(llistes), filtreBase,
					llistes.getIdsOrgansPermesos(),
					params("organ", ctx.organ != null ? descripcio(ctx.organ) : null,
							"organExpedient", expedient != null ? buitSiNull(descripcio(expedient.getOrganGestor())) : null));
			if (ctx.organ != null) {
				ambPermisos(via2, "ORGAN_CAPCALERA", permisosSobre(ctx, ClassType.ORGAN, List.of(ctx.organ.getId())), false, permisOrganCapcaleraEnum(ctx.rol));
			}
			resultat.getVies().add(via2);

			SimulacioComprovacio via3 = viaExpedient(ctx, "EXP_VIA3_PARELLA", !rolAmbOrgan,
					expedientPermisosFiltreHelper.filtreVia3ParellesProcedimentOrgan(llistes), filtreBase,
					llistes.getIdsMetaExpedientOrganPairsPermesos(),
					params());
			ambPermisos(via3, expedient != null ? "PARELLES_CADENA" : "PARELLES",
					permisosSobre(ctx, ClassType.MET_EXP_ORG, expedient != null ? parellesIds : llistes.getIdsMetaExpedientOrganPairsPermesos()), false, ExtendedPermissionEnum.READ);
			resultat.getVies().add(via3);

			// La via 4 només pot donar accés als procediments comuns: si no ho és, no s'hi adjunten òrgans
			SimulacioComprovacio via4 = viaExpedient(ctx, "EXP_VIA4_COMUNS", !rolAmbOrgan,
					expedientPermisosFiltreHelper.filtreVia4ComunsPerOrgan(llistes), filtreBase,
					llistes.getIdsOrgansAmbProcedimentsComunsPermesos(),
					expedient != null ? params("comu", String.valueOf(comu)) : params());
			if (expedient != null && !comu) {
				via4.setSuggerimentVariant("NO_COMU");
			} else {
				ambPermisos(via4, expedient != null ? "ORGANS_CADENA" : "ORGANS_COMU",
						permisosSobre(ctx, ClassType.ORGAN, expedient != null ? organsParelles : llistes.getIdsOrgansAmbProcedimentsComunsPermesos()), true,
						ExtendedPermissionEnum.COMU, ExtendedPermissionEnum.READ);
			}
			resultat.getVies().add(via4);

			SimulacioComprovacio via5 = viaExpedient(ctx, "EXP_VIA5_GRUP", !rolAmbOrgan,
					expedientPermisosFiltreHelper.filtreVia5Grups(llistes), filtreBase,
					llistes.getIdsGrupsPermesos(),
					expedient != null ? params(
							"grup", buitSiNull(expedient.getGrup() != null ? descripcio(expedient.getGrup()) : null),
							"permisDirecte", String.valueOf(permisDirecte)) : params());
			if (expedient != null && grupId == null) {
				via5.setSuggerimentVariant("SENSE_GRUP");
			} else if (permisDirecte) {
				via5.setSuggerimentVariant("PERMIS_DIRECTE");
			}
			ambPermisos(via5, expedient != null ? "GRUP_EXPEDIENT" : "GRUPS",
					permisosSobre(ctx, ClassType.GRUP, idsObjectes(ctx, grupId, llistes.getIdsGrupsPermesos())), false, ExtendedPermissionEnum.READ);
			resultat.getVies().add(via5);
		}

		// Sense element: expedients de l'entitat i expedients que concedeix alguna via (abans de les restriccions)
		Filter filtreUnioVies = null;
		if (ctx.elementId == null) {
			long totalEntitat = comptarExpedients(filtreBase);
			long totalVies;
			if (!aplicaVies) {
				totalVies = totalEntitat;
			} else {
				filtreUnioVies = llistes.capPermis() ? null : expedientPermisosFiltreHelper.filtreVies(llistes);
				totalVies = filtreUnioVies != null ? comptarExpedients(FilterBuilder.and(filtreBase, filtreUnioVies)) : 0L;
			}
			resultat.setTotalEntitat(totalEntitat);
			resultat.setTotalVies(totalVies);
		}

		// Restriccions
		if (aplicaVies && expedientPermisosFiltreHelper.isAplicaRestriccioPermisDirecte(ctx.rol)) {
			List<PermisDetall> permisosA = new ArrayList<>(permisosSobre(ctx, ClassType.MET_NOD, idsObjectes(ctx, procedimentId, llistes.getIdsMetaExpedientsPermesos())));
			if (expedient != null) {
				permisosA.addAll(permisosSobre(ctx, ClassType.MET_EXP_ORG, parellesIds));
			}
			SimulacioComprovacio restriccioA = restriccioExpedient("EXP_RESTRICCIO_PERMIS_DIRECTE",
					expedientPermisosFiltreHelper.filtreRestriccioPermisDirecte(llistes), filtreBase,
					expedient != null ? params("permisDirecte", String.valueOf(expedient.getMetaExpedient().isPermisDirecte())) : params());
			ambPermisos(restriccioA, "PROCEDIMENT_I_PARELLES", permisosA, false, ExtendedPermissionEnum.READ);
			exclososExpedients(ctx, resultat, restriccioA, filtreBase, filtreUnioVies, expedientPermisosFiltreHelper.filtreRestriccioPermisDirecte(llistes));
			resultat.getRestriccions().add(restriccioA);
		} else {
			resultat.getRestriccions().add(comprovacio("EXP_RESTRICCIO_PERMIS_DIRECTE", SimulacioEstat.NO_APLICA, params("rol", ctx.rol)));
		}
		Filter filtreRestriccioOrgans = expedientPermisosFiltreHelper.filtreRestriccioOrgans(llistes);
		if (filtreRestriccioOrgans != null) {
			SimulacioComprovacio restriccioB = restriccioExpedient("EXP_RESTRICCIO_ORGANS", filtreRestriccioOrgans, filtreBase,
					params("organ", ctx.organ != null ? descripcio(ctx.organ) : null,
							"organExpedient", expedient != null ? buitSiNull(descripcio(expedient.getOrganGestor())) : null));
			exclososExpedients(ctx, resultat, restriccioB, filtreBase, filtreUnioVies, filtreRestriccioOrgans);
			resultat.getRestriccions().add(restriccioB);
		} else {
			resultat.getRestriccions().add(comprovacio("EXP_RESTRICCIO_ORGANS", SimulacioEstat.NO_APLICA, params("rol", ctx.rol)));
		}
		if (aplicaVies && expedientPermisosFiltreHelper.isAplicaRestriccioGrups(ctx.rol)) {
			SimulacioComprovacio restriccioC = restriccioExpedient("EXP_RESTRICCIO_GRUPS",
					expedientPermisosFiltreHelper.filtreRestriccioGrups(llistes), filtreBase,
					expedient != null ? params("grup", buitSiNull(expedient.getGrup() != null ? descripcio(expedient.getGrup()) : null)) : params());
			ambPermisos(restriccioC, expedient != null ? "GRUP_EXPEDIENT" : "GRUPS",
					permisosSobre(ctx, ClassType.GRUP, idsObjectes(ctx, grupId, llistes.getIdsGrupsPermesos())), false, ExtendedPermissionEnum.READ);
			exclososExpedients(ctx, resultat, restriccioC, filtreBase, filtreUnioVies, expedientPermisosFiltreHelper.filtreRestriccioGrups(llistes));
			resultat.getRestriccions().add(restriccioC);
		} else {
			resultat.getRestriccions().add(comprovacio("EXP_RESTRICCIO_GRUPS", SimulacioEstat.NO_APLICA, params("rol", ctx.rol)));
		}

		// Contrast del desglose amb la consulta real (només amb element: 0/1)
		if (ctx.elementId != null && resultat.getErrorConsulta() == null) {
			boolean esperat;
			if (!aplicaVies) {
				esperat = algunaOk(resultat.getVies());
			} else {
				esperat = !llistes.capPermis() && algunaOk(resultat.getVies()) && capKo(resultat.getRestriccions());
			}
			resultat.setDiscrepancia(esperat != (resultat.getTotalReal() > 0));
		}
	}

	/**
	 * Compara les files d'IPA_EXPEDIENT_ORGANPARE de l'expedient amb la cadena actual d'òrgans (l'òrgan de
	 * l'expedient i els seus superiors segons la jerarquia de BD, la mateixa que usa crearExpedientOrganPares).
	 * Les vies 3 i 4 només veuen l'expedient a través d'aquestes files. Adjunta els permisos de l'usuari sobre els
	 * òrgans que falten: si n'hi ha, l'usuari hi tindria accés per aquestes vies amb la cadena correcta.
	 */
	private SimulacioComprovacio comprovacioOrganpare(Context ctx, ExpedientEntity expedient, List<MetaExpedientOrganGestorEntity> filesOrganpare) {
		Long procedimentId = expedient.getMetaExpedient().getId();
		List<OrganGestorEntity> cadenaEsperada = expedient.getOrganGestor() != null
				? organGestorHelper.findPares(expedient.getOrganGestor(), true)
				: new ArrayList<>();
		Set<Long> organsEsperats = cadenaEsperada.stream().map(OrganGestorEntity::getId).collect(Collectors.toCollection(LinkedHashSet::new));

		Map<Long, Integer> repeticions = new LinkedHashMap<>();
		Map<Long, OrganGestorEntity> organsActuals = new LinkedHashMap<>();
		int filesAltreProcediment = 0;
		for (MetaExpedientOrganGestorEntity parella: filesOrganpare) {
			if (parella.getMetaExpedient() == null || !procedimentId.equals(parella.getMetaExpedient().getId())) {
				filesAltreProcediment++;
			}
			if (parella.getOrganGestor() != null) {
				organsActuals.put(parella.getOrganGestor().getId(), parella.getOrganGestor());
				repeticions.merge(parella.getOrganGestor().getId(), 1, Integer::sum);
			}
		}
		List<OrganGestorEntity> faltants = cadenaEsperada.stream()
				.filter(o -> !organsActuals.containsKey(o.getId()))
				.collect(Collectors.toList());
		List<OrganGestorEntity> sobrants = organsActuals.values().stream()
				.filter(o -> !organsEsperats.contains(o.getId()))
				.collect(Collectors.toList());
		List<OrganGestorEntity> duplicats = organsActuals.values().stream()
				.filter(o -> repeticions.get(o.getId()) > 1)
				.collect(Collectors.toList());

		boolean sincronitzat = faltants.isEmpty() && sobrants.isEmpty() && duplicats.isEmpty() && filesAltreProcediment == 0;
		SimulacioComprovacio comprovacio = comprovacio("EXPEDIENT_ORGANPARE", sincronitzat ? SimulacioEstat.OK : SimulacioEstat.AVIS, params(
				"esperades", String.valueOf(cadenaEsperada.size()),
				"actuals", String.valueOf(filesOrganpare.size()),
				"faltants", descripcions(faltants),
				"sobrants", descripcions(sobrants),
				"duplicats", descripcions(duplicats),
				"altreProcediment", filesAltreProcediment > 0 ? String.valueOf(filesAltreProcediment) : null));
		ambPermisos(comprovacio, "ORGANS_FALTANTS",
				permisosSobre(ctx, ClassType.ORGAN, faltants.stream().map(OrganGestorEntity::getId).collect(Collectors.toList())), true,
				ExtendedPermissionEnum.COMU, ExtendedPermissionEnum.READ);
		return comprovacio;
	}

	/** Regenera la cadena d'òrgans (organpare) d'un expedient amb el mateix mètode que el canvi d'òrgan i la sincronització DIR3. */
	public void regenerarOrganpare(Long expedientId) {
		ExpedientEntity expedient = expedientId != null ? expedientRepository.findById(expedientId).orElse(null) : null;
		if (expedient == null) {
			throw new IllegalArgumentException(messageHelper.getMessage("usuari.simulador.organpare.error.expedient"));
		}
		organGestorHelper.reconstruirExpedientOrganPares(expedient);
		log.info("Simulador de permisos: regenerada la cadena d'òrgans de l'expedient {} ({})", expedient.getId(), expedient.getNumero());
	}

	private static String descripcions(List<OrganGestorEntity> organs) {
		return organs.isEmpty() ? null : organs.stream().map(o -> descripcio(o)).collect(Collectors.joining(", "));
	}

	private SimulacioComprovacio viaExpedient(
			Context ctx,
			String codi,
			boolean aplica,
			Filter filtreVia,
			Filter filtreBase,
			List<?> llistaObjectes,
			Map<String, String> parametres) {
		if (!aplica) {
			return comprovacio(codi, SimulacioEstat.NO_APLICA, params("rol", ctx.rol));
		}
		return filtreVia == null
				? comprovacioComptada(codi, 0L, 0, parametres)
				: comprovacioComptada(codi, comptarExpedients(FilterBuilder.and(filtreBase, filtreVia)), mida(llistaObjectes), parametres);
	}

	private SimulacioComprovacio restriccioExpedient(
			String codi,
			Filter filtreRestriccio,
			Filter filtreBase,
			Map<String, String> parametres) {
		return comprovacioComptada(codi, comptarExpedients(FilterBuilder.and(filtreBase, filtreRestriccio)), null, parametres);
	}

	/**
	 * Sense element: expedients que alguna via concedeix i que la restricció exclou. Es calcula per diferència
	 * (concedits − concedits que compleixen la restricció) i no amb un NOT del filtre, que amb camps nuls (p. ex.
	 * expedients sense grup) deixaria fora de la comptabilitat justament els casos exclosos.
	 */
	private void exclososExpedients(
			Context ctx,
			SimulacioPermisosResultat resultat,
			SimulacioComprovacio restriccio,
			Filter filtreBase,
			Filter filtreUnioVies,
			Filter filtreRestriccio) {
		if (ctx.elementId != null || resultat.getTotalVies() == null) {
			return;
		}
		long concedits = resultat.getTotalVies();
		long compleixen = filtreUnioVies != null
				? comptarExpedients(FilterBuilder.and(filtreBase, filtreUnioVies, filtreRestriccio))
				: comptarExpedients(FilterBuilder.and(filtreBase, filtreRestriccio));
		restriccio.setExclosos(Math.max(0L, concedits - compleixen));
	}

	/** Expedients de l'entitat simulada que compleixen el filtre (sense la part de permisos del llistat). */
	private long comptarExpedients(Filter filtre) {
		return expedientResourceService.findPage(
				null,
				filtre != null ? filtre.generate() : null,
				new String[] { NAMED_QUERY_SENSE_PERMISOS },
				null,
				PageRequest.of(0, 1)).getTotalElements();
	}

	// =========================================================================================
	// Anotacions
	// =========================================================================================

	private void simularAnotacio(Context ctx, SimulacioPermisosResultat resultat) {
		ExpedientPeticioEntity anotacio = ctx.elementId != null ? expedientPeticioRepository.findById(ctx.elementId).orElse(null) : null;
		MetaExpedientEntity procediment = anotacio != null ? anotacio.getMetaExpedient() : null;
		if (anotacio != null) {
			resultat.setElementId(anotacio.getId());
			resultat.setElementDescripcio(anotacio.getIdentificador());
			resultat.setProcedimentDescripcio(buitSiNull(procediment != null ? descripcio(procediment) : null));
		}

		// Veredicte: consulta real del llistat d'anotacions
		Filter filtreElement = ctx.elementId != null ? FilterBuilder.equal("id", ctx.elementId) : null;
		try {
			resultat.setTotalReal(expedientPeticioResourceService.findPage(
					null,
					filtreElement != null ? filtreElement.generate() : null,
					new String[] { NAMED_QUERY_LLISTAT_ANOTACIONS },
					null,
					PageRequest.of(0, 1)).getTotalElements());
		} catch (Exception ex) {
			log.debug("Simulador de permisos: error a la consulta real d'anotacions", ex);
			resultat.setErrorConsulta(missatge(ex));
		}

		boolean perProcediment = BaseConfig.ROLE_USER.equals(ctx.rol) || BaseConfig.ROLE_DISSENY.equals(ctx.rol);
		if (anotacio != null && perProcediment) {
			resultat.getRequisits().add(comprovacio("ANOTACIO_PROCEDIMENT", procediment != null ? SimulacioEstat.OK : SimulacioEstat.KO,
					params()));
		}
		if (procediment != null && BaseConfig.ROLE_USER.equals(ctx.rol)) {
			// Condicions de la consulta de procediments permesos (findAmbPermis): actiu i, amb revisió activa, revisat
			resultat.getRequisits().add(comprovacio("PROCEDIMENT_ACTIU", procediment.isActiu() ? SimulacioEstat.OK : SimulacioEstat.KO,
					params()));
			if (metaExpedientHelper.isRevisioActiva()) {
				boolean revisat = "REVISAT".equals(String.valueOf(procediment.getRevisioEstat()));
				resultat.getRequisits().add(comprovacio("PROCEDIMENT_REVISAT", revisat ? SimulacioEstat.OK : SimulacioEstat.KO,
						params("estat", procediment.getRevisioEstat() != null ? procediment.getRevisioEstat().name() : BUIT)));
			}
		}

		long totalEntitatAnotacions = ctx.elementId == null ? comptarAnotacions(null) : 0L;
		if (ctx.elementId == null) {
			resultat.setTotalEntitat(totalEntitatAnotacions);
		}

		if (!anotacioPermisosFiltreHelper.isAplicaFiltrePermisos(ctx.rol)) {
			if (ctx.elementId == null) {
				resultat.setTotalVies(totalEntitatAnotacions);
			}
			// Administrador d'entitat: totes les anotacions de l'entitat
			resultat.getVies().add(comprovacioComptada("ANO_ADMIN_ENTITAT", comptarAnotacions(filtreElement), null, params("entitat", ctx.entitat.getNom())));
			resultat.getRestriccions().add(comprovacio("ANO_RESTRICCIO_GRUPS", SimulacioEstat.NO_APLICA, params("rol", ctx.rol)));
		} else if (BaseConfig.ROLE_ADMIN_LECTURA.equals(ctx.rol)) {
			// El llistat no contempla aquest rol: no veu cap anotació
			if (ctx.elementId == null) {
				resultat.setTotalVies(0L);
			}
			resultat.getVies().add(comprovacio("ANO_ROL_SENSE_LLISTAT", SimulacioEstat.KO, params("rol", ctx.rol)));
			resultat.getRestriccions().add(comprovacio("ANO_RESTRICCIO_GRUPS", SimulacioEstat.NO_APLICA, params("rol", ctx.rol)));
		} else {
			PermisosPerAnotacions llistes = expedientPeticioHelper.findPermisosPerAnotacions(
					ctx.entitat.getId(),
					null,
					ctx.rol,
					ctx.organ != null ? ctx.organ.getId() : null);

			if (anotacioPermisosFiltreHelper.isFiltrePerOrganDesti(ctx.rol)) {
				Filter filtre = anotacioPermisosFiltreHelper.filtreOrgansDesti(llistes);
				if (ctx.elementId == null) {
					resultat.setTotalVies(filtre != null ? comptarAnotacions(filtre) : 0L);
				}
				SimulacioComprovacio via = filtre == null
						? comprovacioComptada("ANO_ORGAN_DESTI", 0L, 0, params())
						: comprovacioComptada("ANO_ORGAN_DESTI", comptarAnotacions(FilterBuilder.and(filtreElement, filtre)),
								mida(llistes.getAdminOrganCodisOrganAmbDescendents()),
								params("organ", descripcio(ctx.organ), "desti", anotacio != null ? buitSiNull(anotacio.getRegistre() != null ? anotacio.getRegistre().getDestiCodi() : null) : null));
				ambPermisos(via, "ORGAN_CAPCALERA", permisosSobre(ctx, ClassType.ORGAN, List.of(ctx.organ.getId())), false, ExtendedPermissionEnum.ADMINISTRATION);
				resultat.getVies().add(via);
				resultat.getRestriccions().add(comprovacio("ANO_RESTRICCIO_GRUPS", SimulacioEstat.NO_APLICA, params("rol", ctx.rol)));
			} else {
				if (BaseConfig.ROLE_DISSENY.equals(ctx.rol)) {
					Filter filtre = anotacioPermisosFiltreHelper.filtreProcediments(llistes);
					SimulacioComprovacio via = filtre == null
							? comprovacioComptada("ANO_PROCEDIMENTS_ORGAN", 0L, 0, params("organ", descripcio(ctx.organ)))
							: comprovacioComptada("ANO_PROCEDIMENTS_ORGAN", comptarAnotacions(FilterBuilder.and(filtreElement, filtre)),
									mida(llistes.getProcedimentsPermesos()),
									params("organ", descripcio(ctx.organ)));
					ambPermisos(via, "ORGAN_CAPCALERA", permisosSobre(ctx, ClassType.ORGAN, List.of(ctx.organ.getId())), false, ExtendedPermissionEnum.DISSENY);
					resultat.getVies().add(via);
				} else {
					afegirViesProcedimentAnotacio(ctx, resultat, filtreElement, llistes, anotacio, procediment);
				}
				// Sense element: anotacions dels procediments permesos (unió de totes les vies, abans de la restricció)
				Filter filtreProcedimentsPermesos = anotacioPermisosFiltreHelper.filtreProcediments(llistes);
				if (ctx.elementId == null) {
					resultat.setTotalVies(filtreProcedimentsPermesos != null ? comptarAnotacions(filtreProcedimentsPermesos) : 0L);
				}
				SimulacioComprovacio restriccioGrups = restriccioGrupsAnotacio(ctx, filtreElement, llistes, anotacio, procediment);
				if (ctx.elementId == null) {
					// Per diferència, no amb un NOT del filtre (les anotacions sense grup quedarien fora del recompte)
					long compleixen = filtreProcedimentsPermesos != null
							? comptarAnotacions(FilterBuilder.and(filtreProcedimentsPermesos, anotacioPermisosFiltreHelper.filtreGestioGrups(llistes)))
							: 0L;
					restriccioGrups.setExclosos(Math.max(0L, resultat.getTotalVies() - compleixen));
				}
				resultat.getRestriccions().add(restriccioGrups);
			}
		}

		if (ctx.elementId != null && resultat.getErrorConsulta() == null) {
			boolean esperat = algunaOk(resultat.getVies()) && capKo(resultat.getRestriccions());
			resultat.setDiscrepancia(esperat != (resultat.getTotalReal() > 0));
		}
	}

	/**
	 * Restricció de gestió per grups: si el procediment gestiona per grups, el grup de l'anotació ha d'estar entre
	 * els grups amb READ de l'usuari. Una anotació sense grup en un procediment amb gestió per grups no la veu ningú
	 * que no sigui administrador: el que cal és assignar-li un grup, no donar permisos.
	 */
	private SimulacioComprovacio restriccioGrupsAnotacio(
			Context ctx,
			Filter filtreElement,
			PermisosPerAnotacions llistes,
			ExpedientPeticioEntity anotacio,
			MetaExpedientEntity procediment) {
		boolean gestioGrups = procediment != null && procediment.isGestioAmbGrupsActiva();
		Long grupId = anotacio != null && anotacio.getGrup() != null ? anotacio.getGrup().getId() : null;
		Map<String, String> parametres = anotacio != null
				? params("gestioGrups", String.valueOf(gestioGrups), "grup", buitSiNull(grupId != null ? descripcio(anotacio.getGrup()) : null))
				: params();
		SimulacioComprovacio restriccio = comprovacioComptada("ANO_RESTRICCIO_GRUPS",
				comptarAnotacions(FilterBuilder.and(filtreElement, anotacioPermisosFiltreHelper.filtreGestioGrups(llistes))),
				null,
				parametres);
		if (anotacio != null && gestioGrups && grupId == null) {
			String grupsProcediment = procediment.getGrups() != null
					? procediment.getGrups().stream().map(GrupEntity::getCodi).collect(Collectors.joining(", "))
					: "";
			parametres.put("grupsProcediment", buitSiNull(grupsProcediment.isEmpty() ? null : grupsProcediment));
			restriccio.setSuggerimentVariant(grupsProcediment.isEmpty() ? "SENSE_GRUP_NI_GRUPS" : "SENSE_GRUP");
		}
		ambPermisos(restriccio, anotacio != null ? "GRUP_ANOTACIO" : "GRUPS",
				permisosSobre(ctx, ClassType.GRUP, idsObjectes(ctx, grupId, llistes.getIdsGrupsPermesos())), false, ExtendedPermissionEnum.READ);
		return restriccio;
	}

	/**
	 * Usuari (tothom): procediments amb CREATE o WRITE per 5 vies. Cada via s'avalua amb la mateixa consulta de
	 * procediments permesos (MetaExpedientHelper.findAmbPermisosProcediments) activant només la seva llista.
	 */
	private void afegirViesProcedimentAnotacio(
			Context ctx,
			SimulacioPermisosResultat resultat,
			Filter filtreElement,
			PermisosPerAnotacions llistes,
			ExpedientPeticioEntity anotacio,
			MetaExpedientEntity procediment) {
		List<PermisosProcediments> perPermis = List.of(
				metaExpedientHelper.findPermisosProcediments(ctx.entitat, ExtendedPermission.CREATE, false),
				metaExpedientHelper.findPermisosProcediments(ctx.entitat, ExtendedPermission.WRITE, false));

		Map<String, java.util.function.Function<PermisosProcediments, PermisosProcediments>> vies = new LinkedHashMap<>();
		vies.put("ANO_VIA1_PROCEDIMENT", p -> PermisosProcediments.builder().metaExpedientIds(p.getMetaExpedientIds()).build());
		vies.put("ANO_VIA2_ORGAN", p -> PermisosProcediments.builder().organCodis(p.getOrganCodis()).build());
		vies.put("ANO_VIA3_PARELLA", p -> PermisosProcediments.builder().metaExpedientOrganIds(p.getMetaExpedientOrganIds()).build());
		vies.put("ANO_VIA4_COMUNS", p -> PermisosProcediments.builder().allComuns(p.isAllComuns()).build());
		vies.put("ANO_VIA5_GRUP", p -> PermisosProcediments.builder().grupsIds(p.getGrupsIds()).build());

		for (Map.Entry<String, java.util.function.Function<PermisosProcediments, PermisosProcediments>> via: vies.entrySet()) {
			Set<MetaExpedientEntity> procediments = new HashSet<>();
			int objectes = 0;
			for (PermisosProcediments permisos: perPermis) {
				PermisosProcediments nomesVia = via.getValue().apply(permisos);
				objectes = Math.max(objectes, midaVia(via.getKey(), nomesVia));
				if (teObjectes(nomesVia)) {
					procediments.addAll(metaExpedientHelper.findAmbPermisosProcediments(ctx.entitat, nomesVia, true, null, false, false, null, false));
				}
			}
			SimulacioComprovacio comprovacio;
			if (procediments.isEmpty()) {
				comprovacio = comprovacioComptada(via.getKey(), 0L, objectes, params());
			} else {
				PermisosPerAnotacions llista = new PermisosPerAnotacions();
				llista.setProcedimentsPermesos(new ArrayList<>(procediments));
				Filter filtreVia = anotacioPermisosFiltreHelper.filtreProcediments(llista);
				if ("ANO_VIA5_GRUP".equals(via.getKey())) {
					// Via 5 "efectiva": el grup que dona accés al procediment també ha de ser el de l'anotació si el
					// procediment gestiona per grups. És la mateixa restricció que s'aplica a totes les vies, però aquí
					// s'hi inclou perquè la via respongui si l'usuari veuria l'anotació gràcies als seus grups.
					filtreVia = FilterBuilder.and(filtreVia, anotacioPermisosFiltreHelper.filtreGestioGrups(llistes));
				}
				comprovacio = comprovacioComptada(via.getKey(),
						comptarAnotacions(FilterBuilder.and(filtreElement, filtreVia)),
						objectes,
						params());
			}
			if (procediment != null) {
				detallViaAnotacio(ctx, comprovacio, via.getKey(), procediment, anotacio, llistes);
			}
			resultat.getVies().add(comprovacio);
		}
	}

	/**
	 * Permisos, paràmetres i variant de suggeriment de cada via d'anotacions per al procediment de l'anotació. Les vies
	 * 2 (òrgan) i 3/4 (parella, comuns) són excloents segons si el procediment és comú (sense òrgan gestor).
	 */
	private void detallViaAnotacio(
			Context ctx,
			SimulacioComprovacio comprovacio,
			String via,
			MetaExpedientEntity procediment,
			ExpedientPeticioEntity anotacio,
			PermisosPerAnotacions llistes) {
		boolean comu = procediment.getOrganGestor() == null;
		switch (via) {
		case "ANO_VIA1_PROCEDIMENT":
			ambPermisos(comprovacio, "PROCEDIMENT", permisosSobre(ctx, ClassType.MET_NOD, List.of(procediment.getId())), false,
					ExtendedPermissionEnum.CREATE, ExtendedPermissionEnum.WRITE);
			break;
		case "ANO_VIA2_ORGAN":
			// El permís sobre un òrgan s'estén als seus descendents: interessen l'òrgan del procediment i els seus pares
			comprovacio.getParametres().put("comu", String.valueOf(comu));
			if (comu) {
				comprovacio.setSuggerimentVariant("COMU");
			} else {
				ambPermisos(comprovacio, "ORGANS_PROCEDIMENT",
						permisosSobre(ctx, ClassType.ORGAN, organGestorHelper.findParesIds(procediment.getOrganGestor().getId(), true)), false,
						ExtendedPermissionEnum.CREATE, ExtendedPermissionEnum.WRITE);
			}
			break;
		case "ANO_VIA3_PARELLA":
			comprovacio.getParametres().put("comu", String.valueOf(comu));
			if (!comu) {
				comprovacio.setSuggerimentVariant("NO_COMU");
			} else {
				ambPermisos(comprovacio, "PARELLES_PROCEDIMENT",
						permisosSobre(ctx, ClassType.MET_EXP_ORG, procediment.getMetaExpedientOrganGestors() != null
								? procediment.getMetaExpedientOrganGestors().stream().map(MetaExpedientOrganGestorEntity::getId).collect(Collectors.toList())
								: new ArrayList<>()), false,
						ExtendedPermissionEnum.CREATE, ExtendedPermissionEnum.WRITE);
			}
			break;
		case "ANO_VIA4_COMUNS":
			comprovacio.getParametres().put("comu", String.valueOf(comu));
			// La consulta de procediments permesos no filtra per entitat els òrgans amb COMU/ADM_COMU: s'indica si
			// l'usuari en té en òrgans d'altres entitats, perquè també li donen accés als comuns d'aquesta.
			Set<Long> organsEntitat = organsEntitat(ctx);
			List<PermisDetall> permisosComu = ctx.permisos.stream()
					.filter(p -> ClassType.ORGAN.equals(p.getTipus()))
					.filter(p -> p.getPermisos().contains(ExtendedPermissionEnum.COMU) || p.getPermisos().contains(ExtendedPermissionEnum.ADM_COMU))
					.collect(Collectors.toList());
			if (permisosComu.stream().anyMatch(p -> !organsEntitat.contains(p.getObjectId()))) {
				comprovacio.getParametres().put("comuAltraEntitat", "true");
			}
			if (!comu) {
				comprovacio.setSuggerimentVariant("NO_COMU");
			} else {
				ambPermisos(comprovacio, "ORGANS_COMU_ENTITAT",
						limitar(permisosComu.stream().filter(p -> organsEntitat.contains(p.getObjectId())).collect(Collectors.toList())), false,
						ExtendedPermissionEnum.COMU, ExtendedPermissionEnum.ADM_COMU, ExtendedPermissionEnum.CREATE, ExtendedPermissionEnum.WRITE);
			}
			break;
		case "ANO_VIA5_GRUP":
			detallVia5Anotacio(comprovacio, procediment, anotacio, llistes);
			ambPermisos(comprovacio, "GRUPS_PROCEDIMENT",
					permisosSobre(ctx, ClassType.GRUP, procediment.getGrups() != null
							? procediment.getGrups().stream().map(GrupEntity::getId).collect(Collectors.toList())
							: new ArrayList<>()), false,
					ExtendedPermissionEnum.READ);
			break;
		default:
			break;
		}
	}

	/**
	 * Via 5 efectiva d'anotacions: READ sobre algun grup del procediment (si no exigeix permís directe) i, si el
	 * procediment gestiona per grups, READ sobre el grup de l'anotació. Indica quins grups del procediment té l'usuari
	 * i, si no concedeix, la causa concreta: no té cap grup del procediment, l'anotació no té grup o no en té permís.
	 */
	private void detallVia5Anotacio(
			SimulacioComprovacio comprovacio,
			MetaExpedientEntity procediment,
			ExpedientPeticioEntity anotacio,
			PermisosPerAnotacions llistes) {
		Map<String, String> parametres = comprovacio.getParametres();
		boolean permisDirecte = procediment.isPermisDirecte();
		boolean gestioGrups = procediment.isGestioAmbGrupsActiva();
		Set<Long> grupsUsuari = llistes.getIdsGrupsPermesos() != null ? new HashSet<>(llistes.getIdsGrupsPermesos()) : new HashSet<>();
		List<GrupEntity> grupsProcediment = procediment.getGrups() != null ? procediment.getGrups() : new ArrayList<>();
		List<GrupEntity> grupsAmbPermis = grupsProcediment.stream().filter(g -> grupsUsuari.contains(g.getId())).collect(Collectors.toList());
		GrupEntity grupAnotacio = anotacio != null ? anotacio.getGrup() : null;

		parametres.put("permisDirecte", String.valueOf(permisDirecte));
		parametres.put("grupsProcediment", buitSiNull(codis(grupsProcediment)));
		parametres.put("grupsAmbPermis", buitSiNull(codis(grupsAmbPermis)));
		if (anotacio != null) {
			parametres.put("gestioGrups", String.valueOf(gestioGrups));
			if (gestioGrups) {
				parametres.put("grup", buitSiNull(grupAnotacio != null ? descripcio(grupAnotacio) : null));
			}
		}

		if (permisDirecte) {
			comprovacio.setSuggerimentVariant("PERMIS_DIRECTE");
		} else if (grupsProcediment.isEmpty()) {
			comprovacio.setSuggerimentVariant("SENSE_GRUPS_PROCEDIMENT");
		} else if (grupsAmbPermis.isEmpty()) {
			comprovacio.setSuggerimentVariant("SENSE_PERMIS_GRUPS_PROCEDIMENT");
		} else if (anotacio != null && gestioGrups && grupAnotacio == null) {
			comprovacio.setSuggerimentVariant("SENSE_GRUP_ANOTACIO");
		} else if (anotacio != null && gestioGrups && !grupsUsuari.contains(grupAnotacio.getId())) {
			comprovacio.setSuggerimentVariant("GRUP_ANOTACIO_SENSE_PERMIS");
		}
	}

	private static String codis(List<GrupEntity> grups) {
		return grups.isEmpty() ? null : grups.stream().map(GrupEntity::getCodi).collect(Collectors.joining(", "));
	}

	/** Ids dels òrgans de l'entitat simulada (es calcula una sola vegada per simulació). */
	private Set<Long> organsEntitat(Context ctx) {
		if (ctx.organsEntitat == null) {
			ctx.organsEntitat = organGestorRepository.findByEntitat(ctx.entitat).stream()
					.map(OrganGestorEntity::getId)
					.collect(Collectors.toSet());
		}
		return ctx.organsEntitat;
	}

	private static boolean teObjectes(PermisosProcediments p) {
		return isNotEmpty(p.getMetaExpedientIds()) || isNotEmpty(p.getOrganCodis()) || isNotEmpty(p.getMetaExpedientOrganIds())
				|| p.isAllComuns() || isNotEmpty(p.getGrupsIds());
	}

	private static int midaVia(String via, PermisosProcediments p) {
		switch (via) {
		case "ANO_VIA1_PROCEDIMENT": return mida(p.getMetaExpedientIds());
		case "ANO_VIA2_ORGAN": return mida(p.getOrganCodis());
		case "ANO_VIA3_PARELLA": return mida(p.getMetaExpedientOrganIds());
		case "ANO_VIA4_COMUNS": return p.isAllComuns() ? 1 : 0;
		case "ANO_VIA5_GRUP": return mida(p.getGrupsIds());
		default: return 0;
		}
	}

	/** Anotacions de l'entitat simulada que compleixen el filtre (sense la part de permisos del llistat). */
	private long comptarAnotacions(Filter filtre) {
		return expedientPeticioResourceService.findPage(
				null,
				filtre != null ? filtre.generate() : null,
				null,
				null,
				PageRequest.of(0, 1)).getTotalElements();
	}

	// =========================================================================================
	// Identitat simulada
	// =========================================================================================

	/**
	 * Executa l'acció amb la identitat de l'usuari simulat: el seu codi, tots els seus rols de Keycloak, tothom
	 * i el rol triat (les mateixes authorities que tindria a la sessió), i amb l'entitat, el rol i l'òrgan
	 * simulats al context de ConfigHelper. Restaura sempre el context original de la petició del superusuari.
	 */
	private <T> T ambIdentitat(String usuariCodi, String rol, EntitatEntity entitat, OrganGestorEntity organ, Supplier<T> accio) {
		SecurityContext contextOriginal = SecurityContextHolder.getContext();
		EntitatDto entitatOriginal = ConfigHelper.getEntitat().get();
		String rolOriginal = configHelper.getRolActual();
		String organOriginal = ConfigHelper.getOrganCodi().get();
		try {
			Set<String> rols = new LinkedHashSet<>(findRolsKeycloak(usuariCodi));
			rols.add(BaseConfig.ROLE_USER);
			if (rol != null) {
				rols.add(rol);
			}
			List<GrantedAuthority> authorities = rols.stream()
					.map(SimpleGrantedAuthority::new)
					.collect(Collectors.toList());
			// Context nou (no es modifica el de la sessió del superusuari, que podria estar compartit)
			SecurityContext contextSimulat = SecurityContextHolder.createEmptyContext();
			contextSimulat.setAuthentication(new UsernamePasswordAuthenticationToken(new User(usuariCodi, "", authorities), null, authorities));
			SecurityContextHolder.setContext(contextSimulat);
			if (entitat != null) {
				ConfigHelper.setEntitat(conversioTipusHelper.convertir(entitat, EntitatDto.class));
				ConfigHelper.setRol(rol);
				ConfigHelper.setOrganCodi(organ != null ? organ.getCodi() : null);
			}
			return accio.get();
		} finally {
			SecurityContextHolder.setContext(contextOriginal);
			ConfigHelper.setEntitat(entitatOriginal);
			ConfigHelper.setRol(rolOriginal);
			ConfigHelper.setOrganCodi(organOriginal);
		}
	}

	private Set<String> findRolsKeycloak(String usuariCodi) {
		try {
			return permisosHelper.findRolsAclUsuari(usuariCodi);
		} catch (Exception ex) {
			log.warn("Simulador de permisos: no s'han pogut consultar els rols de l'usuari {}", usuariCodi, ex);
			return Collections.emptySet();
		}
	}

	// =========================================================================================
	// Utilitats
	// =========================================================================================

	/** Permís sobre l'entitat que exigeix cada rol per aparèixer a la capçalera (RolHelper.getRolsUsuariActual). */
	private static Permission permisEntitatRol(String rol) {
		switch (rol) {
		case BaseConfig.ROLE_ADMIN: return ExtendedPermission.ADMINISTRATION;
		case BaseConfig.ROLE_ADMIN_LECTURA: return ExtendedPermission.ADMINISTRATION_READ;
		case BaseConfig.ROLE_DISSENY:
		case BaseConfig.ROLE_USER: return ExtendedPermission.READ;
		default: return null;
		}
	}

	private static ExtendedPermissionEnum permisOrganCapcaleraEnum(String rol) {
		return BaseConfig.ROLE_DISSENY.equals(rol) ? ExtendedPermissionEnum.DISSENY : ExtendedPermissionEnum.ADMINISTRATION;
	}

	/** Permís sobre l'òrgan que fa que aparegui a la capçalera (EntitatHelper.findOrganismesEntitatAmbPermisCacheByRol). */
	private static Permission permisOrganCapcalera(String rol) {
		return BaseConfig.ROLE_DISSENY.equals(rol) ? ExtendedPermission.DISSENY : ExtendedPermission.ADMINISTRATION;
	}

	private static String nomPermis(Permission permis) {
		if (permis == ExtendedPermission.ADMINISTRATION) return ExtendedPermissionEnum.ADMINISTRATION.name();
		if (permis == ExtendedPermission.ADMINISTRATION_READ) return ExtendedPermissionEnum.ADMINISTRATION_READ.name();
		if (permis == ExtendedPermission.DISSENY) return ExtendedPermissionEnum.DISSENY.name();
		if (permis == ExtendedPermission.READ) return ExtendedPermissionEnum.READ.name();
		return String.valueOf(permis);
	}

	/** Permisos de l'usuari o dels seus rols sobre els objectes indicats. */
	private static List<PermisDetall> permisosSobre(Context ctx, ClassType tipus, Collection<Long> objectIds) {
		if (objectIds == null || objectIds.isEmpty()) {
			return new ArrayList<>();
		}
		Set<Long> ids = new HashSet<>(objectIds);
		return limitar(ctx.permisos.stream()
				.filter(p -> tipus.equals(p.getTipus()) && ids.contains(p.getObjectId()))
				.collect(Collectors.toList()));
	}

	private static List<PermisDetall> limitar(List<PermisDetall> permisos) {
		return permisos.size() > MAX_PERMISOS_COMPROVACIO ? new ArrayList<>(permisos.subList(0, MAX_PERMISOS_COMPROVACIO)) : permisos;
	}

	/**
	 * Objectes dels quals es mostren els permisos. Amb element, només el de l'element (si l'element no en té, cap:
	 * p. ex. un expedient sense grup no ha de mostrar els grups de l'usuari); sense element, tots els de la via.
	 */
	private static List<Long> idsObjectes(Context ctx, Long idObjecteElement, List<Long> llistaVia) {
		if (ctx.elementId != null) {
			return idObjecteElement != null ? List.of(idObjecteElement) : new ArrayList<>();
		}
		return llistaVia != null ? llistaVia : new ArrayList<>();
	}

	/** Adjunta els permisos de la comprovació amb l'objecte al qual es refereixen i els permisos que demana. */
	private static void ambPermisos(SimulacioComprovacio comprovacio, String objecte, List<PermisDetall> permisos, boolean tots, ExtendedPermissionEnum... requerits) {
		comprovacio.setPermisos(permisos);
		comprovacio.setPermisosObjecte(objecte);
		comprovacio.setPermisosRequerits(List.of(requerits));
		comprovacio.setPermisosRequeritsTots(tots);
	}

	/** Valor que l'element no té (p. ex. un expedient sense grup): es mostra com a "(sense ...)" en lloc d'amagar-lo. */
	private static String buitSiNull(String valor) {
		return valor != null ? valor : BUIT;
	}

	private static SimulacioComprovacio comprovacio(String codi, SimulacioEstat estat, Map<String, String> parametres) {
		SimulacioComprovacio comprovacio = new SimulacioComprovacio();
		comprovacio.setCodi(codi);
		comprovacio.setEstat(estat);
		comprovacio.setParametres(parametres);
		return comprovacio;
	}

	private static SimulacioComprovacio comprovacioComptada(String codi, long nombre, Integer nombreObjectes, Map<String, String> parametres) {
		SimulacioComprovacio comprovacio = comprovacio(codi, nombre > 0 ? SimulacioEstat.OK : SimulacioEstat.KO, parametres);
		comprovacio.setNombre(nombre);
		comprovacio.setNombreObjectes(nombreObjectes);
		return comprovacio;
	}

	private static Map<String, String> params(String... clauValor) {
		Map<String, String> parametres = new LinkedHashMap<>();
		for (int i = 0; i + 1 < clauValor.length; i += 2) {
			if (clauValor[i + 1] != null) {
				parametres.put(clauValor[i], clauValor[i + 1]);
			}
		}
		return parametres;
	}

	private static boolean algunaOk(List<SimulacioComprovacio> comprovacions) {
		return comprovacions.stream().anyMatch(c -> SimulacioEstat.OK.equals(c.getEstat()));
	}

	private static boolean capKo(List<SimulacioComprovacio> comprovacions) {
		return comprovacions.stream().noneMatch(c -> SimulacioEstat.KO.equals(c.getEstat()));
	}

	private static int mida(Collection<?> llista) {
		return llista != null ? llista.size() : 0;
	}

	private static boolean isNotEmpty(Collection<?> llista) {
		return llista != null && !llista.isEmpty();
	}

	private static Long toLong(String valor) {
		try {
			return valor != null && !valor.isEmpty() ? Long.valueOf(valor) : null;
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	private static String descripcio(OrganGestorEntity organ) {
		return organ != null ? organ.getCodi() + " - " + organ.getNom() : null;
	}

	private static String descripcio(MetaExpedientEntity procediment) {
		return procediment != null ? Objects.toString(procediment.getClassificacio(), "") + " - " + procediment.getNom() : null;
	}

	private static String descripcio(GrupEntity grup) {
		return grup != null ? grup.getCodi() + " - " + grup.getDescripcio() : null;
	}

	private static String missatge(Exception ex) {
		return ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
	}

	/** Dades de la simulació en curs. */
	private static class Context {
		String usuariCodi;
		String rol;
		EntitatEntity entitat;
		OrganGestorEntity organ;
		Long elementId;
		Set<String> rolsKeycloak;
		List<PermisDetall> permisos;
		Set<Long> organsEntitat;
	}
}
