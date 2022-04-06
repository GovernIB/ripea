/**
 * 
 */
package es.caib.ripea.core.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentPortafirmesDto;
import es.caib.ripea.core.api.dto.ExpedientTascaComentariDto;
import es.caib.ripea.core.api.dto.ExpedientTascaDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaExpedientTascaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PortafirmesPrioritatEnumDto;
import es.caib.ripea.core.api.dto.TascaEstatEnumDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.ExpedientTascaService;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientTascaComentariEntity;
import es.caib.ripea.core.entity.ExpedientTascaEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaExpedientTascaEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.firma.DocumentFirmaAppletHelper;
import es.caib.ripea.core.firma.DocumentFirmaAppletHelper.ObjecteFirmaApplet;
import es.caib.ripea.core.firma.DocumentFirmaPortafirmesHelper;
import es.caib.ripea.core.helper.CacheHelper;
import es.caib.ripea.core.helper.ContingutHelper;
import es.caib.ripea.core.helper.ContingutLogHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.DocumentHelper;
import es.caib.ripea.core.helper.EmailHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.helper.TascaHelper;
import es.caib.ripea.core.helper.UsuariHelper;
import es.caib.ripea.core.repository.AlertaRepository;
import es.caib.ripea.core.repository.ExpedientTascaComentariRepository;
import es.caib.ripea.core.repository.ExpedientTascaRepository;
import es.caib.ripea.core.repository.MetaExpedientRepository;
import es.caib.ripea.core.repository.MetaExpedientTascaRepository;
import es.caib.ripea.core.repository.UsuariRepository;

/**
 * Implementació dels mètodes per a gestionar expedient peticions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ExpedientTascaServiceImpl implements ExpedientTascaService {

	@Autowired
	private MetaExpedientRepository metaExpedientRepository;
	@Autowired
	private ExpedientTascaRepository expedientTascaRepository;
	@Autowired
	private MetaExpedientTascaRepository metaExpedientTascaRepository;
	@Autowired
	private ExpedientTascaComentariRepository expedientTascaComentariRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private EmailHelper emailHelper;
	@Autowired
	private ContingutHelper contingutHelper;
	@Autowired
	private AlertaRepository alertaRepository;
	@Autowired
	private DocumentHelper documentHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private UsuariHelper usuariHelper;
	@Autowired
	private DocumentFirmaPortafirmesHelper documentFirmaPortafirmesHelper;
	@Autowired
	private DocumentFirmaAppletHelper documentFirmaAppletHelper;
	@Autowired
	private ContingutLogHelper contingutLogHelper;
	@Autowired
	private TascaHelper tascaHelper;
	
	@Transactional(readOnly = true)
	@Override
	public List<ExpedientTascaDto> findAmbExpedient(
			Long entitatId,
			Long expedientId) {
		logger.debug("Obtenint la llista de l'expedient tasques (" +
				"entitatId=" + entitatId + ", " +
				"expedientId=" + expedientId + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				false,
				true,
				false,
				false,
				false, false, null);
		
		List<ExpedientTascaEntity> tasques = expedientTascaRepository.findByExpedient(expedient);
		return conversioTipusHelper.convertirList(tasques, ExpedientTascaDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ExpedientTascaDto> findAmbAuthentication(
			Long entitatId, 
			PaginacioParamsDto paginacioParams) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Obtenint la llista del usuari tasques (" +
				"auth=" + auth.getName() + ")");
		
		UsuariEntity usuariEntity = usuariRepository.findByCodi(auth.getName());
		
		List<ExpedientTascaEntity> tasques = expedientTascaRepository.findByResponsableAndEstat(
				usuariEntity,
				paginacioHelper.toSpringDataPageable(
						paginacioParams));
		return conversioTipusHelper.convertirList(tasques, ExpedientTascaDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public ContingutDto findTascaExpedient(
			Long entitatId,
			Long contingutId,
			Long tascaId,
			boolean ambFills,
			boolean ambVersions) {
		logger.debug("Obtenint expedient per tasca amb id per usuari ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ", "
				+ "ambFills=" + ambFills + ", "
				+ "ambVersions=" + ambVersions + ")");
		
		
		ContingutEntity contingut = contingutHelper.comprovarContingutPertanyTascaAccesible(
					entitatId,
					tascaId,
					contingutId);
		
		ContingutDto dto = contingutHelper.toContingutDto(
				contingut,
				true,
				ambFills,
				ambFills,
				true,
				true,
				true,
				ambVersions, null, false, null);
		dto.setAlerta(alertaRepository.countByLlegidaAndContingutId(
				false,
				dto.getId()) > 0);

		return dto;
	}

	@Transactional(readOnly = true)
	@Override
	public FitxerDto descarregar(
			Long entitatId,
			Long contingutId,
			Long tascaId,
			String versio) {
		logger.debug("Descarregant contingut del document ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + contingutId + ", "
				+ "versio=" + versio + ")");

		DocumentEntity document = (DocumentEntity) contingutHelper.comprovarContingutPertanyTascaAccesible(
				entitatId,
				tascaId,
				contingutId);

		return documentHelper.getFitxerAssociat(
				document,
				versio);
	}	

	@Transactional(readOnly = true)
	@Override
	public long countTasquesPendents() {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Obtenint la llista del usuari tasques (" +
				"auth=" + auth.getName() + ")");

		return cacheHelper.countTasquesPendents(
				auth.getName());
	}

	@Transactional(readOnly = true)
	@Override
	public List<MetaExpedientTascaDto> findAmbMetaExpedient(
			Long entitatId,
			Long metaExpedientId) {
		logger.debug("Obtenint la llista de l'expedient tasques (" +
				"entitatId=" + entitatId + ", " +
				"metaExpedientId=" + metaExpedientId + ")");
		
		MetaExpedientEntity metaExpedient = metaExpedientRepository.findOne(
				metaExpedientId);
		
		List<MetaExpedientTascaEntity> tasques = metaExpedientTascaRepository.findByMetaExpedientAndActivaTrue(
				metaExpedient);
		
		return conversioTipusHelper.convertirList(
				tasques,
				MetaExpedientTascaDto.class);
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<MetaExpedientTascaDto> findAmbEntitat(
			Long entitatId) {
		logger.debug("Obtenint la llista de l'expedient tasques (" +
				"entitatId=" + entitatId + ")");
		
		List<MetaExpedientTascaEntity> tasques = metaExpedientTascaRepository.findByActivaTrue();
		
		return conversioTipusHelper.convertirList(
				tasques,
				MetaExpedientTascaDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public ExpedientTascaDto findOne(Long expedientTascaId) {
		logger.debug("Consultant el expedient tasca " +
				"expedientTascaId=" +
				expedientTascaId +
				")");

		ExpedientTascaEntity expedientTascaEntity = expedientTascaRepository.findOne(expedientTascaId);

		return conversioTipusHelper.convertir(
				expedientTascaEntity,
				ExpedientTascaDto.class);

	}

	@Transactional
	@Override
	public ExpedientTascaDto canviarEstat(Long expedientTascaId, TascaEstatEnumDto tascaEstatEnumDto, String motiu) {
		logger.debug("Canviant estat del tasca " +
				"expedientTascaId=" + expedientTascaId +", "+
				"tascaEstatEnumDto=" + tascaEstatEnumDto +
				")");

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UsuariEntity responsableActual = usuariHelper.getUsuariByCodiDades(auth.getName());
		ExpedientTascaEntity expedientTascaEntity = tascaHelper.comprovarTasca(expedientTascaId);
		TascaEstatEnumDto estatAnterior = expedientTascaEntity.getEstat();
		
		if (tascaEstatEnumDto == TascaEstatEnumDto.REBUTJADA) {
			expedientTascaEntity.updateRebutjar(motiu);
		} else {
			expedientTascaEntity.updateEstat(tascaEstatEnumDto);
		}
		
		if(tascaEstatEnumDto == TascaEstatEnumDto.INICIADA) {
			expedientTascaEntity.updateResponsableActual(responsableActual);
		}
		
		if (tascaEstatEnumDto == TascaEstatEnumDto.FINALITZADA && expedientTascaEntity.getMetaExpedientTasca().getEstatFinalitzarTasca() != null) {
			ExpedientEntity expedientEntity = expedientTascaEntity.getExpedient();
			expedientEntity.updateExpedientEstat(expedientTascaEntity.getMetaExpedientTasca().getEstatFinalitzarTasca());
		}
		
		emailHelper.enviarEmailCanviarEstatTasca(expedientTascaEntity, estatAnterior);
		
		for (UsuariEntity responsable: expedientTascaEntity.getResponsables()) {
			cacheHelper.evictCountTasquesPendents(responsable.getCodi());	
		}

		log(expedientTascaEntity, LogTipusEnumDto.CANVI_ESTAT);
		
		return conversioTipusHelper.convertir(expedientTascaEntity,
				ExpedientTascaDto.class);
	}

	@Transactional
	@Override
	public ExpedientTascaDto updateResponsables(Long expedientTascaId, String usuariCodi) {
		logger.debug("Canviant responsable de la tasca " +
				"expedientTascaId=" + expedientTascaId +", "+
				"usuariCodi=" + usuariCodi +
				")");

		ExpedientTascaEntity expedientTascaEntity = tascaHelper.comprovarTasca(expedientTascaId);
		
		UsuariEntity nouResponsable = usuariHelper.getUsuariByCodi(usuariCodi);
		List<UsuariEntity> responsables = new ArrayList<UsuariEntity>();
		responsables.add(nouResponsable);
		
		expedientTascaEntity.updateResponsables(responsables);	
		
		emailHelper.enviarEmailReasignarResponsableTasca(expedientTascaEntity);
		
		for (UsuariEntity responsable: expedientTascaEntity.getResponsables()) {
			cacheHelper.evictCountTasquesPendents(responsable.getCodi());	
		}
		
		log(expedientTascaEntity, LogTipusEnumDto.CANVI_RESPONSABLES);
		
		return conversioTipusHelper.convertir(expedientTascaEntity,
				ExpedientTascaDto.class);
	}
	
	@Transactional(readOnly = true)
	@Override
	public MetaExpedientTascaDto findMetaExpedientTascaById(Long metaExpedientTascaId) {
		logger.debug("Consultant el metaexpedient tasca " +
				"expedientTascaId=" +
				metaExpedientTascaId +
				")");

		MetaExpedientTascaEntity metaExpedientTascaEntity = metaExpedientTascaRepository.findOne(metaExpedientTascaId);

		return conversioTipusHelper.convertir(
				metaExpedientTascaEntity,
				MetaExpedientTascaDto.class);

	}

	@Override
	@Transactional
	public ExpedientTascaDto createTasca(
			Long entitatId,
			Long expedientId,
			ExpedientTascaDto expedientTasca){
		
		logger.debug("Creant nou representant ("
					+ "entitatId=" + entitatId + ", "
					+ "expedientId=" + expedientId + ", "
					+ "expedientTasca=" + expedientTasca + ")");
		
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				false,
				false,
				false,
				false,
				false, false, null);

		MetaExpedientTascaEntity metaExpedientTascaEntity = metaExpedientTascaRepository.findOne(expedientTasca.getMetaExpedientTascaId());
		List<UsuariEntity> responsables = new ArrayList<UsuariEntity>();
		for (String responsableCodi: expedientTasca.getResponsablesCodi()) {
			UsuariEntity responsable = usuariHelper.getUsuariByCodiDades(responsableCodi);
			responsables.add(responsable);
		}

		ExpedientTascaEntity expedientTascaEntity = ExpedientTascaEntity.getBuilder(
				expedient, 
				metaExpedientTascaEntity, 
				responsables, 
				expedientTasca.getDataLimit()).build();

		if (expedientTasca.getComentari() != null && !expedientTasca.getComentari().isEmpty()) {
			ExpedientTascaComentariEntity comentari = ExpedientTascaComentariEntity.getBuilder(expedientTascaEntity, expedientTasca.getComentari()).build();
			expedientTascaEntity.addComentari(comentari);
		}
		
		if (metaExpedientTascaEntity.getEstatCrearTasca() != null) {
			expedient.updateExpedientEstat(metaExpedientTascaEntity.getEstatCrearTasca());
		}
		
		for (String responsableCodi: expedientTasca.getResponsablesCodi()) {
			cacheHelper.evictCountTasquesPendents(responsableCodi);	
		}
		
		expedientTascaRepository.save(expedientTascaEntity);
		log(expedientTascaEntity, LogTipusEnumDto.CREACIO);

		return conversioTipusHelper.convertir(
				expedientTascaEntity,
					ExpedientTascaDto.class);
	}	

	@Override
	@Transactional
	public void enviarEmailCrearTasca(Long expedientTascaId) {
		
		ExpedientTascaEntity expedientTascaEntity = tascaHelper.comprovarTasca(expedientTascaId);

		emailHelper.enviarEmailCanviarEstatTasca(
				expedientTascaEntity,
				null);
	}

	@Transactional
	@Override
	public DocumentDto createDocument(
			Long entitatId,
			Long pareId,
			Long tascaId,
			DocumentDto document,
			boolean comprovarMetaExpedient) {
		logger.debug("Creant nou document (" +
				"entitatId=" + entitatId + ", " +
				"pareId=" + pareId + ", " +
				"document=" + document + ")");
		ContingutEntity pare = contingutHelper.comprovarContingutPertanyTascaAccesible(
				entitatId,
				tascaId,
				pareId);
		
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
		
		ExpedientTascaEntity expedientTascaEntity = expedientTascaRepository.findOne(tascaId);
		if (expedientTascaEntity.getEstat() == TascaEstatEnumDto.PENDENT) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			expedientTascaEntity.updateEstat(TascaEstatEnumDto.INICIADA);
			UsuariEntity responsableActual = usuariHelper.getUsuariByCodiDades(auth.getName());
			expedientTascaEntity.updateResponsableActual(responsableActual);
		}

		return documentHelper.crearDocument(
				document,
				pare,
				expedient,
				metaDocument,
				null,
				true);
	}

	@Transactional
	@Override
	public DocumentDto updateDocument(
			Long entitatId,
			Long tascaId,
			DocumentDto documentDto,
			boolean comprovarMetaExpedient) {
		logger.debug("Actualitzant el document (" +
				"entitatId=" + entitatId + ", " +
				"id=" + documentDto.getId() + ", " +
				"document=" + documentDto + ")");
		DocumentEntity documentEntity = (DocumentEntity) contingutHelper.comprovarContingutPertanyTascaAccesible(
				entitatId,
				tascaId,
				documentDto.getId());
	
		return documentHelper.updateDocument(
				entitatId,
				documentEntity,
				documentDto,
				comprovarMetaExpedient);
	}

	@Transactional
	@Override
	public void portafirmesEnviar(
			Long entitatId,
			Long documentId,
			String assumpte,
			PortafirmesPrioritatEnumDto prioritat,
			Date dataCaducitat,
			String[] portafirmesResponsables,
			MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSeqTipus,
			MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus,
			Long[] annexosIds,
			Long tascaId,
			String transaccioId) {
		logger.debug("Enviant document a portafirmes (" +
				"entitatId=" + entitatId + ", " +
				"id=" + documentId + ", " +
				"assumpte=" + assumpte + ", " +
				"prioritat=" + prioritat + ", " +
				"dataCaducitat=" + dataCaducitat + ")");
		
		DocumentEntity document = (DocumentEntity) contingutHelper.comprovarContingutPertanyTascaAccesible(
				entitatId,
				tascaId,
				documentId);
		
		documentFirmaPortafirmesHelper.portafirmesEnviar(
				entitatId,
				document,
				assumpte,
				prioritat,
				dataCaducitat,
				null,
				portafirmesResponsables,
				portafirmesSeqTipus,
				portafirmesFluxTipus,
				annexosIds,
				transaccioId);
	}

	@Transactional(readOnly = true)
	@Override
	public DocumentPortafirmesDto portafirmesInfo(
			Long entitatId,
			Long tascaId,
			Long documentId) {
		logger.debug("Obtenint informació del darrer enviament a portafirmes ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + documentId + ")");
		DocumentEntity document = (DocumentEntity) contingutHelper.comprovarContingutPertanyTascaAccesible(
				entitatId,
				tascaId,
				documentId);

		return documentFirmaPortafirmesHelper.portafirmesInfo(
				entitatId,
				document);
	}

	@Transactional
	@Override
	public void portafirmesReintentar(
			Long entitatId,
			Long id, 
			Long tascaId) {
		logger.debug("Reintentant processament d'enviament a portafirmes amb error ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ")");
		DocumentEntity document = (DocumentEntity) contingutHelper.comprovarContingutPertanyTascaAccesible(
				entitatId,
				tascaId,
				id);
		documentFirmaPortafirmesHelper.portafirmesReintentar(
				entitatId,
				document);

	}

	@Transactional
	@Override
	public void portafirmesCancelar(
			Long entitatId,
			Long tascaId,
			Long docuemntId, 
			String rolActual) {
		logger.debug("Enviant document a portafirmes (" +
				"entitatId=" + entitatId + ", " +
				"id=" + docuemntId + ")");
		DocumentEntity document = (DocumentEntity) contingutHelper.comprovarContingutPertanyTascaAccesible(
				entitatId,
				tascaId,
				docuemntId);

		documentFirmaPortafirmesHelper.portafirmesCancelar(
				entitatId,
				document, 
				rolActual);
	}

	@Transactional(readOnly = true)
	@Override
	public DocumentDto findDocumentById(
			Long entitatId,
			Long tascaId,
			Long documentId) {
		logger.debug("Obtenint el document ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + documentId + ")");
		DocumentEntity document = (DocumentEntity) contingutHelper.comprovarContingutPertanyTascaAccesible(
				entitatId,
				tascaId,
				documentId);
		return toDocumentDto(document);
	}

	@Transactional
	@Override
	public FitxerDto convertirPdfPerFirmaClient(
			Long entitatId,
			Long tascaId,
			Long documentId) {
		logger.debug("Converteix un document en PDF per a la firma client ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + documentId + ")");
		
		DocumentEntity document = (DocumentEntity) contingutHelper.comprovarContingutPertanyTascaAccesible(
				entitatId,
				tascaId,
				documentId);
		
		
		return pluginHelper.conversioConvertirPdf(
				documentHelper.getFitxerAssociat(document, null),
				null);
	}

	@Transactional
	@Override
	public String generarIdentificadorFirmaClient(
			Long entitatId,
			Long tascaId,
			Long id) {
		logger.debug("Generar identificador firma al navegador ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ")");
		contingutHelper.comprovarContingutPertanyTascaAccesible(entitatId, tascaId, id);
		try {
			return documentFirmaAppletHelper.firmaClientXifrar(
					documentFirmaAppletHelper.obtainInstanceObjecteFirmaApplet( 
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
			byte[] arxiuContingut,
			Long tascaId) {
		logger.debug("Custodiar identificador firma applet ("
				+ "identificador=" + identificador + ")");
		ObjecteFirmaApplet objecte = null;
		try {
			objecte = documentFirmaAppletHelper.firmaAppletDesxifrar(
					identificador,
					DocumentFirmaAppletHelper.CLAU_SECRETA);
		} catch (Exception ex) {
			throw new RuntimeException(
					"Error al desxifrar l'identificador per la firma via applet (" +
					"identificador=" + identificador + ")",
					ex);
		}
		if (objecte != null) {
			DocumentEntity document = (DocumentEntity) contingutHelper.comprovarContingutPertanyTascaAccesible(objecte.getEntitatId(), tascaId, objecte.getDocumentId());
			documentFirmaAppletHelper.processarFirmaClient(
					identificador,
					arxiuNom,
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

	@Transactional
	@Override
	@CacheEvict(value = "errorsValidacioNode", key = "#contingutId")
	public ContingutDto deleteTascaReversible(
			Long entitatId,
			Long tascaId,
			Long contingutId) throws IOException {
		logger.debug("Esborrant el contingut ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ")");
		
		ContingutEntity contingut = contingutHelper.comprovarContingutPertanyTascaAccesible(
				entitatId,
				tascaId,
				contingutId);
		
		return contingutHelper.deleteReversible(
				entitatId,
				contingut, null);
	}

	@Transactional
	@Override
	public boolean publicarComentariPerExpedientTasca(
			Long entitatId,
			Long expedientTascaId,
			String text,
			String rolActual) {
		logger.debug("Obtenint els comentaris per la tasca (" + "entitatId=" + entitatId + ", " + "tascaId=" + expedientTascaId + ")");

		entityComprovarHelper.comprovarEntitat(entitatId, false, false, true, false, false);

		ExpedientTascaEntity tasca = expedientTascaRepository.findOne(expedientTascaId);
		if (tasca == null) {
			throw new NotFoundException(expedientTascaId, ExpedientTascaEntity.class);
		}

		entityComprovarHelper.comprovarExpedient(
				entitatId,
				tasca.getExpedient().getId(),
				false,
				false,
				true,
				false,
				false,
				false,
				rolActual);

		// truncam a 1024 caracters
		if (text.length() > 1024)
			text = text.substring(0, 1021) + "...";
		ExpedientTascaComentariEntity comentari = ExpedientTascaComentariEntity.getBuilder(tasca, text).build();
		expedientTascaComentariRepository.save(comentari);
		return true;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ExpedientTascaComentariDto> findComentarisPerTasca(Long entitatId, Long expedientTascaId) {
		logger.debug("Obtenint els comentaris per la tasca (" + "entitatId=" + entitatId + ", " + "tascaId=" + expedientTascaId + ")");
		entityComprovarHelper.comprovarEntitat(entitatId, false, false, true, false, false);

		ExpedientTascaEntity tasca = expedientTascaRepository.findOne(expedientTascaId);
		if (tasca == null) {
			throw new NotFoundException(expedientTascaId, ExpedientTascaEntity.class);
		}

		entityComprovarHelper.comprovarExpedient(
				entitatId,
				tasca.getExpedient().getId(),
				false,
				true,
				false,
				false,
				false, false, null);

		List<ExpedientTascaComentariEntity> tascacoms = expedientTascaComentariRepository.findByExpedientTascaOrderByCreatedDateAsc(tasca);

		return conversioTipusHelper.convertirList(tascacoms, ExpedientTascaComentariDto.class);
	}

	private void log (ExpedientTascaEntity expedientTascaEntity, LogTipusEnumDto tipusLog) {
		contingutLogHelper.log(
				expedientTascaEntity.getExpedient(),
				LogTipusEnumDto.MODIFICACIO,
				expedientTascaEntity,
				LogObjecteTipusEnumDto.TASCA,
				tipusLog,
				expedientTascaEntity.getMetaExpedientTasca().getNom(),
				expedientTascaEntity.getComentaris().size() == 1 ? expedientTascaEntity.getComentaris().get(0).getText() : null, // expedientTascaEntity.getComentari(),
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
				false, null, false, null);
	}

	/*private String getIdiomaPerDefecte() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.usuari.idioma.defecte",
				"CA");
	}*/

	private static final Logger logger = LoggerFactory.getLogger(ExpedientTascaServiceImpl.class);

}
