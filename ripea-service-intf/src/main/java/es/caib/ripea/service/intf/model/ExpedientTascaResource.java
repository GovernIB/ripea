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
                        code = ExpedientTascaResource.ACTION_CHANGE_PRIORITAT_CODE,
                        formClass = ExpedientTascaResource.ChangePrioritatFormAction.class,
                        requiresId = true), 
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientTascaResource.ACTION_REASSIGNAR_CODE,
                        formClass = ExpedientTascaResource.ReassignarTascaFormAction.class,
                        requiresId = true), 
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientTascaResource.ACTION_DELEGAR_CODE,
                        formClass = ExpedientTascaResource.DelegarTascaFormAction.class,
                        requiresId = true),                 
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientTascaResource.ACTION_CHANGE_DATALIMIT_CODE,
                        formClass = ExpedientTascaResource.ChangeDataLimitFormAction.class,
                        requiresId = true),                
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientTascaResource.ACTION_REBUTJAR_CODE,
                        formClass = ExpedientTascaResource.MotiuFormAction.class,
                        requiresId = true),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientTascaResource.ACTION_RETOMAR_CODE,
                        formClass = ExpedientTascaResource.MotiuFormAction.class,
                        requiresId = true),
        })
public class ExpedientTascaResource extends BaseAuditableResource<Long> {

    public static final String PERSPECTIVE_RESPONSABLES_CODE = "RESPONSABLES_RESUM";
    public static final String ACTION_CHANGE_ESTAT_CODE = "CHANGE_ESTAT";
    public static final String ACTION_CHANGE_PRIORITAT_CODE = "CHANGE_PRIORITAT";
    public static final String ACTION_CHANGE_DATALIMIT_CODE = "CHANGE_DATALIMIT";
    public static final String ACTION_REASSIGNAR_CODE = "REASSIGNAR";
    public static final String ACTION_DELEGAR_CODE = "DELEGAR";
    public static final String ACTION_REABRIR_CODE = "REABRIR";
    public static final String ACTION_REBUTJAR_CODE = "REBUTJAR";
    public static final String ACTION_RETOMAR_CODE = "RETOMAR";

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
    private ResourceReference<UsuariResource, String> responsableActual;
    private ResourceReference<UsuariResource, String> delegat;
    private List<ResourceReference<UsuariResource, String>> observadors = new ArrayList<>();
    private List<ResourceReference<UsuariResource, String>> responsables = new ArrayList<>();

    @Getter
    @Setter
    public static class ChangeEstatFormAction implements Serializable {
        @NotNull
        private TascaEstatEnumDto estat;
    }
    
    @Getter
    @Setter
    public static class ChangePrioritatFormAction implements Serializable {
        @NotNull
        private PrioritatEnumDto prioritat;
    }
    
    @Getter
    @Setter
    public static class ChangeDataLimitFormAction implements Serializable {
    	@NotNull
    	private Integer duracio;
    	@NotNull
    	private Date dataLimit;
    }

    @Getter
    @Setter
    public static class ReobrirFormAction extends MotiuFormAction {
        @NotNull
        private List<ResourceReference<UsuariResource, String>> responsables;
    }
    
    @Getter
    @Setter
    public static class DelegarTascaFormAction extends MotiuFormAction {
        @NotNull
        private ResourceReference<UsuariResource, String> usuari;
    }
    
    @Getter
    @Setter
    public static class ReassignarTascaFormAction implements Serializable {
        @NotNull
        private List<ResourceReference<UsuariResource, String>> usuaris;
    }

    @Getter
    @Setter
    public static class MotiuFormAction implements Serializable {
        private String motiu;
    }
}