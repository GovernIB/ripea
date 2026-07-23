package es.caib.ripea.service.intf.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Transient;

import es.caib.ripea.service.intf.base.annotation.ResourceArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNotificacioTipusEnumDto;
import es.caib.ripea.service.intf.dto.NotificaEnviamentTipusEnumDto;
import es.caib.ripea.service.intf.dto.ServeiTipusEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(
        quickFilterFields = { "notificacioIdentificador", "enviamentReferencia", "registreNumero" },
        descriptionField = "registreNumero",
        artifacts = {
            @ResourceArtifact(
                type = ResourceArtifactType.ACTION,
                code = DocumentNotificacioResource.ACTION_ACTUALITZAR_ESTAT_CODE,
                formClass = DocumentNotificacioResource.MassiveAction.class),
            @ResourceArtifact(
                    type = ResourceArtifactType.ACTION,
                    code = DocumentNotificacioResource.ACTION_ELIMINAR,
                    requiresId = true),
			@ResourceArtifact(
					type = ResourceArtifactType.REPORT,
					code = DocumentNotificacioResource.ACTION_DESCARREGAR_JUSTIFICANT,
					formClass = DocumentNotificacioResource.MassiveAction.class),
            @ResourceArtifact(
                    type = ResourceArtifactType.REPORT,
                    code = DocumentNotificacioResource.ACTION_DESCARREGAR_DOC_ENVIAT,
                    formClass = DocumentNotificacioResource.MassiveAction.class),
            @ResourceArtifact(
                    type = ResourceArtifactType.FILTER,
                    code = DocumentNotificacioResource.FILTER_ENVIATS_NOTIB_CODE,
                    formClass = DocumentNotificacioResource.EnviatsNotibFilter.class),
        }
)
public class DocumentNotificacioResource extends DocumentEnviamentResource {

	private static final long serialVersionUID = -1924617628889102191L;
	
	public static final String ACTION_ACTUALITZAR_ESTAT_CODE	= "ACTUALITZAR_ESTAT";
    public static final String ACTION_DESCARREGAR_JUSTIFICANT	= "DESCARREGAR_JUSTIFICANT";
    public static final String ACTION_DESCARREGAR_DOC_ENVIAT	= "DESCARREGAR_DOC_ENVIAT";
    public static final String ACTION_ELIMINAR					= "DELETE_NOTIFICACIO";
    public static final String FILTER_ENVIATS_NOTIB_CODE        = "FILTER_ENVIATS_NOTIB";

    private DocumentNotificacioTipusEnumDto tipus;
    private Date dataProgramada;
    private Integer retard;
    private Date dataCaducitat;
    private String notificacioIdentificador;
    private String enviamentReferencia;
    private DocumentNotificacioEstatEnumDto notificacioEstat;
    private ServeiTipusEnumDto serveiTipusEnum;
    private boolean entregaPostal;
    private Date registreData;
    private String registreNumero;
    private String registreNumeroFormatat;
    private boolean ambRegistres;
    private Date dataEnviada;
    private Date dataFinalitzada;

    @Transient
    private boolean hasDocumentInteressats;

    private ResourceReference<OrganGestorResource, Long> emisor;

    @Transient
    private ResourceReference<MetaExpedientResource, Long> procediment;

    @Transient
    private InteressatResource destinatari;
    
    @Getter
    @Setter
    public static class MassiveAction implements Serializable {
		private static final long serialVersionUID = 2652981509368050812L;
		@NotNull
        @NotEmpty
        private List<Long> ids;
        private boolean massivo = false;
    }

    @Getter
    @Setter
    public static class EnviatsNotibFilter implements Serializable {
        private String nom;
        private NotificaEnviamentTipusEnumDto tipusEnviament;
        private String concepte;
        private DocumentNotificacioEstatEnumDto estat;
        private Date dataEnviamentInici;
        private Date dataEnviamentFi;
        private String interessat;
        private ResourceReference<ExpedientResource, Long> expedient;
        private ResourceReference<OrganGestorResource, Long> emisor;
        private ResourceReference<MetaExpedientResource, Long> procediment;
        private boolean nomesAmbError;
    }
}