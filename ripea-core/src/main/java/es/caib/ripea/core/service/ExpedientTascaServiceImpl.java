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
import es.caib.ripea.core.api.service.ExpedientTascaService;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientTascaComentariEntity;
import es.caib.ripea.core.entity.ExpedientTascaEntity;
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
import es.caib.ripea.core.helper.OrganGestorHelper;
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
	@Autowired
	private OrganGestorHelper organGestorHelper;
	
	@Transactional(readOnly = true)
	@Override
	public List<ExpedientTascaDto> findAmbExpedient(
			Long entitatId,
			Long expedientId) {
		logger.debug("Obtenint la llista de l'expedient tasques (" +
				"entitatId=" + entitatId + ", " +
				"expedientId=" + expedientId + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				expedientId,
				false,
				true,
				false,
				false,
				false,
				null);
		
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
				true,
				true,
				true,
				ambVersions,
				null,
				false,
				null,
				false,
				0,
				null,
				null,
				true,
				true,
				true,
				false);
		dto.setAlerta(alertaRepository.countByLlegidaAndContingutId(
				false,
				dto.getId()) > 0);

		return dto;
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
	public ExpedientTascaDto canviarTascaEstat(Long tascaId, TascaEstatEnumDto tascaEstat, String motiu) {
		logger.debug("Canviant estat del tasca " +
				"tascaId=" + tascaId +", "+
				"tascaEstat=" + tascaEstat +
				")");

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UsuariEntity responsableActual = usuariHelper.getUsuariByCodiDades(auth.getName(), true, true);
		ExpedientTascaEntity tasca = tascaHelper.comprovarTasca(tascaId);
		TascaEstatEnumDto tascaEstatAnterior = tasca.getEstat();
		
		if (tascaEstat == TascaEstatEnumDto.REBUTJADA) {
			tasca.updateRebutjar(motiu);
		} else {
			tasca.updateEstat(tascaEstat);
		}
		
		if(tascaEstat == TascaEstatEnumDto.INICIADA) {
			tasca.updateResponsableActual(responsableActual);
		}
		
		if (tascaEstat == TascaEstatEnumDto.FINALITZADA && tasca.getMetaTasca().getEstatFinalitzarTasca() != null) {
			ExpedientEntity expedientEntity = tasca.getExpedient();
			expedientEntity.updateEstatAdditional(tasca.getMetaTasca().getEstatFinalitzarTasca());
		}
		
		emailHelper.enviarEmailCanviarEstatTasca(tasca, tascaEstatAnterior);
		
		for (UsuariEntity responsable: tasca.getResponsables()) {
			cacheHelper.evictCountTasquesPendents(responsable.getCodi());	
		}

		log(tasca, LogTipusEnumDto.CANVI_ESTAT);
		
		return conversioTipusHelper.convertir(tasca,
				ExpedientTascaDto.class);
	}

	@Transactional
	@Override
	public ExpedientTascaDto updateResponsables(Long expedientTascaId, List<String> responsablesCodi) {
		logger.debug("Canviant responsable de la tasca " +
				"expedientTascaId=" + expedientTascaId +", "+
				"responsablesCodi=" + responsablesCodi +
				")");

		ExpedientTascaEntity expedientTascaEntity = expedientTascaRepository.findOne(expedientTascaId);
		
		List<UsuariEntity> responsables = new ArrayList<UsuariEntity>();
		for (String responsableCodi: responsablesCodi) {
			UsuariEntity responsable = usuariHelper.getUsuariByCodiDades(responsableCodi, true, true);
			responsables.add(responsable);
		}
		
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
				expedientId,
				false,
				false,
				false,
				false,
				false,
				null);

		MetaExpedientTascaEntity metaExpedientTascaEntity = metaExpedientTascaRepository.findOne(expedientTasca.getMetaExpedientTascaId());
		List<UsuariEntity> responsables = new ArrayList<UsuariEntity>();
		for (String responsableCodi: expedientTasca.getResponsablesCodi()) {
			UsuariEntity responsable = usuariHelper.getUsuariByCodiDades(responsableCodi, true, true);
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
			expedient.updateEstatAdditional(metaExpedientTascaEntity.getEstatCrearTasca());
		}
		
		for (String responsableCodi: expedientTasca.getResponsablesCodi()) {
			cacheHelper.evictCountTasquesPendents(responsableCodi);	
		}
		
		expedientTascaRepository.save(expedientTascaEntity);
		log(expedientTascaEntity, LogTipusEnumDto.CREACIO);
		
		emailHelper.enviarEmailCanviarEstatTasca(
				expedientTascaEntity,
				null);

		return conversioTipusHelper.convertir(
				expedientTascaEntity,
					ExpedientTascaDto.class);
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
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(documentId));
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
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(documentId));
		
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
			Long entitatId,
			Long documentId,
			String arxiuNom,
			byte[] arxiuContingut, 
			Long tascaId) {
		String identificador = documentHelper.generarIdentificadorFirmaClient(
				entitatId,
				documentId);
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
				tasca.getExpedient().getId(),
				false,
				false,
				true,
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
				tasca.getExpedient().getId(),
				false,
				true,
				false,
				false,
				false,
				null);

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
				expedientTascaEntity.getMetaTasca().getNom(),
				expedientTascaEntity.getComentaris().size() == 1 ? expedientTascaEntity.getComentaris().get(0).getText() : null, // expedientTascaEntity.getComentari(),
				false,
				false);
	}
	
	private DocumentDto toDocumentDto(
			DocumentEntity document) {
		return (DocumentDto) contingutHelper.toContingutDto(
				document,
				false,
				false,
				false,
				true,
				true,
				false,
				null,
				false,
				null,
				false,
				0,
				null,
				null,
				true,
				true,
				false,
				false);
	}

	/*private String getIdiomaPerDefecte() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.usuari.idioma.defecte",
				"CA");
	}*/

	private static final Logger logger = LoggerFactory.getLogger(ExpedientTascaServiceImpl.class);

}
