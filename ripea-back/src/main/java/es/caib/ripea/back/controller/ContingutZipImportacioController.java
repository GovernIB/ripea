package es.caib.ripea.back.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.back.command.ImportacioZipCommand;
import es.caib.ripea.back.command.ImportacioZipCommand.ProcessarZip;
import es.caib.ripea.back.helper.EnumHelper;
import es.caib.ripea.back.helper.MissatgesHelper;
import es.caib.ripea.back.helper.RolHelper;
import es.caib.ripea.service.intf.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.ProgresProcessamentZipDto;
import es.caib.ripea.service.intf.dto.UsuariDto;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.DocumentService;
import es.caib.ripea.service.intf.service.MetaDocumentService;


/**
 * Controlador per al manteniment d'importació de documents de zip.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/contingut")
public class ContingutZipImportacioController extends BaseUserOAdminOOrganController {

	@Autowired
	private DocumentService documentService;
	@Autowired
	private MetaDocumentService metaDocumentService;
	@Autowired
	private AplicacioService aplicacioService;
		
	@RequestMapping(value = "/{pareId}/zip/importacio/new", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request, 
			@PathVariable Long pareId,
			@RequestParam(value = "tascaId", required = false) Long tascaId, 
			@RequestParam(value = "metaExpedientId", required = false) Long metaExpedientId,
			Model model)
			throws ClassNotFoundException, IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ImportacioZipCommand command = new ImportacioZipCommand();
		
		command.setPareId(pareId);
		command.setTascaId(tascaId);
		command.setMetaExpedientId(metaExpedientId);

		omplirModelFormulari(
				model, 
				pareId, 
				command, 
				entitatActual);
		return "contingutZipImportacioForm";
	}

	@RequestMapping(value = "/{pareId}/zip/importacio/processar", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> processarZipAsync(
	        HttpServletRequest request,
	        @PathVariable Long pareId,
	        @ModelAttribute("command") @Validated({ProcessarZip.class}) ImportacioZipCommand command,
	        BindingResult bindingResult) throws IOException {

	    EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

	    if (bindingResult.hasErrors()) {
	        return ResponseEntity.badRequest().body(
	        		getMessage(
	        				request,
	        				"javax.validation.constraints.NotEmpty.message"));
	    }
	    
	    UsuariDto usuariActual = aplicacioService.getUsuariActual();
	    
	    documentService.inicialitzarProgresProcessamentZip(pareId);
	    
	    Path tempZip = Files.createTempFile("import-", ".zip");
	    command.getArxiuZip().transferTo(tempZip);
	    
	    documentService.processarZipAsync(
	    		usuariActual,
	    		tempZip,
	            RolHelper.getRolActual(request),
	            command.getPareId(),
	            command.getTascaId(),
	            entitatActual);

	    return ResponseEntity.status(HttpStatus.ACCEPTED).body("Processament iniciat");
	}

	@RequestMapping(value = "/{pareId}/zip/importacio/cancelar", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> canceparProcesZip(
	        HttpServletRequest request,
	        @PathVariable Long pareId) throws IOException {
	    documentService.cancelarProcessamentZip(pareId);

	    return ResponseEntity.status(HttpStatus.OK).body("Processament cancelat");
	}

	@RequestMapping(value = "/{pareId}/zip/importacio/progres", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<ProgresProcessamentZipDto> processarZip(
			HttpServletRequest request, 
			@PathVariable Long pareId,
			Model model) throws ClassNotFoundException, IOException {		
		try {
			ProgresProcessamentZipDto progres = documentService.obtenirProgresProcessamentZip(pareId);
			
			return new ResponseEntity<ProgresProcessamentZipDto>(progres, HttpStatus.OK);
		} catch (Exception ex) {
			MissatgesHelper.error(request, ex.getMessage(), ex);
		}
		return new ResponseEntity<ProgresProcessamentZipDto>(HttpStatus.OK);
	}

	private void omplirModelFormulari(Model model, Long pareId, ImportacioZipCommand command, EntitatDto entitatActual) {
		String action = "/contingut/" + pareId + "/zip/importacio/processar";
		
		if (command.getDocuments() != null && ! command.getDocuments().isEmpty())
			action = "/contingut/" + pareId + "/zip/importacio/new";
		
		model.addAttribute("action", action);
		model.addAttribute(
				"metaDocuments",
				metaDocumentService.findActiusPerCreacio(
						entitatActual.getId(),
						null,
						command.getMetaExpedientId(),
						false));
		model.addAttribute(
				"ntiEstatElaboracioOptions",
				EnumHelper.getOptionsForEnum(
						DocumentNtiEstadoElaboracionEnumDto.class,
						"document.nti.estela.enum."));
		model.addAttribute("isPermesPropagarModificacioDefinitius", isPropagarModificacioDefinitiusActiva());
		model.addAttribute("estatsElaboracioIdentificadorEniObligat", obtenirEstatsElaboracioIdentificadorEniObligat());
		model.addAttribute("command", command);
	}
	
	private Boolean isPropagarModificacioDefinitiusActiva() {
		return aplicacioService.propertyBooleanFindByKey("es.caib.ripea.document.propagar.modificacio.arxiu");
	}
	
	private String obtenirEstatsElaboracioIdentificadorEniObligat() {
		return aplicacioService.propertyFindByNom("es.caib.ripea.estat.elaboracio.identificador.origen.obligat");
	}
	
}
