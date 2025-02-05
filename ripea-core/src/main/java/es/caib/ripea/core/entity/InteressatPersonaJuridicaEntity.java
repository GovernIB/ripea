package es.caib.ripea.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.api.dto.InteressatDocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.dto.InteressatIdiomaEnumDto;
import es.caib.ripea.core.api.dto.InteressatPersonaJuridicaDto;
import es.caib.ripea.core.api.dto.InteressatTipusEnumDto;
import es.caib.ripea.core.api.exception.InteressatTipusDocumentException;

/**
 * Classe del model de dades que representa un interessat de tipus persona jurídica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
public class InteressatPersonaJuridicaEntity extends InteressatEntity {

//	CAMP					TIPUS INTERESSAT	DESCRIPCIÓ
//	------------------------------------------------------------------------------------------------------------------------------------
//	raoSocial: 				JURÍDICA			nom de l’empresa en cas de persona jurídica.
	
	@Column(name = "rao_social", length = 80)
	protected String raoSocial;

	public String getRaoSocial() {
		return raoSocial;
	}
	public void setRaoSocial(String raoSocial) {
		this.raoSocial = raoSocial;
	}
	
	@Override
	public String getIdentificador() {
		return this.raoSocial;
	}

	@Override
	public void merge(InteressatDto dto) {
		super.merge(dto);
		InteressatPersonaJuridicaDto pjdDto = (InteressatPersonaJuridicaDto) dto;
		if (pjdDto.getRaoSocial()!=null) { this.raoSocial = pjdDto.getRaoSocial(); }
	}
	
	public void update(
			String raoSocial,
			InteressatDocumentTipusEnumDto documentTipus,
			String documentNum,
			String pais,
			String provincia,
			String municipi,
			String adresa,
			String codiPostal,
			String email,
			String telefon,
			String observacions,
			InteressatIdiomaEnumDto preferenciaIdioma,
			Boolean entregaDeh,
			Boolean entregaDehObligat,
			Boolean incapacitat) {
		this.raoSocial = raoSocial;
		this.documentTipus = documentTipus;
		this.documentNum = documentNum;
		this.pais = pais;
		this.provincia =  provincia;
		this.municipi =  municipi;
		this.adresa =  adresa;
		this.codiPostal =  codiPostal;
		this.email =  email;
		this.telefon =  telefon;
		this.observacions =  observacions;
		this.preferenciaIdioma =  preferenciaIdioma;
		this.entregaDeh = entregaDeh;
		this.entregaDehObligat = entregaDehObligat;
		this.incapacitat = incapacitat;
	}

	@Override
	public void update(InteressatDto dto) {

		if (!(dto instanceof InteressatPersonaJuridicaDto))
			throw new InteressatTipusDocumentException(dto.getDocumentNum(), InteressatTipusEnumDto.PERSONA_JURIDICA.name(), dto.getTipus().name(), this.expedient.getId());

		super.update(dto);

		InteressatPersonaJuridicaDto interessatPersonaJuridicaDto = (InteressatPersonaJuridicaDto) dto;
		this.raoSocial = interessatPersonaJuridicaDto.getRaoSocial();
	}

    /**
	 * Obté el Builder per a crear objectes de tipus interessat-persona jurídica.
	 * 
	 * @param raoSocial La raó social de l'interessat.
	 * @param documentTipus	El tipus de document de l'interessat.
	 * @param documentNum	El número de document de l'interessat.
	 * @param pais	El país de residència de l'interessat.
	 * @param provincia	La província de residència de l'interessat.
	 * @param municipi	El municipi de residència de l'interessat.
	 * @param adresa	L'adreça de residència de l'interessat.
	 * @param codiPostal	El codi postal de la residència de l'interessat.
	 * @param email	El correu electrònic de l'interessat.
	 * @param telefon	El telèfon de l'interessat.
	 * @param observacions	Camp per introduir observacions sobre l'interessat.
	 * @param expedient	Expedient on està vinculat l'interessat.
	 * @param representant	Representant de l'interessat.
	 * @param preferenciaIdioma	Idioma en que l'interessat desitja rebre les notificacions.
	 * @return
	 */
	public static Builder getBuilder(
			String raoSocial,
			InteressatDocumentTipusEnumDto documentTipus,
			String documentNum,
			String pais,
			String provincia,
			String municipi,
			String adresa,
			String codiPostal,
			String email,
			String telefon,
			String observacions,
			InteressatIdiomaEnumDto preferenciaIdioma,
			ExpedientEntity expedient,
			InteressatEntity representant,
			Boolean entregaDeh,
			Boolean entregaDehObligat,
			Boolean incapacitat) {
		return new Builder(
				raoSocial,
				documentTipus,
				documentNum,
				pais,
				provincia,
				municipi,
				adresa,
				codiPostal,
				email,
				telefon,
				observacions,
				preferenciaIdioma,
				expedient,
				representant,
				entregaDeh,
				entregaDehObligat,
				incapacitat);
	}
	public static Builder getBuilder(
			InteressatPersonaJuridicaDto dto,
			ExpedientEntity expedient,
			InteressatEntity representant) {
		return new Builder(
				dto.getRaoSocial(),
				dto.getDocumentTipus(),
				dto.getDocumentNum(),
				dto.getPais(),
				dto.getProvincia(),
				dto.getMunicipi(),
				dto.getAdresa(),
				dto.getCodiPostal(),
				dto.getEmail(),
				dto.getTelefon(),
				dto.getObservacions(),
				dto.getPreferenciaIdioma(),
				expedient,
				representant,
				dto.getEntregaDeh(),
				dto.getEntregaDehObligat(),
				dto.getIncapacitat());
	}

	/**
	 * Builder per a crear noves instàncies d'aquesta classe.
	 * 
	 * @author Limit Tecnologies <limit@limit.es>
	 */
	public static class Builder extends InteressatEntity.Builder {
		InteressatPersonaJuridicaEntity built;
		Builder(
				String raoSocial,
				InteressatDocumentTipusEnumDto documentTipus,
				String documentNum,
				String pais,
				String provincia,
				String municipi,
				String adresa,
				String codiPostal,
				String email,
				String telefon,
				String observacions,
				InteressatIdiomaEnumDto preferenciaIdioma,
				ExpedientEntity expedient,
				InteressatEntity representant,
				Boolean entregaDeh,
				Boolean entregaDehObligat,
				Boolean incapacitat) {
			built = new InteressatPersonaJuridicaEntity();
			built.raoSocial = raoSocial;
			built.documentTipus = documentTipus;
			built.documentNum = documentNum;
			built.pais = pais;
			built.provincia =  provincia;
			built.municipi =  municipi;
			built.adresa =  adresa;
			built.codiPostal =  codiPostal;
			built.email =  email;
			built.telefon =  telefon;
			built.observacions =  observacions;
			built.preferenciaIdioma =  preferenciaIdioma;
			built.notificacioAutoritzat =  true;
			built.expedient =  expedient;
			built.representant =  representant;
			built.esRepresentant = false;
			built.entregaDeh = entregaDeh;
			built.entregaDehObligat = entregaDehObligat;
			built.incapacitat = incapacitat;
		}
		public InteressatPersonaJuridicaEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((documentNum == null) ? 0 : documentNum.hashCode());
		result = prime * result + ((documentTipus == null) ? 0 : documentTipus.hashCode());
		result = prime * result + ((expedient == null) ? 0 : expedient.hashCode());
		result = prime * result + ((raoSocial == null) ? 0 : raoSocial.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		InteressatPersonaJuridicaEntity other = (InteressatPersonaJuridicaEntity) obj;
		if (documentNum == null) {
			if (other.documentNum != null)
				return false;
		} else if (!documentNum.equals(other.documentNum))
			return false;
		if (documentTipus != other.documentTipus)
			return false;
		if (expedient == null) {
			if (other.expedient != null)
				return false;
		} else if (!expedient.equals(other.expedient))
			return false;
		if (raoSocial == null) {
			if (other.raoSocial != null)
				return false;
		} else if (!raoSocial.equals(other.raoSocial))
			return false;
		return true;
	}

	@Override
	public InteressatTipusEnumDto getTipus() {
		return InteressatTipusEnumDto.PERSONA_JURIDICA;
	}

	@Override
	public String getNomComplet() {
		return raoSocial;
	}
	
	private static final long serialVersionUID = -2299453443943600172L;

}
