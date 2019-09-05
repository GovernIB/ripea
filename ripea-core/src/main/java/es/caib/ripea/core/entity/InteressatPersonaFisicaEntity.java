/**
 * 
 */
package es.caib.ripea.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.api.dto.InteressatDocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.InteressatIdiomaEnumDto;

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
			Boolean notificacioAutoritzat,
			Boolean entregaDeh,
			Boolean entregaDehObligat) {
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
		this.notificacioAutoritzat =  notificacioAutoritzat;
		this.entregaDeh = entregaDeh;
		this.entregaDehObligat = entregaDehObligat;
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
	 * @param notificacioIdioma	Idioma en que l'interessat desitja rebre les notificacions.
	 * @param notificacioAutoritzat	Camp per indicar si l'interessat ha autoritzat la recepció de notificacions en format electrònic.
	 * @param expedient	Expedient on està vinculat l'interessat.
	 * @param representant	Representant de l'interessat.
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
			Boolean notificacioAutoritzat,
			ExpedientEntity expedient,
			InteressatEntity representant,
			Boolean entregaDeh,
			Boolean entregaDehObligat) {
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
				notificacioAutoritzat,
				expedient,
				representant,
				entregaDeh,
				entregaDehObligat);
	}

	/**
	 * Builder per a crear noves instàncies d'aquesta classe.
	 * 
	 * @author Limit Tecnologies <limit@limit.es>
	 */
	public static class Builder {
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
				Boolean notificacioAutoritzat,
				ExpedientEntity expedient,
				InteressatEntity representant,
				Boolean entregaDeh,
				Boolean entregaDehObligat) {
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
			built.notificacioAutoritzat =  notificacioAutoritzat;
			built.expedient =  expedient;
			built.representant =  representant;
			built.esRepresentant = false;
			built.entregaDeh = entregaDeh;
			built.entregaDehObligat = entregaDehObligat;
			
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

	private static final long serialVersionUID = -2299453443943600172L;


}
