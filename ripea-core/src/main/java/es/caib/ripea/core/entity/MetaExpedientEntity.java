/**
 * 
 */
package es.caib.ripea.core.entity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.api.dto.MultiplicitatEnumDto;

/**
 * Classe del model de dades que representa un meta-expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "ipa_metaexpedient")
@EntityListeners(AuditingEntityListener.class)
public class MetaExpedientEntity extends MetaNodeEntity {

	@Column(name = "clasif_sia", length = 6, nullable = false)
	private String classificacioSia;
	@Column(name = "serie_doc", length = 30, nullable = false)
	private String serieDocumental;
	@Column(name = "not_activa", nullable = false)
	private boolean notificacioActiva;
	@Column(name = "not_seu_proc_codi", length = 44)
	private String notificacioSeuProcedimentCodi; // "IN0026NSPI"
	@Column(name = "not_seu_reg_lib", length = 4)
	private String notificacioSeuRegistreLlibre; // "L99";
	@Column(name = "not_seu_reg_ofi", length = 9)
	private String notificacioSeuRegistreOficina; // "O00009390"
	@Column(name = "not_seu_reg_org", length = 9)
	private String notificacioSeuRegistreOrgan; // "A04013511"
	@Column(name = "not_seu_exp_uni", length = 9)
	private String notificacioSeuExpedientUnitatOrganitzativa; // "1"
	@Column(name = "not_avis_titol", length = 256)
	private String notificacioAvisTitol;
	@Column(name = "not_avis_text", length = 1024)
	private String notificacioAvisText;
	@Column(name = "not_avis_textm", length = 200)
	private String notificacioAvisTextMobil;
	@Column(name = "not_ofici_titol", length = 1024)
	private String notificacioOficiTitol;
	@Column(name = "not_ofici_text", length = 256)
	private String notificacioOficiText;
	@ManyToOne(
			optional = true,
			fetch = FetchType.EAGER)
	@JoinColumn(name = "pare_id")
	@ForeignKey(name = "ipa_pare_metaexp_fk")
	private MetaExpedientEntity pare;
	@OneToMany(
			mappedBy = "metaExpedient",
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	private Set<MetaExpedientMetaDocumentEntity> metaDocuments = new HashSet<MetaExpedientMetaDocumentEntity>();

	public String getClassificacioSia() {
		return classificacioSia;
	}
	public String getSerieDocumental() {
		return serieDocumental;
	}
	public boolean isNotificacioActiva() {
		return notificacioActiva;
	}
	public String getNotificacioSeuProcedimentCodi() {
		return notificacioSeuProcedimentCodi;
	}
	public String getNotificacioSeuRegistreLlibre() {
		return notificacioSeuRegistreLlibre;
	}
	public String getNotificacioSeuRegistreOficina() {
		return notificacioSeuRegistreOficina;
	}
	public String getNotificacioSeuRegistreOrgan() {
		return notificacioSeuRegistreOrgan;
	}
	public String getNotificacioSeuExpedientUnitatOrganitzativa() {
		return notificacioSeuExpedientUnitatOrganitzativa;
	}
	public String getNotificacioAvisTitol() {
		return notificacioAvisTitol;
	}
	public String getNotificacioAvisText() {
		return notificacioAvisText;
	}
	public String getNotificacioAvisTextMobil() {
		return notificacioAvisTextMobil;
	}
	public String getNotificacioOficiTitol() {
		return notificacioOficiTitol;
	}
	public String getNotificacioOficiText() {
		return notificacioOficiText;
	}
	public MetaExpedientEntity getPare() {
		return pare;
	}
	public Set<MetaExpedientMetaDocumentEntity> getMetaDocuments() {
		return metaDocuments;
	}

	public void update(
			String codi,
			String nom,
			String descripcio,
			String classificacioSia,
			String serieDocumental,
			boolean notificacioActiva,
			String notificacioSeuProcedimentCodi,
			String notificacioSeuRegistreLlibre,
			String notificacioSeuRegistreOficina,
			String notificacioSeuRegistreOrgan,
			String notificacioSeuExpedientUnitatOrganitzativa,
			String notificacioAvisTitol,
			String notificacioAvisText,
			String notificacioAvisTextMobil,
			String notificacioOficiTitol,
			String notificacioOficiText,
			MetaExpedientEntity pare) {
		super.update(
				codi,
				nom,
				descripcio);
		this.classificacioSia = classificacioSia;
		this.serieDocumental = serieDocumental;
		this.notificacioActiva = notificacioActiva;
		this.notificacioSeuProcedimentCodi = notificacioSeuProcedimentCodi;
		this.notificacioSeuRegistreLlibre = notificacioSeuRegistreLlibre;
		this.notificacioSeuRegistreOficina = notificacioSeuRegistreOficina;
		this.notificacioSeuRegistreOrgan = notificacioSeuRegistreOrgan;
		this.notificacioSeuExpedientUnitatOrganitzativa = notificacioSeuExpedientUnitatOrganitzativa;
		this.notificacioAvisTitol = notificacioAvisTitol;
		this.notificacioAvisText = notificacioAvisText;
		this.notificacioAvisTextMobil = notificacioAvisTextMobil;
		this.notificacioOficiTitol = notificacioOficiTitol;
		this.notificacioOficiText = notificacioOficiText;
		this.pare = pare;
	}

	public void metaDocumentAdd(
			MetaDocumentEntity metaDocument,
			MultiplicitatEnumDto multiplicitat,
			boolean readOnly) {
		MetaExpedientMetaDocumentEntity metaExpedientMetaDocument = MetaExpedientMetaDocumentEntity.getBuilder(
				this,
				metaDocument,
				multiplicitat,
				readOnly,
				metaDocuments.size()).build();
		metaDocuments.add(metaExpedientMetaDocument);
	}
	public void metaDocumentDelete(MetaExpedientMetaDocumentEntity metaExpedientMetaDocument) {
		Iterator<MetaExpedientMetaDocumentEntity> it = metaDocuments.iterator();
		while (it.hasNext()) {
			MetaExpedientMetaDocumentEntity memd = it.next();
			if (memd.getId().equals(metaExpedientMetaDocument.getId()))
				it.remove();
		}
	}

	public static Builder getBuilder(
			String codi,
			String nom,
			String descripcio,
			String serieDocumental,
			String classificacioSia,
			boolean notificacioActiva,
			EntitatEntity entitat,
			MetaExpedientEntity pare) {
		return new Builder(
				codi,
				nom,
				descripcio,
				serieDocumental,
				classificacioSia,
				entitat,
				pare,
				notificacioActiva);
	}

	public static class Builder {
		MetaExpedientEntity built;
		Builder(
				String codi,
				String nom,
				String descripcio,
				String serieDocumental,
				String classificacioSia,
				EntitatEntity entitat,
				MetaExpedientEntity pare,
				boolean notificacioActiva) {
			built = new MetaExpedientEntity();
			built.codi = codi;
			built.nom = nom;
			built.descripcio = descripcio;
			built.serieDocumental = serieDocumental;
			built.classificacioSia = classificacioSia;
			built.entitat = entitat;
			built.tipus = MetaNodeTipusEnum.EXPEDIENT;
			built.pare = pare;
			built.notificacioActiva = notificacioActiva;
		}
		public Builder notificacioSeuProcedimentCodi(String notificacioSeuProcedimentCodi) {
			built.notificacioSeuProcedimentCodi = notificacioSeuProcedimentCodi;
			return this;
		}
		public Builder notificacioSeuRegistreLlibre(String notificacioSeuRegistreLlibre) {
			built.notificacioSeuRegistreLlibre = notificacioSeuRegistreLlibre;
			return this;
		}
		public Builder notificacioSeuRegistreOficina(String notificacioSeuRegistreOficina) {
			built.notificacioSeuRegistreOficina = notificacioSeuRegistreOficina;
			return this;
		}
		public Builder notificacioSeuRegistreOrgan(String notificacioSeuRegistreOrgan) {
			built.notificacioSeuRegistreOrgan = notificacioSeuRegistreOrgan;
			return this;
		}
		public Builder notificacioSeuExpedientUnitatOrganitzativa(String notificacioSeuExpedientUnitatOrganitzativa) {
			built.notificacioSeuExpedientUnitatOrganitzativa = notificacioSeuExpedientUnitatOrganitzativa;
			return this;
		}
		public Builder notificacioAvisTitol(String notificacioAvisTitol) {
			built.notificacioAvisTitol = notificacioAvisTitol;
			return this;
		}
		public Builder notificacioAvisText(String notificacioAvisText) {
			built.notificacioAvisText = notificacioAvisText;
			return this;
		}
		public Builder notificacioAvisTextMobil(String notificacioAvisTextMobil) {
			built.notificacioAvisTextMobil = notificacioAvisTextMobil;
			return this;
		}
		public Builder notificacioOficiTitol(String notificacioOficiTitol) {
			built.notificacioOficiTitol = notificacioOficiTitol;
			return this;
		}
		public Builder notificacioOficiText(String notificacioOficiText) {
			built.notificacioOficiText = notificacioOficiText;
			return this;
		}
		public MetaExpedientEntity build() {
			return built;
		}
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
