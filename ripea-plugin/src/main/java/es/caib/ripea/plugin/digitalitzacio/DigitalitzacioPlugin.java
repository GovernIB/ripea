package es.caib.ripea.plugin.digitalitzacio;

import java.util.List;

import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.plugin.SistemaExternException;

/**
 * Plugin per a la integració amb portafirmes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DigitalitzacioPlugin {

	/**
	 * Indica si el plugin suporta la custòdia de documents i si aquesta
	 * es fa de manera automàtica una vegada firmat el document.
	 * 
	 * @return true si està suportada i es fa de forma automàtica o false
	 *            en cas contrari.
	 */
	public List<DigitalitzacioPerfil> recuperarPerfilsDisponibles(
			String idioma) throws SistemaExternException;
	
	/**
	 * Indica si el plugin suporta la custòdia de documents i si aquesta
	 * es fa de manera automàtica una vegada firmat el document.
	 * 
	 * @return true si està suportada i es fa de forma automàtica o false
	 *            en cas contrari.
	 */
	public DigitalitzacioTransaccioResposta iniciarProces(
			String codiPerfil,
			String idioma,
			UsuariDto funcionari,
			String returnUrl) throws SistemaExternException;
	
	/**
	 * Indica si el plugin suporta la custòdia de documents i si aquesta
	 * es fa de manera automàtica una vegada firmat el document.
	 * 
	 * @return true si està suportada i es fa de forma automàtica o false
	 *            en cas contrari.
	 */
	public DigitalitzacioResultat recuperarResultat(
			String idTransaccio,
			boolean returnScannedFile,
			boolean returnSignedFile) throws SistemaExternException;

	/**
	 * Indica si el plugin suporta la custòdia de documents i si aquesta
	 * es fa de manera automàtica una vegada firmat el document.
	 * 
	 * @return true si està suportada i es fa de forma automàtica o false
	 *            en cas contrari.
	 */
	public void tancarTransaccio(
			String idTransaccio) throws SistemaExternException;
}
