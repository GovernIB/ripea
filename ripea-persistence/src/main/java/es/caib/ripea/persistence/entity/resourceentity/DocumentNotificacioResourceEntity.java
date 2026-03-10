package es.caib.ripea.persistence.entity.resourceentity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNotificacioTipusEnumDto;
import es.caib.ripea.service.intf.dto.ServeiTipusEnumDto;
import es.caib.ripea.service.intf.model.DocumentNotificacioResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("DocumentNotificacioEntity")
public class DocumentNotificacioResourceEntity extends DocumentEnviamentResourceEntity<DocumentNotificacioResource> {

    @Column(name = "not_tipus")
    private DocumentNotificacioTipusEnumDto tipus;
    @Column(name = "not_data_prog")
    @Temporal(TemporalType.DATE)
    private Date dataProgramada;
    @Column(name = "not_retard")
    private Integer retard;
    @Column(name = "not_data_caducitat")
    @Temporal(TemporalType.DATE)
    private Date dataCaducitat;
    @Column(name = "not_env_id", length = 100)
    private String notificacioIdentificador;
    @Column(name = "not_env_cert_arxiuid", length = 50)
    private String enviamentCertificacioArxiuId;
    @Enumerated(EnumType.STRING)
    @Column(name = "servei_tipus", length = 10)
    private ServeiTipusEnumDto serveiTipusEnum;
    @Column(name="entrega_postal")
    private Boolean entregaPostal;
    @Column(name="not_env_registre_data")
    @Temporal(TemporalType.TIMESTAMP)
    private Date registreData;
    @Column(name="not_env_registre_numero", length = 19)
    private Integer registreNumero;
    @Column(name="not_env_registre_num_formatat", length = 50)
    private String registreNumeroFormatat;
    @ManyToOne(optional = true)
    @JoinColumn(name = "not_emisor_id")
    private OrganGestorResourceEntity emisor;
    @Column(name = "not_data_enviada")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataEnviada;
    @Column(name = "not_data_finalitzada")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataFinalitzada;
    @Column(name = "notificacio_estat")
    @Enumerated(EnumType.STRING)
    protected DocumentNotificacioEstatEnumDto notificacioEstat;

    @OneToMany(
            mappedBy = "notificacio",
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    protected Set<DocumentEnviamentInteressatResourceEntity> documentInteressats = new HashSet<>();

    
	public boolean isNotificacioFinalitzada() {
		List<DocumentNotificacioEstatEnumDto> estatsFinals = new ArrayList<DocumentNotificacioEstatEnumDto>(Arrays.asList(
				DocumentNotificacioEstatEnumDto.FINALITZADA, 
				DocumentNotificacioEstatEnumDto.PROCESSADA));
		return estatsFinals.contains(this.getNotificacioEstat());
	}
	
	public boolean isCaducada() {
		return (this.getDataCaducitat()!=null && this.getDataCaducitat().before(Calendar.getInstance().getTime()));
	}
}