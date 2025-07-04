package es.caib.ripea.service.intf.resourcevalidation;

import es.caib.ripea.service.intf.model.DocumentResource.NewDocPinbalForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class DocPinbalValidator implements ConstraintValidator<DocPinbalValid, NewDocPinbalForm>{

    private enum CodiServeiPinbal {
        SVDCCAACPASWS01,
        SVDSCDDWS01,
        SCDCPAJU,
        SVDSCTFNWS01,
        SVDCCAACPCWS01,
        SVDDELSEXWS01,
        SCDHPAJU,
        NIVRENTI,
        SVDDGPRESIDENCIALEGALDOCWS01,
        SVDRRCCNACIMIENTOWS01,
        SVDRRCCMATRIMONIOWS01,
        SVDRRCCDEFUNCIONWS01,
        SVDBECAWS01,
    }

    private void validarCampObligatori(
            Object valor,
            String camp,
            ConstraintValidatorContext context,
            String codiActual,
            Set<String> codisRequereixenCamp,
            AtomicBoolean valid
    ) {
        if (codisRequereixenCamp.contains(codiActual)) {
            boolean isEmpty = false;


            if (valor == null) {
                isEmpty = true;
            } else if (valor instanceof String) {
                isEmpty = ((String) valor).isBlank();  // Java 11+, detecta espacios también
            } else if (valor instanceof Collection<?>) {
                isEmpty = ((Collection<?>) valor).isEmpty();
            } else if (valor.getClass().isArray()) {
                isEmpty = Array.getLength(valor) == 0;
            }

            if (isEmpty) {
                context.buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
                        .addPropertyNode(camp)
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                valid.set(false);
            }
        }
    }

	@Override
	public boolean isValid(NewDocPinbalForm resource, ConstraintValidatorContext context) {
        AtomicBoolean valid = new AtomicBoolean(true);

        String codi = resource.getCodiServeiPinbal();

        validarCampObligatori(
                resource.getComunitatAutonoma(),
                NewDocPinbalForm.Fields.comunitatAutonoma,
                context,
                codi,
                Set.of(
                        CodiServeiPinbal.SVDCCAACPASWS01.name(),
                        CodiServeiPinbal.SVDSCDDWS01.name(),
                        CodiServeiPinbal.SVDSCTFNWS01.name(),
                        CodiServeiPinbal.SVDCCAACPCWS01.name()
                ),
                valid
        );

        validarCampObligatori(
                resource.getProvincia(),
                NewDocPinbalForm.Fields.provincia,
                context,
                codi,
                Set.of(
                        CodiServeiPinbal.SVDCCAACPASWS01.name(),
                        CodiServeiPinbal.SVDSCDDWS01.name(),
                        CodiServeiPinbal.SCDCPAJU.name(),
                        CodiServeiPinbal.SVDCCAACPCWS01.name(),
                        CodiServeiPinbal.SCDHPAJU.name()
                ),
                valid
        );

        validarCampObligatori(
                resource.getConsentimentTipusDiscapacitat(),
                NewDocPinbalForm.Fields.consentimentTipusDiscapacitat,
                context,
                codi,
                Set.of(
                        CodiServeiPinbal.SVDSCDDWS01.name()
                ),
                valid
        );

        validarCampObligatori(
                resource.getNacionalitat(),
                NewDocPinbalForm.Fields.nacionalitat,
                context,
                codi,
                Set.of(
                        CodiServeiPinbal.SVDDELSEXWS01.name(),
                        CodiServeiPinbal.SVDDGPRESIDENCIALEGALDOCWS01.name()
                ),
                valid
        );

        validarCampObligatori(
                resource.getPaisNaixament(),
                NewDocPinbalForm.Fields.paisNaixament,
                context,
                codi,
                Set.of(
                        CodiServeiPinbal.SVDDELSEXWS01.name()
                ),
                valid
        );

        validarCampObligatori(
                resource.getProvinciaNaixament(),
                NewDocPinbalForm.Fields.provinciaNaixament,
                context,
                codi,
                Set.of(
                        CodiServeiPinbal.SVDDELSEXWS01.name()
                ),
                valid
        );

        validarCampObligatori(
                resource.getMunicipiNaixament(),
                NewDocPinbalForm.Fields.municipiNaixament,
                context,
                codi,
                Set.of(
                        CodiServeiPinbal.SVDDELSEXWS01.name(),
                        CodiServeiPinbal.SVDRRCCNACIMIENTOWS01.name()
                ),
                valid
        );

        return valid.get();
	}
}