/**
 * 
 */
package es.caib.ripea.core.helper;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.ws.backofficeintegracio.DocumentTipus;
import es.caib.distribucio.ws.backofficeintegracio.NtiEstadoElaboracio;
import es.caib.distribucio.ws.backofficeintegracio.NtiOrigen;
import es.caib.distribucio.ws.backofficeintegracio.NtiTipoDocumento;
import es.caib.plugins.arxiu.api.Carpeta;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.ContingutTipus;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.Expedient;
import es.caib.ripea.core.api.dto.ArxiuFirmaDto;
import es.caib.ripea.core.api.dto.CarpetaDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiTipoDocumentalEnumDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.InteressatDocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.NtiOrigenEnumDto;
import es.caib.ripea.core.api.dto.RegistreAnnexEstatEnumDto;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.CarpetaService;
import es.caib.ripea.core.api.service.ExpedientInteressatService;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.core.entity.CarpetaEntity;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientEstatEntity;
import es.caib.ripea.core.entity.ExpedientPeticioEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.RegistreAnnexEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.repository.CarpetaRepository;
import es.caib.ripea.core.repository.DocumentPortafirmesRepository;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.ExpedientEstatRepository;
import es.caib.ripea.core.repository.ExpedientPeticioRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.RegistreAnnexRepository;
import es.caib.ripea.core.repository.RegistreRepository;

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
	private ContingutHelper contingutHelper;
	@Autowired
	private ContingutLogHelper contingutLogHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private RegistreRepository registreRepository;
	@Autowired
	private UsuariHelper usuariHelper;
	@Autowired
	private EmailHelper emailHelper;
	
	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private CarpetaRepository carpetaRepository;
	@Autowired
	private DocumentPortafirmesRepository documentPortafirmesRepository;

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private DocumentHelper documentHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private CarpetaService carpetaService;
	@Autowired
	private ExpedientRepository expedientRepository;
	@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private ExpedientPeticioRepository expedientPeticioRepository;
	@Autowired
	private RegistreAnnexRepository registreAnnexRepository;
	@Autowired
	private ExpedientInteressatService expedientInteressatService;
	@Resource
	private CarpetaHelper carpetaHelper;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ExpedientDto create(
			Long entitatId,
			Long metaExpedientId,
			Long pareId,
			Integer any,
			String nom,
			Long expedientPeticioId,
			boolean associarInteressats) {
		if (metaExpedientId == null) {
			throw new ValidationException(
					"<creacio>",
					ExpedientEntity.class,
					"No es pot crear un expedient sense un meta-expedient associat");
		}
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitat,
				metaExpedientId,
				false,
				false,
				true,
				false);
		ContingutEntity contingutPare = null;
		if (pareId != null) {
			contingutPare = contingutHelper.comprovarContingutDinsExpedientModificable(
					entitatId,
					pareId,
					false,
					false,
					true,
					false);
		}
		contingutHelper.comprovarNomValid(
				contingutPare,
				nom,
				null,
				ExpedientEntity.class);
		ExpedientEntity expedient = contingutHelper.crearNouExpedient(
				nom,
				metaExpedient,
				contingutPare,
				metaExpedient.getEntitat(),
				"1.0",
				metaExpedient.getEntitat().getUnitatArrel(),
				new Date(),
				any,
				true);
		List<ExpedientEstatEntity> expedientEstats = expedientEstatRepository.findByMetaExpedientOrderByOrdreAsc(expedient.getMetaExpedient());
		
		//find inicial state if exists
		ExpedientEstatEntity estatInicial = null;
		for (ExpedientEstatEntity expedientEstat : expedientEstats) {
			if (expedientEstat.isInicial()){
				estatInicial = expedientEstat;
			}
		}
		//set inicial estat if exists
		if (estatInicial != null) {
			expedient.updateExpedientEstat(estatInicial);
			
			//if estat has usuari responsable agafar expedient by this user
			if (estatInicial.getResponsableCodi()!=null) {
				agafar(
						entitatId, 
						expedient.getId(),
						estatInicial.getResponsableCodi());
			}
		}
		
		
		// if expedient comes from distribucio
		if (expedientPeticioId != null) {
			relateExpedientWithPeticioAndSetAnnexosPendent(
					expedientPeticioId,
					expedient.getId());
			
			//TODO
//			if (associarInteressats) {
//				ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
//				
//				for (RegistreInteressatEntity registreInteressatEntity : expedientPeticioEntity.getRegistre().getInteressats()) {
//					
//					InteressatDto interessatDto = null;
//					switch (registreInteressatEntity.getTipus()) {
//					case PERSONA_FISICA:
//						InteressatPersonaFisicaDto interessatPersonaFisicaDto = new InteressatPersonaFisicaDto();
//						interessatPersonaFisicaDto.setTipus(InteressatTipusEnumDto.PERSONA_FISICA);
//						interessatPersonaFisicaDto.setDocumentTipus(toInteressatDocumentTipusEnumDto(registreInteressatEntity.getDocumentTipus()));
//						interessatPersonaFisicaDto.setDocumentNum(registreInteressatEntity.getDocumentNumero());
//						
//						
//						
//						for (PaisDto paisDto : cacheHelper.findPaisos()) {
//							System.out.println(paisDto.getCodi());
//						}
//						
//						for (ProvinciaDto provinciaDto : cacheHelper.findProvincies()) {
//							System.out.println(provinciaDto.getCodi());
//						}
//						
//						
////						cacheHelper.findMunicipisPerProvincia();
//						
//						
//						
//						
//						interessatPersonaFisicaDto.setPais(registreInteressatEntity.getPaisCodi());
//						
//						interessatDto = interessatPersonaFisicaDto;
//						break;
//					case PERSONA_JURIDICA:
//						
//						InteressatPersonaJuridicaDto interessatPersonaJuridicaDto = new InteressatPersonaJuridicaDto();
//						interessatDto = interessatPersonaJuridicaDto;
//						break;
//					case ADMINISTRACIO:
//						InteressatAdministracioDto interessatAdministracioDto = new InteressatAdministracioDto();
//						interessatDto = interessatAdministracioDto;
//						break;
//					}
//					
//					expedientInteressatService.create(entitatId, expedient.getId(), null, interessatDto, false);
//				}
//				
//
//			}
		}
		
		contingutLogHelper.logCreacio(
				expedient,
				false,
				false);
		ExpedientDto dto = toExpedientDto(
				expedient,
				true);
		contingutHelper.arxiuPropagarModificacio(
				expedient,
				null,
				false,
				false,
				null);
		return dto;
	}
	
	public InteressatDocumentTipusEnumDto toInteressatDocumentTipusEnumDto(DocumentTipus documentTipus) {

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
			}
			
		}
		return interessatDocumentTipusEnumDto;
	}
	
	

	public void relateExpedientWithPeticioAndSetAnnexosPendent(
			Long expedientPeticioId,
			Long expedientId) {

		ExpedientEntity expedient = expedientRepository.findOne(expedientId);
		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);

		expedientPeticioEntity.updateExpedient(expedient);
		expedient.addExpedientPeticio(expedientPeticioEntity);


		// set annexos as pending to create in db and to move in arxiu
		for (RegistreAnnexEntity registreAnnex : expedientPeticioEntity.getRegistre().getAnnexos()) {
			registreAnnex.updateEstat(RegistreAnnexEstatEnumDto.PENDENT);
		}
		
	}
	
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void relateExpedientWithPeticioAndSetAnnexosPendentNewTransaction(
			Long expedientPeticioId,
			Long expedientId) {

		relateExpedientWithPeticioAndSetAnnexosPendent(expedientPeticioId, expedientId);

	}
	
	
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateNotificarError(
			Long expedientPeticioId,
			String error) {
		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		expedientPeticioEntity.updateNotificaDistError(error);

	}
	
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateRegistreAnnexError(
			Long registreAnnexId,
			String error) {
		RegistreAnnexEntity registreAnnexEntity = registreAnnexRepository.findOne(registreAnnexId);
		registreAnnexEntity.updateError(error);

	}
	
	
	
	
	/**
	 * Creates document from registre annex
	 * @param registreAnnexId
	 * @param expedientPeticioId
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void createDocFromAnnex(
			Long registreAnnexId,
			Long expedientPeticioId) {
		
		ExpedientPeticioEntity expedientPeticioEntity;
		ExpedientEntity expedientEntity;
		RegistreAnnexEntity registreAnnexEntity = new RegistreAnnexEntity();
		EntitatEntity entitat;
		DocumentDto dto = null;
		boolean processatOk = true;
		

		expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		expedientEntity = expedientRepository.findOne(expedientPeticioEntity.getExpedient().getId());
		registreAnnexEntity = registreAnnexRepository.findOne(registreAnnexId);
		entitat = entitatRepository.findByUnitatArrel(expedientPeticioEntity.getRegistre().getEntitatCodi());

		logger.debug("Creant carpeta i documents de expedient peticio (" +
				"expedientId=" + expedientPeticioEntity.getExpedient().getId() + ", " +
				"registreAnnexId=" + registreAnnexId + ", " +
				"expedientPeticioId=" + expedientPeticioId + ")");
		
		boolean throwException = false;
		if(throwException)
			throw new RuntimeException("EXCEPION BEFORE CREATING CARPETA!!!!!! ");
		
		// ############################## CREATE CARPETA ##########################################
		
		// create carpeta ind db and arxiu if doesnt already exists
		Long carpetaId = createCarpetaFromExpPeticio(
				expedientEntity,
				entitat.getId(),
				expedientPeticioEntity.getRegistre().getIdentificador());
		
		boolean throwException1 = false;
		if(throwException1)
			throw new RuntimeException("EXCEPION BEFORE CREATING DOCUMENT IN DB!!!!!! ");
		
		
		// ############################## CREATE DOCUMENT IN DB ####################################
		
		CarpetaEntity carpetaEntity = carpetaRepository.findOne(carpetaId);
		DocumentDto documentDto = toDocumentDto(registreAnnexEntity);
		contingutHelper.comprovarNomValid(
				carpetaEntity,
				documentDto.getNom(),
				null,
				DocumentEntity.class);
		
		DocumentEntity docEntity = documentHelper.crearNouDocument(
				documentDto.getDocumentTipus(),
				documentDto.getNom(),
				documentDto.getData(),
				documentDto.getDataCaptura(),
				documentDto.getNtiOrgano(),
				documentDto.getNtiOrigen(),
				documentDto.getNtiEstadoElaboracion(),
				documentDto.getNtiTipoDocumental(),
				null,
				carpetaEntity,
				carpetaEntity.getEntitat(),
				expedientEntity,
				documentDto.getUbicacio(),
				documentDto.getNtiIdDocumentoOrigen());
		
		FitxerDto fitxer = new FitxerDto();
		fitxer.setNom(documentDto.getFitxerNom());
		fitxer.setContentType(documentDto.getFitxerContentType());
		fitxer.setContingut(documentDto.getFitxerContingut());
		List<ArxiuFirmaDto> firmes = null;
		if (documentDto.getFitxerContingut() != null) {
			documentHelper.actualitzarFitxerDocument(
					docEntity,
					fitxer);
			if (documentDto.isAmbFirma()) {
				firmes = documentHelper.validaFirmaDocument(
						docEntity, 
						fitxer,
						documentDto.getFirmaContingut());
			}
		} else {
			docEntity.updateFitxer(
					fitxer.getNom(),
					fitxer.getContentType(),
					fitxer.getContingut());
			
		}
		contingutLogHelper.logCreacio(
				docEntity,
				true,
				true);
		
		
		
		boolean throwException2 = false;
		if(throwException2)
			throw new RuntimeException("EXCEPION BEFORE MOVING DOCUMENT IN DB!!!!!! ");
		
		// ############################## MOVE DOCUMENT IN ARXIU ##########################################
		
		// we put arxiu uuid of document in distribucio
		docEntity.updateArxiu(
				documentDto.getArxiuUuid());
		documentRepository.saveAndFlush(docEntity);
		
		
		// check if already exists in arxiu
		Carpeta carpeta = pluginHelper.arxiuCarpetaConsultar(carpetaEntity);
		boolean documentExistsInArxiu = false;
		String documentUuid = null;

		if (carpeta.getContinguts()!=null) {
			for (ContingutArxiu contingutArxiu : carpeta.getContinguts()) {
				if (contingutArxiu.getTipus() == ContingutTipus.DOCUMENT && contingutArxiu.getNom().equals(docEntity.getNom())) {
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
		
		// save ntiIdentitficador generated in arxiu in db
		Document documentDetalls = pluginHelper.arxiuDocumentConsultar(
				docEntity,
				null,
				null,
				true,
				false);
		documentDetalls.getMetadades().getIdentificadorOrigen();
		docEntity.updateNtiIdentificador(documentDetalls.getMetadades().getIdentificador());
		documentRepository.save(docEntity);
		
		
	}
	
	
	
	private Long createCarpetaFromExpPeticio(ExpedientEntity expedientEntity, Long entitatId, String nom) {
		
		// check if already exists in db
		boolean carpetaExistsInDB = false;
		Long carpetaId = null;
		CarpetaEntity carpetaEntity = null;
		for (ContingutEntity contingut : expedientEntity.getFills()) {
			if (contingut instanceof CarpetaEntity && contingut.getNom().equals(nom)) {
				carpetaExistsInDB = true;
				carpetaId = contingut.getId();
				carpetaEntity = (CarpetaEntity) contingut;
			}
		}
		// check if already exists in arxiu
		Expedient expedient = pluginHelper.arxiuExpedientConsultar(expedientEntity);
		boolean carpetaExistsInArxiu = false;
		String carpetaUuid = null;

		if (expedient.getContinguts()!=null) {
			for (ContingutArxiu contingutArxiu : expedient.getContinguts()) {
				String replacedNom = nom.replace("/", "_");
				if (contingutArxiu.getTipus() == ContingutTipus.CARPETA && contingutArxiu.getNom().equals(replacedNom)) {
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
					carpetaUuid);
			carpetaId = carpetaDto.getId();
		} 
		
		return carpetaId;
	}
	
	private DocumentDto toDocumentDto(RegistreAnnexEntity registreAnnexEntity) {

		DocumentDto document = new DocumentDto();
		document.setDocumentTipus(DocumentTipusEnumDto.DIGITAL);
		document.setEstat(DocumentEstatEnumDto.CUSTODIAT);
		document.setData(new Date());
		document.setNom(registreAnnexEntity.getTitol());
		document.setFitxerNom(registreAnnexEntity.getNom());
		document.setArxiuUuid(registreAnnexEntity.getUuid());
		document.setDataCaptura(registreAnnexEntity.getNtiFechaCaptura());

		document.setNtiOrigen(toNtiOrigenEnumDto(registreAnnexEntity.getNtiOrigen()));
		document.setNtiTipoDocumental(toDocumentNtiTipoDocumentalEnumDto(registreAnnexEntity.getNtiTipoDocumental()));
		document.setNtiEstadoElaboracion(toDocumentNtiEstadoElaboracionEnumDto(registreAnnexEntity.getNtiEstadoElaboracio()));
		document.setFitxerContentType(registreAnnexEntity.getTipusMime());
		
		document.setNtiVersion("1.0");
		
		String codiDir3 = entitatRepository.findByUnitatArrel(registreAnnexEntity.getRegistre().getEntitatCodi()).getUnitatArrel();
		document.setNtiOrgano(codiDir3);

		return document;
	}
	
	
	
	public DocumentNtiEstadoElaboracionEnumDto toDocumentNtiEstadoElaboracionEnumDto(NtiEstadoElaboracio ntiEstadoElaboracio) {

		DocumentNtiEstadoElaboracionEnumDto documentNtiEstadoElaboracionEnumDto = null;
		
		if (ntiEstadoElaboracio != null) {
			switch (ntiEstadoElaboracio) {
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
	
	
	
	public DocumentNtiTipoDocumentalEnumDto toDocumentNtiTipoDocumentalEnumDto(NtiTipoDocumento ntiTipoDocumento) {

		DocumentNtiTipoDocumentalEnumDto documentNtiTipoDocumentalEnumDto = null;

		if (ntiTipoDocumento != null) {
			switch (ntiTipoDocumento) {
			case RESOLUCIO:
				documentNtiTipoDocumentalEnumDto = DocumentNtiTipoDocumentalEnumDto.TD01;
				break;
			case ACORD:
				documentNtiTipoDocumentalEnumDto = DocumentNtiTipoDocumentalEnumDto.TD02;
				break;
			case CONTRACTE:
				documentNtiTipoDocumentalEnumDto = DocumentNtiTipoDocumentalEnumDto.TD03;
				break;
			case CONVENI:
				documentNtiTipoDocumentalEnumDto = DocumentNtiTipoDocumentalEnumDto.TD04;
				break;
			case DECLARACIO:
				documentNtiTipoDocumentalEnumDto = DocumentNtiTipoDocumentalEnumDto.TD05;
				break;
			case COMUNICACIO:
				documentNtiTipoDocumentalEnumDto = DocumentNtiTipoDocumentalEnumDto.TD06;
				break;
			case NOTIFICACIO:
				documentNtiTipoDocumentalEnumDto = DocumentNtiTipoDocumentalEnumDto.TD07;
				break;
			case PUBLICACIO:
				documentNtiTipoDocumentalEnumDto = DocumentNtiTipoDocumentalEnumDto.TD08;
				break;
			case JUSTIFICANT_RECEPCIO:
				documentNtiTipoDocumentalEnumDto = DocumentNtiTipoDocumentalEnumDto.TD09;
				break;
			case ACTA:
				documentNtiTipoDocumentalEnumDto = DocumentNtiTipoDocumentalEnumDto.TD10;
				break;
			case CERTIFICAT:
				documentNtiTipoDocumentalEnumDto = DocumentNtiTipoDocumentalEnumDto.TD11;
				break;
			case DILIGENCIA:
				documentNtiTipoDocumentalEnumDto = DocumentNtiTipoDocumentalEnumDto.TD12;
				break;
			case INFORME:
				documentNtiTipoDocumentalEnumDto = DocumentNtiTipoDocumentalEnumDto.TD13;
				break;
			case SOLICITUD:
				documentNtiTipoDocumentalEnumDto = DocumentNtiTipoDocumentalEnumDto.TD14;
				break;
			case DENUNCIA:
				documentNtiTipoDocumentalEnumDto = DocumentNtiTipoDocumentalEnumDto.TD15;
				break;
			case ALEGACIO:
				documentNtiTipoDocumentalEnumDto = DocumentNtiTipoDocumentalEnumDto.TD16;
				break;
			case RECURS:
				documentNtiTipoDocumentalEnumDto = DocumentNtiTipoDocumentalEnumDto.TD17;
				break;
			case COMUNICACIO_CIUTADA:
				documentNtiTipoDocumentalEnumDto = DocumentNtiTipoDocumentalEnumDto.TD18;
				break;
			case FACTURA:
				documentNtiTipoDocumentalEnumDto = DocumentNtiTipoDocumentalEnumDto.TD19;
				break;
			case ALTRES_INCAUTATS:
				documentNtiTipoDocumentalEnumDto = DocumentNtiTipoDocumentalEnumDto.TD20;
				break;
			case ALTRES:
				documentNtiTipoDocumentalEnumDto = DocumentNtiTipoDocumentalEnumDto.TD99;
				break;
			}
		}
		return documentNtiTipoDocumentalEnumDto;
	}
	
	
	public NtiOrigenEnumDto toNtiOrigenEnumDto(NtiOrigen ntiOrigen) {

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
	
	
	

	private void agafar(
			Long entitatId,
			Long expedientId,
			String usuariCodi) {
		logger.debug("Agafant l'expedient com a usuari ("
				+ "entitatId=" + entitatId + ", "
				+ "expedientId=" + expedientId + ", "
				+ "usuariCodi=" + usuariCodi + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				false,
				false,
				true,
				false,
				false);
		ExpedientEntity expedientSuperior = contingutHelper.getExpedientSuperior(
				expedient,
				false,
				false,
				false);
		if (expedientSuperior != null) {
			logger.error("No es pot agafar un expedient no arrel (id=" + expedientId + ")");
			throw new ValidationException(
					expedientId,
					ExpedientEntity.class,
					"No es pot agafar un expedient no arrel");
		}
		// Agafa l'expedient. Si l'expedient pertany a un altre usuari li pren
		UsuariEntity usuariOriginal = expedient.getAgafatPer();
		UsuariEntity usuariNou = usuariHelper.getUsuariByCodi(usuariCodi);
		expedient.updateAgafatPer(usuariNou);
		if (usuariOriginal != null) {
			// Avisa a l'usuari que li han pres
			emailHelper.emailUsuariContingutAgafatSensePermis(
					expedient,
					usuariOriginal,
					usuariNou);
		}
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.AGAFAR,
				null,
				null,
				false,
				false);
	}

	private ExpedientDto toExpedientDto(
			ExpedientEntity expedient,
			boolean ambPathIPermisos) {
		return (ExpedientDto)contingutHelper.toContingutDto(
				expedient,
				ambPathIPermisos,
				false,
				false,
				false,
				ambPathIPermisos,
				false,
				false);
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientHelper.class);

}