
package es.caib.ripea.plugin.caib.cws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UpdateStep complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UpdateStep">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.indra.es/portafirmasws/cws}Step">
 *       &lt;sequence>
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
@XmlType(name = "UpdateStep", propOrder = {
    "signers"
})
public class UpdateStep
    extends Step
{

    @XmlElement(required = true)
    protected Signers signers;

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
