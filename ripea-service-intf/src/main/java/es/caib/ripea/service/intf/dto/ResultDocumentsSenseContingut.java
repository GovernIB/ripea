package es.caib.ripea.service.intf.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ResultDocumentsSenseContingut {
    List<ResultDocumentSenseContingut> resultDocumentsSenseContigut;
    @Builder.Default
    Integer documentsArreglats = 0;
    @Builder.Default
    Integer documentsError = 0;

    public void addResultDocument(ResultDocumentSenseContingut resultDocument) {
        if (resultDocumentsSenseContigut == null) {
            resultDocumentsSenseContigut = new ArrayList<>();
        }
        resultDocumentsSenseContigut.add(resultDocument);
        if (resultDocument.error) {
            documentsError++;
        } else {
            documentsArreglats++;
        }
    }


    @Data
    @Builder
    public static class ResultDocumentSenseContingut {
        String expedient;
        Long documentId;
        String documentNom;
        String uuidOrigen;
        String uuidDestiSenseContingut;
        String uuidDesti;
        String carpeta;
        List<String> documentsCarpeta;

        boolean error;
        String errorMessage;
    }
}
