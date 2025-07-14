package es.caib.ripea.service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.GrupEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.entity.UsuariEntity;
import es.caib.ripea.persistence.repository.AclSidRepository;
import es.caib.ripea.persistence.repository.AlertaRepository;
import es.caib.ripea.persistence.repository.AvisRepository;
import es.caib.ripea.persistence.repository.ConsultaPinbalRepository;
import es.caib.ripea.persistence.repository.ContingutLogRepository;
import es.caib.ripea.persistence.repository.ContingutMovimentRepository;
import es.caib.ripea.persistence.repository.ContingutRepository;
import es.caib.ripea.persistence.repository.DadaRepository;
import es.caib.ripea.persistence.repository.DispositiuEnviamentRepository;
import es.caib.ripea.persistence.repository.DocumentEnviamentInteressatRepository;
import es.caib.ripea.persistence.repository.DominiRepository;
import es.caib.ripea.persistence.repository.EmailPendentEnviarRepository;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.persistence.repository.ExecucioMassivaContingutRepository;
import es.caib.ripea.persistence.repository.ExecucioMassivaRepository;
import es.caib.ripea.persistence.repository.ExpedientComentariRepository;
import es.caib.ripea.persistence.repository.ExpedientEstatRepository;
import es.caib.ripea.persistence.repository.ExpedientOrganPareRepository;
import es.caib.ripea.persistence.repository.ExpedientPeticioRepository;
import es.caib.ripea.persistence.repository.ExpedientRepository;
import es.caib.ripea.persistence.repository.ExpedientTascaComentariRepository;
import es.caib.ripea.persistence.repository.ExpedientTascaRepository;
import es.caib.ripea.persistence.repository.FluxFirmaUsuariRepository;
import es.caib.ripea.persistence.repository.GrupRepository;
import es.caib.ripea.persistence.repository.InteressatRepository;
import es.caib.ripea.persistence.repository.MetaDadaRepository;
import es.caib.ripea.persistence.repository.MetaExpedientCarpetaRepository;
import es.caib.ripea.persistence.repository.MetaExpedientComentariRepository;
import es.caib.ripea.persistence.repository.MetaExpedientOrganGestorRepository;
import es.caib.ripea.persistence.repository.MetaExpedientRepository;
import es.caib.ripea.persistence.repository.MetaExpedientSequenciaRepository;
import es.caib.ripea.persistence.repository.MetaExpedientTascaRepository;
import es.caib.ripea.persistence.repository.MetaExpedientTascaValidacioRepository;
import es.caib.ripea.persistence.repository.MetaNodeRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.persistence.repository.PinbalServeiRepository;
import es.caib.ripea.persistence.repository.PortafirmesBlockInfoRepository;
import es.caib.ripea.persistence.repository.PortafirmesBlockRepository;
import es.caib.ripea.persistence.repository.RegistreAnnexRepository;
import es.caib.ripea.persistence.repository.RegistreInteressatRepository;
import es.caib.ripea.persistence.repository.RegistreRepository;
import es.caib.ripea.persistence.repository.TipusDocumentalRepository;
import es.caib.ripea.persistence.repository.UsuariRepository;
import es.caib.ripea.persistence.repository.ViaFirmaUsuariRepository;
import es.caib.ripea.persistence.repository.config.ConfigRepository;
import es.caib.ripea.persistence.repository.historic.HistoricUsuariRepository;
import es.caib.ripea.plugin.usuari.DadesUsuari;
import es.caib.ripea.service.helper.CacheHelper;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.ConversioTipusHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.helper.IntegracioHelper;
import es.caib.ripea.service.helper.MetaExpedientHelper;
import es.caib.ripea.service.helper.PaginacioHelper;
import es.caib.ripea.service.helper.PinbalHelper;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.helper.RolHelper;
import es.caib.ripea.service.helper.UsuariHelper;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.DiagnosticFiltreDto;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.ExcepcioLogDto;
import es.caib.ripea.service.intf.dto.GenericDto;
import es.caib.ripea.service.intf.dto.IntegracioAccioDto;
import es.caib.ripea.service.intf.dto.IntegracioDto;
import es.caib.ripea.service.intf.dto.IntegracioFiltreDto;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.PortafirmesCarrecDto;
import es.caib.ripea.service.intf.dto.UsuariDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.utils.Utils;
import es.caib.ripea.service.permission.ExtendedPermission;

@Service
public class AplicacioServiceImpl implements AplicacioService {

	@Autowired private UsuariRepository usuariRepository;
	@Autowired private CacheHelper cacheHelper;
	@Autowired private PluginHelper pluginHelper;
	@Autowired private PinbalHelper pinbalHelper;	
	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Autowired private IntegracioHelper integracioHelper;
	@Autowired private ExcepcioLogHelper excepcioLogHelper;
	@Autowired private UsuariHelper usuariHelper;
	@Autowired private GrupRepository grupRepository;
	@Autowired private RolHelper rolHelper;
	@Autowired private ConfigHelper configHelper;
	@Autowired private PaginacioHelper paginacioHelper;
	@Autowired private MetaExpedientRepository metaExpedientRepository;
	@Autowired private MetaExpedientHelper metaExpedientHelper;
    @Autowired private EntitatRepository entitatRepository;
    @Autowired private OrganGestorRepository organGestorRepository;

    //UpdateUsuaris
    @Autowired private ExpedientRepository expedientRepository;
    @Autowired private HistoricUsuariRepository historicUsuariRepository;
    @Autowired private ConfigRepository configRepository;
    
    @Autowired private AlertaRepository alertaRepository;
    @Autowired private AvisRepository avisRepository;
    @Autowired private ConsultaPinbalRepository consultaPinbalRepository;
    @Autowired private ContingutRepository contingutRepository; //IPA_CONTINGUT i IPA_CONT_COMMENT
    @Autowired private ContingutLogRepository contingutLogRepository;
    @Autowired private ContingutMovimentRepository contingutMovimentRepository;
    @Autowired private DadaRepository dadaRepository;
    @Autowired private DocumentEnviamentInteressatRepository documentEnviamentInteressatRepository; //IPA_DOCUMENT_ENVIAMENT i IPA_DOCUMENT_ENVIAMENT_INTER
    @Autowired private DispositiuEnviamentRepository dispositiuEnviamentRepository;
    @Autowired private DominiRepository dominiRepository;
    @Autowired private EmailPendentEnviarRepository emailPendentEnviarRepository;
    @Autowired private ExecucioMassivaRepository execucioMassivaRepository;
    @Autowired private ExpedientComentariRepository expedientComentariRepository;
    @Autowired private ExpedientEstatRepository expedientEstatRepository; //IPA_EXPEDIENT_ESTAT i IPA_EXPEDIENT_FILTRE
    @Autowired private ExpedientOrganPareRepository expedientOrganPareRepository;
    @Autowired private ExpedientPeticioRepository expedientPeticioRepository;
    @Autowired private ExpedientTascaRepository expedientTascaRepository; 
    @Autowired private ExpedientTascaComentariRepository expedientTascaComentariRepository;
    @Autowired private FluxFirmaUsuariRepository fluxFirmaUsuariRepository;
    @Autowired private InteressatRepository interessatRepository;
    @Autowired private ExecucioMassivaContingutRepository execucioMassivaContingutRepository;
    @Autowired private MetaDadaRepository metaDadaRepository;
    @Autowired private MetaExpedientComentariRepository metaExpedientComentariRepository; //IPA_METAEXP_COMMENT i IPA_METAEXP_DOMINI
    @Autowired private MetaExpedientCarpetaRepository metaExpedientCarpetaRepository; //IPA_METAEXPEDIENT_CARPETA i IPA_METAEXPEDIENT_METADOCUMENT
    @Autowired private MetaExpedientOrganGestorRepository metaExpedientOrganGestorRepository;
    @Autowired private MetaExpedientSequenciaRepository metaExpedientSequenciaRepository;
    @Autowired private MetaExpedientTascaRepository metaExpedientTascaRepository;
    @Autowired private MetaExpedientTascaValidacioRepository metaExpedientTascaValidacioRepository;
    @Autowired private MetaNodeRepository metaNodeRepository; //IPA_METANODE i IPA_METANODE_METADADA
    @Autowired private PinbalServeiRepository pinbalServeiRepository;
    @Autowired private PortafirmesBlockRepository portafirmesBlockRepository;
    @Autowired private PortafirmesBlockInfoRepository portafirmesBlockInfoRepository;
    @Autowired private RegistreRepository registreRepository;
    @Autowired private RegistreAnnexRepository registreAnnexRepository;
    @Autowired private RegistreInteressatRepository registreInteressatRepository;
    @Autowired private TipusDocumentalRepository tipusDocumentalRepository;
    @Autowired private ViaFirmaUsuariRepository viaFirmaUsuariRepository;
    @Autowired private AclSidRepository aclSidRepository;
    @Autowired private AclCache aclCache;

	@Override
	public void actualitzarEntitatThreadLocal(EntitatDto entitat) {
		ConfigHelper.setEntitat(entitat);
	}
	
	@Override
	public void actualitzarRolThreadLocal(String rol) {
		ConfigHelper.setRol(rol);
	}
	
	@Override
	public void actualitzarOrganCodi(String organCodi) {
		ConfigHelper.setOrganCodi(organCodi);
	}

	@Override
    @Transactional(readOnly = true)
    public String getEntitatActualCodi() {
        return configHelper.getEntitatActualCodi();
    }
	
	@Override
    @Transactional(readOnly = true)
    public String getOrganActualCodi() {
        return configHelper.getOrganActualCodi();
    }
	
	@Override
	public Long getEntitatActualId() {
		if (configHelper.getEntitatActualCodi()!=null) {
			EntitatEntity ee = entitatRepository.findByCodi(getEntitatActualCodi());
			return ee==null?null:ee.getId();
		} else {
			return null;
		}
	}

	@Override
	public String getRolActualCodi() {
		return configHelper.getRolActual();
	}

	@Override
	public Long getOrganActualId() {
		Long entitatActualId = getEntitatActualId();
		if (configHelper.getOrganActualCodi()!=null && entitatActualId!=null) {
			OrganGestorEntity oge = organGestorRepository.findByEntitatIdAndCodi(entitatActualId, configHelper.getOrganActualCodi());
			return oge==null?null:oge.getId();
		} else {
			return null;
		}
	}
	
	@Transactional
	@Override
	public void processarAutenticacioUsuari(boolean comprovaAmbUsuariPlugin) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Processant autenticació (usuariCodi=" + auth.getName() + ")");
		UsuariEntity usuari = usuariRepository.findById(auth.getName()).orElse(null);

		logger.debug("Consultant plugin de dades d'usuari (usuariCodi=" + auth.getName() + ")");		
		DadesUsuari dadesUsuari = cacheHelper.findUsuariAmbCodi(auth.getName());
		
		if (dadesUsuari != null) {
			logger.debug("Dades usuari:");
			logger.debug("Dades usuari getCodi: "+dadesUsuari.getCodi());
			logger.debug("Dades usuari getNom: "+dadesUsuari.getNom());
			logger.debug("Dades usuari getLlinatges: "+dadesUsuari.getLlinatges());
			logger.debug("Dades usuari getNomSencer: "+dadesUsuari.getNomSencer());
			logger.debug("Dades usuari getNif: "+dadesUsuari.getNif());
			logger.debug("Dades usuari getEmail: "+dadesUsuari.getEmail());
		} else {
			logger.debug("Dades usuari es null.");
		}
		
		if (usuari == null) {
			if (dadesUsuari != null) {
				logger.debug("GUARDAM NOU usuari a BBDD amb les dades de dadesUsuari.");
				logger.debug("Dades usuari getCodi: "+dadesUsuari.getCodi());
				logger.debug("Dades usuari getNomSencer: "+dadesUsuari.getNomSencer());
				logger.debug("Dades usuari getNif: "+dadesUsuari.getNif());
				logger.debug("Dades usuari getEmail: "+dadesUsuari.getEmail());
				usuari = usuariRepository.save(
						UsuariEntity.getBuilder(
								dadesUsuari.getCodi(),
								dadesUsuari.getNomSencer(),
								dadesUsuari.getNif(),
								dadesUsuari.getEmail(),
								getIdiomaPerDefecte()).build());
			} else {
				if (comprovaAmbUsuariPlugin) {
					throw new NotFoundException(auth.getName(), DadesUsuari.class);
				} else {
					logger.debug("GUARDAM NOU usuari a BBDD amb les dades de auth.");
					logger.debug("auth.getName(): "+auth.getName());
					usuari = usuariRepository.save(
							UsuariEntity.getBuilder(
									auth.getName(),
									auth.getName(),
									null,
									null,
									getIdiomaPerDefecte()).build());
				}
			}
		} else {
			if (dadesUsuari != null) {
				logger.debug("ACTUALITZAM USUARI "+usuari.getCodi()+" a BBDD amb les dades de dadesUsuari.");
				logger.debug("Dades usuari getNomSencer: "+dadesUsuari.getNomSencer());
				logger.debug("Dades usuari getNif: "+dadesUsuari.getNif());
				logger.debug("Dades usuari getEmail: "+dadesUsuari.getEmail());
				usuari.update(
						dadesUsuari.getNomSencer(),
						dadesUsuari.getNif(),
						dadesUsuari.getEmail());
			} else {
				if (comprovaAmbUsuariPlugin) {
					throw new NotFoundException(auth.getName(), DadesUsuari.class);
				}
			}
		}
	}

	@Transactional(readOnly = true)
	@Override
	public UsuariDto getUsuariActual() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Obtenint usuari actual");
		return toUsuariDtoAmbRols(
				usuariRepository.getOne(auth.getName()));
	}
	
	@Transactional
	@Override
	public void setRolUsuariActual(String rolActual) {
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			logger.debug("Actualitzant rol de usuari actual");
			UsuariEntity usuari = usuariRepository.getOne(auth.getName());
			usuari.updateRolActual(rolActual);
			cacheHelper.evictCountAnotacionsPendents(usuari.getCodi());
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(ex.getMessage());
		}
	}
	
	@Transactional
	@Override
	public UsuariDto updateUsuariActual(UsuariDto dto) {
		logger.debug("Actualitzant configuració de usuari actual");
		UsuariEntity usuari = usuariRepository.getOne(dto.getCodi());
		
		usuari.update(
				dto.getEmailAlternatiu(),
				dto.getIdioma(),
				dto.isRebreEmailsAgrupats(),
				dto.isRebreAvisosNovesAnotacions(),
				dto.isRebreEmailsCanviEstatRevisio(),
				dto.getNumElementsPagina(),
				dto.isExpedientListDataDarrerEnviament(),
				dto.isExpedientListAgafatPer(),
				dto.isExpedientListInteressats(),
				dto.isExpedientListComentaris(),
				dto.isExpedientListGrup(),
				dto.getProcedimentId() != null ? metaExpedientRepository.getOne(dto.getProcedimentId()) : null,
				dto.getVistaActual(), 
				dto.isExpedientExpandit(),
				dto.getEntitatPerDefecteId() != null ? entitatRepository.getOne(dto.getEntitatPerDefecteId()) : null,
				dto.getVistaMoureActual());
		
		return toUsuariDtoAmbRols(usuari);
	}

	@Transactional(readOnly = true)
	@Override
	public UsuariDto findUsuariAmbCodi(String codi) {
		logger.debug("Obtenint usuari amb codi (codi=" + codi + ")");
		return conversioTipusHelper.convertir(usuariRepository.findById(codi).get(), UsuariDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<UsuariDto> findUsuariAmbText(String text) {
		logger.debug("Consultant usuaris amb text (text=" + text + ")");
		return usuariHelper.findUsuariAmbText(text);
	}
	
	@Transactional(readOnly = true)
	@Override
	public UsuariDto findUsuariCarrecAmbCodiDades(String nifOrCarrec) {
		logger.debug("Obtenint usuari/càrrec amb codi (codi=" + nifOrCarrec + ")");
		UsuariDto usuariDto = null;
		try {
			//Cercar primer a BBDD, sino al plugin, sempre per NIF, y sense guardarlo a BBDD RIPEA.
			usuariDto = usuariHelper.getUsuariResponsableByNif(nifOrCarrec);
		} catch (NotFoundException ex) {
			if (configHelper.getAsBoolean(PropertyConfig.PORTAFIB_PLUGIN_USUARISPF_WS)) {
				logger.error("No s'ha trobat cap usuari amb el codi " + nifOrCarrec + ". Procedim a cercar si és un càrrec.");
				usuariDto = new UsuariDto();
				PortafirmesCarrecDto carrec = pluginHelper.portafirmesRecuperarCarrec(nifOrCarrec);
				
				if (carrec != null) {
					String nom = carrec.getCarrecName();
				    if (!Utils.isBlank(carrec.getUsuariPersonaNom())) {
				        nom += " - " + carrec.getUsuariPersonaNom();
				    }
					usuariDto.setCodi(carrec.getCarrecId());
					usuariDto.setNom(nom);
					usuariDto.setNif(carrec.getUsuariPersonaNif());
				} else {
					throw new NotFoundException(
							nifOrCarrec,
							DadesUsuari.class);
				}
			} else {
				usuariDto = new UsuariDto();
				usuariDto.setCodi(nifOrCarrec);
				usuariDto.setNif(nifOrCarrec);
				usuariDto.setNom("Usuari o càrrec NO trobat");
			}
		}
		return usuariDto;
	}
	
	@Transactional(readOnly = true)
	@Override
	public UsuariDto findUsuariAmbCodiDades(String codi) {
		logger.debug("Obtenint usuari amb codi (codi=" + codi + ")");
		//Cercar primer a BBDD, sino al plugin, sempre per NIF, y sense guardarlo a BBDD RIPEA.
		UsuariDto usuariDto = usuariHelper.getUsuariResponsableByNif(codi);
		return usuariDto;
	}

	@Transactional(readOnly = true)
	@Override
	public List<UsuariDto> findUsuariAmbTextDades(String text) {
		logger.debug("Consultant usuaris amb text (text=" + text + ")");
		return conversioTipusHelper.convertirList(
				pluginHelper.findAmbFiltre(text),
				UsuariDto.class);
	}

	@Override
	public List<IntegracioDto> integracioFindAll() {
		logger.debug("Consultant les integracions");
		return integracioHelper.findAll();
	}

	@Override
	public List<IntegracioAccioDto> integracioFindDarreresAccionsByCodi(String codi) {
		logger.debug("Consultant les darreres accions per a la integració (codi=" + codi + ")");
		return integracioHelper.findAccionsByIntegracioCodi(codi, null);
	}

	@Override
	public PaginaDto<IntegracioAccioDto> integracioFindDarreresAccionsByCodiPaginat(String codi, PaginacioParamsDto params, IntegracioFiltreDto filtre) {

		logger.debug("Consultant les darreres accions per a la integració (codi=" + codi + ")");
		List<IntegracioAccioDto> accions = integracioHelper.findAccionsByIntegracioCodi(codi, filtre);
		if (accions == null || accions.isEmpty()) {
			return new PaginaDto<>();
		}

		List<List<IntegracioAccioDto>> pagines = paginacioHelper.getPages(accions, params.getPaginaTamany());
		PaginaDto<IntegracioAccioDto> pagina = paginacioHelper.toPaginaDto(pagines.get(params.getPaginaNum()), null);
		pagina.setContingut(pagines.get(params.getPaginaNum()));
		return paginacioHelper.prepararPagina(pagina, pagines, accions);
	}

	@Override
	public void excepcioSave(String uri, Throwable exception) {
		try {
			logger.debug("Emmagatzemant excepció (exception=" + exception + ")");
			excepcioLogHelper.addExcepcio(uri, exception);
		} catch (Exception e) {
			logger.error("Error al guardar excepció al monitor d'excepcions.", e);
			logger.error("Excepció no guardada:", exception);
		}
	}

	@Override
	public ExcepcioLogDto excepcioFindOne(Long index) {
		logger.debug("Consulta d'una excepció (index=" + index + ")");
		return excepcioLogHelper.findAll().get(index.intValue());
	}

	@Override
	public List<ExcepcioLogDto> excepcioFindAll() {
		logger.debug("Consulta de les excepcions disponibles");
		return excepcioLogHelper.findAll();
	}

	@Override
	public List<String> permisosFindRolsDistinctAll() {
		logger.debug("Consulta dels rols definits a les ACLs");
		List<String> rolsAcls = cacheHelper.rolsDisponiblesEnAcls();
		
		
		List<GrupEntity> grups = grupRepository.findAll();
		List<String> grupRols = new ArrayList<>();
		if (grups != null) {
			for (GrupEntity grup : grups) {
				grupRols.add(grup.getCodi());
			}
		}
		rolsAcls.addAll(grupRols);
		return rolsAcls;
	}
	@Override
	public void evictRolsDisponiblesEnAcls() {
		logger.debug("Evict rols disponibles en ACLs");
		cacheHelper.evictRolsDisponiblesEnAcls();
	}
	
	@Override
	public void evictRolsPerUsuari(String usuariCodi) {
		logger.debug("Evict rols per usuari");
		cacheHelper.evictFindRolsAmbCodi(usuariCodi);
	}

	@Override
	public void evictCountAnotacionsPendents(String usuariCodi) {
		logger.debug("Evict count anotacions per usuari");
		cacheHelper.evictCountAnotacionsPendents( usuariCodi);
	}

	@Override
	public String propertyBaseUrl() {
		logger.debug("Consulta de la propietat base URL");
		return configHelper.getConfig(PropertyConfig.BASE_URL);
	}

	@Override
	public Properties propertiesFindByGroup(String codiGrup) {
		logger.debug("Consulta del valor de les properties d'un grup (" +
				"codi grup=" + codiGrup + ")");
		return configHelper.getPropertiesByGroup(codiGrup);
	}

	@Override
	public String propertyFindByNom(String nom) {
		logger.debug("Consulta del valor del propertat amb nom");
		return configHelper.getConfig(nom);
	}
	
	@Override
	public Boolean propertyBooleanFindByKey(String key) {
		logger.debug("Consulta del valor del propietat boolea amb key");
		return configHelper.getAsBoolean(key);
	}
	
	@Override
	@Deprecated
	public boolean propertyBooleanFindByKey(String key, boolean defaultValueIfNull) {
		logger.debug("Consulta del valor del propietat boolea amb key");
		return configHelper.getAsBoolean(key);
	}
	
	
	@Override
	public boolean mostrarLogsRendiment() {
		return cacheHelper.mostrarLogsRendiment();
	}
	
	@Override
	public boolean mostrarLogsCercadorAnotacio() {
		return cacheHelper.mostrarLogsCercadorAnotacio();
	}
	
	@Override
	public boolean getBooleanJbossProperty(
			String key,
			boolean defaultValueIfNull) {
		String property = configHelper.getEnvironmentProperty(key, null);
		if (property != null) {
			return Boolean.parseBoolean(property);
		} else {
			return defaultValueIfNull;
		}
	}
	
	@Transactional(readOnly = true)
	@Override
	public Long getProcedimentPerDefecte(Long entitatId, String rolActual) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UsuariEntity usuari = usuariRepository.getOne(auth.getName());
		
		Long procId = null;
		if (usuari.getProcediment() != null) {
			List<MetaExpedientEntity> metaExpedientsEnt = metaExpedientHelper.findAmbPermis(
					entitatId,
					ExtendedPermission.READ,
					true,
					null, 
					"IPA_ADMIN".equals(rolActual),
					"IPA_ORGAN_ADMIN".equals(rolActual),
					null, 
					false); 
			
			for (MetaExpedientEntity metaExpedientEntity : metaExpedientsEnt) {
				if (metaExpedientEntity.getId().equals(usuari.getProcediment().getId())) {
					procId = metaExpedientEntity.getId();
				}
			}
		}
		
		return procId;
	}
	
	private UsuariDto toUsuariDtoAmbRols(
			UsuariEntity usuari) {
		UsuariDto dto = conversioTipusHelper.convertir(
				usuari,
				UsuariDto.class);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth.getAuthorities() != null) {
			String[] rols = new String[auth.getAuthorities().size()];
			int index = 0;
			for (GrantedAuthority grantedAuthority: auth.getAuthorities()) {
				rols[index++] = grantedAuthority.getAuthority();
			}
			dto.setRols(rols);
		}
		dto.setProcedimentId(usuari.getProcediment() != null ? usuari.getProcediment().getId() : null);
		dto.setEntitatPerDefecteId(usuari.getEntitatPerDefecte() != null ? usuari.getEntitatPerDefecte().getId() : null);
		return dto;
	}
	
	@Override
	public boolean doesCurrentUserHasRol(
			String rolToCheck) {

		return rolHelper.doesCurrentUserHasRol(rolToCheck);
	}

	private String getIdiomaPerDefecte() {
		return configHelper.getConfig(PropertyConfig.IDIOMA_DEFECTE);
	}
	
	@Override
	public List<String> findUsuarisCodisAmbRol(String rol) {
		List<DadesUsuari> dadesUsuaris = pluginHelper.dadesUsuariFindAmbGrup(rol);
		List<String> codisUsuaris = new ArrayList<>();
		for (DadesUsuari dadesUsuari : dadesUsuaris) {
			codisUsuaris.add(dadesUsuari.getCodi());
		}
		return codisUsuaris;
	}
	
	
    @Override
    @Transactional
	public String getValueForOrgan(
			String entitatCodi,
			String organCodi,
			String keyGeneral) {
		return configHelper.getValueForOrgan(
				entitatCodi,
				organCodi,
				keyGeneral);
	}
    
    @Override
    @Transactional(readOnly = true)
    public Properties getAllPropertiesOrganOrEntitatOrGeneral(String entitatCodi, String organCodi) {
        return configHelper.getAllPropertiesOrganOrEntitatOrGeneral(entitatCodi, organCodi);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Properties getAllPropertiesEntitatOrGeneral(String entitatCodi) {
        return configHelper.getAllPropertiesEntitatOrGeneral(entitatCodi);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Properties getGroupPropertiesEntitatOrGeneral(String groupCode, String entitatCodi) {
        return configHelper.getGroupPropertiesEntitatOrGeneral(groupCode, entitatCodi);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Properties getGroupPropertiesOrganOrEntitatOrGeneral(String groupCode, String entitatCodi, String organCodi) {
    	 return configHelper.getGroupPropertiesOrganOrEntitatOrGeneral(groupCode, entitatCodi, organCodi);
    }
	
	@Override
	@Transactional(readOnly = true)
	public GenericDto integracioDiagnostic(String codi, DiagnosticFiltreDto filtre) {
		if (codi!=null) {
			if (codi.equals(IntegracioHelper.INTCODI_PFIRMA)) {
				String resultatDiagnostic = pluginHelper.portafirmesDiagnostic(filtre);
				if (resultatDiagnostic==null) {
					return new GenericDto("integracio.diag.pf.ok", "fa fa-check verd", new Object[] {codi});
				} else {
					return new GenericDto("integracio.diag.pf.ko", "fa fa-times vermell", new Object[] {resultatDiagnostic});
				}
			} else if (codi.equals(IntegracioHelper.INTCODI_FIRMASIMPLE)) {
				String resultatDiagnostic = pluginHelper.firmaSimpleDiagnostic(filtre);
				if (resultatDiagnostic==null) {
					return new GenericDto("integracio.diag.fs.ok", "fa fa-check verd", new Object[] {codi});
				} else {
					return new GenericDto("integracio.diag.fs.ko", "fa fa-times vermell", new Object[] {resultatDiagnostic});
				}
			} else if (codi.equals(IntegracioHelper.INTCODI_FIRMASERV)) {
				String resultatDiagnostic = pluginHelper.firmaServidorDiagnostic(filtre);
				if (resultatDiagnostic==null) {
					return new GenericDto("integracio.diag.fserv.ok", "fa fa-check verd", new Object[] {codi});
				} else {
					return new GenericDto("integracio.diag.fserv.ko", "fa fa-times vermell", new Object[] {resultatDiagnostic});
				}
			} else if (codi.equals(IntegracioHelper.INTCODI_CALLBACK)) {
				return new GenericDto("integracio.diag.cb.info", "fa fa-info-circle blau", new Object[] {codi});
			} else if (codi.equals(IntegracioHelper.INTCODI_ARXIU)) {
				String resultatDiagnostic = pluginHelper.arxiuDiagnostic(filtre);
				if (resultatDiagnostic==null) {
					return new GenericDto("integracio.diag.ax.ok", "fa fa-check verd", new Object[] {codi});
				} else {
					return new GenericDto("integracio.diag.ax.ko", "fa fa-times vermell", new Object[] {resultatDiagnostic});
				}
			} else if (codi.equals(IntegracioHelper.INTCODI_GESDOC)) {
				String resultatDiagnostic = pluginHelper.gestorDocumentalDiagnostic(filtre);
				if (resultatDiagnostic==null) {
					return new GenericDto("integracio.diag.gd.ok", "fa fa-check verd", new Object[] {codi});
				} else {
					return new GenericDto("integracio.diag.gd.ko", "fa fa-times vermell", new Object[] {resultatDiagnostic});
				}
			} else if (codi.equals(IntegracioHelper.INTCODI_PINBAL)) {
				String resultatDiagnostic = pinbalHelper.pinbalDiagnostic(filtre);
				if (resultatDiagnostic==null) {
					return new GenericDto("integracio.diag.pin.ok", "fa fa-check verd", new Object[] {codi});
				} else {
					return new GenericDto("integracio.diag.pin.ko", "fa fa-times vermell", new Object[] {resultatDiagnostic});
				}
			} else if (codi.equals(IntegracioHelper.INTCODI_USUARIS)) {
				String resultatDiagnostic = pluginHelper.dadesUsuariDiagnostic(filtre);
				if (resultatDiagnostic==null) {
					return new GenericDto("integracio.diag.us.ok", "fa fa-check verd", new Object[] {codi});
				} else {
					return new GenericDto("integracio.diag.us.ko", "fa fa-times vermell", new Object[] {resultatDiagnostic});
				}
			} else if (codi.equals(IntegracioHelper.INTCODI_CONVERT)) {
				String resultatDiagnostic = pluginHelper.conversioDocumentsDiagnostic(filtre);
				if (resultatDiagnostic==null) {
					return new GenericDto("integracio.diag.conv.ok", "fa fa-check verd", new Object[] {codi});
				} else {
					return new GenericDto("integracio.diag.conv.ko", "fa fa-times vermell", new Object[] {resultatDiagnostic});
				}
			} else if (codi.equals(IntegracioHelper.INTCODI_DADESEXT)) {
				String resultatDiagnostic = pluginHelper.dadesExternesDiagnostic(filtre);
				if (resultatDiagnostic==null) {
					return new GenericDto("integracio.diag.de.ok", "fa fa-check verd", new Object[] {codi});
				} else {
					return new GenericDto("integracio.diag.de.ko", "fa fa-times vermell", new Object[] {resultatDiagnostic});
				}
			} else if (codi.equals(IntegracioHelper.INTCODI_DISTRIBUCIO)) {
				String resultatDiagnostic = pluginHelper.distribucioDiagnostic(filtre);
				if (resultatDiagnostic==null) {
					return new GenericDto("integracio.diag.dist.ok", "fa fa-check verd", new Object[] {codi});
				} else {
					return new GenericDto("integracio.diag.dist.ko", "fa fa-times vermell", new Object[] {resultatDiagnostic});
				}				
			} else if (codi.equals(IntegracioHelper.INTCODI_NOTIFICACIO)) {
				String resultatDiagnostic = pluginHelper.notibDiagnostic(filtre);
				if (resultatDiagnostic==null) {
					return new GenericDto("integracio.diag.notib.ok", "fa fa-check verd", new Object[] {codi});
				} else {
					return new GenericDto("integracio.diag.notib.ko", "fa fa-times vermell", new Object[] {resultatDiagnostic});
				}
			} else if (codi.equals(IntegracioHelper.INTCODI_VIAFIRMA)) {
				String resultatDiagnostic = pluginHelper.viaFirmaDiagnostic(filtre);
				if (resultatDiagnostic==null) {
					return new GenericDto("integracio.diag.via.ok", "fa fa-check verd", new Object[] {codi});
				} else {
					return new GenericDto("integracio.diag.via.ko", "fa fa-times vermell", new Object[] {resultatDiagnostic});
				}
			} else if (codi.equals(IntegracioHelper.INTCODI_DIGITALITZACIO)) {
				String resultatDiagnostic = pluginHelper.digitalitzacioDiagnostic(filtre);
				if (resultatDiagnostic==null) {
					return new GenericDto("integracio.diag.digi.ok", "fa fa-check verd", new Object[] {codi});
				} else {
					return new GenericDto("integracio.diag.digi.ko", "fa fa-times vermell", new Object[] {resultatDiagnostic});
				}
			} else if (codi.equals(IntegracioHelper.INTCODI_VALIDASIG)) {
				String resultatDiagnostic = pluginHelper.validaFirmaDiagnostic(filtre);
				if (resultatDiagnostic==null) {
					return new GenericDto("integracio.diag.vf.ok", "fa fa-check verd", new Object[] {codi});
				} else {
					return new GenericDto("integracio.diag.vf.ko", "fa fa-times vermell", new Object[] {resultatDiagnostic});
				}
			} else if (codi.equals(IntegracioHelper.INTCODI_PROCEDIMENT)) {
				String resultatDiagnostic = pluginHelper.gesConDiagnostic(filtre);
				if (resultatDiagnostic==null) {
					return new GenericDto("integracio.diag.gc.ok", "fa fa-check verd", new Object[] {codi});
				} else {
					return new GenericDto("integracio.diag.gc.ko", "fa fa-times vermell", new Object[] {resultatDiagnostic});
				}
			} else {
				return new GenericDto("integracio.diag.no", "fa fa-question-circle taronja", new Object[] {codi});
			}
		} else {
			return new GenericDto("integracio.diag.nf", "fa fa-question-circle taronja", new Object[] {codi});
		}
	}
	
	 @Override
	 @Transactional
	 public Long updateUsuariCodi(String codiAntic, String codiNou) {
		 Long registresModificats = 0l;
		 UsuariEntity usuariAntic = usuariRepository.findByCodi(codiAntic);
		 if (usuariAntic != null) {
			 createOrUpdateUsuari(codiNou, usuariAntic);
			 //Actualitzam la informació de auditoria de les taules:
			 registresModificats += updateUsuariAuditoria(codiAntic, codiNou);
			 // Actualitazam els permisos assignats per ACL
			 registresModificats += updateUsuariPermisos(codiAntic, codiNou);
			 //Actualitzam les referencis a l'usuari a taules:
			 registresModificats += updateUsuariReferencies(codiAntic, codiNou);
			 //Eliminam l'usuari antic
			 usuariRepository.delete(usuariAntic);
			 //Netejam caches per que es tornin a consultar les dades la proxima vegada.
			 cacheHelper.evictEntitatsAccessiblesUsuari(codiAntic);
			 cacheHelper.evictEntitatsAccessiblesUsuari(codiNou);
			 cacheHelper.evictAllReadAclById();
			 cacheHelper.evictFindRolsAmbCodi(codiAntic);
			 cacheHelper.evictFindRolsAmbCodi(codiNou);
			 aclCache.clearCache();
		 }
		return registresModificats;
	 }
	 
	 private Long updateUsuariAuditoria(String codiAntic, String codiNou) {
		 Long registresModificats = 0l;
		 registresModificats += alertaRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_ALERTA **
		 registresModificats += avisRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_AVIS **
		 registresModificats += configRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_CONFIG **
		 registresModificats += consultaPinbalRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_CONSULTA_PINBAL **
		 registresModificats += contingutRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_CONTINGUT **
		 registresModificats += contingutLogRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_CONT_LOG **
		 registresModificats += contingutMovimentRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_CONT_MOV **
		 registresModificats += dadaRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_DADA **
		 registresModificats += documentEnviamentInteressatRepository.updateUsuariAuditoriaDocEnv(codiAntic, codiNou);//IPA_DOCUMENT_ENVIAMENT **
		 registresModificats += dispositiuEnviamentRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_DOCUMENT_ENVIAMENT_DIS **
		 registresModificats += documentEnviamentInteressatRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_DOCUMENT_ENVIAMENT_INTER **
		 registresModificats += dominiRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_DOMINI **
		 registresModificats += emailPendentEnviarRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_EMAIL_PENDENT_ENVIAR **
		 registresModificats += entitatRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_ENTITAT **
		 registresModificats += execucioMassivaRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_EXECUCIO_MASSIVA **
		 registresModificats += expedientComentariRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_EXP_COMMENT **
		 registresModificats += expedientEstatRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_EXPEDIENT_ESTAT **
		 registresModificats += expedientOrganPareRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_EXPEDIENT_ORGANPARE **
		 registresModificats += expedientPeticioRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_EXPEDIENT_PETICIO **
		 registresModificats += expedientTascaRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_EXPEDIENT_TASCA **
		 registresModificats += expedientTascaComentariRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_EXP_TASCA_COMMENT **
		 registresModificats += fluxFirmaUsuariRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_FLUX_FIRMA_USUARI **
		 registresModificats += grupRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_GRUP **
		 registresModificats += entitatRepository.updateUsuariAuditoriaHistoric(codiAntic, codiNou);//IPA_HISTORIC **
		 registresModificats += interessatRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_INTERESSAT **
		 registresModificats += execucioMassivaContingutRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_MASSIVA_CONTINGUT **
		 registresModificats += metaDadaRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_METADADA **
		 registresModificats += metaExpedientComentariRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_METAEXP_COMMENT **
		 registresModificats += metaExpedientCarpetaRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_METAEXPEDIENT_CARPETA **
		 registresModificats += metaExpedientOrganGestorRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_METAEXP_ORGAN **
		 registresModificats += metaExpedientSequenciaRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_METAEXP_SEQ **
		 registresModificats += metaExpedientTascaRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_METAEXP_TASCA **
		 registresModificats += metaExpedientTascaValidacioRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_METAEXP_TASCA_VALIDACIO **
		 registresModificats += metaNodeRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_METANODE **
		 registresModificats += organGestorRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_ORGAN_GESTOR **
		 registresModificats += pinbalServeiRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_PINBAL_SERVEI
		 registresModificats += portafirmesBlockRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_PORTAFIRMES_BLOCK **
		 registresModificats += portafirmesBlockInfoRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_PORTAFIRMES_BLOCK_INFO **
		 registresModificats += registreRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_REGISTRE **
		 registresModificats += registreAnnexRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_REGISTRE_ANNEX **
		 registresModificats += registreInteressatRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_REGISTRE_INTERESSAT **
		 registresModificats += tipusDocumentalRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_TIPUS_DOCUMENTAL **
		 return registresModificats;
	 }
	 
	 private Long updateUsuariPermisos(String codiAntic, String codiNou) {
		 Long registresModificats = 0l;
		 registresModificats += aclSidRepository.updateUsuariPermis(codiAntic, codiNou);
	 	return registresModificats;
	 }
	 
	 private Long updateUsuariReferencies(String codiAntic, String codiNou) {
		 Long registresModificats = 0l;
		 registresModificats += expedientRepository.updateAgaftPer(codiAntic, codiNou);
		 registresModificats += expedientRepository.updateExpTasca(codiAntic, codiNou);
		 registresModificats += expedientRepository.updateExpTascaResponsable(codiAntic, codiNou);
		 registresModificats += expedientRepository.updateExpTascaObservador(codiAntic, codiNou);
		 registresModificats += expedientEstatRepository.updateExpEstatResponsable(codiAntic, codiNou);
		 registresModificats += contingutMovimentRepository.updateRemitentCodi(codiAntic, codiNou);
		 registresModificats += expedientRepository.updateExpSeguidorCodi(codiAntic, codiNou);
		 registresModificats += expedientRepository.updateExpPeticio(codiAntic, codiNou);
		 registresModificats += fluxFirmaUsuariRepository.updateUsuariCodi(codiAntic, codiNou);
		 registresModificats += historicUsuariRepository.updateUsuariCodi(codiAntic, codiNou);
		 registresModificats += viaFirmaUsuariRepository.updateUsuariViaFirma(codiAntic, codiNou);
		 registresModificats += metaExpedientTascaRepository.updateUsuariResponsable(codiAntic, codiNou);
		 return registresModificats;
	 }
	
	 private String createOrUpdateUsuari(String codiNou, UsuariEntity usuariAntic) {
		 Long t0 = System.currentTimeMillis();
		 UsuariEntity usuariNou = usuariRepository.findByCodi(codiNou);
		 boolean creat = false;
		 if (usuariNou==null) {
			 usuariNou = UsuariEntity.getBuilder(
					 codiNou,
					 usuariAntic.getNom(),
					 usuariAntic.getNif(),
					 usuariAntic.getEmail(),
					 usuariAntic.getIdioma()).build();
			 usuariRepository.saveAndFlush(usuariNou);
			 creat = true;
		 }
		 usuariNou.update(
				 usuariAntic.getEmailAlternatiu(),
				 usuariAntic.getIdioma(),
				 usuariAntic.isRebreEmailsAgrupats(),
				 usuariAntic.isRebreAvisosNovesAnotacions(),
				 usuariAntic.isRebreEmailsCanviEstatRevisio(),
				 usuariAntic.getNumElementsPagina(),
				 usuariAntic.isExpedientListDataDarrerEnviament(),
				 usuariAntic.isExpedientListAgafatPer(),
				 usuariAntic.isExpedientListInteressats(),
				 usuariAntic.isExpedientListComentaris(),
				 usuariAntic.isExpedientListGrup(),
				 usuariAntic.getProcediment(),
				 usuariAntic.getVistaActual(),
				 usuariAntic.isExpedientExpandit(),
				 usuariAntic.getEntitatPerDefecte(),
				 usuariAntic.getVistaMoureActual());
		 
		 usuariNou.setInicialitzat(usuariAntic.isInicialitzat());
		 usuariNou.setRolActual(usuariAntic.getRolActual());
		 usuariNou.setVersion(usuariAntic.getVersion());
		
		 if (creat) {
			return "<li>Creat nou usuari '"+codiNou+"' en "+(System.currentTimeMillis()-t0)+" ms.</li>";
		 } else {
			return "<li>Actualitzat usuari existent '"+codiNou+"' en "+(System.currentTimeMillis()-t0)+" ms.</li>";
		 }
	}

	private static final Logger logger = LoggerFactory.getLogger(AplicacioServiceImpl.class);
}