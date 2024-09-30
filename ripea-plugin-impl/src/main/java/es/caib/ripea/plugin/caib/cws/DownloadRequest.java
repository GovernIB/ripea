
package es.caib.ripea.plugin.caib.cws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="application" type="{http://www.indra.es/portafirmasws/cws}Application"/>
 *         &lt;element name="download-documents" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="additional-info" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="archive-info" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="document" type="{http://www.indra.es/portafirmasws/cws}DownloadRequestDocument"/>
 *       &lt;/sequence>
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "application",
    "downloadDocuments",
    "additionalInfo",
    "archiveInfo",
    "document"
})
@XmlRootElement(name = "download-request")
public class DownloadRequest {

    @XmlElement(required = true)
    protected Application application;
    @XmlElementRef(name = "download-documents", type = JAXBElement.class, required = false)
    protected JAXBElement<Boolean> downloadDocuments;
    @XmlElementRef(name = "additional-info", type = JAXBElement.class, required = false)
    protected JAXBElement<Boolean> additionalInfo;
    @XmlElementRef(name = "archive-info", type = JAXBElement.class, required = false)
    protected JAXBElement<Boolean> archiveInfo;
    @XmlElement(required = true)
    protected DownloadRequestDocument document;
    @XmlAttribute(name = "version")
    protected String version;

    /**
     * Gets the value of the application property.
     * 
     * @return
     *     possible object is
     *     {@link Application }
     *     
     */
    public Application getApplication() {
        return application;
    }

    /**
     * Sets the value of the application property.
     * 
     * @param value
     *     allowed object is
     *     {@link Application }
     *     
     */
    public void setApplication(Application value) {
        this.application = value;
    }

    /**
     * Gets the value of the downloadDocuments property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getDownloadDocuments() {
        return downloadDocuments;
    }

    /**
     * Sets the value of the downloadDocuments property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setDownloadDocuments(JAXBElement<Boolean> value) {
        this.downloadDocuments = value;
    }

    /**
     * Gets the value of the additionalInfo property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getAdditionalInfo() {
        return additionalInfo;
    }

    /**
     * Sets the value of the additionalInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setAdditionalInfo(JAXBElement<Boolean> value) {
        this.additionalInfo = value;
    }

    /**
     * Gets the value of the archiveInfo property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getArchiveInfo() {
        return archiveInfo;
    }

    /**
     * Sets the value of the archiveInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setArchiveInfo(JAXBElement<Boolean> value) {
        this.archiveInfo = value;
    }

    /**
     * Gets the value of the document property.
     * 
     * @return
     *     possible object is
     *     {@link DownloadRequestDocument }
     *     
     */
    public DownloadRequestDocument getDocument() {
        return document;
    }

    /**
     * Sets the value of the document property.
     * 
     * @param value
     *     allowed object is
     *     {@link DownloadRequestDocument }
     *     
     */
    public void setDocument(DownloadRequestDocument value) {
        this.document = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

}
