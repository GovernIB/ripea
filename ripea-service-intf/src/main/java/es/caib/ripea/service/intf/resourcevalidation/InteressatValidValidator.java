package es.caib.ripea.service.intf.resourcevalidation;

import es.caib.ripea.service.intf.dto.InteressatDocumentTipusEnumDto;
import es.caib.ripea.service.intf.model.InteressatResource;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InteressatValidValidator implements ConstraintValidator<InteressatValid, InteressatResource> {

    @Override
    public void initialize(InteressatValid constraintAnnotation) {}

    @Override
    public boolean isValid(InteressatResource value, ConstraintValidatorContext context) {
        context
            .buildConstraintViolationWithTemplate("{es.caib.ripea.service.intf.resourcevalidation.InteressatValid.documentNum}")
            .addPropertyNode("documentNum")
            .addConstraintViolation()
            .disableDefaultConstraintViolation();
        if (
                value.getRepresentat()!=null && Objects.equals(value.getRepresentat().getId(), value.getId())
                || value.getRepresentant()!=null && Objects.equals(value.getRepresentant().getId(), value.getId())
        ) {
            return false;
        }
        if (value.getDocumentTipus() == InteressatDocumentTipusEnumDto.NIF){
            return validarNIF(value.getDocumentNum());
        }
        return true;
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
