/**
 * 
 */
package es.caib.ripea.war.controller;

import javax.servlet.http.HttpServletRequest;

import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.PinbalConsentimentEnumDto;
import es.caib.ripea.core.api.service.DadesExternesService;
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.war.helper.EnumHelper;
import org.springframework.beans.factory.annotation.Autowired;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.service.EntitatService;
import es.caib.ripea.war.helper.EntitatHelper;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class BaseUserOAdminOOrganController extends BaseController {

	@Autowired private EntitatService entitatService;
	@Autowired private MetaDocumentService metaDocumentService;
	@Autowired private DadesExternesService dadesExternesService;
	
	public EntitatDto getEntitatActualComprovantPermisos(
			HttpServletRequest request) {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat == null) {
			throw new SecurityException("No te cap entitat assignada");
		}
		
		if (!entitat.isUsuariActualRead() && !entitat.isUsuariActualAdministration() && !entitat.isUsuariActualTeOrgans()) {
			throw new SecurityException("No te permisos per accedir a aquesta entitat com a usuari o administrador d'entitat o administrator de l'organ");
		}
		return entitat;
	}

	public void omplirModelFormulari(
			HttpServletRequest request,
			Long contingutId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		List<MetaDocumentDto> metaDocuments = metaDocumentService.findActiusPerCreacio(
				entitatActual.getId(),
				contingutId,
				null,
				false);
		Iterator<MetaDocumentDto> itmd = metaDocuments.iterator();
		while (itmd.hasNext()) {
			MetaDocumentDto metaDocument = itmd.next();
			if (!metaDocument.isPinbalActiu()) {
				itmd.remove();
			}
		};
		model.addAttribute("metaDocuments", metaDocuments);
		model.addAttribute("interessats", new ArrayList<>());
		model.addAttribute(
				"consentimentOptions",
				EnumHelper.getOptionsForEnum(
						PinbalConsentimentEnumDto.class,
						"pinbal.consentiment.enum."));
		model.addAttribute(
				"comunitats",
				Arrays.asList(new EnumHelper.HtmlOption("04", "Illes Balears")));
		model.addAttribute(
				"provincies",
				Arrays.asList(new EnumHelper.HtmlOption("07", "Illes Balears")));
		model.addAttribute(
				"municipis",
				dadesExternesService.findMunicipisPerProvinciaPinbal("07"));
		model.addAttribute(
				"paisos",
				dadesExternesService.findPaisos());
	}

}
