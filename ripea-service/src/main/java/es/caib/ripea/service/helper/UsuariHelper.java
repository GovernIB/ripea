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
import es.caib.ripea.service.intf.dto.UsuariDto;
import es.caib.ripea.service.intf.dto.ViaFirmaUsuariDto;
import es.caib.ripea.service.intf.exception.NotFoundException;

@Component
public class UsuariHelper {
 
	@Autowired private UsuariRepository usuariRepository;
	@Autowired private CacheHelper cacheHelper;
	@Autowired private ConfigHelper configHelper;
	@Autowired private ConversioTipusHelper conversioTipusHelper;

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
	

	public UsuariEntity getUsuariByCodiDades(
			String usuariCodi,
			boolean checkAlsoByNif,
			boolean throwException) {
		
		DadesUsuari dadesUsuari = cacheHelper.findUsuariAmbCodi(usuariCodi);
		if (dadesUsuari == null) {
			if (throwException) {
				throw new NotFoundException(
						usuariCodi,
						DadesUsuari.class);
			} else {
				return null;
			}
		}
		
		UsuariEntity usuari = usuariRepository.findById(usuariCodi).orElse(null);
		
		if (usuari == null && checkAlsoByNif)
			usuari = usuariRepository.findByNif(usuariCodi);
		
		if (usuari == null) {
			usuari = usuariRepository.save(
					UsuariEntity.getBuilder(
							dadesUsuari.getCodi(),
							dadesUsuari.getNom(),
							dadesUsuari.getNif(),
							dadesUsuari.getEmail(),
							getIdiomaPerDefecte()).build());
		} else {
			usuari.update(
					dadesUsuari.getNom(),
					dadesUsuari.getNif(),
					dadesUsuari.getEmail());
		}
		
		return usuari;
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
