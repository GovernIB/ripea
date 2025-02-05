package es.caib.ripea.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.api.dto.InteressatDocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.dto.InteressatIdiomaEnumDto;
import es.caib.ripea.core.api.dto.InteressatPersonaFisicaDto;
import es.caib.ripea.core.api.dto.InteressatTipusEnumDto;
import es.caib.ripea.core.api.exception.InteressatTipusDocumentException;

/**
 * Classe del model de dades que representa un interessat de tipus persona física.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
public class InteressatPersonaFisicaEntity extends InteressatEntity {
	
//	CAMP					TIPUS INTERESSAT	DESCRIPCIÓ
//	------------------------------------------------------------------------------------------------------------------------------------
//	nom: 					FÍSICA				nom de l’interessat.
//	llinatge1: 				FÍSICA				primer llinatge de l’interessat.
//	llinatge2: 				FÍSICA				segon llinatge de l’interessat.

	@Column(name = "nom", length = 30)
	protected String nom;
	@Column(name = "llinatge1", length = 30)
	protected String llinatge1;
	@Column(name = "llinatge2", length = 30)
	protected String llinatge2;

	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getLlinatge1() {
		return llinatge1;
	}
	public void setLlinatge1(String llinatge1) {
		this.llinatge1 = llinatge1;
	}

	public String getLlinatge2() {
		return llinatge2;
	}
	public void setLlinatge2(String llinatge2) {
		this.llinatge2 = llinatge2;
	}
	
	@Override
	public String getIdentificador() {
		return this.llinatge1 + (this.llinatge2 != null ? " " + this.llinatge2 : "") + ", " + this.nom;
	}
	
	@Override
	public void merge(InteressatDto dto) {
		super.merge(dto);
		InteressatPersonaFisicaDto pfiDto = (InteressatPersonaFisicaDto) dto;
		if (pfiDto.getNom()!=null) { this.nom = pfiDto.getNom(); }
		if (pfiDto.getLlinatge1()!=null) { this.llinatge1 = pfiDto.getLlinatge1(); }
		if (pfiDto.getLlinatge2()!=null) { this.llinatge2 = pfiDto.getLlinatge2(); }
	}
	
	public void update(
			String nom,
			String llinatge1,
			String llinatge2,
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
		this.nom = nom;
		this.llinatge1 = llinatge1;
		this.llinatge2 = llinatge2;
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

		if (!(dto instanceof InteressatPersonaFisicaDto))
			throw new InteressatTipusDocumentException(dto.getDocumentNum(), InteressatTipusEnumDto.PERSONA_FISICA.name(), dto.getTipus().name(), this.expedient.getId());

		super.update(dto);

		InteressatPersonaFisicaDto interessatPersonaFisicaDto = (InteressatPersonaFisicaDto) dto;
		this.nom = interessatPersonaFisicaDto.getNom();
        this.llinatge1 = interessatPersonaFisicaDto.getLlinatge1();
        this.llinatge2 = interessatPersonaFisicaDto.getLlinatge2();
	}

	/**
	 * Obté el Builder per a crear objectes de tipus interessat-persona física.
	 * 
	 * @param nom El nom de l'interessat.
	 * @param llinatge1	El primer llinatge de l'interessat.
	 * @param llinatge2	El segon llinatge de l'interessat.
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
			String nom,
			String llinatge1,
			String llinatge2,
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
				nom,
				llinatge1,
				llinatge2,
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
			InteressatPersonaFisicaDto dto,
			ExpedientEntity expedient,
			InteressatEntity representant) {
		return new Builder(
				dto.getNom(),
				dto.getLlinatge1(),
				dto.getLlinatge2(),
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
		InteressatPersonaFisicaEntity built;
		Builder(
				String nom,
				String llinatge1,
				String llinatge2,
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
			built = new InteressatPersonaFisicaEntity();
			built.nom = nom;
			built.llinatge1 = llinatge1;
			built.llinatge2 = llinatge2;
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
			built.notificacioAutoritzat = true;
			built.expedient =  expedient;
			built.representant =  representant;
			built.esRepresentant = false;
			built.entregaDeh = entregaDeh;
			built.entregaDehObligat = entregaDehObligat;
			built.incapacitat = incapacitat;
		}
		public InteressatPersonaFisicaEntity build() {
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
		result = prime * result + ((llinatge1 == null) ? 0 : llinatge1.hashCode());
		result = prime * result + ((llinatge2 == null) ? 0 : llinatge2.hashCode());
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
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
		InteressatPersonaFisicaEntity other = (InteressatPersonaFisicaEntity) obj;
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
		if (llinatge1 == null) {
			if (other.llinatge1 != null)
				return false;
		} else if (!llinatge1.equals(other.llinatge1))
			return false;
		if (llinatge2 == null) {
			if (other.llinatge2 != null)
				return false;
		} else if (!llinatge2.equals(other.llinatge2))
			return false;
		if (nom == null) {
			if (other.nom != null)
				return false;
		} else if (!nom.equals(other.nom))
			return false;
		return true;
	}

	@Override
	public String getNomComplet() {
		StringBuilder sb = new StringBuilder();
		if (nom != null) {
			sb.append(nom);
		}
		if (llinatge1 != null) {
			sb.append(" ");
			sb.append(llinatge1);
			if (llinatge2 != null) {
				sb.append(" ");
				sb.append(llinatge2);
			}
		}
		return sb.toString();
	}
	
	@Override
	public InteressatTipusEnumDto getTipus() {
		return InteressatTipusEnumDto.PERSONA_FISICA;
	}

	private static final long serialVersionUID = -2299453443943600172L;


}
