
package es.caib.ripea.plugin.caib.cws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExternalIDs complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExternalIDs">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="logical-id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="verification-code" type="{http://www.indra.es/portafirmasws/cws}VerificationCode" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExternalIDs", propOrder = {
    "logicalId",
    "verificationCode"
})
public class ExternalIDs {

    @XmlElementRef(name = "logical-id", type = JAXBElement.class, required = false)
    protected JAXBElement<String> logicalId;
    @XmlElementRef(name = "verification-code", type = JAXBElement.class, required = false)
    protected JAXBElement<VerificationCode> verificationCode;

    /**
     * Gets the value of the logicalId property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getLogicalId() {
        return logicalId;
    }

    /**
     * Sets the value of the logicalId property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setLogicalId(JAXBElement<String> value) {
        this.logicalId = value;
    }

    /**
     * Gets the value of the verificationCode property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link VerificationCode }{@code >}
     *     
     */
    public JAXBElement<VerificationCode> getVerificationCode() {
        return verificationCode;
    }

    /**
     * Sets the value of the verificationCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link VerificationCode }{@code >}
     *     
     */
    public void setVerificationCode(JAXBElement<VerificationCode> value) {
        this.verificationCode = value;
    }

}
