package es.caib.ripea.back.validation;

import es.caib.ripea.back.command.InteressatCommand;
import es.caib.ripea.back.helper.MessageHelper;
import es.caib.ripea.service.intf.dto.InteressatDto;
import es.caib.ripea.service.intf.service.ExpedientInteressatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RepresentantNotSameInteressatValidator implements ConstraintValidator<RepresentantNotSameInteressat, InteressatCommand> {

    @Autowired
    private ExpedientInteressatService expedientInteressatService;
    @Autowired
    private HttpServletRequest request;

    @Override
    public void initialize(RepresentantNotSameInteressat representantNotSameInteressat) {

    }

    @Override
    public boolean isValid(InteressatCommand representant, ConstraintValidatorContext context) {
        try {
            if (representant.getInteressatId() != null) {
                InteressatDto interessatDto = expedientInteressatService.findById(representant.getInteressatId(), false);
                if (interessatDto.getDocumentNum().equals(representant.getDocumentNum())) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                                    MessageHelper.getInstance().getMessage("RepresentantSameInteressat", null, new RequestContext(request).getLocale()))
                            .addNode("documentNum")
                            .addConstraintViolation();
                    return false;
                }
            }
        } catch (final Exception ex) {
            LOGGER.error("Error al comprovar si el representant és el mateix que l'interessat", ex);
            return false;
        }
        return true;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(InteressatNoRepetitValidator.class);
}
