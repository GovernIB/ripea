package es.caib.ripea.service.helper;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.UsuariEntity;
import es.caib.ripea.persistence.entity.ViaFirmaUsuariEntity;
import es.caib.ripea.persistence.repository.UsuariRepository;
import es.caib.ripea.plugin.usuari.DadesUsuari;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.PortafirmesCarrecDto;
import es.caib.ripea.service.intf.dto.UsuariDto;
import es.caib.ripea.service.intf.dto.ViaFirmaUsuariDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.utils.Utils;

@Component
public class UsuariHelper {
 
	@Autowired private UsuariRepository usuariRepository;
	@Autowired private CacheHelper cacheHelper;
	@Autowired private ConfigHelper configHelper;
	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Autowired private PluginHelper pluginHelper;

	public Authentication generarUsuariAutenticat(
			String usuariCodi,
			boolean establirComAUsuariActual) {
		List<GrantedAuthority> authorities = null;
		Authentication auth = new DadesUsuariAuthenticationToken(
				usuariCodi,
			authorities);
		if (establirComAUsuariActual)
			SecurityContextHolder.getContext().setAuthentication(auth);
		return auth;
	}

	public List<UsuariDto> findUsuariAmbText(String text) {
		return conversioTipusHelper.convertirList(
				usuariRepository.findByText(text),
				UsuariDto.class);
	}
	
	public List<DadesUsuari> findDadesUsuariAmbText(String text) {
		return conversioTipusHelper.convertirList(
				usuariRepository.findByText(text),
				DadesUsuari.class);
	}
	
	public class DadesUsuariAuthenticationToken extends AbstractAuthenticationToken {
		String principal;
		public DadesUsuariAuthenticationToken(
				String usuariCodi,
				Collection<GrantedAuthority> authorities) {
			super(authorities);
			principal = usuariCodi;
		}
		@Override
		public Object getCredentials() {
			return principal;
		}
		@Override
		public Object getPrincipal() {
			return principal;
		}
		private static final long serialVersionUID = 5974089352023050267L;
	}

	public UsuariEntity getUsuariAutenticat() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null)
			return null;
		UsuariEntity usuari = usuariRepository.findById(auth.getName()).orElse(null);
		if (usuari == null) {
			logger.debug("Consultant plugin de dades d'usuari (" +
					"usuariCodi=" + auth.getName() + ")");
			// Primer cream l'usuari amb dades fictícies i després l'actualitzam.
			// Així evitam possibles bucles infinits a l'hora de guardar registre
			// de les peticions al plugin d'usuaris. <-- can't understand why doing it this way would prevent any infinite loops TODO: think if it can be removed and if can use method getUsuariByCodiDades() instead
			usuari = usuariRepository.save(
					UsuariEntity.getBuilder(
							auth.getName(),
							auth.getName(),
							"00000000X",
							auth.getName() + "@" + "caib.es",
							getIdiomaPerDefecte()).build());
			DadesUsuari dadesUsuari = cacheHelper.findUsuariAmbCodi(auth.getName());
			if (dadesUsuari != null) {
				if (auth.getName().equals("INIT")) {
					logger.info("INIT user in seycon: " + dadesUsuari.getNom() + ", " + dadesUsuari.getNif() + ", " + dadesUsuari.getEmail());
				}
				usuari.update(
						dadesUsuari.getNom(),
						dadesUsuari.getNif(),
						dadesUsuari.getEmail());
				usuariRepository.save(usuari);
			} else {
				throw new NotFoundException(
						auth.getName(),
						UsuariEntity.class);
			}
		}
		return usuari;
	}

	public UsuariEntity getUsuariByCodi(String codi) {
		
		UsuariEntity usuari = usuariRepository.findById(codi).orElse(null);
		if (usuari == null) {
			logger.debug("Consultant plugin de dades d'usuari (" +
					"usuariCodi=" + codi + ")");
			// Primer cream l'usuari amb dades fictícies i després l'actualitzam.
			// Així evitam possibles bucles infinits a l'hora de guardar registre
			// de les peticions al plugin d'usuaris. <-- can't understand why doing it this way would prevent any infinite loops TODO: think if it can be removed and if can use method getUsuariByCodiDades() instead
			usuari = usuariRepository.save(
					UsuariEntity.getBuilder(
							codi,
							codi,
							"00000000X",
							codi + "@" + "caib.es",
							getIdiomaPerDefecte()).build());
			DadesUsuari dadesUsuari = cacheHelper.findUsuariAmbCodi(codi);
			if (dadesUsuari != null) {
				usuari.update(
						dadesUsuari.getNom(),
						dadesUsuari.getNif(),
						dadesUsuari.getEmail());
				usuariRepository.save(usuari);
			} else {
				throw new NotFoundException(
						codi,
						UsuariEntity.class);
			}
		}
		return usuari;
	}
	
	/**
	 * A aquest mètode sempre arriben NIFs, que son els responsables de PF.
	 * Que es guarden a la taula IPA_METADOCUMENT.PORTAFIRMES_RESPONS separats per comes.
	 */
	public UsuariDto getUsuariResponsableByNif(String usuariNif) {
		
		//1.- Cerca a BBDD NIF (nomes hauria de retornar un resultat)
		List<UsuariEntity> usuari = usuariRepository.findByNifOrderByVersionDesc(usuariNif);
		
		if (usuari != null && usuari.size()>0) {
			return conversioTipusHelper.convertir(usuari.get(0), UsuariDto.class);
		}
		
		//2.- Cerca per plugin usuaris si no ha trobat usuari a BBDD
		DadesUsuari dadesUsuari = null;
		List<DadesUsuari> dadesUsuaris = pluginHelper.findAmbFiltre(usuariNif);
		if (dadesUsuaris!=null && dadesUsuaris.size()>0) {
			dadesUsuari = dadesUsuaris.get(0);
		}
		
		if (dadesUsuari == null) {
			throw new NotFoundException(usuariNif, DadesUsuari.class);
		} else {
			UsuariDto aux = new UsuariDto();
			aux.setNif(dadesUsuari.getNif());
			aux.setNom(dadesUsuari.getNomSencer());
			aux.setCodi(dadesUsuari.getCodi());
			aux.setEmail(dadesUsuari.getEmail());
			return aux;
		}
	}
	
	/**
	 * Metode anterior AMPLIAT a CARRECS.
	 * Primer crida a getUsuariResponsableByNif per cercar per NIF tant a BBDD com a plugin.
	 * Opcionalment, es crida al WS de UsuariEntitat.
	 */
	public UsuariDto findUsuariCarrecAmbCodiDades(String nifOrCarrec) {
		logger.debug("Obtenint usuari/càrrec amb codi (codi=" + nifOrCarrec + ")");
		UsuariDto usuariDto = null;
		try {
			//Cercar primer a BBDD, sino al plugin, sempre per NIF, y sense guardarlo a BBDD RIPEA.
			usuariDto = getUsuariResponsableByNif(nifOrCarrec);
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

	public UsuariDto getUsuariByCodiDades(String codi) {
		UsuariEntity usuariBD = usuariRepository.findByCodi(codi);
		if (usuariBD!=null) {
			return conversioTipusHelper.convertir(usuariBD, UsuariDto.class);
		}
		DadesUsuari duPlugin = pluginHelper.dadesUsuariFindAmbCodi(codi);
		if (duPlugin!=null) {
			UsuariDto resultat = new UsuariDto();
			resultat.setCodi(codi);
			resultat.setEmail(duPlugin.getEmail());
			resultat.setNif(duPlugin.getNif());
			resultat.setNom(duPlugin.getNomSencer());
			return resultat;
		}
		return null;
	}
	
	private String getIdiomaPerDefecte() {
		return configHelper.getConfig(PropertyConfig.IDIOMA_DEFECTE);
	}

	public Set<ViaFirmaUsuariEntity> viaFirmaUsuarisUsuariActual() {
		UsuariEntity usuari = usuariRepository.findByCodi(SecurityContextHolder.getContext().getAuthentication().getName());
		return usuari.getViaFirmaUsuaris();
	}
	
	private static final Logger logger = LoggerFactory.getLogger(UsuariHelper.class);

}
