package es.caib.ripea.back.command;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class ImportarDocsMassiuCommand implements Serializable {

	private static final long serialVersionUID = 7100396039735416619L;
	
	private boolean allSameProcediment = false;
	private Integer numExps;
    private List<DocumentMassiuItem> documents = new ArrayList<>();
    
    @Data
    public static class DocumentMassiuItem implements Serializable {
        private static final long serialVersionUID = 1L;
        @NotEmpty
        private MultipartFile file;
        @NotEmpty
        private Long tipusDocumentId;
    }
}
