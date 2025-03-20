package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceField;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.PrioritatEnumDto;
import es.caib.ripea.service.intf.dto.TascaEstatEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        quickFilterFields = { "id, titol" },
        descriptionField = "titol",
        artifacts = {
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = ExpedientTascaResource.PERSPECTIVE_RESPONSABLES_CODE),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientTascaResource.ACTION_REABRIR_CODE,
                        formClass = ExpedientTascaResource.ReobrirFormAction.class,
                        requiresId = true),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientTascaResource.ACTION_CHANGE_ESTAT_CODE,
                        formClass = ExpedientTascaResource.ChangeEstatFormAction.class,
                        requiresId = true),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientTascaResource.ACTION_REBUTJAR_CODE,
                        formClass = ExpedientTascaResource.RebutjarFormAction.class,
                        requiresId = true),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientTascaResource.ACTION_RETOMAR_CODE,
                        formClass = ExpedientTascaResource.RetomarFormAction.class,
                        requiresId = true),
        })
public class ExpedientTascaResource extends BaseAuditableResource<Long> {

    public static final String PERSPECTIVE_RESPONSABLES_CODE = "RESPONSABLES_RESUM";
    public static final String ACTION_CHANGE_ESTAT_CODE = "ACTION_CHANGE_ESTAT";
    public static final String ACTION_REABRIR_CODE = "ACTION_REABRIR";
    public static final String ACTION_REBUTJAR_CODE = "ACTION_REBUTJAR";
    public static final String ACTION_RETOMAR_CODE = "ACTION_RETOMAR";

//    @NotNull
    private Date dataInici;
    private Date dataFi;
    @NotNull
    private TascaEstatEnumDto estat = TascaEstatEnumDto.PENDENT;
    private String motiuRebuig;
    @ResourceField(onChangeActive = true)
    private Date dataLimit;
    private boolean shouldNotifyAboutDeadline;
    private String comentari;
    private long numComentaris;

    private boolean usuariActualResponsable;
    private boolean usuariActualObservador;
    private boolean usuariActualDelegat;

    private String titol;
    private String observacions;
    @ResourceField(onChangeActive = true)
    private Integer duracio;
    @NotNull
    private PrioritatEnumDto prioritat = PrioritatEnumDto.B_NORMAL;
    private String responsablesStr;

    @Transient
    private String metaExpedientTascaDescription;

    @NotNull
    private ResourceReference<ExpedientResource, Long> expedient;
    @NotNull
    @ResourceField(onChangeActive = true)
    private ResourceReference<MetaExpedientTascaResource, Long> metaExpedientTasca;
    @NotNull
    private ResourceReference<UsuariResource, String> responsableActual;
    private ResourceReference<UsuariResource, String> delegat;

    private List<ResourceReference<UsuariResource, String>> observadors = new ArrayList<>();

    @Getter
    @Setter
    public static class ChangeEstatFormAction implements Serializable {
        @NotNull
        private TascaEstatEnumDto estat;
    }

    @Getter
    @Setter
    public static class ReobrirFormAction implements Serializable {
        @NotNull
        private ResourceReference<UsuariResource, String> responsableActual;
        private String motiu;
    }

    @Getter
    @Setter
    public static class RetomarFormAction implements Serializable {
        private String motiu;
    }

    @Getter
    @Setter
    public static class RebutjarFormAction implements Serializable {
        @NotNull
        private TascaEstatEnumDto estat;
        private String motiuRebuig;
    }
}
