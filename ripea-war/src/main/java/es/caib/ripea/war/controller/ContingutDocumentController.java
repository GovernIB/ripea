/**
 * 
 */
package es.caib.ripea.war.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
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

import es.caib.ripea.core.api.dto.ArxiuFirmaDetallDto;
import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.DadaDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiTipoDocumentalEnumDto;
import es.caib.ripea.core.api.dto.DocumentTipusFirmaEnumDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.NtiOrigenEnumDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.api.service.MetaDadaService;
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.war.command.DocumentCommand;
import es.caib.ripea.war.command.DocumentCommand.CreateDigital;
import es.caib.ripea.war.command.DocumentCommand.CreateFirmaSeparada;
import es.caib.ripea.war.command.DocumentCommand.CreateFisic;
import es.caib.ripea.war.command.DocumentCommand.DocumentFisicOrigenEnum;
import es.caib.ripea.war.command.DocumentCommand.UpdateDigital;
import es.caib.ripea.war.command.DocumentCommand.UpdateFisic;
import es.caib.ripea.war.escaneig.EscaneigConfig;
import es.caib.ripea.war.escaneig.EscaneigHelper;
import es.caib.ripea.war.helper.AjaxHelper;
import es.caib.ripea.war.helper.AjaxHelper.AjaxFormResponse;
import es.caib.ripea.war.helper.ArxiuTemporalHelper;
import es.caib.ripea.war.helper.BeanGeneratorHelper;
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
	private MetaDadaService metaDadaService;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private ArxiuTemporalHelper arxiuTemporalHelper;
	@Autowired
	private BeanGeneratorHelper beanGeneratorHelper;


	@RequestMapping(value = "/{pareId}/document/new", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long pareId,
			Model model) throws ClassNotFoundException, IOException {
		return get(request, pareId, null, model);
	}
	@RequestMapping(value = "/{pareId}/document/{documentId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long pareId,
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

			omplirModelFormulari(
					request,
					command,
					pareId,
					model);
		}
		command.setEntitatId(entitatActual.getId());
		command.setPareId(pareId);
		command.setOrigen(DocumentFisicOrigenEnum.DISC);
		command.setTipusFirma(DocumentTipusFirmaEnumDto.ADJUNT);
		model.addAttribute(command);
		model.addAttribute("contingutId", pareId);
		model.addAttribute("documentId", documentId);
		return "contingutDocumentForm";
	}
	@RequestMapping(value = "/{pareId}/document/docNew", method = RequestMethod.POST)
	public String postNew(
			HttpServletRequest request,
			@PathVariable Long pareId,
			@Validated({CreateDigital.class, CreateFirmaSeparada.class}) DocumentCommand command,
			BindingResult bindingResult,
			Model model) throws IOException, ClassNotFoundException, NotFoundException, ValidationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (bindingResult.hasErrors()) {
			omplirModelFormulari(
					request,
					command,
					pareId,
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
					pareId,
					model);
			return "contingutDocumentForm";
		}
		
		
	}
	@RequestMapping(value = "/{contingutId}/document/docUpdate", method = RequestMethod.POST)
	public String postUpdate(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@Validated({UpdateDigital.class}) DocumentCommand command,
			BindingResult bindingResult,
			Model model) throws IOException, ClassNotFoundException, NotFoundException, ValidationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
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


	
	@RequestMapping(value = "/{contingutId}/document/{documentId}/mostraDetallSignants", method = RequestMethod.GET)
	@ResponseBody
	public AjaxFormResponse mostraDetallSignants(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@PathVariable Long documentId,
			Model model) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		List<ArxiuFirmaDetallDto> detallSignants = documentService.getDetallSignants(
				entitatActual.getId(),
				documentId,
				null);
		return AjaxHelper.generarAjaxFormOk(detallSignants);
	}

	@RequestMapping(value = "/{pareId}/document/{documentId}/descarregar", method = RequestMethod.GET)
	public String descarregar(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long pareId,
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
			return "redirect:../../contingut/" + pareId;
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
	
	@RequestMapping(value = "/{contingutId}/metaDocument/{metaDocumentId}/dadesnti", method = RequestMethod.GET)
	@ResponseBody
	public MetaDocumentDto metaDocumentDadesNti(
			HttpServletRequest request,
			HttpServletResponse response,
			Model model,
			@PathVariable Long contingutId,
			@PathVariable Long metaDocumentId) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		MetaDocumentDto document = metaDocumentService.getDadesNti(
				entitatActual.getId(), 
				contingutId, 
				metaDocumentId);
		return document;
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
			Model model) throws NotFoundException, ValidationException, IOException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		//FitxerDto fitxer = null;
		List<DadaDto> dades = new ArrayList<DadaDto>();
		Map<String, Object> valors = new HashMap<String, Object>();

		if (command.getId() == null) {
			DocumentDto document = documentService.create(
					entitatActual.getId(),
					command.getPareId(),
					DocumentCommand.asDto(command));
			//Valor per defecte d'algunes metadades
			List<MetaDadaDto> metadades = metaDadaService.findByNode(
					entitatActual.getId(), 
					document.getId());
			for (MetaDadaDto metadada : metadades) {
				DadaDto dada = new DadaDto();
				dada.setMetaDada(metadada);
				dada.setValor(metadada.getValor());
				dades.add(dada);
			}
			Object dadesCommand = beanGeneratorHelper.generarCommandDadesNode(
					entitatActual.getId(),
					document.getId(),
					dades);
			for (DadaDto dada: dades) {
				MetaDadaDto metaDada = metaDadaService.findById(
						entitatActual.getId(), 
						command.getMetaNodeId(),
						dada.getMetaDada().getId());
				Object valor = PropertyUtils.getSimpleProperty(dadesCommand, metaDada.getCodi());
				if (valor != null && (!(valor instanceof String) || !((String) valor).isEmpty())) {
					valors.put(metaDada.getCodi(), valor);
				}
			}
			contingutService.dadaSave(
					entitatActual.getId(),
					document.getId(),
					valors);
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../../contingut/" + command.getPareId(),
					"document.controller.creat.ok");
		} else {
			documentService.update(
					entitatActual.getId(),
					DocumentCommand.asDto(command));
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
		model.addAttribute(
				"tipusFirmaOptions",
				EnumHelper.getOptionsForEnum(
						DocumentTipusFirmaEnumDto.class,
						"document.tipus.firma.enum."));
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
		String propertyEscanejarActiu = aplicacioService.propertyFindByNom("es.caib.ripea.document.nou.escanejar.actiu");
		model.addAttribute(
				"escanejarActiu",
				(propertyEscanejarActiu == null) ? false : new Boolean(propertyEscanejarActiu));
	}


}
