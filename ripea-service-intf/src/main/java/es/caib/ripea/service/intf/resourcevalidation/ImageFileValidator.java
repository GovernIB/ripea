package es.caib.ripea.service.intf.resourcevalidation;

import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import es.caib.ripea.service.intf.base.model.FileReference;
import es.caib.ripea.service.intf.utils.Utils;

public class ImageFileValidator implements ConstraintValidator<ValidImageFile, FileReference> {

    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
        "image/jpeg",
        "image/png",
        "image/gif",
        "image/bmp",
        "image/webp"
    );

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
        "jpg", "jpeg", "png", "gif", "bmp", "webp"
    );

    @Override
    public void initialize(ValidImageFile constraintAnnotation) {
    }

    @Override
    public boolean isValid(FileReference fileReference, ConstraintValidatorContext context) {
        // Si es null, se considera válido (usar @NotNull si es obligatorio)
        if (fileReference == null) {
            return true;
        } else if (fileReference.getContent()!=null && fileReference.getContent().length>0 && !Utils.hasValue(fileReference.getContentType())) {
        	//Caso: Se viene de un metodo PATCH o de un UPDATE, no tenemos información del tipo de fichero
        	return true;
        }
        
        String fileName = fileReference.getName();
        String mimeType = fileReference.getContentType();

        // Validar por extensión
        if (fileName != null) {
            String extension = fileName.contains(".")
                ? fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase()
                : "";
            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                return false;
            }
        }

        // Validar por MIME type
        if (mimeType != null && !ALLOWED_MIME_TYPES.contains(mimeType.toLowerCase())) {
            return false;
        }

        return true;
    }
}