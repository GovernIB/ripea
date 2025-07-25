package es.caib.ripea.service.intf.resourcevalidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;

import es.caib.ripea.service.intf.dto.ViaFirmaTipusDestinatariEnum;
import es.caib.ripea.service.intf.model.DocumentResource.ViaFirmaForm;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ViaFirmaValidator implements ConstraintValidator<ViaFirmaValid, ViaFirmaForm>{@Override
	public boolean isValid(ViaFirmaForm value, ConstraintValidatorContext context) {
		boolean valid = true;
		if (ViaFirmaTipusDestinatariEnum.EMAIL.equals(value.getTipusDestinatari())) {
			if (!Utils.hasValue(value.getSignantEmail())) {
                context
                .buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
                .addPropertyNode(ViaFirmaForm.Fields.signantEmail)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
                valid = false;
			}
		} else {
			if (!Utils.hasValue(value.getCodiUsuariViaFirma())) {
                context
                .buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
                .addPropertyNode(ViaFirmaForm.Fields.codiUsuariViaFirma)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
                valid = false;
			}
		}
		return valid;
	}
}