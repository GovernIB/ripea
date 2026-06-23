package es.caib.ripea.service.resourceservice;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.persistence.entity.CarpetaEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.resourceentity.CarpetaResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.CarpetaRestriccioResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.UsuariResourceEntity;
import es.caib.ripea.persistence.repository.CarpetaRepository;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.CarpetaHelper;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.ContingutHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.exception.ReportGenerationException;
import es.caib.ripea.service.intf.base.exception.ResourceNotFoundException;
import es.caib.ripea.service.intf.base.model.DownloadableFile;
import es.caib.ripea.service.intf.base.model.ReportFileType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.CarpetaDto;
import es.caib.ripea.service.intf.dto.FitxerDto;
import es.caib.ripea.service.intf.exception.ValidationException;
import es.caib.ripea.service.intf.model.CarpetaResource;
import es.caib.ripea.service.intf.model.CarpetaResource.ModificarFormAction;
import es.caib.ripea.service.intf.model.CarpetaResource.MoureCopiarFormAction;
import es.caib.ripea.service.intf.model.ContingutResource;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.model.UsuariResource;
import es.caib.ripea.service.intf.resourceservice.CarpetaResourceService;
import es.caib.ripea.service.resourcehelper.ContingutResourceHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarpetaResourceServiceImpl extends BaseMutableResourceService<CarpetaResource, Long, CarpetaResourceEntity> implements CarpetaResourceService {

	private final EntitatRepository entitatRepository;
	private final CarpetaRepository carpetaRepository;
	
	private final ContingutHelper contingutHelper;
	private final ExcepcioLogHelper excepcioLogHelper;
	private final CarpetaHelper carpetaHelper;
	private final ConfigHelper configHelper;
	private final ContingutResourceHelper contingutResourceHelper;
	private final EntityComprovarHelper entityComprovarHelper;

    @PostConstruct
    public void init() {
        register(CarpetaResource.PERSPECTIVE_PATH_CODE,	new PathPerspectiveApplicator());
        register(CarpetaResource.PERSPECTIVE_RESPONSABLE_RESTRICCIO, new ResponsableRestriccioPerspectiveApplicator());
        register(CarpetaResource.PERSPECTIVE_RESTRICCIONS, new RestriccionsPerspectiveApplicator());
        register(CarpetaResource.ACTION_MODIFICAR_NOM,	new ModificarNomActionExecutor());
        register(CarpetaResource.REPORT_EXPORTAR_INDEX_PDF,	new ExportIdexPdfGenerator());
        register(CarpetaResource.REPORT_EXPORTAR_INDEX_XLS,	new ExportIdexXlsGenerator());
        register(CarpetaResource.ACTION_MOURE_COPIAR, new MoureCopiarActionExecutor());
        register(CarpetaResource.ACTION_GUARDAR_ARXIU, new GuardarArxiuActionExecutor());
    }
	
    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
    	String entitatActualCodi = configHelper.getEntitatActualCodi();
        Filter filtreResultat = FilterBuilder.and(
                (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null,
                FilterBuilder.equal(ContingutResource.Fields.entitat + "." + EntitatResource.Fields.codi, 
                		entitatActualCodi != null?entitatActualCodi:"................................................................................")
        );
        String resultat = filtreResultat.generate();
        return resultat;
    }
	
    @Override
    public CarpetaResource create(CarpetaResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
    	try {
			//La entitat ja es comprova a pinbalHelper
			EntitatEntity entitatEntity = entitatRepository.findByCodi(configHelper.getEntitatActualCodi());
			Long pareId = resource.getPare() != null && resource.getPare().getId() != null
					? resource.getPare().getId()
					: resource.getExpedient().getId();
			CarpetaDto carpetaCreada = carpetaHelper.create(
					entitatEntity.getId(),
					pareId,
					resource.getNom(),
					false,
					null,
					false,
					null, 
					false, 
					configHelper.getRolActual(), 
					true);
			resource.setId(carpetaCreada.getId());
			
			List<String> usuaris = resource.getRestriccions().stream()
                    .map(ResourceReference::getId)
                    .collect(Collectors.toList());
			
			carpetaHelper.restringirCarpeta(
					entitatEntity.getId(), 
					carpetaCreada.getId(), 
					resource.getRestringida(),
					resource.getMotiuRestriccio(), 
					usuaris);
//			reorderIfReorderable(carpetaResourceRepository.findById(carpetaCreada.getId()).get(), null, null, true, false);
    	} catch (ValidationException ex) {
    		throw ex;
    	} catch (Exception ex) {
    		excepcioLogHelper.addExcepcio("/carpeta/"+resource.getId()+"/create", ex);
    	}
    	return resource;
    }
    
    @Override
	public CarpetaResource update(Long id, CarpetaResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotFoundException {
    	try {
            //TODO: ara mateix falla arxiu al renombrar una carpeta
            carpetaHelper.modificarNomCarpeta(
                    resource.getEntitat().getId(),
                    id,
                    resource.getNom());

            List<String> usuaris = resource.getRestriccions().stream()
                    .map(ResourceReference::getId)
                    .collect(Collectors.toList());

            carpetaHelper.restringirCarpeta(
                    resource.getEntitat().getId(),
                    id,
                    resource.getRestringida(),
                    resource.getMotiuRestriccio(),
                    usuaris);
    	} catch (Exception ex) {
    		excepcioLogHelper.addExcepcio("/carpeta/"+resource.getId()+"/update", ex);
    	}
    	return resource;
    }
    
    @Override
    public void delete(Long id, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotFoundException {
    	try {
    		EntitatEntity entitatEntity = entitatRepository.findByCodi(configHelper.getEntitatActualCodi());
    		contingutHelper.deleteReversible(entitatEntity.getId(), id, null, configHelper.getRolActual());
    	} catch (Exception ex) {
    		excepcioLogHelper.addExcepcio("/document/"+id+"/delete", ex);
    		throw new ResourceNotFoundException(getResourceClass(), ex.getMessage());
    	}
    }

    private class PathPerspectiveApplicator implements PerspectiveApplicator<CarpetaResourceEntity, CarpetaResource> {
        @Override
        public void applySingle(String code, CarpetaResourceEntity entity, CarpetaResource resource) throws PerspectiveApplicationException {
            resource.setTreePath(contingutResourceHelper.getTreePath(entity));
        }
    }
    
    private class ResponsableRestriccioPerspectiveApplicator implements PerspectiveApplicator<CarpetaResourceEntity, CarpetaResource> {
        @Override
        public void applySingle(String code, CarpetaResourceEntity entity, CarpetaResource resource) throws PerspectiveApplicationException {
        	UsuariResourceEntity responsableRestriccio = entity.getResponsableRestriccio();
        	
        	if (responsableRestriccio != null) {
//	            resource.setResponsableRestriccio(ResourceReference.toResourceReference(
//	            		responsableRestriccio.getId(),
//	            		responsableRestriccio.getNom()));
        	}
        }
    }
    
    private class RestriccionsPerspectiveApplicator implements PerspectiveApplicator<CarpetaResourceEntity, CarpetaResource> {
		@Override
		public void applySingle(String code, CarpetaResourceEntity entity, CarpetaResource resource) throws PerspectiveApplicationException {
	        if (entity.getRestriccions() == null || entity.getRestriccions().isEmpty()) {
	            resource.setRestriccions(Collections.emptyList());
	            return;
	        }

			List<ResourceReference<UsuariResource, String>> restriccions = entity.getRestriccions().stream()
					.map(CarpetaRestriccioResourceEntity::getUsuari).filter(Objects::nonNull).map(usuari -> {
						ResourceReference<UsuariResource, String> ref = new ResourceReference<>();
						ref.setId(usuari.getCodi());
						ref.setDescription(usuari.getNom());
						return ref;
					}).collect(Collectors.toList());

			resource.setRestriccions(restriccions);
		}
    }
    
    @Override
    protected void afterConversion(CarpetaResourceEntity entity, CarpetaResource resource) {
    	resource.setHasDocumentsFills(contingutResourceHelper.contingutHasDocumentsFills(entity.getId()));
    }
    
    private class ModificarNomActionExecutor implements ActionExecutor<CarpetaResourceEntity, CarpetaResource.ModificarFormAction, CarpetaResource> {

		@Override
		public void onChange(Serializable id, ModificarFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, ModificarFormAction target) {}

		@Override
		public CarpetaResource exec(String code, CarpetaResourceEntity entity, ModificarFormAction params) throws ActionExecutionException {
			try {
				EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
				carpetaHelper.modificarNomCarpeta(entitatEntity.getId(), entity.getId(), params.getNom());
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/carpeta/"+entity.getId()+"/ModificarNomActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, e.getMessage());
			}
			return objectMappingHelper.newInstanceMap(entity, CarpetaResource.class);
		}
    }
    
    private class ExportIdexPdfGenerator implements ReportGenerator<CarpetaResourceEntity, Serializable, Serializable> {

		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {}

		@Override
		public List<Serializable> generateData(String code, CarpetaResourceEntity entity, Serializable params) throws ReportGenerationException {
			List<Serializable> parametres = new ArrayList<Serializable>();
			parametres.add(entity!=null?entity.getId():0l);
			return parametres;
		}
		
    	@Override
		public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
			DownloadableFile resultat = null;
			Long carpetaId = data.get(0)!=null?(Long)data.get(0):null;
			try {
				EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
				List<CarpetaEntity> carpetes = new ArrayList<CarpetaEntity>();
				carpetes.add(carpetaRepository.findById(carpetaId).get());
        		FitxerDto fitxerDto = carpetaHelper.exportarCarpetes(entitatEntity, carpetes, "PDF");
            	resultat = new DownloadableFile(fitxerDto.getNom(), fitxerDto.getContentType(), fitxerDto.getContingut());
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/carpeta/"+carpetaId+"/ExportIdexPdfGenerator", e);
				throw new ActionExecutionException(getResourceClass(), carpetaId, code, e.getMessage());
			}
			return resultat;
		}
    }
    
    private class ExportIdexXlsGenerator implements ReportGenerator<CarpetaResourceEntity, Serializable, Serializable> {

		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {}

		@Override
		public List<Serializable> generateData(String code, CarpetaResourceEntity entity, Serializable params) throws ReportGenerationException {
			List<Serializable> parametres = new ArrayList<Serializable>();
			parametres.add(entity!=null?entity.getId():0l);
			return parametres;
		}
		
    	@Override
		public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
			DownloadableFile resultat = null;
			Long carpetaId = data.get(0)!=null?(Long)data.get(0):null;
			try {
				EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
				List<CarpetaEntity> carpetes = new ArrayList<CarpetaEntity>();
				carpetes.add(carpetaRepository.findById(carpetaId).get());
        		FitxerDto fitxerDto = carpetaHelper.exportarCarpetes(entitatEntity, carpetes, "XLSX");
            	resultat = new DownloadableFile(fitxerDto.getNom(), fitxerDto.getContentType(), fitxerDto.getContingut());
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/carpeta/"+carpetaId+"/ExportIdexPdfGenerator", e);
				throw new ActionExecutionException(getResourceClass(), carpetaId, code, e.getMessage());
			}
			return resultat;
		}
    }
    
    private class MoureCopiarActionExecutor implements ActionExecutor<CarpetaResourceEntity, CarpetaResource.MoureCopiarFormAction, CarpetaResource> {

		@Override
		public void onChange(Serializable id, MoureCopiarFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, MoureCopiarFormAction target) {}

		@Override
		public CarpetaResource exec(String code, CarpetaResourceEntity entity, MoureCopiarFormAction params) throws ActionExecutionException {
			try {
				EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
				Long contingutDestiId = params.getCarpeta()!=null?params.getCarpeta().getId():params.getExpedient().getId();
				switch (params.getAction()) {
				case MOURE:
					contingutHelper.move(
							entitatEntity.getId(),
							entity.getId(),
							contingutDestiId,
							null,
							params.getCarpetaNova(),
							configHelper.getRolActual());							
					break;
				case COPIAR:
					contingutHelper.copy(entitatEntity.getId(), entity.getId(), contingutDestiId, false); //No recursiu
					break;
				}
				return objectMappingHelper.newInstanceMap(entity, CarpetaResource.class);
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/carpeta/"+entity.getId()+"/MoureCopiarActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, e.getMessage());
			}
		}
    }

    private class GuardarArxiuActionExecutor implements ActionExecutor<CarpetaResourceEntity, Serializable, Serializable> {

		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue,Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {}

		@Override
		public Serializable exec(String code, CarpetaResourceEntity entity, Serializable params) throws ActionExecutionException {
			try {
				if (entity.getArxiuUuid() == null) {
					CarpetaEntity carpetaEntity = carpetaRepository.findById(entity.getId()).orElse(null);
					if (carpetaEntity!=null) {
						contingutHelper.arxiuPropagarModificacio(carpetaEntity);
					}
				}
				return objectMappingHelper.newInstanceMap(entity, CarpetaResource.class);
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/carpeta/"+entity.getId()+"/GuardarArxiuActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, e.getMessage());
			}
		}
    }
}