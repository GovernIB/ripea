package es.caib.ripea.service.resourceservice;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.persistence.entity.resourceentity.FluxFirmaUsuariResourceEntity;
import es.caib.ripea.persistence.repository.UsuariRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.ResourceNotDeletedException;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.model.FluxFirmaUsuariResource;
import es.caib.ripea.service.intf.model.UsuariResource;
import es.caib.ripea.service.intf.resourceservice.FluxFirmaUsuariResourceService;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servei de recurs per al manteniment dels fluxos de firma d'un usuari.
 *
 * Els fluxos només es poden crear o modificar des de la interfície de PortaFIB: les accions
 * retornen la url a mostrar dins d'un iframe i és PortaFIB qui, en acabar, crida la url de
 * retorn de RIPEA (FluxFirmaUsuariController) que és on es persisteix el flux.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FluxFirmaUsuariResourceServiceImpl extends BaseMutableResourceService<FluxFirmaUsuariResource, Long, FluxFirmaUsuariResourceEntity> implements FluxFirmaUsuariResourceService {

	// Valor que no pot coincidir amb cap codi: si no es pot resoldre l'entitat o l'usuari
	// actual el llistat ha de quedar buit, mai mostrar els fluxos de tothom.
	private static final String CODI_INEXISTENT = "................................................................................";

	private final ConfigHelper configHelper;
	private final PluginHelper pluginHelper;
	private final UsuariRepository usuariRepository;
	private final ExcepcioLogHelper excepcioLogHelper;
	private final EntityComprovarHelper entityComprovarHelper;

	@PostConstruct
	public void init() {
		register(FluxFirmaUsuariResource.ACTION_CREAR_FLUX_CODE, new CrearFluxActionExecutor());
		register(FluxFirmaUsuariResource.ACTION_EDITAR_FLUX_CODE, new EditarFluxActionExecutor());
	}

	/**
	 * Els fluxos són privats de cada usuari dins de cada entitat. El filtre s'aplica també
	 * al getOne, a l'esborrat i a les accions (totes passen per getEntity), de manera que un
	 * usuari no pot accedir als fluxos d'un altre encara que en conegui l'id.
	 */
	@Override
	protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
		String entitatActualCodi = configHelper.getEntitatActualCodi();
		String usuariActualCodi = getUsuariActualCodi();
		Filter filtreBase = FilterBuilder.and(
				(currentSpringFilter != null && !currentSpringFilter.isEmpty()) ? Filter.parse(currentSpringFilter) : null,
				FilterBuilder.equal(
						FluxFirmaUsuariResource.Fields.entitat + "." + EntitatResource.Fields.codi,
						entitatActualCodi != null ? entitatActualCodi : CODI_INEXISTENT),
				FilterBuilder.equal(
						FluxFirmaUsuariResource.Fields.usuari + "." + UsuariResource.Fields.codi,
						usuariActualCodi != null ? usuariActualCodi : CODI_INEXISTENT));
		return filtreBase.generate();
	}

	@Override
	protected void beforeDelete(FluxFirmaUsuariResourceEntity entity, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotDeletedException {
		if (!isCreacioFluxUsuariActiva()) {
			throw new ResourceNotDeletedException(
					getResourceClass(),
					String.valueOf(entity.getId()),
					"La propietat " + PropertyConfig.PERMETRE_USUARIS_CREAR_FLUX_PORTAFIB + " no està activada");
		}
	}

	/**
	 * En esborrar el flux s'esborra també la plantilla a PortaFIB, igual que fa la interfície
	 * antiga (FluxFirmaUsuariServiceImpl.delete). Es fa a l'afterDelete, dins de la mateixa
	 * transacció: si PortaFIB retorna error no s'esborra la fila.
	 */
	@Override
	protected void afterDelete(FluxFirmaUsuariResourceEntity entity, Map<String, AnswerRequiredException.AnswerValue> answers) {
		if (entity.getPortafirmesFluxId() != null) {
			pluginHelper.portafirmesEsborrarPlantillaFirma(getIdiomaUsuariActual(), entity.getPortafirmesFluxId());
		}
	}

	private class CrearFluxActionExecutor implements ActionExecutor<FluxFirmaUsuariResourceEntity, Serializable, Serializable> {

		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue,
				Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {
		}

		@Override
		public Serializable exec(String code, FluxFirmaUsuariResourceEntity entity, Serializable params) throws ActionExecutionException {
			Map<String, String> result = new HashMap<>();
			try {
				comprovarCreacioFluxUsuariActiva();
				String url = pluginHelper.portafirmesIniciarFluxDeFirma(
						true,
						getUrlRetorn(null)).getUrlRedireccio();
				result.put("url", url);
				return (Serializable) result;
			} catch (Exception ex) {
				excepcioLogHelper.addExcepcio("/flux-firma-usuari/CrearFluxActionExecutor", ex);
				throw new ActionExecutionException(getResourceClass(), null, code, ex.getMessage());
			}
		}
	}

	private class EditarFluxActionExecutor implements ActionExecutor<FluxFirmaUsuariResourceEntity, Serializable, Serializable> {

		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue,
				Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {
		}

		@Override
		public Serializable exec(String code, FluxFirmaUsuariResourceEntity entity, Serializable params) throws ActionExecutionException {
			Map<String, String> result = new HashMap<>();
			try {
				comprovarCreacioFluxUsuariActiva();
				String url = pluginHelper.portafirmesRecuperarUrlPlantilla(
						entity.getPortafirmesFluxId(),
						getIdiomaUsuariActual(),
						getUrlRetorn(entity.getId()),
						true);
				result.put("url", url);
				return (Serializable) result;
			} catch (Exception ex) {
				excepcioLogHelper.addExcepcio("/flux-firma-usuari/EditarFluxActionExecutor", ex);
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, ex.getMessage());
			}
		}
	}

	/**
	 * Url a la qual PortaFIB retorna en acabar l'edició del flux. Les dades necessàries per
	 * processar el retorn viatgen encriptades dins la url perquè el retorn no depengui de la
	 * sessió: id de l'entitat, flux a actualitzar (null si és una creació) i usuari a qui notificar
	 * el resultat per SSE.
	 *
	 * En el cas de la creació PortaFIB hi afegeix l'id de transacció al final.
	 */
	private String getUrlRetorn(Long fluxFirmaUsuariId) {
		// Es comprova, com fa la interfície antiga, que l'usuari tengui accés a l'entitat actual
		// (usuari, administrador d'entitat o administrador d'òrgan) abans d'iniciar la transacció.
		Long entitatId = entityComprovarHelper.comprovarEntitat(
				configHelper.getEntitatActualCodi(),
				false,
				false,
				false,
				true,
				false).getId();
		String dadesUrl = entitatId + "#" + fluxFirmaUsuariId + "#" + getUsuariActualCodi();
		String paramSecure = Utils.encripta(dadesUrl, configHelper.getConfig(PropertyConfig.CLAU_ENCRIPTACIO));
		return configHelper.getConfig(PropertyConfig.BASE_URL) + "/fluxusuari/event/" + paramSecure + "/returnurl/";
	}

	/**
	 * La gestió de fluxos de firma d'usuari només està disponible si la propietat que ho
	 * permet està activada, igual que a la interfície antiga (AccesFluxosFirmaUsuariInterceptor).
	 */
	private boolean isCreacioFluxUsuariActiva() {
		return configHelper.getAsBoolean(PropertyConfig.PERMETRE_USUARIS_CREAR_FLUX_PORTAFIB);
	}

	private void comprovarCreacioFluxUsuariActiva() {
		if (!isCreacioFluxUsuariActiva()) {
			throw new SecurityException(
					"Es necessari activar la propietat " + PropertyConfig.PERMETRE_USUARIS_CREAR_FLUX_PORTAFIB +
					" per accedir a la gestió de fluxos de firma");
		}
	}

	private String getUsuariActualCodi() {
		return SecurityContextHolder.getContext().getAuthentication() != null
				? SecurityContextHolder.getContext().getAuthentication().getName()
				: null;
	}

	private String getIdiomaUsuariActual() {
		String idioma = usuariRepository.getOne(getUsuariActualCodi()).getIdioma();
		return idioma != null ? idioma : "ca";
	}

}
