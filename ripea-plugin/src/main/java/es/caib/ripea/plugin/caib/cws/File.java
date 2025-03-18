
package es.caib.ripea.plugin.caib.cws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for File complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="File">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="index" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="type" type="{http://www.indra.es/portafirmasws/cws}TypeEnum"/>
 *         &lt;element name="reference" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="profile" type="{http://www.indra.es/portafirmasws/cws}ProfileEnum"/>
 *         &lt;element name="file-format" type="{http://www.indra.es/portafirmasws/cws}CodificationTypeEnum"/>
 *         &lt;element name="extension" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mime-type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base64-data" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="number-signatures" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="signers-id" type="{http://www.indra.es/portafirmasws/cws}SignersID" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "File", propOrder = {
    "index",
    "type",
    "reference",
    "profile",
    "fileFormat",
    "extension",
    "mimeType",
    "base64Data",
    "numberSignatures",
    "signersId"
})
public class File {

    protected int index;
    @XmlElement(required = true)
    protected TypeEnum type;
    protected String reference;
    @XmlElement(required = true)
    protected ProfileEnum profile;
    @XmlElement(name = "file-format", required = true, nillable = true)
    protected CodificationTypeEnum fileFormat;
    protected String extension;
    @XmlElementRef(name = "mime-type", type = JAXBElement.class, required = false)
    protected JAXBElement<String> mimeType;
    @XmlElementRef(name = "base64-data", type = JAXBElement.class, required = false)
    protected JAXBElement<String> base64Data;
    @XmlElementRef(name = "number-signatures", type = JAXBElement.class, required = false)
    protected JAXBElement<String> numberSignatures;
    @XmlElementRef(name = "signers-id", type = JAXBElement.class, required = false)
    protected JAXBElement<SignersID> signersId;

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
     * Gets the value of the profile property.
     * 
     * @return
     *     possible object is
     *     {@link ProfileEnum }
     *     
     */
    public ProfileEnum getProfile() {
        return profile;
    }

    /**
     * Sets the value of the profile property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProfileEnum }
     *     
     */
    public void setProfile(ProfileEnum value) {
        this.profile = value;
    }

    /**
     * Gets the value of the fileFormat property.
     * 
     * @return
     *     possible object is
     *     {@link CodificationTypeEnum }
     *     
     */
    public CodificationTypeEnum getFileFormat() {
        return fileFormat;
    }

    /**
     * Sets the value of the fileFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodificationTypeEnum }
     *     
     */
    public void setFileFormat(CodificationTypeEnum value) {
        this.fileFormat = value;
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
     * Gets the value of the mimeType property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMimeType() {
        return mimeType;
    }

    /**
     * Sets the value of the mimeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMimeType(JAXBElement<String> value) {
        this.mimeType = value;
    }

    /**
     * Gets the value of the base64Data property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getBase64Data() {
        return base64Data;
    }

    /**
     * Sets the value of the base64Data property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setBase64Data(JAXBElement<String> value) {
        this.base64Data = value;
    }

    /**
     * Gets the value of the numberSignatures property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getNumberSignatures() {
        return numberSignatures;
    }

    /**
     * Sets the value of the numberSignatures property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setNumberSignatures(JAXBElement<String> value) {
        this.numberSignatures = value;
    }

    /**
     * Gets the value of the signersId property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link SignersID }{@code >}
     *     
     */
    public JAXBElement<SignersID> getSignersId() {
        return signersId;
    }

    /**
     * Sets the value of the signersId property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link SignersID }{@code >}
     *     
     */
    public void setSignersId(JAXBElement<SignersID> value) {
        this.signersId = value;
    }

}
