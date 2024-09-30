
package es.caib.ripea.plugin.caib.cws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PdfAppearance complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PdfAppearance">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="signature-image" type="{http://www.indra.es/portafirmasws/cws}SignatureImage" minOccurs="0"/>
 *         &lt;element name="positions" type="{http://www.indra.es/portafirmasws/cws}Positions" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PdfAppearance", propOrder = {
    "signatureImage",
    "positions"
})
public class PdfAppearance {

    @XmlElementRef(name = "signature-image", type = JAXBElement.class, required = false)
    protected JAXBElement<SignatureImage> signatureImage;
    protected Positions positions;

    /**
     * Gets the value of the signatureImage property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link SignatureImage }{@code >}
     *     
     */
    public JAXBElement<SignatureImage> getSignatureImage() {
        return signatureImage;
    }

    /**
     * Sets the value of the signatureImage property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link SignatureImage }{@code >}
     *     
     */
    public void setSignatureImage(JAXBElement<SignatureImage> value) {
        this.signatureImage = value;
    }

    /**
     * Gets the value of the positions property.
     * 
     * @return
     *     possible object is
     *     {@link Positions }
     *     
     */
    public Positions getPositions() {
        return positions;
    }

    /**
     * Sets the value of the positions property.
     * 
     * @param value
     *     allowed object is
     *     {@link Positions }
     *     
     */
    public void setPositions(Positions value) {
        this.positions = value;
    }

}
