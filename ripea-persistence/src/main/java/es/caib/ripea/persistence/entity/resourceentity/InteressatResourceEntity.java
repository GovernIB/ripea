package es.caib.ripea.persistence.entity.resourceentity;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.InteressatDocumentTipusEnumDto;
import es.caib.ripea.service.intf.dto.InteressatIdiomaEnumDto;
import es.caib.ripea.service.intf.dto.InteressatTipusEnum;
import es.caib.ripea.service.intf.model.InteressatResource;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "interessat")
@Getter
@Setter
@NoArgsConstructor
public class InteressatResourceEntity extends BaseAuditableEntity<InteressatResource> {
	
	@Column(name = "dtype", length = 40)
	@Enumerated(EnumType.STRING)
	protected InteressatTipusEnum tipus;
	
	@Column(name = "nom", length = 30)
	protected String nom;
	@Column(name = "llinatge1", length = 30)
	protected String llinatge1;
	@Column(name = "llinatge2", length = 30)
	protected String llinatge2;
	
	@Column(name = "rao_social", length = 80)
	protected String raoSocial;
	
	@Column(name = "organ_codi", length = 9)
	protected String organCodi;
	@Column(name = "organ_nom", length = 256)
	protected String organNom;
	@Column(name = "amb_oficina_sir")
	protected Boolean ambOficinaSir;
	
	@Column(name = "document_tipus", length = 40)
	@Enumerated(EnumType.STRING)
	protected InteressatDocumentTipusEnumDto documentTipus;
	@Column(name = "document_num", length = 17)
	protected String documentNum;
	@Column(name = "pais", length = 4)
	protected String pais;
	@Column(name = "provincia", length = 2)
	protected String provincia;
	@Column(name = "municipi", length = 5)
	protected String municipi;
	@Column(name = "adresa", length = 160)
	protected String adresa;
	@Column(name = "codi_postal", length = 5)
	protected String codiPostal;
	@Column(name = "email", length = 160)
	protected String email;
	@Column(name = "telefon", length = 20)
	protected String telefon;
	@Column(name = "observacions", length = 160)
	protected String observacions;
	@Column(name = "not_idioma", length = 2)
	@Enumerated(EnumType.STRING)
	protected InteressatIdiomaEnumDto preferenciaIdioma;
	@Column(name = "not_autoritzat")
	protected boolean notificacioAutoritzat;
	@Column(name = "es_representant")
	protected boolean esRepresentant;	
	@Column(name = "entrega_deh")
	protected Boolean entregaDeh;
	@Column(name = "entrega_deh_obligat")
	protected Boolean entregaDehObligat;	
	@Column(name = "incapacitat")
	protected Boolean incapacitat;	
	@Column(name = "arxiu_propagat")
	protected boolean arxiuPropagat;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "arxiu_intent_data")
	protected Date arxiuIntentData;
	@Column(name = "arxiu_reintents")
	protected int arxiuReintents;

    @Version
    private long version = 0;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "expedient_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "interessat_expedient_fk"))
	protected ExpedientResourceEntity expedient;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "representant_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "interessat_repres_fk"))
	protected InteressatResourceEntity representant;

    @OneToMany(mappedBy = "representant", cascade = {CascadeType.DETACH}, fetch = FetchType.LAZY)
    protected List<InteressatResourceEntity> representats;
    
    public boolean adressaCompleta() {
		if (Utils.isEmpty(this.getPais()) || Utils.isEmpty(this.getProvincia()) ||
			Utils.isEmpty(this.getMunicipi()) || Utils.isEmpty(this.getCodiPostal()) ||
			Utils.isEmpty(this.getAdresa())) {
				return true;
		}
		return false;
    }
}