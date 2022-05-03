/**
 * 
 */
package es.caib.ripea.core.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.api.dto.CrearReglaDistribucioEstatEnumDto;
import es.caib.ripea.core.api.dto.MetaExpedientRevisioEstatEnumDto;
import lombok.Getter;

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

    @Column(name = "clasif_sia", length = 30, nullable = false)
    private String classificacioSia;
    @Column(name = "serie_doc", length = 30, nullable = false)
    private String serieDocumental;
    @Column(name = "expressio_numero", length = 100)
    private String expressioNumero;
    @Column(name = "not_activa", nullable = false)
    private boolean notificacioActiva;

    @Column(name = "PERMET_METADOCS_GENERALS", nullable = false)
    private boolean permetMetadocsGenerals;

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
    
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "ipa_metaexpedient_grup",
			joinColumns = {@JoinColumn(name = "metaexpedient_id", referencedColumnName="id")},
			inverseJoinColumns = {@JoinColumn(name = "grup_id")})
	@ForeignKey(
			name = "ipa_metaexp_metaexpgrup_fk",
			inverseName = "ipa_grup_metaexpgrup_fk")
	private List<GrupEntity> grups = new ArrayList<GrupEntity>();
	

	
	@Column(name = "revisio_estat", length = 8)
	@Enumerated(EnumType.STRING)
	private MetaExpedientRevisioEstatEnumDto revisioEstat;
	@Column(name = "revisio_comentari", length = 1024)
	private String revisioComentari;
	
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
			String classificacioSia,
			String serieDocumental,
			String expressioNumero,
			boolean notificacioActiva,
			boolean permetMetadocsGenerals,
			MetaExpedientEntity pare,
			OrganGestorEntity organGestor,
			boolean gestioAmbGrupsActiva) {
        super.update(codi, nom, descripcio);
        this.classificacioSia = classificacioSia;
        this.serieDocumental = serieDocumental;
        this.expressioNumero = expressioNumero;
        this.notificacioActiva = notificacioActiva;
        this.pare = pare;
        this.codiPropi = codi;
        this.permetMetadocsGenerals = permetMetadocsGenerals;
        this.organGestor = organGestor;
        this.gestioAmbGrupsActiva = gestioAmbGrupsActiva;

    }
	
	public void updateRevisioEstat(
			MetaExpedientRevisioEstatEnumDto revisioEstat,
			String revisioComentari) {
        this.revisioEstat = revisioEstat;
        this.revisioComentari = revisioComentari;
    }

	public static Builder getBuilder(
			String codi,
			String nom,
			String descripcio,
			String serieDocumental,
			String classificacioSia,
			boolean notificacioActiva,
			boolean permetMetadocsGenerals,
			EntitatEntity entitat,
			MetaExpedientEntity pare,
			OrganGestorEntity organGestor,
			boolean gestioAmbGrupsActiva) {
		return new Builder(
				codi,
				nom,
				descripcio,
				serieDocumental,
				classificacioSia,
				entitat,
				pare,
				notificacioActiva,
				permetMetadocsGenerals,
				organGestor,
				gestioAmbGrupsActiva
				);
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
				boolean notificacioActiva,
				boolean permetMetadocsGenerals,
				OrganGestorEntity organGestor,
				boolean gestioAmbGrupsActiva) {
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
            built.codiPropi = codi;
            built.entitatPropia = entitat;
            built.permetMetadocsGenerals = permetMetadocsGenerals;
            built.organGestor = organGestor;
            built.gestioAmbGrupsActiva = gestioAmbGrupsActiva;
        }

        public Builder expressioNumero(String expressioNumero) {
            built.expressioNumero = expressioNumero;
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
    
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CarpetaEntity other = (CarpetaEntity) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}
	

    private static final long serialVersionUID = -2299453443943600172L;

}
