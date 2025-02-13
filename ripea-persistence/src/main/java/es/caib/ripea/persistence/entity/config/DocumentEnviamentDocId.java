package es.caib.ripea.persistence.entity.config;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;

@Embeddable
public class DocumentEnviamentDocId implements Serializable {

	private static final long serialVersionUID = -3698611925952651505L;
	
	private Long documentEnviamentId;
    private Long documentId;

    public Long getDocumentEnviamentId() {
        return documentEnviamentId;
    }

    public void setDocumentEnviamentId(Long documentEnviamentId) {
        this.documentEnviamentId = documentEnviamentId;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentEnviamentId, documentId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DocumentEnviamentDocId that = (DocumentEnviamentDocId) obj;
        return Objects.equals(documentEnviamentId, that.documentEnviamentId) &&
               Objects.equals(documentId, that.documentId);
    }
}
