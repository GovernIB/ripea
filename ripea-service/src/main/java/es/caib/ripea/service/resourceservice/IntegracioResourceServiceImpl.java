package es.caib.ripea.service.resourceservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import es.caib.ripea.service.intf.base.model.FieldOption;
import es.caib.ripea.service.intf.model.ExpedientResource;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.entity.resourceentity.IntegracioResourceEntity;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.helper.IntegracioHelper;
import es.caib.ripea.service.helper.MessageHelper;
import es.caib.ripea.service.helper.PaginacioHelper;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.base.exception.ResourceNotFoundException;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.DiagnosticFiltreDto;
import es.caib.ripea.service.intf.dto.GenericDto;
import es.caib.ripea.service.intf.dto.IntegracioAccioDto;
import es.caib.ripea.service.intf.dto.IntegracioDto;
import es.caib.ripea.service.intf.dto.IntegracioFiltreDto;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.model.IntegracioResource;
import es.caib.ripea.service.intf.model.IntegracioResource.DiagnosticResetForm;
import es.caib.ripea.service.intf.resourceservice.IntegracioResourceService;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class IntegracioResourceServiceImpl extends BaseMutableResourceService<IntegracioResource, Long, IntegracioResourceEntity> implements IntegracioResourceService {

	private final OrganGestorRepository organGestorRepository;
	private final EntitatRepository entitatRepository;
	
	private final ExcepcioLogHelper excepcioLogHelper;
	private final IntegracioHelper integracioHelper;
	private final PaginacioHelper paginacioHelper;
	private final ConfigHelper configHelper;
	private final PluginHelper pluginHelper;
	private final MessageHelper messageHelper;
	
    @PostConstruct
    public void init() {
    	register(IntegracioResource.ACTION_INTEGRACIONS_LIST,	new IntegracionsListActionExecutor());
    	register(IntegracioResource.ACTION_DIAGNOSTIC_PLUGIN,	new DiagnosticPluginActionExecutor());
    	register(IntegracioResource.ACTION_REINICIAR_PLUGIN,	new ReiniciarPluginActionExecutor());
    	register(IntegracioResource.FILTER_PLUGIN_CODE,	        new PluginFilterOnchangeLogicProcessor());
    }
	
	@Override
	public Page<IntegracioResource> findPage(
			String quickFilter,
			String filter,
			String[] namedQueries,
			String[] perspectives,
			Pageable pageable) {
		
		//1.- Convertir el filter en un IntegracioFiltreDto
		IntegracioFiltreDto filtre = new IntegracioFiltreDto();
		
		Map<String, String> filtres = Utils.parseSpringFilter(filter);
		
		//2.- Fer la cerca
		List<IntegracioAccioDto> accions = integracioHelper.findAccionsByIntegracioCodi(namedQueries[0], filtre);
		
		if (accions == null || accions.isEmpty()) {
			return new PageImpl<>(List.of(), pageable, 0);
		}

		//3. Paginar
		List<List<IntegracioAccioDto>> pagines = paginacioHelper.getPages(accions, pageable.getPageSize());
		PaginaDto<IntegracioAccioDto> pagina = paginacioHelper.toPaginaDto(pagines.get(pageable.getPageNumber()), null);
		pagina.setContingut(pagines.get(pageable.getPageNumber()));
		PaginaDto<IntegracioAccioDto> aux = paginacioHelper.prepararPagina(pagina, pagines, accions);
		
		List<IntegracioResource> resultatResource = new ArrayList<IntegracioResource>();
		if (aux!=null && aux.getContingut()!=null) {
			for (IntegracioAccioDto accioDto: aux.getContingut()) {
				resultatResource.add(objectMappingHelper.newInstanceMap(accioDto, IntegracioResource.class));
			}
		}
		
		//4.- Convertir la paginacio dto a paginació spring
	    return new PageImpl<>(
	    	resultatResource,
	        pageable,
	        aux.getElementsTotal()
	    );
		
	}
    
	@Override
	public IntegracioResource getOne(
			Long id,
			String[] perspectives) throws ResourceNotFoundException {
		
		List<IntegracioAccioDto> accions = integracioHelper.findAccionsByIntegracioCodi(perspectives[0], null);
		if (accions != null) {
			for (IntegracioAccioDto accio: accions) {
				if (accio.getIndex() != null && id.equals(accio.getIndex())) {
					return objectMappingHelper.newInstanceMap(accio, IntegracioResource.class);
				}
			}
		}
		
		return null;
	}
    
	@Override
	public boolean isEntityRepositoryOptional() {
		return true;
	}
	
	private class IntegracionsListActionExecutor implements ActionExecutor<IntegracioResourceEntity, Serializable, IntegracioDto[]> {
		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue,
				Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {}
		@Override
		public IntegracioDto[] exec(String code, IntegracioResourceEntity entity, Serializable params) throws ActionExecutionException {
			return integracioHelper.findAll().toArray(new IntegracioDto[0]);
		}
	}
	
	private void initDiagnosticResetForm(DiagnosticResetForm target) {
		
        String entitatActualCodi = configHelper.getEntitatActualCodi();
        String organActualCodi	 = configHelper.getOrganActualCodi();
		
        if (Utils.hasValue(entitatActualCodi)) {
        	
        	EntitatEntity ee = entitatRepository.findByCodi(entitatActualCodi);
        	
        	if (ee!=null) {
        	
	       		target.setEntitat(ResourceReference.toResourceReference(ee.getId(), ee.getNom()));
	        	
	        	if (Utils.hasValue(entitatActualCodi)) {
	            	OrganGestorEntity oge = organGestorRepository.findByEntitatAndCodi(ee, organActualCodi);
	            	if (ee!=null) {
	            		target.setOrgan(ResourceReference.toResourceReference(oge.getId(), oge.getNom()));
	            	}
	        	}
        	}
        	
        } else {
        	List<EntitatEntity> entitats = entitatRepository.findBy(Sort.by("id"));
        	if (entitats!=null && entitats.size()>0) {
        		target.setEntitat(ResourceReference.toResourceReference(entitats.get(0).getId(), entitats.get(0).getNom()));
        	} else {
        		target.setEntitat(null);
        	}
        	target.setOrgan(null);
        }		
	}
	
	private class DiagnosticPluginActionExecutor implements ActionExecutor<IntegracioResourceEntity, IntegracioResource.DiagnosticResetForm, Serializable> {
		@Override
		public void onChange(Serializable id, DiagnosticResetForm previous, String fieldName, Object fieldValue,
				Map<String, AnswerValue> answers, String[] previousFieldNames, DiagnosticResetForm target) {
			if (fieldName == null) {
				initDiagnosticResetForm(target);
			}
		}
		@Override
		public Serializable exec(String code, IntegracioResourceEntity entity, DiagnosticResetForm params) throws ActionExecutionException {
			try {
				DiagnosticFiltreDto df = new DiagnosticFiltreDto();
				EntitatEntity ee = null;
				OrganGestorEntity oge = null;
				if (params.getEntitat()!=null) {
					ee = entitatRepository.findById(params.getEntitat().getId()).get();
				}
				if (params.getOrgan()!=null) {
					oge = organGestorRepository.findById(params.getOrgan().getId()).get();
				}
				
				df.setEntitatCodi((ee!=null)?ee.getCodi():null);
				df.setOrganCodi((oge!=null)?oge.getCodi():null);
				
				GenericDto resultat = pluginHelper.integracioDiagnostic(params.getCodiIntegracio(), df);
				String missatge = messageHelper.getMessage(resultat.getCodi(), resultat.getArguments());
				String nivell = "OK";
				if (resultat.getTexte()!=null) {
					if (resultat.getTexte().contains("blau")) { nivell = "INFO"; }
					if (resultat.getTexte().contains("vermell")) { nivell = "ERROR"; }
					if (resultat.getTexte().contains("taronja")) { nivell = "WARN"; }
				}
                Map<String, String> result = new HashMap<String, String>();
                result.put("nivell", nivell);
                result.put("missatge", missatge.length() > 100 ?missatge.substring(0, 100)+"..." :missatge);
                return (Serializable) result;
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/integracio/"+entity.getId()+"/DiagnosticPluginActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, messageHelper.getMessage("message.common.action.error")+": "+e.getMessage());
			}
		}		
	}
	
	private class ReiniciarPluginActionExecutor implements ActionExecutor<IntegracioResourceEntity, IntegracioResource.DiagnosticResetForm, Serializable> {
		@Override
		public void onChange(Serializable id, DiagnosticResetForm previous, String fieldName, Object fieldValue,
				Map<String, AnswerValue> answers, String[] previousFieldNames, DiagnosticResetForm target) {
			if (fieldName == null) {
				initDiagnosticResetForm(target);
			}
		}
		@Override
		public Serializable exec(String code, IntegracioResourceEntity entity, DiagnosticResetForm params) throws ActionExecutionException {
			
			try {
			
				if (Utils.hasValue(params.getCodiIntegracio())) {
					//En el mateix ordre que apareixen les pipelles en pantalla:
					switch (params.getCodiIntegracio()) {
						case "PORTAFIRMES": pluginHelper.resetPlugins("pf"); break;
						case "FIRMA_SIMPLE_WEB": pluginHelper.resetPlugins("si"); break;
						case "FIRMA_SERVIDOR": pluginHelper.resetPlugins("fs"); break;
						//CALLBACK es el callback portafirmes, que té tab en el monitor, pero que realment no hi ha res a resetejar
						case "CONCSV": pluginHelper.resetPlugins("csv"); break;
						case "GES_DOC": pluginHelper.resetPlugins("gd"); break;
						//PINBAL no té un array de plugins en memoria que es pugui resetejar...
						case "DISTRIBUCIO": pluginHelper.resetPlugins("dis"); break;
						case "USUARIS": pluginHelper.resetPlugins("us"); break;
						case "CONVERSIO": pluginHelper.resetPlugins("cd"); break;
						case "DADESEXT": pluginHelper.resetPlugins("pi"); pluginHelper.resetPlugins("de"); break;
						case "NOTIB": pluginHelper.resetPlugins("no"); break;
						case "FIRMA_VIAFIRMA": pluginHelper.resetPlugins("vi"); break;
						case "DIGITALITZACIO": pluginHelper.resetPlugins("dg"); break;
						case "VALIDATE_SIGNATURE": pluginHelper.resetPlugins("vf"); break;
						case "GESCONADM": pluginHelper.resetPlugins("ro"); break;
						case "SUMMARIZE": pluginHelper.resetPlugins("su"); break;
						case "COMANDA": pluginHelper.resetPlugins("cmd"); break;
						case "FIRMA_AGIL": pluginHelper.resetPlugins("fag"); break;
						case "ORGANISMES": pluginHelper.resetPlugins("di"); break;
						case "REGISTRE": pluginHelper.resetPlugins("reg"); break;
					default:
						break;
					}
				} else {
					pluginHelper.resetPlugins("xx");
				}
				return "{\"resultat\": \"OK\"}";
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/integracio/"+entity.getId()+"/ReiniciarPluginActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, messageHelper.getMessage("message.common.action.error")+": "+e.getMessage());
			}
		}		
	}

    private class PluginFilterOnchangeLogicProcessor implements FilterProcessor<IntegracioResource.DiagnosticResetForm> {
        @Override
        public void onChange(Serializable id, DiagnosticResetForm previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, DiagnosticResetForm target) {
            if (fieldName == null) {
                initDiagnosticResetForm(target);
            }
        }
    }
}