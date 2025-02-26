package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.ExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.InteressatResourceRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.model.ExpedientResource;
import es.caib.ripea.service.intf.model.InteressatResource;
import es.caib.ripea.service.intf.resourceservice.ExpedientResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementació del servei de gestió d'expedients.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExpedientResourceServiceImpl extends BaseMutableResourceService<ExpedientResource, Long, ExpedientResourceEntity> implements ExpedientResourceService {

    private final InteressatResourceRepository interessatResourceRepository;

    @PostConstruct
	public void init() {
	}

    @Override
    protected ExpedientResource applyPerspectives(ExpedientResourceEntity entity, ExpedientResource resource, String[] perspectives) {
        if(Arrays.asList(perspectives).contains("INTERESSATS_RESUM")){
            String interessatsResum = "";
            List<InteressatResource> interessats= interessatResourceRepository.findByExpedient(entity).stream()
                    .map(interessatResourceEntity -> objectMappingHelper.newInstanceMap(interessatResourceEntity, InteressatResource.class))
                    .collect(Collectors.toList());

            if (!interessats.isEmpty()) {
                for (InteressatResource interessat : interessats) {
                    switch (interessat.getTipus()){
                        case InteressatPersonaFisicaEntity:
                            interessatsResum += interessat.getNom() == null ? "" : interessat.getNom() + " ";
                            interessatsResum += interessat.getLlinatge1() == null ? "" : interessat.getLlinatge1() + " ";
                            interessatsResum += interessat.getLlinatge2() == null ? "" : interessat.getLlinatge2() + " ";
                            interessatsResum += "(" + interessat.getDocumentNum() + ")" + "\n";
                            break;
                        case InteressatPersonaJuridicaEntity:
                            interessatsResum += interessat.getRaoSocial() + " ";
                            interessatsResum += "(" + interessat.getDocumentNum() + ")" + "\n";
                            break;
                        case InteressatAdministracioEntity:
                            interessatsResum += interessat.getNomComplet() + " ";
                            interessatsResum += "(" + interessat.getDocumentNum() + ")" + "\n";
                            break;
                    }
                }
            }

            resource.setInteressatsResum(interessatsResum);
        }

        return resource;
    }
}
