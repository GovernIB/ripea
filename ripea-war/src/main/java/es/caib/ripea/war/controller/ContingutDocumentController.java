/**
 * 
 */
package es.caib.ripea.war.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.http.MediaType;
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
import es.caib.ripea.core.api.dto.DigitalitzacioResultatDto;
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
import es.caib.ripea.core.api.service.DigitalitzacioService;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.api.service.MetaDadaService;
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.war.command.DocumentCommand;
import es.caib.ripea.war.command.DocumentCommand.CreateDigital;
import es.caib.ripea.war.command.DocumentCommand.CreateFirmaSeparada;
import es.caib.ripea.war.command.DocumentCommand.DocumentFisicOrigenEnum;
import es.caib.ripea.war.command.DocumentCommand.UpdateDigital;
import es.caib.ripea.war.command.DocumentConcatenatCommand;
import es.caib.ripea.war.helper.AjaxHelper;
import es.caib.ripea.war.helper.AjaxHelper.AjaxFormResponse;
import es.caib.ripea.war.helper.ArxiuTemporalHelper;
import es.caib.ripea.war.helper.BeanGeneratorHelper;
import es.caib.ripea.war.helper.DocumentHelper;
import es.caib.ripea.war.helper.EnumHelper;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;

/**
 * Controlador per al manteniment de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/contingut")
public class ContingutDocumentController extends BaseUserController {

	private static final String SESSION_ATTRIBUTE_SELECCIO = "ContingutDocumentController.session.seleccio";
	private static final String SESSION_ATTRIBUTE_ORDRE = "ContingutDocumentController.session.ordre";
	private static final String SESSION_ATTRIBUTE_ENTREGA_POSTAL = "ContingutDocumentController.session.entregaPostal";
	private static final String SESSION_ATTRIBUTE_RETURN_SCANNED = "DigitalitzacioController.session.scanned";
	private static final String SESSION_ATTRIBUTE_RETURN_SIGNED = "DigitalitzacioController.session.signed";
	private static final String SESSION_ATTRIBUTE_RETURN_IDTRANSACCIO = "DigitalitzacioController.session.idTransaccio";
	
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
	@Autowired 
	private DocumentHelper documentHelper;
	@Autowired
	private DigitalitzacioService digitalitzacioService;
	
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
					null,
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
					null,
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

		//Recuperar document escanejat
		if (command.getOrigen().equals(DocumentFisicOrigenEnum.ESCANER)) {
			String idTransaccio = (String) RequestSessionHelper.obtenirObjecteSessio(
					request,
					SESSION_ATTRIBUTE_RETURN_IDTRANSACCIO);
			boolean returnScannedFile = (boolean) RequestSessionHelper.obtenirObjecteSessio(
					request,
					SESSION_ATTRIBUTE_RETURN_SCANNED);
			boolean returnSignedFile = (boolean) RequestSessionHelper.obtenirObjecteSessio(
					request,
					SESSION_ATTRIBUTE_RETURN_SIGNED);
			
			DigitalitzacioResultatDto resultat = digitalitzacioService.recuperarResultat(
					idTransaccio, 
					returnScannedFile, 
					returnSignedFile);
			
			System.out.println(resultat);
		}
		
		
		if (bindingResult.hasErrors()) {
			omplirModelFormulari(
					request,
					command,
					null,
					pareId,
					model);
			model.addAttribute("contingutId", pareId);
			return "contingutDocumentForm";
		}
		try {
			return createUpdateDocument(
					request,
					command,
					null,
					false,
					true);
		} catch (Exception exception) {
			MissatgesHelper.error(request, exception.getMessage());
			omplirModelFormulari(
					request,
					command,
					null,
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
					null,
					contingutId,
					model);
			return "contingutDocumentForm";
		}
		try {
			return createUpdateDocument(
					request,
					command,
					null,
					false,
					true);
		} catch (Exception exception) {
			MissatgesHelper.error(request, exception.getMessage());
			omplirModelFormulari(
					request,
					command,
					null,
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
	
	@RequestMapping(value = "/{pareId}/descarregarMultiples", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public void descarregarMultiple(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long pareId) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		ContingutDto expedient = contingutService.findAmbIdUser(
				entitatActual.getId(),
				pareId,
				true,
				false);
		
		byte[] reportContent = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		@SuppressWarnings("unchecked")
		Set<Long> docsIdx = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		
		documentHelper.generarFitxerZip(
				entitatActual.getId(),
				documentService, 
				contingutService,
				entitatActual, 
				null,
				docsIdx,
				expedient,
				baos);
		
		reportContent = baos.toByteArray();
		response.setHeader("Content-Disposition", "attachment; filename=" + expedient.getNom().replaceAll(" ", "_") + ".zip");
		response.getOutputStream().write(reportContent);
		response.getOutputStream().flush();
	}
	
	@RequestMapping(value = "/{contingutId}/notificar", method = RequestMethod.GET)
	public String concatenar(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) throws ClassNotFoundException, IOException, NotFoundException, ValidationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		Map<String, Long> ordre = new LinkedHashMap<String, Long>();
		boolean totsFirmats = true; 
		boolean totsDocuments = true;
		boolean mostrarTextLoading = true;
		
		RequestSessionHelper.actualitzarObjecteSessio(
				request,
				SESSION_ATTRIBUTE_ENTREGA_POSTAL,
				mostrarTextLoading);
		
		ContingutDto contingut = contingutService.findAmbIdUser(
				entitatActual.getId(),
				contingutId,
				true,
				false);
		
		List<DocumentDto> documents = new ArrayList<DocumentDto>();
		@SuppressWarnings("unchecked")
		Set<Long> docsIdx = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		
		for (Long docId: docsIdx) {
			DocumentDto document = null;
			ContingutDto contingutDoc = contingutService.findAmbIdUser(
					entitatActual.getId(),
					docId,
					true,
					false);
			
			if (contingutDoc instanceof DocumentDto) {
				document = (DocumentDto) contingutDoc;
				if (document.isFirmat() || document.isCustodiat())
					documents.add(document);
				else
					totsFirmats = false;
			} else {
				totsDocuments = false;
				break;
			}
		}
		
		if (docsIdx != null) {
			for (Long id: docsIdx) {
				ordre.put("document-" + id, id);
			}
		}

		RequestSessionHelper.actualitzarObjecteSessio(
				request,
				SESSION_ATTRIBUTE_ORDRE,
				ordre);
		
		if (totsDocuments && totsFirmats) {
			model.addAttribute("documents", documents);
			model.addAttribute("contingut", contingut);
			boolean entregaPostal = true;
			
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_ENTREGA_POSTAL,
					entregaPostal);
			
			MissatgesHelper.warning(
					request, 
					getMessage(
							request, 
							"contingut.document.form.titol.concatenacio.info"));
			return "contingutConcatenacioForm";
		} else {
			DocumentConcatenatCommand command = new DocumentConcatenatCommand();
			command.setPareId(contingutId);
			boolean entregaPostal = false;
			
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_ENTREGA_POSTAL,
					entregaPostal);
			
			documentHelper.generarFitxerZip(
					entitatActual.getId(),
					documentService, 
					contingutService,
					entitatActual, 
					command,
					docsIdx,
					contingut,
					null);
			MissatgesHelper.warning(
					request, 
					getMessage(
							request, 
							"contingut.document.form.titol.compresio.info"));
			return createUpdateDocument(
					request,
					null,
					command,
					true,
					false);
		}
	}
	
	@RequestMapping(value = "/{pareId}/notificarForm", method = RequestMethod.GET)
	public String concatenarDocuments(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long pareId,
			Model model) throws IOException, ClassNotFoundException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		@SuppressWarnings("unchecked")
		Map<String, Long> ordre = (Map<String, Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_ORDRE);
		DocumentConcatenatCommand command = new DocumentConcatenatCommand();
		command.setPareId(pareId);
		
		documentHelper.concatenarDocuments(
				entitatActual.getId(),
				documentService, 
				contingutService,
				entitatActual, 
				command,
				ordre);
		
		try {
			return createUpdateDocument(
					request,
					null,
					command,
					true,
					false);
		} catch (Exception exception) {
			return getModalControllerReturnValueErrorMessageText(
					request, 
					null, 
					exception.getMessage());
		}
	}

	@RequestMapping(value = "/{pareId}/ordre", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Long> ordre(
			HttpServletRequest request,
			@PathVariable Long pareId,
			@RequestParam(value="ordreId[]", required = false) Long[] ordreId) {
		Map<String, Long> ordre = new LinkedHashMap<String, Long>();
		
		if (ordreId != null) {
			for (Long id: ordreId) {
				ordre.put("document-" + id, id);
			}
		}

		RequestSessionHelper.actualitzarObjecteSessio(
				request,
				SESSION_ATTRIBUTE_ORDRE,
				ordre);
		return ordre;
	}

	@RequestMapping(value = "/{pareId}/inicialitzar/seleccio", method = RequestMethod.GET)
	@ResponseBody
	public int inicialitzar(
			HttpServletRequest request,
			@PathVariable Long pareId) {
		Set<Long> seleccio = new HashSet<Long>();
		
		RequestSessionHelper.actualitzarObjecteSessio(
			request,
			SESSION_ATTRIBUTE_SELECCIO,
			seleccio);
		
		return seleccio.size();
	}
	
	@RequestMapping(value = "/{pareId}/select", method = RequestMethod.GET)
	@ResponseBody
	public int select(
			HttpServletRequest request,
			@PathVariable Long pareId,
			@RequestParam(value="docsIdx[]", required = false) Long[] docsIdx) {
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO,
					seleccio);
		}
		if (docsIdx != null) {
			for (Long id: docsIdx) {
				if(!seleccio.contains(id)) {
					seleccio.add(id);	
				}
			}
		}
		return seleccio.size();
	}

	@RequestMapping(value = "/{pareId}/deselect", method = RequestMethod.GET)
	@ResponseBody
	public int deselect(
			HttpServletRequest request,
			@PathVariable Long pareId,
			@RequestParam(value="docsIdx[]", required = false) Long[] docsIdx) {
		List<Long> idxRemove = new ArrayList<Long>();
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO,
					seleccio);
		}
		if (docsIdx != null) {
			List<Long> docsIdxList = Arrays.asList(docsIdx);
			for (Long id: seleccio) {
				if (!docsIdxList.contains(id))
					idxRemove.add(id);
			}
		} else {
			seleccio.clear();
		}
		
		if (!idxRemove.isEmpty())
			seleccio.removeAll(idxRemove);
		
		return seleccio.size();
	}
	
	@RequestMapping(value = "/{pareId}/comprovarContingut", method = RequestMethod.GET)
	@ResponseBody
	public boolean comprovarContingut(
			HttpServletRequest request,
			@PathVariable Long pareId,
			@RequestParam(value="docsIdx[]", required = false) Long[] docsIdx) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		boolean totPdfFirmat = true;
		
		if (docsIdx != null) {
			for (Long docId: docsIdx) {
				ContingutDto contingut = contingutService.findAmbIdUser(
						entitatActual.getId(),
						docId,
						true,
						false);
				if (contingut instanceof DocumentDto) {
					DocumentDto document = (DocumentDto) contingut;
					if ((!document.isFirmat() || document.isCustodiat()) 
							&& (document.isFirmat() || !document.isCustodiat())) {
						totPdfFirmat = false;
						break;
					}
				}
			}
		}
		return totPdfFirmat;
	}
	
	@RequestMapping(value = "/{pareId}/comprovarContingut/{contingutId}", method = RequestMethod.GET)
	@ResponseBody
	public boolean comprovarContingut(
			HttpServletRequest request,
			@PathVariable Long pareId,
			@PathVariable Long contingutId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		boolean isDocument = true;
		
		ContingutDto contingut = contingutService.findAmbIdUser(
				entitatActual.getId(),
				contingutId,
				true,
				false);
		if (!contingut.isCarpeta())
			isDocument = true;
		else
			isDocument = false;
		
		return isDocument;
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
	    binder.registerCustomEditor(
		        Long[].class,
		        new StringArrayPropertyEditor(null)); 
	}



	private String createUpdateDocument(
			HttpServletRequest request,
			DocumentCommand command,
			DocumentConcatenatCommand commandConc,
			boolean notificar,
			boolean comprovarMetaExpedient) throws NotFoundException, ValidationException, IOException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		//FitxerDto fitxer = null;
		List<DadaDto> dades = new ArrayList<DadaDto>();
		Map<String, Object> valors = new HashMap<String, Object>();

		Long pareId = commandConc == null ? command.getPareId() : commandConc.getPareId();
		if (commandConc == null ? command.getId() == null : commandConc.getId() == null) {
			DocumentDto document = documentService.create(
					entitatActual.getId(),
					pareId,
					commandConc == null ? DocumentCommand.asDto(command) : DocumentConcatenatCommand.asDto(commandConc),
					comprovarMetaExpedient);
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
						commandConc == null ? command.getMetaNodeId() : commandConc.getMetaNodeId(),
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
			
			if (!notificar) {
				return getModalControllerReturnValueSuccess(
						request,
						"redirect:../../contingut/" + pareId,
						"document.controller.creat.ok");
			} else {
				modalUrlTancar();
				return "redirect:../../document/" + document.getId() + "/notificar";
			}
		} else {
			documentService.update(
					entitatActual.getId(),
					commandConc == null ? DocumentCommand.asDto(command) : DocumentConcatenatCommand.asDto(commandConc),
					comprovarMetaExpedient);
			
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../contingut/" + pareId,
					"document.controller.modificat.ok");
		}
	}

	private void omplirModelFormulariAmbDocument(
			HttpServletRequest request,
			DocumentCommand command,
			DocumentConcatenatCommand commandConc,
			Long contingutId,
			Model model,
			DocumentDto document) throws ClassNotFoundException, IOException {
		if(document.getFitxerNom() != null) {
			model.addAttribute("nomDocument", document.getFitxerNom());
		}
		omplirModelFormulari(request, command, commandConc, contingutId, model);
	}
	
	private void omplirModelFormulari(
			HttpServletRequest request,
			DocumentCommand command,
			DocumentConcatenatCommand commandConc,
			Long contingutId,
			Model model) throws ClassNotFoundException, IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (commandConc == null ? command.getId() == null: commandConc.getId() == null) {
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
							commandConc == null ? command.getId() : commandConc.getId()));
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
		String tempId = commandConc == null ? command.getEscanejatTempId() : commandConc.getEscanejatTempId();
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
