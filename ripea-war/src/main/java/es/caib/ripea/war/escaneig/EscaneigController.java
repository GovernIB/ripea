package es.caib.ripea.war.escaneig;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.fundaciobit.plugins.scanweb.api.ScanWebStatus;
import org.fundaciobit.plugins.signature.api.StatusSignaturesSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import es.caib.ripea.war.helper.MissatgesHelper;

/**
 * Controller per a les accions de l'escaneig de documents.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping(value = EscaneigHelper.CONTEXTWEB)
public class EscaneigController {

	public static final boolean stepSelectionWhenOnlyOnePlugin = true;

	@Autowired
	private EscaneigHelper escaneigHelper;



	@RequestMapping(value = "/selectscanwebmodule/{scanWebId}")
	public String selectScanWebModule(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable("scanWebId") long scanWebId,
			Model model) throws Exception {
		List<EscaneigPlugin> pluginsFiltered = escaneigHelper.getAllPlugins(
				request,
				scanWebId);
		if (stepSelectionWhenOnlyOnePlugin) {
			if (pluginsFiltered.size() == 1) {
				EscaneigPlugin plugin = pluginsFiltered.get(0);
				return "redirect:" +
						EscaneigHelper.CONTEXTWEB + "/showscanwebmodule/" +
						plugin.getPluginId() + "/" + scanWebId;
			}
		}
		if (pluginsFiltered.size() == 0) {
			String msg = "No existeix cap mòdul de scan que passi els filtres";
			EscaneigConfig swc = escaneigHelper.getScanWebConfig(
					request,
					scanWebId);
			if (swc == null) {
				MissatgesHelper.error(request, msg);
			} else {
				ScanWebStatus sws = swc.getStatus();
				sws.setErrorMsg(msg);
				sws.setErrorException(null);
				sws.setStatus(StatusSignaturesSet.STATUS_FINAL_ERROR);
			}
			return "redirect:" + swc.getUrlFinal();
		}
		model.addAttribute(
				"scanWebId",
				scanWebId);
		model.addAttribute(
				"plugins",
				pluginsFiltered);
		return "escaneigSeleccio";
	}

	@RequestMapping(value = "/showscanwebmodule/{pluginId}/{scanWebId}")
	public RedirectView showScanWebModule(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable("pluginId") String pluginId,
			@PathVariable("scanWebId") Long scanWebId) throws Exception {
		EscaneigConfig swc = escaneigHelper.getScanWebConfig(
				request,
				scanWebId);
		swc.setPluginId(pluginId);
	    if (swc.getFlags() == null || swc.getFlags().size() == 0) {
	      Set<String> defaultFlags = escaneigHelper.getDefaultFlags(swc);
	      swc.setFlags(defaultFlags);
	    }
	    String urlToPluginWebPage = escaneigHelper.scanDocument(
	    		request,
	    		scanWebId);
	    return new RedirectView(urlToPluginWebPage, false);
	}

	private static final String REQUEST_PLUGIN_MAPPING = "/requestPlugin/{scanWebId}/**";
	@RequestMapping(value = REQUEST_PLUGIN_MAPPING)
	public void requestPlugin(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable long scanWebId) throws Exception {
		String servletPath = request.getServletPath();
		int indexBarra = StringUtils.ordinalIndexOf(
				servletPath,
				"/",
				StringUtils.countMatches(
						EscaneigHelper.CONTEXTWEB + REQUEST_PLUGIN_MAPPING,
						"/"));
		String query = servletPath.substring(indexBarra + 1);
		escaneigHelper.requestPlugin(
				request,
				response,
				scanWebId,
				query);
	}

	@RequestMapping(value = "/final/{scanWebId}")
	public String finalProcesEscaneig(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable long scanWebId) throws Exception {
		EscaneigConfig ewc = escaneigHelper.finalitzarEscaneig(
				request,
				scanWebId);
		return "redirect:" + ewc.getUrlFinalRipea() + "?scanWebId=" + scanWebId;
	}

}
