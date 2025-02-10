package es.caib.ripea.service.intf.dto;

import java.util.ArrayList;
import java.util.List;

public class AmpliarPlazoForm {

	private Long id;
	private List<DocumentEnviamentInteressatDto> documentEnviamentInteressats = new ArrayList<DocumentEnviamentInteressatDto>();

	public Long getId() {
		return id;
	}

	public void setId(
			Long id) {
		this.id = id;
	}

	public List<DocumentEnviamentInteressatDto> getDocumentEnviamentInteressats() {
		return documentEnviamentInteressats;
	}

	public void setDocumentEnviamentInteressats(
			List<DocumentEnviamentInteressatDto> documentEnviamentInteressats) {
		this.documentEnviamentInteressats = documentEnviamentInteressats;
	}
}