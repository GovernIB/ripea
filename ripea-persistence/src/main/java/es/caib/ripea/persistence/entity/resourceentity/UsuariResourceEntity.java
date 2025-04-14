package es.caib.ripea.persistence.entity.resourceentity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import es.caib.ripea.persistence.base.entity.ResourceEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.ContingutVistaEnumDto;
import es.caib.ripea.service.intf.dto.MoureDestiVistaEnumDto;
import es.caib.ripea.service.intf.model.UsuariResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "usuari")
@Getter
@Setter
@NoArgsConstructor
public class UsuariResourceEntity implements ResourceEntity<UsuariResource, String> {

	@Id
	@Column(name = "codi", length = 64, nullable = false)
	private String codi;
	@Column(name = "nom", length = 200)
	private String nom;
	@Column(name = "nif", length = 9, nullable = false)
	private String nif;
	@Column(name = "email", length = 200)
	private String email;
	@Column(name = "email_alternatiu", length = 200)
	private String emailAlternatiu;
	@Column(name="idioma", length = 2)
	private String idioma;
	@Column(name = "inicialitzat")
	private boolean inicialitzat = false;
//	@ManyToMany(
//			cascade = CascadeType.ALL,
//			fetch = FetchType.EAGER)
//	@JoinTable(
//			name = BaseConfig.DB_PREFIX + "usuari_viafirma_ripea",
//			joinColumns = {@JoinColumn(name = "ripea_user_codi")},
//			inverseJoinColumns = {@JoinColumn(name = "viafirma_user_codi")})
//	private Set<ViaFirmaUsuariEntity> viaFirmaUsuaris = new HashSet<ViaFirmaUsuariEntity>();

	@Column(name="rol_actual", length = 64)
	private String rolActual;

	@Column(name="vista_actual", length = 64)
	@Enumerated(EnumType.STRING)
	private ContingutVistaEnumDto vistaActual = ContingutVistaEnumDto.TREETABLE_PER_CARPETA;

	@Column(name="num_elements_pagina")
	private Long numElementsPagina;

	@Column(name = "emails_agrupats")
	private boolean rebreEmailsAgrupats = true;

	@Column(name = "avisos_noves_anotacions")
	private boolean rebreAvisosNovesAnotacions;

	@Column(name = "emails_canvi_estat_revisio")
	private boolean rebreEmailsCanviEstatRevisio = true;

	@Column(name = "exp_list_data_darrer_env")
	private boolean expedientListDataDarrerEnviament = false;
	@Column(name = "exp_list_agafat_per")
	private boolean expedientListAgafatPer = true;
	@Column(name = "exp_list_interessats")
	private boolean expedientListInteressats = true;
	@Column(name = "exp_list_comentaris")
	private boolean expedientListComentaris = true;
	@Column(name = "exp_list_grup")
	private boolean expedientListGrup = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "metaexpedient_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "metaexp_usuari_fk"))
	private MetaExpedientResourceEntity procediment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "entitat_defecte_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "entitat_usuari_fk"))
	private EntitatResourceEntity entitatPerDefecte;

	@Column(name = "expedient_expandit")
	private boolean expedientExpandit = true;

	@Column(name="vista_moure_actual", length = 16)
	@Enumerated(EnumType.STRING)
	private MoureDestiVistaEnumDto vistaMoureActual = MoureDestiVistaEnumDto.LLISTA;

    @Column(name = "codi", insertable = false, updatable = false)
    private String id;
	@Override
	public String getId() {
		return this.codi;
	}

	@Override
	public boolean isNew() {
		return this.codi == null;
	}

	@Transient
	private String codiAndNom;
	public String getCodiAndNom() {
		return codi + " - " + nom;
	}

}
