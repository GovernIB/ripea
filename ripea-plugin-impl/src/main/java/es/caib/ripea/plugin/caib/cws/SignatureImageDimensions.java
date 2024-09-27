
package es.caib.ripea.plugin.caib.cws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SignatureImageDimensions complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SignatureImageDimensions">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="width" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="height" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="x_padding" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="y_padding" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SignatureImageDimensions", propOrder = {
    "width",
    "height",
    "xPadding",
    "yPadding"
})
public class SignatureImageDimensions {

    @XmlElement(required = true)
    protected String width;
    @XmlElement(required = true)
    protected String height;
    @XmlElement(name = "x_padding", required = true)
    protected String xPadding;
    @XmlElement(name = "y_padding", required = true)
    protected String yPadding;

    /**
     * Gets the value of the width property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWidth() {
        return width;
    }

    /**
     * Sets the value of the width property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWidth(String value) {
        this.width = value;
    }

    /**
     * Gets the value of the height property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHeight() {
        return height;
    }

    /**
     * Sets the value of the height property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHeight(String value) {
        this.height = value;
    }

    /**
     * Gets the value of the xPadding property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXPadding() {
        return xPadding;
    }

    /**
     * Sets the value of the xPadding property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXPadding(String value) {
        this.xPadding = value;
    }

    /**
     * Gets the value of the yPadding property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getYPadding() {
        return yPadding;
    }

    /**
     * Sets the value of the yPadding property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setYPadding(String value) {
        this.yPadding = value;
    }

}
