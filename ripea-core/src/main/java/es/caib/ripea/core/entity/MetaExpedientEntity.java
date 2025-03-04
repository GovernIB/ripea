/**
 * 
 */
package es.caib.ripea.core.entity;

import es.caib.ripea.core.api.dto.CrearReglaDistribucioEstatEnumDto;
import es.caib.ripea.core.api.dto.MetaExpedientRevisioEstatEnumDto;
import es.caib.ripea.core.api.dto.TipusClassificacioEnumDto;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Classe del model de dades que representa un meta-expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "ipa_metaexpedient", uniqueConstraints = {
        @UniqueConstraint(name = "ipa_metaexp_entitat_codi_uk", columnNames = { "entitat_id", "codi" }) })
@EntityListeners(AuditingEntityListener.class)
@Getter
public class MetaExpedientEntity extends MetaNodeEntity {

	@Column(name = "tipus_classificacio", length = 3, nullable = false)
	@Enumerated(EnumType.STRING)
    private TipusClassificacioEnumDto tipusClassificacio;
    @Column(name = "clasif_sia", length = 30, nullable = false)
    private String classificacio;
    @Column(name = "serie_doc", length = 30, nullable = false)
    private String serieDocumental;
    @Column(name = "expressio_numero", length = 100)
    private String expressioNumero;
    @Column(name = "not_activa", nullable = false)
    private boolean notificacioActiva;
    @Column(name = "PERMET_METADOCS_GENERALS", nullable = false)
    private boolean permetMetadocsGenerals;
    @Column(name = "PERMIS_DIRECTE")
    private boolean permisDirecte = false;
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "pare_id")
    @ForeignKey(name = "ipa_pare_metaexp_fk")
    private MetaExpedientEntity pare;

    @OneToMany(mappedBy = "metaExpedient", cascade = { CascadeType.ALL })
    protected Set<MetaExpedientSequenciaEntity> sequencies;

    @OneToMany(mappedBy = "metaExpedient", cascade = { CascadeType.ALL })
    protected Set<MetaDocumentEntity> metaDocuments;

    @OneToMany(mappedBy = "metaExpedient", cascade = { CascadeType.ALL })
    protected Set<ExpedientEstatEntity> estats;
    
    @OneToMany(mappedBy = "metaExpedient", cascade = { CascadeType.ALL })
    protected Set<MetaExpedientTascaEntity> tasques;
    
    @OneToMany(mappedBy = "metaExpedient", cascade = { CascadeType.ALL })
    protected Set<HistoricEntity> historics;
    

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "entitat_id")
    @ForeignKey(name = "ipa_entitat_metaexp_fk")
    private EntitatEntity entitatPropia;
    @Column(name = "codi", length = 64, nullable = false)
    private String codiPropi;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "organ_gestor_id")
    @ForeignKey(name = "ipa_organ_gestor_metaexp_fk")
    private OrganGestorEntity organGestor;

    @OneToMany(mappedBy = "metaExpedient", cascade = { CascadeType.ALL })
    protected Set<MetaExpedientOrganGestorEntity> metaExpedientOrganGestors;
    
    @Column(name = "gestio_amb_grups_activa", nullable = false)
    private boolean gestioAmbGrupsActiva;
    
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "ipa_metaexpedient_grup",
			joinColumns = {@JoinColumn(name = "metaexpedient_id", referencedColumnName="id")},
			inverseJoinColumns = {@JoinColumn(name = "grup_id")})
	@ForeignKey(
			name = "ipa_metaexp_metaexpgrup_fk",
			inverseName = "ipa_grup_metaexpgrup_fk")
	private List<GrupEntity> grups = new ArrayList<GrupEntity>();
	
	
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "grup_per_defecte")
    @ForeignKey(name = "ipa_grup_metaexp_fk")
    private GrupEntity grupPerDefecte;

	
	@Column(name = "revisio_estat", length = 8)
	@Enumerated(EnumType.STRING)
	private MetaExpedientRevisioEstatEnumDto revisioEstat;
	@Column(name = "revisio_comentari", length = 1024)
	private String revisioComentari; // TODO to delete in next version
	
	@OneToMany(
			mappedBy = "metaExpedient",
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	private List<MetaExpedientComentariEntity> comentaris = new ArrayList<MetaExpedientComentariEntity>();
	
	
	@Column(name = "crear_regla_dist_estat", length = 10)
	@Enumerated(EnumType.STRING)
	private CrearReglaDistribucioEstatEnumDto crearReglaDistribucioEstat;
	@Column(name = "crear_regla_dist_error", length = 1024)
	private String crearReglaDistribucioError;

	@Column(name = "organ_no_sinc", nullable = false)
	private boolean organNoSincronitzat;

	@Column(name = "interessat_obligatori", nullable = false)
    private boolean interessatObligatori;
	
	public void updateCrearReglaDistribucio(CrearReglaDistribucioEstatEnumDto crearReglaDistribucioEstat) {
		this.crearReglaDistribucioEstat = crearReglaDistribucioEstat;
	}
	public void updateCrearReglaDistribucioError(String crearReglaDistribucioError) {
		this.crearReglaDistribucioEstat = CrearReglaDistribucioEstatEnumDto.ERROR;
		this.crearReglaDistribucioError = crearReglaDistribucioError;
	}
	
	public boolean isCrearReglaDistribucio() {
		return crearReglaDistribucioEstat != null;
	}
	public void addGrup(GrupEntity grup) {
		grups.add(grup);
	}
	
	public void removeGrup(GrupEntity grup) {
		grups.remove(grup);
	}

	public void update(
			String codi,
			String nom,
			String descripcio,
			String classificacio,
			String serieDocumental,
			String expressioNumero,
			boolean notificacioActiva,
			boolean permetMetadocsGenerals,
			MetaExpedientEntity pare,
			OrganGestorEntity organGestor,
			boolean gestioAmbGrupsActiva, 
			TipusClassificacioEnumDto tipusClassificacio,
			boolean interessatObligatori,
			boolean permisDirecte) {
        super.update(codi, nom, descripcio);
        this.classificacio = classificacio;
        this.serieDocumental = serieDocumental;
        this.expressioNumero = expressioNumero;
        this.notificacioActiva = notificacioActiva;
        this.pare = pare;
        this.codiPropi = codi;
        this.permetMetadocsGenerals = permetMetadocsGenerals;
        this.organGestor = organGestor;
        this.gestioAmbGrupsActiva = gestioAmbGrupsActiva;
        this.tipusClassificacio = tipusClassificacio;
        this.interessatObligatori = interessatObligatori;
        this.permisDirecte = permisDirecte;
    }
	
	public void updateRevisioEstat(MetaExpedientRevisioEstatEnumDto revisioEstat) {
        this.revisioEstat = revisioEstat;
    }

	public void updateSync(
			String nom,
			String descripcio,
			OrganGestorEntity organGestor,
			boolean organNoSincronitzat) {
		this.nom = nom;
		this.descripcio = StringUtils.abbreviate(descripcio, 1000);
		this.organGestor = organGestor;
		this.organNoSincronitzat = organNoSincronitzat;
	}

	public static Builder getBuilder(
			String codi,
			String nom,
			String descripcio,
			String serieDocumental,
			String classificacio,
			boolean notificacioActiva,
			boolean permetMetadocsGenerals,
			EntitatEntity entitat,
			MetaExpedientEntity pare,
			OrganGestorEntity organGestor,
			boolean gestioAmbGrupsActiva,
			boolean interessatObligatori,
			boolean permisDirecte) {
		return new Builder(
				codi,
				nom,
				descripcio,
				serieDocumental,
				classificacio,
				entitat,
				pare,
				notificacioActiva,
				permetMetadocsGenerals,
				organGestor,
				gestioAmbGrupsActiva,
				interessatObligatori,
				permisDirecte);
	}

    public static class Builder {
        MetaExpedientEntity built;

		Builder(
				String codi,
				String nom,
				String descripcio,
				String serieDocumental,
				String classificacio,
				EntitatEntity entitat,
				MetaExpedientEntity pare,
				boolean notificacioActiva,
				boolean permetMetadocsGenerals,
				OrganGestorEntity organGestor,
				boolean gestioAmbGrupsActiva,
				boolean interessatObligatori,
				boolean permisDirecte) {
            built = new MetaExpedientEntity();
            built.codi = codi;
            built.nom = nom!=null?nom.trim():null;
            built.descripcio = StringUtils.abbreviate(descripcio, 1000);
            built.serieDocumental = serieDocumental;
            built.classificacio = classificacio;
            built.entitat = entitat;
            built.tipus = MetaNodeTipusEnum.EXPEDIENT;
            built.pare = pare;
            built.notificacioActiva = notificacioActiva;
            built.codiPropi = codi;
            built.entitatPropia = entitat;
            built.permetMetadocsGenerals = permetMetadocsGenerals;
            built.organGestor = organGestor;
            built.gestioAmbGrupsActiva = gestioAmbGrupsActiva;
            built.interessatObligatori = interessatObligatori;
            built.permisDirecte = permisDirecte;
        }

        public Builder expressioNumero(String expressioNumero) {
            built.expressioNumero = expressioNumero;
            return this;
        }
        
        public Builder tipusClassificacio(TipusClassificacioEnumDto tipusClassificacio) {
            built.tipusClassificacio = tipusClassificacio;
            return this;
        }

        public MetaExpedientEntity build() {
            return built;
        }
    }
    
	public boolean isComu() {
		if (organGestor == null) {
			return true;
		} else {
			return false;
		}
	}
	
    public String getCodiSiaINom() {
    	return classificacio + " - " + nom;
    }
    
	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof MetaExpedientEntity)) return false;
		if (!super.equals(o)) return false;

		MetaExpedientEntity that = (MetaExpedientEntity) o;
		if (this.getId() == null) {
			if (that.getId() != null) return false;
			else
				return Objects.equals(this.getClassificacio(), that.getClassificacio()) &&
						this.getEntitatPropia().equals(that.getEntitatPropia()) &&
						this.getCodiPropi().equals(that.getCodiPropi());
		} else
			return this.getId().equals(that.getId());
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + Objects.hashCode(classificacio);
		result = 31 * result + entitatPropia.hashCode();
		result = 31 * result + codiPropi.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "MetaExpedientEntity: [" +
				"id: " + this.getId() + ", " +
				"codi: " + this.codi + ", " +
				"nom: " + this.nom + ", " +
				"descripcio: " + this.descripcio + ", " +
				"codiSia: " + this.classificacio + ", " +
				"organGestor: " + (this.organGestor != null ? organGestor.getCodi() : "null") + ", " +
				"actiu: " + this.actiu + ", " +
				"entitat: " + (this.entitat != null ? this.entitat.getCodi() : "NULL") + "]";
	}
	

    public void setGrupPerDefecte(
			GrupEntity grupPerDefecte) {
		this.grupPerDefecte = grupPerDefecte;
	}

	public void setPermisDirecte(
			boolean permisDirecte) {
		this.permisDirecte = permisDirecte;
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
