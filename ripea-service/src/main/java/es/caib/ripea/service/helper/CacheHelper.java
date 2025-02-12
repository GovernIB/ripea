package es.caib.ripea.service.helper;

import es.caib.ripea.persistence.entity.*;
import es.caib.ripea.persistence.repository.*;
import es.caib.ripea.plugin.usuari.DadesUsuari;
import es.caib.ripea.service.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.exception.DominiException;
import es.caib.ripea.service.intf.utils.Utils;
import es.caib.ripea.service.permission.ExtendedPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.*;

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

	@Resource private EntitatRepository entitatRepository;
	@Resource private DadaRepository dadaRepository;
	@Resource private DocumentRepository documentRepository;
	@Resource private MetaDadaRepository metaDadaRepository;
	@Resource private MetaDocumentRepository metaDocumentRepository;
	@Resource private ConversioTipusHelper conversioTipusHelper;
	@Resource private PermisosHelper permisosHelper;
	@Resource private PermisosEntitatHelper permisosEntitatHelper;
	private PluginHelper pluginHelper;
	@Resource private UsuariRepository usuariRepository;
	@Resource private ExpedientTascaRepository expedientTascaRepository;
	@Resource private DocumentPortafirmesRepository documentPortafirmesRepository;
	@Resource private DocumentNotificacioRepository documentNotificacioRepository;
	@Autowired private AclSidRepository aclSidRepository;
	@Resource private ExpedientPeticioRepository expedientPeticioRepository;
	@Resource private EntityComprovarHelper entityComprovarHelper;
	@Resource private OrganGestorHelper organGestorHelper;
	@Resource private OrganGestorRepository organGestorRepository;
	@Resource private ExpedientPeticioHelper expedientPeticioHelper;
	@Autowired private ConfigHelper configHelper;
//	@Autowired private TascaHelper tascaHelper;

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
	
	@Cacheable(value = "findOrganismesEntitatAmbPermisDisseny", key="{#entitatId, #usuariCodi}")
	public List<OrganGestorDto> findOrganismesEntitatAmbPermisDisseny(Long entitatId, String usuariCodi) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false, false);
		return conversioTipusHelper.convertirList(
				organGestorHelper.findAmbEntitatPermis(entitat, ExtendedPermission.DISSENY),
				OrganGestorDto.class);
	}
	
	@CacheEvict(value = "findOrganismesEntitatAmbPermis", key="{#entitatId, #usuariCodi}")
	public void evictOrganismesEntitatAmbPermis(Long entitatId, String usuariCodi) {}
	@CacheEvict(value = "findOrganismesEntitatAmbPermis", allEntries = true)
	public void evictAllOrganismesEntitatAmbPermis() {}
	@CacheEvict(value = "findOrganismesEntitatAmbPermisDisseny", key="{#entitatId, #usuariCodi}")
	public void evictOrganismesEntitatAmbPermisDisseny(Long entitatId, String usuariCodi) {}

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
			List<DocumentEntity> documents = documentRepository.findByExpedientAndEsborrat(expedient, 0);
			
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
				if (hasNotificacionsSenseErrorNoCaducadesPendents(document)) {
					errors.add(
							crearValidacioError(
									null,
									null,
									ErrorsValidacioTipusEnumDto.NOTIFICACIONS));
					break;
				}
			}
			
			boolean isObligarInteressatActiu = configHelper.getAsBoolean("es.caib.ripea.permetre.obligar.interessat");
			MetaExpedientEntity procediment = expedient.getMetaExpedient();
			if (isObligarInteressatActiu && procediment.isInteressatObligatori()
					&& (expedient.getInteressatsORepresentants() == null
							|| expedient.getInteressatsORepresentants().isEmpty())) {
				errors.add(crearValidacioError(null, null, ErrorsValidacioTipusEnumDto.INTERESSATS));
			}
			
			//Validar les tasques del expedient
			/*List<ExpedientTascaEntity> tasquesExpedient = expedientTascaRepository.findByExpedient(expedient, null);
			if (tasquesExpedient!=null) {
				for (ExpedientTascaEntity tasca: tasquesExpedient) {
					if (!TascaEstatEnumDto.FINALITZADA.equals(tasca.getEstat()) &&
						!TascaEstatEnumDto.CANCELLADA.equals(tasca.getEstat())) {
						errors.add(crearValidacioError(null, null, ErrorsValidacioTipusEnumDto.TASQUES));
					} else {
						List<MetaExpedientTascaValidacioDto> validacions = tascaHelper.getValidacionsPendentsTasca(tasca.getId());
						if (validacions!=null && validacions.size()>0) {
							errors.add(crearValidacioError(null, null, ErrorsValidacioTipusEnumDto.TASQUES));
						}
					}
				}
			}*/
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

	@CacheEvict(allEntries = true, value = "usuariAmbCodi")
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

	@Cacheable(value = "organigrama", key="#entitatCodi")
	public Map<String, OrganismeDto> findOrganigramaByEntitat(String entitatCodi) {
		Map<String, OrganismeDto> organigrama = new HashMap<>();

		EntitatEntity entitat = entitatRepository.findByCodi(entitatCodi);
		List<OrganGestorEntity> organs = organGestorRepository.findByEntitatIdAndEstat(entitat.getId(), OrganEstatEnumDto.V);
		if (organs == null || organs.isEmpty()) {
			return organigrama;
		}
		OrganGestorEntity arrel = organGestorRepository.findByCodi(entitat.getUnitatArrel());
		if (arrel == null) {
			return organigrama;
		}
		Map<String, List<OrganGestorEntity>> organsMap = organsToMap(organs);
		organToOrganigrama(arrel, organsMap, organigrama);
		return organigrama;

	}

	@CacheEvict(value = "organigrama", key="#entitatcodi")
	public void evictFindOrganigramaByEntitat(String entitatcodi) {
	}

	private HashMap<String, List<OrganGestorEntity>> organsToMap(final List<OrganGestorEntity> organs) {

		HashMap<String, List<OrganGestorEntity>> organsMap = new HashMap<>();
		for (OrganGestorEntity organ: organs) {
			organsMap.put(organ.getCodi(), organ.getFills());
		}
		return organsMap;
	}

	private void organToOrganigrama(final OrganGestorEntity organ, final Map<String, List<OrganGestorEntity>> organsMap, Map<String, OrganismeDto> organigrama) {

		List<OrganGestorEntity> fills = organsMap.get(organ.getCodi());
		List<String> codisFills = null;
		if (fills != null && !fills.isEmpty()) {
			codisFills = new ArrayList<>();
			for (OrganGestorEntity fill: fills) {
				codisFills.add(fill.getCodi());
			}
		}
		OrganismeDto organisme = OrganismeDto.builder()
				.id(organ.getId())
				.codi(organ.getCodi())
				.nom(organ.getNom())
				.pare(organ.getPare() != null ? organ.getPare().getCodi() : null)
				.fills(codisFills)
				.estat(organ.getEstat()).build();
		organigrama.put(organ.getCodi(), organisme);
		if (fills == null) {
			return;
		}
		for (OrganGestorEntity fill : fills) {
			organToOrganigrama(fill, organsMap, organigrama);
		}
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
				DocumentNotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS,
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
	
	private boolean hasNotificacionsSenseErrorNoCaducadesPendents(DocumentEntity document) {
		List<DocumentNotificacioEntity> notificacionsPendents = documentNotificacioRepository.findByDocumentOrderByCreatedDateDesc(document);
		//No permetrem tancar l'expedient si té alguna notificacio:
		// - Que esta pendent
		// - Que no té error
		// - Que no esta caducada
		if (Utils.isNotEmpty(notificacionsPendents) &&
			!notificacionsPendents.get(0).isNotificacioFinalitzada() &&
			!notificacionsPendents.get(0).isError() &&
			!notificacionsPendents.get(0).isCaducada()) {
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
	
	
	

	@Cacheable(value = "anotacionsUsuari", key="{#usuariCodi}")
	public long countAnotacionsPendents(EntitatEntity entitat, String rolActual, String usuariCodi, Long organActualId) {
		logger.debug("Consulta anotacions pendents de processar per l'usuari " + usuariCodi);
		
		PermisosPerAnotacions permisosPerAnotacions = expedientPeticioHelper.findPermisosPerAnotacions(
				entitat.getId(),
				rolActual,
				organActualId);


		long numAnotacionsPendents = expedientPeticioRepository.countAnotacionsPendentsPerMetaExpedients(
				entitat,
				rolActual,
				permisosPerAnotacions.getProcedimentsPermesos(0),
				permisosPerAnotacions.getProcedimentsPermesos(1),
				permisosPerAnotacions.getProcedimentsPermesos(2),
				permisosPerAnotacions.getProcedimentsPermesos(3),
				permisosPerAnotacions.getAdminOrganCodisOrganAmbDescendents(0),
				permisosPerAnotacions.getAdminOrganCodisOrganAmbDescendents(1),
				permisosPerAnotacions.getAdminOrganCodisOrganAmbDescendents(2),
				permisosPerAnotacions.getAdminOrganCodisOrganAmbDescendents(3),
				permisosPerAnotacions.getIdsGrupsPermesos() == null,
				permisosPerAnotacions.getIdsGrupsPermesos());
		return numAnotacionsPendents;
	}

	private static <T> List<T> getList(List<List<T>> list, int index) {
		if (list == null) {
			throw new NullPointerException("La llista és nul·la.");
		}
		if (index < 0) {
			throw new IndexOutOfBoundsException("Index " + index + ". L'índex no pot ser negatiu.");
		}
		if (index > list.size()) {
			throw new IndexOutOfBoundsException("Index " + index + ". La llista només té " + list.size() + " elements.");
		}
		return Utils.getNullIfEmpty(list.get(index));
	}
	
	
	@CacheEvict(value = "anotacionsUsuari", key="{#usuariCodi}")
	public void evictCountAnotacionsPendents(String usuariCodi) {
		logger.debug("Buidant cache de número d'anotacions pendents per l'usuari " + usuariCodi);
	}

	@CacheEvict(value = "anotacionsUsuari", allEntries=true)
	public void evictAllCountAnotacionsPendents() {
		logger.debug("Buidant cache de número d'anotacions pendents per tots els usuaris ");
	}

	
	@Cacheable(value = "mostrarLogsIntegracio")
	public boolean mostrarLogsIntegracio() {
		String prop = configHelper.getConfig("es.caib.ripea.mostrar.logs.integracio");
		if (prop != null && prop.equals("true")) {
			return true;
		} else {
			return false;
		}
	}
	@CacheEvict(value = "mostrarLogsIntegracio")
	public void evictMostrarLogsIntegracio() {
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
		String prop = configHelper.getConfig("es.caib.ripea.mostrar.logs.segonpla");
		if (prop != null && prop.equals("true")) {
			return true;
		} else {
			return false;
		}
	}
	@CacheEvict(value = "mostrarLogsSegonPla")
	public void evictMostrarLogsSegonPla() {
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