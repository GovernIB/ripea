package es.caib.ripea.service.intf.resourcevalidation;

import es.caib.ripea.service.intf.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.service.intf.model.DocumentResource.EnviarPortafirmesFormAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
@RequiredArgsConstructor
public class EnviarPortafirmesValidator implements ConstraintValidator<EnviarPortafirmesValid, EnviarPortafirmesFormAction>{

	@Override
	public boolean isValid(EnviarPortafirmesFormAction resource, ConstraintValidatorContext context) {
        boolean valid = true;

        if (MetaDocumentFirmaFluxTipusEnumDto.SIMPLE.equals(resource.getPortafirmesFluxTipus())){
            if (resource.getPortafirmesSequenciaTipus() == null){
                context
                        .buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
                        .addPropertyNode(EnviarPortafirmesFormAction.Fields.portafirmesSequenciaTipus)
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                valid = false;
            }
        }

        if (MetaDocumentFirmaFluxTipusEnumDto.PORTAFIB.equals(resource.getPortafirmesFluxTipus())){
            if (resource.getAnnexos() == null || resource.getAnnexos().isEmpty()){
                context
                        .buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
                        .addPropertyNode(EnviarPortafirmesFormAction.Fields.annexos)
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                valid = false;
            }
            if (resource.getPortafirmesEnviarFluxId() == null || resource.getPortafirmesEnviarFluxId().isBlank()){
                context
                        .buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
                        .addPropertyNode(EnviarPortafirmesFormAction.Fields.portafirmesEnviarFluxId)
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                valid = false;
            }
        }

        return valid;
	}
}