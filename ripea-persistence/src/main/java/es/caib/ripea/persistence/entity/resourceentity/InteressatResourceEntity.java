package es.caib.ripea.persistence.entity.resourceentity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.EntregaPostalTipusEnum;
import es.caib.ripea.service.intf.dto.EntregaPostalViaTipusEnum;
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
	@Column(name = "document_num", length = 36)
	protected String documentNum;
	@Column(name = "pais", length = 4)
	protected String pais;
	@Column(name = "provincia", length = 2)
	protected String provincia;
	@Column(name = "municipi", length = 5)
	protected String municipi;
	@Column(name = "codi_postal", length = 5)
	protected String codiPostal;
	// ADRESSA NORMALITZADA //
	@Column(name = "ADRESSA_TIPUS", length = 50)
	@Enumerated(EnumType.STRING)
	protected EntregaPostalTipusEnum adressaTipus = EntregaPostalTipusEnum.NACIONAL;
	@Column(name = "ADRESSA_TIPUS_VIA", length = 15)
	@Enumerated(EnumType.STRING)
	protected EntregaPostalViaTipusEnum adressaTipusVia = EntregaPostalViaTipusEnum.CALLE;	
	@Column(name = "ADRESA", length = 250)
	protected String adresa;
	@Column(name = "ADRESSA_NUM_CASA", length = 5)
	protected String adressaNumCasa;
	@Column(name = "ADRESSA_QUAL", length = 3)
	protected String adresaQualificador;
	@Column(name = "ADRESSA_PUNT_KM", length = 10)
	protected String adresaPuntKm;
	@Column(name = "ADRESSA_AP_CORREUS", length = 10)
	protected String adresaApartatCorreus;
	@Column(name = "ADRESSA_PORTAL", length = 50)
	protected String adresaPortal;
	@Column(name = "ADRESSA_ESCALA", length = 50)
	protected String adresaEscala;
	@Column(name = "ADRESSA_PLANTA", length = 50)
	protected String adresaPlanta;
	@Column(name = "ADRESSA_PORTA", length = 50)
	protected String adresaPorta;
	@Column(name = "ADRESSA_BLOC", length = 50)
	protected String adresaBloc;
	@Column(name = "ADRESSA_COMPLEMENT", length = 250)
	protected String adresaComplement;
	@Column(name = "ADRESSA_POBLACIO", length = 255)
	protected String adresaPoblacio;
	// FI ADRESSA NORMALITZADA //	
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
    
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = BaseConfig.DB_PREFIX + "interessat_grup_rel",
            joinColumns = @JoinColumn(name = "interessat_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "grup_id", referencedColumnName = "id"),
            foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "inter_gruprel_inter_fk"),
            inverseForeignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "inter_gruprel_grup_fk")
    )
    protected List<InteressatGrupResourceEntity> grups = new ArrayList<>();
	
	public String getCodiNom() {
		return Utils.getCodiNom(this.tipus, this.documentNum, this.nom, this.llinatge1, this.llinatge2, this.raoSocial, this.organNom);
    }

	public String getNomComplet() {
		return Utils.getNomComplet(this.tipus,this.nom, this.llinatge1, this.llinatge2, this.raoSocial, this.organNom);
	}
    
    public boolean adressaCompleta() {
		if (Utils.isEmpty(this.getPais()) || Utils.isEmpty(this.getProvincia()) ||
			Utils.isEmpty(this.getMunicipi()) || Utils.isEmpty(this.getCodiPostal()) ||
			Utils.isEmpty(this.getAdresa())) {
				return false;
		}
		return true;
    }
    
    public boolean adressaNormalitzadaCompleta() {
    	if(this.getAdressaTipus()!=null && !this.getAdressaTipus().equals(EntregaPostalTipusEnum.SENSE_NORMALITZAR) && Utils.isEmpty(this.getAdresaPoblacio())) {
    		return false;
    	}
    	return true;
    }
    
    public List<InteressatGrupResourceEntity> getGrups() {
    	return this.grups;
    }
}