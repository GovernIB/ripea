
package es.indra.portafirmasws.cws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SignatureImage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SignatureImage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="base64-data" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="mime-type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SignatureImage", propOrder = {
    "base64Data",
    "mimeType"
})
public class SignatureImage {

    @XmlElement(name = "base64-data", required = true, nillable = true)
    protected String base64Data;
    @XmlElementRef(name = "mime-type", type = JAXBElement.class)
    protected JAXBElement<String> mimeType;

    /**
     * Gets the value of the base64Data property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBase64Data() {
        return base64Data;
    }

    /**
     * Sets the value of the base64Data property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBase64Data(String value) {
        this.base64Data = value;
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
        this.mimeType = ((JAXBElement<String> ) value);
    }

}
