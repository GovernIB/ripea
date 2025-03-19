
package es.caib.ripea.plugin.caib.cws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Field complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Field">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.indra.es/portafirmasws/cws}CriteriaEnum"/>
 *         &lt;element name="condition" type="{http://www.indra.es/portafirmasws/cws}ConditionEnum"/>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Field", propOrder = {
    "name",
    "condition",
    "value"
})
public class Field {

    @XmlElement(required = true)
    protected CriteriaEnum name;
    @XmlElement(required = true)
    protected ConditionEnum condition;
    @XmlElement(required = true)
    protected String value;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link CriteriaEnum }
     *     
     */
    public CriteriaEnum getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link CriteriaEnum }
     *     
     */
    public void setName(CriteriaEnum value) {
        this.name = value;
    }

    /**
     * Gets the value of the condition property.
     * 
     * @return
     *     possible object is
     *     {@link ConditionEnum }
     *     
     */
    public ConditionEnum getCondition() {
        return condition;
    }

    /**
     * Sets the value of the condition property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConditionEnum }
     *     
     */
    public void setCondition(ConditionEnum value) {
        this.condition = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

}
