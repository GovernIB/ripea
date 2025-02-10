package es.caib.ripea.back.controller;

import es.caib.ripea.back.helper.EntitatHelper;
import es.caib.ripea.back.helper.EnumHelper;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.MetaDocumentDto;
import es.caib.ripea.service.intf.dto.PinbalConsentimentEnumDto;
import es.caib.ripea.service.intf.service.DadesExternesService;
import es.caib.ripea.service.intf.service.EntitatService;
import es.caib.ripea.service.intf.service.MetaDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class BaseUserOAdminOOrganController extends BaseController {

	@Autowired private EntitatService entitatService;
	@Autowired private MetaDocumentService metaDocumentService;
	@Autowired private DadesExternesService dadesExternesService;
	
	public EntitatDto getEntitatActualComprovantPermisos(HttpServletRequest request) {
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
