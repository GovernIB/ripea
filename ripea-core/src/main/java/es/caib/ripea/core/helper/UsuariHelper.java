/**
 * 
 */
package es.caib.ripea.core.helper;

import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.repository.UsuariRepository;
import es.caib.ripea.plugin.usuari.DadesUsuari;

/**
 * Helper per a operacions amb usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class UsuariHelper {
 
	@Resource
	private UsuariRepository usuariRepository;

	@Resource
	private CacheHelper cacheHelper;
	@Autowired
	private ConfigHelper configHelper;



	public Authentication generarUsuariAutenticatEjb(
			SessionContext sessionContext,
			boolean establirComAUsuariActual) {
		if (sessionContext != null && sessionContext.getCallerPrincipal() != null) {
			return generarUsuariAutenticat(
					sessionContext.getCallerPrincipal().getName(),
					establirComAUsuariActual);
		} else {
			return null;
		}
	}
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
		UsuariEntity usuari = usuariRepository.findOne(auth.getName());
		if (usuari == null) {
			logger.debug("Consultant plugin de dades d'usuari (" +
					"usuariCodi=" + auth.getName() + ")");
			// Primer cream l'usuari amb dades fictícies i després l'actualitzam.
			// Així evitam possibles bucles infinits a l'hora de guardar registre
			// de les peticions al plugin d'usuaris.
			usuari = usuariRepository.save(
					UsuariEntity.getBuilder(
							auth.getName(),
							auth.getName(),
							"00000000X",
							auth.getName() + "@" + "caib.es",
							getIdiomaPerDefecte()).build());
			DadesUsuari dadesUsuari = cacheHelper.findUsuariAmbCodi(auth.getName());
			if (dadesUsuari != null) {
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
		
		UsuariEntity usuari = usuariRepository.findOne(codi);
		if (usuari == null) {
			logger.debug("Consultant plugin de dades d'usuari (" +
					"usuariCodi=" + codi + ")");
			// Primer cream l'usuari amb dades fictícies i després l'actualitzam.
			// Així evitam possibles bucles infinits a l'hora de guardar registre
			// de les peticions al plugin d'usuaris.
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
	
	public UsuariEntity getUsuariByCodiDades(String usuariCodi) {

		logger.debug("Cercant d’usuari a la base de dades (usuariCodi=" + usuariCodi + ")");
		UsuariEntity usuari = usuariRepository.findOne(usuariCodi);
		
		if (usuari == null)
			usuari = usuariRepository.findByNif(usuariCodi);
			
		if (usuari == null) {
			logger.debug("Consultant plugin de dades d'usuari (" +
					"usuariCodi=" + usuariCodi + ")");
			DadesUsuari dadesUsuari = cacheHelper.findUsuariAmbCodi(usuariCodi);
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
						usuariCodi,
						DadesUsuari.class);
			}
		} else {
			logger.debug("Consultant plugin de dades d'usuari (" +
					"usuariCodi=" + usuariCodi + ")");
			DadesUsuari dadesUsuari = cacheHelper.findUsuariAmbCodi(usuariCodi);
			if (dadesUsuari != null) {
				usuari.update(
						dadesUsuari.getNom(),
						dadesUsuari.getNif(),
						dadesUsuari.getEmail());
			} else {
				throw new NotFoundException(
						usuariCodi,
						DadesUsuari.class);
			}
		}
		
		return usuari;
	}
	

	private String getIdiomaPerDefecte() {
		return configHelper.getConfig("es.caib.ripea.usuari.idioma.defecte");
	}

	private static final Logger logger = LoggerFactory.getLogger(UsuariHelper.class);

}
