
package es.caib.ripea.plugin.caib.cws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DownloadFileRequestDocument complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DownloadFileRequestDocument">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="download-options" type="{http://www.indra.es/portafirmasws/cws}DownloadOptions"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DownloadFileRequestDocument", propOrder = {
    "id",
    "downloadOptions"
})
public class DownloadFileRequestDocument {

    protected int id;
    @XmlElement(name = "download-options", required = true, nillable = true)
    protected DownloadOptions downloadOptions;

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
     * Gets the value of the downloadOptions property.
     * 
     * @return
     *     possible object is
     *     {@link DownloadOptions }
     *     
     */
    public DownloadOptions getDownloadOptions() {
        return downloadOptions;
    }

    /**
     * Sets the value of the downloadOptions property.
     * 
     * @param value
     *     allowed object is
     *     {@link DownloadOptions }
     *     
     */
    public void setDownloadOptions(DownloadOptions value) {
        this.downloadOptions = value;
    }

}
