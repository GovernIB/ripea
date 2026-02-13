package es.caib.ripea.service.intf.resourcevalidation;

import es.caib.ripea.service.intf.dto.TipusImportEnumDto;
import es.caib.ripea.service.intf.model.ExpedientResource.ImportarDocumentsZipForm;
import es.caib.ripea.service.intf.model.ImportacioZipDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
@RequiredArgsConstructor
public class ImportarDocumentsZipValidator implements ConstraintValidator<ImportarDocumentsZipValid, ImportarDocumentsZipForm>{

	@Override
	public boolean isValid(ImportarDocumentsZipForm resource, ConstraintValidatorContext context) {
        boolean valid = true;

        if (resource.getDocumentsZip() != null) {
            if (resource.getDocumentsZip().stream().noneMatch(ImportacioZipDocument::isImportar)) {
                context
                        .buildConstraintViolationWithTemplate("{javax.validation.constraints.NotEmpty.message}")
                        .addPropertyNode(ImportarDocumentsZipForm.Fields.documentsZip)
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                valid = false;
            }

            if (resource.getDocumentsZip().stream().anyMatch(d -> d.getNom() == null || d.getNom().isBlank())) {
                context
                        .buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
                        .addPropertyNode(ImportarDocumentsZipForm.Fields.nom)
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                valid = false;
            }

            if (resource.getDocumentsZip().stream().anyMatch(d -> d.getTipusDocument() == null)) {
                context
                        .buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
                        .addPropertyNode(ImportarDocumentsZipForm.Fields.tipusDocument)
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                valid = false;
            }
        }

        return valid;
	}
}