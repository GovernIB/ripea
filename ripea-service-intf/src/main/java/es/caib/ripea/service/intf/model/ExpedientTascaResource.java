package es.caib.ripea.service.intf.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Transient;

import es.caib.ripea.service.intf.base.annotation.ResourceArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
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

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        quickFilterFields = { "titol", "observacions", "metaExpedientTasca.nom"},
        descriptionField = "titol",
        artifacts = {
                @ResourceArtifact(
                        type = ResourceArtifactType.FILTER,
                        code = ExpedientTascaResource.FILTER_CODE,
                        formClass = ExpedientTascaResource.TascaFilterForm.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = ExpedientTascaResource.PERSPECTIVE_RESPONSABLES_CODE),
				@ResourceArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = ExpedientTascaResource.PERSPECTIVE_AUDIT_CODE),
                @ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = ExpedientTascaResource.PERSPECTIVE_CONTEXT_USUARI_CODE),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientTascaResource.ACTION_REABRIR_CODE,
                        formClass = ExpedientTascaResource.ReobrirFormAction.class,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientTascaResource.ACTION_CHANGE_ESTAT_CODE,
                        formClass = ExpedientTascaResource.ChangeEstatFormAction.class,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientTascaResource.ACTION_CHANGE_PRIORITAT_CODE,
                        formClass = ExpedientTascaResource.ChangePrioritatFormAction.class,
                        requiresId = true), 
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientTascaResource.ACTION_REASSIGNAR_CODE,
                        formClass = ExpedientTascaResource.ReassignarTascaFormAction.class,
                        requiresId = true), 
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientTascaResource.ACTION_DELEGAR_CODE,
                        formClass = ExpedientTascaResource.DelegarTascaFormAction.class,
                        requiresId = true),                 
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientTascaResource.ACTION_CHANGE_DATALIMIT_CODE,
                        formClass = ExpedientTascaResource.ChangeDataLimitFormAction.class,
                        requiresId = true),                
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientTascaResource.ACTION_REBUTJAR_CODE,
                        formClass = ExpedientTascaResource.MotiuFormAction.class,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientTascaResource.ACTION_RETOMAR_CODE,
                        formClass = ExpedientTascaResource.MotiuFormAction.class,
                        requiresId = true),
        })
public class ExpedientTascaResource extends BaseAuditableResource<Long> {

    public static final String PERSPECTIVE_RESPONSABLES_CODE = "RESPONSABLES_RESUM";
    public static final String PERSPECTIVE_AUDIT_CODE = "AUDITORIA";
    public static final String PERSPECTIVE_CONTEXT_USUARI_CODE = "CONTEXT_USUARI";
    public static final String ACTION_CHANGE_ESTAT_CODE = "CHANGE_ESTAT";
    public static final String ACTION_CHANGE_PRIORITAT_CODE = "CHANGE_PRIORITAT";
    public static final String ACTION_CHANGE_DATALIMIT_CODE = "CHANGE_DATALIMIT";
    public static final String ACTION_REASSIGNAR_CODE = "REASSIGNAR";
    public static final String ACTION_DELEGAR_CODE = "DELEGAR";
    public static final String ACTION_REABRIR_CODE = "REABRIR";
    public static final String ACTION_REBUTJAR_CODE = "REBUTJAR";
    public static final String ACTION_RETOMAR_CODE = "RETOMAR";

    public static final String FILTER_CODE = "TASCA_FILTER";

//    @NotNull
    private Date dataInici;
    private Date dataFi;
    @NotNull
    private TascaEstatEnumDto estat = TascaEstatEnumDto.PENDENT;
    private String motiuRebuig;
    
    @ResourceField(onChangeActive = true)
    private Date dataLimit;
    @Transient private boolean dataLimitExpirada;
    @Transient private boolean shouldNotifyAboutDeadline;
    
    private String comentari;
    private long numComentaris;

    private boolean usuariActualResponsable; //L'usuari actual es dins la llista de responsables
    private boolean usuariActualObservador;  //L'usuari actual es dins la llista de observadors
    private boolean usuariActualDelegat; //L'usuari actual es el delegat
    private boolean usuariActualOnlyObservador; //L'usuari actual es observador, pero no responsable ni delegat
    private boolean agafadaUsuariActual; //L'usuari actual es el responsable actual de la tasca

    private String titol;
    private String observacions;
    @ResourceField(onChangeActive = true)
    private Integer duracio;
    @NotNull
    private PrioritatEnumDto prioritat = PrioritatEnumDto.B_NORMAL;
    private String responsablesActualStr;
    private String responsablesStr;
    private String observadorsStr;

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
    @NotNull
    @NotEmpty
    private List<ResourceReference<UsuariResource, String>> responsables = new ArrayList<>();
    
    @Getter
    @Setter
    public static class ChangeEstatFormAction implements Serializable {
        @NotNull
        private TascaEstatEnumDto estat;
    }
    
    @Getter
    @Setter
    public static class TascaFilterForm implements Serializable {
        private ResourceReference<MetaExpedientResource, Long> metaExpedient;
        private ResourceReference<ExpedientResource, Long> expedient;
        private ResourceReference<MetaExpedientTascaResource, Long> metaExpedientTasca;
        private String titol;
        private PrioritatEnumDto prioritat;
        private Date dataInici;
        private Date dataFi;
        private Date dataLimitInici;
        private Date dataLimitFi;
        private TascaEstatEnumDto estat;
        private List<TascaEstatEnumDto> estats = new ArrayList<>(
                List.of(TascaEstatEnumDto.PENDENT, TascaEstatEnumDto.INICIADA, TascaEstatEnumDto.AGAFADA));
        private ResourceReference<UsuariResource, String> responsable;
    }

    @Getter
    @Setter
    public static class ChangePrioritatFormAction implements Serializable {
        private String titol;
        private ResourceReference<MetaExpedientTascaResource, Long> metaExpedientTasca;
        @NotNull
        private PrioritatEnumDto prioritat;
    }
    
    @Getter
    @Setter
    @FieldNameConstants
    public static class ChangeDataLimitFormAction implements Serializable {
        @NotNull
    	private Date dataInici;
    	@NotNull
        @ResourceField(onChangeActive = true)
    	private Integer duracio;
    	@NotNull
        @ResourceField(onChangeActive = true)
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