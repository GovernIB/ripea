package es.caib.ripea.war.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.ElementTipusEnumDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExecucioMassivaContingutDto;
import es.caib.ripea.core.api.dto.ExecucioMassivaDto;
import es.caib.ripea.core.api.dto.ExecucioMassivaEstatDto;
import es.caib.ripea.core.api.dto.ExecucioMassivaTipusDto;
import es.caib.ripea.core.api.dto.FirmaResultatDto;
import es.caib.ripea.core.api.dto.FirmaResultatDto.FirmaSignatureStatus;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.ResultEnumDto;
import es.caib.ripea.core.api.dto.StatusEnumDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.api.service.ExecucioMassivaService;
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.command.ContingutMassiuFiltreCommand;
import es.caib.ripea.war.command.FirmaSimpleWebCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.EntitatHelper;
import es.caib.ripea.war.helper.ExceptionHelper;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;
import es.caib.ripea.war.helper.RolHelper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/massiu/firmasimpleweb")
public class DocumentMassiuFirmaWebController extends BaseUserOAdminOOrganController {
	
	public static final String SESSION_ATTRIBUTE_FILTRE = "DocumentMassiuFirmaWebController.session.filtre";
	private static final String SESSION_ATTRIBUTE_SELECCIO = "DocumentMassiuFirmaWebController.session.seleccio";
	private static final String SESSION_ATTRIBUTE_DATA_INICI = "DocumentMassiuFirmaWebController.session.data.inici";

	@Autowired private ContingutService contingutService;
	@Autowired private MetaExpedientService metaExpedientService;
	@Autowired private ExecucioMassivaService execucioMassivaService;
	@Autowired private AplicacioService aplicacioService;
	@Autowired private MetaDocumentService metaDocumentService;
	@Autowired private DocumentService documentService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutMassiuFiltreCommand filtreCommand = getFiltreCommand(request);

		model.addAttribute(
				"seleccio",
				RequestSessionHelper.obtenirObjecteSessio(
						request,
						SESSION_ATTRIBUTE_SELECCIO));

		model.addAttribute(filtreCommand);		
		model.addAttribute(
				"metaExpedients",
				metaExpedientService.findActiusAmbEntitatPerModificacio(entitatActual.getId(), RolHelper.getRolActual(request)));
		
		
		if (filtreCommand.getMetaExpedientId() != null) {
			model.addAttribute(
					"metaDocuments",
					 metaDocumentService.findByMetaExpedientAndFirmaSimpleWebActiva(entitatActual.getId(), filtreCommand.getMetaExpedientId()));
		}
		

		return "firmaSimpleWebMassiuList";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid ContingutMassiuFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model,
			@RequestParam(value = "accio", required = false) String accio) {
		
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE);
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO);

		} else {
			if (!bindingResult.hasErrors()) {
				RequestSessionHelper.actualitzarObjecteSessio(
						request,
						SESSION_ATTRIBUTE_FILTRE,
						filtreCommand);
			}
		}
		
		return "redirect:/massiu/firmasimpleweb";
	}
	
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutMassiuFiltreCommand contingutMassiuFiltreCommand = getFiltreCommand(request);

		try {
			return DatatablesHelper.getDatatableResponse(
					request,
					 contingutService.findDocumentsPerFirmaSimpleWebMassiu(
								entitatActual.getId(), 
								ContingutMassiuFiltreCommand.asDto(contingutMassiuFiltreCommand),
								DatatablesHelper.getPaginacioDtoFromRequest(request), 
								RolHelper.getRolActual(request),
								ResultEnumDto.PAGE).getPagina(),
					 "id",
					 SESSION_ATTRIBUTE_SELECCIO);
		} catch (Exception e) {
			throw e;
		}
		
	}



	@RequestMapping(value = "/select", method = RequestMethod.GET)
	@ResponseBody
	public int select(
			HttpServletRequest request,
			@RequestParam(value="ids[]", required = false) Long[] ids) {
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
		if (ids != null) {
			for (Long id: ids) {
				seleccio.add(id);
			}
		} else {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			ContingutMassiuFiltreCommand filtreCommand = getFiltreCommand(request);
			
			seleccio.addAll(
					 contingutService.findDocumentsPerFirmaSimpleWebMassiu(
								entitatActual.getId(), 
								ContingutMassiuFiltreCommand.asDto(filtreCommand),
								null, 
								RolHelper.getRolActual(request),
								ResultEnumDto.IDS).getIds());
		}
		return seleccio.size();
	}

	@RequestMapping(value = "/deselect", method = RequestMethod.GET)
	@ResponseBody
	public int deselect(
			HttpServletRequest request,
			@RequestParam(value="ids[]", required = false) Long[] ids) {
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
		if (ids != null) {
			for (Long id: ids) {
				seleccio.remove(id);
			}
		} else {
			seleccio.clear();
		}
		return seleccio.size();
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/firmaSimpleWeb", method = RequestMethod.GET)
	public String tancarMassiuGet(
			HttpServletRequest request,
			Model model) {
		
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		
		if (seleccio == null || seleccio.isEmpty()) {
			return getModalControllerReturnValueError(
					request,
					"redirect:/massiu/firmasimpleweb",
					"accio.massiva.seleccio.buida",
					null);
		}
		
		FirmaSimpleWebCommand command = new FirmaSimpleWebCommand();
		model.addAttribute(command);
		
		return "firmaSimpleWebMassiuForm";
	}
	
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/firmaSimpleWebStart", method = RequestMethod.POST)
	public String canviarEstatPost(
			HttpServletRequest request,
			@Valid FirmaSimpleWebCommand command,
			BindingResult bindingResult,
			Model model) {
		
		RequestSessionHelper.actualitzarObjecteSessio(
				request,
				SESSION_ATTRIBUTE_DATA_INICI,
				new Date());

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

		if (bindingResult.hasErrors()) {
			return "expedientEstatsForm";
		}
		
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		
		
		String urlReturnToRipea = aplicacioService.propertyBaseUrl() + "/massiu/firmasimpleweb/firmaSimpleWebEnd";
		
		String urlRedirectToPortafib = documentService.firmaSimpleWebStartMassiu(
				seleccio,
				command.getMotiu(),
				urlReturnToRipea, 
				entitatActual.getId());
		
		
		
		return "redirect:" + urlRedirectToPortafib;

	}
	
	
	@RequestMapping(value = "/firmaSimpleWebEnd")
	public String firmaSimpleWebEnd(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value = "transactionID", required = true) String transactionID) throws Exception {
		
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		
		Date dataInici = (Date)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_DATA_INICI);
		
		int errors = 0;
		int correctes = 0;
		int cancelled = 0;
		List<ExecucioMassivaContingutDto> execucioMassivaElements = new ArrayList<>();
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		FirmaResultatDto firmaResultat = documentService.firmaSimpleWebEnd(transactionID);
		
		if (firmaResultat.getStatus() == StatusEnumDto.OK) {

			for (FirmaSignatureStatus firmaSignatureStatus : firmaResultat.getSignatures()) {
				

				if (firmaSignatureStatus.getStatus() == StatusEnumDto.OK) {
					Throwable throwable = null;
					try {
						documentService.processarFirmaClient(
								entitatActual.getId(),
								Long.valueOf(firmaSignatureStatus.getSignID()),
								firmaSignatureStatus.getFitxerFirmatNom(), 
								firmaSignatureStatus.getFitxerFirmatContingut(), 
								RolHelper.getRolActual(request), 
								null);
						correctes++;
						
					} catch (Exception e) {
						log.error("Error al gurdar document firmat des del navegador=" + firmaSignatureStatus.getSignID(), e);
						errors++;
						throwable = ExceptionHelper.getRootCauseOrItself(e);
					}
					
					execucioMassivaElements.add(
							new ExecucioMassivaContingutDto(
									dataInici,
									new Date(),
									Long.valueOf(firmaSignatureStatus.getSignID()),
									throwable));
					
				} else if (firmaSignatureStatus.getStatus() == StatusEnumDto.WARNING) {
					
					execucioMassivaElements.add(
							new ExecucioMassivaContingutDto(
									dataInici,
									new Date(),
									Long.valueOf(firmaSignatureStatus.getSignID()),
									ExecucioMassivaEstatDto.ESTAT_CANCELAT));
					cancelled++;
				} else if (firmaSignatureStatus.getStatus() == StatusEnumDto.ERROR) {
					
					execucioMassivaElements.add(
							new ExecucioMassivaContingutDto(
									dataInici,
									new Date(),
									Long.valueOf(firmaSignatureStatus.getSignID()),
									new RuntimeException(firmaSignatureStatus.getMsg())));
					errors++;
				}

			}

		} else if (firmaResultat.getStatus() == StatusEnumDto.WARNING) {
			for (Long id : seleccio) {
				execucioMassivaElements.add(
						new ExecucioMassivaContingutDto(
								dataInici,
								new Date(),
								id,
								ExecucioMassivaEstatDto.ESTAT_CANCELAT));
			}
			
			cancelled = seleccio.size();

		} else if (firmaResultat.getStatus() == StatusEnumDto.ERROR) {
			for (Long id : seleccio) {
				execucioMassivaElements.add(
						new ExecucioMassivaContingutDto(
								dataInici,
								new Date(),
								id,
								new RuntimeException(firmaResultat.getMsg())));
			}
			errors = seleccio.size();
		}
		
		execucioMassivaService.saveExecucioMassiva(
				entitatActual.getId(),
				new ExecucioMassivaDto(
						ExecucioMassivaTipusDto.FIRMASIMPLEWEB,
						dataInici,
						new Date(),
						RolHelper.getRolActual(request)),
				execucioMassivaElements, 
				ElementTipusEnumDto.DOCUMENT);
		
		if (correctes > 0) {
			MissatgesHelper.success(request, getMessage(request, "document.controller.massiu.firmasimpleweb.ok", new Object[]{correctes}));
		}  
		if (cancelled > 0) {
			MissatgesHelper.warning(request, getMessage(request, "document.controller.massiu.firmasimpleweb.cancelled", new Object[]{cancelled}));
		}  
		if (errors > 0) {
			MissatgesHelper.error(request, getMessage(request, "document.controller.massiu.firmasimpleweb.error", new Object[]{errors}), null);
		} 
		
		RequestSessionHelper.esborrarObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);

		
		return "redirect://massiu/firmasimpleweb";

	}
	
	

	@RequestMapping(value = "/metaDocuments/{metaExpedientId}", method = RequestMethod.GET)
	@ResponseBody
	public List<MetaDocumentDto> findMetaDocuments(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		List<MetaDocumentDto> metaDocuments = new ArrayList<MetaDocumentDto>();
		metaDocuments = metaDocumentService.findByMetaExpedientAndFirmaSimpleWebActiva(entitatActual.getId(), metaExpedientId);
		
		return metaDocuments;
	}	



	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}



	private ContingutMassiuFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		ContingutMassiuFiltreCommand filtreCommand = (ContingutMassiuFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new ContingutMassiuFiltreCommand();
			filtreCommand.setMetaExpedientId(aplicacioService.getProcedimentPerDefecte(EntitatHelper.getEntitatActual(request).getId(), RolHelper.getRolActual(request)));
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}


	
}