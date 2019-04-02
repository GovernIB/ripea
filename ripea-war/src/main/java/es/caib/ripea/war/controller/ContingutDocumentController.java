/**
 * 
 */
package es.caib.ripea.war.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fundaciobit.plugins.scanweb.api.IScanWebPlugin;
import org.fundaciobit.plugins.scanweb.api.ScanWebMode;
import org.fundaciobit.plugins.scanweb.api.ScanWebStatus;
import org.fundaciobit.plugins.scanweb.api.ScannedDocument;
import org.fundaciobit.plugins.utils.Metadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiTipoDocumentalEnumDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.NtiOrigenEnumDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.war.command.DocumentCommand;
import es.caib.ripea.war.command.DocumentCommand.CreateDigital;
import es.caib.ripea.war.command.DocumentCommand.CreateFisic;
import es.caib.ripea.war.command.DocumentCommand.DocumentFisicOrigenEnum;
import es.caib.ripea.war.command.DocumentCommand.UpdateDigital;
import es.caib.ripea.war.command.DocumentCommand.UpdateFisic;
import es.caib.ripea.war.escaneig.EscaneigConfig;
import es.caib.ripea.war.escaneig.EscaneigHelper;
import es.caib.ripea.war.helper.AjaxHelper;
import es.caib.ripea.war.helper.AjaxHelper.AjaxFormResponse;
import es.caib.ripea.war.helper.ArxiuTemporalHelper;
import es.caib.ripea.war.helper.EnumHelper;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.ModalHelper;

/**
 * Controlador per al manteniment de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/contingut")
public class ContingutDocumentController extends BaseUserController {

	@Autowired
	private ServletContext servletContext;

	@Autowired
	private ContingutService contingutService;
	@Autowired
	private DocumentService documentService;
	@Autowired
	private MetaDocumentService metaDocumentService;

	@Autowired
	private EscaneigHelper escaneigHelper;
	@Autowired
	private ArxiuTemporalHelper arxiuTemporalHelper;



	@RequestMapping(value = "/{contingutId}/document/new", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) throws ClassNotFoundException, IOException {
		return get(request, contingutId, null, model);
	}
	@RequestMapping(value = "/{contingutId}/document/{documentId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@PathVariable Long documentId,
			Model model) throws ClassNotFoundException, IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DocumentDto document = null;
		if (documentId != null) {
			document = documentService.findById(
					entitatActual.getId(),
					documentId);
		}
		DocumentCommand command = null;
		if (document != null) {
			command = DocumentCommand.asCommand(document);
			omplirModelFormulariAmbDocument(
					request,
					command,
					documentId,
					model,
					document);
		} else {
			command = new DocumentCommand();
			Date ara = new Date();
			command.setData(ara);
			command.setDataCaptura(ara);
			command.setNtiOrigen(NtiOrigenEnumDto.O0);
			command.setNtiEstadoElaboracion(DocumentNtiEstadoElaboracionEnumDto.EE01);
			command.setNtiTipoDocumental(DocumentNtiTipoDocumentalEnumDto.TD99);
			ContingutDto contingut = contingutService.findAmbIdUser(
					entitatActual.getId(),
					contingutId,
					false,
					false);
			if (contingut.isExpedient()) {
				command.setNtiOrgano(((ExpedientDto)contingut).getNtiOrgano());
			} else if (contingut.getExpedientPare() != null) {
				command.setNtiOrgano(contingut.getExpedientPare().getNtiOrgano());
			}
			omplirModelFormulari(
					request,
					command,
					contingutId,
					model);
		}
		model.addAttribute("contingutId", contingutId);
		model.addAttribute("documentId", documentId);
		command.setEntitatId(entitatActual.getId());
		command.setPareId(contingutId);
		command.setOrigen(DocumentFisicOrigenEnum.DISC);
		model.addAttribute(command);
		return "contingutDocumentForm";
	}
	@RequestMapping(value = "/{contingutId}/document/digital/new", method = RequestMethod.POST)
	public String postDigitalNew(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@Validated({CreateDigital.class}) DocumentCommand command,
			BindingResult bindingResult,
			Model model) throws IOException, ClassNotFoundException {
		if (bindingResult.hasErrors()) {
			omplirModelFormulari(
					request,
					command,
					contingutId,
					model);
			return "contingutDocumentForm";
		}
		try {
			return createUpdateDocument(
					request,
					command,
					bindingResult,
					model);
		} catch (Exception exception) {
			MissatgesHelper.error(request, exception.getMessage());
			omplirModelFormulari(
					request,
					command,
					contingutId,
					model);
			return "contingutDocumentForm";
		}
		
		
	}
	@RequestMapping(value = "/{contingutId}/document/digital/update", method = RequestMethod.POST)
	public String postDigitalUpdate(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@Validated({UpdateDigital.class}) DocumentCommand command,
			BindingResult bindingResult,
			Model model) throws IOException, ClassNotFoundException {
		if (bindingResult.hasErrors()) {
			omplirModelFormulari(
					request,
					command,
					contingutId,
					model);
			return "contingutDocumentForm";
		}
		try {
			return createUpdateDocument(
					request,
					command,
					bindingResult,
					model);
		} catch (Exception exception) {
			MissatgesHelper.error(request, exception.getMessage());
			omplirModelFormulari(
					request,
					command,
					contingutId,
					model);
			return "contingutDocumentForm";
		}
	}
	@RequestMapping(value = "/{contingutId}/document/fisic/new", method = RequestMethod.POST)
	public String postFisicNew(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@Validated({CreateFisic.class}) DocumentCommand command,
			BindingResult bindingResult,
			Model model) throws IOException, ClassNotFoundException {
		if (bindingResult.hasErrors()) {
			omplirModelFormulari(
					request,
					command,
					contingutId,
					model);
			return "contingutDocumentForm";
		}
		return createUpdateDocument(
				request,
				command,
				bindingResult,
				model);
	}
	@RequestMapping(value = "/{contingutId}/document/fisic/update", method = RequestMethod.POST)
	public String postFisicUpdate(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@Validated({UpdateFisic.class}) DocumentCommand command,
			BindingResult bindingResult,
			Model model) throws IOException, ClassNotFoundException {
		if (bindingResult.hasErrors()) {
			omplirModelFormulari(
					request,
					command,
					contingutId,
					model);
			return "contingutDocumentForm";
		}
		return createUpdateDocument(
				request,
				command,
				bindingResult,
				model);
	}

	@RequestMapping(value = "/{contingutId}/document/escaneig/inici", method = RequestMethod.POST)
	public String escaneigInici(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			DocumentCommand command,
			BindingResult bindingResult,
			Model model) throws IOException, ClassNotFoundException {
		String modalStr = (ModalHelper.isModal(request)) ? "/modal" : "";
		/* ScanTypes:
		 *   IScanWebPlugin.SCANTYPE_PDF
		 *   IScanWebPlugin.SCANTYPE_JPG
		 *   IScanWebPlugin.SCANTYPE_PNG
		 *   IScanWebPlugin.SCANTYPE_GIF
		 *   IScanWebPlugin.SCANTYPE_TIFF
		 */
		String scanType = IScanWebPlugin.SCANTYPE_PDF;
		/* Flags:
		 *   IScanWebPlugin.FLAG_NON_SIGNED
		 *   IScanWebPlugin.FLAG_SIGNED
		 *   IScanWebPlugin.FLAG_TIMESTAMP
		 *   IScanWebPlugin.FLAG_CSV
		 */
		Set<String> flags = new HashSet<String>();
		flags.add(IScanWebPlugin.FLAG_NON_SIGNED);
		/* ScanWebMode:
		 *   ScanWebMode.SYNCHRONOUS
		 *   ScanWebMode.ASYNCHRONOUS
		 */
		ScanWebMode mode = ScanWebMode.SYNCHRONOUS;
		List<Metadata> metadades = new ArrayList<Metadata>();
		String procesEscaneigUrl = modalStr + escaneigHelper.iniciarEscaneig(
				request,
				scanType,
				flags,
				metadades,
				mode,
				LocaleContextHolder.getLocale().getLanguage(),
				command,
				modalStr + "/contingut/" + contingutId + "/document/escaneig/final");
		return "redirect:" + procesEscaneigUrl;
	}

	@RequestMapping(value = "/{contingutId}/document/escaneig/final")
	public String escaneigFinal(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@RequestParam("scanWebId") long scanWebId,
			Model model) throws IOException, ClassNotFoundException {
		EscaneigConfig swc = escaneigHelper.getScanWebConfig(
				request,
				scanWebId);
		DocumentCommand command = swc.getCommand();
		ScanWebStatus status = swc.getStatus();
		switch (status.getStatus()) {
		//case ScanWebStatus.STATUS_IN_PROGRESS:
		case ScanWebStatus.STATUS_FINAL_OK:
			List<ScannedDocument> listDocs = swc.getScannedFiles();
			if (listDocs.size() == 0) {
				MissatgesHelper.warning(
						request,
						getMessage(
								request, 
								"document.controller.escaneig.no.document"));
			} else {
				ScannedDocument escanejat = listDocs.get(0);
				if (escanejat.getScannedPlainFile() == null) {
					MissatgesHelper.warning(
							request,
							getMessage(
									request, 
									"document.controller.escaneig.no.doc.plain"));
				} else {
					FitxerDto fitxer = new FitxerDto();
					fitxer.setNom(
							escanejat.getScannedPlainFile().getName());
					fitxer.setContentType(
							escanejat.getScannedPlainFile().getMime());
					fitxer.setContingut(
							escanejat.getScannedPlainFile().getData());
					String escanejatTempId = arxiuTemporalHelper.crearFitxer(
							servletContext,
							fitxer);
					command.setEscanejatTempId(escanejatTempId);
				}
			}
			break;
		case ScanWebStatus.STATUS_FINAL_ERROR:
			MissatgesHelper.error(
					request,
					getMessage(
							request, 
							"document.controller.escaneig.final.error",
							new Object[] {status.getErrorMsg()}));
			break;
		case ScanWebStatus.STATUS_CANCELLED:
			MissatgesHelper.warning(
					request,
					getMessage(
							request, 
							"document.controller.escaneig.final.cancel"));
			break;
		default:
			MissatgesHelper.warning(
					request,
					getMessage(
							request, 
							"document.controller.escaneig.final.desconegut"));
		
		}
		escaneigHelper.closeScanWebProcess(
				request,
				swc);
		model.addAttribute(command);
		omplirModelFormulari(
				request,
				command,
				contingutId,
				model);
		return "contingutDocumentForm";
	}
	
	@RequestMapping(value = "/{contingutId}/document/{documentId}/mostraDetallSignants", method = RequestMethod.GET)
	@ResponseBody
	public AjaxFormResponse mostraDetallSignants(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@PathVariable Long documentId,
			Model model) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutDto contingut = contingutService.findAmbIdUser(
				entitatActual.getId(),
				documentId,
				true,
				false);
		if (contingut instanceof DocumentDto) {
			FitxerDto fitxer = documentService.descarregar(
					entitatActual.getId(),
					documentId,
					null);
			Object objecte = contingutService.getDetallSignants(fitxer.getContingut());
			return AjaxHelper.generarAjaxFormOk(objecte);
		}
		return null;
	}

	@RequestMapping(value = "/{contingutId}/document/{documentId}/descarregar", method = RequestMethod.GET)
	public String descarregar(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long contingutId,
			@PathVariable Long documentId) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutDto contingut = contingutService.findAmbIdUser(
				entitatActual.getId(),
				documentId,
				true,
				false);
		if (contingut instanceof DocumentDto) {
			FitxerDto fitxer = documentService.descarregar(
					entitatActual.getId(),
					documentId,
					null);
			writeFileToResponse(
					fitxer.getNom(),
					fitxer.getContingut(),
					response);
			return null;
		}
		MissatgesHelper.error(
				request, 
				getMessage(
						request, 
						"document.controller.descarregar.error"));
		if (contingut.getPare() != null)
			return "redirect:../../contingut/" + contingutId;
		else
			return "redirect:../../expedient";
	}
	
	@RequestMapping(value = "/{contingutId}/document/{documentId}/descarregarImprimible", method = RequestMethod.GET)
	public String descarregarImprimible(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long contingutId,
			@PathVariable Long documentId) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutDto contingut = contingutService.findAmbIdUser(
				entitatActual.getId(),
				documentId,
				true,
				false);
		if (contingut instanceof DocumentDto) {
			FitxerDto fitxer = documentService.descarregarImprimible(
					entitatActual.getId(),
					documentId,
					null);
			writeFileToResponse(
					fitxer.getNom(),
					fitxer.getContingut(),
					response);
			return null;
		}
		MissatgesHelper.error(
				request, 
				getMessage(
						request, 
						"document.controller.descarregar.error"));
		if (contingut.getPare() != null)
			return "redirect:../../contingut/" + contingutId;
		else
			return "redirect:../../expedient";

	}
	
	@RequestMapping(value = "/{contingutId}/document/{documentId}/versio/{versio}/descarregar", method = RequestMethod.GET)
	public String descarregarVersio(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long contingutId,
			@PathVariable Long documentId,
			@PathVariable String versio) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		FitxerDto fitxer = documentService.descarregar(
				entitatActual.getId(),
				documentId,
				versio);
		writeFileToResponse(
				fitxer.getNom(),
				fitxer.getContingut(),
				response);
		return null;
	}

	@RequestMapping(value = "/{contingutId}/metaDocument/{metaDocumentId}", method = RequestMethod.GET)
	@ResponseBody
	public MetaDocumentDto metaDocumentInfo(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@PathVariable Long metaDocumentId) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		List<MetaDocumentDto> metaDocuments = metaDocumentService.findActiusPerCreacio(
				entitatActual.getId(),
				contingutId);
		for (MetaDocumentDto metaDocument: metaDocuments) {
			if (metaDocument.getId().equals(metaDocumentId))
				return metaDocument;
		}
		return null;
	}

	@RequestMapping(value = "/{contingutId}/metaDocument/{metaDocumentId}/plantilla", method = RequestMethod.GET)
	public String metaDocumentPlantilla(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long contingutId,
			@PathVariable Long metaDocumentId) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		FitxerDto plantilla = metaDocumentService.getPlantilla(
				entitatActual.getId(),
				contingutId,
				metaDocumentId);
		writeFileToResponse(
				plantilla.getNom(),
				plantilla.getContingut(),
				response);
		return null;
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}



	private String createUpdateDocument(
			HttpServletRequest request,
			DocumentCommand command,
			BindingResult bindingResult,
			Model model) throws NotFoundException, ValidationException, IOException, ClassNotFoundException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		FitxerDto fitxer = null;
		switch (command.getOrigen()) {
		case DISC:
			if (command.getArxiu() != null && !command.getArxiu().isEmpty()) {
				fitxer = new FitxerDto();
				fitxer.setNom(command.getArxiu().getOriginalFilename());
				fitxer.setContentType(command.getArxiu().getContentType());
				fitxer.setContingut(command.getArxiu().getBytes());
			}
			break;
		case ESCANER:
			if (command.getEscanejatTempId() != null && !command.getEscanejatTempId().isEmpty()) {
				fitxer = arxiuTemporalHelper.llegirFitxerAmbContingut(
						servletContext,
						command.getEscanejatTempId());
			}
			break;
		}
		if (command.getId() == null) {
			documentService.create(
					entitatActual.getId(),
					command.getPareId(),
					DocumentCommand.asDto(command),
					fitxer);
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../../contingut/" + command.getPareId(),
					"document.controller.creat.ok");
		} else {
			documentService.update(
					entitatActual.getId(),
					command.getId(),
					DocumentCommand.asDto(command),
					fitxer);
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../contingut/" + command.getPareId(),
					"document.controller.modificat.ok");
		}
	}

	private void omplirModelFormulariAmbDocument(
			HttpServletRequest request,
			DocumentCommand command,
			Long contingutId,
			Model model,
			DocumentDto document) throws ClassNotFoundException, IOException {
		if(document.getFitxerNom() != null) {
			model.addAttribute("nomDocument", document.getFitxerNom());
		}
		omplirModelFormulari(request, command, contingutId, model);
	}
	
	private void omplirModelFormulari(
			HttpServletRequest request,
			DocumentCommand command,
			Long contingutId,
			Model model) throws ClassNotFoundException, IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (command.getId() == null) {
			model.addAttribute(
					"metaDocuments",
					metaDocumentService.findActiusPerCreacio(
							entitatActual.getId(),
							contingutId));
		} else {
			model.addAttribute(
					"metaDocuments",
					metaDocumentService.findActiusPerModificacio(
							entitatActual.getId(),
							command.getId()));
		}
		model.addAttribute(
				"digitalOrigenOptions",
				EnumHelper.getOptionsForEnum(
						DocumentFisicOrigenEnum.class,
						"document.fisic.origen.enum."));
		String tempId = command.getEscanejatTempId();
		if (tempId != null) {
			model.addAttribute(
					"escanejat",
					arxiuTemporalHelper.llegirFitxerSenseContingut(
							servletContext,
							tempId));
		}
		model.addAttribute(
				"ntiOrigenOptions",
				EnumHelper.getOptionsForEnum(
						NtiOrigenEnumDto.class,
						"document.nti.origen.enum."));
		model.addAttribute(
				"ntiEstatElaboracioOptions",
				EnumHelper.getOptionsForEnum(
						DocumentNtiEstadoElaboracionEnumDto.class,
						"document.nti.estela.enum."));
		model.addAttribute(
				"ntiTipusDocumentalOptions",
				EnumHelper.getOptionsForEnum(
						DocumentNtiTipoDocumentalEnumDto.class,
						"document.nti.tipdoc.enum."));
	}


}
