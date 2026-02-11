package es.caib.ripea.service.intf.dto;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Component
public class ImportacioRegistreResultDto {

	private List<ImportacioDocumentPreviewDto> documents;
	private List<ImportacioInteressatPreviewDto> interessats;

	public void addDocumentPreview(String nom, String uuid) {
		documents.add(new ImportacioDocumentPreviewDto(nom, uuid));
	}

	public void addInteressatPreview(List<String> documentIdentificacio) {
		interessats.add(new ImportacioInteressatPreviewDto(documentIdentificacio));
	}

	@Getter
	@Setter
	@AllArgsConstructor
	private class ImportacioDocumentPreviewDto {
		private String uuid;
		private String nom;
	}

	@Getter
	@Setter
	@AllArgsConstructor
	private class ImportacioInteressatPreviewDto {
		private List<String> documentIdentificacio;
	}

}
