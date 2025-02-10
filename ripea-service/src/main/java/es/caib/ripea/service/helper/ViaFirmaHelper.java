package es.caib.ripea.service.helper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viafirma.documents.sdk.java.model.Message;
import es.caib.ripea.service.intf.dto.ViaFirmaCallbackEstatEnumDto;
import es.caib.ripea.service.intf.dto.ViaFirmaRespostaDto;
import org.springframework.stereotype.Component;

/**
 * Helper per interpretar la resposta de viaFirma
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ViaFirmaHelper {

	public ViaFirmaRespostaDto processarRespostaViaFirma(String messageJson) {
		ViaFirmaRespostaDto viaFirmaResponseDto = new ViaFirmaRespostaDto();
		Message message = null;
		String messageCode;
		String estat;
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			message = mapper.readValue(messageJson, Message.class);
			if (message != null) {
				messageCode = message.getCode();
				estat = message.getWorkflow().getCurrent();
				viaFirmaResponseDto.setMessageCode(messageCode);
				viaFirmaResponseDto.setStatus(ViaFirmaCallbackEstatEnumDto.valueOf(estat));
			}
		} catch (Exception ex) {
			throw new RuntimeException(
					"Error a l'hora de fer la conversió del messageJson a Message",
					ex);
		}
		return viaFirmaResponseDto;
	}

}
