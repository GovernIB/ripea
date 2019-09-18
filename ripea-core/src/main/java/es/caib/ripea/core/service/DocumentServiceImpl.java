/**
 * 
 */
package es.caib.ripea.core.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.plugins.arxiu.api.Document;
import es.caib.ripea.core.api.dto.ArxiuFirmaDetallDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaDto;
import es.caib.ripea.core.api.dto.ContingutTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentPortafirmesDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentViaFirmaDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.core.api.dto.MultiplicitatEnumDto;
import es.caib.ripea.core.api.dto.PortafirmesCallbackEstatEnumDto;
import es.caib.ripea.core.api.dto.PortafirmesPrioritatEnumDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.dto.ViaFirmaCallbackEstatEnumDto;
import es.caib.ripea.core.api.dto.ViaFirmaDispositiuDto;
import es.caib.ripea.core.api.dto.ViaFirmaEnviarDto;
import es.caib.ripea.core.api.dto.ViaFirmaRespostaDto;
import es.caib.ripea.core.api.dto.ViaFirmaUsuariDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.SistemaExternException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DispositiuEnviamentEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.DocumentEnviamentInteressatEntity;
import es.caib.ripea.core.entity.DocumentPortafirmesEntity;
import es.caib.ripea.core.entity.DocumentViaFirmaEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.entity.ViaFirmaUsuariEntity;
import es.caib.ripea.core.helper.CacheHelper;
import es.caib.ripea.core.helper.ContingutHelper;
import es.caib.ripea.core.helper.ContingutLogHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.DocumentHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.PermisosHelper;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.helper.ViaFirmaHelper;
import es.caib.ripea.core.repository.DispositiuEnviamentRepository;
import es.caib.ripea.core.repository.DocumentEnviamentInteressatRepository;
import es.caib.ripea.core.repository.DocumentNotificacioRepository;
import es.caib.ripea.core.repository.DocumentPortafirmesRepository;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.core.repository.DocumentViaFirmaRepository;
import es.caib.ripea.core.repository.UsuariRepository;
import es.caib.ripea.core.security.ExtendedPermission;

/**
 * Implementació dels mètodes per a gestionar documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class DocumentServiceImpl implements DocumentService {

	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private DocumentPortafirmesRepository documentPortafirmesRepository;
	@Autowired
	private DocumentViaFirmaRepository documentViaFirmaRepository;
	@Autowired
	private DispositiuEnviamentRepository dispositiuEnviamentRepository;
	@Resource
	private DocumentNotificacioRepository documentNotificacioRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private ContingutHelper contingutHelper;
	@Autowired
	private DocumentHelper documentHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private ContingutLogHelper contingutLogHelper;
	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private ViaFirmaHelper viaFirmaHelper;
	
	DocumentEnviamentInteressatRepository documentEnviamentInteressatRepository;

	@Transactional
	@Override
	public DocumentDto create(
			Long entitatId,
			Long pareId,
			DocumentDto document) {
		logger.debug("Creant nou document (" +
				"entitatId=" + entitatId + ", " +
				"pareId=" + pareId + ", " +
				"document=" + document + ")");
		ContingutEntity pare = contingutHelper.comprovarContingutDinsExpedientModificable(
				entitatId,
				pareId,
				false,
				false,
				false,
				false);
		ExpedientEntity expedient;
		if (ContingutTipusEnumDto.EXPEDIENT.equals(pare.getTipus())) {
			expedient = (ExpedientEntity)pare;
		} else {
			expedient = pare.getExpedient();
		}
		MetaDocumentEntity metaDocument = null;
		if (document.getMetaDocument() != null) {
			metaDocument = entityComprovarHelper.comprovarMetaDocument(
					pare.getEntitat(),
					expedient.getMetaExpedient(),
					document.getMetaDocument().getId(),
					true);
		} else {
			throw new ValidationException(
					"<creacio>",
					ExpedientEntity.class,
					"No es pot crear un document sense un meta-document associat");
		}
		contingutHelper.comprovarNomValid(
				pare,
				document.getNom(),
				null,
				DocumentEntity.class);
		List<DocumentEntity> documents = documentRepository.findByExpedientAndMetaNodeAndEsborrat(
				expedient,
				metaDocument,
				0);
		if (documents.size() > 0 && (metaDocument.getMultiplicitat().equals(MultiplicitatEnumDto.M_1) || metaDocument.getMultiplicitat().equals(MultiplicitatEnumDto.M_0_1))) {
			throw new ValidationException(
					"<creacio>",
					ExpedientEntity.class,
					"La multiplicitat del meta-document no permet crear nous documents a dins l'expedient (" +
					"metaExpedientId=" + expedient.getMetaExpedient().getId() + ", " +
					"metaDocumentId=" + document.getMetaDocument().getId() + ", " +
					"metaDocumentMultiplicitat=" + metaDocument.getMultiplicitat() + ", " +
					"expedientId=" + expedient.getId() + ")");
		}
		if (expedient != null) {
			cacheHelper.evictErrorsValidacioPerNode(expedient);
		}
		DocumentEntity entity = documentHelper.crearNouDocument(
				document.getDocumentTipus(),
				document.getNom(),
				document.getData(),
				new Date(),
				expedient.getNtiOrgano(),
				metaDocument.getNtiOrigen(),
				document.getNtiEstadoElaboracion(),
				metaDocument.getNtiTipoDocumental(),
				metaDocument,
				pare,
				pare.getEntitat(),
				expedient,
				document.getUbicacio(),
				document.getNtiIdDocumentoOrigen());
		FitxerDto fitxer = new FitxerDto();
		fitxer.setNom(document.getFitxerNom());
		fitxer.setContentType(document.getFitxerContentType());
		fitxer.setContingut(document.getFitxerContingut());
		List<ArxiuFirmaDto> firmes = null;
		if (document.getFitxerContingut() != null) {
			documentHelper.actualitzarFitxerDocument(
					entity,
					fitxer);
			if (document.isAmbFirma()) {
				firmes =documentHelper.validaFirmaDocument(
						entity, 
						fitxer,
						document.getFirmaContingut());
			}
		}
		contingutLogHelper.logCreacio(
				entity,
				true,
				true);
		contingutHelper.arxiuPropagarModificacio(
				entity,
				fitxer,
				document.isAmbFirma(),
				document.isFirmaSeparada(),
				firmes);
		DocumentDto dto = toDocumentDto(entity);
		return dto;
	}

	@Transactional
	@Override
	public DocumentDto update(
			Long entitatId,
			Long id,
			DocumentDto document) {
		logger.debug("Actualitzant el document (" +
				"entitatId=" + entitatId + ", " +
				"id=" + id + ", " +
				"document=" + document + ")");
		DocumentEntity entity = documentHelper.comprovarDocumentDinsExpedientModificable(
				entitatId,
				id,
				false,
				true,
				false,
				false);
		MetaDocumentEntity metaDocument = null;
		List<ArxiuFirmaDto> firmes = null;
		if (document.getMetaDocument() != null) {
			metaDocument = entityComprovarHelper.comprovarMetaDocument(
					entity.getEntitat(),
					entity.getMetaDocument().getMetaExpedient(),
					document.getMetaDocument().getId(),
					false);
		} else {
			throw new ValidationException(
					id,
					DocumentEntity.class,
					"No es pot actualitzar un document sense un meta-document associat");
		}
		contingutHelper.comprovarNomValid(
				entity.getPare(),
				document.getNom(),
				id,
				DocumentEntity.class);
		cacheHelper.evictErrorsValidacioPerNode(entity);
		String nomOriginal = entity.getNom();
		entity.update(
				metaDocument,
				document.getNom(),
				document.getData(),
				document.getUbicacio(),
				entity.getDataCaptura(),
				entity.getNtiOrgano(),
				entity.getNtiOrigen(),
				document.getNtiEstadoElaboracion(),
				entity.getNtiTipoDocumental(),
				document.getNtiIdDocumentoOrigen(),
				document.getNtiTipoFirma(),
				document.getNtiCsv(),
				document.getNtiCsvRegulacion());
		FitxerDto fitxer = null;
		if (document.getFitxerContingut() != null) {
			fitxer = new FitxerDto();
			fitxer.setNom(document.getFitxerNom());
			fitxer.setContentType(document.getFitxerContentType());
			fitxer.setContingut(document.getFitxerContingut());
		}
		if (document.getFitxerContingut() != null) {
			documentHelper.actualitzarFitxerDocument(
					entity,
					fitxer);
			if (document.isAmbFirma()) {
				firmes = documentHelper.validaFirmaDocument(
						entity, 
						fitxer,
						document.getFirmaContingut());
			}
		}
		// Registra al log la modificació del document
		contingutLogHelper.log(
				entity,
				LogTipusEnumDto.MODIFICACIO,
				(!nomOriginal.equals(document.getNom())) ? document.getNom() : null,
				(document.getFitxerContingut() != null) ? "VERSIO_NOVA" : null,
				false,
				false);
		DocumentDto dto = toDocumentDto(entity);
		contingutHelper.arxiuPropagarModificacio(
				entity,
				fitxer,
				document.isAmbFirma(),
				document.isFirmaSeparada(),
				firmes);
		return dto;
	}

	@Transactional(readOnly = true)
	@Override
	public DocumentDto findById(
			Long entitatId,
			Long id) {
		logger.debug("Obtenint el document ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ")");
		DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
				entitatId,
				id,
				true,
				false);
		return toDocumentDto(document);
	}

	@Transactional(readOnly = true)
	@Override
	public List<DocumentDto> findAmbExpedientIPermisRead(
			Long entitatId,
			Long expedientId) {
		logger.debug("Obtenint els documents amb permis de lectura de l'expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "expedientId=" + expedientId + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				false,
				false,
				false,
				false,
				false);
		List<DocumentEntity> documents = documentRepository.findByExpedient(expedient);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Iterator<DocumentEntity> it = documents.iterator();
		while (it.hasNext()) {
			DocumentEntity d = it.next();
			if (d.getMetaDocument() != null && !permisosHelper.isGrantedAll(
					d.getMetaDocument().getId(),
					MetaNodeEntity.class,
					new Permission[] {ExtendedPermission.READ},
					auth)) {
				it.remove();
			}
		}
		List<DocumentDto> dtos = new ArrayList<DocumentDto>();
		for (DocumentEntity document: documents) {
			dtos.add(
					(DocumentDto)contingutHelper.toContingutDto(document));
		}
		return dtos;
	}

	@Transactional(readOnly = true)
	@Override
	public FitxerDto infoDocument(
			Long entitatId,
			Long id,
			String versio) {
		logger.debug("Descarregant contingut del document ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ", "
				+ "versio=" + versio + ")");
		DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
				entitatId,
				id,
				true,
				false);
		return documentHelper.getFitxerAssociat(
				document,
				versio);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ArxiuFirmaDetallDto> getDetallSignants(
			Long entitatId,
			Long id,
			String versio) throws NotFoundException {
		logger.debug("Consultant el detall de les firmes d'un document ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ", "
				+ "versio=" + versio + ")");
		DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
				entitatId,
				id,
				true,
				false);
		if (document.getArxiuUuid() != null) {
			if (pluginHelper.isArxiuPluginActiu()) {
				Document arxiuDocument = pluginHelper.arxiuDocumentConsultar(
						document,
						null,
						versio,
						true,
						false);
				List<ArxiuFirmaDto> arxiuFirmes = pluginHelper.validaSignaturaObtenirFirmes(
						documentHelper.getContingutFromArxiuDocument(arxiuDocument),
						documentHelper.getFirmaDetachedFromArxiuDocument(arxiuDocument),
						null);
				return arxiuFirmes.get(0).getDetalls();
			}
		}
		return null;
	}

	@Transactional(readOnly = true)
	@Override
	public FitxerDto descarregar(
			Long entitatId,
			Long id,
			String versio) {
		logger.debug("Descarregant contingut del document ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ", "
				+ "versio=" + versio + ")");
		DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
				entitatId,
				id,
				true,
				false);
		return documentHelper.getFitxerAssociat(
				document,
				versio);
	}

	@Transactional(readOnly = true)
	@Override
	public FitxerDto descarregarImprimible(
			Long entitatId,
			Long id,
			String versio) {
		logger.debug("Descarregant versió imprimible del document ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ", "
				+ "versio=" + versio + ")");
		DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
				entitatId,
				id,
				true,
				false);
		return pluginHelper.arxiuDocumentVersioImprimible(
				document);
	}

	@Transactional
	@Override
	public void portafirmesEnviar(
			Long entitatId,
			Long id,
			String assumpte,
			PortafirmesPrioritatEnumDto prioritat,
			Date dataCaducitat,
			String[] portafirmesResponsables,
			MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus) {
		logger.debug("Enviant document a portafirmes (" +
				"entitatId=" + entitatId + ", " +
				"id=" + id + ", " +
				"assumpte=" + assumpte + ", " +
				"prioritat=" + prioritat + ", " +
				"dataCaducitat=" + dataCaducitat + ")");
		DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientModificable(
				entitatId,
				id,
				false,
				true,
				false,
				false);
		if (!DocumentTipusEnumDto.DIGITAL.equals(document.getDocumentTipus())) {
			throw new ValidationException(
					document.getId(),
					DocumentEntity.class,
					"El document a enviar al portafirmes no és del tipus " + DocumentTipusEnumDto.DIGITAL);
		}
		if (!cacheHelper.findErrorsValidacioPerNode(document).isEmpty()) {
			throw new ValidationException(
					document.getId(),
					DocumentEntity.class,
					"El document a enviar al portafirmes te alertes de validació");
		}
		if (	DocumentEstatEnumDto.FIRMAT.equals(document.getEstat()) ||
				DocumentEstatEnumDto.CUSTODIAT.equals(document.getEstat())) {
			throw new ValidationException(
					document.getId(),
					DocumentEntity.class,
					"No es poden enviar al portafirmes documents firmates o custodiats");
		}
		List<DocumentPortafirmesEntity> enviamentsPendents = documentPortafirmesRepository.findByDocumentAndEstatInOrderByCreatedDateDesc(
				document,
				new DocumentEnviamentEstatEnumDto[] {
						DocumentEnviamentEstatEnumDto.PENDENT,
						DocumentEnviamentEstatEnumDto.ENVIAT
				});
		if (enviamentsPendents.size() > 0) {
			throw new ValidationException(
					document.getId(),
					DocumentEntity.class,
					"Aquest document te enviaments al portafirmes pendents");
		}
		if (!document.getMetaDocument().isFirmaPortafirmesActiva()) {
			throw new ValidationException(
					id,
					DocumentEntity.class,
					"El document no te activada la firma amb portafirmes");
		}
		DocumentPortafirmesEntity documentPortafirmes = DocumentPortafirmesEntity.getBuilder(
				DocumentEnviamentEstatEnumDto.PENDENT,
				assumpte,
				prioritat,
				dataCaducitat,
				document.getMetaDocument().getPortafirmesDocumentTipus(),
				portafirmesResponsables,
				portafirmesFluxTipus,
				document.getMetaDocument().getPortafirmesFluxId(),
				document.getExpedient(),
				document).build();
		// Si l'enviament produeix excepcions la retorna
		SistemaExternException sex = documentHelper.portafirmesEnviar(documentPortafirmes);
		if (sex != null) {
			throw sex;
		}
		documentPortafirmesRepository.save(documentPortafirmes);
		document.updateEstat(
				DocumentEstatEnumDto.FIRMA_PENDENT);
		contingutLogHelper.log(
				document,
				LogTipusEnumDto.PFIRMA_ENVIAMENT,
				documentPortafirmes.getPortafirmesId(),
				documentPortafirmes.getEstat().name(),
				false,
				false);
	}

	@Transactional
	@Override
	public void portafirmesCancelar(
			Long entitatId,
			Long id) {
		logger.debug("Enviant document a portafirmes (" +
				"entitatId=" + entitatId + ", " +
				"id=" + id + ")");
		DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientModificable(
				entitatId,
				id,
				false,
				true,
				false,
				false);
		List<DocumentPortafirmesEntity> enviamentsPendents = documentPortafirmesRepository.findByDocumentAndEstatInOrderByCreatedDateDesc(
				document,
				new DocumentEnviamentEstatEnumDto[] {DocumentEnviamentEstatEnumDto.ENVIAT});
		if (enviamentsPendents.size() == 0) {
			throw new ValidationException(
					document.getId(),
					DocumentEntity.class,
					"Aquest document no te enviaments a portafirmes pendents");
		}
		DocumentPortafirmesEntity documentPortafirmes = enviamentsPendents.get(0);
		if (DocumentEnviamentEstatEnumDto.ENVIAT.equals(documentPortafirmes.getEstat())) {
			pluginHelper.portafirmesDelete(documentPortafirmes);
		}
		documentPortafirmes.updateCancelat(new Date());
		document.updateEstat(
				DocumentEstatEnumDto.REDACCIO);
		contingutLogHelper.log(
				document,
				LogTipusEnumDto.PFIRMA_CANCELACIO,
				documentPortafirmes.getPortafirmesId(),
				documentPortafirmes.getEstat().name(),
				false,
				false);
	}
	
	@Transactional
	@Override
	public Exception portafirmesCallback(
			long portafirmesId,
			PortafirmesCallbackEstatEnumDto callbackEstat) {
		logger.debug("Processant petició del callback ("
				+ "portafirmesId=" + portafirmesId + ", "
				+ "callbackEstat=" + callbackEstat + ")");
		DocumentPortafirmesEntity documentPortafirmes = documentPortafirmesRepository.findByPortafirmesId(
				new Long(portafirmesId).toString());
		if (documentPortafirmes == null) {
			return new NotFoundException(
					"(portafirmesId=" + portafirmesId + ")",
					DocumentPortafirmesEntity.class);
		}
		contingutLogHelper.log(
				documentPortafirmes.getDocument(),
				LogTipusEnumDto.PFIRMA_CALLBACK,
				documentPortafirmes.getPortafirmesId(),
				documentPortafirmes.getEstat().name(),
				false,
				false);
		documentPortafirmes.updateCallbackEstat(callbackEstat);
		return documentHelper.portafirmesProcessar(documentPortafirmes);
	}

	@Transactional
	@Override
	public void portafirmesReintentar(
			Long entitatId,
			Long id) {
		logger.debug("Reintentant processament d'enviament a portafirmes amb error ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ")");
		DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientModificable(
				entitatId,
				id,
				false,
				true,
				false,
				false);
		List<DocumentPortafirmesEntity> enviamentsPendents = documentPortafirmesRepository.findByDocumentAndEstatInAndErrorOrderByCreatedDateDesc(
				document,
				new DocumentEnviamentEstatEnumDto[] {
						DocumentEnviamentEstatEnumDto.PENDENT,
						DocumentEnviamentEstatEnumDto.ENVIAT
				},
				true);
		if (enviamentsPendents.size() == 0) {
			throw new ValidationException(
					document.getId(),
					DocumentEntity.class,
					"Aquest document no te enviaments a portafirmes pendents de processar");
		}
		DocumentPortafirmesEntity documentPortafirmes = enviamentsPendents.get(0);
		contingutLogHelper.log(
				documentPortafirmes.getDocument(),
				LogTipusEnumDto.PFIRMA_REINTENT,
				documentPortafirmes.getPortafirmesId(),
				documentPortafirmes.getEstat().name(),
				false,
				false);
		if (DocumentEnviamentEstatEnumDto.PENDENT.equals(documentPortafirmes.getEstat())) {
			documentHelper.portafirmesEnviar(documentPortafirmes);
		} else if (DocumentEnviamentEstatEnumDto.ENVIAT.equals(documentPortafirmes.getEstat())) {
			documentHelper.portafirmesProcessar(documentPortafirmes);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public DocumentPortafirmesDto portafirmesInfo(
			Long entitatId,
			Long id) {
		logger.debug("Obtenint informació del darrer enviament a portafirmes ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ")");
		DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
				entitatId,
				id,
				true,
				false);
		List<DocumentPortafirmesEntity> enviamentsPendents = documentPortafirmesRepository.findByDocumentAndEstatInOrderByCreatedDateDesc(
				document,
				new DocumentEnviamentEstatEnumDto[] {
						DocumentEnviamentEstatEnumDto.PENDENT,
						DocumentEnviamentEstatEnumDto.ENVIAT
				});
		if (enviamentsPendents.size() == 0) {
			throw new ValidationException(
					document.getId(),
					DocumentEntity.class,
					"Aquest document no te enviaments a portafirmes");
		}
		return conversioTipusHelper.convertir(
				enviamentsPendents.get(0),
				DocumentPortafirmesDto.class);
	}
	
	@Transactional
	@Override
	public void viaFirmaReintentar(
			Long entitatId,
			Long id) {
		logger.debug("Reintentant processament d'enviament a viaFirma amb error ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ")");
		DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientModificable(
				entitatId,
				id,
				false,
				true,
				false,
				false);
		List<DocumentViaFirmaEntity> enviamentsPendents = documentViaFirmaRepository.findByDocumentAndEstatInAndErrorOrderByCreatedDateDesc(
				document,
				new DocumentEnviamentEstatEnumDto[] {
						DocumentEnviamentEstatEnumDto.PENDENT,
						DocumentEnviamentEstatEnumDto.ENVIAT
				},
				true);
		if (enviamentsPendents.size() == 0) {
			throw new ValidationException(
					document.getId(),
					DocumentEntity.class,
					"Aquest document no te enviaments a portafirmes pendents de processar");
		}
		DocumentViaFirmaEntity documentPortafirmes = enviamentsPendents.get(0);
		contingutLogHelper.log(
				documentPortafirmes.getDocument(),
				LogTipusEnumDto.VFIRMA_REINTENT,
				documentPortafirmes.getMessageCode(),
				documentPortafirmes.getEstat().name(),
				false,
				false);
		if (DocumentEnviamentEstatEnumDto.PENDENT.equals(documentPortafirmes.getEstat())) {
			documentHelper.viaFirmaEnviar(documentPortafirmes);
		} else if (DocumentEnviamentEstatEnumDto.ENVIAT.equals(documentPortafirmes.getEstat())) {
			documentHelper.viaFirmaProcessar(documentPortafirmes);
		}
	}

	@Transactional
	@Override
	public void viaFirmaEnviar(
			Long entitatId, 
			Long documentId, 
			ViaFirmaEnviarDto viaFirmaEnviarDto,
			UsuariDto usuariActual)
			throws NotFoundException, IllegalStateException, SistemaExternException {
		logger.debug("Enviant document a viaFirma (" +
				"entitatId=" + entitatId + ", " +
				"id=" + documentId + ")");
		String contrasenyaUsuariViaFirma;
		try {
			UsuariEntity usuari = usuariRepository.findByCodi(usuariActual.getCodi());
			
			DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientModificable(
					entitatId,
					documentId,
					false,
					true,
					false,
					false);
			if (!DocumentTipusEnumDto.DIGITAL.equals(document.getDocumentTipus())) {
				throw new ValidationException(
						document.getId(),
						DocumentEntity.class,
						"El document a enviar a viaFirma no és del tipus " + DocumentTipusEnumDto.DIGITAL);
			}
			if (!cacheHelper.findErrorsValidacioPerNode(document).isEmpty()) {
				throw new ValidationException(
						document.getId(),
						DocumentEntity.class,
						"El document a enviar a viaFirma te alertes de validació");
			}
			if (DocumentEstatEnumDto.FIRMAT.equals(document.getEstat()) ||
					DocumentEstatEnumDto.CUSTODIAT.equals(document.getEstat())) {
				throw new ValidationException(
						document.getId(),
						DocumentEntity.class,
						"No es poden enviar a viaFirma documents firmats o custodiats");
			}
			//Recuperar contrasenya usuari
			for (ViaFirmaUsuariEntity viaFirmaDispositiuDto : usuari.getViaFirmaUsuaris()) {
				if (viaFirmaDispositiuDto.getCodi().equals(viaFirmaEnviarDto.getCodiUsuariViaFirma())) {
					contrasenyaUsuariViaFirma = viaFirmaDispositiuDto.getContrasenya();
					viaFirmaEnviarDto.setContrasenyaUsuariViaFirma(contrasenyaUsuariViaFirma);
				}
			}
			//Guardar dispositiu associat a l'enviament
			DispositiuEnviamentEntity dispositiuEnviament = DispositiuEnviamentEntity.getBuilder(
					viaFirmaEnviarDto.getViaFirmaDispositiu().getCodi(), 
					viaFirmaEnviarDto.getViaFirmaDispositiu().getCodiAplicacio(), 
					viaFirmaEnviarDto.getViaFirmaDispositiu().getDescripcio(),
					viaFirmaEnviarDto.getViaFirmaDispositiu().getLocal(),
					viaFirmaEnviarDto.getViaFirmaDispositiu().getEstat(),
					viaFirmaEnviarDto.getViaFirmaDispositiu().getToken(), 
					viaFirmaEnviarDto.getViaFirmaDispositiu().getIdentificador(),
					viaFirmaEnviarDto.getViaFirmaDispositiu().getTipus(),
					viaFirmaEnviarDto.getViaFirmaDispositiu().getEmailUsuari(),
					viaFirmaEnviarDto.getViaFirmaDispositiu().getCodiUsuari(),
					viaFirmaEnviarDto.getViaFirmaDispositiu().getIdentificadorNacional()).build();
			
			dispositiuEnviamentRepository.save(dispositiuEnviament);
			
			//Guardar document a enviar
			DocumentViaFirmaEntity documentViaFirma = DocumentViaFirmaEntity.getBuilder(
					DocumentEnviamentEstatEnumDto.PENDENT,
					viaFirmaEnviarDto.getCodiUsuariViaFirma(),
					viaFirmaEnviarDto.getContrasenyaUsuariViaFirma(),
					viaFirmaEnviarDto.getTitol(),
					viaFirmaEnviarDto.getDescripcio(),
					viaFirmaEnviarDto.getViaFirmaDispositiu().getCodi(),
					dispositiuEnviament,
					document.getMetaDocument().isBiometricaLectura(),
					document.getExpedient(),
					document).build();
			
			documentHelper.viaFirmaEnviar(documentViaFirma);
			
			documentViaFirmaRepository.save(documentViaFirma);
			document.updateEstat(
					DocumentEstatEnumDto.FIRMA_PENDENT_VIAFIRMA);
			contingutLogHelper.log(
					document,
					LogTipusEnumDto.VFIRMA_ENVIAMENT,
					documentViaFirma.getMessageCode(),
					documentViaFirma.getEstat().name(),
					false,
					false);
		} catch (Exception ex) {
			logger.error(
					"Error a l'hora d'enviar el document a viaFirma (" +
					"documentId=" + documentId + ")",
					ex);
			throw new RuntimeException(
					"Error a l'hora d'enviar el document a viaFirma  (" +
					"documentId=" + documentId + ")",
					ex);
		}
	}
	
	@Transactional
	@Override
	public void viaFirmaCancelar(
			Long entitatId,
			Long id) {
		logger.debug("Enviant document a viaFirma (" +
				"entitatId=" + entitatId + ", " +
				"id=" + id + ")");
		DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientModificable(
				entitatId,
				id,
				false,
				true,
				false,
				false);
		List<DocumentViaFirmaEntity> enviamentsPendents = documentViaFirmaRepository.findByDocumentAndEstatInOrderByCreatedDateDesc(
				document,
				new DocumentEnviamentEstatEnumDto[] {DocumentEnviamentEstatEnumDto.ENVIAT});
		if (enviamentsPendents.size() == 0) {
			throw new ValidationException(
					document.getId(),
					DocumentEntity.class,
					"Aquest document no te enviaments a viaFirma pendents");
		}
		DocumentViaFirmaEntity documentViaFirma = enviamentsPendents.get(0);
		documentViaFirma.updateMessageCode(null);
		documentViaFirma.updateCancelat(new Date());
		document.updateEstat(
				DocumentEstatEnumDto.REDACCIO);
		contingutLogHelper.log(
				document,
				LogTipusEnumDto.VFIRMA_CANCELACIO,
				documentViaFirma.getMessageCode(),
				documentViaFirma.getEstat().name(),
				false,
				false);
	}
	
	@Transactional(readOnly = true)
	@Override
	public DocumentViaFirmaDto viaFirmaInfo(
			Long entitatId,
			Long id) {
		logger.debug("Obtenint informació del darrer enviament a viaFirma ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ")");
		DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
				entitatId,
				id,
				true,
				false);
		List<DocumentViaFirmaEntity> enviamentsPendents = documentViaFirmaRepository.findByDocumentAndEstatInOrderByCreatedDateDesc(
				document,
				new DocumentEnviamentEstatEnumDto[] {
						DocumentEnviamentEstatEnumDto.PENDENT,
						DocumentEnviamentEstatEnumDto.ENVIAT
				});
		if (enviamentsPendents.size() == 0) {
			throw new ValidationException(
					document.getId(),
					DocumentEntity.class,
					"Aquest document no te enviaments a viaFirma");
		}
		return conversioTipusHelper.convertir(
				enviamentsPendents.get(0),
				DocumentViaFirmaDto.class);
	}
	
	@Transactional
	@Override
	public List<ViaFirmaDispositiuDto> viaFirmaDispositius(
			String viaFirmaUsuari,
			UsuariDto usuariActual)
			throws NotFoundException, IllegalStateException, SistemaExternException {
		logger.debug("Obtenint ( obtenint els dispositius de viaFirma de l'uusuari: " + usuariActual.getCodi() + ")");
		List<ViaFirmaDispositiuDto> viaFirmaDispositiusDto = new ArrayList<ViaFirmaDispositiuDto>();
		String contasenya = null;
		try {
			//Recuperar usuaris viaFirma usuari actual
			UsuariEntity usuari = usuariRepository.findByCodi(usuariActual.getCodi());

			for (ViaFirmaUsuariEntity viaFirmaDispositiuDto : usuari.getViaFirmaUsuaris()) {
				if (viaFirmaDispositiuDto.getCodi().equals(viaFirmaUsuari)) {
					contasenya = viaFirmaDispositiuDto.getContrasenya();
				}
			}
			viaFirmaDispositiusDto = pluginHelper.getDeviceUser(
					viaFirmaUsuari, 
					contasenya);
		} catch (Exception ex) {
			logger.error(
					"Error a l'hora de recuperar els usuaris de viaFirma (" +
					"usuariCodi=" + usuariActual.getCodi() + ")",
					ex);
			throw new RuntimeException(
					"Error a l'hora de recuperar els usuaris de viaFirma (" +
					"usuariCodi=" + usuariActual.getCodi() + ")",
					ex);
		}
		return viaFirmaDispositiusDto;
	}
	
	@Transactional
	@Override
	public List<ViaFirmaUsuariDto> viaFirmaUsuaris(UsuariDto usuariActual)
			throws NotFoundException, IllegalStateException, SistemaExternException {
		logger.debug("Obtenint ( obtenint els usuaris de viaFirma de l'uusuari: " + usuariActual.getCodi() + ")");
		List<ViaFirmaUsuariDto> viaFirmaUsuaris = new ArrayList<ViaFirmaUsuariDto>();
		try {
			//Recuperar usuaris viaFirma usuari actual
			UsuariEntity usuari = usuariRepository.findByCodi(usuariActual.getCodi());
			Set<ViaFirmaUsuariEntity> viaFirmaUsuarisEntity = usuari.getViaFirmaUsuaris();
			
			Set<ViaFirmaUsuariDto> viaFirmaUsuarisDto = conversioTipusHelper.convertirSet(
					viaFirmaUsuarisEntity, 
					ViaFirmaUsuariDto.class);
			
			viaFirmaUsuaris = new ArrayList<ViaFirmaUsuariDto>(viaFirmaUsuarisDto);
		} catch (Exception ex) {
			logger.error(
					"Error a l'hora de recuperar els usuaris de viaFirma (" +
					"usuariCodi=" + usuariActual.getCodi() + ")",
					ex);
			throw new RuntimeException(
					"Error a l'hora de recuperar els usuaris de viaFirma (" +
					"usuariCodi=" + usuariActual.getCodi() + ")",
					ex);
		}
		return viaFirmaUsuaris;
	}
	
	@Transactional
	@Override
	public Exception processarRespostaViaFirma(String messageJson) {
		Exception exception = null;
		try {
			ViaFirmaRespostaDto response = viaFirmaHelper.processarRespostaViaFirma(messageJson);
			
			exception = viaFirmaCallback(
					response.getMessageCode(), 
					response.getStatus());
		} catch (Exception ex) {
			throw new RuntimeException(
					"Error a l'hora de cridar el callback dins del servidor",
					ex);
		}
		return exception;
	}
	
	@Transactional
	@Override
	public Exception viaFirmaCallback(
			String messageCode, 
			ViaFirmaCallbackEstatEnumDto callbackEstat) throws NotFoundException {
		logger.debug("Processant petició del callback ("
				+ "messageCode=" + messageCode + ", "
				+ "callbackEstat=" + callbackEstat + ")");
		DocumentViaFirmaEntity documentViaFirma = documentViaFirmaRepository.findByMessageCode(messageCode);
		if (documentViaFirma == null) {
			return new NotFoundException(
					"(messageCode=" + messageCode + ")",
					DocumentViaFirmaEntity.class);
		}
		contingutLogHelper.log(
				documentViaFirma.getDocument(),
				LogTipusEnumDto.VFIRMA_CALLBACK,
				documentViaFirma.getMessageCode(),
				documentViaFirma.getEstat().name(),
				false,
				false);
		documentViaFirma.updateCallbackEstat(callbackEstat);
		return documentHelper.viaFirmaProcessar(documentViaFirma);
	}

	
	@Transactional
	@Override
	public FitxerDto convertirPdfPerFirmaClient(
			Long entitatId,
			Long id) {
		logger.debug("Converteix un document en PDF per a la firma client ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ")");
		DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
				entitatId,
				id,
				true,
				false);
		return pluginHelper.conversioConvertirPdf(
				documentHelper.getFitxerAssociat(document, null),
				null);
	}

	@Transactional
	@Override
	public String generarIdentificadorFirmaClient(
			Long entitatId,
			Long id) {
		logger.debug("Generar identificador firma al navegador ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ")");
		documentHelper.comprovarDocumentDinsExpedientAccessible(
				entitatId,
				id,
				true,
				false);
		try {
			return firmaClientXifrar(
					new ObjecteFirmaApplet( 
							new Long(System.currentTimeMillis()),
							entitatId,
							id));
		} catch (Exception ex) {
			logger.error(
					"Error al generar l'identificador per la firma al navegador (" +
					"entitatId=" + entitatId + ", " +
					"documentId=" + id + ")",
					ex);
			throw new RuntimeException(
					"Error al generar l'identificador per la firma al navegador (" +
					"entitatId=" + entitatId + ", " +
					"documentId=" + id + ")",
					ex);
		}
	}

	@Transactional
	@Override
	public void processarFirmaClient(
			String identificador,
			String arxiuNom,
			byte[] arxiuContingut) {
		logger.debug("Custodiar identificador firma applet ("
				+ "identificador=" + identificador + ")");
		ObjecteFirmaApplet objecte = null;
		try {
			objecte = firmaAppletDesxifrar(
					identificador,
					CLAU_SECRETA);
		} catch (Exception ex) {
			throw new RuntimeException(
					"Error al desxifrar l'identificador per la firma via applet (" +
					"identificador=" + identificador + ")",
					ex);
		}
		if (objecte != null) {
			DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientModificable(
					objecte.getEntitatId(),
					objecte.getDocumentId(),
					false,
					true,
					false,
					false);
			// Registra al log la firma del document
			contingutLogHelper.log(
					document,
					LogTipusEnumDto.FIRMA_CLIENT,
					null,
					null,
					false,
					false);
			// Custodia el document firmat
			FitxerDto fitxer = new FitxerDto();
			fitxer.setNom(arxiuNom);
			fitxer.setContingut(arxiuContingut);
			fitxer.setContentType("application/pdf");
			document.updateEstat(
					DocumentEstatEnumDto.CUSTODIAT);
			String custodiaDocumentId = pluginHelper.arxiuDocumentGuardarPdfFirmat(
					document,
					fitxer);
			document.updateInformacioCustodia(
					new Date(),
					custodiaDocumentId,
					document.getCustodiaCsv());
			documentHelper.actualitzarVersionsDocument(document);
			// Registra al log la custòdia de la firma del document
			contingutLogHelper.log(
					document,
					LogTipusEnumDto.ARXIU_CUSTODIAT,
					custodiaDocumentId,
					null,
					false,
					false);
		} else {
			logger.error(
					"No s'han trobat les dades del document amb identificador applet (" +
					"identificador=" + identificador + ")");
			throw new RuntimeException(
					"No s'han trobat les dades del document amb identificador applet (" +
					"identificador=" + identificador + ")");
		}
	}
	
	
	@Transactional
	@Override
	public void notificacioActualitzarEstat(
			String identificador, 
			String referencia) {
		
		DocumentEnviamentInteressatEntity documentEnviamentInteressatEntity = documentEnviamentInteressatRepository.findByIdentificadorIReferencia(
				identificador, referencia);
		if (documentEnviamentInteressatEntity == null) {
			throw new NotFoundException(documentEnviamentInteressatEntity, DocumentEnviamentInteressatEntity.class);
		}
		try {
			pluginHelper.notificacioConsultarIActualitzarEstat(documentEnviamentInteressatEntity);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de notificacions";
			logger.error(errorDescripcio, ex);
			throw new RuntimeException(ex);
		}
	}	



	private DocumentDto toDocumentDto(
			DocumentEntity document) {
		return (DocumentDto)contingutHelper.toContingutDto(
				document,
				false,
				false,
				false,
				false,
				true,
				true,
				false);
	}

	private String firmaClientXifrar(
			ObjecteFirmaApplet objecte) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(baos);
		Long[] array = new Long[] {
				objecte.getSysdate(),
				objecte.getEntitatId(),
				objecte.getDocumentId()};
		os.writeObject(array);
		os.close();
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(
				Cipher.ENCRYPT_MODE,
				buildKey(CLAU_SECRETA));
		byte[] xifrat = cipher.doFinal(baos.toByteArray());
		return new String(Base64.encode(xifrat));
	}

	private static final String CLAU_SECRETA = "R1p3AR1p3AR1p3AR";
	private ObjecteFirmaApplet firmaAppletDesxifrar(
			String missatge,
			String key) throws Exception {
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(
				Cipher.DECRYPT_MODE,
				buildKey(key));
		ByteArrayInputStream bais = new ByteArrayInputStream(
				cipher.doFinal(
						Base64.decode(missatge.getBytes())));
		ObjectInputStream is = new ObjectInputStream(bais);
		Long[] array = (Long[])is.readObject();
		ObjecteFirmaApplet objecte = new ObjecteFirmaApplet(
				array[0],
				array[1],
				array[2]);
		is.close();
		return objecte;
	}
	private SecretKeySpec buildKey(String message) throws Exception {
		MessageDigest sha = MessageDigest.getInstance("SHA-1");
		byte[] key = sha.digest(message.getBytes());
		key = Arrays.copyOf(key, 16);
		return new SecretKeySpec(key, "AES");
	}

	public class ObjecteFirmaApplet implements Serializable {
		private Long sysdate;
		private Long entitatId;
		private Long documentId;
		public ObjecteFirmaApplet(
				Long sysdate,
				Long entitatId,
				Long documentId) {
			this.sysdate = sysdate;
			this.entitatId = entitatId;
			this.documentId = documentId;
		}
		public Long getSysdate() {
			return sysdate;
		}
		public void setSysdate(Long sysdate) {
			this.sysdate = sysdate;
		}
		public Long getEntitatId() {
			return entitatId;
		}
		public void setEntitatId(Long entitatId) {
			this.entitatId = entitatId;
		}
		public Long getDocumentId() {
			return documentId;
		}
		public void setDocumentId(Long documentId) {
			this.documentId = documentId;
		}
		private static final long serialVersionUID = -6929597339153341365L;
	}



	private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);
}
