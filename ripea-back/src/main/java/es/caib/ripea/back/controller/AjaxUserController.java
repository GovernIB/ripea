/**
 * 
 */
package es.caib.ripea.back.controller;

import es.caib.ripea.back.helper.EnumHelper.HtmlOption;
import es.caib.ripea.service.intf.dto.UsuariDto;
import es.caib.ripea.service.intf.service.AplicacioService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador per a les consultes ajax dels usuaris normals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/userajax") // No podem posar "/ajaxuser" per mor del AjaxInterceptor
public class AjaxUserController extends BaseUserController {

	@Autowired
	private AplicacioService aplicacioService;



	@RequestMapping(value = "/usuari/{codi}", method = RequestMethod.GET)
	@ResponseBody
	public UsuariDto getByCodi(
			HttpServletRequest request,
			@PathVariable String codi,
			Model model) {
		return aplicacioService.findUsuariAmbCodi(codi);
	}

	@RequestMapping(value = "/usuaris/{text}", method = RequestMethod.GET)
	@ResponseBody
	public List<UsuariDto> get(
			HttpServletRequest request,
			@PathVariable String text,
			Model model) {
		return aplicacioService.findUsuariAmbText(text);
	}

	@RequestMapping(value = "/usuariDades/item/{codi}", method = RequestMethod.GET)
	@ResponseBody
	public UsuariDto getByCodiPluginDadesUsuari(
			HttpServletRequest request,
			@PathVariable String codi,
			Model model) {
		try {
			return aplicacioService.findUsuariCarrecAmbCodiDades(codi);
		} catch (Exception ex) {
			logger.error("Error al consultar la informació de l'usuari " + codi, ex);
			return null;
		}
	}

	@RequestMapping(value = "/usuarisDades/{text}", method = RequestMethod.GET, produces= {"application/json; charset=UTF-8"})
	@ResponseBody
	public List<UsuariDto> getPluginDadesUsuari(
			HttpServletRequest request,
			@PathVariable String text,
			Model model) {
		try {
			return aplicacioService.findUsuariAmbTextDades(decodedParam(text));
		} catch (Exception ex) {
			logger.error("Error al consultar la informació dels usuaris amb el filtre \"" + text + "\"", ex);
			return new ArrayList<UsuariDto>();
		}
	}

	@RequestMapping(value = "/enum/{enumClass}", method = RequestMethod.GET)
	@ResponseBody
	public List<HtmlOption> enumValorsAmbText(
			HttpServletRequest request,
			@PathVariable String enumClass) throws ClassNotFoundException {
		Class<?> enumeracio = findEnumDtoClass(enumClass);
		StringBuilder textKeyPrefix = new StringBuilder();
		String[] textKeys = StringUtils.splitByCharacterTypeCamelCase(enumClass);
		for (String textKey: textKeys) {
			if (!"dto".equalsIgnoreCase(textKey)) {
				textKeyPrefix.append(textKey.toLowerCase());
				textKeyPrefix.append(".");
			}
		}
		List<HtmlOption> resposta = new ArrayList<HtmlOption>();
		if (enumeracio.isEnum()) {
			for (Object e: enumeracio.getEnumConstants()) {
				resposta.add(new HtmlOption( 
						((Enum<?>)e).name(),
						getMessage(
								request,
								textKeyPrefix.toString() + ((Enum<?>)e).name(),
								null)));
			}
		}
		return resposta;
	}
	
	private Class<?> findEnumDtoClass(String className) throws ClassNotFoundException{
		try {
			return Class.forName("es.caib.ripea.core.api.dto." + className);
		} catch(ClassNotFoundException e) {
			// TODO: això hauria de cercar per tots els subpackages de dto
			return Class.forName("es.caib.ripea.core.api.dto.historic." + className);
		}		
	}

	private String decodedParam(String param) {
		String decodedParam = param;
		if (param != null && !param.isEmpty()) {
			try {
				decodedParam = new String(param.getBytes("ISO-8859-1"), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return decodedParam;
	}

	private static final Logger logger = LoggerFactory.getLogger(AjaxUserController.class);

}
