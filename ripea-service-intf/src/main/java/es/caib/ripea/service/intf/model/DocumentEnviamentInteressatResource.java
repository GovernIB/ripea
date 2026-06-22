package es.caib.ripea.service.intf.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Transient;

import es.caib.ripea.service.intf.base.annotation.ResourceArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.Resource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.EnviamentEstatEnumDto;
import es.caib.ripea.service.intf.dto.RegistreEstatEnumDto;
import es.caib.ripea.service.intf.resourcevalidation.InteressatValid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@InteressatValid(groups = {Resource.OnCreate.class, Resource.OnUpdate.class})
@ResourceConfig(
        quickFilterFields = { "nom" },
        descriptionField = "nom",
        artifacts = {
                @ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = DocumentEnviamentInteressatResource.PERSPECTIVE_DETAIL_CODE),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = DocumentEnviamentInteressatResource.ACTION_AMPLIAR_PLAC_CODE,
                        formClass = DocumentEnviamentInteressatResource.AmpliarPalacFormAction.class,
                        requiresId = true),
    			@ResourceArtifact(
    					type = ResourceArtifactType.REPORT,
    					code = DocumentEnviamentInteressatResource.REPORT_DESCARREGAR_CERTIFICAT,
    					formClass = DocumentEnviamentInteressatResource.MassiveAction.class),                
        }
)
public class DocumentEnviamentInteressatResource extends BaseAuditableResource<Long> {

    public static final String PERSPECTIVE_DETAIL_CODE = "DETAIL";
    public static final String ACTION_AMPLIAR_PLAC_CODE = "AMPLIAR_PLAC";
    public static final String REPORT_DESCARREGAR_CERTIFICAT = "DESCARREGAR_CERTIFICAT";

    private String enviamentReferencia;
    private EnviamentEstatEnumDto enviamentDatatEstat;
    private Date enviamentDatatData;
    private String enviamentDatatOrigen;
    private Date enviamentCertificacioData;
    private String enviamentCertificacioOrigen;
    protected Boolean error;
    protected String errorDescripcio;
    private Date registreData;
    private Integer registreNumero;
    private String registreNumeroFormatat;
    private RegistreEstatEnumDto registreEstat;
    private boolean finalitzat;

    private ResourceReference<InteressatResource, Long> interessat;
    private ResourceReference<DocumentNotificacioResource, Long> notificacio;

    @Transient private InteressatResource interessatInfo;
    @Transient private InteressatResource representantInfo;
    @Transient private DocumentNotificacioResource notificacioInfo;
    @Transient private String entregaNif;
    @Transient private String classificacio;

    @Getter
    @Setter
    public static class AmpliarPalacFormAction implements Serializable {
        @NotNull
        private Integer diesAmpliacio;
        private String motiu;
    }
    
    @Getter
    @Setter
    public static class MassiveAction implements Serializable {
		private static final long serialVersionUID = 8132919073092963463L;
		@NotNull
        @NotEmpty
        private List<Long> ids;
        private boolean massivo = false;
    }
}