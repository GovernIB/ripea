package es.caib.ripea.service.resourceservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.resourceentity.DadaResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaDadaResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.DadaResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.MetaDadaResourceRepository;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.DominiHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.model.FieldOption;
import es.caib.ripea.service.intf.dto.DigitalitzacioPerfilDto;
import es.caib.ripea.service.intf.dto.ResultatConsultaDto;
import es.caib.ripea.service.intf.model.DadaResource;
import es.caib.ripea.service.intf.resourceservice.DadaResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementació del servei de gestió de tasques.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DadaResourceServiceImpl extends BaseMutableResourceService<DadaResource, Long, DadaResourceEntity> implements DadaResourceService {

	private final DominiHelper dominiHelper;
	private final ConfigHelper configHelper;
	private final ExcepcioLogHelper excepcioLogHelper;
	private final EntitatRepository entitatRepository;
    private final DadaResourceRepository dadaResourceRepository;
    private final MetaDadaResourceRepository metaDadaResourceRepository;

    @PostConstruct
    public void init() {
    	register(DadaResource.Fields.domini, new DominiOptionsProvider());
    }
    
    public class DominiOptionsProvider implements FieldOptionsProvider {
		public List<FieldOption> getOptions(String fieldName, Map<String,String[]> requestParameterMap) {
			List<FieldOption> resultat = new ArrayList<FieldOption>();
			resultat.add(new FieldOption("NO_APLICA", "No aplica"));
			try {
				EntitatEntity entitatEntity = entitatRepository.findByCodi(configHelper.getEntitatActualCodi());
				List<ResultatConsultaDto> fluxosDto = dominiHelper.getResultDomini(
						entitatEntity.getId(), 
						requestParameterMap.get("METADADA_CODI")[0],
						"",
						1,
						Integer.MAX_VALUE).getResultat();
				
				if (fluxosDto!=null) {
					for (ResultatConsultaDto flx: fluxosDto) {
						resultat.add(new FieldOption(flx.getId(), flx.getText()));
					}
				}
			} catch (Exception ex) {
				excepcioLogHelper.addExcepcio("/dada/"+fieldName+"/DominiOptionsProvider", ex);
			}
			return resultat;
		}
	}
    
    @Override
    protected void beforeCreateSave(DadaResourceEntity entity, DadaResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        updateOrder(entity, entity.getOrdre());
        beforeSave(entity, resource, answers);
    }

    @Override
    protected void beforeUpdateSave(DadaResourceEntity entity, DadaResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        beforeSave(entity, resource, answers);
    }

    private void beforeSave(DadaResourceEntity entity, DadaResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        MetaDadaResourceEntity metaDadaResourceEntity = metaDadaResourceRepository.findById(resource.getMetaDada().getId()).get();

        String value = resource.getValueByFieldName(metaDadaResourceEntity.getTipus());
        entity.setValor(value);
    }

    @Override
    protected void afterDelete(DadaResourceEntity entity, Map<String, AnswerRequiredException.AnswerValue> answers) {
        updateOrder(entity, null);
    }

    @Override
    protected void afterConversion(DadaResourceEntity entity, DadaResource resource) {
        resource.setValueByFieldName(entity.getMetaDada().getTipus(), entity.getValor());
    }

    private void updateOrder(DadaResourceEntity entity, Integer position) {
        List<DadaResourceEntity> dadaResourceEntityList =
                dadaResourceRepository.findAllByNodeIdAndMetaDadaIdOrderByOrdreAsc(entity.getNode().getId(), entity.getMetaDada().getId())
                        .stream().filter(dada->dada!=entity)
                        .collect(Collectors.toList());

        int count = 0;
        for (DadaResourceEntity dadaResourceEntity : dadaResourceEntityList) {
            if (position != null && position == count) {
                entity.setOrdre(count);
                count++;
            }

            dadaResourceEntity.setOrdre(count);
            count++;
        }

        if (position == null) {
            entity.setOrdre(count);
        }

        dadaResourceRepository.saveAll(dadaResourceEntityList);
    }
}
