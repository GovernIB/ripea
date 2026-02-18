package es.caib.ripea.service.intf.dto;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import es.caib.plugins.arxiu.api.Document;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Component
public class ImportacioRegistreWarapperDto {

	private final Map<String, Document> documentsWrapper = new ConcurrentHashMap<>();	

}
