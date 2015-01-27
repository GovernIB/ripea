
package es.indra.portafirmasws.cws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Position complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Position">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="source" type="{http://www.indra.es/portafirmasws/cws}TypeEnum"/>
 *         &lt;element name="reference" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="type" type="{http://www.indra.es/portafirmasws/cws}PdfTypeEnum"/>
 *         &lt;element name="pdf-position" type="{http://www.indra.es/portafirmasws/cws}PdfPosition" minOccurs="0"/>
 *         &lt;element name="signature-image-dimensions" type="{http://www.indra.es/portafirmasws/cws}SignatureImageDimensions" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Position", propOrder = {
    "source",
    "reference",
    "type",
    "pdfPosition",
    "signatureImageDimensions"
})
public class Position {

    @XmlElement(required = true)
    protected TypeEnum source;
    protected String reference;
    @XmlElement(required = true)
    protected PdfTypeEnum type;
    @XmlElementRef(name = "pdf-position", type = JAXBElement.class)
    protected JAXBElement<PdfPosition> pdfPosition;
    @XmlElementRef(name = "signature-image-dimensions", type = JAXBElement.class)
    protected JAXBElement<SignatureImageDimensions> signatureImageDimensions;

    /**
     * Gets the value of the source property.
     * 
     * @return
     *     possible object is
     *     {@link TypeEnum }
     *     
     */
    public TypeEnum getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeEnum }
     *     
     */
    public void setSource(TypeEnum value) {
        this.source = value;
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
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link PdfTypeEnum }
     *     
     */
    public PdfTypeEnum getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link PdfTypeEnum }
     *     
     */
    public void setType(PdfTypeEnum value) {
        this.type = value;
    }

    /**
     * Gets the value of the pdfPosition property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link PdfPosition }{@code >}
     *     
     */
    public JAXBElement<PdfPosition> getPdfPosition() {
        return pdfPosition;
    }

    /**
     * Sets the value of the pdfPosition property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link PdfPosition }{@code >}
     *     
     */
    public void setPdfPosition(JAXBElement<PdfPosition> value) {
        this.pdfPosition = ((JAXBElement<PdfPosition> ) value);
    }

    /**
     * Gets the value of the signatureImageDimensions property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link SignatureImageDimensions }{@code >}
     *     
     */
    public JAXBElement<SignatureImageDimensions> getSignatureImageDimensions() {
        return signatureImageDimensions;
    }

    /**
     * Sets the value of the signatureImageDimensions property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link SignatureImageDimensions }{@code >}
     *     
     */
    public void setSignatureImageDimensions(JAXBElement<SignatureImageDimensions> value) {
        this.signatureImageDimensions = ((JAXBElement<SignatureImageDimensions> ) value);
    }

}
