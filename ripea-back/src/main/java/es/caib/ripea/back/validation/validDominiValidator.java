/**
 * 
 */
package es.caib.ripea.back.validation;

import es.caib.ripea.back.command.DominiCommand;
import es.caib.ripea.back.helper.MessageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.StringReader;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi d'entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class validDominiValidator implements ConstraintValidator<validDomini, DominiCommand> {
	
	@Override
	public void initialize(final validDomini constraintAnnotation) {
	}

	@Override
	public boolean isValid(final DominiCommand value, final ConstraintValidatorContext context) {
		boolean validXml = true;
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
	        SAXParser sp = spf.newSAXParser();
	        XMLReader xr = sp.getXMLReader();
	        xr.parse(new InputSource(new StringReader(value.getCadena())));
        } catch (final Exception ex) {
        	LOGGER.error("XML no vàlid", ex);
        	validXml = false;
        	context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("domini.form.camp.cadena-validacio"))
			.addNode("cadena")
			.addConstraintViolation();
        }
		return validXml;
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(validDominiValidator.class);

}
