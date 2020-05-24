/**
 * 
 */
package es.caib.ripea.core.service;

import java.io.IOException;
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
import es.caib.ripea.core.api.dto.ExpedientTascaDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaExpedientTascaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PortafirmesPrioritatEnumDto;
import es.caib.ripea.core.api.dto.TascaEstatEnumDto;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.ExpedientTascaService;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientTascaEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaExpedientTascaEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.helper.CacheHelper;
import es.caib.ripea.core.helper.ContingutHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.DocumentHelper;
import es.caib.ripea.core.helper.DocumentHelper.ObjecteFirmaApplet;
import es.caib.ripea.core.helper.EmailHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.helper.UsuariHelper;
import es.caib.ripea.core.repository.AlertaRepository;
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
				false);
		
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
				ambVersions);
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

		ExpedientTascaEntity expedientTascaEntity = expedientTascaRepository.findOne(expedientTascaId);
		TascaEstatEnumDto estatAnterior = expedientTascaEntity.getEstat();
				
		if (tascaEstatEnumDto == TascaEstatEnumDto.REBUTJADA) {
			expedientTascaEntity.updateRebutjar(motiu);
		} else {
			expedientTascaEntity.updateEstat(tascaEstatEnumDto);
		}
		
		emailHelper.enviarEmailCanviarEstatTasca(expedientTascaEntity, estatAnterior);
		cacheHelper.evictCountTasquesPendents(expedientTascaEntity.getResponsable().getCodi());
		
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
				false);

		MetaExpedientTascaEntity metaExpedientTascaEntity = metaExpedientTascaRepository.findOne(expedientTasca.getMetaExpedientTascaId());
		
		UsuariEntity responsable = usuariHelper.getUsuariByCodiDades(expedientTasca.getResponsableCodi());

		ExpedientTascaEntity expedientTascaEntity = ExpedientTascaEntity.getBuilder(expedient, metaExpedientTascaEntity, responsable).build();
		
		cacheHelper.evictCountTasquesPendents(expedientTascaEntity.getResponsable().getCodi());
		
		return conversioTipusHelper.convertir(
					expedientTascaRepository.save(expedientTascaEntity),
					ExpedientTascaDto.class);
		
	}	

	@Override
	@Transactional
	public void enviarEmailCrearTasca(Long expedientTascaId) {
		
		ExpedientTascaEntity expedientTascaEntity = expedientTascaRepository.findOne(expedientTascaId);

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
			expedientTascaEntity.updateEstat(TascaEstatEnumDto.INICIADA);
		}

		return documentHelper.crearDocument(
				document,
				pare,
				expedient,
				metaDocument);
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
		
		documentHelper.portafirmesEnviar(
				entitatId,
				document,
				assumpte,
				prioritat,
				dataCaducitat,
				portafirmesResponsables,
				portafirmesSeqTipus,
				portafirmesFluxTipus,
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

		return documentHelper.portafirmesInfo(
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
		documentHelper.portafirmesReintentar(
				entitatId,
				document);

	}

	@Transactional
	@Override
	public void portafirmesCancelar(
			Long entitatId,
			Long tascaId,
			Long docuemntId) {
		logger.debug("Enviant document a portafirmes (" +
				"entitatId=" + entitatId + ", " +
				"id=" + docuemntId + ")");
		DocumentEntity document = (DocumentEntity) contingutHelper.comprovarContingutPertanyTascaAccesible(
				entitatId,
				tascaId,
				docuemntId);

		documentHelper.portafirmesCancelar(
				entitatId,
				document);
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
			byte[] arxiuContingut,
			Long tascaId) {
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
			DocumentEntity document = (DocumentEntity) contingutHelper.comprovarContingutPertanyTascaAccesible(objecte.getEntitatId(), tascaId, objecte.getDocumentId());
			documentHelper.processarFirmaClient(
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
				contingut);
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

	/*private String getIdiomaPerDefecte() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.usuari.idioma.defecte",
				"CA");
	}*/

	private static final Logger logger = LoggerFactory.getLogger(ExpedientTascaServiceImpl.class);

}
