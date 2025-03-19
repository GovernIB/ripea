
package es.caib.ripea.plugin.caib.cws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DownloadOptions complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DownloadOptions">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="download-type" type="{http://www.indra.es/portafirmasws/cws}ModeTypeEnum" minOccurs="0"/>
 *         &lt;element name="files" type="{http://www.indra.es/portafirmasws/cws}Files" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DownloadOptions", propOrder = {
    "downloadType",
    "files"
})
public class DownloadOptions {

    @XmlElementRef(name = "download-type", type = JAXBElement.class, required = false)
    protected JAXBElement<ModeTypeEnum> downloadType;
    protected Files files;

    /**
     * Gets the value of the downloadType property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ModeTypeEnum }{@code >}
     *     
     */
    public JAXBElement<ModeTypeEnum> getDownloadType() {
        return downloadType;
    }

    /**
     * Sets the value of the downloadType property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ModeTypeEnum }{@code >}
     *     
     */
    public void setDownloadType(JAXBElement<ModeTypeEnum> value) {
        this.downloadType = value;
    }

    /**
     * Gets the value of the files property.
     * 
     * @return
     *     possible object is
     *     {@link Files }
     *     
     */
    public Files getFiles() {
        return files;
    }

    /**
     * Sets the value of the files property.
     * 
     * @param value
     *     allowed object is
     *     {@link Files }
     *     
     */
    public void setFiles(Files value) {
        this.files = value;
    }

}
