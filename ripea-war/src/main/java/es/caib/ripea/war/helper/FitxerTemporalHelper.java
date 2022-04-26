/**
 * 
 */
package es.caib.ripea.war.helper;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import es.caib.ripea.core.api.dto.FitxerTemporalDto;
import es.caib.ripea.war.command.DocumentCommand;

/**
 * Utilitat per a gestionar arxius temporals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class FitxerTemporalHelper {
	public static final String SESSION_ATTRIBUTE_DOCUMENT = "ContingutDocumentController.session.document";
	public static final String SESSION_ATTRIBUTE_FIRMA = "ContingutDocumentController.session.firma";

	public static void guardarFitxersAdjuntsSessio(			
			HttpServletRequest request,
			DocumentCommand command,
			Model model) throws IOException {
		if (command.getArxiu() != null && !command.getArxiu().isEmpty()) {
			FitxerTemporalDto fitxer = new FitxerTemporalDto(
					command.getArxiu().getOriginalFilename(),
					command.getArxiu().getContentType(),
					command.getArxiu().getBytes());
			request.getSession().setAttribute(SESSION_ATTRIBUTE_DOCUMENT, fitxer);
		} 
		FitxerTemporalDto fitxerTemp = (FitxerTemporalDto) request.getSession().getAttribute(SESSION_ATTRIBUTE_DOCUMENT);
		if (fitxerTemp != null) {
			command.setArxiuTemporal(fitxerTemp);
			model.addAttribute("nomDocument", fitxerTemp.getNom());
		}
		
		if (command.getFirma() != null && !command.getFirma().isEmpty()) {
			FitxerTemporalDto fitxer = new FitxerTemporalDto(
					command.getFirma().getOriginalFilename(),
					command.getFirma().getContentType(),
					command.getFirma().getBytes());
			request.getSession().setAttribute(SESSION_ATTRIBUTE_FIRMA, fitxer);
		} 
		FitxerTemporalDto firmaTemp = (FitxerTemporalDto) request.getSession().getAttribute(SESSION_ATTRIBUTE_FIRMA);
		if (firmaTemp != null) {
			command.setFirmaTemporal(firmaTemp);
			model.addAttribute("nomFirma", firmaTemp.getNom());
		}
	}

	public static void esborrarFitxersAdjuntsSessio(HttpServletRequest request) {
		FitxerTemporalDto fitxerTemporalDto = ((FitxerTemporalDto) request.getSession().getAttribute(SESSION_ATTRIBUTE_DOCUMENT));
		if (fitxerTemporalDto != null) {
			fitxerTemporalDto.delete();
		}
		request.getSession().removeAttribute(SESSION_ATTRIBUTE_DOCUMENT);
		if (((FitxerTemporalDto) request.getSession().getAttribute(SESSION_ATTRIBUTE_FIRMA)) != null) {
			((FitxerTemporalDto) request.getSession().getAttribute(SESSION_ATTRIBUTE_FIRMA)).delete();
			request.getSession().removeAttribute(SESSION_ATTRIBUTE_FIRMA);
		}
	}
}
