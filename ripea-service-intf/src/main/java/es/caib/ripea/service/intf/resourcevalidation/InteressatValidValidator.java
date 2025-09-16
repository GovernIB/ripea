package es.caib.ripea.service.intf.resourcevalidation;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import es.caib.ripea.service.intf.dto.EntregaPostalTipusEnum;
import org.springframework.stereotype.Component;

import es.caib.ripea.service.intf.dto.InteressatDocumentTipusEnumDto;
import es.caib.ripea.service.intf.dto.InteressatTipusEnum;
import es.caib.ripea.service.intf.model.InteressatResource;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InteressatValidValidator implements ConstraintValidator<InteressatValid, InteressatResource> {

    private void addViolation(ConstraintValidatorContext context, String field, String message) {
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(field)
                .addConstraintViolation();
    }
    private void notNullViolation(ConstraintValidatorContext context, String field) {
        addViolation(context, field, "{javax.validation.constraints.NotNull.message}");
    }

    @Override
    public boolean isValid(InteressatResource resource, ConstraintValidatorContext context) {
        boolean valid = true;
        context.disableDefaultConstraintViolation();

        if (resource.getEntregaDeh() != null && resource.getEntregaDeh()) {
            if (resource.getEmail() == null || resource.getEmail().isBlank()) {
                notNullViolation(context, InteressatResource.Fields.email);
                valid = false;
            }
        }
        if (InteressatTipusEnum.InteressatPersonaFisicaEntity.equals(resource.getTipus())) {
            if (resource.getNom() == null || resource.getNom().isBlank()) {
                notNullViolation(context, InteressatResource.Fields.nom);
                valid = false;
            }
            if (resource.getLlinatge1() == null || resource.getLlinatge1().isBlank()) {
                notNullViolation(context, InteressatResource.Fields.llinatge1);
                valid = false;
            }
        }
        if (InteressatTipusEnum.InteressatAdministracioEntity.equals(resource.getTipus())) {
            if (resource.getOrganCodi() == null) {
                notNullViolation(context, InteressatResource.Fields.organCodi);
                valid = false;
            }
        }
        if (InteressatTipusEnum.InteressatPersonaJuridicaEntity.equals(resource.getTipus())) {
            if (resource.getRaoSocial() == null) {
                notNullViolation(context, InteressatResource.Fields.raoSocial);
                valid = false;
            }
        }
        if (!InteressatTipusEnum.InteressatAdministracioEntity.equals(resource.getTipus())) {
            if (!EntregaPostalTipusEnum.SENSE_NORMALITZAR.equals(resource.getAdressaTipus())) {
                if (resource.getAdressaTipusVia() == null) {
                    notNullViolation(context, InteressatResource.Fields.adressaTipusVia);
                    valid = false;
                }
                if (resource.getAdresa() == null || resource.getAdresa().isBlank()) {
                    notNullViolation(context, InteressatResource.Fields.adresa);
                    valid = false;
                }
                if (resource.getAdressaNumCasa() == null || resource.getAdressaNumCasa().isBlank()) {
                    notNullViolation(context, InteressatResource.Fields.adressaNumCasa);
                    valid = false;
                }
            }

            if (resource.getDocumentNum() == null || resource.getDocumentNum().isBlank()) {
                notNullViolation(context, InteressatResource.Fields.documentNum);
                valid = false;
            } else {
                boolean validDocumentNum = true;

                if (resource.getDocumentTipus() == InteressatDocumentTipusEnumDto.NIF) {
                    if (resource.getTipus() == InteressatTipusEnum.InteressatPersonaFisicaEntity) {
                        if (!validarNIF(resource.getDocumentNum())) {
                            validDocumentNum = false;
                        }
                    } else {
                        if (!validarCIF(resource.getDocumentNum())) {
                            validDocumentNum = false;
                        }
                    }
                } else if (resource.getDocumentTipus() == InteressatDocumentTipusEnumDto.DOCUMENT_IDENTIFICATIU_ESTRANGERS) {
                    if (!validarNIE(resource.getDocumentNum())) {
                        validDocumentNum = false;
                    }
                }

                if ((resource.getRepresentat() != null && Objects.equals(resource.getRepresentat().getId(), resource.getId()))
                        || (resource.getRepresentant() != null && Objects.equals(resource.getRepresentant().getId(), resource.getId()))) {
                    validDocumentNum = false;
                }

                if (!validDocumentNum) {
                    addViolation(context, InteressatResource.Fields.documentNum,
                            "{es.caib.ripea.service.intf.resourcevalidation.InteressatValid.documentNum}");
                    valid = false;
                }
            }
        }

        /*if (!resource.isEsRepresentant()) {
            List<InteressatResource> interesados = interessatResourceService.findBySpringFilter(
                    "expedient.id : " + resource.getExpedient().getId() + " and esRepresentant : false"
            );

            for (InteressatResource interesado : interesados) {
                if ((resource.getId() == null || !Objects.equals(resource.getId(), interesado.getId())) && Objects.equals(resource.getDocumentNum(), interesado.getDocumentNum())) {
                    context
                            .buildConstraintViolationWithTemplate("{es.caib.ripea.service.intf.resourcevalidation.InteressatValid.documentNumExists}")
                            .addPropertyNode(InteressatResource.Fields.documentNum)
                            .addConstraintViolation()
                            .disableDefaultConstraintViolation();
                    valid = false;
                    break;
                }
            }
        }*/

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

    public static boolean validarNIE(String nie) {
        if (nie == null || !nie.matches("^[XYZxyz]\\d{7}[A-Za-z]$")) {
            return false;
        }

        nie = nie.toUpperCase();
        char letraInicial = nie.charAt(0);
        String nieNum;
        switch (letraInicial) {
            case 'X':
                nieNum = "0" + nie.substring(1, 8);
                break;
            case 'Y':
                nieNum = "1" + nie.substring(1, 8);
                break;
            case 'Z':
                nieNum = "2" + nie.substring(1, 8);
                break;
            default:
                return false;
        }

        int numero = Integer.parseInt(nieNum);
        char letraControl = "TRWAGMYFPDXBNJZSQVHLCKE".charAt(numero % 23);

        return letraControl == nie.charAt(8);
    }

    public static boolean validarCIF(String cif) {
        if (cif == null || !cif.matches("^[ABCDEFGHJKLMNPQRSUVW]\\d{7}[0-9A-J]$")) {
            return false;
        }

        cif = cif.toUpperCase();
        int sumaPar = 0;
        int sumaImpar = 0;

        // posiciones impares (1, 3, 5)
        for (int i = 1; i < 8; i += 2) {
            int n = Character.getNumericValue(cif.charAt(i));
            int doble = n * 2;
            sumaImpar += (doble / 10) + (doble % 10);
        }

        // posiciones pares (2, 4, 6)
        for (int i = 2; i < 7; i += 2) {
            sumaPar += Character.getNumericValue(cif.charAt(i));
        }

        int sumaTotal = sumaPar + sumaImpar;
        int digitoControl = (10 - (sumaTotal % 10)) % 10;

        char letraControl = cif.charAt(8);
        char letraEsperada = "JABCDEFGHI".charAt(digitoControl);

        if (Character.isDigit(letraControl)) {
            return letraControl == Character.forDigit(digitoControl, 10);
        } else {
            return letraControl == letraEsperada;
        }
    }
}
