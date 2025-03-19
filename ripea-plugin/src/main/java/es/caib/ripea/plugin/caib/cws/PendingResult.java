
package es.caib.ripea.plugin.caib.cws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PendingResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PendingResult">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.indra.es/portafirmasws/cws}SearchResult">
 *       &lt;sequence>
 *         &lt;element name="pending-documents" type="{http://www.indra.es/portafirmasws/cws}PendingDocuments"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PendingResult", propOrder = {
    "pendingDocuments"
})
public class PendingResult
    extends SearchResult
{

    @XmlElement(name = "pending-documents", required = true, nillable = true)
    protected PendingDocuments pendingDocuments;

    /**
     * Gets the value of the pendingDocuments property.
     * 
     * @return
     *     possible object is
     *     {@link PendingDocuments }
     *     
     */
    public PendingDocuments getPendingDocuments() {
        return pendingDocuments;
    }

    /**
     * Sets the value of the pendingDocuments property.
     * 
     * @param value
     *     allowed object is
     *     {@link PendingDocuments }
     *     
     */
    public void setPendingDocuments(PendingDocuments value) {
        this.pendingDocuments = value;
    }

}
