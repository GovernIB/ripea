package es.caib.ripea.service.resourceservice;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;
import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.helper.ConfigHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import es.caib.ripea.persistence.entity.resourceentity.ExecucioMassivaContingutResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExecucioMassivaResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.UsuariResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.UsuariResourceRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ExecucioMassivaCuaHelper;
import es.caib.ripea.service.helper.ExecucioMassivaHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.base.model.DownloadableFile;
import es.caib.ripea.service.intf.dto.ExecucioMassivaEstatDto;
import es.caib.ripea.service.intf.dto.FitxerDto;
import es.caib.ripea.service.intf.model.ExecucioMassivaResource;
import es.caib.ripea.service.intf.resourceservice.ExecucioMassivaResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExecucioMassivaResourceServiceImpl extends BaseMutableResourceService<ExecucioMassivaResource, Long, ExecucioMassivaResourceEntity> implements ExecucioMassivaResourceService {
    
	private final ExecucioMassivaHelper execucioMassivaHelper;
	private final ConfigHelper configHelper;
	private final UsuariResourceRepository usuariResourceRepository;
	private final ExecucioMassivaCuaHelper execucioMassivaCuaHelper;

    @PostConstruct
    public void init() {
    	register(ExecucioMassivaResource.Fields.documentNom, new DocumentFieldDownloader());
    	register(ExecucioMassivaResource.ACTION_CANCELAR, new CancelarActionExecutor());
    }

    private class CancelarActionExecutor implements ActionExecutor<ExecucioMassivaResourceEntity, Serializable, Serializable> {

		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {}

		@Override
		public Serializable exec(String code, ExecucioMassivaResourceEntity entity, Serializable params) throws ActionExecutionException {
			try {
				//Els permisos es comproven sobre l'entitat de la mateixa execució massiva
				return execucioMassivaCuaHelper.cancelar(entity.getEntitat().getId(), entity.getId(), configHelper.getRolActual());
			} catch (Exception e) {
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, e.getMessage());
			}
		}
    }

    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
        String rolActual = configHelper.getRolActual();

        if (!"IPA_ADMIN".equals(rolActual)){
            Filter baseSpringFilter = (currentSpringFilter != null && !currentSpringFilter.isEmpty())? Filter.parse(currentSpringFilter):null;
            return FilterBuilder.and(
                    baseSpringFilter,
                    FilterBuilder.equal(BaseAuditableEntity.Fields.createdBy, SecurityContextHolder.getContext().getAuthentication().getName())
            ).generate();
        }
        return currentSpringFilter;
    }

    private class DocumentFieldDownloader implements FieldDownloader<ExecucioMassivaResourceEntity> {
        @Override
        public DownloadableFile download(ExecucioMassivaResourceEntity entity, String fieldName, OutputStream out) {
        	FitxerDto fitxerDto = execucioMassivaHelper.descarregarDocumentExecMassiva(entity.getEntitat().getId(), entity.getId());
            return new DownloadableFile(fitxerDto.getNom(),fitxerDto.getContentType(),fitxerDto.getContingut());
        }
    }
    
	@Override
    protected void afterConversion(ExecucioMassivaResourceEntity entity, ExecucioMassivaResource resource) {
        List<ExecucioMassivaContingutResourceEntity> continguts = entity.getContinguts();
        Map<ExecucioMassivaEstatDto, List<ExecucioMassivaContingutResourceEntity>> contingutMap = continguts.stream()
                        .collect(Collectors.groupingBy(ExecucioMassivaContingutResourceEntity::getEstat));

        resource.setFinalitzades( contingutMap.containsKey(ExecucioMassivaEstatDto.ESTAT_FINALITZAT) ?contingutMap.get(ExecucioMassivaEstatDto.ESTAT_FINALITZAT).size() :0);
        resource.setErrors( contingutMap.containsKey(ExecucioMassivaEstatDto.ESTAT_ERROR) ?contingutMap.get(ExecucioMassivaEstatDto.ESTAT_ERROR).size() :0);
        resource.setPendents( contingutMap.containsKey(ExecucioMassivaEstatDto.ESTAT_PENDENT) ?contingutMap.get(ExecucioMassivaEstatDto.ESTAT_PENDENT).size() :0);
        resource.setCancelats( contingutMap.containsKey(ExecucioMassivaEstatDto.ESTAT_CANCELAT) ?contingutMap.get(ExecucioMassivaEstatDto.ESTAT_CANCELAT).size() :0);

        resource.setExecutades(continguts.size());

        resource.setCancelable(entity.getTipus() != null && entity.getTipus().isCancelable());
        resource.setPotCancelar(ExecucioMassivaCuaHelper.potCancelar(
                entity.getCreatedBy(),
                SecurityContextHolder.getContext().getAuthentication().getName(),
                configHelper.getRolActual()));

        resource.setPortafirmesResponsables(
                entity.getPortafirmesResponsablesString() != null
                        ? entity.getPortafirmesResponsablesString().split(",")
                        : null);

        if (entity.getCreatedBy() != null) {
            UsuariResourceEntity usuariResourceEntity = usuariResourceRepository.findById(entity.getCreatedBy()).orElse(null);
            if (usuariResourceEntity != null) {
                resource.setCreatedByFullName(usuariResourceEntity.getNom() + " (" + usuariResourceEntity.getCodi() + ")");
            }
        }
    }
}