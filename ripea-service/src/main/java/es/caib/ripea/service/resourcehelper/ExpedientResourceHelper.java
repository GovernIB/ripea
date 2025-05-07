package es.caib.ripea.service.resourcehelper;

import es.caib.ripea.persistence.entity.resourceentity.ExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.DocumentResourceRepository;
import es.caib.ripea.service.helper.ContingutHelper;
import es.caib.ripea.service.intf.model.ExpedientResource;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExpedientResourceHelper {

    private final DocumentResourceRepository documentResourceRepository;

    private final CacheResourceHelper cacheResourceHelper;
    private final ContingutHelper contingutHelper;

    public void setPotTancar(ExpedientResourceEntity entity, ExpedientResource resource){
        this.setExpedientEstatDocuments(entity, resource);
        resource.setErrors(cacheResourceHelper.findErrorsValidacioPerNode(entity));
        resource.setValid(resource.getErrors().isEmpty());

        resource.setPotTancar(
                resource.isValid()
                        && resource.isConteDocuments()
                        && !resource.isConteDocumentsEnProcessDeFirma()
                        && !resource.isConteDocumentsDePortafirmesNoCustodiats()
                        && !resource.isConteDocumentsPendentsReintentsArxiu()
        );
    }

    public void setExpedientEstatDocuments(ExpedientResourceEntity entity, ExpedientResource resource) {
        resource.setConteDocuments(CollectionUtils.isNotEmpty(documentResourceRepository.findByExpedientAndEsborrat(entity, 0)));
        resource.setConteDocumentsDefinitius(documentResourceRepository.expedientHasDocumentsDefinitius(entity));
        resource.setConteDocumentsEnProcessDeFirma(CollectionUtils.isNotEmpty(documentResourceRepository.findEnProccessDeFirma(entity)));
        resource.setConteDocumentsDePortafirmesNoCustodiats(CollectionUtils.isNotEmpty(documentResourceRepository.findDocumentsDePortafirmesNoCustodiats(entity)));
        resource.setConteDocumentsPendentsReintentsArxiu(CollectionUtils.isNotEmpty(documentResourceRepository.findDocumentsPendentsReintentsArxiu(entity, contingutHelper.getArxiuMaxReintentsDocuments())));
//        resource.setConteDocumentsDeAnotacionesNoMogutsASerieFinal(CollectionUtils.isNotEmpty(registreAnnexResourceRepository.findDocumentsDeAnotacionesNoMogutsASerieFinal(entity)));
    }
}
