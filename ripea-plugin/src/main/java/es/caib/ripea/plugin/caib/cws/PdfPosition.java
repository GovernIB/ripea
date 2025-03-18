
package es.caib.ripea.plugin.caib.cws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PdfPosition complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PdfPosition">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="format" type="{http://www.indra.es/portafirmasws/cws}Format" minOccurs="0"/>
 *         &lt;element name="sheet" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="x_0" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="y_0" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="x_1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="y_1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PdfPosition", propOrder = {
    "format",
    "sheet",
    "x0",
    "y0",
    "x1",
    "y1"
})
public class PdfPosition {

    protected Format format;
    protected String sheet;
    @XmlElement(name = "x_0")
    protected String x0;
    @XmlElement(name = "y_0")
    protected String y0;
    @XmlElement(name = "x_1")
    protected String x1;
    @XmlElement(name = "y_1")
    protected String y1;

    /**
     * Gets the value of the format property.
     * 
     * @return
     *     possible object is
     *     {@link Format }
     *     
     */
    public Format getFormat() {
        return format;
    }

    /**
     * Sets the value of the format property.
     * 
     * @param value
     *     allowed object is
     *     {@link Format }
     *     
     */
    public void setFormat(Format value) {
        this.format = value;
    }

    /**
     * Gets the value of the sheet property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSheet() {
        return sheet;
    }

    /**
     * Sets the value of the sheet property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSheet(String value) {
        this.sheet = value;
    }

    /**
     * Gets the value of the x0 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getX0() {
        return x0;
    }

    /**
     * Sets the value of the x0 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setX0(String value) {
        this.x0 = value;
    }

    /**
     * Gets the value of the y0 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getY0() {
        return y0;
    }

    /**
     * Sets the value of the y0 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setY0(String value) {
        this.y0 = value;
    }

    /**
     * Gets the value of the x1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getX1() {
        return x1;
    }

    /**
     * Sets the value of the x1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setX1(String value) {
        this.x1 = value;
    }

    /**
     * Gets the value of the y1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getY1() {
        return y1;
    }

    /**
     * Sets the value of the y1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setY1(String value) {
        this.y1 = value;
    }

}
