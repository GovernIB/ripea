
/**
 * 
 */
package es.caib.ripea.core.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import es.caib.distribucio.rest.client.integracio.domini.DocumentTipus;
import es.caib.distribucio.rest.client.integracio.domini.FirmaTipus;
import es.caib.distribucio.rest.client.integracio.domini.InteressatTipus;
import es.caib.distribucio.rest.client.integracio.domini.NtiEstadoElaboracion;
import es.caib.distribucio.rest.client.integracio.domini.NtiOrigen;
import es.caib.distribucio.rest.client.integracio.domini.NtiTipoDocumento;
import es.caib.plugins.arxiu.api.Carpeta;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.ContingutTipus;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.Expedient;
import es.caib.plugins.arxiu.caib.ArxiuConversioHelper;
import es.caib.ripea.core.api.dto.ArxiuEstatEnumDto;
import es.caib.ripea.core.api.dto.CarpetaDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiTipoFirmaEnumDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientEstatDto;
import es.caib.ripea.core.api.dto.ExpedientEstatEnumDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.InteressatAdministracioDto;
import es.caib.ripea.core.api.dto.InteressatAssociacioAccioEnum;
import es.caib.ripea.core.api.dto.InteressatDocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.dto.InteressatPersonaFisicaDto;
import es.caib.ripea.core.api.dto.InteressatPersonaJuridicaDto;
import es.caib.ripea.core.api.dto.InteressatTipusEnumDto;
import es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaExpedientCarpetaDto;
import es.caib.ripea.core.api.dto.MultiplicitatEnumDto;
import es.caib.ripea.core.api.dto.NtiOrigenEnumDto;
import es.caib.ripea.core.api.dto.PermisosPerExpedientsDto;
import es.caib.ripea.core.api.dto.PermissionEnumDto;
import es.caib.ripea.core.api.dto.PrioritatEnumDto;
import es.caib.ripea.core.api.dto.RegistreAnnexEstatEnumDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.exception.ArxiuJaGuardatException;
import es.caib.ripea.core.api.exception.InteressatTipusDocumentException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.entity.CarpetaEntity;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DadaEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientEstatEntity;
import es.caib.ripea.core.entity.ExpedientPeticioEntity;
import es.caib.ripea.core.entity.GrupEntity;
import es.caib.ripea.core.entity.InteressatAdministracioEntity;
import es.caib.ripea.core.entity.InteressatEntity;
import es.caib.ripea.core.entity.InteressatPersonaFisicaEntity;
import es.caib.ripea.core.entity.InteressatPersonaJuridicaEntity;
import es.caib.ripea.core.entity.MetaDadaEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.entity.RegistreAnnexEntity;
import es.caib.ripea.core.entity.RegistreInteressatEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.repository.AlertaRepository;
import es.caib.ripea.core.repository.CarpetaRepository;
import es.caib.ripea.core.repository.ContingutRepository;
import es.caib.ripea.core.repository.DadaRepository;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.ExpedientEstatRepository;
import es.caib.ripea.core.repository.ExpedientPeticioRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.InteressatRepository;
import es.caib.ripea.core.repository.MetaDadaRepository;
import es.caib.ripea.core.repository.MetaDocumentRepository;
import es.caib.ripea.core.repository.MetaExpedientRepository;
import es.caib.ripea.core.repository.OrganGestorRepository;
import es.caib.ripea.core.repository.RegistreAnnexRepository;
import es.caib.ripea.core.security.ExtendedPermission;

/**
 * Mètodes comuns per a la gestió d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ExpedientHelper {

	@Autowired
	private ExpedientEstatRepository expedientEstatRepository;
	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private CarpetaRepository carpetaRepository;
	@Autowired
	private ExpedientRepository expedientRepository;
	@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private ExpedientPeticioRepository expedientPeticioRepository;
	@Autowired
	private RegistreAnnexRepository registreAnnexRepository;
	@Autowired
	private OrganGestorRepository organGestorRepository;
	@Autowired
	private CarpetaHelper carpetaHelper;
	@Autowired
	private DocumentHelper documentHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private ContingutHelper contingutHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private UsuariHelper usuariHelper;
	@Autowired
	private EmailHelper emailHelper;
	@Autowired
	private ExpedientInteressatHelper expedientInteressatHelper;
	@Autowired
	private MetaDadaRepository metaDadaRepository;
	@Autowired
	private DadaRepository dadaRepository;
	@Autowired
	private ContingutLogHelper contingutLogHelper;
	@Autowired
	private MetaExpedientCarpetaHelper metaExpedientCarpetaHelper;
	@Autowired
	private OrganGestorHelper organGestorHelper;
	@Autowired
	private ContingutRepository contingutRepository;
	@Autowired
	private MessageHelper messageHelper;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private MetaDocumentRepository metaDocumentRepository;
	@Autowired
	private AlertaRepository alertaRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private ExpedientPeticioHelper expedientPeticioHelper;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private MetaExpedientRepository metaExpedientRepository;
	@Autowired
	private InteressatRepository interessatRepository;
	@Autowired
	private ExpedientHelper2 expedientHelper2;
	@Autowired
	private OrganGestorCacheHelper organGestorCacheHelper;
	
	public static List<DocumentDto> expedientsWithImportacio = new ArrayList<DocumentDto>();

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Long create(
			Long entitatId,
			Long metaExpedientId,
			Long metaExpedientDominiId,
			Long organGestorId,
			Integer any,
			String nom,
			Long expedientPeticioId,
			boolean associarInteressats,
			Map<String, InteressatAssociacioAccioEnum> interessatsAccionsMap,
			Long grupId,
			String rolActual,
			PrioritatEnumDto prioritat,
			String prioritatMotiu) {

		logger.info(
				"Expedient crear Helper START(" +
						"entitatId=" + entitatId + ", " +
						"metaExpedientId=" + metaExpedientId + ", " +
						"metaExpedientDominiId=" + metaExpedientDominiId + ", " +
						"organGestorId=" + organGestorId + ", " +
						"any=" + any + ", " +
						"nom=" + nom + ", " +
						"expedientPeticioId=" + expedientPeticioId + ")");


//		try {
//			Thread.sleep(5000L);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

		if (metaExpedientId == null) {
			throw new ValidationException(
					"<creacio>",
					ExpedientEntity.class,
					"No es pot crear un expedient sense un meta-expedient associat");
		}
		if (expedientPeticioId != null) {
			ExpedientPeticioEntity expedientPeticio = expedientPeticioRepository.findOne(expedientPeticioId);
			if (expedientPeticio.getExpedient() != null) {
 				throw new ValidationException(
						"<creacio>",
						ExpedientEntity.class,
						"Aquesta anotació ja està relacionada amb algun expedient");
			}
		}
		boolean exists = checkIfExistsByMetaExpedientAndNom(
				metaExpedientId,
				nom) != null;
		if (exists) {
			throw new ValidationException(
					"<creacio>",
					ExpedientEntity.class,
					"Ja existeix un altre expedient amb el mateix títol per aquest procediment");
		}

		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false,
				true,
				false);
		
		entityComprovarHelper.comprovarPermisExpedientCreation(
				metaExpedientId,
				organGestorId, 
				grupId, 
				rolActual);
		
		MetaExpedientEntity metaExpedient = metaExpedientRepository.findOne(metaExpedientId);
		
		OrganGestorEntity organGestor = getOrganGestorForExpedient(
				metaExpedient,
				organGestorId,
				ExtendedPermission.CREATE, 
				rolActual);

		ExpedientEntity expedient = contingutHelper.crearNouExpedient(
				nom,
				metaExpedient,
				null,
				metaExpedient.getEntitat(),
				organGestor,
				"1.0",
				metaExpedient.getEntitat().getUnitatArrel(),
				new Date(),
				any,
				true,
				grupId,
				prioritat,
				prioritatMotiu);
		contingutLogHelper.logCreacio(expedient, false, false);
		crearDadesPerDefecte(
				metaExpedient,
				expedient);
		List<ExpedientEstatEntity> expedientEstats = expedientEstatRepository.findByMetaExpedientOrderByOrdreAsc(expedient.getMetaExpedient());
		// find inicial state if exists
		ExpedientEstatEntity estatInicial = null;
		for (ExpedientEstatEntity expedientEstat : expedientEstats) {
			if (expedientEstat.isInicial()) {
				estatInicial = expedientEstat;
			}
		}
		// set inicial estat if exists
		if (estatInicial != null) {
			expedient.updateEstatAdditional(estatInicial);
			// if estat has usuari responsable agafar expedient by this user
			if (estatInicial.getResponsableCodi() != null) {
				agafar(expedient, estatInicial.getResponsableCodi());
				
			}
		}
		// Crea les relacions expedients i organs pare
		organGestorHelper.crearExpedientOrganPares(
				expedient,
				organGestor);
		
		// if expedient comes from distribucio
		if (expedientPeticioId != null) {
			relateExpedientWithPeticioAndSetAnnexosPendent(expedientPeticioId, expedient.getId());
			if (associarInteressats) {
				associateInteressats(
						expedient.getId(),
						expedientPeticioId,
						PermissionEnumDto.CREATE,
						rolActual,
						interessatsAccionsMap);
			}
			ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
			expedientPeticioHelper.canviEstatExpedientPeticio(expedientPeticioEntity, ExpedientPeticioEstatEnumDto.PROCESSAT_PENDENT);
		}
		// crear carpetes per defecte del procediment
		crearCarpetesMetaExpedient(entitatId, metaExpedient, expedient);
		
		boolean throwExcepcion = false;//throwExcepcion = true;
		if (throwExcepcion) {
			throw new RuntimeException("Mock excepcion al crear expedient");
		}
		
		logger.info(
				"Expedient crear Helper END(" +
						"sequencia=" + expedient.getSequencia() + ", " +
						"any=" + expedient.getAny() + ", " +
						"metaExpedient=" + expedient.getMetaExpedient().getId() + " - " + expedient.getMetaExpedient().getCodi() + ")");

		return expedient.getId();
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public boolean arxiuPropagarExpedientAmbInteressatsNewTransaction(
			Long expedientId) {
		return arxiuPropagarExpedientAmbInteressats(expedientId);
	}
	
	public boolean arxiuPropagarExpedientAmbInteressats(
			Long expedientId) {

		if (cacheHelper.mostrarLogsCreacioContingut())
			logger.info(
				"Expedient crear ARXIU Helper START(" +
						"expedientId=" + expedientId + ")");
		ExpedientEntity expedient = expedientRepository.findOne(expedientId);

		Exception exception = expedientInteressatHelper.arxiuPropagarInteressats(expedient, null);
		
		expedient.updateArxiuIntent();
		
		boolean throwExcepcion = false;//throwExcepcion = true;
		if (throwExcepcion) {
			throw new RuntimeException("Mock excepcion després de crear expedient en arxiu");
		}
		if (cacheHelper.mostrarLogsCreacioContingut())
			logger.info(
					"Expedient crear ARXIU Helper END(" +
							"sequencia=" + expedient.getSequencia() + ", " +
							"any=" + expedient.getAny() + ", " +
							"metaExpedient=" + expedient.getMetaExpedient().getId() + " - " + expedient.getMetaExpedient().getCodi() + ")");
			
		return exception == null;
	}

	@Transactional
	public void associateInteressats(
			Long expedientId,
			Long expedientPeticioId,
			PermissionEnumDto permission,
			String rolActual,
			Map<String, InteressatAssociacioAccioEnum> interessatsAccionsMap) {
		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		ExpedientEntity expedientEntity = expedientRepository.findOne(expedientId);
		Map<String, InteressatDto> interessatsOvewritten = getInteressatsOverwritten(expedientEntity, expedientPeticioEntity.getRegistre().getInteressats());

		for (RegistreInteressatEntity interessatDistribucio : expedientPeticioEntity.getRegistre().getInteressats()) {

			// Si l'interessat no té document, no s'incorpora a l'expedient
			if (interessatDistribucio.getDocumentNumero() == null) {
				continue;
			}
			// Si hi ha algun problema obtenint l'acció a realitzar, per defecte s'asocia l'interessat
			InteressatAssociacioAccioEnum accioARealitzar = interessatsAccionsMap.get(interessatDistribucio.getDocumentNumero());
			if (accioARealitzar == null) {
				accioARealitzar = InteressatAssociacioAccioEnum.ASSOCIAR;
			}
			RegistreInteressatEntity representantDistribucio = interessatDistribucio.getRepresentant();
			boolean hasRepresentantDistribucio = representantDistribucio != null;

			InteressatDto interessatOverwritten = interessatsOvewritten.get(interessatDistribucio.getDocumentNumero());
			InteressatDto representantOverwritten = hasRepresentantDistribucio ? interessatsOvewritten.get(representantDistribucio.getDocumentNumero()) : null;

			switch (accioARealitzar) {
				case ASSOCIAR:
					associarInteressat(
							expedientId,
							permission,
							rolActual,
							interessatDistribucio,
							interessatOverwritten,
							hasRepresentantDistribucio,
							representantOverwritten,
							representantDistribucio);
					break;
//					if (interessatsOvewritten.get(interessatDistribucio.getDocumentNumero()) != null) {
//						throw new InteressatAssociarException("No es pot associar el nou interessat amb document número " + interessatDistribucio.getDocumentNumero() + " degut a que ja existeix un interessat amb el mateix document.");
//					}
//					if (representantDistribucio != null && interessatsOvewritten.get(representantDistribucio.getDocumentNumero()) != null) {
//						throw new InteressatAssociarException("No es pot associar el nou representant amb document número " + representantDistribucio.getDocumentNumero() + " degut a que ja existeix un interessat o representant amb el mateix document.");
//					}
//					crearInteressatIRepresentant(expedientId, entitatId, permission, rolActual, interessatDistribucio, interessatsOvewritten);
//					break;
//				case SOBREESCRIURE:
//					if (interessatsOvewritten.get(interessatDistribucio.getDocumentNumero()) == null) {
//						throw new InteressatAssociarException("No es actualitzar l'interessat amb document número " + interessatDistribucio.getDocumentNumero() + " degut a no existeix cap interessat amb aquest document a l'expedient.");
//					}
//					updateInteressat();
//					if (representantDistribucio != null) {
//						if (interessatsOvewritten.get(representantDistribucio.getDocumentNumero()) == null) {
//							throw new InteressatAssociarException("No es actualitzar el representant amb document número " + representantDistribucio.getDocumentNumero() + " degut a no existeix cap interessat o representant amb aquest document a l'expedient.");
//						}
//						updateRepresentant(entitatId, interessatsOvewritten.get(representantDistribucio.getDocumentNumero()));
//					}
//					break;
//				case SOBREESCRIURE_REPRESENTANT:
//					if (interessatsOvewritten.get(interessatDistribucio.getDocumentNumero()) == null) {
//						throw new InteressatAssociarException("No es actualitzar l'interessat amb document número " + interessatDistribucio.getDocumentNumero() + " degut a no existeix cap interessat amb aquest document a l'expedient.");
//					}
//					if (representantDistribucio == null) {
//						throw new InteressatAssociarException("");
//					}
//					updateInteressat();
//					if (isRepresentantUsedOnce) {
//						deleteRepresentant();
//					}
//					createRepresentant(expedientId, entitatId, permission, rolActual, interessatDistribucio, interessatsOvewritten, interessatDto);
//					break;
//				case ASSOCIAR_SOBREESCRIURE_REPRESENTANT:
//					if (interessatsOvewritten.get(interessatDistribucio.getDocumentNumero()) != null) {
//						throw new InteressatAssociarException("No es pot associar el nou interessat amb document número " + interessatDistribucio.getDocumentNumero() + " degut a que ja existeix un interessat amb el mateix document.");
//					}
//					if (representantDistribucio == null || interessatsOvewritten.get(representantDistribucio.getDocumentNumero()) == null) {
//						throw new InteressatAssociarException("No es actualitzar el representant amb document número " + representantDistribucio.getDocumentNumero() + " degut a no existeix cap interessat o representant amb aquest document a l'expedient.");
//					}
//					createInteressat(expedientId, entitatId, permission, rolActual, interessatDistribucio, interessatsOvewritten);
//					updateRepresentant();
//					break;
//				// Si s'ha seleccionat NO_ASSOCIAR, no associarem l'interessat a l'expedient
				case NO_ASSOCIAR:
				default:
					continue;
			}

//			InteressatDto interessatOvewritten = interessatsOvewritten.get(interessatDistribucio.getDocumentNumero());
//			InteressatDto representantOvewritten = null;
//			if (representantDistribucio != null) {
//				representantOvewritten = interessatsOvewritten.get(representantDistribucio.getDocumentNumero());
//			}
//
//			if (interessatOvewritten != null || representantOvewritten != null) {
//				InteressatEntity interessatRipea = getInteressatOvewritten(
//						expedientId,
//						interessatOvewritten != null ? interessatOvewritten.getDocumentNum() : null,
//						representantOvewritten != null ? representantOvewritten.getDocumentNum() : null);
//				if (interessatRipea != null) {
//					if (interessatRipea.getRepresentant() != null) {
//						expedientInteressatHelper.deleteRepresentant(
//								entitatId,
//								expedientId,
//								interessatRipea.getId(),
//								interessatRipea.getRepresentant().getId(),
//								rolActual);
//					}
//					expedientInteressatHelper.delete(
//							entitatId,
//							expedientId,
//							interessatRipea.getId(),
//							rolActual);
//				}
//			}
//
//			crearInteressatIRepresentant(expedientId, entitatId, permission, rolActual, interessatDistribucio, interessatsOvewritten);
		}
	}

	private void associarInteressat(
			Long expedientId,
			PermissionEnumDto permission,
			String rolActual,
			RegistreInteressatEntity interessatDistribucio,
			InteressatDto interessatOverwritten,
			boolean hasRepresentantDistribucio,
			InteressatDto representantOverwritten,
			RegistreInteressatEntity representantDistribucio) {
		InteressatEntity interessat = null;
		 // L'interessat ja existeix: actualitzam les seves dades
		if (interessatOverwritten != null) {
			interessat = updateInteressat(expedientId, permission, rolActual, interessatOverwritten);
		// L'interessat no existeix: cream el nou interessat
		} else {
			interessat = interessatRepository.findByExpedientIdAndDocumentNum(expedientId, interessatDistribucio.getDocumentNumero());
			if (interessat == null)
				interessat = createInteressat(expedientId, permission, rolActual, interessatDistribucio);
		}

		// L'interessat que estam associant té un representant
		if (hasRepresentantDistribucio) {
			if (representantOverwritten != null) {
				if (distinctDocNum(interessat.getDocumentNum(),representantOverwritten.getDocumentNum())) {
					updateRepresentant(expedientId, interessat.getId(), permission, rolActual, representantOverwritten);
				}
			} else {
				if (representantDistribucio != null 
						&& distinctDocNum(interessat.getDocumentNum(), representantDistribucio.getDocumentNumero())) {
					InteressatEntity representant = interessatRepository.findByExpedientIdAndDocumentNum(expedientId, representantDistribucio.getDocumentNumero());
					if (representant == null) {
						createRepresentant(expedientId, interessat.getId(), permission, rolActual, representantDistribucio);
					} else {
						interessat.updateRepresentant(representant);
					}
				}
			}
		// L'interessat que estam associant no té representant
		} else {
			// L'interessat existent té un representant assignat
			if (interessat.getRepresentant() != null) {
				// Treure representant de l'interessat, i borrar-lo de BBDD si és necessari
				expedientInteressatHelper.removeRepresentant(interessat);
			}
		}
	}

	private boolean distinctDocNum(String interessatDocNum, String representantDocNum) {
		if (interessatDocNum == null && representantDocNum == null) return false;
		if (interessatDocNum != null) return !interessatDocNum.equalsIgnoreCase(representantDocNum);
		return !representantDocNum.equalsIgnoreCase(interessatDocNum);
	}

	private InteressatEntity createInteressat(Long expedientId, PermissionEnumDto permission, String rolActual, RegistreInteressatEntity interessatDistribucio) {
		InteressatEntity createdInteressat = expedientInteressatHelper.create(
				expedientId,
				toInteressatDto(interessatDistribucio, null),
				permission,
				rolActual);
		return createdInteressat;
	}

	private void createRepresentant(Long expedientId, Long interessatId, PermissionEnumDto permission, String rolActual, RegistreInteressatEntity representantDistribucio) {
		expedientInteressatHelper.createRepresentant(
				expedientId,
				interessatId,
				toInteressatDto(representantDistribucio, null),
				permission,
				rolActual);
	}

	private InteressatEntity updateInteressat(Long expedientId, PermissionEnumDto permission, String rolActual, InteressatDto interessatOverwritten) {
//		InteressatEntity createdInteressat = expedientInteressatHelper.update(
//				expedientId,
//				interessatOverwritten,
//				permission,
//				rolActual);
		InteressatEntity createdInteressat = expedientInteressatHelper.updateInteressatRepresentantEntity(
				expedientId,
				null,
				interessatOverwritten,
				rolActual,
				false,
				false);
		return createdInteressat;

	}

	private void updateRepresentant(Long expedientId, Long interessatId, PermissionEnumDto permission, String rolActual, InteressatDto representantOverwritten) {
//		expedientInteressatHelper.updateRepresentant(
//				expedientId,
//				interessatId,
//				representantOverwritten,
//				permission,
//				rolActual);
		expedientInteressatHelper.updateInteressatRepresentantEntity(
				expedientId,
				interessatId,
				representantOverwritten,
				rolActual,
				false,
				false);
	}


//	private void updateInteressat(ExpedientEntity expedient, Long entitatId, InteressatDto interessat, String rolActual) {
//		InteressatEntity interessatEntity = interessatRepository.findByExpedientAndDocumentNum(expedient, interessat.getDocumentNum());
//		if (!sameTipusInteressat(interessat, interessatEntity)) {
//			throw new InteressatTipusDocumentException(
//					interessat.getDocumentNum(),
//					getTipusInteressat(interessatEntity).name(),
//					interessat.getTipus().name(),
//					expedient.getId());
//		}
//		expedientInteressatHelper.update(entitatId, expedient.getId(), null, interessat, rolActual, false, false);
//	}
//
//	private void crearInteressatIRepresentant(Long expedientId, Long entitatId, PermissionEnumDto permission, String rolActual, RegistreInteressatEntity interessatDistribucio, Map<String, InteressatDto> interessatsOvewritten) {
//		InteressatDto createdInteressat = createInteressat(expedientId, entitatId, permission, rolActual, interessatDistribucio, interessatsOvewritten);
//		if (interessatDistribucio.getRepresentant() != null) {
//			createRepresentant(expedientId, entitatId, permission, rolActual, interessatDistribucio, interessatsOvewritten, createdInteressat);
//		}
//	}

//	private void createRepresentant(Long expedientId, Long entitatId, PermissionEnumDto permission, String rolActual, RegistreInteressatEntity interessatDistribucio, Map<String, InteressatDto> interessatsOvewritten, InteressatDto createdInteressat) {
//		expedientInteressatHelper.createRepresentant(
//				expedientId,
//				createdInteressat.getId(),
//				interessatsOvewritten.containsKey(interessatDistribucio.getRepresentant().getDocumentNumero()) ?
//						interessatsOvewritten.get(interessatDistribucio.getRepresentant().getDocumentNumero()) :
//						toInteressatDto(interessatDistribucio.getRepresentant(), null),
//				false,
//				permission,
//				rolActual,
//				false);
//	}
//
//	private InteressatDto createInteressat(Long expedientId, Long entitatId, PermissionEnumDto permission, String rolActual, RegistreInteressatEntity interessatDistribucio, Map<String, InteressatDto> interessatsOvewritten) {
//		InteressatDto createdInteressat = expedientInteressatHelper.create(
//				expedientId,
//				interessatsOvewritten.containsKey(interessatDistribucio.getDocumentNumero()) ?
//						interessatsOvewritten.get(interessatDistribucio.getDocumentNumero()) :
//						toInteressatDto(interessatDistribucio, null),
//				false,
//				permission,
//				rolActual,
//				false);
//		return createdInteressat;
//	}

//	private InteressatEntity getInteressatOvewritten(Long expedientId, String interessatNumDocument, String representantNumDocument) {
//		if (interessatNumDocument != null) {
//			InteressatEntity interessat = interessatRepository.findByExpedientIdAndDocumentNum(expedientId, interessatNumDocument);
//			if (interessat != null) {
//				return interessat;
//			}
//		}
//		if (representantNumDocument != null) {
//			InteressatEntity representant = interessatRepository.findByExpedientIdAndRepresentantDocumentNum(expedientId, representantNumDocument);
//			if (representant != null) {
//				return representant;
//			}
//		}
//		return null;
//	}

//	private Map<String, InteressatEntity> convertInteressatListToMap(Set<InteressatEntity> interessatsORepresenantsRipea) {
//		Map<String, InteressatEntity> result = new HashMap<>();
//		if (interessatsORepresenantsRipea != null) {
//			for (InteressatEntity interessat : interessatsORepresenantsRipea) {
//				result.put(interessat.getDocumentNum(), interessat);
//			}
//		}
//		return result;
//	}

	public Map<String, InteressatDto> getInteressatsOverwritten(ExpedientEntity expedientEntity, List<RegistreInteressatEntity> interessatsDistribucio) {

		Map<String, InteressatDto> interessatsOverwritten = new HashMap<>();

		Set<InteressatEntity> interessatsORepresenantsRipea = expedientEntity.getInteressatsORepresentants();
		if (interessatsORepresenantsRipea == null || interessatsORepresenantsRipea.isEmpty()) {
			return interessatsOverwritten;
		}

		Set<RegistreInteressatEntity> interessatsOrRepresentantsDistribucio = getInteressatOrRepresentantsDistribucio(interessatsDistribucio);

		boolean sobreescriureTipus = configHelper.getAsBoolean("es.caib.ripea.interessats.permet.canvi.tipus");


		for (InteressatEntity interessatRipea : interessatsORepresenantsRipea) {
			for (RegistreInteressatEntity interessatDistribucio : interessatsOrRepresentantsDistribucio) {
				if (interessatRipea.getDocumentNum().equals(interessatDistribucio.getDocumentNumero())) {

					if (!sobreescriureTipus && !sameTipusInteressat(interessatDistribucio.getTipus(), interessatRipea)) {
						throw new InteressatTipusDocumentException(
								interessatRipea.getDocumentNum(),
								interessatRipea.getTipus().name(),
								interessatDistribucio.getTipus().name(),
								expedientEntity.getId());
					}
                    interessatsOverwritten.put(
							interessatRipea.getDocumentNum(),
							toInteressatMergedDtoCheckingTipus(
									interessatDistribucio,
									conversioTipusHelper.convertir(interessatRipea, InteressatDto.class)));
                }
			}
		}

		return interessatsOverwritten;
	}

	private static Set<RegistreInteressatEntity> getInteressatOrRepresentantsDistribucio(List<RegistreInteressatEntity> interessatsDistribucio) {
		Set<RegistreInteressatEntity> interessatsOrRepresentantsDistribucio = new HashSet<>();
		for (RegistreInteressatEntity inter : interessatsDistribucio) {
			interessatsOrRepresentantsDistribucio.add(inter);
			if (inter.getRepresentant() != null) {
				interessatsOrRepresentantsDistribucio.add(inter.getRepresentant());
			}
		}
		return interessatsOrRepresentantsDistribucio;
	}

//	public InteressatOvewritten checkForInteressatOverwritten(RegistreInteressatEntity interessatDistribucio, Set<InteressatEntity> interessatsORepresenantsRipea) {
//
//		List<InteressatEntity> interessatsRipea = getInteressatRipea(interessatsORepresenantsRipea);
//		InteressatOvewritten interessatRipeaOverwritten = null;
//
//		for (InteressatEntity interessatRipea : interessatsRipea) {
//
//			InteressatEntity representantRipea = interessatRipea.getRepresentant();
//			RegistreInteressatEntity representantDistribucio = interessatDistribucio.getRepresentant();
//			boolean sameInteressats = sameInteressat(interessatRipea, interessatDistribucio);					// interessatRipea == interessatDistribucio
//			boolean interessatEqualsRepresentant = sameInteressat(interessatRipea, representantDistribucio);	// interessatRipea == representantDistribucio
//			boolean representantEqualsInteressat = sameInteressat(representantRipea, interessatDistribucio);	// representantRipea == interessatDistribucio
//			boolean sameRepresentants = sameInteressat(representantRipea, representantDistribucio);				// representantRipea == representantDistribucio
//
//			if (sameInteressats || interessatEqualsRepresentant || representantEqualsInteressat || sameRepresentants) {
//				return  InteressatOvewritten.builder()
//						.interessat(interessatRipea)
//						.ovewriteInteressat(sameInteressats)
//						.ovewriteInteressatAmbRepresentant(interessatEqualsRepresentant)
//						.ovewriteRepresentantAmbInteressat(representantEqualsInteressat)
//						.ovewriteRepresentant(sameRepresentants)
//						.build();
//
//			}
//		}
//		return interessatRipeaOverwritten;
//	}
//
//	@Data @Builder
//	public static class InteressatOvewritten {
//		private InteressatEntity interessat;
//		private boolean ovewriteInteressat;
//		private boolean ovewriteInteressatAmbRepresentant;
//		private boolean ovewriteRepresentantAmbInteressat;
//		private boolean ovewriteRepresentant;
//	}
//
//	private static List<InteressatEntity> getInteressatRipea(Set<InteressatEntity> interessatsORepresenantsRipea) {
//		List<InteressatEntity> interessatsRipea = new ArrayList<>();
//		for (InteressatEntity inter : interessatsORepresenantsRipea) {
//			if (!inter.isEsRepresentant()) {
//				interessatsRipea.add(inter);
//			}
//		}
//		return interessatsRipea;
//	}
//
//	private boolean sameInteressat(InteressatEntity interessatRipea, RegistreInteressatEntity interessatDistribucio) {
//		if (interessatRipea == null || interessatDistribucio == null) return false;
//
//		return Utils.isNotNullAndEquals(interessatRipea.getDocumentNum(), interessatDistribucio.getDocumentNumero()) &&
//				sameTipusInteressat(interessatDistribucio.getTipus(), interessatRipea);
//	}

	private boolean sameTipusInteressat(InteressatTipus tipus, InteressatEntity interessat) {
		if (tipus == null || interessat == null) return false;

		return interessat instanceof InteressatPersonaFisicaEntity && InteressatTipus.PERSONA_FISICA.equals(tipus) ||
				interessat instanceof InteressatPersonaJuridicaEntity && InteressatTipus.PERSONA_JURIDICA.equals(tipus) ||
				interessat instanceof InteressatAdministracioEntity && InteressatTipus.ADMINISTRACIO.equals(tipus);
	}

//	private boolean sameTipusInteressat(InteressatDto dto, InteressatEntity interessat) {
//		if (dto == null || interessat == null) return false;
//
//		return interessat instanceof InteressatPersonaFisicaEntity && dto instanceof InteressatPersonaFisicaDto ||
//				interessat instanceof InteressatPersonaJuridicaEntity && dto instanceof InteressatPersonaJuridicaDto ||
//				interessat instanceof InteressatAdministracioEntity && dto instanceof InteressatAdministracioDto;
//	}
//
//	private InteressatTipusEnumDto getTipusInteressat(InteressatEntity interessat) {
//		return interessat instanceof InteressatPersonaFisicaEntity ? InteressatTipusEnumDto.PERSONA_FISICA :
//				interessat instanceof InteressatPersonaJuridicaEntity ? InteressatTipusEnumDto.PERSONA_JURIDICA :
//				interessat instanceof InteressatAdministracioEntity ? InteressatTipusEnumDto.ADMINISTRACIO : null;
//	}
	

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void relateExpedientWithPeticioAndSetAnnexosPendentNewTransaction(
			Long expedientPeticioId,
			Long expedientId, 
			String rolActual, 
			Long entitatId, 
			boolean associarInteressats,
			Map<String, InteressatAssociacioAccioEnum> interessatsAccionsMap,
			boolean agafarExpedient) {
		
		ExpedientPeticioEntity expedientPeticio = expedientPeticioRepository.findOne(expedientPeticioId);
		if (expedientPeticio.getExpedient() != null) {
			throw new ValidationException(
					"<creacio>",
					ExpedientEntity.class,
					"Aquesta anotació ja està relacionada amb algun expedient");
		}
		
		if (agafarExpedient) {
			ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
					expedientId,
					false,
					true,
					false,
					false,
					false,
					rolActual);
			agafar(expedient, usuariHelper.getUsuariAutenticat().getCodi());
		}
		
		relateExpedientWithPeticioAndSetAnnexosPendent(expedientPeticioId, expedientId);
		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		expedientPeticioHelper.canviEstatExpedientPeticio(expedientPeticioEntity, ExpedientPeticioEstatEnumDto.PROCESSAT_PENDENT);
		
		if (associarInteressats) {
			associateInteressats(expedientId, expedientPeticioId, PermissionEnumDto.WRITE, rolActual, interessatsAccionsMap);
			arxiuPropagarExpedientAmbInteressats(expedientId);
		}
	}

	@Transactional
	public void updateNotificarError(Long expedientPeticioId, String error) {
		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		expedientPeticioEntity.updateNotificaDistError(error);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateRegistreAnnexError(Long registreAnnexId, String error) {
		RegistreAnnexEntity registreAnnexEntity = registreAnnexRepository.findOne(registreAnnexId);
		registreAnnexEntity.updateError(error);

	}

	/**
	 * Creates document from registre annex
	 * @param expedientId 
	 * @param registreAnnexId
	 * @param expedientPeticioId
	 * @param metaDocumentId
	 * @param rolActual
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Exception crearDocFromAnnex(Long expedientId, Long registreAnnexId, Long expedientPeticioId, Long metaDocumentId, String rolActual) {
		ExpedientEntity expedientEntity;
		RegistreAnnexEntity registreAnnexEntity;
		EntitatEntity entitat;
		CarpetaEntity carpetaEntity = null;
		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		expedientEntity = expedientRepository.findOne(expedientId);
		registreAnnexEntity = registreAnnexRepository.findOne(registreAnnexId);
		entitat = expedientPeticioEntity.getRegistre().getEntitat();

//		if (registreAnnexEntity.getAnnexEstat() == null) {
//			try {
//				Document documentArxiu = pluginHelper.arxiuDocumentConsultar(null, registreAnnexEntity.getUuid(), null, false);
//				registreAnnexEntity.updateAnnexEstat(DocumentEstat.ESBORRANY.equals(documentArxiu.getEstat()) ? ArxiuEstatEnumDto.ESBORRANY : ArxiuEstatEnumDto.DEFINITIU);
//			} catch (Exception ex) {
//				logger.debug("No s'ha pogut obtenir informació del document a l'arxiu");
//			}
//		}

		if (expedientEntity.getArxiuUuid() == null) {
			throw new RuntimeException("Annex no s'ha processat perque l'expedient no s'ha creat a l'arxiu");
		}
		
		if (registreAnnexEntity.getDocument() == null) {
			
			logger.info("Creant carpeta i documents de expedient peticio (" + "expedientId=" +
					expedientId + ", " + "registreAnnexId=" + registreAnnexId + 
					", " + "registreAnnexNom=" + registreAnnexEntity.getNom() + 
					", " + "registreAnnexTitol=" + registreAnnexEntity.getTitol() +
					", " + "expedientPeticioId=" + expedientPeticioEntity.getId() + ")");
		
			// ########################## CREATE CARPETA IN DB AND IN ARXIU ######################
			// create carpeta in db and arxiu if doesnt already exists
			Long carpetaId = createCarpetaForDocFromAnnex(
					expedientEntity,
					entitat.getId(),
					"Registre entrada: " + expedientPeticioEntity.getRegistre().getIdentificador(), 
					rolActual);
			carpetaEntity = carpetaRepository.findOne(carpetaId);
		
			carpetaEntity.updateNumeroRegistre(expedientPeticioEntity.getIdentificador());
			
			// ########################### CREATE DOCUMENT IN DB ########################
			DocumentDto documentDto = toDocumentDto(registreAnnexEntity);
			
			contingutHelper.comprovarNomValid(
					carpetaEntity,
					documentDto.getNom(),
					null,
					DocumentEntity.class);
			
			String uniqueNameInPare = contingutHelper.getUniqueNameInPare(documentDto.getNom(), carpetaEntity.getId());
			documentDto.setNom(uniqueNameInPare);
			
			//	Recuperar tipus document per defecte
			MetaDocumentEntity metaDocument = metaDocumentId != null ? metaDocumentRepository.findOne(metaDocumentId) : null;
			
			
			List<DocumentEntity> documents = documentRepository.findByExpedientAndMetaNodeAndEsborrat(
					expedientEntity,
					metaDocument,
					0);
			if (documents.size() > 0 && (metaDocument.getMultiplicitat().equals(MultiplicitatEnumDto.M_1) || metaDocument.getMultiplicitat().equals(MultiplicitatEnumDto.M_0_1))) {
				throw new ValidationException(
						"<creacio>",
						ExpedientEntity.class,
						"La multiplicitat del meta-document no permet crear nous documents a dins l'expedient (" +
						"metaExpedientId=" + expedientEntity.getMetaExpedient().getId() + ", " +
						"metaDocumentId=" + metaDocumentId + ", " +
						"metaDocumentMultiplicitat=" + metaDocument.getMultiplicitat() + ", " +
						"expedientId=" + expedientEntity.getId() + ")");
			}
			
		
			DocumentEntity docEntity = documentHelper.crearDocumentDB(
					documentDto.getDocumentTipus(),
					documentDto.getNom(),
					documentDto.getDescripcio(),
					documentDto.getData(),
					documentDto.getDataCaptura(),
					documentDto.getNtiOrgano(),
					documentDto.getNtiOrigen(),
					documentDto.getNtiEstadoElaboracion(),
					documentDto.getNtiTipoDocumental(),
					metaDocument,
					carpetaEntity,
					expedientEntity.getEntitat(),
					expedientEntity,
					documentDto.getUbicacio(),
					documentDto.getNtiIdDocumentoOrigen(),
					null,
					registreAnnexEntity.isValidacioFirmaCorrecte(),
					registreAnnexEntity.getValidacioFirmaErrorMsg(),
					registreAnnexEntity.getAnnexArxiuEstat(), 
					documentHelper.getDocumentFirmaTipus(documentDto.getNtiTipoFirma()), 
					expedientEntity.getEstatAdditional());
			
			FitxerDto fitxer = new FitxerDto(
					documentDto.getFitxerNom(),
					documentDto.getFitxerContentType(),
					documentDto.getFitxerTamany());
			
			documentHelper.actualitzarFitxerDB(
					docEntity,
					fitxer);
			
			docEntity.updateNumeroRegistre(expedientPeticioEntity.getIdentificador());
			
			if (fitxer.getContingut() != null && documentDto.isAmbFirma()) {
				documentHelper.validaFirmaDocument(docEntity, fitxer, documentDto.getFirmaContingut(), true, false);
			} 
			
			if (ArxiuEstatEnumDto.DEFINITIU.equals(registreAnnexEntity.getAnnexArxiuEstat()) || registreAnnexEntity.getAnnexArxiuEstat() == null) {
				if (registreAnnexEntity.getFirmaTipus() != null) {
					docEntity.updateEstat(DocumentEstatEnumDto.CUSTODIAT);
				} else {
					docEntity.updateEstat(DocumentEstatEnumDto.DEFINITIU);
				}
			}
			docEntity.setNtiTipoFirma(documentDto.getNtiTipoFirma());
			registreAnnexEntity.updateDocument(docEntity);
			contingutLogHelper.logCreacio(docEntity, true, true);
		}

		Exception exception = null;
		
		// ############################ MOVE ANNEX IN ARXIU ####################
		exception = moveAnnexArxiu(registreAnnexEntity.getId());
		return exception;
	}
	
	
	public Exception moveAnnexArxiu(Long registreAnnexId) {
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromAnnexId(registreAnnexId));
		RegistreAnnexEntity registreAnnexEntity = registreAnnexRepository.findOne(registreAnnexId);
		DocumentEntity docEntity = registreAnnexEntity.getDocument();
		ContingutEntity pare = docEntity.getPare();
		ExpedientEntity expedientEntity = docEntity.getExpedient();
		Exception exception = null;

		
		String uuidToMove = null;
//		if (!StringUtils.isEmpty(registreAnnexEntity.getUuidDispatched())) {
//			uuidToMove = registreAnnexEntity.getUuidDispatched();
//		} else {
			uuidToMove = registreAnnexEntity.getUuid();
//		}
		
		docEntity.updateArxiu(uuidToMove);
		
		try {
			organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(expedientEntity.getId()));
			String uuidDesti = contingutHelper.arxiuDocumentPropagarMoviment(
					uuidToMove,
					pare,
					expedientEntity.getArxiuUuid());
			// if document was dispatched, update uuid to new document
			if (uuidDesti != null) {
				docEntity.updateArxiu(uuidDesti);
			}
			
			if (ArxiuEstatEnumDto.DEFINITIU.equals(registreAnnexEntity.getAnnexArxiuEstat()) || registreAnnexEntity.getAnnexArxiuEstat() == null) {
				docEntity.updateArxiuEstat(ArxiuEstatEnumDto.DEFINITIU);
			} else {
				docEntity.updateArxiuEstat(ArxiuEstatEnumDto.ESBORRANY);
			}
			
			registreAnnexEntity.updateError(null);
			
		} catch (Exception e) {
			exception = e;
			logger.error("Error mover document en arxiu", e);
			registreAnnexEntity.updateError(ExceptionUtils.getStackTrace(e));
		}
		
		if (exception == null) {
			try {
				// ###################### UPDATE DOCUMENT WITH INFO FROM ARXIU ###############
				// save ntiIdentitficador generated in arxiu in db
				Document documentDetalls = pluginHelper.arxiuDocumentConsultar(docEntity, null, null, true, false);
				documentDetalls.getMetadades().getIdentificadorOrigen();
				docEntity.updateNtiIdentificador(documentDetalls.getMetadades().getIdentificador());
				documentRepository.save(docEntity);
				
				// comprovar si el justificant s'ha importat anteriorment
				List<DocumentDto> documents = documentHelper.findByArxiuUuid(documentDetalls.getIdentificador());
				if (documents != null && !documents.isEmpty()) {
					for (DocumentDto documentAlreadyImported: documents) {
						expedientsWithImportacio.add(documentAlreadyImported);
					}
				}
			} catch (Exception e) {
				exception = e;
				logger.error("Error despues de mover documento en arxiu", e);
				registreAnnexEntity.updateError(ExceptionUtils.getStackTrace(e));
			}
		}
		return exception;
		
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Exception moveDocumentArxiuNewTransaction(Long registreAnnexId) {
		return moveAnnexArxiu(registreAnnexId);
	}
	
	
	/**
	 * Creates document from registre annex
	 * @param expedientId 
	 * 
	 * @param arxiuUuid
	 * @param expedientPeticioId
	 * @param justificantIdMetaDoc 
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public DocumentEntity crearDocFromUuid(
			Long expedientId, 
			String arxiuUuid, 
			Long expedientPeticioId,
			Long justificantIdMetaDoc) {
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(expedientId));
		ExpedientEntity expedientEntity;
		EntitatEntity entitat;
		CarpetaEntity carpetaEntity = null;
		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		expedientEntity = expedientRepository.findOne(expedientId);
		Document documentDetalls = pluginHelper.arxiuDocumentConsultar(
				null, 
				arxiuUuid, 
				null, 
				true, 
				false);
		//registreAnnexEntity = registreAnnexRepository.findOne(registreAnnexId);
		entitat = entitatRepository.findByUnitatArrel(expedientPeticioEntity.getRegistre().getEntitatCodi());
		logger.debug(
				"Creant justificant de expedient peticio (" + "expedientId=" +
						expedientId + ", " + "arxiuUuid=" + arxiuUuid +
						", " + "expedientPeticioId=" + expedientPeticioEntity.getId() + ")");

		// ############################## CREATE CARPETA IN DB AND IN ARXIU
		// ##########################################
		boolean isCarpetaActive = configHelper.getAsBoolean("es.caib.ripea.creacio.carpetes.activa");
		if (isCarpetaActive) {
			// create carpeta ind db and arxiu if doesnt already exists
			Long carpetaId = createCarpetaForDocFromAnnex(
					expedientEntity,
					entitat.getId(),
					"Registre entrada: " + expedientPeticioEntity.getRegistre().getIdentificador(), null);
			carpetaEntity = carpetaRepository.findOne(carpetaId);
			
			carpetaEntity.updateNumeroRegistre(expedientPeticioEntity.getIdentificador());
		}

		// ############################## CREATE DOCUMENT IN DB
		// ####################################
		DocumentDto documentDto = toDocumentDto(
				documentDetalls, 
				expedientPeticioEntity.getIdentificador());
		// comprovar si el justificant s'ha importat anteriorment
		List<DocumentDto> documents = documentHelper.findByArxiuUuid(arxiuUuid);
		if (documents != null && !documents.isEmpty()) {
			for (DocumentDto documentAlreadyImported: documents) {
				expedientsWithImportacio.add(documentAlreadyImported);
			}
		}
		contingutHelper.comprovarNomValid(
				isCarpetaActive ? carpetaEntity : expedientEntity,
				documentDto.getNom(),
				null,
				DocumentEntity.class);
//		Recuperar tipus document per defecte
//		MetaDocumentEntity metaDocument = metaDocumentRepository.findByMetaExpedientAndPerDefecteTrue(expedientEntity.getMetaExpedient());
		MetaDocumentEntity metaDocument = justificantIdMetaDoc != null ? metaDocumentRepository.findOne(justificantIdMetaDoc) : null;
		
		DocumentEntity docEntity = documentHelper.crearDocumentDB(
				documentDto.getDocumentTipus(),
				documentDto.getNom(),
				documentDto.getDescripcio(),
				documentDto.getData(),
				documentDto.getDataCaptura(),
				documentDto.getNtiOrgano(),
				documentDto.getNtiOrigen(),
				documentDto.getNtiEstadoElaboracion(),
				documentDto.getNtiTipoDocumental(),
				metaDocument,
				isCarpetaActive ? carpetaEntity : expedientEntity,
				expedientEntity.getEntitat(),
				expedientEntity,
				documentDto.getUbicacio(),
				documentDto.getNtiIdDocumentoOrigen(),
				null, 
				documentHelper.getDocumentFirmaTipus(documentDto.getNtiTipoFirma()), 
				expedientEntity.getEstatAdditional());
		
		FitxerDto fitxer = new FitxerDto(
				documentDto.getFitxerNom(),
				documentDto.getFitxerContentType(),
				documentDto.getFitxerTamany());

		documentHelper.actualitzarFitxerDB(
				docEntity,
				fitxer);
		
		docEntity.updateNumeroRegistre(expedientPeticioEntity.getIdentificador());
		
		if (fitxer.getContingut() != null && documentDto.isAmbFirma()) {
			documentHelper.validaFirmaDocument(docEntity, fitxer, documentDto.getFirmaContingut(), true, false);
		}


		docEntity.updateEstat(DocumentEstatEnumDto.CUSTODIAT);
		docEntity.setNtiTipoFirma(documentDto.getNtiTipoFirma());
		
		// ############################## MOVE DOCUMENT IN ARXIU
		// ##########################################
		// put arxiu uuid of annex
		docEntity.updateArxiu(documentDto.getArxiuUuid());
		docEntity.updateArxiuEstat(ArxiuEstatEnumDto.DEFINITIU);
		documentRepository.saveAndFlush(docEntity);
		if (isCarpetaActive && ! contingutHelper.isCarpetaLogica()) {
			Carpeta carpeta = pluginHelper.arxiuCarpetaConsultar(carpetaEntity);
			boolean documentExistsInArxiu = false;
			String documentUuid = null;
			if (carpeta != null && carpeta.getContinguts() != null) {
				for (ContingutArxiu contingutArxiu : carpeta.getContinguts()) {
					if (contingutArxiu.getTipus() == ContingutTipus.DOCUMENT &&
							contingutArxiu.getNom().equals(docEntity.getNom())) {
						documentExistsInArxiu = true;
						documentUuid = contingutArxiu.getIdentificador();
					}
				}
			}
			if (documentExistsInArxiu && carpetaEntity.getArxiuUuid() == null) {
				carpetaEntity.updateArxiu(documentUuid);
			}
			if (!documentExistsInArxiu) {
				String uuidDesti = contingutHelper.arxiuDocumentPropagarMoviment(
						docEntity.getArxiuUuid(),
						carpetaEntity,
						expedientEntity.getArxiuUuid());
				// if document was dispatched, update uuid to new document
				if (uuidDesti != null) {
					docEntity.updateArxiu(uuidDesti);
				}
			}
		} else {
			Expedient expedient = pluginHelper.arxiuExpedientConsultar(expedientEntity);
			boolean documentExistsInArxiu = false;
			String documentUuid = null;
			if (expedient.getContinguts() != null) {
				for (ContingutArxiu contingutArxiu : expedient.getContinguts()) {
					if (contingutArxiu.getTipus() == ContingutTipus.DOCUMENT &&
							contingutArxiu.getNom().equals(docEntity.getNom())) {
						documentExistsInArxiu = true;
						documentUuid = contingutArxiu.getIdentificador();
					}
				}
			}
			if (documentExistsInArxiu && carpetaEntity.getArxiuUuid() == null) {
				expedientEntity.updateArxiu(documentUuid);
			}
			if (!documentExistsInArxiu) {
				String uuidDesti = contingutHelper.arxiuDocumentPropagarMoviment(
						docEntity.getArxiuUuid(),
						expedientEntity,
						expedientEntity.getArxiuUuid());
				// if document was dispatched, update uuid to new document
				if (uuidDesti != null) {
					docEntity.updateArxiu(uuidDesti);
				}
			}
		}
		// save ntiIdentitficador generated in arxiu in db
		documentDetalls.getMetadades().getIdentificadorOrigen();
		docEntity.updateNtiIdentificador(documentDetalls.getMetadades().getIdentificador());
		documentRepository.save(docEntity);
		contingutLogHelper.logCreacio(docEntity, true, true);
		return docEntity;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateRegistresImportats(
			Long expedientId, 
			String numeroRegistre) {
		ExpedientEntity expedientEntity = expedientRepository.findOne(expedientId);
		
		expedientEntity.updateRegistresImportats(numeroRegistre);
	}
	
	public void inicialitzarExpedientsWithImportacio() {
		expedientsWithImportacio = new ArrayList<DocumentDto>();
	}
	
	public List<DocumentDto> consultaExpedientsAmbImportacio() {
		return expedientsWithImportacio;
	}
	
	public List<ExpedientEntity> consultaExpedientsPendentsTancarArxiu(EntitatEntity entitat) {
		return expedientRepository.findByEstatAndTancatLogicOrderByTancatProgramat(ExpedientEstatEnumDto.TANCAT, entitat, new Date());
	}
	
	public ExpedientEntity updateNomExpedient(ExpedientEntity expedient, String nom) {
		contingutHelper.comprovarNomValid(expedient.getPare(), nom, expedient.getId(), ExpedientEntity.class);
		String nomOriginal = expedient.getNom();
		expedient.updateNom(nom);
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.MODIFICACIO,
				(!nomOriginal.equals(expedient.getNom())) ? expedient.getNom() : null,
				null,
				false,
				false);
		
		return expedient;
	}
	
	public ExpedientEntity updateAnyExpedient(ExpedientEntity expedient, int any) {
		int anyOriginal = expedient.getAny();
		expedient.updateAny(any);
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.MODIFICACIO,
				(anyOriginal != (expedient.getAny())) ? String.valueOf(expedient.getAny()) : null,
				null,
				false,
				false);
		return expedient;
	}
	
	
	public ExpedientEntity updateOrganGestor(ExpedientEntity expedient, Long organGestorId, String rolActual) {
		Long id = expedient.getOrganGestor() != null ? expedient.getOrganGestor().getId() : null;
		
		OrganGestorEntity organGestorEntity = getOrganGestorForExpedient(expedient.getMetaExpedient(), organGestorId, ExtendedPermission.WRITE, rolActual);
		expedient.updateOrganGestor(organGestorEntity);
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.MODIFICACIO,
				(id != (organGestorId)) ? String.valueOf(organGestorId) : null,
				null,
				false,
				false);
		
		// Actualitza les relacions expedients i organs pare
		organGestorHelper.removeOldExpedientOrganPares(
				expedient);
		organGestorHelper.crearExpedientOrganPares(
				expedient,
				organGestorEntity);
		
		return expedient;
	}

	public ExpedientEntity updatePrioritat(ExpedientEntity expedient, PrioritatEnumDto prioritat, String prioritatMotiu) {

		PrioritatEnumDto prioritatAnterior = expedient.getPrioritat();
		expedient.updatePrioritat(prioritat, prioritatMotiu);

		// log change of state
		if(!prioritatAnterior.equals(prioritat)){
			contingutLogHelper.log(
					expedient,
					LogTipusEnumDto.CANVI_PRIORITAT,
					messageHelper.getMessage("prioritat.enum." + prioritatAnterior.name()),
					messageHelper.getMessage("prioritat.enum." + prioritat.name()),
					false,
					false);
		}

		return expedient;
	}


	public Long checkIfExistsByMetaExpedientAndNom(
			Long metaExpedientId,
			String nom) {

		MetaExpedientEntity metaExpedient = metaExpedientRepository.findOne(metaExpedientId);

		ExpedientEntity expedient = expedientRepository.findByMetaExpedientAndPareAndNomAndEsborrat(
				metaExpedient,
				null,
				nom,
				0);
		return expedient != null ? expedient.getId() : null;
	}


	@Transactional(readOnly = true)
	public ExpedientDto toExpedientDto(
			ExpedientEntity expedient,
			boolean ambPath,
			boolean ambPermisos,
			String rolActual,
			boolean onlyForList) {
		ExpedientDto expedientDto = (ExpedientDto) contingutHelper.toContingutDto(
				expedient,
				ambPermisos,
				false,
				false,
				ambPath,
				false,
				false,
				rolActual,
				onlyForList,
				null,
				false,
				0,
				null,
				null,
				true,
				true,
				false,
				false);
		return expedientDto;
	}
	
	public ExpedientEntity tancar(Long entitatId, Long expedientId, String motiu, Long[] documentsPerFirmar, boolean checkPerMassiuAdmin) {

		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(expedientId));

		logger.debug("Tancant l'expedient (" + "entitatId=" + entitatId + ", " + "id=" + expedientId + "," + "motiu=" + motiu + ")");

		expedientHelper2.checkIfExpedientCanBeClosed(expedientId);
		
		expedientHelper2.signDocumentsSelected(motiu, documentsPerFirmar);
		
		expedientHelper2.deleteDocumentsNotSelectedDB(entitatId, expedientId, documentsPerFirmar);
		
		expedientHelper2.markAllDocumentsEsborranysAsDefinitiusArxiu(expedientId);
		
		expedientHelper2.deleteDocumentsEsborranysArxiu(expedientId);
		
		expedientHelper2.closeExpedientDbAndArxiu(expedientId, motiu);

		return expedientRepository.findOne(expedientId);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public List<Long> getMetaExpedientIdDomini(String dominiCodi) {
		List<Long> metaExpedientIdDomini = new ArrayList<Long>();
		List<MetaDadaEntity> metaDades = metaDadaRepository.findByCodi(dominiCodi);
		for (MetaDadaEntity metaDadaEntity : metaDades) {
			MetaNodeEntity metaNodeEntityDeproxied = HibernateHelper.deproxy(metaDadaEntity.getMetaNode());
			if (metaNodeEntityDeproxied instanceof MetaExpedientEntity) {
				MetaExpedientEntity metaExpedient = (MetaExpedientEntity)metaNodeEntityDeproxied;
				metaExpedientIdDomini.add(metaExpedient.getId());
			}
		}
		return metaExpedientIdDomini;
	}
	
	private MustacheFactory mustacheFactory = new DefaultMustacheFactory();

	public String calcularNumero(ExpedientEntity expedient) {
		MetaExpedientEntity metaExpedient = expedient.getMetaExpedient();
		String expressioNumero = metaExpedient.getExpressioNumero();
		if (expressioNumero != null && !expressioNumero.isEmpty()) {
			Mustache mustache = mustacheFactory.compile(new StringReader(expressioNumero), "expressioNumero");
			StringWriter writer = new StringWriter();
			HashMap<String, Object> model = new HashMap<String, Object>();
			model.put("codi", expedient.getCodi());
			model.put("seq", expedient.getSequencia());
			model.put("any", expedient.getAny());
			mustache.execute(writer, model);
			writer.flush();
			return writer.toString();
		} else {
			String separador = configHelper.getConfig("es.caib.ripea.numero.expedient.separador", "/");
			return expedient.getCodi() + separador + expedient.getSequencia() + separador + expedient.getAny();
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void generateNumeroExpedient(Long id) {
		ExpedientEntity expedientEntity = expedientRepository.findOne(id);
		String numero = calcularNumero(expedientEntity);
		expedientEntity.updateNumero(numero);
	}
	


	public ExpedientDto toExpedientDto(ExpedientEntity entity) {
		ExpedientDto dto = new ExpedientDto();

		dto.setNumero(entity.getNumero());
		dto.setNom(entity.getNom());
		dto.setAlerta(alertaRepository.countByLlegidaAndContingutId(false, entity.getId()) > 0);
		dto.setValid(cacheHelper.findErrorsValidacioPerNode(entity).isEmpty());
		dto.setErrorLastEnviament(cacheHelper.hasEnviamentsPortafirmesAmbErrorPerExpedient(entity));
		dto.setErrorLastNotificacio(cacheHelper.hasNotificacionsAmbErrorPerExpedient(entity));
		dto.setAmbEnviamentsPendents(cacheHelper.hasEnviamentsPortafirmesPendentsPerExpedient(entity));
		dto.setAmbNotificacionsPendents(cacheHelper.hasNotificacionsPendentsPerExpedient(entity));
		dto.setArxiuUuid(entity.getArxiuUuid());
		dto.setId(entity.getId());
		dto.setCreatedDate(entity.getCreatedDate().toDate());
		dto.setEstat(entity.getEstat());
		dto.setAgafatPer(conversioTipusHelper.convertir(entity.getAgafatPer(),UsuariDto.class));
		// expedient estat
		if (entity.getEstatAdditional() != null) {
			dto.setExpedientEstat(conversioTipusHelper.convertir(
					entity.getEstatAdditional(),
					ExpedientEstatDto.class));
		}

		return dto;
	}
	public String agafar(ExpedientEntity expedient, String usuariCodi) {

		ExpedientEntity expedientSuperior = contingutHelper.getExpedientSuperior(expedient, false, false, false, null);
		if (expedientSuperior != null) {
			logger.error("No es pot agafar un expedient no arrel (id=" + expedient.getId() + ")");
			throw new ValidationException(expedient.getId(), ExpedientEntity.class, "No es pot agafar un expedient no arrel");
		}
		// Agafa l'expedient. Si l'expedient pertany a un altre usuari li pren
		UsuariEntity usuariOriginal = expedient.getAgafatPer();
		UsuariEntity usuariNou = usuariHelper.getUsuariByCodi(usuariCodi);
		expedient.updateAgafatPer(usuariNou);
		if (usuariOriginal != null) {
			// Avisa a l'usuari que li han pres
			emailHelper.contingutAgafatPerAltreUsusari(expedient, usuariOriginal, usuariNou);
		}
		contingutLogHelper.log(expedient, LogTipusEnumDto.AGAFAR, usuariCodi, null, false, false);

		return expedient.getNom();
	}
	
	public String alliberar(ExpedientEntity expedient) {
		UsuariEntity usuariActual = expedient.getAgafatPer();
		UsuariEntity usuariCreador = expedient.getCreatedBy();

		boolean agafatPerUsuariActual = false;
		
		if (usuariActual != null && usuariCreador != null && usuariActual.getCodi().equalsIgnoreCase(usuariCreador.getCodi())) {
			agafatPerUsuariActual = true;
			expedient.updateAgafatPer(null);
		} else {
			expedient.updateAgafatPer(usuariCreador);
		}
		
		if (usuariCreador != null && !agafatPerUsuariActual) {
			// Avisa a l'usuari que li han retornat
			emailHelper.contingutAlliberat(expedient, usuariCreador, usuariActual);
		}
		contingutLogHelper.log(expedient, LogTipusEnumDto.ALLIBERAR, usuariActual.getCodi(), null, false, false);
		return expedient.getNom();
	}
	
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public Exception guardarExpedientArxiu(
			Long expId) {
		
		logger.info("Guardar expedient arxiu (id=" + expId + ", entitatCodi=" + configHelper.getEntitatActualCodi() + ")");
		Exception exception = null;
		
		ExpedientEntity expedient = expedientRepository.findOne(expId);
		
		if (expedient.getArxiuUuid() != null) { // concurrency check
			throw new ArxiuJaGuardatException("L'expedient ja s'ha guardat en arxiu per otra persona o el process en segon pla");
		}
		concurrencyCheckExpedientJaTancat(expedient);
		
		exception = expedientInteressatHelper.arxiuPropagarInteressats(expedient, null);
		
		expedient.updateArxiuIntent();
		
		return exception;
	}
	
	public FitxerDto exportarEniExpedientPerInside(boolean massiu, ExpedientEntity expedient, ZipOutputStream zos, boolean ambDocuments) throws IOException {
		if (massiu) {
			String expedientExportacioEni = pluginHelper.arxiuExpedientExportar(expedient);
			if (expedientExportacioEni != null) {
				FitxerDto exportacioEni = new FitxerDto();
				exportacioEni.setNom(expedient.getNom() + "_exportació_ENI.xml");
				exportacioEni.setContentType("application/xml");
				exportacioEni.setContingut(expedientExportacioEni.getBytes());
				contingutHelper.crearNovaEntrada(exportacioEni.getNom(), exportacioEni, zos);
			}
		} else {
			String expedientExportacioEni = pluginHelper.arxiuExpedientExportar(expedient);
			if (expedientExportacioEni != null && !ambDocuments) {
				// Exportar només ENI expedient
				FitxerDto resultat = new FitxerDto();
				resultat.setNom(expedient.getNom() + "_exportació_ENI.xml");
				resultat.setContentType("application/xml");
				resultat.setContingut(expedientExportacioEni.getBytes());
				
				return resultat;
			} else {
				FitxerDto exportacioEni = new FitxerDto();
				exportacioEni.setNom(expedient.getNom() + "_exportació_ENI.xml");
				exportacioEni.setContentType("application/xml");
				exportacioEni.setContingut(expedientExportacioEni.getBytes());
				contingutHelper.crearNovaEntrada(exportacioEni.getNom(), exportacioEni, zos);
			}
			
			if (ambDocuments) {
				List<DocumentEntity> documentsDefinitius = documentRepository.findByExpedientAndEstatInAndEsborrat(
						expedient, 
						new DocumentEstatEnumDto[] {
								DocumentEstatEnumDto.FIRMAT,
								DocumentEstatEnumDto.CUSTODIAT,
								DocumentEstatEnumDto.DEFINITIU
							},
						0);
				
				Map<String, Integer> duplicateCountMap = new HashMap<>();
				for (DocumentEntity document: documentsDefinitius) {
					if (document.getArxiuUuid() != null) {
						String documentExportacioEni = pluginHelper.arxiuDocumentExportar(document);
						FitxerDto exportacioEni = new FitxerDto();
						String documentNom = document.getNom() + "_exportació_ENI.xml";
						
						if (duplicateCountMap.containsKey(documentNom)) {
							int count = duplicateCountMap.get(documentNom);
		                    count++;
		                    duplicateCountMap.put(documentNom, count);
		                    
		                    documentNom = removeExtension(documentNom) + "_" + count + ".xml";
						} else {
		                    duplicateCountMap.put(documentNom, 0);
		                }
		                
						exportacioEni.setNom(documentNom);
						exportacioEni.setContentType("application/xml");
						exportacioEni.setContingut(documentExportacioEni.getBytes());
						contingutHelper.crearNovaEntrada(exportacioEni.getNom(), exportacioEni, zos);
					}
				}
			}
		}
		return new FitxerDto();
	}

    private String removeExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1)
            return filename;
        
        return filename.substring(0, dotIndex);
    }
    
	public FitxerDto exportarExpedient(
			EntitatEntity entitatActual, 
			List<ExpedientEntity> expedients,
			boolean exportar,
			String format) throws IOException {
		FitxerDto resultat = new FitxerDto();
		if (exportar) {
//			crear estructura documents + exportació ENI + índex
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ZipOutputStream zos = new ZipOutputStream(baos);
			BigDecimal sum = new BigDecimal(1);
			ExpedientEntity expedient = expedients.get(0);
//			List<ContingutEntity> continguts = contingutRepository.findByPareAndEsborrat(
//					expedient,
//					0,
//					contingutHelper.isOrdenacioPermesa() ? new Sort("ordre") : new Sort("createdDate"));
			List<ContingutEntity> continguts = new ArrayList<ContingutEntity>();
			if (contingutHelper.isOrdenacioPermesa()) {
				continguts = contingutRepository.findByPareAndEsborratAndOrdenatOrdre(expedient, 0);
			} else {
				continguts = contingutRepository.findByPareAndEsborratAndOrdenat(expedient, 0);
			}
			BigDecimal num = new BigDecimal(0);
			for (ContingutEntity contingut : continguts) {
				if (num.scale() > 0)
					num = num.setScale(0, BigDecimal.ROUND_HALF_UP);
				
				if (contingut instanceof DocumentEntity) {
					DocumentEntity document = (DocumentEntity)contingut;
					if (document.getEstat().equals(DocumentEstatEnumDto.CUSTODIAT) || document.getEstat().equals(DocumentEstatEnumDto.DEFINITIU)) {
						FitxerDto fitxer = documentHelper.getFitxerAssociat(document, null);
						num = num.add(sum);
						String nomDocument = num.scale() > 0 ? String.valueOf(num.doubleValue()) : String.valueOf(num.intValue()) + " - " + document.getNom();
						contingutHelper.crearNovaEntrada(nomDocument, fitxer, zos);

						if (document.isFirmat()) {
							String documentExportacioEni = pluginHelper.arxiuDocumentExportar(document);
							if (documentExportacioEni != null) {
								FitxerDto exportacioEni = new FitxerDto();
								exportacioEni.setNom("ENI_documents/" + nomDocument + "_exportacio_ENI.xml");
								exportacioEni.setContentType("application/xml");
								exportacioEni.setContingut(documentExportacioEni.getBytes());
		
								contingutHelper.crearNovaEntrada(exportacioEni.getNom(), exportacioEni, zos);
							}
						}
					}
				}

				if (contingut instanceof CarpetaEntity) {
					CarpetaEntity carpeta = (CarpetaEntity)contingut;
					String ruta = "";
					try {
						if (carpeta.getExpedientRelacionat() != null) {
							num = num.add(sum);
						} else {
							num = crearFilesCarpetaActual(
									num, 
									sum, 
									carpeta, 
									entitatActual, 
									ruta,
									zos);
						}
					} catch (Exception ex) {
						logger.error("Hi ha hagut un error generant l'entrada " + num + " dins del fitxer comprimit", ex);
					}
				}
			}
			
			String expedientExportacioEni = pluginHelper.arxiuExpedientExportar(expedient);
			if (expedientExportacioEni != null) {
				FitxerDto exportacioEni = new FitxerDto();
				exportacioEni.setNom(expedient.getNom() + "_exportacio_ENI.xml");
				exportacioEni.setContentType("application/xml");
				exportacioEni.setContingut(expedientExportacioEni.getBytes());
				contingutHelper.crearNovaEntrada(exportacioEni.getNom(), exportacioEni, zos);
			}
			
			FitxerDto indexDoc = contingutHelper.generarIndexPdf(entitatActual, expedients, exportar);
			contingutHelper.crearNovaEntrada(indexDoc.getNom(), indexDoc, zos);
			zos.close();
			
			resultat.setNom(messageHelper.getMessage("expedient.service.exportacio.index") + " " + expedient.getNom() + ".zip");
			resultat.setContentType("application/zip");
			resultat.setContingut(baos.toByteArray());
		} else {
			if ("PDF".equalsIgnoreCase(format))
				resultat = contingutHelper.generarIndexPdf(entitatActual, expedients, exportar);
			else if ("XLSX".equalsIgnoreCase(format))
				resultat = contingutHelper.generarIndexXlsx(entitatActual, expedients, exportar);
			else
				resultat = contingutHelper.generarIndexPdf(entitatActual, expedients, exportar);
		}
		return resultat;
	}
	
	
	
	public PermisosPerExpedientsDto findPermisosPerExpedients(
			Long entitatId,
			String rolActual,
			Long organActual) {

		PermisosPerExpedientsDto permisosPerExpedientsDto = new PermisosPerExpedientsDto();
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);

		List<Long> idsMetaExpedientsPermesos = null;
		List<Long> idsOrgansPermesos = null;
		List<Long> idsMetaExpedientOrganPairsPermesos = null;
		List<Long> procedimentsComunsIds = null;
		List<Long> idsOrgansAmbProcedimentsComunsPermesos = null;
		List<Long> idsGrupsPermesos = null;

        long t1 = System.currentTimeMillis();

		if (rolActual.equals("IPA_ADMIN")) {

			//Si ets admin veruas els expedients de tots els procediments de la entitat
			idsMetaExpedientsPermesos = metaExpedientRepository.findAllIdsByEntitat(entitat);

            if (cacheHelper.mostrarLogsRendiment())
                logger.info("findPermisosPerExpedients > idsMetaExpedientsPermesos (" + (idsMetaExpedientsPermesos!=null?idsMetaExpedientsPermesos.size():0) + ") time:  " + (System.currentTimeMillis() - t1) + " ms");

		} else if (rolActual.equals("IPA_ORGAN_ADMIN")) {

			//Si ets admin de organ, veuras tots els expedients del organ seleccionat a la capçalera + fills
			if (organActual!=null) {
				idsOrgansPermesos = organGestorCacheHelper.getIdsOrgansFills(entitat.getCodi(), organGestorRepository.findOne(organActual).getCodi());
			}

            if (cacheHelper.mostrarLogsRendiment())
                logger.info("findPermisosPerExpedients > idsOrgansPermesos (" + (idsOrgansPermesos!=null?idsOrgansPermesos.size():0) + ") time:  " + (System.currentTimeMillis() - t1) + " ms");

		}else {
			// Si ets usuari normal, permisos de lectura de varies fonts

			idsMetaExpedientsPermesos = toListLong(permisosHelper.getObjectsIdsWithPermission(
					MetaNodeEntity.class,
					ExtendedPermission.READ));

            if (cacheHelper.mostrarLogsRendiment())
                logger.info("findPermisosPerExpedients > idsMetaExpedientsPermesos (" + (idsMetaExpedientsPermesos!=null?idsMetaExpedientsPermesos.size():0) + ") time:  " + (System.currentTimeMillis() - t1) + " ms");

            long t2 = System.currentTimeMillis();
			idsMetaExpedientOrganPairsPermesos = toListLong(permisosHelper.getObjectsIdsWithPermission(
					MetaExpedientOrganGestorEntity.class,
					ExtendedPermission.READ));

            if (cacheHelper.mostrarLogsRendiment())
                logger.info("findPermisosPerExpedients > idsMetaExpedientOrganPairsPermesos (" + (idsMetaExpedientOrganPairsPermesos!=null?idsMetaExpedientOrganPairsPermesos.size():0) + ") time:  " + (System.currentTimeMillis() - t2) + " ms");

            long t3 = System.currentTimeMillis();
			idsOrgansAmbProcedimentsComunsPermesos = toListLong(permisosHelper.getObjectsIdsWithTwoPermissions(
					OrganGestorEntity.class,
					ExtendedPermission.COMU,
					ExtendedPermission.READ));

            if (cacheHelper.mostrarLogsRendiment())
                logger.info("findPermisosPerExpedients > idsOrgansAmbProcedimentsComunsPermesos (" + (idsOrgansAmbProcedimentsComunsPermesos!=null?idsOrgansAmbProcedimentsComunsPermesos.size():0) + ") time:  " + (System.currentTimeMillis() - t3) + " ms");

            long t4 = System.currentTimeMillis();
			procedimentsComunsIds = metaExpedientRepository.findProcedimentsComunsActiveIds(entitat);

            if (cacheHelper.mostrarLogsRendiment())
                logger.info("findPermisosPerExpedients > procedimentsComunsIds (" + (procedimentsComunsIds!=null?procedimentsComunsIds.size():0) + ") time:  " + (System.currentTimeMillis() - t4) + " ms");

            long t5 = System.currentTimeMillis();
			idsGrupsPermesos = toListLong(permisosHelper.getObjectsIdsWithPermission(
					GrupEntity.class,
					ExtendedPermission.READ));

            if (cacheHelper.mostrarLogsRendiment())
                logger.info("findPermisosPerExpedients > idsGrupsPermesos (" + (idsGrupsPermesos!=null?idsGrupsPermesos.size():0) + ") time:  " + (System.currentTimeMillis() - t5) + " ms");
		}

		permisosPerExpedientsDto.setIdsMetaExpedientsPermesos(idsMetaExpedientsPermesos);
		permisosPerExpedientsDto.setIdsOrgansPermesos(idsOrgansPermesos);
		permisosPerExpedientsDto.setIdsMetaExpedientOrganPairsPermesos(idsMetaExpedientOrganPairsPermesos);
		permisosPerExpedientsDto.setIdsOrgansAmbProcedimentsComunsPermesos(idsOrgansAmbProcedimentsComunsPermesos);
		permisosPerExpedientsDto.setIdsProcedimentsComuns(procedimentsComunsIds);
		permisosPerExpedientsDto.setIdsGrupsPermesos(idsGrupsPermesos);

		return permisosPerExpedientsDto;
	}
	
	private BigDecimal crearFilesCarpetaActual(
			BigDecimal num, 
			BigDecimal sum, 
			ContingutEntity contingut, 
			EntitatEntity entitatActual, 
			String ruta,
			ZipOutputStream zos) throws Exception {
		ContingutEntity carpetaActual = contingut;
		
//		List<ContingutEntity> contingutsCarpetaActual = contingutRepository.findByPareAndEsborrat(
//				carpetaActual, 
//				0, 
//				contingutHelper.isOrdenacioPermesa() ? new Sort("ordre") : new Sort("createdDate"));
		List<ContingutEntity> contingutsCarpetaActual = new ArrayList<ContingutEntity>();
		if (contingutHelper.isOrdenacioPermesa()) {
			contingutsCarpetaActual = contingutRepository.findByPareAndEsborratAndOrdenatOrdre(carpetaActual, 0);
		} else {
			contingutsCarpetaActual = contingutRepository.findByPareAndEsborratAndOrdenat(carpetaActual, 0);
		}
		
		for (ContingutEntity contingutCarpetaActual : contingutsCarpetaActual) {
			if (contingutCarpetaActual instanceof CarpetaEntity) {
				CarpetaEntity subCarpeta = (CarpetaEntity)contingutCarpetaActual;
				if (subCarpeta.getExpedientRelacionat() != null) {
					num = num.add(sum);
				} else {
					num = crearFilesCarpetaActual(
							num, 
							sum,
							subCarpeta, 
							entitatActual,  
							ruta,
							zos);
				}
			} else {
				ContingutEntity pare = contingutCarpetaActual.getPare();
				List<String> estructuraCarpetes = new ArrayList<String>();
				while (pare instanceof CarpetaEntity) {
					estructuraCarpetes.add(pare.getNom());
					if (pare.getPare() instanceof CarpetaEntity)
						pare = (CarpetaEntity) pare.getPare();
					else
						pare = (ExpedientEntity) pare.getPare();
				}

				Collections.reverse(estructuraCarpetes);
				for (String folder: estructuraCarpetes) {
					ruta += folder.replaceAll("[\\/*?\"<>|]", "_").replace(":", "") + "/";
				}
				DocumentEntity document = (DocumentEntity)contingutCarpetaActual;
				if (document.getEstat().equals(DocumentEstatEnumDto.CUSTODIAT) || document.getEstat().equals(DocumentEstatEnumDto.DEFINITIU)) {
					FitxerDto fitxer = documentHelper.getFitxerAssociat(document, null);
					num = num.add(sum);
					String nomDocument = num + " - "+ document.getNom();
					String rutaDoc = ruta + nomDocument;
					contingutHelper.crearNovaEntrada(
							rutaDoc, 
							fitxer, 
							zos);
					if (document.isFirmat()) {
						String documentExportacioEni = pluginHelper.arxiuDocumentExportar(document);
						if (documentExportacioEni != null) {
							FitxerDto exportacioEni = new FitxerDto();
							exportacioEni.setNom("ENI_documents/" + nomDocument + "_exportacio_ENI.xml");
							exportacioEni.setContentType("application/xml");
							exportacioEni.setContingut(documentExportacioEni.getBytes());
		
							contingutHelper.crearNovaEntrada(exportacioEni.getNom(), exportacioEni, zos);
						}
					}
				}
			}
			ruta = "";
		}
		return num;
	}
	
	private OrganGestorEntity getOrganGestorForExpedient(MetaExpedientEntity metaExpedient, Long organGestorId, Permission permis, String rolActual) {
		
		OrganGestorEntity organGestor;
		if (metaExpedient.getOrganGestor() != null) {
			organGestor = metaExpedient.getOrganGestor();
		} else {
			if (organGestorId == null) {
				throw new ValidationException(
						metaExpedient.getId(),
						MetaExpedientEntity.class,
						"La creació/modificació d'un expedient de tipus (metaExpedientId=" + metaExpedient.getId() + ") requereix especificar un òrgan gestor");
			}
			organGestor = organGestorRepository.getOne(organGestorId);
			if (!organGestorHelper.isOrganGestorPermes(metaExpedient, organGestor, permis, rolActual)) {
				throw new ValidationException(
						metaExpedient.getId(),
						MetaExpedientEntity.class,
						"L'usuari actual no te permisos aquest expedient (" +
						"permis=" + permis + ", " +
						"metaExpedientId=" + metaExpedient.getId() + ", " +
						"organGestorId=" + organGestorId + ")");
			}
		}
		return organGestor;
	}
	
	
	private void crearDadesPerDefecte(MetaExpedientEntity metaExpedient, ExpedientEntity expedient) {
		List<MetaDadaEntity> metaDades = metaDadaRepository.findByMetaNodeOrderByOrdreAsc(metaExpedient);
		for (int i = 0; i < metaDades.size(); i++) {
			if (metaDades.get(i).getValor()!= null && !metaDades.get(i).getValor().isEmpty()) {
				Object valor;
				switch (metaDades.get(i).getTipus()) {
				case BOOLEA:
					valor = (Boolean) DadaEntity.getDadaValorPerRetornar(metaDades.get(i), metaDades.get(i).getValor());
					break;
				case DATA:
					valor = (Date) DadaEntity.getDadaValorPerRetornar(metaDades.get(i), metaDades.get(i).getValor());
					break;
				case FLOTANT:
					valor = (Double) DadaEntity.getDadaValorPerRetornar(metaDades.get(i), metaDades.get(i).getValor());
					break;
				case IMPORT:
					valor = (BigDecimal) DadaEntity.getDadaValorPerRetornar(metaDades.get(i), metaDades.get(i).getValor());
					break;
				case SENCER:
					valor = (Long) DadaEntity.getDadaValorPerRetornar(metaDades.get(i), metaDades.get(i).getValor());
					break;
				case TEXT:
					valor = (String) DadaEntity.getDadaValorPerRetornar(metaDades.get(i), metaDades.get(i).getValor());
					break;
				case DOMINI:
					valor = (String) DadaEntity.getDadaValorPerRetornar(metaDades.get(i), metaDades.get(i).getValor());
					break;
				default:
					valor = (String) DadaEntity.getDadaValorPerRetornar(metaDades.get(i), metaDades.get(i).getValor());
					break;
				}
				DadaEntity dada = DadaEntity.getBuilder(
						metaDades.get(i),
						expedient,
						valor,
						i).build();
				dadaRepository.save(dada);
				contingutLogHelper.log(
						expedient,
						LogTipusEnumDto.MODIFICACIO,
						dada,
						LogObjecteTipusEnumDto.DADA,
						LogTipusEnumDto.CREACIO,
						metaDades.get(i).getCodi(),
						dada.getValorComString(),
						false,
						false);
			}
		}
	}

	private void relateExpedientWithPeticioAndSetAnnexosPendent(Long expedientPeticioId, Long expedientId) {
		ExpedientEntity expedient = expedientRepository.findOne(expedientId);
		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		expedientPeticioEntity.updateExpedient(expedient);
		expedient.addExpedientPeticio(expedientPeticioEntity);
		// set annexos as pending to create in db and to move in arxiu
		for (RegistreAnnexEntity registreAnnex : expedientPeticioEntity.getRegistre().getAnnexos()) {
			registreAnnex.updateEstat(RegistreAnnexEstatEnumDto.PENDENT);
		}

	}

	private DocumentDto toDocumentDto(RegistreAnnexEntity registreAnnexEntity) {
		DocumentDto document = new DocumentDto();
		document.setDocumentTipus(DocumentTipusEnumDto.IMPORTAT);
		document.setEstat(DocumentEstatEnumDto.CUSTODIAT);
		document.setData(new Date());
		document.setNom(registreAnnexEntity.getTitol() + " - " + registreAnnexEntity.getRegistre().getIdentificador().replace('/', '_'));
		document.setFitxerNom(registreAnnexEntity.getNom());
		document.setFitxerTamany(registreAnnexEntity.getTamany());
		document.setArxiuUuid(registreAnnexEntity.getUuid());
		document.setDataCaptura(registreAnnexEntity.getNtiFechaCaptura());
		document.setNtiOrigen(toNtiOrigenEnumDto(registreAnnexEntity.getNtiOrigen()));
		document.setNtiTipoDocumental(toDocumentNtiTipoDocumentalEnumDto(registreAnnexEntity.getNtiTipoDocumental()));
		document.setNtiEstadoElaboracion(
				toDocumentNtiEstadoElaboracionEnumDto(registreAnnexEntity.getNtiEstadoElaboracion()));
		document.setFitxerContentType(registreAnnexEntity.getTipusMime());
		document.setNtiVersion("1.0");
		String codiDir3 = entitatRepository.findByUnitatArrel(
				registreAnnexEntity.getRegistre().getEntitatCodi()).getUnitatArrel();
		document.setNtiOrgano(codiDir3);
		document.setValidacioFirmaCorrecte(registreAnnexEntity.isValidacioFirmaCorrecte());
		document.setValidacioFirmaErrorMsg(registreAnnexEntity.getValidacioFirmaErrorMsg());
		document.setAnnexArxiuEstat(registreAnnexEntity.getAnnexArxiuEstat());
		document.setNtiTipoFirma(toNtiTipoFirma(registreAnnexEntity.getFirmaTipus()));
		return document;
	}
	
	public DocumentDto toDocumentDto(
			Document documentArxiu, 
			String numeroRegistre) {
		DocumentDto document = new DocumentDto();
		String tituloDoc = (String) documentArxiu.getMetadades().getMetadadaAddicional("tituloDoc");
		String nomDocument = tituloDoc != null ? (tituloDoc + " - " +  numeroRegistre.replace('/', '_')) : documentArxiu.getNom();
		
		DocumentContingut contingut = documentArxiu.getContingut();
		
		document.setDocumentTipus(DocumentTipusEnumDto.IMPORTAT);
		document.setEstat(DocumentEstatEnumDto.CUSTODIAT);
		document.setData(new Date());
		document.setNom(nomDocument);
		document.setFitxerNom(contingut.getArxiuNom() != null ? contingut.getArxiuNom() : documentArxiu.getNom());
		document.setFitxerTamany(contingut.getTamany());
		document.setArxiuUuid(documentArxiu.getIdentificador());
		document.setDataCaptura(documentArxiu.getMetadades().getDataCaptura());
		document.setNtiOrigen(ArxiuConversions.getOrigen(documentArxiu));
		document.setNtiTipoDocumental(ArxiuConversions.getTipusDocumental(documentArxiu));
		document.setNtiEstadoElaboracion(ArxiuConversions.getEstatElaboracio(documentArxiu));
		document.setNtiTipoFirma(ArxiuConversions.getNtiTipoFirma(documentArxiu));
		document.setFitxerContentType(contingut.getTipusMime());
		document.setNtiVersion("1.0");
		document.setNtiOrgano(getOrgans(documentArxiu));
		return document;
	}
	
	private String getOrgans(Document documentArxiu) {
		String organs = null;
		if (documentArxiu.getMetadades().getOrgans() != null) {
			List<String> metadadaOrgans = documentArxiu.getMetadades().getOrgans();
			StringBuilder organsSb = new StringBuilder();
			boolean primer = true;
			for (String organ: metadadaOrgans) {
				organsSb.append(organ);
				if (primer || metadadaOrgans.size() == 1) {
					primer = false;
				} else {
					organsSb.append(",");
				}
			}
			organs = organsSb.toString();
		}
		return organs;
	}
	

	
	
	private InteressatDto toInteressatDto(RegistreInteressatEntity registreInteressatEntity, Long existingInteressatId) {
		InteressatDto interessatDto = null;
		switch (registreInteressatEntity.getTipus()) {
		case PERSONA_FISICA:
			InteressatPersonaFisicaDto interessatPersonaFisicaDto = new InteressatPersonaFisicaDto();
			interessatPersonaFisicaDto.setDocumentTipus(toInteressatDocumentTipusEnumDto(registreInteressatEntity.getDocumentTipus()));
			interessatPersonaFisicaDto.setDocumentNum(registreInteressatEntity.getDocumentNumero());
			interessatPersonaFisicaDto.setPais(registreInteressatEntity.getPaisCodi());
			interessatPersonaFisicaDto.setProvincia(registreInteressatEntity.getProvinciaCodi());
			interessatPersonaFisicaDto.setMunicipi(registreInteressatEntity.getMunicipiCodi());
			interessatPersonaFisicaDto.setAdresa(registreInteressatEntity.getAdresa());
			interessatPersonaFisicaDto.setCodiPostal(registreInteressatEntity.getCp());
			interessatPersonaFisicaDto.setEmail(registreInteressatEntity.getEmail());
			interessatPersonaFisicaDto.setTelefon(registreInteressatEntity.getTelefon());
			interessatPersonaFisicaDto.setObservacions(registreInteressatEntity.getObservacions());
			interessatPersonaFisicaDto.setTipus(InteressatTipusEnumDto.PERSONA_FISICA);
			interessatPersonaFisicaDto.setNom(registreInteressatEntity.getNom());
			interessatPersonaFisicaDto.setLlinatge1(registreInteressatEntity.getLlinatge1());
			interessatPersonaFisicaDto.setLlinatge2(registreInteressatEntity.getLlinatge2());
			interessatPersonaFisicaDto.setId(existingInteressatId);
			interessatDto = interessatPersonaFisicaDto;
			break;
		case PERSONA_JURIDICA:
			InteressatPersonaJuridicaDto interessatPersonaJuridicaDto = new InteressatPersonaJuridicaDto();
			interessatPersonaJuridicaDto.setDocumentTipus(toInteressatDocumentTipusEnumDto(registreInteressatEntity.getDocumentTipus()));
			interessatPersonaJuridicaDto.setDocumentNum(registreInteressatEntity.getDocumentNumero());
			interessatPersonaJuridicaDto.setPais(registreInteressatEntity.getPaisCodi());
			interessatPersonaJuridicaDto.setProvincia(registreInteressatEntity.getProvinciaCodi());
			interessatPersonaJuridicaDto.setMunicipi(registreInteressatEntity.getMunicipiCodi());
			interessatPersonaJuridicaDto.setAdresa(registreInteressatEntity.getAdresa());
			interessatPersonaJuridicaDto.setCodiPostal(registreInteressatEntity.getCp());
			interessatPersonaJuridicaDto.setEmail(registreInteressatEntity.getEmail());
			interessatPersonaJuridicaDto.setTelefon(registreInteressatEntity.getTelefon());
			interessatPersonaJuridicaDto.setObservacions(registreInteressatEntity.getObservacions());
			interessatPersonaJuridicaDto.setTipus(InteressatTipusEnumDto.PERSONA_JURIDICA);
			interessatPersonaJuridicaDto.setRaoSocial(registreInteressatEntity.getRaoSocial());
			interessatPersonaJuridicaDto.setId(existingInteressatId);
			interessatDto = interessatPersonaJuridicaDto;
			break;
		case ADMINISTRACIO:
			InteressatAdministracioDto interessatAdministracioDto = new InteressatAdministracioDto();
			interessatAdministracioDto.setDocumentTipus(toInteressatDocumentTipusEnumDto(registreInteressatEntity.getDocumentTipus()));
			interessatAdministracioDto.setDocumentNum(registreInteressatEntity.getDocumentNumero());
			interessatAdministracioDto.setPais(registreInteressatEntity.getPaisCodi());
			interessatAdministracioDto.setProvincia(registreInteressatEntity.getProvinciaCodi());
			interessatAdministracioDto.setMunicipi(registreInteressatEntity.getMunicipiCodi());
			interessatAdministracioDto.setAdresa(registreInteressatEntity.getAdresa());
			interessatAdministracioDto.setCodiPostal(registreInteressatEntity.getCp());
			interessatAdministracioDto.setEmail(registreInteressatEntity.getEmail());
			interessatAdministracioDto.setTelefon(registreInteressatEntity.getTelefon());
			interessatAdministracioDto.setObservacions(registreInteressatEntity.getObservacions());
			interessatAdministracioDto.setTipus(InteressatTipusEnumDto.ADMINISTRACIO);
			interessatAdministracioDto.setOrganCodi(registreInteressatEntity.getOrganCodi());
			interessatAdministracioDto.setId(existingInteressatId);
			interessatDto = interessatAdministracioDto;
			break;
		}
		return interessatDto;
	}

	private boolean sameTipus(InteressatTipus tipusReg, InteressatTipusEnumDto tipusInt) {
		if (tipusReg == null || tipusInt == null) return false;
		return tipusReg.name().equals(tipusInt.name());
	}

	public InteressatDto toInteressatMergedDtoCheckingTipus(RegistreInteressatEntity registreInteressatEntity, InteressatDto existingInteressatDto) {

		if (sameTipus(registreInteressatEntity.getTipus(), existingInteressatDto.getTipus())) {
			return toInteressatMergedDto(registreInteressatEntity, existingInteressatDto);
		} else {
			InteressatDto interessatDto = toInteressatDto(registreInteressatEntity, existingInteressatDto.getId());
			return toInteressatMergedDto(interessatDto, existingInteressatDto);
		}

	}

	public InteressatDto toInteressatMergedDto(RegistreInteressatEntity registreInteressatEntity, InteressatDto existingInteressatDto) {

		if (registreInteressatEntity.getDocumentTipus() != null) existingInteressatDto.setDocumentTipus(toInteressatDocumentTipusEnumDto(registreInteressatEntity.getDocumentTipus()));
		if (registreInteressatEntity.getDocumentNumero() != null) existingInteressatDto.setDocumentNum(registreInteressatEntity.getDocumentNumero());
		if (registreInteressatEntity.getPaisCodi() != null) existingInteressatDto.setPais(registreInteressatEntity.getPaisCodi());
		if (registreInteressatEntity.getProvinciaCodi() != null) existingInteressatDto.setProvincia(registreInteressatEntity.getProvinciaCodi());
		if (registreInteressatEntity.getMunicipiCodi() != null) existingInteressatDto.setMunicipi(registreInteressatEntity.getMunicipiCodi());
		if (registreInteressatEntity.getAdresa() != null) existingInteressatDto.setAdresa(registreInteressatEntity.getAdresa());
		if (registreInteressatEntity.getCp() != null) existingInteressatDto.setCodiPostal(registreInteressatEntity.getCp());
		if (registreInteressatEntity.getEmail() != null) existingInteressatDto.setEmail(registreInteressatEntity.getEmail());
		if (registreInteressatEntity.getTelefon() != null) existingInteressatDto.setTelefon(registreInteressatEntity.getTelefon());
		if (registreInteressatEntity.getObservacions() != null) existingInteressatDto.setObservacions(registreInteressatEntity.getObservacions());

		switch (registreInteressatEntity.getTipus()) {
			case PERSONA_FISICA:
				InteressatPersonaFisicaDto interessatPersonaFisicaDto = (InteressatPersonaFisicaDto) existingInteressatDto;
				if (registreInteressatEntity.getNom() != null)
					interessatPersonaFisicaDto.setNom(registreInteressatEntity.getNom());
				if (registreInteressatEntity.getLlinatge1() != null)
					interessatPersonaFisicaDto.setLlinatge1(registreInteressatEntity.getLlinatge1());
				if (registreInteressatEntity.getLlinatge2() != null)
					interessatPersonaFisicaDto.setLlinatge2(registreInteressatEntity.getLlinatge2());
				break;
			case PERSONA_JURIDICA:
				InteressatPersonaJuridicaDto interessatPersonaJuridicaDto = (InteressatPersonaJuridicaDto) existingInteressatDto;
				if (registreInteressatEntity.getRaoSocial() != null)
					interessatPersonaJuridicaDto.setRaoSocial(registreInteressatEntity.getRaoSocial());
				break;
			case ADMINISTRACIO:
				InteressatAdministracioDto interessatAdministracioDto = (InteressatAdministracioDto) existingInteressatDto;
				if (registreInteressatEntity.getOrganCodi() != null && !registreInteressatEntity.getOrganCodi().equals(interessatAdministracioDto.getOrganCodi())) {
					interessatAdministracioDto.setOrganCodi(registreInteressatEntity.getOrganCodi());
					interessatAdministracioDto.setOrganNom(null);
				}
				break;
		}
		return existingInteressatDto;
	}

	public InteressatDto toInteressatMergedDto(InteressatDto interessatRegistreDto, InteressatDto existingInteressatDto) {

		if (interessatRegistreDto.getDocumentTipus() == null) interessatRegistreDto.setDocumentTipus(existingInteressatDto.getDocumentTipus());
		if (interessatRegistreDto.getDocumentNum() == null) interessatRegistreDto.setDocumentNum(existingInteressatDto.getDocumentNum());
		if (interessatRegistreDto.getPais() == null) interessatRegistreDto.setPais(existingInteressatDto.getPais());
		if (interessatRegistreDto.getProvincia() == null) interessatRegistreDto.setProvincia(existingInteressatDto.getProvincia());
		if (interessatRegistreDto.getMunicipi() == null) interessatRegistreDto.setMunicipi(existingInteressatDto.getMunicipi());
		if (interessatRegistreDto.getAdresa() == null) interessatRegistreDto.setAdresa(existingInteressatDto.getAdresa());
		if (interessatRegistreDto.getCodiPostal() == null) interessatRegistreDto.setCodiPostal(existingInteressatDto.getCodiPostal());
		if (interessatRegistreDto.getEmail() == null) interessatRegistreDto.setEmail(existingInteressatDto.getEmail());
		if (interessatRegistreDto.getTelefon() == null) interessatRegistreDto.setTelefon(existingInteressatDto.getTelefon());
		if (interessatRegistreDto.getObservacions() == null) interessatRegistreDto.setObservacions(existingInteressatDto.getObservacions());

		if (InteressatTipusEnumDto.ADMINISTRACIO.equals(interessatRegistreDto.getTipus())) {
			expedientInteressatHelper.updateOrganNom(interessatRegistreDto);
		}
		return interessatRegistreDto;
	}

	private InteressatDocumentTipusEnumDto toInteressatDocumentTipusEnumDto(DocumentTipus documentTipus) {
		InteressatDocumentTipusEnumDto interessatDocumentTipusEnumDto = null;
		if (documentTipus != null) {
			switch (documentTipus) {
			case NIF:
			case CIF:
				interessatDocumentTipusEnumDto = InteressatDocumentTipusEnumDto.NIF;
				break;
			case PASSAPORT:
				interessatDocumentTipusEnumDto = InteressatDocumentTipusEnumDto.PASSAPORT;
				break;
			case NIE:
				interessatDocumentTipusEnumDto = InteressatDocumentTipusEnumDto.DOCUMENT_IDENTIFICATIU_ESTRANGERS;
				break;
			case ALTRES:
				interessatDocumentTipusEnumDto = InteressatDocumentTipusEnumDto.ALTRES_DE_PERSONA_FISICA;
				break;
			case CODI_ORIGEN:
				interessatDocumentTipusEnumDto = InteressatDocumentTipusEnumDto.CODI_ORIGEN;
				break;
			}
		}
		return interessatDocumentTipusEnumDto;
	}

	private NtiOrigenEnumDto toNtiOrigenEnumDto(NtiOrigen ntiOrigen) {
		NtiOrigenEnumDto ntiOrigenEnumDto = null;
		if (ntiOrigen != null) {
			switch (ntiOrigen) {
			case CIUTADA:
				ntiOrigenEnumDto = NtiOrigenEnumDto.O0;
				break;
			case ADMINISTRACIO:
				ntiOrigenEnumDto = NtiOrigenEnumDto.O1;
				break;
			}
		}
		return ntiOrigenEnumDto;
	}

	private DocumentNtiEstadoElaboracionEnumDto toDocumentNtiEstadoElaboracionEnumDto(
			NtiEstadoElaboracion ntiEstadoElaboracion) {
		DocumentNtiEstadoElaboracionEnumDto documentNtiEstadoElaboracionEnumDto = null;
		if (ntiEstadoElaboracion != null) {
			switch (ntiEstadoElaboracion) {
			case ORIGINAL:
				documentNtiEstadoElaboracionEnumDto = DocumentNtiEstadoElaboracionEnumDto.EE01;
				break;
			case COPIA_ELECT_AUTENTICA_CANVI_FORMAT:
				documentNtiEstadoElaboracionEnumDto = DocumentNtiEstadoElaboracionEnumDto.EE02;
				break;
			case COPIA_ELECT_AUTENTICA_PAPER:
				documentNtiEstadoElaboracionEnumDto = DocumentNtiEstadoElaboracionEnumDto.EE03;
				break;
			case COPIA_ELECT_AUTENTICA_PARCIAL:
				documentNtiEstadoElaboracionEnumDto = DocumentNtiEstadoElaboracionEnumDto.EE04;
				break;
			case ALTRES:
				documentNtiEstadoElaboracionEnumDto = DocumentNtiEstadoElaboracionEnumDto.EE99;
				break;
			}
		}
		return documentNtiEstadoElaboracionEnumDto;
	}

	private String toDocumentNtiTipoDocumentalEnumDto(NtiTipoDocumento ntiTipoDocumento) {
		String documentNtiTipoDocumental = null;
		if (ntiTipoDocumento != null) {
			switch (ntiTipoDocumento) {
			case RESOLUCIO:
				documentNtiTipoDocumental = "TD01";
				break;
			case ACORD:
				documentNtiTipoDocumental = "TD02";
				break;
			case CONTRACTE:
				documentNtiTipoDocumental = "TD03";
				break;
			case CONVENI:
				documentNtiTipoDocumental = "TD04";
				break;
			case DECLARACIO:
				documentNtiTipoDocumental = "TD05";
				break;
			case COMUNICACIO:
				documentNtiTipoDocumental = "TD06";
				break;
			case NOTIFICACIO:
				documentNtiTipoDocumental = "TD07";
				break;
			case PUBLICACIO:
				documentNtiTipoDocumental = "TD08";
				break;
			case JUSTIFICANT_RECEPCIO:
				documentNtiTipoDocumental = "TD09";
				break;
			case ACTA:
				documentNtiTipoDocumental = "TD10";
				break;
			case CERTIFICAT:
				documentNtiTipoDocumental = "TD11";
				break;
			case DILIGENCIA:
				documentNtiTipoDocumental = "TD12";
				break;
			case INFORME:
				documentNtiTipoDocumental = "TD13";
				break;
			case SOLICITUD:
				documentNtiTipoDocumental = "TD14";
				break;
			case DENUNCIA:
				documentNtiTipoDocumental = "TD15";
				break;
			case ALEGACIO:
				documentNtiTipoDocumental = "TD16";
				break;
			case RECURS:
				documentNtiTipoDocumental = "TD17";
				break;
			case COMUNICACIO_CIUTADA:
				documentNtiTipoDocumental = "TD18";
				break;
			case FACTURA:
				documentNtiTipoDocumental = "TD19";
				break;
			case ALTRES_INCAUTATS:
				documentNtiTipoDocumental = "TD20";
				break;
			case ALTRES:
				documentNtiTipoDocumental = "TD99";
				break;
			case LLEI:
				documentNtiTipoDocumental = "TD51";
				break;
			case MOCIO:
				documentNtiTipoDocumental = "TD52";
				break;
			case INSTRUCCIO:
				documentNtiTipoDocumental = "TD53";
				break;
			case CONVOCATORIA:
				documentNtiTipoDocumental = "TD54";
				break;
			case ORDRE_DIA:
				documentNtiTipoDocumental = "TD55";
				break;
			case INFORME_PONENCIA:
				documentNtiTipoDocumental = "TD56";
				break;
			case DICTAMEN_COMISSIO:
				documentNtiTipoDocumental = "TD57";
				break;
			case INICIATIVA_LEGISLATIVA:
				documentNtiTipoDocumental = "TD58";
				break;
			case PREGUNTA:
				documentNtiTipoDocumental = "TD59";
				break;
			case INTERPELACIO:
				documentNtiTipoDocumental = "TD60";
				break;
			case RESPOSTA:
				documentNtiTipoDocumental = "TD61";
				break;
			case PROPOSICIO_NO_LLEI:
				documentNtiTipoDocumental = "TD62";
				break;
			case ESMENA:
				documentNtiTipoDocumental = "TD63";
				break;
			case PROPOSTA_RESOLUCIO:
				documentNtiTipoDocumental = "TD64";
				break;
			case COMPAREIXENSA:
				documentNtiTipoDocumental = "TD65";
				break;
			case SOLICITUD_INFORMACIO:
				documentNtiTipoDocumental = "TD66";
				break;
			case ESCRIT:
				documentNtiTipoDocumental = "TD67";
				break;
			case PETICIO:
				documentNtiTipoDocumental = "TD69";
				break;
			}
		}
		return documentNtiTipoDocumental;
	}

	public Long createCarpetaForDocFromAnnex(ExpedientEntity expedientEntity, Long entitatId, String nom, String rolActual) {
		// check if already exists in db
		boolean carpetaExistsInDB = false;
		Long carpetaId = null;
		CarpetaEntity carpetaEntity = null;
		for (ContingutEntity contingut : expedientEntity.getFills()) {
			if (contingut instanceof CarpetaEntity && contingut.getNom().equals(nom)) {
				carpetaExistsInDB = true;
				carpetaId = contingut.getId();
				carpetaEntity = (CarpetaEntity)contingut;
			}
		}
		// check if already exists in arxiu
		Expedient expedient = pluginHelper.arxiuExpedientConsultar(expedientEntity);
		boolean carpetaExistsInArxiu = false;
		String carpetaUuid = null;
		if (expedient.getContinguts() != null) {
			for (ContingutArxiu contingutArxiu : expedient.getContinguts()) {
				String replacedNom = ArxiuConversioHelper.revisarContingutNom(nom);
				if (contingutArxiu.getTipus() == ContingutTipus.CARPETA &&
						contingutArxiu.getNom().equals(replacedNom)) {
					carpetaExistsInArxiu = true;
					carpetaUuid = contingutArxiu.getIdentificador();
				}
			}
		}
		if (carpetaExistsInDB && carpetaExistsInArxiu && carpetaEntity.getArxiuUuid() == null) {
			carpetaEntity.updateArxiu(carpetaUuid);
		}
		if (!carpetaExistsInDB || !carpetaExistsInArxiu) {
			CarpetaDto carpetaDto = carpetaHelper.create(
					entitatId,
					expedientEntity.getId(),
					nom,
					carpetaExistsInDB,
					carpetaId,
					carpetaExistsInArxiu,
					carpetaUuid, 
					true, 
					rolActual, 
					false);
			carpetaId = carpetaDto.getId();
		}
		return carpetaId;
	}
	
	
	public void concurrencyCheckExpedientJaTancat(ExpedientEntity expedient) {
		if (expedient.getEstat() == ExpedientEstatEnumDto.TANCAT) { 
			throw new RuntimeException("L'expedient ja ha estat tancat per una altre persona. No és possible fer cap canvi");
		}
	}
	
	
	//crea les carpetes per defecte definides al procediment
	private void crearCarpetesMetaExpedient(
			Long entitatId, 
			MetaExpedientEntity metaExpedient, 
			ExpedientEntity expedient) {
		List<MetaExpedientCarpetaDto> carpetesMetaExpedient = metaExpedientCarpetaHelper.findCarpetesArrelMetaExpedient(metaExpedient);
		
		for (MetaExpedientCarpetaDto metaExpedientCarpeta : carpetesMetaExpedient) {

			CarpetaDto carpetaPare = carpetaHelper.create(
					entitatId, 
					expedient.getId(), 
					metaExpedientCarpeta.getNom(), 
					false, 
					null, 
					false, 
					null, 
					false, 
					null, 
					false);
			if (! metaExpedientCarpeta.getFills().isEmpty()) {
				crearSubCarpetes(
						metaExpedientCarpeta.getFills(), 
						entitatId, 
						carpetaPare);
			}
		}
	}
	
	private void crearSubCarpetes(
			Set<MetaExpedientCarpetaDto> subCarpetes, 
			Long entitatId, 
			CarpetaDto pare) {
		for (MetaExpedientCarpetaDto metaExpedientCarpetaDto : subCarpetes) {
			CarpetaDto subCarpeta = carpetaHelper.create(
					entitatId, 
					pare.getId(), 
					metaExpedientCarpetaDto.getNom(), 
					false, 
					null, 
					false, 
					null, false, null, true);
				
			crearSubCarpetes(
					metaExpedientCarpetaDto.getFills(), 
					entitatId, 
					subCarpeta);
		}
	}

	
	
	private DocumentNtiTipoFirmaEnumDto toNtiTipoFirma(FirmaTipus firmaTipus) {
		DocumentNtiTipoFirmaEnumDto documentNtiTipoFirmaEnumDto = null;
		
		if (firmaTipus != null) {
			switch (firmaTipus) {
			case CSV:
				documentNtiTipoFirmaEnumDto = DocumentNtiTipoFirmaEnumDto.TF01;
				break;
			case XADES_DET:
				documentNtiTipoFirmaEnumDto = DocumentNtiTipoFirmaEnumDto.TF02;
				break;
			case XADES_ENV:
				documentNtiTipoFirmaEnumDto = DocumentNtiTipoFirmaEnumDto.TF03;
				break;
			case CADES_DET:
				documentNtiTipoFirmaEnumDto = DocumentNtiTipoFirmaEnumDto.TF04;
				break;
			case CADES_ATT:
				documentNtiTipoFirmaEnumDto = DocumentNtiTipoFirmaEnumDto.TF05;
				break;
			case PADES:
				documentNtiTipoFirmaEnumDto = DocumentNtiTipoFirmaEnumDto.TF06;
				break;
			case SMIME:
				documentNtiTipoFirmaEnumDto = DocumentNtiTipoFirmaEnumDto.TF07;
				break;
			case ODT:
				documentNtiTipoFirmaEnumDto = DocumentNtiTipoFirmaEnumDto.TF08;
				break;
			case OOXML:
				documentNtiTipoFirmaEnumDto = DocumentNtiTipoFirmaEnumDto.TF09;
				break;
			}
		}

		return documentNtiTipoFirmaEnumDto;
	}

	
	private List<Long> toListLong(List<Serializable> original) {
		List<Long> listLong = new ArrayList<Long>(original.size());
		for (Serializable s: original) { 
			listLong.add((Long)s); 
		}
		return listLong;
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientHelper.class);

}