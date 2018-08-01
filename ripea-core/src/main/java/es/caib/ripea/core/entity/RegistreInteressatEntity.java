/**
 * 
 */
package es.caib.ripea.core.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.api.registre.RegistreInteressatCanalEnum;
import es.caib.ripea.core.api.registre.RegistreInteressatDocumentTipusEnum;
import es.caib.ripea.core.api.registre.RegistreInteressatTipusEnum;
import es.caib.ripea.core.audit.RipeaAuditable;

/**
 * Classe del model de dades que representa un interessat
 * d'una anotació al registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name = "ipa_registre_inter")
@EntityListeners(AuditingEntityListener.class)
public class RegistreInteressatEntity extends RipeaAuditable<Long> {

	@Column(name = "tipus", length = 1, nullable = false)
	private String tipus;
	@Column(name = "doc_tipus", length = 1)
	private String documentTipus;
	@Column(name = "doc_num", length = 17)
	private String documentNum;
	@Column(name = "nom", length = 30)
	private String nom;
	@Column(name = "llinatge1", length = 30)
	private String llinatge1;
	@Column(name = "llinatge2", length = 30)
	private String llinatge2;
	@Column(name = "rao_social", length = 80)
	private String raoSocial;
	@Column(name = "pais", length = 4)
	private String pais;
	@Column(name = "provincia", length = 100)
	private String provincia;
	@Column(name = "municipi", length = 100)
	private String municipi;
	@Column(name = "adresa", length = 160)
	private String adresa;
	@Column(name = "codi_postal", length = 5)
	private String codiPostal;
	@Column(name = "email", length = 160)
	private String email;
	@Column(name = "telefon", length = 20)
	private String telefon;
	@Column(name = "email_hab", length = 160)
	private String emailHabilitat;
	@Column(name = "canal_pref", length = 2)
	private String canalPreferent;
	@Column(name = "observacions", length = 160)
	private String observacions;
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "representant_id")
	@ForeignKey(name = "ipa_interessat_representant_fk")
	protected RegistreInteressatEntity representant;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "representat_id")
	@ForeignKey(name = "ipa_interessat_representat_fk")
	protected RegistreInteressatEntity representat;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "registre_id")
	@ForeignKey(name = "ipa_interessat_registre_fk")
	protected RegistreEntity registre;
	@Version
	private long version = 0;



	public RegistreInteressatTipusEnum getTipus() {
		return RegistreInteressatTipusEnum.valorAsEnum(tipus);
	}
	public RegistreInteressatDocumentTipusEnum getDocumentTipus() {
		return RegistreInteressatDocumentTipusEnum.valorAsEnum(documentTipus);
	}
	public String getDocumentNum() {
		return documentNum;
	}
	public String getNom() {
		return nom;
	}
	public String getLlinatge1() {
		return llinatge1;
	}
	public String getLlinatge2() {
		return llinatge2;
	}
	public String getRaoSocial() {
		return raoSocial;
	}
	public String getPais() {
		return pais;
	}
	public String getProvincia() {
		return provincia;
	}
	public String getMunicipi() {
		return municipi;
	}
	public String getAdresa() {
		return adresa;
	}
	public String getCodiPostal() {
		return codiPostal;
	}
	public String getEmail() {
		return email;
	}
	public String getTelefon() {
		return telefon;
	}
	public String getEmailHabilitat() {
		return emailHabilitat;
	}
	public RegistreInteressatCanalEnum getCanalPreferent() {
		return RegistreInteressatCanalEnum.valorAsEnum(canalPreferent);
	}
	public String getObservacions() {
		return observacions;
	}
	public RegistreInteressatEntity getRepresentant() {
		return representant;
	}
	public RegistreInteressatEntity getRepresentat() {
		return representat;
	}
	public RegistreEntity getRegistre() {
		return registre;
	}

	public void updateRepresentant(
			RegistreInteressatTipusEnum tipus,
			RegistreInteressatDocumentTipusEnum documentTipus,
			String documentNum,
			String nom,
			String llinatge1,
			String llinatge2,
			String raoSocial,
			String pais,
			String provincia,
			String municipi,
			String adresa,
			String codiPostal,
			String email,
			String telefon,
			String emailHabilitat,
			RegistreInteressatCanalEnum canalPreferent) {
		Builder representantBuilder;
		if (RegistreInteressatTipusEnum.PERSONA_FIS == tipus) {
			representantBuilder = getBuilder(
					tipus,
					documentTipus,
					documentNum,
					nom,
					llinatge1,
					llinatge2,
					registre);
		} else {
			representantBuilder = getBuilder(
					tipus,
					documentTipus,
					documentNum,
					raoSocial,
					registre);
		}
		representantBuilder.
				pais(pais).
				provincia(provincia).
				municipi(municipi).
				adresa(adresa).
				codiPostal(codiPostal).
				email(email).
				telefon(telefon).
				emailHabilitat(emailHabilitat).
				canalPreferent(canalPreferent).
				representat(this);
		this.representant = representantBuilder.build();
	}

	public static Builder getBuilder(
			RegistreInteressatTipusEnum tipus,
			RegistreInteressatDocumentTipusEnum documentTipus,
			String documentNum,
			String nom,
			String llinatge1,
			String llinatge2,
			RegistreEntity registre) {
		return new Builder(
				tipus,
				documentTipus,
				documentNum,
				nom,
				llinatge1,
				llinatge2,
				registre);
	}
	public static Builder getBuilder(
			RegistreInteressatTipusEnum tipus,
			RegistreInteressatDocumentTipusEnum documentTipus,
			String documentNum,
			String raoSocial,
			RegistreEntity registre) {
		return new Builder(
				tipus,
				documentTipus,
				documentNum,
				raoSocial,
				registre);
	}

	public static class Builder {
		RegistreInteressatEntity built;
		Builder(
				RegistreInteressatTipusEnum tipus,
				RegistreInteressatDocumentTipusEnum documentTipus,
				String documentNum,
				String nom,
				String llinatge1,
				String llinatge2,
				RegistreEntity registre) {
			built = new RegistreInteressatEntity();
			built.tipus = tipus.getValor();
			if (documentTipus != null)
				built.documentTipus = documentTipus.getValor();
			built.documentNum = documentNum;
			built.nom = nom;
			built.llinatge1 = llinatge1;
			built.llinatge2 = llinatge2;
			built.registre = registre;
		}
		Builder(
				RegistreInteressatTipusEnum tipus,
				RegistreInteressatDocumentTipusEnum documentTipus,
				String documentNum,
				String raoSocial,
				RegistreEntity registre) {
			built = new RegistreInteressatEntity();
			built.tipus = tipus.getValor();
			if (documentTipus != null)
				built.documentTipus = documentTipus.getValor();
			built.documentNum = documentNum;
			built.raoSocial = raoSocial;
			built.registre = registre;
		}
		public Builder pais(String pais) {
			built.pais = pais;
			return this;
		}
		public Builder provincia(String provincia) {
			built.provincia = provincia;
			return this;
		}
		public Builder municipi(String municipi) {
			built.municipi = municipi;
			return this;
		}
		public Builder adresa(String adresa) {
			built.adresa = adresa;
			return this;
		}
		public Builder codiPostal(String codiPostal) {
			built.codiPostal = codiPostal;
			return this;
		}
		public Builder email(String email) {
			built.email = email;
			return this;
		}
		public Builder telefon(String telefon) {
			built.telefon = telefon;
			return this;
		}
		public Builder emailHabilitat(String emailHabilitat) {
			built.emailHabilitat = emailHabilitat;
			return this;
		}
		public Builder canalPreferent(RegistreInteressatCanalEnum canalPreferent) {
			if (canalPreferent != null)
				built.canalPreferent = canalPreferent.getValor();
			return this;
		}
		public Builder observacions(String observacions) {
			built.observacions = observacions;
			return this;
		}
		public Builder representat(RegistreInteressatEntity representat) {
			built.representat = representat;
			return this;
		}
		public RegistreInteressatEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((documentNum == null) ? 0 : documentNum.hashCode());
		result = prime * result
				+ ((documentTipus == null) ? 0 : documentTipus.hashCode());
		result = prime * result
				+ ((llinatge1 == null) ? 0 : llinatge1.hashCode());
		result = prime * result
				+ ((llinatge2 == null) ? 0 : llinatge2.hashCode());
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
		result = prime * result
				+ ((raoSocial == null) ? 0 : raoSocial.hashCode());
		result = prime * result
				+ ((registre == null) ? 0 : registre.hashCode());
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
		RegistreInteressatEntity other = (RegistreInteressatEntity) obj;
		if (documentNum == null) {
			if (other.documentNum != null)
				return false;
		} else if (!documentNum.equals(other.documentNum))
			return false;
		if (documentTipus != other.documentTipus)
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
		if (raoSocial == null) {
			if (other.raoSocial != null)
				return false;
		} else if (!raoSocial.equals(other.raoSocial))
			return false;
		if (registre == null) {
			if (other.registre != null)
				return false;
		} else if (!registre.equals(other.registre))
			return false;
		return true;
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
