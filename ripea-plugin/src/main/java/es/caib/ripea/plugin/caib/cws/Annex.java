
package es.caib.ripea.plugin.caib.cws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Annex complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Annex">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="extension" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reference" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sign-annex" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="sender" type="{http://www.indra.es/portafirmasws/cws}Sender" minOccurs="0"/>
 *         &lt;element name="external-ids" type="{http://www.indra.es/portafirmasws/cws}ExternalIDs" minOccurs="0"/>
 *         &lt;element name="archive-options" type="{http://www.indra.es/portafirmasws/cws}ArchiveOptions" minOccurs="0"/>
 *         &lt;element name="type-sign" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="is-file-sign" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Annex", propOrder = {
    "description",
    "extension",
    "reference",
    "url",
    "signAnnex",
    "sender",
    "externalIds",
    "archiveOptions",
    "typeSign",
    "isFileSign"
})
public class Annex {

    protected String description;
    protected String extension;
    protected String reference;
    protected String url;
    @XmlElementRef(name = "sign-annex", type = JAXBElement.class, required = false)
    protected JAXBElement<Boolean> signAnnex;
    protected Sender sender;
    @XmlElementRef(name = "external-ids", type = JAXBElement.class, required = false)
    protected JAXBElement<ExternalIDs> externalIds;
    @XmlElementRef(name = "archive-options", type = JAXBElement.class, required = false)
    protected JAXBElement<ArchiveOptions> archiveOptions;
    @XmlElementRef(name = "type-sign", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> typeSign;
    @XmlElementRef(name = "is-file-sign", type = JAXBElement.class, required = false)
    protected JAXBElement<Boolean> isFileSign;

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtension(String value) {
        this.extension = value;
    }

    /**
     * Gets the value of the reference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReference() {
        return reference;
    }

    /**
     * Sets the value of the reference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReference(String value) {
        this.reference = value;
    }

    /**
     * Gets the value of the url property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrl(String value) {
        this.url = value;
    }

    /**
     * Gets the value of the signAnnex property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getSignAnnex() {
        return signAnnex;
    }

    /**
     * Sets the value of the signAnnex property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setSignAnnex(JAXBElement<Boolean> value) {
        this.signAnnex = value;
    }

    /**
     * Gets the value of the sender property.
     * 
     * @return
     *     possible object is
     *     {@link Sender }
     *     
     */
    public Sender getSender() {
        return sender;
    }

    /**
     * Sets the value of the sender property.
     * 
     * @param value
     *     allowed object is
     *     {@link Sender }
     *     
     */
    public void setSender(Sender value) {
        this.sender = value;
    }

    /**
     * Gets the value of the externalIds property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ExternalIDs }{@code >}
     *     
     */
    public JAXBElement<ExternalIDs> getExternalIds() {
        return externalIds;
    }

    /**
     * Sets the value of the externalIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ExternalIDs }{@code >}
     *     
     */
    public void setExternalIds(JAXBElement<ExternalIDs> value) {
        this.externalIds = value;
    }

    /**
     * Gets the value of the archiveOptions property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArchiveOptions }{@code >}
     *     
     */
    public JAXBElement<ArchiveOptions> getArchiveOptions() {
        return archiveOptions;
    }

    /**
     * Sets the value of the archiveOptions property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArchiveOptions }{@code >}
     *     
     */
    public void setArchiveOptions(JAXBElement<ArchiveOptions> value) {
        this.archiveOptions = value;
    }

    /**
     * Gets the value of the typeSign property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getTypeSign() {
        return typeSign;
    }

    /**
     * Sets the value of the typeSign property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setTypeSign(JAXBElement<Integer> value) {
        this.typeSign = value;
    }

    /**
     * Gets the value of the isFileSign property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getIsFileSign() {
        return isFileSign;
    }

    /**
     * Sets the value of the isFileSign property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setIsFileSign(JAXBElement<Boolean> value) {
        this.isFileSign = value;
    }

}
