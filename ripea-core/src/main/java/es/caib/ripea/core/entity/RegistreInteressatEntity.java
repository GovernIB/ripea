/**
 * 
 */
package es.caib.ripea.core.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.distribucio.ws.backofficeintegracio.DocumentTipus;
import es.caib.distribucio.ws.backofficeintegracio.InteressatTipus;
import es.caib.ripea.core.audit.RipeaAuditable;

/**
 * Classe del model de dades que representa un interessat
 * d'una anotació al registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name = "ipa_registre_interessat")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class RegistreInteressatEntity extends RipeaAuditable<Long> {

	@Column(name = "adresa", length = 160)
	private String adresa;
	@Column(name = "canal", length = 30)
	private String canal;
	@Column(name = "cp", length = 5)
	private String cp;
	@Column(name = "doc_numero", length = 17)
	private String documentNumero;
	@Enumerated(EnumType.STRING)
	@Column(name = "doc_tipus", length = 15)
	private DocumentTipus documentTipus;
	@Column(name = "email", length = 160)
	private String email;
	@Column(name = "llinatge1", length = 30)
	private String llinatge1;
	@Column(name = "llinatge2", length = 30)
	private String llinatge2;
	@Column(name = "nom", length = 30)
	private String nom;
	@Column(name = "observacions", length = 160)
	private String observacions;
	@Column(name = "municipi_codi", length = 100)
	private String municipiCodi;
	@Column(name = "pais_codi", length = 4)
	private String paisCodi;
	@Column(name = "provincia_codi", length = 100)
	private String provinciaCodi;
	@Column(name = "municipi", length = 200)
	private String municipi;
	@Column(name = "pais", length = 200)
	private String pais;
	@Column(name = "provincia", length = 200)
	private String provincia;
	@Column(name = "rao_social", length = 80)
	private String raoSocial;
	@Column(name = "telefon", length = 20)
	private String telefon;
	@Enumerated(EnumType.STRING)
	@Column(name = "tipus", length = 40, nullable = false)
	private InteressatTipus tipus;
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "representant_id")
	@ForeignKey(name = "ipa_interessat_representant_fk")
	private RegistreInteressatEntity representant;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "registre_id")
	@ForeignKey(name = "ipa_interessat_registre_fk")
	private RegistreEntity registre;
	@Column(name = "organ_codi", length = 9)
	private String organCodi;
	
	
	
	public String getOrganCodi() {
		return organCodi;
	}

	public static Builder getBuilder(
			InteressatTipus tipus) {
		return new Builder(
				tipus);
	}

	public static class Builder {
		RegistreInteressatEntity built;

		Builder(
				InteressatTipus tipus) {
			built = new RegistreInteressatEntity();
			built.tipus = tipus;
		}

		public Builder adresa(String adresa) {
			built.adresa = adresa;
			return this;
		}
		public Builder canal(String canal) {
			built.canal = canal;
			return this;
		}
		public Builder cp(String cp) {
			built.cp = cp;
			return this;
		}
		public Builder documentNumero(String documentNumero) {
			built.documentNumero = documentNumero;
			return this;
		}
		public Builder documentTipus(DocumentTipus documentTipus) {
			built.documentTipus = documentTipus;
			return this;
		}
		public Builder email(String email) {
			built.email = email;
			return this;
		}
		public Builder llinatge1(String llinatge1) {
			built.llinatge1 = llinatge1;
			return this;
		}
		public Builder llinatge2(String llinatge2) {
			built.llinatge2 = llinatge2;
			return this;
		}
		public Builder municipiCodi(String municipiCodi) {
			built.municipiCodi = municipiCodi;
			return this;
		}
		public Builder nom(String nom) {
			built.nom = nom;
			return this;
		}
		public Builder observacions(String observacions) {
			built.observacions = observacions;
			return this;
		}
		public Builder paisCodi(String paisCodi) {
			built.paisCodi = paisCodi;
			return this;
		}
		public Builder provinciaCodi(String provinciaCodi) {
			built.provinciaCodi = provinciaCodi;
			return this;
		}
		public Builder raoSocial(String raoSocial) {
			built.raoSocial = raoSocial;
			return this;
		}
		public Builder telefon(String telefon) {
			built.telefon = telefon;
			return this;
		}
		public Builder representant(RegistreInteressatEntity representant) {
			built.representant = representant;
			return this;
		}
		public Builder registre(RegistreEntity registre) {
			built.registre = registre;
			return this;
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
		public Builder organCodi(String organCodi) {
			built.organCodi = organCodi;
			return this;
		}			
		public RegistreInteressatEntity build() {
			return built;
		}
	}

	public String getAdresa() {
		return adresa;
	}

	public String getCanal() {
		return canal;
	}

	public String getCp() {
		return cp;
	}

	public String getDocumentNumero() {
		return documentNumero;
	}

	public DocumentTipus getDocumentTipus() {
		return documentTipus;
	}

	public String getEmail() {
		return email;
	}

	public String getLlinatge1() {
		return llinatge1;
	}

	public String getLlinatge2() {
		return llinatge2;
	}

	public String getMunicipiCodi() {
		return municipiCodi;
	}

	public String getNom() {
		return nom;
	}

	public String getObservacions() {
		return observacions;
	}

	public String getPaisCodi() {
		return paisCodi;
	}

	public String getProvinciaCodi() {
		return provinciaCodi;
	}

	public String getRaoSocial() {
		return raoSocial;
	}

	public String getTelefon() {
		return telefon;
	}

	public InteressatTipus getTipus() {
		return tipus;
	}

	public RegistreInteressatEntity getRepresentant() {
		return representant;
	}

	public RegistreEntity getRegistre() {
		return registre;
	}
	public String getMunicipi() {
		return municipi;
	}

	public String getPais() {
		return pais;
	}

	public String getProvincia() {
		return provincia;
	}
	

}
