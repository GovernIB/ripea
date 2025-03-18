
package es.caib.ripea.plugin.caib.cws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UploadStep complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UploadStep">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.indra.es/portafirmasws/cws}Step">
 *       &lt;sequence>
 *         &lt;element name="minimal-signers" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="signers" type="{http://www.indra.es/portafirmasws/cws}Signers"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UploadStep", propOrder = {
    "minimalSigners",
    "signers"
})
public class UploadStep
    extends Step
{

    @XmlElementRef(name = "minimal-signers", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> minimalSigners;
    @XmlElement(required = true)
    protected Signers signers;

    /**
     * Gets the value of the minimalSigners property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getMinimalSigners() {
        return minimalSigners;
    }

    /**
     * Sets the value of the minimalSigners property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setMinimalSigners(JAXBElement<Integer> value) {
        this.minimalSigners = value;
    }

    /**
     * Gets the value of the signers property.
     * 
     * @return
     *     possible object is
     *     {@link Signers }
     *     
     */
    public Signers getSigners() {
        return signers;
    }

    /**
     * Sets the value of the signers property.
     * 
     * @param value
     *     allowed object is
     *     {@link Signers }
     *     
     */
    public void setSigners(Signers value) {
        this.signers = value;
    }

}
