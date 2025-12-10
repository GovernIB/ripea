package es.caib.ripea.plugin.helper;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;

public class EvidenciaIdExtractor {

    private static final String CAMP_EVIDENCIA_ID = "EvidenciesIB.EvidenciaID";

    public static Long extractEvidenciaId(byte[] pdfBytes) throws IOException {
        try (PDDocument doc = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
            PDDocumentInformation info = doc.getDocumentInformation();
            if (info != null) {
                for (String key : info.getMetadataKeys()) {
                	if (CAMP_EVIDENCIA_ID.equals(key)) {
                		return Long.valueOf(info.getCustomMetadataValue(key));
                	}
                }
            }
        } catch (Exception e) {
			e.printStackTrace();
			return null;
		}

        return null;
    }
}
