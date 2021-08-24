/**
 * 
 */
package es.caib.ripea.core.service;

import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;

import es.caib.ripea.core.helper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.ExcepcioLogDto;
import es.caib.ripea.core.api.dto.IntegracioAccioDto;
import es.caib.ripea.core.api.dto.IntegracioDto;
import es.caib.ripea.core.api.dto.PortafirmesCarrecDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.repository.UsuariRepository;
import es.caib.ripea.plugin.usuari.DadesUsuari;

/**
 * Implementació dels mètodes per a gestionar la versió de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class AplicacioServiceImpl implements AplicacioService {

	@Resource
	private UsuariRepository usuariRepository;
	@Resource
	private CacheHelper cacheHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private IntegracioHelper integracioHelper;
	@Resource
	private ExcepcioLogHelper excepcioLogHelper;
	@Resource
	private UsuariHelper usuariHelper;
	@Autowired
	private ConfigHelper configHelper;


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
	}
	
	@Transactional
	@Override
	public UsuariDto updateUsuariActual(UsuariDto dto) {
		logger.debug("Actualitzant configuració de usuari actual");
		UsuariEntity usuari = usuariRepository.findOne(dto.getCodi());
		usuari.update(
				dto.getIdioma());
		usuari.updateRebreEmailsAgrupats(dto.isRebreEmailsAgrupats());
		
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
	public UsuariDto findUsuariAmbCodiDades(String codi) {
		logger.debug("Obtenint usuari/càrrec amb codi (codi=" + codi + ")");
		UsuariDto usuariDto = null;
		try {
			usuariDto = conversioTipusHelper.convertir(
					usuariHelper.getUsuariByCodiDades(codi),
					UsuariDto.class);
		} catch (NotFoundException ex) {
			logger.error("No s'ha trobat cap usuari amb el codi " + codi + ". Procedim a cercar si és un càrrec.");
			usuariDto = new UsuariDto();
			PortafirmesCarrecDto carrec = pluginHelper.portafirmesRecuperarCarrec(codi);
			
			if (carrec != null) {
				usuariDto.setCodi(carrec.getCarrecId());
				usuariDto.setNom(carrec.getCarrecName() + " - " + carrec.getUsuariPersonaNom());
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
		logger.debug("Consultant les darreres accions per a la integració (" +
				"codi=" + codi + ")");
		return integracioHelper.findAccionsByIntegracioCodi(codi);
	}

	@Override
	public void excepcioSave(Throwable exception) {
		logger.debug("Emmagatzemant excepció (" +
				"exception=" + exception + ")");
		excepcioLogHelper.addExcepcio(exception);
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
		return cacheHelper.rolsDisponiblesEnAcls();
		//return aclSidRepository.findSidByPrincipalFalse();
	}

	@Override
	public boolean isPluginArxiuActiu() {
		logger.debug("Consulta si el plugin d'arxiu està actiu");
		return pluginHelper.isArxiuPluginActiu();
	}

	@Override
	public String propertyBaseUrl() {
		logger.debug("Consulta de la propietat base URL");
		return configHelper.getConfig("es.caib.ripea.base.url");
	}

	@Override
	public String propertyPluginPassarelaFirmaIds() {
		logger.debug("Consulta de la propietat amb les ids pels plugins de passarela de firma");
		return configHelper.getConfig("es.caib.ripea.plugin.passarelafirma.ids");
	}

	@Override
	public String propertyPluginPassarelaFirmaIgnorarModalIds() {
		logger.debug("Consulta de la propietat amb les ids pels plugins de passarela de firma");
		return configHelper.getConfig("es.caib.ripea.plugin.passarelafirma.ignorar.modal.ids");
	}

	@Override
	public String propertyPluginEscaneigIds() {
		logger.debug("Consulta de la propietat amb les ids pels plugins d'escaneig de documents");
		return configHelper.getConfig("es.caib.ripea.plugin.escaneig.ids");
	}

	@Override
	public Properties propertyFindByGroup(String codiGrup) {
		logger.debug("Consulta del valor de les properties d'un grup (" +
				"codi grup=" + codiGrup + ")");
		return configHelper.getGroupProperties(codiGrup);
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
		return dto;
	}

	private String getIdiomaPerDefecte() {
		return configHelper.getConfig("es.caib.ripea.usuari.idioma.defecte");
	}

	private static final Logger logger = LoggerFactory.getLogger(AplicacioServiceImpl.class);

}
