package es.caib.ripea.core.api.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.commons.lang.builder.ToStringBuilder;
import java.io.Serializable;

@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.PROPERTY,
		property = "tipus"
)
@JsonSubTypes({
		@JsonSubTypes.Type(value = InteressatPersonaFisicaDto.class, name = "PERSONA_FISICA"),
		@JsonSubTypes.Type(value = InteressatPersonaJuridicaDto.class, name = "PERSONA_JURIDICA"),
		@JsonSubTypes.Type(value = InteressatAdministracioDto.class, name = "ADMINISTRACIO")
})
public abstract class InteressatDto implements Serializable {

	protected Long id;
	protected InteressatTipusEnumDto tipus;
	protected InteressatDocumentTipusEnumDto documentTipus;
	protected String documentNum;
	protected String pais;
	protected String paisNom;
	protected String provincia;
	protected String provinciaNom;
	protected String municipi;
	protected String municipiNom;
	protected String adresa;
	protected String codiPostal;
	protected String email;
	protected String telefon;
	protected String observacions;
	protected InteressatIdiomaEnumDto preferenciaIdioma;
	protected Long representantId;
	protected String representantIdentificador;
	protected String identificador;
	private boolean esRepresentant;
	private InteressatDto representant;
	protected boolean arxiuPropagat;
	protected boolean representantArxiuPropagat;
	protected boolean expedientArxiuPropagat;
	protected Boolean entregaDeh;
	private Boolean entregaDehObligat;
	private Boolean incapacitat;

	private boolean jaExistentExpedient = false;
	private InteressatAssociacioAccioEnum accio;
	
	protected boolean exporta = true;
	protected boolean exportaRepresentant= (representant!=null);
	
	public Boolean getIncapacitat() {
		return incapacitat;
	}
	public void setIncapacitat(Boolean incapacitat) {
		this.incapacitat = incapacitat;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public abstract InteressatTipusEnumDto getTipus();
	public void setTipus(InteressatTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public InteressatDocumentTipusEnumDto getDocumentTipus() {
		return documentTipus;
	}
	public void setDocumentTipus(InteressatDocumentTipusEnumDto documentTipus) {
		this.documentTipus = documentTipus;
	}
	public String getDocumentNum() {
		return documentNum;
	}
	public void setDocumentNum(String documentNum) {
		this.documentNum = documentNum;
	}
	public String getPais() {
		return pais;
	}
	public void setPais(String pais) {
		this.pais = pais;
	}
	public String getProvincia() {
		return provincia;
	}
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}
	public String getMunicipi() {
		return municipi;
	}
	public void setMunicipi(String municipi) {
		this.municipi = municipi;
	}
	public String getAdresa() {
		return adresa;
	}
	public void setAdresa(String adresa) {
		this.adresa = adresa;
	}
	public String getCodiPostal() {
		return codiPostal;
	}
	public void setCodiPostal(String codiPostal) {
		this.codiPostal = codiPostal;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTelefon() {
		return telefon;
	}
	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	public InteressatIdiomaEnumDto getPreferenciaIdioma() {
		return preferenciaIdioma;
	}
	public void setPreferenciaIdioma(InteressatIdiomaEnumDto preferenciaIdioma) {
		this.preferenciaIdioma = preferenciaIdioma;
	}
	public Long getRepresentantId() {
		return representantId;
	}
	public void setRepresentantId(Long representantId) {
		this.representantId = representantId;
	}
	public String getRepresentantIdentificador() {
		return representantIdentificador;
	}
	public void setRepresentantIdentificador(String representantIdentificador) {
		this.representantIdentificador = representantIdentificador;
	}
	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}
	public String getPaisNom() {
		return paisNom;
	}
	public void setPaisNom(String paisNom) {
		this.paisNom = paisNom;
	}
	public String getProvinciaNom() {
		return provinciaNom;
	}
	public void setProvinciaNom(String provinciaNom) {
		this.provinciaNom = provinciaNom;
	}
	public String getMunicipiNom() {
		return municipiNom;
	}
	public void setMunicipiNom(String municipiNom) {
		this.municipiNom = municipiNom;
	}
	public boolean isExporta() {
		return exporta;
	}
	public void setExporta(boolean exporta) {
		this.exporta = exporta;
	}
	public boolean isExportaRepresentant() {
		return exportaRepresentant;
	}
	public void setExportaRepresentant(boolean exportaRepresentant) {
		this.exportaRepresentant = exportaRepresentant;
	}
	public boolean isPersonaFisica() {
		return this instanceof InteressatPersonaFisicaDto;
	}
	public boolean isPersonaJuridica() {
		return this instanceof InteressatPersonaJuridicaDto;
	}
	public boolean isAdministracio() {
		return this instanceof InteressatAdministracioDto;
	}

	public InteressatAssociacioAccioEnum getAccio() { return accio; }
	public void setAccio(InteressatAssociacioAccioEnum accio) { this.accio = accio; }
	public boolean isJaExistentExpedient() { return jaExistentExpedient; }
	public void setJaExistentExpedient(boolean jaExistentExpedient) { this.jaExistentExpedient = jaExistentExpedient; }

	public abstract String getNomComplet();
	public String getNomCompletAmbDocument() {
		StringBuilder sb = new StringBuilder();
		sb.append(getNomComplet());
		if (documentNum != null) {
			sb.append(" - ");
			if (documentTipus != null) {
				sb.append(documentTipus.name());
				sb.append(": ");
			}
			sb.append(documentNum);
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public Boolean getEntregaDeh() {
		return entregaDeh;
	}
	public void setEntregaDeh(Boolean entregaDeh) {
		this.entregaDeh = entregaDeh;
	}

	public Boolean getEntregaDehObligat() {
		return entregaDehObligat;
	}
	public void setEntregaDehObligat(Boolean entregaDehObligat) {
		this.entregaDehObligat = entregaDehObligat;
	}
	public InteressatDto getRepresentant() {
		return representant;
	}
	public void setRepresentant(InteressatDto representant) {
		this.representant = representant;
	}
	public boolean isEsRepresentant() {
		return esRepresentant;
	}
	public void setEsRepresentant(boolean esRepresentant) {
		this.esRepresentant = esRepresentant;
	}
	public boolean isArxiuPropagat() {
		return arxiuPropagat;
	}
	public void setArxiuPropagat(boolean arxiuPropagat) {
		this.arxiuPropagat = arxiuPropagat;
	}
	public boolean isRepresentantArxiuPropagat() {
		return representantArxiuPropagat;
	}
	public void setRepresentantArxiuPropagat(boolean representantArxiuPropagat) {
		this.representantArxiuPropagat = representantArxiuPropagat;
	}
	public boolean isExpedientArxiuPropagat() {
		return expedientArxiuPropagat;
	}
	public void setExpedientArxiuPropagat(boolean expedientArxiuPropagat) {
		this.expedientArxiuPropagat = expedientArxiuPropagat;
	}



	private static final long serialVersionUID = -139254994389509932L;

}
