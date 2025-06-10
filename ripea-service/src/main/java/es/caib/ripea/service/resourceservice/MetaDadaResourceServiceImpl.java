package es.caib.ripea.service.resourceservice;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import es.caib.ripea.persistence.entity.resourceentity.DadaResourceEntity;
import org.springframework.stereotype.Service;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaDadaResourceEntity;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.DominiHelper;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.dto.MetaDadaTipusEnumDto;
import es.caib.ripea.service.intf.dto.ResultatConsultaDto;
import es.caib.ripea.service.intf.model.DadaResource;
import es.caib.ripea.service.intf.model.MetaDadaResource;
import es.caib.ripea.service.intf.resourceservice.MetaDadaResourceService;
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
public class MetaDadaResourceServiceImpl extends BaseMutableResourceService<MetaDadaResource, Long, MetaDadaResourceEntity> implements MetaDadaResourceService {

	private final DominiHelper dominiHelper;
	private final ConfigHelper configHelper;
	private final EntitatRepository entitatRepository;
	
    @PostConstruct
    public void init() {
        register(MetaDadaResource.PERSPECTIVE_DADES, new CountPerspectiveApplicator());
    }

    // PerspectiveApplicator
    private class CountPerspectiveApplicator implements PerspectiveApplicator<MetaDadaResourceEntity, MetaDadaResource> {

        @Override
        public void applySingle(String code, MetaDadaResourceEntity metaDadaEntity, MetaDadaResource resource) throws PerspectiveApplicationException {
        	List<DadaResource> dadaResourceList = metaDadaEntity.getDades().stream()
                    .map(dadaResource->objectMappingHelper.newInstanceMap(dadaResource, DadaResource.class))
                    .collect(Collectors.toList());

        	if (!dadaResourceList.isEmpty() && MetaDadaTipusEnumDto.DOMINI.equals(metaDadaEntity.getTipus())) {
        		//Carregar la descripció del valor del domini
        		EntitatEntity entitatEntity = entitatRepository.findByCodi(configHelper.getEntitatActualCodi());
        		for (DadaResource dr: dadaResourceList) {
                    List<ResultatConsultaDto> dominiValors = dominiHelper.getResultDomini(
                            entitatEntity.getId(),
                            metaDadaEntity.getCodi(),
                            "",
                            1,
                            Integer.MAX_VALUE).getResultat();

                    ResultatConsultaDto resultatConsultaDto = new ResultatConsultaDto();
                    resultatConsultaDto.setId("NO_APLICA");
                    resultatConsultaDto.setText("No aplica");
                    dominiValors.add(resultatConsultaDto);
                    for (ResultatConsultaDto valorOptionDomini : dominiValors) {
                        if (valorOptionDomini.getId().equals(dr.getValor())) {
                            dr.setDominiDescription(valorOptionDomini.getText());
                            break;
                        }
                    }
                }
        	}

            resource.setDades(dadaResourceList);
        }
    }
    
}