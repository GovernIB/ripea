package es.caib.ripea.service.intf.resourcevalidation;

import java.util.Objects;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;

import es.caib.ripea.service.intf.dto.EntregaPostalTipusEnum;
import es.caib.ripea.service.intf.dto.InteressatDocumentTipusEnumDto;
import es.caib.ripea.service.intf.dto.InteressatTipusEnum;
import es.caib.ripea.service.intf.model.InteressatResource;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InteressatValidValidator implements ConstraintValidator<InteressatValid, InteressatResource> {

    private static final Pattern CODI_POSTAL_PATTERN = Pattern.compile("^\\d{5}$");

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
            if (resource.isEsRepresentant()) {
                addViolation(context, InteressatResource.Fields.tipus,
                        "{es.caib.ripea.service.intf.resourcevalidation.InteressatValid.repAdmin}");
                valid = false;
            }
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

            if (resource.getCodiPostal() != null && !resource.getCodiPostal().isBlank()
                    && !CODI_POSTAL_PATTERN.matcher(resource.getCodiPostal()).matches()) {
                addViolation(context, InteressatResource.Fields.codiPostal,
                        "{es.caib.ripea.service.intf.resourcevalidation.InteressatValid.codiPostal}");
                valid = false;
            }

            if (resource.getDocumentNum() == null || resource.getDocumentNum().isBlank()) {
                notNullViolation(context, InteressatResource.Fields.documentNum);
                valid = false;
            } else {
                boolean validDocumentNum = true;

                if (resource.getDocumentTipus() == InteressatDocumentTipusEnumDto.NIF) {
                    if (resource.getTipus() == InteressatTipusEnum.InteressatPersonaFisicaEntity) {
                        if (!Utils.validacioDni(resource.getDocumentNum())) {
                            validDocumentNum = false;
                        }
                    } else {
                        if (!Utils.validacioCif(resource.getDocumentNum())) {
                            validDocumentNum = false;
                        }
                    }
                } else if (resource.getDocumentTipus() == InteressatDocumentTipusEnumDto.DOCUMENT_IDENTIFICATIU_ESTRANGERS) {
                    if (!Utils.validacioNie(resource.getDocumentNum())) {
                        validDocumentNum = false;
                    }
                } else if (resource.getDocumentTipus() == InteressatDocumentTipusEnumDto.ESTRANGER_EIDAS) {
                    if (!Utils.validacioEidas(resource.getDocumentNum())) {
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

        return valid;
    }
}