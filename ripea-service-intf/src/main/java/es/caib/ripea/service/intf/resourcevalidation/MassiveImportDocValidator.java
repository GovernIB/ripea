package es.caib.ripea.service.intf.resourcevalidation;

import es.caib.ripea.service.intf.model.ExpedientResource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
@RequiredArgsConstructor
public class MassiveImportDocValidator implements ConstraintValidator<MassiveImportDocValid, ExpedientResource.MassiveImportDocsAction>{

	@Override
	public boolean isValid(ExpedientResource.MassiveImportDocsAction resource, ConstraintValidatorContext context) {
        boolean valid = true;
        if (!resource.isTotsExpedientsMateixProcediment()) {
            context
                    .buildConstraintViolationWithTemplate("{javax.validation.constraints.AssertTrue.message}")
                    .addPropertyNode(ExpedientResource.MassiveImportDocsAction.Fields.totsExpedientsMateixProcediment)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        }

        if ( resource.getDocuments().stream()
                .anyMatch(doc -> doc.getFitxer() == null)
        ){
            context
                    .buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
                    .addPropertyNode(ExpedientResource.MassiveImportDocsAction.Fields.file)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        }

        if ( resource.getDocuments().stream()
                .anyMatch(doc -> doc.getTipusDocument() == null)
        ){
            context
                    .buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
                    .addPropertyNode(ExpedientResource.MassiveImportDocsAction.Fields.tipusDocument)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        }

        return valid;
	}
}