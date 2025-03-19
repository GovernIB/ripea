
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
 *         &lt;element name="application" type="{http://www.indra.es/portafirmasws/cws}Application"/>
 *         &lt;element name="search-criterias" type="{http://www.indra.es/portafirmasws/cws}SearchCriterias" minOccurs="0"/>
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
    "application",
    "searchCriterias"
})
@XmlRootElement(name = "search-request")
public class SearchRequest {

    @XmlElement(required = true)
    protected Application application;
    @XmlElementRef(name = "search-criterias", type = JAXBElement.class, required = false)
    protected JAXBElement<SearchCriterias> searchCriterias;
    @XmlAttribute(name = "version")
    protected String version;

    /**
     * Gets the value of the application property.
     * 
     * @return
     *     possible object is
     *     {@link Application }
     *     
     */
    public Application getApplication() {
        return application;
    }

    /**
     * Sets the value of the application property.
     * 
     * @param value
     *     allowed object is
     *     {@link Application }
     *     
     */
    public void setApplication(Application value) {
        this.application = value;
    }

    /**
     * Gets the value of the searchCriterias property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link SearchCriterias }{@code >}
     *     
     */
    public JAXBElement<SearchCriterias> getSearchCriterias() {
        return searchCriterias;
    }

    /**
     * Sets the value of the searchCriterias property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link SearchCriterias }{@code >}
     *     
     */
    public void setSearchCriterias(JAXBElement<SearchCriterias> value) {
        this.searchCriterias = value;
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
