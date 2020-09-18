/**
 *
 */
package es.caib.ripea.core.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.dto.ArbreDto;
import es.caib.ripea.core.api.dto.ComunitatDto;
import es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.MultiplicitatEnumDto;
import es.caib.ripea.core.api.dto.MunicipiDto;
import es.caib.ripea.core.api.dto.NivellAdministracioDto;
import es.caib.ripea.core.api.dto.PaisDto;
import es.caib.ripea.core.api.dto.ProvinciaDto;
import es.caib.ripea.core.api.dto.ResultatDominiDto;
import es.caib.ripea.core.api.dto.TipusViaDto;
import es.caib.ripea.core.api.dto.UnitatOrganitzativaDto;
import es.caib.ripea.core.api.dto.ValidacioErrorDto;
import es.caib.ripea.core.api.exception.DominiException;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DadaEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.DocumentNotificacioEntity;
import es.caib.ripea.core.entity.DocumentPortafirmesEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.MetaDadaEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.NodeEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.ripea.core.repository.AclSidRepository;
import es.caib.ripea.core.repository.DadaRepository;
import es.caib.ripea.core.repository.DocumentNotificacioRepository;
import es.caib.ripea.core.repository.DocumentPortafirmesRepository;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.ExpedientTascaRepository;
import es.caib.ripea.core.repository.MetaDadaRepository;
import es.caib.ripea.core.repository.MetaDocumentRepository;
import es.caib.ripea.core.repository.UsuariRepository;
import es.caib.ripea.core.security.ExtendedPermission;
import es.caib.ripea.plugin.usuari.DadesUsuari;

/**
 * Utilitat per a accedir a les caches. Els mètodes cacheables es
 * defineixen aquí per evitar la impossibilitat de fer funcionar
 * l'anotació @Cacheable als mètodes privats degut a limitacions
 * AOP.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class CacheHelper {

	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private DadaRepository dadaRepository;
	@Resource
	private DocumentRepository documentRepository;
	@Resource
	private MetaDadaRepository metaDadaRepository;
	@Resource
	private MetaDocumentRepository metaDocumentRepository;

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private ContingutHelper contenidorHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private PermisosEntitatHelper permisosEntitatHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private UsuariHelper usuariHelper;
	@Resource
	private UsuariRepository usuariRepository;
	@Resource
	private ExpedientTascaRepository expedientTascaRepository;
	@Resource
	private DocumentPortafirmesRepository documentPortafirmesRepository;
	@Resource
	private DocumentNotificacioRepository documentNotificacioRepository;
	@Resource
	private AclSidRepository aclSidRepository;

	
	@Cacheable(value = "tasquesUsuari", key="#usuariCodi")
	public long countTasquesPendents(String usuariCodi) {
		logger.debug("Consulta entitats accessibles (usuariCodi=" + usuariCodi + ")");
		UsuariEntity usuariEntity = usuariRepository.findByCodi(usuariCodi);
		return expedientTascaRepository.countTasquesPendents(usuariEntity);
	}
	@CacheEvict(value = "tasquesUsuari", key="#usuariCodi")
	public void evictCountTasquesPendents(String usuariCodi) {
	}



	@Cacheable(value = "entitatsUsuari", key="#usuariCodi")
	public List<EntitatDto> findEntitatsAccessiblesUsuari(String usuariCodi) {
		logger.debug("Consulta entitats accessibles (usuariCodi=" + usuariCodi + ")");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<EntitatEntity> entitats = entitatRepository.findByActiva(true);
		permisosHelper.filterGrantedAny(
				entitats,
				new ObjectIdentifierExtractor<EntitatEntity>() {
					public Long getObjectIdentifier(EntitatEntity entitat) {
						return entitat.getId();
					}
				},
				EntitatEntity.class,
				new Permission[] {
					ExtendedPermission.READ,
					ExtendedPermission.ADMINISTRATION},
				auth);
		List<EntitatDto> resposta = conversioTipusHelper.convertirList(
				entitats,
				EntitatDto.class);
		permisosEntitatHelper.omplirPermisosPerEntitats(
				resposta,
				false);
		return resposta;
	}
	@CacheEvict(value = "entitatsUsuari", key="#usuariCodi")
	public void evictEntitatsAccessiblesUsuari(String usuariCodi) {
	}

	@Cacheable(value = "errorsValidacioNode", key = "#node.id")
	public List<ValidacioErrorDto> findErrorsValidacioPerNode(
			NodeEntity node) {
		logger.debug("Consulta dels errors de validació pel node (nodeId=" + node.getId() + ")");
		List<ValidacioErrorDto> errors = new ArrayList<ValidacioErrorDto>();
		List<DadaEntity> dades = dadaRepository.findByNode(node);
		// Valida dades específiques del meta-node
		List<MetaDadaEntity> metaDades = metaDadaRepository.findByMetaNodeAndActivaTrueOrderByOrdreAsc(node.getMetaNode());
		for (MetaDadaEntity metaDada: metaDades) {
			if (metaDada.getMultiplicitat().equals(MultiplicitatEnumDto.M_1) || metaDada.getMultiplicitat().equals(MultiplicitatEnumDto.M_1_N)) {
				boolean trobada = false;
				for (DadaEntity dada: dades) {
					if (dada.getMetaDada() != null && dada.getMetaDada().equals(metaDada)) {
						trobada = true;
						break;
					}
				}
				if (!trobada)
					errors.add(
							crearValidacioError(
									metaDada,
									metaDada.getMultiplicitat()));
			}
		}
		if (node instanceof ExpedientEntity) {
			ExpedientEntity expedient = (ExpedientEntity)node;
			List<DocumentEntity> documents = documentRepository.findByExpedientAndEsborrat(
					expedient,
					0);
			// Valida documents específics del meta-node
			List<MetaDocumentEntity> metaDocumentsDelMetaExpedient = metaDocumentRepository.findByMetaExpedient(expedient.getMetaExpedient());
			for (MetaDocumentEntity metaDocument: metaDocumentsDelMetaExpedient) {
				if (metaDocument.getMultiplicitat().equals(MultiplicitatEnumDto.M_1) || metaDocument.getMultiplicitat().equals(MultiplicitatEnumDto.M_1_N)) {
					boolean trobat = false;
					for (DocumentEntity document: documents) {
						if (document.getMetaDocument() != null && document.getMetaDocument().equals(metaDocument)) {
							trobat = true;
							break;
						}
					}
					if (!trobat)
						errors.add(
								crearValidacioError(
										metaDocument,
										metaDocument.getMultiplicitat()));
				}
			}
		}
		return errors;
	}
	@CacheEvict(value = "errorsValidacioNode", key = "#node.id")
	public void evictErrorsValidacioPerNode(
			NodeEntity node) {
	}

	@Cacheable(value = "usuariAmbCodi", key="#usuariCodi")
	public DadesUsuari findUsuariAmbCodi(
			String usuariCodi) {
		return pluginHelper.dadesUsuariFindAmbCodi(
				usuariCodi);
	}

	@CacheEvict(allEntries = true, value = "usuariAmbCodi", key="#usuariCodi")
	@Scheduled(fixedDelay = 86400000)
	public void evictUsuariAmbCodi() {
	}

	@Cacheable(value = "unitatsOrganitzatives", key="#entitatCodi")
	public ArbreDto<UnitatOrganitzativaDto> findUnitatsOrganitzativesPerEntitat(
			String entitatCodi) {
		EntitatEntity entitat = entitatRepository.findByCodi(entitatCodi);
		return pluginHelper.unitatsOrganitzativesFindArbreByPare(
				entitat.getUnitatArrel());
	}
	@CacheEvict(value = "unitatsOrganitzatives", key="#entitatCodi")
	public void evictUnitatsOrganitzativesPerEntitat(
			String entitatCodi) {
	}

	@Cacheable(value = "paisos")
	public List<PaisDto> findPaisos() {
		return conversioTipusHelper.convertirList(
				pluginHelper.dadesExternesPaisosFindAll(),
				PaisDto.class);
	}

	@Cacheable(value = "comunitats")
	public List<ComunitatDto> findComunitats() {
		return conversioTipusHelper.convertirList(
				pluginHelper.dadesExternesComunitatsFindAll(),
				ComunitatDto.class);
	}

	@Cacheable(value = "provincies")
	public List<ProvinciaDto> findProvincies() {
		return conversioTipusHelper.convertirList(
				pluginHelper.dadesExternesProvinciesFindAll(),
				ProvinciaDto.class);
	}

	@Cacheable(value = "provinciesPerComunitat", key="#comunitatCodi")
	public List<ProvinciaDto> findProvinciesPerComunitat(String comunitatCodi) {
		return conversioTipusHelper.convertirList(
				pluginHelper.dadesExternesProvinciesFindAmbComunitat(comunitatCodi),
				ProvinciaDto.class);
	}

	@Cacheable(value = "municipisPerProvincia", key="#provinciaCodi")
	public List<MunicipiDto> findMunicipisPerProvincia(String provinciaCodi) {
		return conversioTipusHelper.convertirList(
				pluginHelper.dadesExternesMunicipisFindAmbProvincia(provinciaCodi),
				MunicipiDto.class);
	}

	@Cacheable(value = "nivellAdministracio")
	public List<NivellAdministracioDto> findNivellAdministracio() {
		return pluginHelper.dadesExternesNivellsAdministracioAll();
	}

	@Cacheable(value = "tipusVia")
	public List<TipusViaDto	> findTipusVia() {
		return pluginHelper.dadesExternesTipusViaAll();
	}

	@Cacheable(value = "connexioDomini", key="#entitatCodi")
	public DataSource createDominiConnexio(
			String entitatCodi,
			Properties conProps) {
		DataSource dataSource = null;
		try {
			dataSource = new DriverManagerDataSource(
					conProps.getProperty("url"),
					conProps);
		} catch (Exception e) {
			throw new DominiException(
					"No s'ha pogut crear el datasource " + e.getMessage(),
					e.getCause());
		}
		return dataSource;
	}
	@CacheEvict(value = "connexioDomini", allEntries=true)
	public void evictCreateDominiConnexio() {
	}

	@Cacheable(value = "resultatConsultaDominis", key="#consulta")
	public List<ResultatDominiDto> findDominisByConsutla(
			JdbcTemplate jdbcTemplate,
			String consulta) {
		List<ResultatDominiDto> resultat = new ArrayList<ResultatDominiDto>();
		try {
			if (jdbcTemplate != null)
				resultat = jdbcTemplate.query(consulta, new DominiRowMapperHelper());
		} catch (Exception e) {
			throw new DominiException(
					"No s'ha pogut recuperar el resultat de consulta: " + e.getMessage(),
					e.getCause());
		}
		return resultat;
	}
	@CacheEvict(value = "resultatConsultaDominis", allEntries=true)
	public void evictFindDominisByConsutla() {
	}

	@Cacheable(value = "enviamentsPortafirmesAmbErrorPerExpedient", key="#expedient")
	public boolean hasEnviamentsPortafirmesAmbErrorPerExpedient(
			ExpedientEntity expedient) {
		boolean errorLastEnviament = false;
		for (ContingutEntity contingut : expedient.getFills()) {
			if (contingut instanceof DocumentEntity) {
				List<DocumentPortafirmesEntity> enviamentsPortafirmes = documentPortafirmesRepository.findByDocumentOrderByCreatedDateDesc(
						(DocumentEntity) contingut);
				//Si només hi ha un enviament amb error sortim del bucle
				if (enviamentsPortafirmes != null && enviamentsPortafirmes.size() > 0 && enviamentsPortafirmes.get(0).isError()) {
					errorLastEnviament = true;
					break;
				}
			}
		}
		return errorLastEnviament;
	}

	@CacheEvict(value = "enviamentsPortafirmesAmbErrorPerExpedient", key="#expedient")
	public void evictEnviamentsPortafirmesAmbErrorPerExpedient(ExpedientEntity expedient) {
	}

	@Cacheable(value = "notificacionsAmbErrorPerExpedient", key="#expedient")
	public boolean hasNotificacionsAmbErrorPerExpedient(
			ExpedientEntity expedient) {
		boolean errorLastNotificacio = false; //enviaments Portafirmes amb error
		for (ContingutEntity contingut : expedient.getFills()) {
			if (contingut instanceof DocumentEntity) {
				List<DocumentNotificacioEntity> notificacions = documentNotificacioRepository.findByDocumentOrderByCreatedDateDesc(
						(DocumentEntity) contingut);
				//Si només hi ha una notificació amb error sortim del bucle
				if (notificacions != null && notificacions.size() > 0 && notificacions.get(0).isError()) {
					errorLastNotificacio = true;
					break;
				}
			}
		}
		return errorLastNotificacio;
	}
		
	@CacheEvict(value = "notificacionsAmbErrorPerExpedient", key="#expedient")
	public void evictNotificacionsAmbErrorPerExpedient(ExpedientEntity expedient) { }
	
	@Cacheable(value = "enviamentsPortafirmesPendentsPerExpedient", key="#expedient")
	public boolean hasEnviamentsPortafirmesPendentsPerExpedient(
			ExpedientEntity expedient) {
		boolean hasEnviamentsPortafirmesPendents = false; //enviaments Portafirmes amb error
		for (ContingutEntity contingut : expedient.getFills()) {
			if (contingut instanceof DocumentEntity) {
				List<DocumentPortafirmesEntity> enviamentsPortafirmesPendents = documentPortafirmesRepository.findByDocumentAndEstatInAndErrorOrderByCreatedDateAsc(
						(DocumentEntity) contingut,
						new DocumentEnviamentEstatEnumDto[] {
								DocumentEnviamentEstatEnumDto.PENDENT,
								DocumentEnviamentEstatEnumDto.ENVIAT
						},
						false);
				//Si hi ha només un enviament pendent sortim del bucle
				if (enviamentsPortafirmesPendents != null && enviamentsPortafirmesPendents.size() > 0) {
					hasEnviamentsPortafirmesPendents = true;
					break;
				}
			}
		}
		return hasEnviamentsPortafirmesPendents;
	}

	@CacheEvict(value = "enviamentsPortafirmesPendentsPerExpedient", key="#expedient")
	public void evictEnviamentsPortafirmesPendentsPerExpedient(ExpedientEntity expedient) {
	}

	@Cacheable(value = "notificacionsPendentsPerExpedient", key="#expedient")
	public boolean hasNotificacionsPendentsPerExpedient(
			ExpedientEntity expedient) {
		boolean hasNotificacionsPendents = false;
		for (ContingutEntity contingut : expedient.getFills()) {
			if (contingut instanceof DocumentEntity) {

				List<DocumentNotificacioEntity> notificacionsPendents = documentNotificacioRepository.findByDocumentAndNotificacioEstatInAndErrorOrderByCreatedDateAsc(
						(DocumentEntity) contingut,
						new DocumentNotificacioEstatEnumDto[] {
								DocumentNotificacioEstatEnumDto.PENDENT, 
								DocumentNotificacioEstatEnumDto.ENVIADA, 
								DocumentNotificacioEstatEnumDto.REGISTRADA
						},
						false);
				//Si hi ha només una notificació pendent sortim del bucle
				if (notificacionsPendents != null && notificacionsPendents.size() > 0) {
					hasNotificacionsPendents = true;
					break;
				}
			}
		}
		return hasNotificacionsPendents;
	}
	
	@CacheEvict(value = "notificacionsPendentsPerExpedient", key="#expedient")
	public void evictNotificacionsPendentsPerExpedient(ExpedientEntity expedient) {
	}

	@Cacheable(value = "rolsDisponiblesEnAcls")
	public List<String> rolsDisponiblesEnAcls() {
		return aclSidRepository.findSidByPrincipalFalse();
	}
	@CacheEvict(value = "rolsDisponiblesEnAcls", allEntries = true)
	public void evictRolsDisponiblesEnAcls() {
	}

	private ValidacioErrorDto crearValidacioError(
			MetaDadaEntity metaDada,
			MultiplicitatEnumDto multiplicitat) {
		return new ValidacioErrorDto(
				conversioTipusHelper.convertir(
						metaDada,
						MetaDadaDto.class),
				MultiplicitatEnumDto.valueOf(multiplicitat.toString()));
	}
	private ValidacioErrorDto crearValidacioError(
			MetaDocumentEntity metaDocument,
			MultiplicitatEnumDto multiplicitat) {
		return new ValidacioErrorDto(
				conversioTipusHelper.convertir(
						metaDocument,
						MetaDocumentDto.class),
				MultiplicitatEnumDto.valueOf(multiplicitat.toString()));
	}

	private static final Logger logger = LoggerFactory.getLogger(CacheHelper.class);

}