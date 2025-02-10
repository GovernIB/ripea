/**
 * 
 */
package es.caib.ripea.back.validation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.ripea.back.command.DescarregaCommand;
import es.caib.ripea.back.helper.MessageHelper;
import es.caib.ripea.service.intf.dto.ArbreJsonDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Constraint de validació que controla si s'ha seleccionat una carpeta per descarregar
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DescarregaEstructuraValidator implements ConstraintValidator<DescarregaEstructura, DescarregaCommand> {

	@Autowired
	private HttpServletRequest request;
	
	@Override
	public void initialize(final DescarregaEstructura constraintAnnotation) {
	}

	@Override
	public boolean isValid(final DescarregaCommand value, final ConstraintValidatorContext context) {
		boolean valid = true;
		
		try {
			String seleccio = value.getEstructuraCarpetesJson();
			
			if (seleccio != null && !seleccio.isEmpty()) {
				ObjectMapper objectMapper = new ObjectMapper();
				List<ArbreJsonDto> listCarpetes = objectMapper.readValue(seleccio, new TypeReference<List<ArbreJsonDto>>() {});

				List<ArbreJsonDto> listCarpetesSelected = filterSelectedNodes(listCarpetes);
				
				if (listCarpetesSelected == null || listCarpetesSelected.isEmpty()) {
					context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty", null, new RequestContext(request).getLocale()))
					.addNode("estructuraCarpetesJson")
					.addConstraintViolation();
					valid = false;
				}
				
				if (!valid)
					context.disableDefaultConstraintViolation();
				
				return valid;
			}
		} catch (final Exception ex) {
			LOGGER.error("Error en la validació del NIF", ex);
			return false;
		}
		
		return valid;
	}
	
	private static List<ArbreJsonDto> filterSelectedNodes(List<ArbreJsonDto> nodes) {
        List<ArbreJsonDto> filteredNodes = new ArrayList<>();
        
        for (ArbreJsonDto node : nodes) {
            ArbreJsonDto filteredNode = new ArbreJsonDto();
            filteredNode.setId(node.getId());
            filteredNode.setText(node.getText());
            filteredNode.setState(node.getState());
            
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                List<ArbreJsonDto> filteredChildren = filterSelectedNodes(node.getChildren());
                filteredNode.setChildren(filteredChildren);
            } else {
                filteredNode.setChildren(new ArrayList<ArbreJsonDto>());
            }

            if ((filteredNode.getState() != null && filteredNode.getState().isSelected()) || 
                (filteredNode.getChildren() != null && !filteredNode.getChildren().isEmpty())) {
                filteredNodes.add(filteredNode);
            }
        }
        
        return filteredNodes;
    }

	private static final Logger LOGGER = LoggerFactory.getLogger(DescarregaEstructuraValidator.class);

}
