
package es.caib.ripea.plugin.caib.cws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for DocumentAttributes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DocumentAttributes">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="state" type="{http://www.indra.es/portafirmasws/cws}StateEnum" minOccurs="0"/>
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="subject" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="extension" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sender" type="{http://www.indra.es/portafirmasws/cws}Sender" minOccurs="0"/>
 *         &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="importance" type="{http://www.indra.es/portafirmasws/cws}ImportanceEnum" minOccurs="0"/>
 *         &lt;element name="date-entry" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="date-limit" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="date-last-update" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="date-notice" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="number-annexes" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="sign-annexes" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="external-data" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="type-sign" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="is-file-sign" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="generate-visuals" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="external-ids" type="{http://www.indra.es/portafirmasws/cws}ExternalIDs" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocumentAttributes", propOrder = {
    "state",
    "title",
    "type",
    "subject",
    "description",
    "extension",
    "sender",
    "url",
    "importance",
    "dateEntry",
    "dateLimit",
    "dateLastUpdate",
    "dateNotice",
    "numberAnnexes",
    "signAnnexes",
    "externalData",
    "typeSign",
    "isFileSign",
    "generateVisuals",
    "externalIds"
})
public class DocumentAttributes {

    protected Integer state;
    protected String title;
    protected Integer type;
    protected String subject;
    protected String description;
    protected String extension;
    protected Sender sender;
    protected String url;
    protected ImportanceEnum importance;
    @XmlElementRef(name = "date-entry", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dateEntry;
    @XmlElementRef(name = "date-limit", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dateLimit;
    @XmlElementRef(name = "date-last-update", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dateLastUpdate;
    @XmlElementRef(name = "date-notice", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dateNotice;
    @XmlElementRef(name = "number-annexes", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> numberAnnexes;
    @XmlElementRef(name = "sign-annexes", type = JAXBElement.class, required = false)
    protected JAXBElement<Boolean> signAnnexes;
    @XmlElementRef(name = "external-data", type = JAXBElement.class, required = false)
    protected JAXBElement<String> externalData;
    @XmlElementRef(name = "type-sign", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> typeSign;
    @XmlElementRef(name = "is-file-sign", type = JAXBElement.class, required = false)
    protected JAXBElement<Boolean> isFileSign;
    @XmlElementRef(name = "generate-visuals", type = JAXBElement.class, required = false)
    protected JAXBElement<Boolean> generateVisuals;
    @XmlElementRef(name = "external-ids", type = JAXBElement.class, required = false)
    protected JAXBElement<ExternalIDs> externalIds;

    /**
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setState(Integer value) {
        this.state = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setType(Integer value) {
        this.type = value;
    }

    /**
     * Gets the value of the subject property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the value of the subject property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubject(String value) {
        this.subject = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtension(String value) {
        this.extension = value;
    }

    /**
     * Gets the value of the sender property.
     * 
     * @return
     *     possible object is
     *     {@link Sender }
     *     
     */
    public Sender getSender() {
        return sender;
    }

    /**
     * Sets the value of the sender property.
     * 
     * @param value
     *     allowed object is
     *     {@link Sender }
     *     
     */
    public void setSender(Sender value) {
        this.sender = value;
    }

    /**
     * Gets the value of the url property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrl(String value) {
        this.url = value;
    }

    /**
     * Gets the value of the importance property.
     * 
     * @return
     *     possible object is
     *     {@link ImportanceEnum }
     *     
     */
    public ImportanceEnum getImportance() {
        return importance;
    }

    /**
     * Sets the value of the importance property.
     * 
     * @param value
     *     allowed object is
     *     {@link ImportanceEnum }
     *     
     */
    public void setImportance(ImportanceEnum value) {
        this.importance = value;
    }

    /**
     * Gets the value of the dateEntry property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getDateEntry() {
        return dateEntry;
    }

    /**
     * Sets the value of the dateEntry property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setDateEntry(JAXBElement<XMLGregorianCalendar> value) {
        this.dateEntry = value;
    }

    /**
     * Gets the value of the dateLimit property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getDateLimit() {
        return dateLimit;
    }

    /**
     * Sets the value of the dateLimit property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setDateLimit(JAXBElement<XMLGregorianCalendar> value) {
        this.dateLimit = value;
    }

    /**
     * Gets the value of the dateLastUpdate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getDateLastUpdate() {
        return dateLastUpdate;
    }

    /**
     * Sets the value of the dateLastUpdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setDateLastUpdate(JAXBElement<XMLGregorianCalendar> value) {
        this.dateLastUpdate = value;
    }

    /**
     * Gets the value of the dateNotice property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getDateNotice() {
        return dateNotice;
    }

    /**
     * Sets the value of the dateNotice property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setDateNotice(JAXBElement<XMLGregorianCalendar> value) {
        this.dateNotice = value;
    }

    /**
     * Gets the value of the numberAnnexes property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getNumberAnnexes() {
        return numberAnnexes;
    }

    /**
     * Sets the value of the numberAnnexes property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setNumberAnnexes(JAXBElement<Integer> value) {
        this.numberAnnexes = value;
    }

    /**
     * Gets the value of the signAnnexes property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getSignAnnexes() {
        return signAnnexes;
    }

    /**
     * Sets the value of the signAnnexes property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setSignAnnexes(JAXBElement<Boolean> value) {
        this.signAnnexes = value;
    }

    /**
     * Gets the value of the externalData property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getExternalData() {
        return externalData;
    }

    /**
     * Sets the value of the externalData property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setExternalData(JAXBElement<String> value) {
        this.externalData = value;
    }

    /**
     * Gets the value of the typeSign property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getTypeSign() {
        return typeSign;
    }

    /**
     * Sets the value of the typeSign property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setTypeSign(JAXBElement<Integer> value) {
        this.typeSign = value;
    }

    /**
     * Gets the value of the isFileSign property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getIsFileSign() {
        return isFileSign;
    }

    /**
     * Sets the value of the isFileSign property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setIsFileSign(JAXBElement<Boolean> value) {
        this.isFileSign = value;
    }

    /**
     * Gets the value of the generateVisuals property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getGenerateVisuals() {
        return generateVisuals;
    }

    /**
     * Sets the value of the generateVisuals property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setGenerateVisuals(JAXBElement<Boolean> value) {
        this.generateVisuals = value;
    }

    /**
     * Gets the value of the externalIds property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ExternalIDs }{@code >}
     *     
     */
    public JAXBElement<ExternalIDs> getExternalIds() {
        return externalIds;
    }

    /**
     * Sets the value of the externalIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ExternalIDs }{@code >}
     *     
     */
    public void setExternalIds(JAXBElement<ExternalIDs> value) {
        this.externalIds = value;
    }

}
