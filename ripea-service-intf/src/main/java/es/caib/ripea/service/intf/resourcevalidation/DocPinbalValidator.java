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
        if (codiActual!=null && codisRequereixenCamp.contains(codiActual)) {
            boolean isEmpty = false;

            if (valor == null) {
                isEmpty = true;
            } else if (valor instanceof String) {
                isEmpty = ((String) valor).isBlank();
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
                        CodiServeiPinbal.SVDDELSEXWS01.name()
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
                resource.getMunicipi(),
                NewDocPinbalForm.Fields.municipi,
                context,
                codi,
                Set.of(
                        CodiServeiPinbal.SCDCPAJU.name(),
                        CodiServeiPinbal.SCDHPAJU.name()
                ),
                valid
        );

        validarCampObligatori(
                resource.getMunicipiNaixament(),
                NewDocPinbalForm.Fields.municipiNaixament,
                context,
                codi,
                Set.of(
                        CodiServeiPinbal.SVDDELSEXWS01.name()
                ),
                valid
        );

        validarCampObligatori(
                resource.getDataNaixement(),
                NewDocPinbalForm.Fields.dataNaixement,
                context,
                codi,
                Set.of(
                        CodiServeiPinbal.SVDDELSEXWS01.name()
                ),
                valid
        );

        validarCampObligatori(
                resource.getRegistreCivil(),
                NewDocPinbalForm.Fields.registreCivil,
                context,
                codi,
                Set.of(
                        CodiServeiPinbal.SVDRRCCNACIMIENTOWS01.name(),
                        CodiServeiPinbal.SVDRRCCMATRIMONIOWS01.name()
                ),
                valid
        );

        validarCampObligatori(
                resource.getTom(),
                NewDocPinbalForm.Fields.tom,
                context,
                codi,
                Set.of(
                        CodiServeiPinbal.SVDRRCCNACIMIENTOWS01.name(),
                        CodiServeiPinbal.SVDRRCCMATRIMONIOWS01.name()
                ),
                valid
        );

        validarCampObligatori(
                resource.getPagina(),
                NewDocPinbalForm.Fields.pagina,
                context,
                codi,
                Set.of(
                        CodiServeiPinbal.SVDRRCCNACIMIENTOWS01.name(),
                        CodiServeiPinbal.SVDRRCCMATRIMONIOWS01.name()
                ),
                valid
        );

        validarCampObligatori(
                resource.getDataRegistre(),
                NewDocPinbalForm.Fields.dataRegistre,
                context,
                codi,
                Set.of(
                        CodiServeiPinbal.SVDRRCCNACIMIENTOWS01.name(),
                        CodiServeiPinbal.SVDRRCCMATRIMONIOWS01.name()
                ),
                valid
        );

        validarCampObligatori(
                resource.getCurs(),
                NewDocPinbalForm.Fields.curs,
                context,
                codi,
                Set.of(
                        CodiServeiPinbal.SVDBECAWS01.name()
                ),
                valid
        );

        validarCampObligatori(
                resource.getExercici(),
                NewDocPinbalForm.Fields.exercici,
                context,
                codi,
                Set.of(
                        CodiServeiPinbal.NIVRENTI.name()
                ),
                valid
        );

        if(CodiServeiPinbal.SVDDGPRESIDENCIALEGALDOCWS01.name().equals(codi)){
            if (resource.getTipusPassaport()==null && (resource.getNumeroSoporte()==null || resource.getNumeroSoporte().isBlank())){
                context.buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
                        .addPropertyNode(NewDocPinbalForm.Fields.numeroSoporte)
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
                        .addPropertyNode(NewDocPinbalForm.Fields.tipusPassaport)
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                valid.set(false);
            }

            if (resource.getTipusPassaport()!=null){
                if(resource.getNacionalitat()==null || resource.getNacionalitat().isBlank()){
                    context.buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
                            .addPropertyNode(NewDocPinbalForm.Fields.nacionalitat)
                            .addConstraintViolation()
                            .disableDefaultConstraintViolation();
                    valid.set(false);
                }
                if(resource.getDataCaducidad()==null){
                    context.buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
                            .addPropertyNode(NewDocPinbalForm.Fields.dataCaducidad)
                            .addConstraintViolation()
                            .disableDefaultConstraintViolation();
                    valid.set(false);
                }
            }
        }
        if(CodiServeiPinbal.SVDDELSEXWS01.name().equals(codi)){
            if (
                    (resource.getNomPare()==null || resource.getNomPare().isBlank())
                    && (resource.getNomMare()==null || resource.getNomMare().isBlank())
            ){
                context.buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
                        .addPropertyNode(NewDocPinbalForm.Fields.nomPare)
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();

                context.buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
                        .addPropertyNode(NewDocPinbalForm.Fields.nomMare)
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                valid.set(false);
            }
        }

        return valid.get();
	}
}