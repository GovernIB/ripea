
package es.caib.ripea.plugin.caib.cws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ListStep complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ListStep">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.indra.es/portafirmasws/cws}Step">
 *       &lt;sequence>
 *         &lt;element name="minimal-signers" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="signers-action" type="{http://www.indra.es/portafirmasws/cws}Signers" minOccurs="0"/>
 *         &lt;element name="signers-reject" type="{http://www.indra.es/portafirmasws/cws}Signers" minOccurs="0"/>
 *         &lt;element name="signers-none" type="{http://www.indra.es/portafirmasws/cws}Signers" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListStep", propOrder = {
    "minimalSigners",
    "signersAction",
    "signersReject",
    "signersNone"
})
public class ListStep
    extends Step
{

    @XmlElementRef(name = "minimal-signers", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> minimalSigners;
    @XmlElementRef(name = "signers-action", type = JAXBElement.class, required = false)
    protected JAXBElement<Signers> signersAction;
    @XmlElementRef(name = "signers-reject", type = JAXBElement.class, required = false)
    protected JAXBElement<Signers> signersReject;
    @XmlElementRef(name = "signers-none", type = JAXBElement.class, required = false)
    protected JAXBElement<Signers> signersNone;

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
     * Gets the value of the signersAction property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Signers }{@code >}
     *     
     */
    public JAXBElement<Signers> getSignersAction() {
        return signersAction;
    }

    /**
     * Sets the value of the signersAction property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Signers }{@code >}
     *     
     */
    public void setSignersAction(JAXBElement<Signers> value) {
        this.signersAction = value;
    }

    /**
     * Gets the value of the signersReject property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Signers }{@code >}
     *     
     */
    public JAXBElement<Signers> getSignersReject() {
        return signersReject;
    }

    /**
     * Sets the value of the signersReject property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Signers }{@code >}
     *     
     */
    public void setSignersReject(JAXBElement<Signers> value) {
        this.signersReject = value;
    }

    /**
     * Gets the value of the signersNone property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Signers }{@code >}
     *     
     */
    public JAXBElement<Signers> getSignersNone() {
        return signersNone;
    }

    /**
     * Sets the value of the signersNone property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Signers }{@code >}
     *     
     */
    public void setSignersNone(JAXBElement<Signers> value) {
        this.signersNone = value;
    }

}
