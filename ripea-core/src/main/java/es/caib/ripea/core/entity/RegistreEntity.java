/**
 * 
 */
package es.caib.ripea.core.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.audit.RipeaAuditable;

/**
 * Classe del model de dades que representa una anotació al registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "ipa_registre")
@EntityListeners(AuditingEntityListener.class)
public class RegistreEntity extends RipeaAuditable<Long> {

	@Column(name = "aplicacio_codi", length = 20)
	private String aplicacioCodi;
	@Column(name = "aplicacio_versio", length = 15)
	private String aplicacioVersio;
	@Column(name = "assumpte_codi_codi", length = 16)
	private String assumpteCodiCodi;
	@Column(name = "assumpte_codi_desc", length = 100)
	private String assumpteCodiDescripcio;
	@Column(name = "assumpte_tipus_codi", length = 16)
	private String assumpteTipusCodi;
	@Column(name = "assumpte_tipus_desc", length = 100)
	private String assumpteTipusDescripcio;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data", nullable = false)
	private Date data;
	@Column(name = "doc_fisica_codi", length = 1)
	private String docFisicaCodi;
	@Column(name = "doc_fisica_desc", length = 100)
	private String docFisicaDescripcio;
	@Column(name = "entitat_codi", length = 21, nullable = false)
	private String entitatCodi;
	@Column(name = "entitat_desc", length = 100)
	private String entitatDescripcio;
	@Column(name = "expedient_numero", length = 80)
	private String expedientNumero;
	@Column(name = "exposa", length = 4000)
	private String exposa;
	@Column(name = "extracte", length = 240)
	private String extracte;
	@Column(name = "procediment_codi", length = 20)
	private String procedimentCodi;
	@Column(name = "identificador", length = 100, nullable = false)
	private String identificador;
	@Column(name = "idioma_codi", length = 2, nullable = false)
	private String idiomaCodi;
	@Column(name = "idioma_desc", length = 100)
	private String idiomaDescripcio;
	@Column(name = "llibre_codi", length = 4, nullable = false)
	private String llibreCodi;
	@Column(name = "llibre_desc", length = 100)
	private String llibreDescripcio;
	@Column(name = "observacions", length = 50)
	private String observacions;
	@Column(name = "oficina_codi", length = 21, nullable = false)
	private String oficinaCodi;
	@Column(name = "oficina_desc", length = 100)
	private String oficinaDescripcio;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "origen_data")
	private Date origenData;
	@Column(name = "origen_registre_num", length = 80)
	private String origenRegistreNumero;
	@Column(name = "ref_externa", length = 16)
	private String refExterna;
	@Column(name = "solicita", length = 4000)
	private String solicita;
	@Column(name = "transport_num", length = 20)
	private String transportNumero;
	@Column(name = "transport_tipus_codi", length = 2)
	private String transportTipusCodi;
	@Column(name = "transport_tipus_desc", length = 100)
	private String transportTipusDescripcio;
	@Column(name = "usuari_codi", length = 20)
	private String usuariCodi;
	@Column(name = "usuari_nom", length = 80)
	private String usuariNom;
	@Column(name = "desti_codi", length = 21, nullable = false)
	private String destiCodi;
	@Column(name = "desti_descripcio", length = 100)
	private String destiDescripcio;
	@Column(name = "justificant_arxiu_uuid", length = 256)
	private String justificantArxiuUuid;
	
	

	@OneToMany(
			mappedBy = "registre",
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	private List<RegistreInteressatEntity> interessats = new ArrayList<RegistreInteressatEntity>();
	
	@OneToMany(
			mappedBy = "registre",
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	private List<RegistreAnnexEntity> annexos = new ArrayList<RegistreAnnexEntity>();
	
	
	// removed "cascade = CascadeType.ALL, orphanRemoval = true" because registreRepository.delete(registre) in ExpedientPeticioHelper.crearExpedientsPeticions() was removing also expedientPeticio
	@OneToMany(
			mappedBy = "registre",
			fetch = FetchType.LAZY)
	private List<ExpedientPeticioEntity> expedientPeticions = new ArrayList<ExpedientPeticioEntity>();
	
	
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "entitat_id")
	protected EntitatEntity entitat;

	public static Builder getBuilder(
			String assumpteTipusCodi,
			Date data,
			String entitatCodi,
			String identificador,
			String idiomaCodi,
			String llibreCodi,
			String oficinaCodi,
			String destiCodi,
			EntitatEntity entitat) {
		return new Builder(
				assumpteTipusCodi,
				data,
				entitatCodi,
				identificador,
				idiomaCodi,
				llibreCodi,
				oficinaCodi,
				destiCodi,
				entitat);
	}
	
	

	/**
	 * Builder per a crear noves instàncies d'aquesta classe.
	 * 
	 * @author Limit Tecnologies <limit@limit.es>
	 */
	public static class Builder {
		RegistreEntity built;

		Builder(
				String assumpteTipusCodi,
				Date data,
				String entitatCodi,
				String identificador,
				String idiomaCodi,
				String llibreCodi,
				String oficinaCodi,
				String destiCodi,
				EntitatEntity entitat) {
			built = new RegistreEntity();
			built.assumpteTipusCodi = assumpteTipusCodi;
			built.data = data;
			built.entitatCodi = entitatCodi;
			built.identificador = identificador;
			built.idiomaCodi = idiomaCodi;
			built.llibreCodi = llibreCodi;
			built.oficinaCodi = oficinaCodi;
			built.destiCodi = destiCodi;
			built.entitat = entitat;
		}

		public Builder aplicacioCodi(String aplicacioCodi) {
			built.aplicacioCodi = aplicacioCodi;
			return this;
		}

		public Builder aplicacioVersio(String aplicacioVersio) {
			built.aplicacioVersio = aplicacioVersio;
			return this;
		}

		public Builder assumpteCodiCodi(String assumpteCodiCodi) {
			built.assumpteCodiCodi = assumpteCodiCodi;
			return this;
		}

		public Builder assumpteCodiDescripcio(String assumpteCodiDescripcio) {
			built.assumpteCodiDescripcio = assumpteCodiDescripcio;
			return this;
		}

		public Builder assumpteTipusDescripcio(String assumpteTipusDescripcio) {
			built.assumpteTipusDescripcio = assumpteTipusDescripcio;
			return this;
		}

		public Builder docFisicaCodi(String docFisicaCodi) {
			built.docFisicaCodi = docFisicaCodi;
			return this;
		}

		public Builder docFisicaDescripcio(String docFisicaDescripcio) {
			built.docFisicaDescripcio = docFisicaDescripcio;
			return this;
		}

		public Builder entitatDescripcio(String entitatDescripcio) {
			built.entitatDescripcio = entitatDescripcio;
			return this;
		}

		public Builder expedientNumero(String expedientNumero) {
			built.expedientNumero = expedientNumero;
			return this;
		}

		public Builder exposa(String exposa) {
			built.exposa = exposa;
			return this;
		}

		public Builder extracte(String extracte) {
			built.extracte = extracte;
			return this;
		}
		public Builder procedimentCodi(String procedimentCodi) {
			built.procedimentCodi = procedimentCodi;
			return this;
		}

		public Builder idiomaDescripcio(String idiomaDescripcio) {
			built.idiomaDescripcio = idiomaDescripcio;
			return this;
		}

		public Builder llibreDescripcio(String llibreDescripcio) {
			built.llibreDescripcio = llibreDescripcio;
			return this;
		}

		public Builder observacions(String observacions) {
			built.observacions = observacions;
			return this;
		}
		
		public Builder oficinaDescripcio(String oficinaDescripcio) {
			built.oficinaDescripcio = oficinaDescripcio;
			return this;
		}

		public Builder origenData(Date origenData) {
			built.origenData = origenData;
			return this;
		}

		public Builder origenRegistreNumero(String origenRegistreNumero) {
			built.origenRegistreNumero = origenRegistreNumero;
			return this;
		}

		public Builder refExterna(String refExterna) {
			built.refExterna = refExterna;
			return this;
		}

		public Builder solicita(String solicita) {
			built.solicita = solicita;
			return this;
		}

		public Builder transportNumero(String transportNumero) {
			built.transportNumero = transportNumero;
			return this;
		}

		public Builder transportTipusCodi(String transportTipusCodi) {
			built.transportTipusCodi = transportTipusCodi;
			return this;
		}

		public Builder transportTipusDescripcio(String transportTipusDescripcio) {
			built.transportTipusDescripcio = transportTipusDescripcio;
			return this;
		}

		public Builder usuariCodi(String usuariCodi) {
			built.usuariCodi = usuariCodi;
			return this;
		}

		public Builder usuariNom(String usuariNom) {
			built.usuariNom = usuariNom;
			return this;
		}
		
		public Builder destiDescripcio(String destiDescripcio) {
			built.destiDescripcio = destiDescripcio;
			return this;
		}
		
		public Builder justificantArxiuUuid(String justificantArxiuUuid) {
			built.justificantArxiuUuid = justificantArxiuUuid;
			return this;
		}

		public RegistreEntity build() {
			return built;
		}
	}
	
	

	public String getAplicacioCodi() {
		return aplicacioCodi;
	}

	public void updateAplicacioCodi(String aplicacioCodi) {
		this.aplicacioCodi = aplicacioCodi;
	}

	public String getAplicacioVersio() {
		return aplicacioVersio;
	}

	public void updateAplicacioVersio(String aplicacioVersio) {
		this.aplicacioVersio = aplicacioVersio;
	}

	public String getAssumpteCodiCodi() {
		return assumpteCodiCodi;
	}

	public void updateAssumpteCodiCodi(String assumpteCodiCodi) {
		this.assumpteCodiCodi = assumpteCodiCodi;
	}

	public String getAssumpteCodiDescripcio() {
		return assumpteCodiDescripcio;
	}

	public void updateAssumpteCodiDescripcio(String assumpteCodiDescripcio) {
		this.assumpteCodiDescripcio = assumpteCodiDescripcio;
	}

	public String getAssumpteTipusCodi() {
		return assumpteTipusCodi;
	}

	public void updateAssumpteTipusCodi(String assumpteTipusCodi) {
		this.assumpteTipusCodi = assumpteTipusCodi;
	}

	public String getAssumpteTipusDescripcio() {
		return assumpteTipusDescripcio;
	}

	public void updateAssumpteTipusDescripcio(String assumpteTipusDescripcio) {
		this.assumpteTipusDescripcio = assumpteTipusDescripcio;
	}

	public Date getData() {
		return data;
	}

	public void updateData(Date data) {
		this.data = data;
	}

	public String getDocFisicaCodi() {
		return docFisicaCodi;
	}

	public void updateDocFisicaCodi(String docFisicaCodi) {
		this.docFisicaCodi = docFisicaCodi;
	}

	public String getDocFisicaDescripcio() {
		return docFisicaDescripcio;
	}

	public void updateDocFisicaDescripcio(String docFisicaDescripcio) {
		this.docFisicaDescripcio = docFisicaDescripcio;
	}

	public String getEntitatCodi() {
		return entitatCodi;
	}

	public void updateEntitatCodi(String entitatCodi) {
		this.entitatCodi = entitatCodi;
	}

	public String getEntitatDescripcio() {
		return entitatDescripcio;
	}

	public void updateEntitatDescripcio(String entitatDescripcio) {
		this.entitatDescripcio = entitatDescripcio;
	}

	public String getExpedientNumero() {
		return expedientNumero;
	}

	public void updateExpedientNumero(String expedientNumero) {
		this.expedientNumero = expedientNumero;
	}

	public String getExposa() {
		return exposa;
	}

	public void updateExposa(String exposa) {
		this.exposa = exposa;
	}

	public String getIdentificador() {
		return identificador;
	}

	public void updateIdentificador(String identificador) {
		this.identificador = identificador;
	}

	public String getIdiomaCodi() {
		return idiomaCodi;
	}

	public void updateIdiomaCodi(String idiomaCodi) {
		this.idiomaCodi = idiomaCodi;
	}

	public String getidiomaDescripcio() {
		return idiomaDescripcio;
	}

	public String getExtracte() {
		return extracte;
	}

	public String getProcedimentCodi() {
		return procedimentCodi;
	}

	public EntitatEntity getEntitat() {
		return entitat;
	}

	public void updateidiomaDescripcio(String idiomaDescripcio) {
		this.idiomaDescripcio = idiomaDescripcio;
	}

	public String getLlibreCodi() {
		return llibreCodi;
	}

	public void updateLlibreCodi(String llibreCodi) {
		this.llibreCodi = llibreCodi;
	}

	public String getLlibreDescripcio() {
		return llibreDescripcio;
	}

	public void updateLlibreDescripcio(String llibreDescripcio) {
		this.llibreDescripcio = llibreDescripcio;
	}

	public String getObservacions() {
		return observacions;
	}

	public void updateObservacions(String observacions) {
		this.observacions = observacions;
	}

	public String getOficinaCodi() {
		return oficinaCodi;
	}

	public void updateOficinaCodi(String oficinaCodi) {
		this.oficinaCodi = oficinaCodi;
	}

	public String getOficinaDescripcio() {
		return oficinaDescripcio;
	}

	public void updateOficinaDescripcio(String oficinaDescripcio) {
		this.oficinaDescripcio = oficinaDescripcio;
	}

	public Date getOrigenData() {
		return origenData;
	}

	public void updateOrigenData(Date origenData) {
		this.origenData = origenData;
	}

	public String getOrigenRegistreNumero() {
		return origenRegistreNumero;
	}

	public void updateOrigenRegistreNumero(String origenRegistreNumero) {
		this.origenRegistreNumero = origenRegistreNumero;
	}

	public String getRefExterna() {
		return refExterna;
	}

	public void updateRefExterna(String refExterna) {
		this.refExterna = refExterna;
	}

	public String getSolicita() {
		return solicita;
	}

	public void updateSolicita(String solicita) {
		this.solicita = solicita;
	}

	public String getTransportNumero() {
		return transportNumero;
	}

	public void updateTransportNumero(String transportNumero) {
		this.transportNumero = transportNumero;
	}

	public String getTransportTipusCodi() {
		return transportTipusCodi;
	}

	public void updateTransportTipusCodi(String transportTipusCodi) {
		this.transportTipusCodi = transportTipusCodi;
	}

	public String getTransportTipusDescripcio() {
		return transportTipusDescripcio;
	}

	public void updateTransportTipusDescripcio(String transportTipusDescripcio) {
		this.transportTipusDescripcio = transportTipusDescripcio;
	}

	public String getUsuariCodi() {
		return usuariCodi;
	}

	public void updateUsuariCodi(String usuariCodi) {
		this.usuariCodi = usuariCodi;
	}

	public String getUsuariNom() {
		return usuariNom;
	}

	public void updateUsuariNom(String usuariNom) {
		this.usuariNom = usuariNom;
	}

	public String getDestiCodi() {
		return destiCodi;
	}

	public void updateDestiCodi(String destiCodi) {
		this.destiCodi = destiCodi;
	}

	public String getDestiDescripcio() {
		return destiDescripcio;
	}

	public void updateDestiDescripcio(String destiDescripcio) {
		this.destiDescripcio = destiDescripcio;
	}

	public List<RegistreInteressatEntity> getInteressats() {
		return interessats;
	}

	public void updateInteressats(List<RegistreInteressatEntity> interessats) {
		this.interessats = interessats;
	}

	public List<RegistreAnnexEntity> getAnnexos() {
		return annexos;
	}

	public void updateAnnexos(List<RegistreAnnexEntity> annexos) {
		this.annexos = annexos;
	}
	
	public String getJustificantArxiuUuid() {
		return justificantArxiuUuid;
	}

	public List<ExpedientPeticioEntity> getExpedientPeticions() {
		return expedientPeticions;
	}

	private static final long serialVersionUID = 1815997738055924981L;

}
