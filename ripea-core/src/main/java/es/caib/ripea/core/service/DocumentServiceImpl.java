/**
 * 
 */
package es.caib.ripea.core.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.plugins.arxiu.api.Document;
import es.caib.ripea.core.api.dto.ArxiuFirmaDetallDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentPortafirmesDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentViaFirmaDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentTipusGenericEnumDto;
import es.caib.ripea.core.api.dto.NotificacioInfoRegistreDto;
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
import es.caib.ripea.core.entity.DocumentNotificacioEntity;
import es.caib.ripea.core.entity.DocumentPortafirmesEntity;
import es.caib.ripea.core.entity.DocumentViaFirmaEntity;
import es.caib.ripea.core.entity.EntitatEntity;
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
import es.caib.ripea.core.helper.EmailHelper;
import es.caib.ripea.core.helper.DocumentHelper.ObjecteFirmaApplet;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.PermisosHelper;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.helper.PropertiesHelper;
import es.caib.ripea.core.helper.ViaFirmaHelper;
import es.caib.ripea.core.repository.DispositiuEnviamentRepository;
import es.caib.ripea.core.repository.DocumentEnviamentInteressatRepository;
import es.caib.ripea.core.repository.DocumentNotificacioRepository;
import es.caib.ripea.core.repository.DocumentPortafirmesRepository;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.core.repository.DocumentViaFirmaRepository;
import es.caib.ripea.core.repository.MetaDocumentRepository;
import es.caib.ripea.core.repository.UsuariRepository;
import es.caib.ripea.core.security.ExtendedPermission;
import es.caib.ripea.plugin.notificacio.RespostaConsultaEstatEnviament;
import es.caib.ripea.plugin.notificacio.RespostaConsultaInfoRegistre;

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
	@Autowired
	private DocumentEnviamentInteressatRepository documentEnviamentInteressatRepository;
	@Autowired
	private MetaDocumentRepository metaDocumentRepository;
	@Autowired
	private EmailHelper emailHelper;
	
	@Transactional
	@Override
	public DocumentDto create(
			Long entitatId,
			Long pareId,
			DocumentDto document,
			boolean comprovarMetaExpedient) {
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
		ExpedientEntity expedient = pare.getExpedientPare();
		MetaDocumentEntity metaDocument = null;
		if (document.getMetaDocument() != null) {
			metaDocument = entityComprovarHelper.comprovarMetaDocument(
					pare.getEntitat(),
					expedient.getMetaExpedient(),
					document.getMetaDocument().getId(),
					true,
					comprovarMetaExpedient);
		} else {
			throw new ValidationException(
					"<creacio>",
					ExpedientEntity.class,
					"No es pot crear un document sense un meta-document associat");
		}
		return documentHelper.crearDocument(
				document,
				pare,
				expedient,
				metaDocument);
	}

	@Transactional
	@Override
	public DocumentDto update(
			Long entitatId,
			DocumentDto documentDto,
			boolean comprovarMetaExpedient) {
		logger.debug("Actualitzant el document (" +
				"entitatId=" + entitatId + ", " +
				"id=" + documentDto.getId() + ", " +
				"document=" + documentDto + ")");
		DocumentEntity documentEntity = documentHelper.comprovarDocumentDinsExpedientModificable(
				entitatId,
				documentDto.getId(),
				false,
				true,
				false,
				false);
		return documentHelper.updateDocument(
				entitatId,
				documentEntity,
				documentDto,
				comprovarMetaExpedient);
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
	public List<DocumentDto> findAnnexosAmbExpedient(
			Long entitatId,
			DocumentDto document) {
		if (document.getExpedientPare() == null)
			throw new ValidationException(
					document.getId(),
					DocumentEntity.class,
					"El document seleccionat no disposa d'expedient pare");
		Long expedientId = document.getExpedientPare().getId();
		
		logger.debug("Obtenint els documents (annexos) de l'expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "expedientId=" + expedientId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId, 
				true, 
				false, 
				false);
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				false,
				false,
				false,
				false,
				false);
		List<DocumentEntity> documents = documentRepository.findByExpedientAndTipus(
				entitat, 
				expedient,
				document.getId());
		return conversioTipusHelper.convertirList(
				documents, 
				DocumentDto.class);
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
			MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSeqTipus,
			MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus,
			Long[] annexosIds,
			String transaccioId) {
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
		
		documentHelper.portafirmesEnviar(
				entitatId,
				document,
				assumpte,
				prioritat,
				dataCaducitat,
				portafirmesResponsables,
				portafirmesSeqTipus,
				portafirmesFluxTipus,
				annexosIds,
				transaccioId);
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

		documentHelper.portafirmesCancelar(
				entitatId,
				document);
	}
	
	@Transactional
	@Override
	public Exception portafirmesCallback(
			long portafirmesId,
			PortafirmesCallbackEstatEnumDto callbackEstat,
			String motiuRebuig) {
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
		documentPortafirmes.updateMotiuRebuig(motiuRebuig);
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
		documentHelper.portafirmesReintentar(
				entitatId,
				document);

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

		return documentHelper.portafirmesInfo(
				entitatId,
				document);
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
					"Error a l'hora d'enviar el document a viaFirma: " + ex.getMessage(),
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
			return documentHelper.firmaClientXifrar(
					documentHelper.obtainInstanceObjecteFirmaApplet( 
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
			objecte = documentHelper.firmaAppletDesxifrar(
					identificador,
					DocumentHelper.CLAU_SECRETA);
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
			
			documentHelper.processarFirmaClient(
					identificador,
					changeExtensioToPdf(arxiuNom),
					arxiuContingut,
					document);
		} else {
			logger.error(
					"No s'han trobat les dades del document amb identificador applet (" +
					"identificador=" + identificador + ")");
			throw new RuntimeException(
					"No s'han trobat les dades del document amb identificador applet (" +
					"identificador=" + identificador + ")");
		}
	}
	
	private String changeExtensioToPdf(String nom) {
		int indexPunt = nom.lastIndexOf(".");
		if (indexPunt != -1 && indexPunt < nom.length() - 1) {
			return nom.substring(0,indexPunt)+ ".pdf";
		} else {
			return nom;
		}
	}
	
	
	@Transactional
	@Override
	public void notificacioActualitzarEstat(
			String identificador, 
			String referencia) {
		logger.debug("Rebre callback de notib: identificador=" + identificador + ", referencia=" + referencia);
		DocumentEnviamentInteressatEntity documentEnviamentInteressatEntity = documentEnviamentInteressatRepository.findByIdentificadorIReferencia(
				identificador, referencia);
		try {
			if (documentEnviamentInteressatEntity == null) {
				logger.error("Callback de notib envia notificació que no existeix a la base de dades: identificador=" + identificador + ", referencia=" + referencia);
				// throw new NotFoundException(documentEnviamentInteressatEntity, DocumentEnviamentInteressatEntity.class);
			} else {
				DocumentNotificacioEntity notificacio = documentEnviamentInteressatEntity.getNotificacio();
				DocumentNotificacioEstatEnumDto estatAnterior = notificacio.getNotificacioEstat();
				logger.debug("Estat anterior: " + estatAnterior);
				RespostaConsultaEstatEnviament resposta = pluginHelper.notificacioConsultarIActualitzarEstat(documentEnviamentInteressatEntity);
				if (getPropertyGuardarCertificacioExpedient() && documentEnviamentInteressatEntity.getEnviamentCertificacioData() == null && resposta.getCertificacioData() != null) {
					logger.debug("[CERT] Guardant certificació rebuda de Notib...");
					MetaDocumentEntity metaDocument = metaDocumentRepository.findByEntitatAndTipusGeneric(
							true, 
							null, 
							MetaDocumentTipusGenericEnumDto.ACUSE_RECIBO_NOTIFICACION);
					DocumentDto document = documentHelper.certificacioToDocumentDto(
							documentEnviamentInteressatEntity,
							metaDocument,
							resposta);
					DocumentDto documentCreat = documentHelper.crearDocument(
							document, 
							documentEnviamentInteressatEntity.getNotificacio().getDocument().getPare(), 
							documentEnviamentInteressatEntity.getNotificacio().getDocument().getExpedientPare(), 
							metaDocument);
					
					DocumentEntity documentEntity = documentRepository.findOne(documentCreat.getId());
					documentEntity.updateEstat(DocumentEstatEnumDto.CUSTODIAT);
					logger.debug("[CERT] La certificació s'ha guardat correctament...");
				}
				DocumentNotificacioEstatEnumDto estatDespres = documentEnviamentInteressatEntity.getNotificacio().getNotificacioEstat();
				logger.debug("Estat després: " + estatDespres);
				if (estatAnterior != estatDespres 
						&& (estatAnterior != DocumentNotificacioEstatEnumDto.FINALITZADA && estatDespres != DocumentNotificacioEstatEnumDto.PROCESSADA)) {
					emailHelper.canviEstatNotificacio(notificacio, estatAnterior);
				}
			}
			
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de notificacions";
			logger.error(errorDescripcio, ex);
			throw new RuntimeException(ex);
		}
	}	
	
	@Transactional
	@Override
	public byte[] notificacioConsultarIDescarregarCertificacio(Long documentEnviamentInteressatId){
		
		DocumentEnviamentInteressatEntity documentEnviamentInteressatEntity = documentEnviamentInteressatRepository.findOne(
				documentEnviamentInteressatId);
		return pluginHelper.notificacioConsultarIDescarregarCertificacio(documentEnviamentInteressatEntity);
	}
	
	@Override
	public NotificacioInfoRegistreDto notificacioConsultarIDescarregarJustificant(
			Long entitatId,
			Long documentId,
			Long docuemntEnviamentId) {
		NotificacioInfoRegistreDto infoRegistre = new NotificacioInfoRegistreDto();
		try {
			DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
					entitatId,
					documentId,
					false,
					true);
			ExpedientEntity expedient = document.getExpedient();
			if (expedient == null) {
				throw new ValidationException(
						documentId,
						DocumentEntity.class,
						"El document no te cap expedient associat (documentId=" + documentId + ")");
			}
			DocumentEnviamentInteressatEntity enviament = documentEnviamentInteressatRepository.findOne(docuemntEnviamentId);
			
			RespostaConsultaInfoRegistre resposta = pluginHelper.notificacioConsultarIDescarregarJustificant(
					enviament);
			
			if (!resposta.isError() && resposta != null) {
				infoRegistre.setDataRegistre(resposta.getDataRegistre());
				infoRegistre.setNumRegistreFormatat(resposta.getNumRegistreFormatat());
				infoRegistre.setJustificant(resposta.getJustificant());
			} else {
				infoRegistre.setError(true);
				infoRegistre.setErrorData(resposta.getErrorData());
				infoRegistre.setErrorDescripcio(resposta.getErrorDescripcio());
				return infoRegistre;
			}
		} catch (Exception ex) {
			logger.error("No s'ha pogut recuperar la informació del registre", ex);
		}
		return infoRegistre;
	}

	@Override
	@Transactional
	public void documentActualitzarEstat(
			Long entitatId, 
			Long documentId,
			DocumentEstatEnumDto nouEstat) {
		DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
				entitatId,
				documentId,
				false,
				true);
			
		if (document.getEstat().equals(DocumentEstatEnumDto.REDACCIO) && !document.getDocumentTipus().equals(DocumentTipusEnumDto.IMPORTAT)) {
			document.updateEstat(nouEstat);
		}
		contingutLogHelper.log(
				document,
				LogTipusEnumDto.CANVI_ESTAT,
				nouEstat.name(),
				null,
				false,
				false);
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
	
	private boolean getPropertyGuardarCertificacioExpedient() {
		return PropertiesHelper.getProperties().getAsBoolean(
				"es.caib.ripea.notificacio.guardar.certificacio.expedient");
	}
	
	private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);
	
}
