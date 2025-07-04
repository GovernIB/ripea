package es.caib.ripea.service.intf.resourcevalidation;

import es.caib.ripea.service.intf.dto.TipusImportEnumDto;
import es.caib.ripea.service.intf.model.ExpedientResource.ImportarDocumentsForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
@RequiredArgsConstructor
public class ImportarDocumentValidator implements ConstraintValidator<ImportarDocumentValid, ImportarDocumentsForm>{

	@Override
	public boolean isValid(ImportarDocumentsForm resource, ConstraintValidatorContext context) {
        boolean valid = true;

        if (TipusImportEnumDto.NUMERO_REGISTRE.equals(resource.getTipusImportacio())){
            if (resource.getNumeroRegistre() == null || resource.getNumeroRegistre().isBlank()){
                context
                        .buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
                        .addPropertyNode(ImportarDocumentsForm.Fields.numeroRegistre)
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                valid = false;
            }
            if (resource.getDataPresentacio() == null){
                context
                        .buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
                        .addPropertyNode(ImportarDocumentsForm.Fields.dataPresentacio)
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                valid = false;
            }
        }

        if (TipusImportEnumDto.CODI_ENI.equals(resource.getTipusImportacio())){
            if (resource.getCodiEni() == null || resource.getCodiEni().isBlank()){
                context
                        .buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
                        .addPropertyNode(ImportarDocumentsForm.Fields.codiEni)
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                valid = false;
            }
        }

        return valid;
	}
}