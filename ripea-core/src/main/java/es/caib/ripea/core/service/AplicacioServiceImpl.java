package es.caib.ripea.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.DiagnosticFiltreDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExcepcioLogDto;
import es.caib.ripea.core.api.dto.GenericDto;
import es.caib.ripea.core.api.dto.IntegracioAccioDto;
import es.caib.ripea.core.api.dto.IntegracioDto;
import es.caib.ripea.core.api.dto.IntegracioFiltreDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PortafirmesCarrecDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.core.entity.GrupEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.helper.CacheHelper;
import es.caib.ripea.core.helper.ConfigHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.ExcepcioLogHelper;
import es.caib.ripea.core.helper.IntegracioHelper;
import es.caib.ripea.core.helper.MetaExpedientHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.helper.PinbalHelper;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.helper.RolHelper;
import es.caib.ripea.core.helper.UsuariHelper;
import es.caib.ripea.core.repository.AlertaRepository;
import es.caib.ripea.core.repository.AvisRepository;
import es.caib.ripea.core.repository.ConsultaPinbalRepository;
import es.caib.ripea.core.repository.ContingutLogRepository;
import es.caib.ripea.core.repository.ContingutMovimentRepository;
import es.caib.ripea.core.repository.ContingutRepository;
import es.caib.ripea.core.repository.DadaRepository;
import es.caib.ripea.core.repository.DispositiuEnviamentRepository;
import es.caib.ripea.core.repository.DocumentEnviamentInteressatRepository;
import es.caib.ripea.core.repository.DominiRepository;
import es.caib.ripea.core.repository.EmailPendentEnviarRepository;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.ExecucioMassivaContingutRepository;
import es.caib.ripea.core.repository.ExecucioMassivaRepository;
import es.caib.ripea.core.repository.ExpedientComentariRepository;
import es.caib.ripea.core.repository.ExpedientEstatRepository;
import es.caib.ripea.core.repository.ExpedientOrganPareRepository;
import es.caib.ripea.core.repository.ExpedientPeticioRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.ExpedientTascaComentariRepository;
import es.caib.ripea.core.repository.ExpedientTascaRepository;
import es.caib.ripea.core.repository.FluxFirmaUsuariRepository;
import es.caib.ripea.core.repository.GrupRepository;
import es.caib.ripea.core.repository.InteressatRepository;
import es.caib.ripea.core.repository.MetaDadaRepository;
import es.caib.ripea.core.repository.MetaExpedientCarpetaRepository;
import es.caib.ripea.core.repository.MetaExpedientComentariRepository;
import es.caib.ripea.core.repository.MetaExpedientOrganGestorRepository;
import es.caib.ripea.core.repository.MetaExpedientRepository;
import es.caib.ripea.core.repository.MetaExpedientSequenciaRepository;
import es.caib.ripea.core.repository.MetaExpedientTascaRepository;
import es.caib.ripea.core.repository.MetaExpedientTascaValidacioRepository;
import es.caib.ripea.core.repository.MetaNodeRepository;
import es.caib.ripea.core.repository.OrganGestorRepository;
import es.caib.ripea.core.repository.PinbalServeiRepository;
import es.caib.ripea.core.repository.PortafirmesBlockInfoRepository;
import es.caib.ripea.core.repository.PortafirmesBlockRepository;
import es.caib.ripea.core.repository.RegistreAnnexRepository;
import es.caib.ripea.core.repository.RegistreInteressatRepository;
import es.caib.ripea.core.repository.RegistreRepository;
import es.caib.ripea.core.repository.TipusDocumentalRepository;
import es.caib.ripea.core.repository.URLInstruccioRepository;
import es.caib.ripea.core.repository.UsuariRepository;
import es.caib.ripea.core.repository.config.ConfigRepository;
import es.caib.ripea.core.repository.historic.HistoricUsuariRepository;
import es.caib.ripea.core.security.ExtendedPermission;
import es.caib.ripea.plugin.usuari.DadesUsuari;

@Service
public class AplicacioServiceImpl implements AplicacioService {

	@Resource private UsuariRepository usuariRepository;
	@Resource private CacheHelper cacheHelper;
	@Resource private PluginHelper pluginHelper;
	@Resource private PinbalHelper pinbalHelper;	
	@Resource private ConversioTipusHelper conversioTipusHelper;
	@Resource private IntegracioHelper integracioHelper;
	@Resource private ExcepcioLogHelper excepcioLogHelper;
	@Resource private UsuariHelper usuariHelper;
	@Resource private GrupRepository grupRepository;
	@Resource private RolHelper rolHelper;
	@Autowired private ConfigHelper configHelper;
	@Autowired private PaginacioHelper paginacioHelper;
	@Autowired private MetaExpedientRepository metaExpedientRepository;
	@Autowired private MetaExpedientHelper metaExpedientHelper;
    @Autowired private EntitatRepository entitatRepository;
    
    //UpdateUsuaris
    @Autowired private ExpedientRepository expedientRepository;
    @Autowired private HistoricUsuariRepository historicUsuariRepository;
    @Autowired private ConfigRepository configRepository;
    
    @Autowired private AlertaRepository alertaRepository;
    @Autowired private AvisRepository avisRepository;
    @Autowired private ConsultaPinbalRepository consultaPinbalRepository;
    @Autowired private ContingutRepository contingutRepository; //IPA_CONTINGUT
    @Autowired private ContingutLogRepository contingutLogRepository;
    @Autowired private ContingutMovimentRepository contingutMovimentRepository;
    @Autowired private DadaRepository dadaRepository;
    @Autowired private DocumentEnviamentInteressatRepository documentEnviamentInteressatRepository; //IPA_DOCUMENT_ENVIAMENT i IPA_DOCUMENT_ENVIAMENT_INTER
    @Autowired private DispositiuEnviamentRepository dispositiuEnviamentRepository;
    @Autowired private DominiRepository dominiRepository;
    @Autowired private EmailPendentEnviarRepository emailPendentEnviarRepository;
    @Autowired private ExecucioMassivaRepository execucioMassivaRepository;
    @Autowired private ExpedientComentariRepository expedientComentariRepository;
    @Autowired private ExpedientEstatRepository expedientEstatRepository; //IPA_EXPEDIENT_ESTAT
    @Autowired private ExpedientOrganPareRepository expedientOrganPareRepository;
    @Autowired private ExpedientPeticioRepository expedientPeticioRepository;
    @Autowired private ExpedientTascaRepository expedientTascaRepository; 
    @Autowired private ExpedientTascaComentariRepository expedientTascaComentariRepository;
    @Autowired private FluxFirmaUsuariRepository fluxFirmaUsuariRepository;
    @Autowired private InteressatRepository interessatRepository;
    @Autowired private ExecucioMassivaContingutRepository execucioMassivaContingutRepository;
    @Autowired private MetaDadaRepository metaDadaRepository;
    @Autowired private MetaExpedientComentariRepository metaExpedientComentariRepository; //IPA_METAEXP_COMMENT i IPA_METAEXP_DOMINI
    @Autowired private MetaExpedientCarpetaRepository metaExpedientCarpetaRepository; //IPA_METAEXPEDIENT_CARPETA
    @Autowired private MetaExpedientOrganGestorRepository metaExpedientOrganGestorRepository;
    @Autowired private MetaExpedientSequenciaRepository metaExpedientSequenciaRepository;
    @Autowired private MetaExpedientTascaRepository metaExpedientTascaRepository;
    @Autowired private MetaExpedientTascaValidacioRepository metaExpedientTascaValidacioRepository;
    @Autowired private MetaNodeRepository metaNodeRepository; //IPA_METANODE
    @Autowired private OrganGestorRepository organGestorRepository;
    @Autowired private PinbalServeiRepository pinbalServeiRepository;
    @Autowired private PortafirmesBlockRepository portafirmesBlockRepository;
    @Autowired private PortafirmesBlockInfoRepository portafirmesBlockInfoRepository;
    @Autowired private RegistreRepository registreRepository;
    @Autowired private RegistreAnnexRepository registreAnnexRepository;
    @Autowired private RegistreInteressatRepository registreInteressatRepository;
    @Autowired private TipusDocumentalRepository tipusDocumentalRepository;
    @Autowired private URLInstruccioRepository uRLInstruccioRepository;
    @Autowired private AclCache aclCache;

	@Override
	public void actualitzarEntiatThreadLocal(EntitatDto entitat) {
		ConfigHelper.setEntitat(entitat);
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
	
	
	@Transactional
	@Override
	public void processarAutenticacioUsuari() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Processant autenticació (usuariCodi=" + auth.getName() + ")");
		UsuariEntity usuari = usuariRepository.findOne(auth.getName());
		if (usuari == null) {
			logger.debug("Consultant plugin de dades d'usuari (" +
					"usuariCodi=" + auth.getName() + ")");
			DadesUsuari dadesUsuari = cacheHelper.findUsuariAmbCodi(auth.getName());
			if (dadesUsuari != null) {
				usuari = usuariRepository.save(
						UsuariEntity.getBuilder(
								dadesUsuari.getCodi(),
								dadesUsuari.getNom(),
								dadesUsuari.getNif(),
								dadesUsuari.getEmail(),
								getIdiomaPerDefecte()).build());
			} else {
				throw new NotFoundException(
						auth.getName(),
						DadesUsuari.class);
			}
		} else {
			logger.debug("Consultant plugin de dades d'usuari (" +
					"usuariCodi=" + auth.getName() + ")");
			DadesUsuari dadesUsuari = cacheHelper.findUsuariAmbCodi(auth.getName());
			if (dadesUsuari != null) {
				usuari.update(
						dadesUsuari.getNom(),
						dadesUsuari.getNif(),
						dadesUsuari.getEmail());
			} else {
				throw new NotFoundException(
						auth.getName(),
						DadesUsuari.class);
			}
		}
	}

	@Transactional(readOnly = true)
	@Override
	public UsuariDto getUsuariActual() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Obtenint usuari actual");
		return toUsuariDtoAmbRols(
				usuariRepository.findOne(auth.getName()));
	}
	
	@Transactional
	@Override
	public void setRolUsuariActual(String rolActual) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Actualitzant rol de usuari actual");

		UsuariEntity usuari = usuariRepository.findOne(auth.getName());
		usuari.updateRolActual(rolActual);

		cacheHelper.evictCountAnotacionsPendents(usuari.getCodi());
	}
	
	@Transactional
	@Override
	public UsuariDto updateUsuariActual(UsuariDto dto) {
		logger.debug("Actualitzant configuració de usuari actual");
		UsuariEntity usuari = usuariRepository.findOne(dto.getCodi());
		
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
				dto.getProcedimentId() != null ? metaExpedientRepository.findOne(dto.getProcedimentId()) : null,
				dto.getVistaActual(), 
				dto.isExpedientExpandit(),
				dto.getEntitatPerDefecteId() != null ? entitatRepository.findOne(dto.getEntitatPerDefecteId()) : null,
				dto.getVistaMoureActual());
		
		return toUsuariDtoAmbRols(usuari);
	}

	@Transactional(readOnly = true)
	@Override
	public UsuariDto findUsuariAmbCodi(String codi) {
		logger.debug("Obtenint usuari amb codi (codi=" + codi + ")");
		return conversioTipusHelper.convertir(
				usuariRepository.findOne(codi),
				UsuariDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<UsuariDto> findUsuariAmbText(String text) {
		logger.debug("Consultant usuaris amb text (text=" + text + ")");
		return conversioTipusHelper.convertirList(
				usuariRepository.findByText(text),
				UsuariDto.class);
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public UsuariDto findUsuariCarrecAmbCodiDades(String codi) {
		logger.debug("Obtenint usuari/càrrec amb codi (codi=" + codi + ")");
		UsuariDto usuariDto = null;
		try {
			usuariDto = conversioTipusHelper.convertir(
					usuariHelper.getUsuariByCodiDades(codi, true, true),
					UsuariDto.class);
		} catch (NotFoundException ex) {
			logger.error("No s'ha trobat cap usuari amb el codi " + codi + ". Procedim a cercar si és un càrrec.");
			usuariDto = new UsuariDto();
			PortafirmesCarrecDto carrec = pluginHelper.portafirmesRecuperarCarrec(codi);
			
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
						codi,
						DadesUsuari.class);
			}
		}
		return usuariDto;
	}
	
	@Transactional(readOnly = true)
	@Override
	public UsuariDto findUsuariAmbCodiDades(String codi) {
		logger.debug("Obtenint usuari amb codi (codi=" + codi + ")");
		UsuariDto usuariDto = null;
		usuariDto = conversioTipusHelper.convertir(
				usuariHelper.getUsuariByCodiDades(codi, true, true),
				UsuariDto.class);

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
	public void excepcioSave(Throwable exception) {
		try {
			logger.debug("Emmagatzemant excepció (exception=" + exception + ")");
			excepcioLogHelper.addExcepcio(exception);
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
		cacheHelper.evictCountAnotacionsPendents(usuariCodi);
	}

	@Override
	public String propertyBaseUrl() {
		logger.debug("Consulta de la propietat base URL");
		return configHelper.getConfig("es.caib.ripea.base.url");
	}

	@Override
	public String propertyPluginEscaneigIds() {
		logger.debug("Consulta de la propietat amb les ids pels plugins d'escaneig de documents");
		return configHelper.getConfig("es.caib.ripea.plugin.escaneig.ids");
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
		String property = configHelper.getJBossProperty(key);
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
		UsuariEntity usuari = usuariRepository.findOne(auth.getName());
		
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
		return configHelper.getConfig("es.caib.ripea.usuari.idioma.defecte");
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
		 registresModificats += uRLInstruccioRepository.updateUsuariAuditoria(codiAntic, codiNou);//IPA_URL_INSTRUCCIO
		 return registresModificats;
	 }
	 
	 private Long updateUsuariPermisos(String codiAntic, String codiNou) {
		 Long registresModificats = 0l;
		 registresModificats += usuariRepository.updateUsuariPermis(codiAntic, codiNou);
	 	usuariRepository.flush();
	 	return registresModificats;
	 }
	 
	 private Long updateUsuariReferencies(String codiAntic, String codiNou) {
		 Long registresModificats = 0l;
		 registresModificats += expedientRepository.updateAgaftPer(codiAntic, codiNou);
		 registresModificats += registresModificats += expedientRepository.updateExpTasca(codiAntic, codiNou);
		 registresModificats += expedientRepository.updateExpTascaResponsable(codiAntic, codiNou);
		 registresModificats += expedientRepository.updateExpTascaObservador(codiAntic, codiNou);
		 registresModificats += expedientEstatRepository.updateExpEstatResponsable(codiAntic, codiNou);
		 registresModificats += contingutMovimentRepository.updateRemitentCodi(codiAntic, codiNou);
		 registresModificats += expedientRepository.updateExpSeguidorCodi(codiAntic, codiNou);
		 registresModificats += expedientRepository.updateExpPeticio(codiAntic, codiNou);
		 registresModificats += fluxFirmaUsuariRepository.updateUsuariCodi(codiAntic, codiNou);
		 registresModificats += historicUsuariRepository.updateUsuariCodi(codiAntic, codiNou);
		 registresModificats += usuariRepository.updateUsuariViaFirma(codiAntic, codiNou);
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