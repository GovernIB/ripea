
package es.caib.ripea.plugin.caib.cws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PendingDocuments complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PendingDocuments">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="owned" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="delegated" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PendingDocuments", propOrder = {
    "owned",
    "delegated"
})
public class PendingDocuments {

    @XmlElement(required = true)
    protected String owned;
    @XmlElement(required = true)
    protected String delegated;

    /**
     * Gets the value of the owned property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOwned() {
        return owned;
    }

    /**
     * Sets the value of the owned property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOwned(String value) {
        this.owned = value;
    }

    /**
     * Gets the value of the delegated property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDelegated() {
        return delegated;
    }

    /**
     * Sets the value of the delegated property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDelegated(String value) {
        this.delegated = value;
    }

}
