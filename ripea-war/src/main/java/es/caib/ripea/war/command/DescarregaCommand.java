/**
 * 
 */
package es.caib.ripea.war.command;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.ripea.core.api.dto.ArbreJsonDto;
import es.caib.ripea.core.api.dto.DescarregaDto;
import es.caib.ripea.core.api.dto.ImportacioDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import es.caib.ripea.war.validation.DescarregaEstructura;
import lombok.Getter;
import lombok.Setter;

/**
 * Command per al manteniment d'importació de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@DescarregaEstructura
public class DescarregaCommand {

	protected Long pareId;
	
	private String estructuraCarpetesJson;
	
	public static DescarregaCommand asCommand(ImportacioDto dto) {
		DescarregaCommand command = ConversioTipusHelper.convertir(
				dto,
				DescarregaCommand.class);
		return command;
	}
	
	public static DescarregaDto asDto(DescarregaCommand command) throws ParseException, JsonMappingException {
		DescarregaDto descarregarDto = ConversioTipusHelper.convertir(
				command,
				DescarregaDto.class);
		
		try {
			if (command.getEstructuraCarpetesJson() != null) {
				ObjectMapper objectMapper = new ObjectMapper();
				List<ArbreJsonDto> listCarpetes = objectMapper.readValue(command.getEstructuraCarpetesJson(), new TypeReference<List<ArbreJsonDto>>(){});
				
				List<ArbreJsonDto> listCarpetesSelected = filterSelectedNodes(listCarpetes);
				
				descarregarDto.setEstructuraCarpetes(listCarpetesSelected);
			}
		} catch (IOException ex) {
			throw new JsonMappingException("Hi ha hagut un error en la conversió del json de jstree a List<ArbreJsonDto>", ex);
		}
		
		return descarregarDto;
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
	
}
