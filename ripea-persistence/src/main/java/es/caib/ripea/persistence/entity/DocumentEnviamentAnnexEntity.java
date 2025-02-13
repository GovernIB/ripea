package es.caib.ripea.persistence.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import es.caib.ripea.persistence.entity.config.DocumentEnviamentDocId;
import es.caib.ripea.service.intf.config.BaseConfig;

@Entity
@Table( name = BaseConfig.DB_PREFIX +"DOCUMENT_ENVIAMENT_DOC")
public class DocumentEnviamentAnnexEntity {

	@EmbeddedId
    private DocumentEnviamentDocId id;
	
    @ManyToOne
    @MapsId("documentEnviamentId")
    @JoinColumn(name = "DOCUMENT_ENVIAMENT_ID")
    private DocumentEnviamentEntity documentEnviament;

    @ManyToOne
    @MapsId("documentId")
    @JoinColumn(name = "DOCUMENT_ID")
    private DocumentEntity document;

	public DocumentEnviamentDocId getId() {
		return id;
	}

	public void setId(
			DocumentEnviamentDocId id) {
		this.id = id;
	}

	public DocumentEnviamentEntity getDocumentEnviament() {
		return documentEnviament;
	}

	public void setDocumentEnviament(
			DocumentEnviamentEntity documentEnviament) {
		this.documentEnviament = documentEnviament;
	}

	public DocumentEntity getDocument() {
		return document;
	}

	public void setDocument(
			DocumentEntity document) {
		this.document = document;
	}
    
    public void nouAnnex(DocumentEntity document, DocumentEnviamentEntity documentEnviament) {
		this.setDocumentEnviament(documentEnviament);
		this.setDocument(document);
		DocumentEnviamentDocId id = new DocumentEnviamentDocId();
		id.setDocumentEnviamentId(documentEnviament.getId());
		id.setDocumentId(document.getId());
		this.setId(id);
    }
}