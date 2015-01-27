
package es.indra.portafirmasws.cws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArchiveLocator complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArchiveLocator">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="repository-id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="archive-uri" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="archive-version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="repository-base" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="folder-name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="file-path-name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="signature-files-path-pattern" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="visual-file-path-name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="retention-policy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="signature-custody" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArchiveLocator", propOrder = {
    "repositoryId",
    "archiveUri",
    "archiveVersion",
    "repositoryBase",
    "folderName",
    "filePathName",
    "signatureFilesPathPattern",
    "visualFilePathName",
    "retentionPolicy",
    "signatureCustody"
})
public class ArchiveLocator {

    @XmlElement(name = "repository-id", required = true, nillable = true)
    protected String repositoryId;
    @XmlElementRef(name = "archive-uri", type = JAXBElement.class)
    protected JAXBElement<String> archiveUri;
    @XmlElementRef(name = "archive-version", type = JAXBElement.class)
    protected JAXBElement<String> archiveVersion;
    @XmlElementRef(name = "repository-base", type = JAXBElement.class)
    protected JAXBElement<String> repositoryBase;
    @XmlElementRef(name = "folder-name", type = JAXBElement.class)
    protected JAXBElement<String> folderName;
    @XmlElementRef(name = "file-path-name", type = JAXBElement.class)
    protected JAXBElement<String> filePathName;
    @XmlElementRef(name = "signature-files-path-pattern", type = JAXBElement.class)
    protected JAXBElement<String> signatureFilesPathPattern;
    @XmlElementRef(name = "visual-file-path-name", type = JAXBElement.class)
    protected JAXBElement<String> visualFilePathName;
    @XmlElementRef(name = "retention-policy", type = JAXBElement.class)
    protected JAXBElement<String> retentionPolicy;
    @XmlElementRef(name = "signature-custody", type = JAXBElement.class)
    protected JAXBElement<Boolean> signatureCustody;

    /**
     * Gets the value of the repositoryId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRepositoryId() {
        return repositoryId;
    }

    /**
     * Sets the value of the repositoryId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRepositoryId(String value) {
        this.repositoryId = value;
    }

    /**
     * Gets the value of the archiveUri property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getArchiveUri() {
        return archiveUri;
    }

    /**
     * Sets the value of the archiveUri property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setArchiveUri(JAXBElement<String> value) {
        this.archiveUri = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the archiveVersion property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getArchiveVersion() {
        return archiveVersion;
    }

    /**
     * Sets the value of the archiveVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setArchiveVersion(JAXBElement<String> value) {
        this.archiveVersion = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the repositoryBase property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRepositoryBase() {
        return repositoryBase;
    }

    /**
     * Sets the value of the repositoryBase property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRepositoryBase(JAXBElement<String> value) {
        this.repositoryBase = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the folderName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getFolderName() {
        return folderName;
    }

    /**
     * Sets the value of the folderName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setFolderName(JAXBElement<String> value) {
        this.folderName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the filePathName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getFilePathName() {
        return filePathName;
    }

    /**
     * Sets the value of the filePathName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setFilePathName(JAXBElement<String> value) {
        this.filePathName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the signatureFilesPathPattern property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getSignatureFilesPathPattern() {
        return signatureFilesPathPattern;
    }

    /**
     * Sets the value of the signatureFilesPathPattern property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setSignatureFilesPathPattern(JAXBElement<String> value) {
        this.signatureFilesPathPattern = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the visualFilePathName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getVisualFilePathName() {
        return visualFilePathName;
    }

    /**
     * Sets the value of the visualFilePathName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setVisualFilePathName(JAXBElement<String> value) {
        this.visualFilePathName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the retentionPolicy property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRetentionPolicy() {
        return retentionPolicy;
    }

    /**
     * Sets the value of the retentionPolicy property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRetentionPolicy(JAXBElement<String> value) {
        this.retentionPolicy = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the signatureCustody property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getSignatureCustody() {
        return signatureCustody;
    }

    /**
     * Sets the value of the signatureCustody property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setSignatureCustody(JAXBElement<Boolean> value) {
        this.signatureCustody = ((JAXBElement<Boolean> ) value);
    }

}
