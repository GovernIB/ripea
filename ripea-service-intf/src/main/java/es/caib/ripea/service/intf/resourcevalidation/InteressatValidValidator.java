package es.caib.ripea.service.intf.resourcevalidation;

import es.caib.ripea.service.intf.dto.InteressatDocumentTipusEnumDto;
import es.caib.ripea.service.intf.model.InteressatResource;
import es.caib.ripea.service.intf.resourceservice.InteressatResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class InteressatValidValidator implements ConstraintValidator<InteressatValid, InteressatResource> {

    private final InteressatResourceService interessatResourceService;

    @Override
    public boolean isValid(InteressatResource resource, ConstraintValidatorContext context) {
        boolean valid = true;

        if (!resource.isEsRepresentant()) {
            List<InteressatResource> interesados = interessatResourceService.findBySpringFilter(
                    "expedient.id : " + resource.getExpedient().getId() + " and esRepresentant : false"
            );

            for (InteressatResource interesado : interesados) {
                if (Objects.equals(resource.getDocumentNum(), interesado.getDocumentNum())) {
                    context
                            .buildConstraintViolationWithTemplate("{es.caib.ripea.service.intf.resourcevalidation.InteressatValid.documentNumExists}")
                            .addPropertyNode(InteressatResource.Fields.documentNum)
                            .addConstraintViolation()
                            .disableDefaultConstraintViolation();
                    valid = false;
                    break;
                }
            }
        }

        if (
                (resource.getRepresentat()!=null && Objects.equals(resource.getRepresentat().getId(), resource.getId()))
                || (resource.getRepresentant()!=null && Objects.equals(resource.getRepresentant().getId(), resource.getId()))
                || (resource.getDocumentTipus() == InteressatDocumentTipusEnumDto.NIF && !validarNIF(resource.getDocumentNum()))
        ) {
            context
                    .buildConstraintViolationWithTemplate("{es.caib.ripea.service.intf.resourcevalidation.InteressatValid.documentNum}")
                    .addPropertyNode(InteressatResource.Fields.documentNum)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        }

        return valid;
    }

    public static boolean validarNIF(String nif) {
        nif = nif.toUpperCase().trim();

        // Expresión regular para NIF (DNI) y NIE
        Pattern patronNIF = Pattern.compile("^(\\d{8})([A-Z])$");
        Pattern patronNIE = Pattern.compile("^[XYZ]\\d{7}[A-Z]$");

        // Tabla de letras de control para DNI
        String letrasControl = "TRWAGMYFPDXBNJZSQVHLCKE";

        Matcher matcherNIF = patronNIF.matcher(nif);
        if (matcherNIF.matches()) {
            int numero = Integer.parseInt(matcherNIF.group(1));
            char letraCalculada = letrasControl.charAt(numero % 23);
            return letraCalculada == matcherNIF.group(2).charAt(0);
        }

        Matcher matcherNIE = patronNIE.matcher(nif);
        if (matcherNIE.matches()) {
            char primerCaracter = nif.charAt(0);
            String numero;
            if (primerCaracter == 'X') {
                numero = "0" + nif.substring(1, 8);
            } else if (primerCaracter == 'Y') {
                numero = "1" + nif.substring(1, 8);
            } else { // 'Z'
                numero = "2" + nif.substring(1, 8);
            }

            int num = Integer.parseInt(numero);
            char letraCalculada = letrasControl.charAt(num % 23);
            return letraCalculada == nif.charAt(8);
        }

        return false;
    }
}
