
package es.caib.ripea.plugin.caib.cws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ListResponseDocument complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ListResponseDocument">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="attributes" type="{http://www.indra.es/portafirmasws/cws}DocumentAttributes"/>
 *         &lt;element name="archive-options" type="{http://www.indra.es/portafirmasws/cws}ArchiveOptions" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListResponseDocument", propOrder = {
    "id",
    "attributes",
    "archiveOptions"
})
public class ListResponseDocument {

    protected int id;
    @XmlElement(required = true)
    protected DocumentAttributes attributes;
    @XmlElementRef(name = "archive-options", type = JAXBElement.class, required = false)
    protected JAXBElement<ArchiveOptions> archiveOptions;

    /**
     * Gets the value of the id property.
     * 
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    public void setId(int value) {
        this.id = value;
    }

    /**
     * Gets the value of the attributes property.
     * 
     * @return
     *     possible object is
     *     {@link DocumentAttributes }
     *     
     */
    public DocumentAttributes getAttributes() {
        return attributes;
    }

    /**
     * Sets the value of the attributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentAttributes }
     *     
     */
    public void setAttributes(DocumentAttributes value) {
        this.attributes = value;
    }

    /**
     * Gets the value of the archiveOptions property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArchiveOptions }{@code >}
     *     
     */
    public JAXBElement<ArchiveOptions> getArchiveOptions() {
        return archiveOptions;
    }

    /**
     * Sets the value of the archiveOptions property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArchiveOptions }{@code >}
     *     
     */
    public void setArchiveOptions(JAXBElement<ArchiveOptions> value) {
        this.archiveOptions = value;
    }

}
