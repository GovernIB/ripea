package es.caib.ripea.back.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import es.caib.ripea.back.command.DocumentCommand;
import es.caib.ripea.back.command.DocumentCommand.CreateDigitalZip;
import es.caib.ripea.back.command.ImportacioZipCommand;
import es.caib.ripea.back.command.ImportacioZipCommand.ProcessarZip;
import es.caib.ripea.back.helper.EnumHelper;
import es.caib.ripea.back.helper.MissatgesHelper;
import es.caib.ripea.back.helper.RolHelper;
import es.caib.ripea.service.intf.dto.DocumentDto;
import es.caib.ripea.service.intf.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.ProgresProcessamentZipDto;
import es.caib.ripea.service.intf.exception.ElementNotValidException;
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
	public String processarZip(
			HttpServletRequest request, 
			@PathVariable Long pareId,
			@ModelAttribute("command") @Validated({ProcessarZip.class}) ImportacioZipCommand command,
			BindingResult bindingResult,
			Model model) throws ClassNotFoundException, IOException {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		try {
			if (bindingResult.hasErrors()) {
				omplirModelFormulari(
						model, 
						pareId, 
						command,
						entitatActual);
				return "contingutZipImportacioForm";
			}
			
			List<DocumentCommand> documents = new ArrayList<DocumentCommand>();
			try {
				List<DocumentDto> documentsDto = documentService.extreureDocumentsZip(
						command.getArxiuZip().getInputStream(), 
						command.getMetaExpedientId(),
						command.getPareId(), 
						entitatActual);
				
				documents = documentsDto.stream()
					    .map(DocumentCommand::asCommand)
					    .collect(Collectors.toList());
			} catch (Exception e) {
				omplirModelFormulari(
						model, 
						pareId, 
						command,
						entitatActual);
				
				if (e instanceof ElementNotValidException) {
					MissatgesHelper.error(request,  e.getMessage());
		        	return "contingutZipImportacioForm";
				}
				
				throw e;	
			}
			command.setDocuments(documents);
			omplirModelFormulari(
					model, 
					pareId, 
					command,
					entitatActual);
			
			return "contingutZipImportacioForm";
		} catch (Exception ex) {
			omplirModelFormulari(
					model, 
					pareId, 
					command,
					entitatActual);
			MissatgesHelper.error(request, ex.getMessage(), ex);
			return "contingutZipImportacioForm";
		}
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

	@RequestMapping(value = "/{pareId}/zip/importacio/new", method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@PathVariable Long pareId,
			@ModelAttribute("command") @Validated({CreateDigitalZip.class}) ImportacioZipCommand command,
			BindingResult bindingResult,
			Model model) throws ClassNotFoundException, IOException {
		StringBuilder documentsCreats = new StringBuilder();
		StringBuilder documentsAmbError = new StringBuilder();
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		try {
			if (bindingResult.hasErrors()) {
				omplirModelFormulari(
						model, 
						pareId, 
						command, 
						entitatActual);
				return "contingutZipImportacioForm";
			}
			
			for (DocumentCommand documentCommand : command.getDocuments()) {
				byte[] fitxerContingut = documentService.obtenirContingutFitxerZip(documentCommand.getFitxerNom());
 				documentCommand.setFitxerContingut(fitxerContingut);
				
 				try {
					DocumentDto document = documentService.crearAmbCarpetes(
							entitatActual.getId(),
							pareId,
							DocumentCommand.asDto(documentCommand),
							false, 
							RolHelper.getRolActual(request), 
							command.getTascaId());
					
					documentsCreats.append(" - " + document.getNom());
					documentsCreats.append("<br>");
 				} catch (Exception ex) {
 					documentsAmbError.append(" - " + documentCommand.getNom() + (ex.getMessage() != null ? " - " + ex.getMessage() : ""));
 					documentsAmbError.append("<br>");
 					
 					logger.error("Hi ha hagut un error creant un dels documents del fitxe zip", ex);
				}
				
			}
			
			if (documentsAmbError.length() != 0) {
				MissatgesHelper.error(
						request,
						getMessage(
								request,
								"document.controller.multiple.creat.ko",
								new Object[] { documentsAmbError.toString() }));
			}
			
			if (documentsCreats.length() != 0) {
				MissatgesHelper.success(
						request,
						getMessage(
								request,
								"document.controller.multiple.creat.ok",
								new Object[] { documentsCreats.toString() }));
			}
			
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../contingut/" + pareId,
					null);
		} catch (Exception ex) {
			omplirModelFormulari(
					model, 
					pareId, 
					command, 
					entitatActual);
			return getModalControllerReturnValueError(
					request,
					"redirect:../../contingut/" + pareId + "/zip/importacio/new",
					"document.controller.multiple.ko",
					ex);
		}
	}
	
    @RequestMapping(value = "/zip/importacio/plantilla", method = RequestMethod.GET)
    @ResponseBody
    public void getModelDadesCarregaMassiuCSV(
    		HttpServletResponse response) throws IOException {

        response.setHeader("Set-cookie", "fileDownload=true; path=/");
        try {
        	byte[] contingutPlantilla = documentService.getPlantillaImportacioZip();
        	
            writeFileToResponse(
            		"model_dades_importacio_zip.csv", 
            		contingutPlantilla, 
            		response);
        } catch (Exception ex) {
        	logger.debug("Error al obtenir la plantilla del model de dades CSV per a la importació ZIP", ex);
        }
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
	
	private static final Logger logger = LoggerFactory.getLogger(ContingutZipImportacioController.class);

}
