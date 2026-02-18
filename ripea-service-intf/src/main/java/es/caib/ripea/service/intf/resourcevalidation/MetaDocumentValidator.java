package es.caib.ripea.service.intf.resourcevalidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.ripea.service.intf.dto.MetaDocumentDto;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.service.intf.model.MetaDocumentResource;
import es.caib.ripea.service.intf.model.MetaNodeResource;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.MetaDocumentService;

public class MetaDocumentValidator implements ConstraintValidator<MetaDocumentValid, MetaDocumentResource>{
	
	@Autowired private MetaDocumentService metaDocumentService;
	@Autowired private AplicacioService aplicacioService;
	
	@Override
	public boolean isValid(MetaDocumentResource resource, ConstraintValidatorContext context) {
		
		boolean valid = true;
		
        //CodiMetaDocumentNoRepetit
        String codi = resource.getCodi();
        if (codi != null && !codi.isEmpty()) {
			final Long entitatId = resource.getEntitat()!=null?resource.getEntitat().getId():aplicacioService.getEntitatActualId();
			final Long metaExpedientId = resource.getMetaExpedient()!=null?resource.getMetaExpedient().getId():null;
			MetaDocumentDto metaDocument = metaDocumentService.findByCodi(
					entitatId,
					metaExpedientId,
					codi);
			//Hi ha un altre metadocument que no es l'actual, amb el mateix codi
			if (metaDocument != null && !metaDocument.getId().equals(resource.getId())) {
	            context
                .buildConstraintViolationWithTemplate("{MetaDocumentValidator.CodiMetaDocumentNoRepetit.message}")
                .addPropertyNode(MetaNodeResource.Fields.codi)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
	            valid = false;
			}
		}
		
		if (resource.isFirmaPortafirmesActiva()) {
			if (resource.getPortafirmesFluxTipus()==null) {
	            context
                .buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
                .addPropertyNode(MetaDocumentResource.Fields.portafirmesFluxTipus)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
	            valid = false;
			} else if (MetaDocumentFirmaFluxTipusEnumDto.SIMPLE.equals(resource.getPortafirmesFluxTipus())) {
				if (resource.getPortafirmesResponsables()==null || resource.getPortafirmesResponsables().size()==0) {
		            context
	                .buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
	                .addPropertyNode(MetaDocumentResource.Fields.portafirmesResponsables)
	                .addConstraintViolation()
	                .disableDefaultConstraintViolation();
		            valid = false;
				}
			} else if (MetaDocumentFirmaFluxTipusEnumDto.PORTAFIB.equals(resource.getPortafirmesFluxTipus())) {
				
			}
		}
		return valid;
	}
}