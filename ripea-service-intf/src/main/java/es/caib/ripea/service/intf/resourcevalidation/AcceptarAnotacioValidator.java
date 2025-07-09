package es.caib.ripea.service.intf.resourcevalidation;

import es.caib.ripea.service.intf.dto.ExpedientPeticioAccioEnumDto;
import es.caib.ripea.service.intf.dto.PrioritatEnumDto;
import es.caib.ripea.service.intf.model.ExpedientPeticioResource.AcceptarAnotacioForm;
import es.caib.ripea.service.intf.model.ExpedientResource;
import es.caib.ripea.service.intf.resourceservice.ExpedientPeticioResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
@RequiredArgsConstructor
public class AcceptarAnotacioValidator implements ConstraintValidator<AcceptarAnotacioValid, AcceptarAnotacioForm>{

	@Override
	public boolean isValid(AcceptarAnotacioForm resource, ConstraintValidatorContext context) {
        boolean valid = true;

        if (ExpedientPeticioAccioEnumDto.CREAR.equals(resource.getAccio())){
            if (resource.getNewExpedientTitol() == null || resource.getNewExpedientTitol().trim().isEmpty()){
                context
                        .buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
                        .addPropertyNode(AcceptarAnotacioForm.Fields.newExpedientTitol)
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                valid = false;
            }
            if (resource.getPrioritat() == null){
                context
                        .buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
                        .addPropertyNode(AcceptarAnotacioForm.Fields.prioritat)
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                valid = false;
            }
            if (!PrioritatEnumDto.B_NORMAL.equals(resource.getPrioritat())
                    && (resource.getPrioritatMotiu() == null || resource.getPrioritatMotiu().isBlank())){
                context
                        .buildConstraintViolationWithTemplate("{es.caib.ripea.service.intf.resourcevalidation.ExpedientValidator.prioritat}")
                        .addPropertyNode(ExpedientResource.Fields.prioritatMotiu)
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                valid = false;
            }
            if (resource.getAny() == null){
                context
                        .buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
                        .addPropertyNode(AcceptarAnotacioForm.Fields.any)
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                valid = false;
            }
            if (resource.getSequencia() == null){
                context
                        .buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
                        .addPropertyNode(AcceptarAnotacioForm.Fields.sequencia)
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                valid = false;
            }
            if (resource.isGestioAmbGrupsActiva() && resource.getGrup() == null){
                context
                        .buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
                        .addPropertyNode(AcceptarAnotacioForm.Fields.grup)
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                valid = false;
            }
        }

        if (ExpedientPeticioAccioEnumDto.INCORPORAR.equals(resource.getAccio())){
            if (resource.getExpedient() == null){
                context
                        .buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
                        .addPropertyNode(AcceptarAnotacioForm.Fields.expedient)
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                valid = false;
            }
        }

        boolean hasBlank = resource.getAnnexos().values().stream()
                .anyMatch(value -> value == null || value.trim().isEmpty());
        if (hasBlank) {
            context
                    .buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
                    .addPropertyNode(AcceptarAnotacioForm.Fields.annexos)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        }
        return valid;
	}
}