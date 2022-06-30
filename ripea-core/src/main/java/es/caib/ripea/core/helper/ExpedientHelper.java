
/**
 * 
 */
package es.caib.ripea.core.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import es.caib.distribucio.ws.backofficeintegracio.DocumentTipus;
import es.caib.distribucio.ws.backofficeintegracio.NtiEstadoElaboracion;
import es.caib.distribucio.ws.backofficeintegracio.NtiOrigen;
import es.caib.distribucio.ws.backofficeintegracio.NtiTipoDocumento;
import es.caib.plugins.arxiu.api.Carpeta;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.ContingutTipus;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.Expedient;
import es.caib.plugins.arxiu.api.Firma;
import es.caib.plugins.arxiu.api.FirmaTipus;
import es.caib.plugins.arxiu.caib.ArxiuConversioHelper;
import es.caib.ripea.core.api.dto.CarpetaDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiTipoFirmaEnumDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientEstatDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.InteressatAdministracioDto;
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
import es.caib.ripea.core.api.dto.PermissionEnumDto;
import es.caib.ripea.core.api.dto.RegistreAnnexEstatEnumDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.entity.CarpetaEntity;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DadaEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientEstatEntity;
import es.caib.ripea.core.entity.ExpedientPeticioEntity;
import es.caib.ripea.core.entity.InteressatEntity;
import es.caib.ripea.core.entity.MetaDadaEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
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
import es.caib.ripea.core.repository.MetaDadaRepository;
import es.caib.ripea.core.repository.MetaDocumentRepository;
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


	public static List<DocumentDto> expedientsWithImportacio = new ArrayList<DocumentDto>();
	

	/**
	 * 
	 * This method is synchronized to avoid possibility of getting and using same sequence by concurrent threads, to avoid creating expedients with the same name by concurrent threads, to avoid creating expedients from the same annotation by concurrent threads
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Long create(
			Long entitatId,
			Long metaExpedientId,
			Long metaExpedientDominiId,
			Long organGestorId,
			Long pareId,
			Integer any,
			Long sequencia,
			String nom,
			Long expedientPeticioId,
			boolean associarInteressats,
			Long grupId, 
			String rolActual) {

		logger.info(
				"Expedient crear Helper START(" +
						"entitatId=" + entitatId + ", " +
						"metaExpedientId=" + metaExpedientId + ", " +
						"metaExpedientDominiId=" + metaExpedientDominiId + ", " +
						"organGestorId=" + organGestorId + ", " +
						"pareId=" + pareId + ", " +
						"any=" + any + ", " +
						"sequencia=" + sequencia + ", " +
						"nom=" + nom + ", " +
						"expedientPeticioId=" + expedientPeticioId + ")");


//		try {
//			Thread.sleep(5000L);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
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
		ExpedientDto exp = findByMetaExpedientAndPareAndNomAndEsborrat(
				entitatId,
				metaExpedientId,
				pareId,
				nom,
				0,
				rolActual,
				organGestorId);
		if (exp != null) {
			throw new ValidationException(
					"<creacio>",
					ExpedientEntity.class,
					"Ja existeix expedient amb aquest nom");
		}

		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, true, false);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedientPerExpedient(
				entitat,
				metaExpedientId,
				false,
				false,
				true,
				false, 
				false, 
				rolActual, 
				organGestorId);
		
		OrganGestorEntity organGestor = getOrganGestorForExpedient(
				metaExpedient,
				organGestorId,
				ExtendedPermission.CREATE, rolActual);

//		if (metaExpedientDominiId != null) {
//			metaExpedientDomini = metaExpedientDominiRepository.findOne(metaExpedientDominiId);
//		}
		ContingutEntity contingutPare = null;
		if (pareId != null) {
			contingutPare = contingutHelper.comprovarContingutDinsExpedientModificable(
					entitatId,
					pareId,
					false,
					false,
					true,
					false, 
					false, 
					true, rolActual);
		}
		contingutHelper.comprovarNomValid(contingutPare, nom, null, ExpedientEntity.class);
//		comprovarSiExpedientAmbMateixNom(
//				metaExpedient,
//				contingutPare,
//				nom,
//				null,
//				ExpedientEntity.class);
		ExpedientEntity expedient = contingutHelper.crearNouExpedient(
				nom,
				metaExpedient,
				contingutPare,
				metaExpedient.getEntitat(),
				organGestor,
				"1.0",
				metaExpedient.getEntitat().getUnitatArrel(),
				new Date(),
				any,
				sequencia,
				true,
				grupId);
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
			expedient.updateExpedientEstat(estatInicial);
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
				associateInteressats(expedient.getId(), entitat.getId(), expedientPeticioId, PermissionEnumDto.CREATE, rolActual);
			}
			expedientPeticioHelper.canviEstatExpedientPeticio(expedientPeticioId, ExpedientPeticioEstatEnumDto.PROCESSAT_PENDENT);
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
	public boolean createArxiu(
			Long expedientId) {

		logger.info(
				"Expedient crear ARXIU Helper START(" +
						"expedientId=" + expedientId + ")");
		boolean ok = true;
		ExpedientEntity expedient = expedientRepository.findOne(expedientId);

		try {
			// create expedient in arxiu
			contingutHelper.arxiuPropagarModificacio(
					expedient,
					null,
					false,
					false,
					null,
					false);

			for (InteressatEntity interessat : expedient.getInteressats()) {
				interessat.updateArxiuIntent(true);
			}
		} catch (Exception ex) {
			ok = false;
			logger.error(
					"Error al custodiar expedient en arxiu  (" + "id=" + expedient.getId() + ")",
					ex);
			for (InteressatEntity interessat : expedient.getInteressats()) {
				interessat.updateArxiuIntent(false);
			}
		}
		expedient.updateArxiuIntent();
		
		boolean throwExcepcion = false;//throwExcepcion = true;
		if (throwExcepcion) {
			throw new RuntimeException("Mock excepcion després de crear expedient en arxiu");
		}
		
		logger.info(
				"Expedient crear ARXIU Helper END(" +
						"sequencia=" + expedient.getSequencia() + ", " +
						"any=" + expedient.getAny() + ", " +
						"metaExpedient=" + expedient.getMetaExpedient().getId() + " - " + expedient.getMetaExpedient().getCodi() + ")");
		
		return ok;
	}


	@Transactional
	public void associateInteressats(Long expedientId, Long entitatId, Long expedientPeticioId, PermissionEnumDto permission, String rolActual) {
		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		ExpedientEntity expedientEntity = expedientRepository.findOne(expedientId);
		Set<InteressatEntity> existingInteressats = expedientEntity.getInteressats();
		for (RegistreInteressatEntity interessatRegistre : expedientPeticioEntity.getRegistre().getInteressats()) {
			boolean alreadyExists = false;
			InteressatEntity interessatExpedient = null;
			for (InteressatEntity interessatExp : existingInteressats) {
				if (interessatExp.getDocumentNum() != null && interessatRegistre.getDocumentNumero() != null && interessatExp.getDocumentNum().equals(interessatRegistre.getDocumentNumero())) {
					alreadyExists = true;
					interessatExpedient = interessatExp;
				}
			}
			if (!alreadyExists) {
				InteressatDto createdInteressat = expedientInteressatHelper.create(
						entitatId,
						expedientId,
						null,
						toInteressatDto(interessatRegistre, null),
						true, 
						permission, 
						rolActual, 
						false);
				if (interessatRegistre.getRepresentant() != null) {
					expedientInteressatHelper.create(
							entitatId,
							expedientId,
							createdInteressat.getId(),
							toInteressatDto(interessatRegistre.getRepresentant(), null),
							true, 
							permission, 
							rolActual, 
							false);
				}
			} else {
				RegistreInteressatEntity representantRegistre = interessatRegistre.getRepresentant();
				Long idRepresentantExpedient = representantRegistre != null && interessatExpedient.getRepresentant() != null ? interessatExpedient.getRepresentant().getId() : null; //modificar o afegir
				expedientInteressatHelper.update(
						entitatId, 
						expedientId, 
						interessatExpedient.getId(), 
						toInteressatDto(interessatRegistre, interessatExpedient.getId()), 
						true,
						representantRegistre != null ? toInteressatDto(representantRegistre, idRepresentantExpedient) : null, rolActual);
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void relateExpedientWithPeticioAndSetAnnexosPendentNewTransaction(
			Long expedientPeticioId,
			Long expedientId, 
			String rolActual, 
			Long entitatId, 
			boolean associarInteressats, 
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
					entitatId,
					expedientId,
					false,
					true,
					false,
					false,
					false, 
					false, 
					rolActual);
			agafar(expedient, usuariHelper.getUsuariAutenticat().getCodi());
		}
		
		relateExpedientWithPeticioAndSetAnnexosPendent(expedientPeticioId, expedientId);
		expedientPeticioHelper.canviEstatExpedientPeticio(expedientPeticioId, ExpedientPeticioEstatEnumDto.PROCESSAT_PENDENT);
		
		if (associarInteressats) {
			associateInteressats(expedientId, entitatId, expedientPeticioId, PermissionEnumDto.WRITE, rolActual);
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
	 * @param rolActual TODO
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

		if (expedientEntity.getArxiuUuid() == null) {
			throw new RuntimeException("Annex no s'ha processat perque expedient no es creat en arxiu");
		}
		
		if (registreAnnexEntity.getDocument() == null) {
			
			logger.info("Creant carpeta i documents de expedient peticio (" + "expedientId=" +
					expedientId + ", " + "registreAnnexId=" + registreAnnexId + 
					", " + "registreAnnexNom=" + registreAnnexEntity.getNom() + 
					", " + "registreAnnexTitol=" + registreAnnexEntity.getTitol() +
					", " + "expedientPeticioId=" + expedientPeticioEntity.getId() + ")");
		
			// ########################## CREATE CARPETA IN DB AND IN ARXIU ######################
			// create carpeta ind db and arxiu if doesnt already exists
			Long carpetaId = createCarpetaForDocFromAnnex(
					expedientEntity,
					entitat.getId(),
					"Registre entrada: " + expedientPeticioEntity.getRegistre().getIdentificador(), 
					rolActual);
			carpetaEntity = carpetaRepository.findOne(carpetaId);
		
			// ########################### CREATE DOCUMENT IN DB ########################
			DocumentDto documentDto = toDocumentDto(registreAnnexEntity);
			contingutHelper.comprovarNomValid(
					carpetaEntity,
					documentDto.getNom(),
					null,
					DocumentEntity.class);
			
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
					null);
			FitxerDto fitxer = new FitxerDto();
			fitxer.setNom(documentDto.getFitxerNom());
			fitxer.setContentType(documentDto.getFitxerContentType());
			fitxer.setContingut(documentDto.getFitxerContingut());
			if (documentDto.getFitxerContingut() != null) {
				documentHelper.actualitzarFitxerDocument(docEntity, fitxer);
				if (documentDto.isAmbFirma()) {
					documentHelper.validaFirmaDocument(docEntity, fitxer, documentDto.getFirmaContingut());
				}
			} else {
				docEntity.updateFitxer(fitxer.getNom(), fitxer.getContentType(), fitxer.getContingut());
			}
			if (registreAnnexEntity.getFirmaTipus() != null) {
				docEntity.updateEstat(DocumentEstatEnumDto.CUSTODIAT);
				docEntity.setNtiTipoFirma(toNtiTipoFirma(registreAnnexEntity.getFirmaTipus()));
			} else {
				docEntity.updateEstat(DocumentEstatEnumDto.DEFINITIU);
			}
			
			registreAnnexEntity.updateDocument(docEntity);
			contingutLogHelper.logCreacio(docEntity, true, true);
		}

		Exception exception = null;
		
		// ############################ MOVE DOCUMENT IN ARXIU ####################
		exception = moveDocumentArxiu(registreAnnexEntity.getId());
		return exception;
	}
	
	
	public Exception moveDocumentArxiu(Long registreAnnexId) {
		
		RegistreAnnexEntity registreAnnexEntity = registreAnnexRepository.findOne(registreAnnexId);
		DocumentEntity docEntity = registreAnnexEntity.getDocument();
		ContingutEntity pare = docEntity.getPare();
		ExpedientEntity expedientEntity = docEntity.getExpedient();
		Exception exception = null;
		
		// put arxiu uuid of annex
		if (docEntity.getArxiuUuid() == null || docEntity.getArxiuUuid().isEmpty()) {
			docEntity.updateArxiu(registreAnnexEntity.getUuid());
		}
		documentRepository.saveAndFlush(docEntity);
		
		try {
			String uuidDesti = contingutHelper.arxiuPropagarMoviment(
					docEntity,
					pare,
					expedientEntity.getArxiuUuid());
			// if document was dispatched, update uuid to new document
			if (uuidDesti != null) {
				docEntity.updateArxiu(uuidDesti);
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
		return moveDocumentArxiu(registreAnnexId);
	}
	
	
	/**
	 * Creates document from registre annex
	 * @param expedientId 
	 * 
	 * @param registreAnnexId
	 * @param expedientPeticioId
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public DocumentEntity crearDocFromUuid(
			Long expedientId, 
			String arxiuUuid, 
			Long expedientPeticioId) {
		ExpedientEntity expedientEntity;
		EntitatEntity entitat;
		CarpetaEntity carpetaEntity = null;
		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		expedientEntity = expedientRepository.findOne(expedientId);
		Document documentDetalls = pluginHelper.arxiuDocumentConsultar(
				null, 
				arxiuUuid, 
				null, 
				false, 
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
		MetaDocumentEntity metaDocument = metaDocumentRepository.findByMetaExpedientAndPerDefecteTrue(expedientEntity.getMetaExpedient());
		
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
				null);
		FitxerDto fitxer = new FitxerDto();
		fitxer.setNom(documentDto.getFitxerNom());
		fitxer.setContentType(documentDto.getFitxerContentType());
		fitxer.setContingut(documentDto.getFitxerContingut());
		if (documentDto.getFitxerContingut() != null) {
			documentHelper.actualitzarFitxerDocument(docEntity, fitxer);
			if (documentDto.isAmbFirma()) {
				documentHelper.validaFirmaDocument(docEntity, fitxer, documentDto.getFirmaContingut());
			}
		} else {
			docEntity.updateFitxer(fitxer.getNom(), fitxer.getContentType(), fitxer.getContingut());

		}
		docEntity.updateEstat(DocumentEstatEnumDto.CUSTODIAT);
		docEntity.setNtiTipoFirma(documentDto.getNtiTipoFirma());
		
		// ############################## MOVE DOCUMENT IN ARXIU
		// ##########################################
		// put arxiu uuid of annex
		docEntity.updateArxiu(documentDto.getArxiuUuid());
		documentRepository.saveAndFlush(docEntity);
		if (isCarpetaActive) {
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
				String uuidDesti = contingutHelper.arxiuPropagarMoviment(
						docEntity,
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
				String uuidDesti = contingutHelper.arxiuPropagarMoviment(
						docEntity,
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
	
	public void inicialitzarExpedientsWithImportacio() {
		expedientsWithImportacio = new ArrayList<DocumentDto>();
	}
	
	public List<DocumentDto> consultaExpedientsAmbImportacio() {
		return expedientsWithImportacio;
	}
	
	public ExpedientEntity updateNomExpedient(ExpedientEntity expedient, String nom) {
		contingutHelper.comprovarNomValid(expedient.getPare(), nom, expedient.getId(), ExpedientEntity.class);
		String nomOriginal = expedient.getNom();
		expedient.update(nom);
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
				expedient,
				organGestorEntity);
		organGestorHelper.crearExpedientOrganPares(
				expedient,
				organGestorEntity);
		
		return expedient;
	}
	


	public ExpedientDto findByMetaExpedientAndPareAndNomAndEsborrat(
			Long entitatId,
			Long metaExpedientId,
			Long pareId,
			String nom,
			int esborrat,
			String rolActual,
			Long organId) {
		logger.debug(
				"Consultant expedient (" + "entitatId=" + entitatId + ", " + "metaExpedientId=" + metaExpedientId +
						", " + "pareId=" + pareId + ", " + "nom=" + nom + ", " + "esborrat=" + esborrat + "organId=" + organId + "rolActual=" + rolActual + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, true, false);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedientPerExpedient(
				entitat,
				metaExpedientId,
				false,
				false,
				true,
				false,
				false,
				rolActual,
				organId);

		ContingutEntity contingutPare = null;
		if (pareId != null) {
			contingutPare = contingutHelper.comprovarContingutDinsExpedientModificable(
					entitatId,
					pareId,
					false,
					false,
					true,
					false, false, true, null);
		}
		ExpedientEntity expedient = expedientRepository.findByMetaExpedientAndPareAndNomAndEsborrat(
				metaExpedient,
				contingutPare,
				nom,
				esborrat);
		return expedient == null ? null : toExpedientDto(expedient, true, null, false);
	}


	@Transactional(readOnly = true)
	public ExpedientDto toExpedientDto(ExpedientEntity expedient, boolean ambPathIPermisos, String rolActual, boolean onlyForList) {
		ExpedientDto expedientDto = (ExpedientDto)contingutHelper.toContingutDto(
				expedient,
				ambPathIPermisos,
				false,
				false,
				false,
				ambPathIPermisos,
				false,
				false,
				rolActual,
				onlyForList, null);
		return expedientDto;
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
			return expedient.getCodi() + "/" + expedient.getSequencia() + "/" + expedient.getAny();
		}
	}

	public ExpedientDto toExpedientDto(ExpedientEntity expedient, boolean ambPathIPermisos) {
		return (ExpedientDto)contingutHelper.toContingutDto(
				expedient,
				ambPathIPermisos,
				false,
				false,
				false,
				ambPathIPermisos,
				false,
				false, null, false, null);
	}


	public ExpedientDto toExpedientDto(ExpedientEntity entity) {
		ExpedientDto dto = new ExpedientDto();

		dto.setNumero(calcularNumero(entity));
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
		if (entity.getExpedientEstat() != null) {
			dto.setExpedientEstat(conversioTipusHelper.convertir(
					entity.getExpedientEstat(),
					ExpedientEstatDto.class));
		}

		return dto;
	}
	public void agafar(ExpedientEntity expedient, String usuariCodi) {

		ExpedientEntity expedientSuperior = contingutHelper.getExpedientSuperior(expedient, false, false, false, false, null);
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
	}
	
	public void alliberar(ExpedientEntity expedient) {
		UsuariEntity prevUserAgafat = expedient.getAgafatPer();
		expedient.updateAgafatPer(null);
		contingutLogHelper.log(expedient, LogTipusEnumDto.ALLIBERAR, prevUserAgafat.getCodi(), null, false, false);
	}
	
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public Exception guardarExpedientArxiu(
			Long expId) {
		
		logger.info("Guardar expedient arxiu (id=" + expId + ")");
		Exception exception = null;
		
		ExpedientEntity expedient = expedientRepository.findOne(expId);
		
		try {
			contingutHelper.arxiuPropagarModificacio(
					expedient,
					null,
					false,
					false,
					null, false);
			
			for (InteressatEntity interessat : expedient.getInteressats()) {
				interessat.updateArxiuIntent(true);
			}
			
		} catch (Exception ex) {
			logger.error("Error al custodiar expedient en arxiu  (" +
					"id=" + expedient.getId() + ")",
					ex);
			exception = ex;
			for (InteressatEntity interessat : expedient.getInteressats()) {
				interessat.updateArxiuIntent(false);
			}
		}
		expedient.updateArxiuIntent();
		
		return exception;
	}
	
	
	public FitxerDto exportarExpedient(
			EntitatEntity entitatActual, 
			List<ExpedientEntity> expedients,
			boolean exportar) throws IOException {
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
			List<ContingutEntity> fillsOrder1 = contingutRepository.findByPareAndEsborratAndOrdenat(
					expedient,
					0,
					contingutHelper.isOrdenacioPermesa() ? new Sort("ordre") : new Sort("createdDate"));
			
			List<ContingutEntity> fillsOrder2 = contingutRepository.findByPareAndEsborratSenseOrdre(
					expedient,
					0,
					new Sort("createdDate"));
			
			continguts.addAll(fillsOrder1);
			continguts.addAll(fillsOrder2);
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
			
			FitxerDto indexDoc = contingutHelper.generarIndex(entitatActual, expedients, exportar);
			contingutHelper.crearNovaEntrada(indexDoc.getNom(), indexDoc, zos);
			zos.close();
			
			resultat.setNom(messageHelper.getMessage("expedient.service.exportacio.index") + " " + expedient.getNom() + ".zip");
			resultat.setContentType("application/zip");
			resultat.setContingut(baos.toByteArray());
		} else {
			resultat = contingutHelper.generarIndex(entitatActual, expedients, exportar);
		}
		return resultat;
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
		List<ContingutEntity> fillsOrder1 = contingutRepository.findByPareAndEsborratAndOrdenat(
				carpetaActual,
				0,
				contingutHelper.isOrdenacioPermesa() ? new Sort("ordre") : new Sort("createdDate"));
		
		List<ContingutEntity> fillsOrder2 = contingutRepository.findByPareAndEsborratSenseOrdre(
				carpetaActual,
				0,
				new Sort("createdDate"));
		
		contingutsCarpetaActual.addAll(fillsOrder1);
		contingutsCarpetaActual.addAll(fillsOrder2);
		
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
		return document;
	}
	
	public DocumentDto toDocumentDto(
			Document documentArxiu, 
			String numeroRegistre) {
		DocumentDto document = new DocumentDto();
		String tituloDoc = (String) documentArxiu.getMetadades().getMetadadaAddicional("tituloDoc");
		String nomDocument = tituloDoc != null ? (tituloDoc + " - " +  numeroRegistre.replace('/', '_')) : documentArxiu.getNom();
		
		document.setDocumentTipus(DocumentTipusEnumDto.IMPORTAT);
		document.setEstat(DocumentEstatEnumDto.CUSTODIAT);
		document.setData(new Date());
		document.setNom(nomDocument);
		document.setFitxerNom(documentArxiu.getNom());
		document.setArxiuUuid(documentArxiu.getIdentificador());
		document.setDataCaptura(documentArxiu.getMetadades().getDataCaptura());
		document.setNtiOrigen(getOrigen(documentArxiu));
		document.setNtiTipoDocumental(getTipusDocumental(documentArxiu));
		document.setNtiEstadoElaboracion(getEstatElaboracio(documentArxiu));
		document.setNtiTipoFirma(getNtiTipoFirma(documentArxiu));
		document.setFitxerContentType(documentArxiu.getContingut().getTipusMime());
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
	
	private static DocumentNtiEstadoElaboracionEnumDto getEstatElaboracio(Document document) {
		DocumentNtiEstadoElaboracionEnumDto estatElaboracio = null;

		switch (document.getMetadades().getEstatElaboracio()) {
		case ORIGINAL:
			estatElaboracio = DocumentNtiEstadoElaboracionEnumDto.EE01;
			break;
		case COPIA_CF:
			estatElaboracio = DocumentNtiEstadoElaboracionEnumDto.EE02;
			break;
		case COPIA_DP:
			estatElaboracio = DocumentNtiEstadoElaboracionEnumDto.EE03;
			break;
		case COPIA_PR:
			estatElaboracio = DocumentNtiEstadoElaboracionEnumDto.EE04;
			break;
		case ALTRES:
			estatElaboracio = DocumentNtiEstadoElaboracionEnumDto.EE99;
			break;
		}
		return estatElaboracio;
	}
	
	private static NtiOrigenEnumDto getOrigen(Document document) {
		NtiOrigenEnumDto origen = null;

		switch (document.getMetadades().getOrigen()) {
		case CIUTADA:
			origen = NtiOrigenEnumDto.O0;
			break;
		case ADMINISTRACIO:
			origen = NtiOrigenEnumDto.O1;
			break;
		}
		return origen;
	}
	
	private DocumentNtiTipoFirmaEnumDto getNtiTipoFirma(Document documentArxiu) {
		DocumentNtiTipoFirmaEnumDto ntiTipoFirma = null;
		if (documentArxiu.getFirmes() != null && !documentArxiu.getFirmes().isEmpty()) {
			FirmaTipus firmaTipus = null;
			for (Firma firma: documentArxiu.getFirmes()) {
				if (firma.getTipus() != FirmaTipus.CSV) {
					firmaTipus = firma.getTipus();
					break;
				}
			}
			switch (firmaTipus) {
			case CSV:
				ntiTipoFirma = DocumentNtiTipoFirmaEnumDto.TF01;
				break;
			case XADES_DET:
				ntiTipoFirma = DocumentNtiTipoFirmaEnumDto.TF02;
				break;
			case XADES_ENV:
				ntiTipoFirma = DocumentNtiTipoFirmaEnumDto.TF03;
				break;
			case CADES_DET:
				ntiTipoFirma = DocumentNtiTipoFirmaEnumDto.TF04;
				break;
			case CADES_ATT:
				ntiTipoFirma = DocumentNtiTipoFirmaEnumDto.TF05;
				break;
			case PADES:
				ntiTipoFirma = DocumentNtiTipoFirmaEnumDto.TF06;
				break;
			case SMIME:
				ntiTipoFirma = DocumentNtiTipoFirmaEnumDto.TF07;
				break;
			case ODT:
				ntiTipoFirma = DocumentNtiTipoFirmaEnumDto.TF08;
				break;
			case OOXML:
				ntiTipoFirma = DocumentNtiTipoFirmaEnumDto.TF09;
				break;
			}
		}
		return ntiTipoFirma;
	}
	
	@SuppressWarnings("incomplete-switch")
	private static String getTipusDocumental(Document document) {
		String tipusDocumental = null;

		if (document.getMetadades().getTipusDocumental() != null) {
			switch (document.getMetadades().getTipusDocumental()) {
			case RESOLUCIO:
				tipusDocumental = "TD01";
				break;
			case ACORD:
				tipusDocumental = "TD02";
				break;
			case CONTRACTE:
				tipusDocumental = "TD03";
				break;
			case CONVENI:
				tipusDocumental = "TD04";
				break;
			case DECLARACIO:
				tipusDocumental = "TD05";
				break;
			case COMUNICACIO:
				tipusDocumental = "TD06";
				break;
			case NOTIFICACIO:
				tipusDocumental = "TD07";
				break;
			case PUBLICACIO:
				tipusDocumental = "TD08";
				break;
			case JUSTIFICANT_RECEPCIO:
				tipusDocumental = "TD09";
				break;
			case ACTA:
				tipusDocumental = "TD10";
				break;
			case CERTIFICAT:
				tipusDocumental = "TD11";
				break;
			case DILIGENCIA:
				tipusDocumental = "TD12";
				break;
			case INFORME:
				tipusDocumental = "TD13";
				break;
			case SOLICITUD:
				tipusDocumental = "TD14";
				break;
			case DENUNCIA:
				tipusDocumental = "TD15";
				break;
			case ALEGACIO:
				tipusDocumental = "TD16";
				break;
			case RECURS:
				tipusDocumental = "TD17";
				break;
			case COMUNICACIO_CIUTADA:
				tipusDocumental = "TD18";
				break;
			case FACTURA:
				tipusDocumental = "TD19";
				break;
			case ALTRES_INCAUTATS:
				tipusDocumental = "TD20";
				break;
			case ALTRES:
				tipusDocumental = "TD99";
				break;
			}
		} else if (document.getMetadades().getTipusDocumentalAddicional() != null) {
			tipusDocumental = document.getMetadades().getTipusDocumentalAddicional();
		}

		return tipusDocumental;
	}

	private InteressatDto toInteressatDto(RegistreInteressatEntity registreInteressatEntity, Long existingInteressatId) {
		InteressatDto interessatDto = null;
		switch (registreInteressatEntity.getTipus()) {
		case PERSONA_FISICA:
			InteressatPersonaFisicaDto interessatPersonaFisicaDto = new InteressatPersonaFisicaDto();
			interessatPersonaFisicaDto.setDocumentTipus(
					toInteressatDocumentTipusEnumDto(registreInteressatEntity.getDocumentTipus()));
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
			interessatPersonaJuridicaDto.setDocumentTipus(
					toInteressatDocumentTipusEnumDto(registreInteressatEntity.getDocumentTipus()));
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
			interessatAdministracioDto.setDocumentTipus(
					toInteressatDocumentTipusEnumDto(registreInteressatEntity.getDocumentTipus()));
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

	private InteressatDocumentTipusEnumDto toInteressatDocumentTipusEnumDto(DocumentTipus documentTipus) {
		InteressatDocumentTipusEnumDto interessatDocumentTipusEnumDto = null;
		if (documentTipus != null) {
			switch (documentTipus) {
			case NIF:
				interessatDocumentTipusEnumDto = InteressatDocumentTipusEnumDto.NIF;
				break;
			case CIF:
				interessatDocumentTipusEnumDto = InteressatDocumentTipusEnumDto.CIF;
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

	
	
	private DocumentNtiTipoFirmaEnumDto toNtiTipoFirma(es.caib.distribucio.ws.backofficeintegracio.FirmaTipus firmaTipus) {
		DocumentNtiTipoFirmaEnumDto documentNtiTipoFirmaEnumDto = null;
		
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
		return documentNtiTipoFirmaEnumDto;
	}
//	public void comprovarSiExpedientAmbMateixNom(
//			MetaExpedientEntity metaExpedient,
//			ContingutEntity contingutPare,
//			String nom,
//			Long id,
//			Class<?> objectClass) {
//		ExpedientEntity expedient = expedientRepository.findByMetaExpedientAndPareAndNomAndEsborrat(
//				metaExpedient,
//				contingutPare,
//				nom,
//				0);
//		if (expedient != null) {
//			throw new ValidationException(
//					id,
//					objectClass,
//					"Ja existeix un altre expedient amb el mateix tipus i nom");
//		}
//	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientHelper.class);

}