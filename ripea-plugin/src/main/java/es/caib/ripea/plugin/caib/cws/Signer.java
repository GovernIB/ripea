
package es.caib.ripea.plugin.caib.cws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for Signer complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Signer">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="check-cert" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="date" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="delegates" type="{http://www.indra.es/portafirmasws/cws}Delegates" minOccurs="0"/>
 *         &lt;element name="substitutes" type="{http://www.indra.es/portafirmasws/cws}Substitutes" minOccurs="0"/>
 *         &lt;element name="job" type="{http://www.indra.es/portafirmasws/cws}Job" minOccurs="0"/>
 *         &lt;element name="certificate" type="{http://www.indra.es/portafirmasws/cws}Certificate" minOccurs="0"/>
 *         &lt;element name="rejection" type="{http://www.indra.es/portafirmasws/cws}Rejection" minOccurs="0"/>
 *         &lt;element name="signature-files" type="{http://www.indra.es/portafirmasws/cws}SignerSignatureFiles" minOccurs="0"/>
 *         &lt;element name="id-update" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pdf-appearance" type="{http://www.indra.es/portafirmasws/cws}PdfAppearance" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Signer", propOrder = {
    "id",
    "name",
    "email",
    "checkCert",
    "date",
    "delegates",
    "substitutes",
    "job",
    "certificate",
    "rejection",
    "signatureFiles",
    "idUpdate",
    "pdfAppearance"
})
public class Signer {

    @XmlElement(required = true)
    protected String id;
    protected String name;
    protected String email;
    @XmlElementRef(name = "check-cert", type = JAXBElement.class, required = false)
    protected JAXBElement<Boolean> checkCert;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar date;
    protected Delegates delegates;
    protected Substitutes substitutes;
    protected Job job;
    protected Certificate certificate;
    protected Rejection rejection;
    @XmlElementRef(name = "signature-files", type = JAXBElement.class, required = false)
    protected JAXBElement<SignerSignatureFiles> signatureFiles;
    @XmlElementRef(name = "id-update", type = JAXBElement.class, required = false)
    protected JAXBElement<String> idUpdate;
    @XmlElementRef(name = "pdf-appearance", type = JAXBElement.class, required = false)
    protected JAXBElement<PdfAppearance> pdfAppearance;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Gets the value of the checkCert property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getCheckCert() {
        return checkCert;
    }

    /**
     * Sets the value of the checkCert property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setCheckCert(JAXBElement<Boolean> value) {
        this.checkCert = value;
    }

    /**
     * Gets the value of the date property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDate() {
        return date;
    }

    /**
     * Sets the value of the date property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDate(XMLGregorianCalendar value) {
        this.date = value;
    }

    /**
     * Gets the value of the delegates property.
     * 
     * @return
     *     possible object is
     *     {@link Delegates }
     *     
     */
    public Delegates getDelegates() {
        return delegates;
    }

    /**
     * Sets the value of the delegates property.
     * 
     * @param value
     *     allowed object is
     *     {@link Delegates }
     *     
     */
    public void setDelegates(Delegates value) {
        this.delegates = value;
    }

    /**
     * Gets the value of the substitutes property.
     * 
     * @return
     *     possible object is
     *     {@link Substitutes }
     *     
     */
    public Substitutes getSubstitutes() {
        return substitutes;
    }

    /**
     * Sets the value of the substitutes property.
     * 
     * @param value
     *     allowed object is
     *     {@link Substitutes }
     *     
     */
    public void setSubstitutes(Substitutes value) {
        this.substitutes = value;
    }

    /**
     * Gets the value of the job property.
     * 
     * @return
     *     possible object is
     *     {@link Job }
     *     
     */
    public Job getJob() {
        return job;
    }

    /**
     * Sets the value of the job property.
     * 
     * @param value
     *     allowed object is
     *     {@link Job }
     *     
     */
    public void setJob(Job value) {
        this.job = value;
    }

    /**
     * Gets the value of the certificate property.
     * 
     * @return
     *     possible object is
     *     {@link Certificate }
     *     
     */
    public Certificate getCertificate() {
        return certificate;
    }

    /**
     * Sets the value of the certificate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Certificate }
     *     
     */
    public void setCertificate(Certificate value) {
        this.certificate = value;
    }

    /**
     * Gets the value of the rejection property.
     * 
     * @return
     *     possible object is
     *     {@link Rejection }
     *     
     */
    public Rejection getRejection() {
        return rejection;
    }

    /**
     * Sets the value of the rejection property.
     * 
     * @param value
     *     allowed object is
     *     {@link Rejection }
     *     
     */
    public void setRejection(Rejection value) {
        this.rejection = value;
    }

    /**
     * Gets the value of the signatureFiles property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link SignerSignatureFiles }{@code >}
     *     
     */
    public JAXBElement<SignerSignatureFiles> getSignatureFiles() {
        return signatureFiles;
    }

    /**
     * Sets the value of the signatureFiles property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link SignerSignatureFiles }{@code >}
     *     
     */
    public void setSignatureFiles(JAXBElement<SignerSignatureFiles> value) {
        this.signatureFiles = value;
    }

    /**
     * Gets the value of the idUpdate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getIdUpdate() {
        return idUpdate;
    }

    /**
     * Sets the value of the idUpdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setIdUpdate(JAXBElement<String> value) {
        this.idUpdate = value;
    }

    /**
     * Gets the value of the pdfAppearance property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link PdfAppearance }{@code >}
     *     
     */
    public JAXBElement<PdfAppearance> getPdfAppearance() {
        return pdfAppearance;
    }

    /**
     * Sets the value of the pdfAppearance property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link PdfAppearance }{@code >}
     *     
     */
    public void setPdfAppearance(JAXBElement<PdfAppearance> value) {
        this.pdfAppearance = value;
    }

}
