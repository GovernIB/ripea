/**
 *
 */
package es.caib.ripea.core.helper;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.dto.ArbreDto;
import es.caib.ripea.core.api.dto.ComunitatDto;
import es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ErrorsValidacioTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.MultiplicitatEnumDto;
import es.caib.ripea.core.api.dto.MunicipiDto;
import es.caib.ripea.core.api.dto.NivellAdministracioDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.PaisDto;
import es.caib.ripea.core.api.dto.ProvinciaDto;
import es.caib.ripea.core.api.dto.ResultatConsultaDto;
import es.caib.ripea.core.api.dto.ResultatDominiDto;
import es.caib.ripea.core.api.dto.TipusViaDto;
import es.caib.ripea.core.api.dto.UnitatOrganitzativaDto;
import es.caib.ripea.core.api.dto.ValidacioErrorDto;
import es.caib.ripea.core.api.exception.DominiException;
import es.caib.ripea.core.api.utils.Utils;
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
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.ripea.core.repository.AclSidRepository;
import es.caib.ripea.core.repository.DadaRepository;
import es.caib.ripea.core.repository.DocumentNotificacioRepository;
import es.caib.ripea.core.repository.DocumentPortafirmesRepository;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.ExpedientPeticioRepository;
import es.caib.ripea.core.repository.ExpedientTascaRepository;
import es.caib.ripea.core.repository.MetaDadaRepository;
import es.caib.ripea.core.repository.MetaDocumentRepository;
import es.caib.ripea.core.repository.MetaExpedientRepository;
import es.caib.ripea.core.repository.OrganGestorRepository;
import es.caib.ripea.core.repository.UsuariRepository;
import es.caib.ripea.core.security.ExtendedPermission;
import es.caib.ripea.plugin.PropertiesHelper;
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
	private MetaExpedientRepository metaExpedientRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private ContingutHelper contenidorHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private PermisosEntitatHelper permisosEntitatHelper;
//	@Resource
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
	@Autowired
	private AclSidRepository aclSidRepository;
	@Resource
	private ExpedientPeticioRepository expedientPeticioRepository;
	@Resource
	private MetaExpedientHelper metaExpedientHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private OrganGestorHelper organGestorHelper;
	@Resource
	private OrganGestorRepository organGestorRepository;
	@Resource
	private ExpedientPeticioHelper expedientPeticioHelper;
	@Autowired
	private ConfigHelper configHelper;

	@Autowired
	public void setPluginHelper(PluginHelper pluginHelper) {
		this.pluginHelper = pluginHelper;
	}

	@Resource
	private MutableAclService aclService;

	
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
		List<Serializable> objectsIds = permisosHelper.getObjectsIdsWithPermission(
				OrganGestorEntity.class,
				ExtendedPermission.ADMINISTRATION);
		if (objectsIds != null && !objectsIds.isEmpty()) {
			List<Long> objectsIdsTypeLong = new ArrayList<Long>();
			for (Serializable oid: objectsIds) {
				objectsIdsTypeLong.add((Long)oid);
			}
			List<EntitatEntity> entitatsOfOrgans = entitatRepository.findByOrgansIds(objectsIdsTypeLong);
			entitats.addAll(entitatsOfOrgans);
			// remove duplicates
			entitats = new ArrayList<EntitatEntity>(new HashSet<EntitatEntity>(entitats));
		}
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

	@CacheEvict(value = "entitatsUsuari", allEntries=true)
	public void evictEntitatsAccessiblesAllUsuaris() {
	}
	
	
	@Cacheable(value = "findOrganismesEntitatAmbPermis", key="{#entitatId, #usuariCodi}")
	public List<OrganGestorDto> findOrganismesEntitatAmbPermis(Long entitatId, String usuariCodi) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false, false);
		return conversioTipusHelper.convertirList(
				organGestorHelper.findAmbEntitatPermis(
						entitat,
						ExtendedPermission.ADMINISTRATION),
				OrganGestorDto.class);
	}
	
	@CacheEvict(value = "findOrganismesEntitatAmbPermis", key="{#entitatId, #usuariCodi}")
	public void evictOrganismesEntitatAmbPermis(Long entitatId, String usuariCodi) {
	}
	@CacheEvict(value = "findOrganismesEntitatAmbPermis", allEntries = true)
	public void evictAllOrganismesEntitatAmbPermis() {
	}
	

	@Cacheable(value = "errorsValidacioNode", key = "#node.id")
	public List<ValidacioErrorDto> findErrorsValidacioPerNode(
			NodeEntity node) {
		logger.debug("Consulta dels errors de validació pel node (nodeId=" + node.getId() + ")");
		List<ValidacioErrorDto> errors = new ArrayList<ValidacioErrorDto>();
		List<DadaEntity> dades = dadaRepository.findByNode(node);
		// Valida dades específiques del meta-node
		List<MetaDadaEntity> metaDades = metaDadaRepository.findByMetaNodeAndActivaTrueAndMultiplicitatIn(node.getMetaNode(),
					new MultiplicitatEnumDto [] {
						MultiplicitatEnumDto.M_1,
						MultiplicitatEnumDto.M_1_N
					});
		for (MetaDadaEntity metaDada: metaDades) {
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
		if (node instanceof ExpedientEntity) {
			ExpedientEntity expedient = (ExpedientEntity)node;
			List<DocumentEntity> documents = documentRepository.findByExpedientAndEsborrat(
					expedient,
					0);
			
			// Valida documents específics del meta-node
			List<MetaDocumentEntity> metaDocumentsDelMetaExpedient = metaDocumentRepository.findByMetaExpedientAndMultiplicitatIn(
					expedient.getMetaExpedient(),
					new MultiplicitatEnumDto [] {
						MultiplicitatEnumDto.M_1,
						MultiplicitatEnumDto.M_1_N
					});
			for (MetaDocumentEntity metaDocument: metaDocumentsDelMetaExpedient) {
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
									metaDocument.getMultiplicitat(),
									ErrorsValidacioTipusEnumDto.MULTIPLICITAT));
			}
			for (DocumentEntity document : documents) {
				if (document.getMetaNode() == null) {
					errors.add(
							crearValidacioError(
									null,
									null,
									ErrorsValidacioTipusEnumDto.METADOCUMENT));
					break;
				}
			}
			
			for (DocumentEntity document : documents) {
				if (hasNotificacionsNoFinalitzadesINoPendentsAmbError(document)) {
					errors.add(
							crearValidacioError(
									null,
									null,
									ErrorsValidacioTipusEnumDto.NOTIFICACIONS));
					break;
				}
			}
		}
		if (!errors.isEmpty()) {
			// TODO: registrar a la base de dades
			// Aquesta operació és necessària per a generar els historics de forma eficient
			
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
	@Cacheable(value = "municipisPerProvinciaPinbal", key="#provinciaCodi")
	public List<MunicipiDto> findMunicipisPerProvinciaPinbal(String provinciaCodi) {
		return conversioTipusHelper.convertirList(
				pluginHelper.dadesExternesMunicipisFindAmbProvinciaPinbal(provinciaCodi),
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

	@Cacheable(value = "resultatConsultaDominis", key="#consulta.concat(#filtre).concat(#iniciCerca).concat(#finalCerca)")
	public ResultatDominiDto findDominisByConsutla(
			JdbcTemplate jdbcTemplate,
			String consulta,
			String filtre,
			int iniciCerca,
			int finalCerca) throws SQLException {
		ResultatDominiDto resultat = new ResultatDominiDto();
		Connection connection = jdbcTemplate.getDataSource().getConnection();
		try {
			if (connection == null)
				throw new DominiException("La hi ha cap connexió establerta amb el domini configurat.");
			if (connection.isClosed())
				throw new DominiException("La connexió amb el domini està tancada.");

   			boolean isMySql = connection.getMetaData().getDatabaseProductName().toLowerCase().contains("mysql");
			jdbcTemplate.setMaxRows(finalCerca);
			boolean compatible = true;
			
//   		### SI ÉS UNA BBDD MYSQL I HI HA FILTRE
   			if (isMySql && !filtre.isEmpty()) {
   				jdbcTemplate.setMaxRows(0);
   				consulta = "SELECT * FROM (" + consulta + ") RESULT WHERE LOWER(RESULT.VALOR) LIKE LOWER('%"+ filtre + "%')";
   			}
   			
//			### NOMÉS PAGINACIÓ SENSE FILTRE
   			if (isMySql && filtre.isEmpty()) {
   				consulta += " LIMIT " + iniciCerca + "," + finalCerca;
   			}
   			
//   		### SI ÉS UNA BBDD ORACLE I HI HA FILTRE
   			if (!isMySql && !filtre.isEmpty()) {
   				jdbcTemplate.setMaxRows(0);
   				consulta = "SELECT * FROM (" + consulta + ") WHERE LOWER(VALOR) LIKE LOWER('%"+ filtre + "%')";
   			}
   			
   			if (!isMySql && filtre.isEmpty()) {
//   			### ORACLE > 12C OR ORACLE < 12C
   				if (isOracleVersionGtOrEq12c(jdbcTemplate, connection)) {
					consulta += " OFFSET " + iniciCerca + " ROWS FETCH NEXT " + finalCerca + " ROWS ONLY";
				} else if (!isOracleVersionGtOrEq12c(jdbcTemplate, connection)) {
					StringBuilder consultaBuilder = new StringBuilder(consulta);
					consultaBuilder.insert(consulta.toLowerCase().indexOf("valor") + 5, ", ROWNUM AS RW ");
					consulta = "SELECT * FROM (" + consultaBuilder + ") WHERE RW  >= " + iniciCerca + " AND RW <= " + finalCerca;
				}
   			}
   			
   			if (!compatible)
   				throw new DominiException("La versió actual de la BBDD no és compatible amb la cerca de dominis. Consulti el seu administrador.");
   			
			if (jdbcTemplate != null) {
				List<ResultatConsultaDto> resultatConsulta = jdbcTemplate.query(consulta, new DominiRowMapperHelper());
				
				resultat.setTotalElements(resultatConsulta.size());
				resultat.setResultat(resultatConsulta);
			}
		} catch (Exception e) {
			throw new DominiException(
					"No s'ha pogut recuperar el resultat de consulta: " + e.getMessage(),
					e.getCause());
		} finally {
			try {
				connection.close();
			} catch (SQLException ex) {
				logger.error("Hi ha hagut un error tancant la connexió JDBC", ex);
			}
		}
		return resultat;
	}
	
	@Cacheable(value = "resultatConsultaDominis", key="#consulta.concat(#dadaValor)")
	public ResultatConsultaDto getValueSelectedDomini(
			JdbcTemplate jdbcTemplate,
			String consulta,
			String dadaValor) throws SQLException {
		List<ResultatConsultaDto> resultat = new ArrayList<ResultatConsultaDto>();
		Connection connection = jdbcTemplate.getDataSource().getConnection();
		try {
			if (connection == null)
				throw new DominiException("La hi ha cap connexió establerta amb el domini configurat.");
			if (connection.isClosed())
				throw new DominiException("La connexió amb el domini està tancada.");

   			boolean isMySql = connection.getMetaData().getDatabaseProductName().toLowerCase().contains("mysql");
			boolean compatible = true;
			
//   		### SI ÉS UNA BBDD MYSQL I HI HA FILTRE
   			if (isMySql && !dadaValor.isEmpty()) {
   				jdbcTemplate.setMaxRows(0);
   				consulta = "SELECT * FROM (" + consulta + ") RESULT WHERE RESULT.ID = '"+ dadaValor + "'";
   			}
   			
//   		### SI ÉS UNA BBDD ORACLE I HI HA FILTRE
   			if (!isMySql && !dadaValor.isEmpty()) {
   				jdbcTemplate.setMaxRows(0);
   				consulta = "SELECT * FROM (" + consulta + ") WHERE ID = '"+ dadaValor + "'";
   			}
   			
   			if (!compatible)
   				throw new DominiException("La versió actual de la BBDD no és compatible amb la cerca de dominis. Consulti el seu administrador.");
   			
			if (jdbcTemplate != null) {
				resultat = jdbcTemplate.query(consulta, new DominiRowMapperHelper());
			}
		} catch (Exception e) {
			throw new DominiException(
					"No s'ha pogut recuperar el resultat de consulta: " + e.getMessage(),
					e.getCause());
		} finally {
			try {
				connection.close();
			} catch (SQLException ex) {
				logger.error("Hi ha hagut un error tancant la connexió JDBC", ex);
			}
		}
		return !resultat.isEmpty() ? resultat.get(0) : new ResultatConsultaDto();
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
		List<DocumentEntity> documents = documentRepository.findByExpedientAndEsborrat(
				expedient,
				0);
		boolean hasNotificacionsPendents = false;
		for (ContingutEntity contingut : documents) {
			if (contingut instanceof DocumentEntity) {
				DocumentEntity document = (DocumentEntity) contingut;
				if (hasNotificacionsNoFinalitzades(document)) {
					hasNotificacionsPendents = true;
					break;
				}
			}
		}
		return hasNotificacionsPendents;
	}
	
	@Cacheable(value = "dataDarrerEnviament", key="#expedient")
	public Date getDataDarrerEnviament(
			ExpedientEntity expedient) {
		List<DocumentPortafirmesEntity> lastEnviamentPortafirmesPendent = documentPortafirmesRepository.findByExpedientAndEstatInAndErrorOrderByEnviatDataDesc(
				(ExpedientEntity) expedient,
				new DocumentEnviamentEstatEnumDto[] {
						DocumentEnviamentEstatEnumDto.PENDENT,
						DocumentEnviamentEstatEnumDto.ENVIAT
				},
				false);
		return (lastEnviamentPortafirmesPendent != null && !lastEnviamentPortafirmesPendent.isEmpty()) ? lastEnviamentPortafirmesPendent.get(0).getEnviatData() : null;
	}

	@CacheEvict(value = "dataDarrerEnviament", key="#expedient")
	public void evictDataDarrerEnviament(ExpedientEntity expedient) {
	}
	
	private boolean hasNotificacionsNoFinalitzades(DocumentEntity document) {
		List<DocumentNotificacioEstatEnumDto> estatsFinals = new ArrayList<DocumentNotificacioEstatEnumDto>(Arrays.asList(
				DocumentNotificacioEstatEnumDto.FINALITZADA, 
				DocumentNotificacioEstatEnumDto.PROCESSADA));
		List<DocumentNotificacioEntity> notificacionsPendents = documentNotificacioRepository.findByDocumentOrderByCreatedDateDesc(document);
		//Si la darrera notificació del document no està finalitzada
		if (notificacionsPendents != null && 
				notificacionsPendents.size() > 0 &&
				!estatsFinals.contains(notificacionsPendents.get(0).getNotificacioEstat())) {
			return true;
		}
		return false;
	}
	
	
	private boolean hasNotificacionsNoFinalitzadesINoPendentsAmbError(DocumentEntity document) {
		List<DocumentNotificacioEstatEnumDto> estatsFinals = new ArrayList<DocumentNotificacioEstatEnumDto>(Arrays.asList(
				DocumentNotificacioEstatEnumDto.FINALITZADA, 
				DocumentNotificacioEstatEnumDto.PROCESSADA));
		List<DocumentNotificacioEntity> notificacionsPendents = documentNotificacioRepository.findByDocumentOrderByCreatedDateDesc(document);
		//Si la darrera notificació del document no està finalitzada
		if (Utils.isNotEmpty(notificacionsPendents) && !estatsFinals.contains(notificacionsPendents.get(0).getNotificacioEstat()) && !notificacionsPendents.get(0).isError()) {
			return true;
		}
		return false;
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
	
	
	@Cacheable(value = "findRolsAmbCodi", key="#usuariCodi")
	public List<String> findRolsAmbCodi(String usuariCodi) {
		return pluginHelper.rolsUsuariFindAmbCodi(usuariCodi);
	}

	@CacheEvict(value = "findRolsAmbCodi", key="#usuariCodi")
	public void evictFindRolsAmbCodi(String usuariCodi) {
	}
	
	
	@Cacheable(value = "readAclById", key="#oid")
	public Acl readAclById(ObjectIdentity oid) {
		Acl acl = null;
		try {
			acl = aclService.readAclById(oid);
		} catch (NotFoundException e) {
		}
		return acl;
	}

	@CacheEvict(value = "readAclById", key="#oid")
	public void evictReadAclById(ObjectIdentity oid) {
	}
	
	
	

	@Cacheable(value = "anotacionsUsuari", key="{#entitat, #rolActual, #usuariCodi, #organActualId}")
	public long countAnotacionsPendents(EntitatEntity entitat, String rolActual, String usuariCodi, Long organActualId) {
		logger.debug("Consulta anotacions pendents de processar");
		
		PermisosPerAnotacions permisosPerAnotacions = expedientPeticioHelper.findPermisosPerAnotacions(
				entitat.getId(),
				rolActual,
				organActualId);

		return expedientPeticioRepository.countAnotacionsPendentsPerMetaExpedients(
				entitat,
				rolActual,
				permisosPerAnotacions.getProcedimentsPermesos(),
				permisosPerAnotacions.getAdminOrganCodisOrganAmbDescendents(),
				permisosPerAnotacions.isAdminOrganHasPermisAdminComu());
		
		
	}
	
	
	@CacheEvict(value = "anotacionsUsuari", key="{#entitat, #rolActual, #usuariCodi, #organActualId}", allEntries=true)
	public void evictCountAnotacionsPendents(EntitatEntity entitat) {
	}

	
	@Cacheable(value = "mostrarLogsIntegracio")
	public boolean mostrarLogsIntegracio() {
		String prop = PropertiesHelper.getProperties().getProperty("es.caib.ripea.mostrar.logs.integracio");
		if (prop != null && prop.equals("true")) {
			return true;
		} else {
			return false;
		}
	}
	
	@CacheEvict(value = "mostrarLogsEmail")
	public void evictMostrarLogsEmail() {
	}
	
	@Cacheable(value = "mostrarLogsEmail")
	public boolean mostrarLogsEmail() {
		
		String prop = configHelper.getConfig("es.caib.ripea.mostrar.logs.email");
		if (prop != null && prop.equals("true")) {
			return true;
		} else {
			return false;
		}
	}
	
	@CacheEvict(value = "mostrarLogsPermisos")
	public void evictMostrarLogsPermisos() {
	}
	
	@Cacheable(value = "mostrarLogsPermisos")
	public boolean mostrarLogsPermisos() {
		
		String prop = configHelper.getConfig("es.caib.ripea.activar.logs.permisos");
		if (prop != null && prop.equals("true")) {
			return true;
		} else {
			return false;
		}
	}
	
	@CacheEvict(value = "mostrarLogsGrups")
	public void evictMostrarLogsGrups() {
	}
	@Cacheable(value = "mostrarLogsGrups")
	public boolean mostrarLogsGrups() {
		
		String prop = configHelper.getConfig("es.caib.ripea.activar.logs.grups");
		if (prop != null && prop.equals("true")) {
			return true;
		} else {
			return false;
		}
	}
	
	@Cacheable(value = "mostrarLogsSegonPla")
	public boolean mostrarLogsSegonPla() {
		String prop = PropertiesHelper.getProperties().getProperty("es.caib.ripea.mostrar.logs.segonpla");
		if (prop != null && prop.equals("true")) {
			return true;
		} else {
			return false;
		}
	}
	
	@Cacheable(value = "mostrarLogsRendiment")
	public boolean mostrarLogsRendiment() {
		String prop = configHelper.getConfig("es.caib.ripea.mostrar.logs.rendiment");
		if (prop != null && prop.equals("true")) {
			return true;
		} else {
			return false;
		}
	}
	
	@CacheEvict(value = "mostrarLogsRendiment")
	public void evictMostrarLogsRendiment() {
	}
	
	@Cacheable(value = "mostrarLogsRendimentDescarregarAnotacio")
	public boolean mostrarLogsRendimentDescarregarAnotacio() {
		String prop = configHelper.getConfig("es.caib.ripea.mostrar.logs.rendiment.descarregar.anotacio");
		if (prop != null && prop.equals("true")) {
			return true;
		} else {
			return false;
		}
	}
	
	@CacheEvict(value = "mostrarLogsRendimentDescarregarAnotacio")
	public void evictMostrarLogsRendimentDescarregarAnotacio() {
	}
	
	@Cacheable(value = "mostrarLogsCercadorAnotacio")
	public boolean mostrarLogsCercadorAnotacio() {
		String prop = configHelper.getConfig("es.caib.ripea.mostrar.logs.cercador.anotacions");
		if (prop != null && prop.equals("true")) {
			return true;
		} else {
			return false;
		}
	}
	
	@CacheEvict(value = "mostrarLogsCercadorAnotacio")
	public void evictMostrarLogsCercadorAnotacio() {
	}
	
	
	@Cacheable(value = "mostrarLogsCreacioContingut")
	public boolean mostrarLogsCreacioContingut() {
		String prop = configHelper.getConfig("es.caib.ripea.mostrar.logs.creacio.contingut");
		if (prop != null && prop.equals("true")) {
			return true;
		} else {
			return false;
		}
	}
	
	@CacheEvict(value = "mostrarLogsCreacioContingut")
	public void evictMostrarLogsCreacioContingut() {
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
			MultiplicitatEnumDto multiplicitat,
			ErrorsValidacioTipusEnumDto tipus) {
		return new ValidacioErrorDto(
				multiplicitat != null ? conversioTipusHelper.convertir(
						metaDocument,
						MetaDocumentDto.class) : null,
				multiplicitat != null ? MultiplicitatEnumDto.valueOf(multiplicitat.toString()) : null,
				tipus);
		
	}
	
	private boolean isOracleVersionGtOrEq12c(JdbcTemplate jdbcTemplate, Connection connection) throws SQLException {
		DatabaseMetaData databaseMetaData = connection.getMetaData();
		int versionMajor = databaseMetaData.getDatabaseMajorVersion();
		int versionMinnor = databaseMetaData.getDatabaseMinorVersion();
		
		if (versionMajor < 12) {
			return false;
		} else if (versionMajor == 12) {
			if (versionMinnor >= 1) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(CacheHelper.class);

}