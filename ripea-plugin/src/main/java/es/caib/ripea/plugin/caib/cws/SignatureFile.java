
package es.caib.ripea.plugin.caib.cws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SignatureFile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SignatureFile">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="index" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="type" type="{http://www.indra.es/portafirmasws/cws}TypeEnum"/>
 *         &lt;element name="number-signatures" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="reference" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="external-ids" type="{http://www.indra.es/portafirmasws/cws}ExternalIDs" minOccurs="0"/>
 *         &lt;element name="archive-options" type="{http://www.indra.es/portafirmasws/cws}ArchiveOptions" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SignatureFile", propOrder = {
    "index",
    "type",
    "numberSignatures",
    "reference",
    "externalIds",
    "archiveOptions"
})
public class SignatureFile {

    protected int index;
    @XmlElement(required = true)
    protected TypeEnum type;
    @XmlElement(name = "number-signatures")
    protected int numberSignatures;
    protected String reference;
    @XmlElementRef(name = "external-ids", type = JAXBElement.class, required = false)
    protected JAXBElement<ExternalIDs> externalIds;
    @XmlElementRef(name = "archive-options", type = JAXBElement.class, required = false)
    protected JAXBElement<ArchiveOptions> archiveOptions;

    /**
     * Gets the value of the index property.
     * 
     */
    public int getIndex() {
        return index;
    }

    /**
     * Sets the value of the index property.
     * 
     */
    public void setIndex(int value) {
        this.index = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link TypeEnum }
     *     
     */
    public TypeEnum getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeEnum }
     *     
     */
    public void setType(TypeEnum value) {
        this.type = value;
    }

    /**
     * Gets the value of the numberSignatures property.
     * 
     */
    public int getNumberSignatures() {
        return numberSignatures;
    }

    /**
     * Sets the value of the numberSignatures property.
     * 
     */
    public void setNumberSignatures(int value) {
        this.numberSignatures = value;
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

}
