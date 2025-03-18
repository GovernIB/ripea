
package es.caib.ripea.plugin.caib.cws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="result" type="{http://www.indra.es/portafirmasws/cws}Result"/>
 *         &lt;element name="server-signers" type="{http://www.indra.es/portafirmasws/cws}ServerSigners" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "result",
    "serverSigners"
})
@XmlRootElement(name = "listServerSigners-response")
public class ListServerSignersResponse {

    @XmlElement(required = true)
    protected Result result;
    @XmlElementRef(name = "server-signers", type = JAXBElement.class, required = false)
    protected JAXBElement<ServerSigners> serverSigners;
    @XmlAttribute(name = "version")
    protected String version;

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link Result }
     *     
     */
    public Result getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link Result }
     *     
     */
    public void setResult(Result value) {
        this.result = value;
    }

    /**
     * Gets the value of the serverSigners property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ServerSigners }{@code >}
     *     
     */
    public JAXBElement<ServerSigners> getServerSigners() {
        return serverSigners;
    }

    /**
     * Sets the value of the serverSigners property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ServerSigners }{@code >}
     *     
     */
    public void setServerSigners(JAXBElement<ServerSigners> value) {
        this.serverSigners = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

}
