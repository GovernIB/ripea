package es.caib.ripea.service.intf.model;

import java.io.Serializable;
import java.time.Year;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import es.caib.ripea.service.intf.base.annotation.ResourceField;
import org.springframework.data.annotation.Transient;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ExpedientPeticioAccioEnumDto;
import es.caib.ripea.service.intf.dto.ExpedientPeticioEstatEnumDto;
import es.caib.ripea.service.intf.dto.ExpedientPeticioEstatViewEnumDto;
import es.caib.ripea.service.intf.dto.PrioritatEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        quickFilterFields = { "identificador" },
        descriptionField = "identificador",
        artifacts = {
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.FILTER,
                        code = ExpedientPeticioResource.FILTER_CODE,
                        formClass = ExpedientPeticioResource.FilterForm.class),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = ExpedientPeticioResource.PERSPECTIVE_REGISTRE_CODE),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = ExpedientPeticioResource.PERSPECTIVE_ESTAT_VIEW_CODE),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.REPORT,
                        code = ExpedientPeticioResource.REPORT_DOWNLOAD_JUSTIFICANT,
                        requiresId = true),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientPeticioResource.ACTION_REBUTJAR_ANOTACIO,
                        formClass = ExpedientPeticioResource.RebutjarAnotacioForm.class,
                        requiresId = true),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientPeticioResource.ACTION_ACCEPTAR_ANOTACIO,
                        formClass = ExpedientPeticioResource.AcceptarAnotacioForm.class,
                        requiresId = true),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExpedientPeticioResource.ACTION_ESTAT_DISTRIBUCIO,
                        requiresId = true),                 
        }
)
public class ExpedientPeticioResource extends BaseAuditableResource<Long> {

    public static final String FILTER_CODE = "ANOTACIO_FILTER";

    public static final String PERSPECTIVE_REGISTRE_CODE = "REGISTRE";
    public static final String PERSPECTIVE_ESTAT_VIEW_CODE = "ESTAT_VIEW";
    public static final String REPORT_DOWNLOAD_JUSTIFICANT = "DOWNLOAD_JUSTIFICANT";
    public static final String ACTION_REBUTJAR_ANOTACIO = "REBUTJAR_ANOTACIO";
    public static final String ACTION_ACCEPTAR_ANOTACIO = "ACCEPTAR_ANOTACIO";
    public static final String ACTION_ESTAT_DISTRIBUCIO = "ESTAT_DISTRIBUCIO";

//    private Long id;
    private String identificador;
    private String clauAcces;
    private Date dataAlta;
    private ExpedientPeticioEstatEnumDto estat;
    private ExpedientPeticioAccioEnumDto accio;
    private String notificaDistError;
    private Date dataActualitzacio;
    private ResourceReference<UsuariResource, String> usuariActualitzacio;
    private String observacions;

    private ResourceReference<RegistreResource, Long> registre;
    private ResourceReference<MetaExpedientResource, Long> metaExpedient;
    private ResourceReference<GrupResource, Long> grup;
    private ResourceReference<ExpedientResource, Long> expedient;
    
    private boolean pendentCanviEstatDistribucio;
    private int reintentsCanviEstatDistribucio;

    @Transient private RegistreResource registreInfo;
    @Transient private ExpedientPeticioEstatViewEnumDto estatView;

    @Getter
    @Setter
    @NoArgsConstructor
    @FieldNameConstants
    public static class FilterForm implements Serializable {
        private String numRegistre;
        private String extracte;
        private String destinacio;
        private ResourceReference<MetaExpedientResource, Long> metaExpedient;
        private Date dataRecepcioInicial;
        private Date dataRecepcioFinal;
        private ExpedientPeticioEstatViewEnumDto estat = ExpedientPeticioEstatViewEnumDto.PENDENT;
        private String interessat;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @FieldNameConstants
    public static class AcceptarAnotacioForm implements Serializable {
        @NotNull
    	private ExpedientPeticioAccioEnumDto accio = ExpedientPeticioAccioEnumDto.CREAR;
        @NotNull @ResourceField(onChangeActive = true)
    	private ResourceReference<MetaExpedientResource, Long> metaExpedient;
    	private ResourceReference<ExpedientResource, Long> expedient;
    	private String newExpedientTitol;
    	private PrioritatEnumDto prioritat = PrioritatEnumDto.B_NORMAL;
    	private String prioritatMotiu;
        @ResourceField(onChangeActive = true)
    	private Integer any = Year.now().getValue();
    	private Long sequencia;
    	private boolean associarInteressats = true;
    	private boolean agafarExpedient = true;
    	private ResourceReference<OrganGestorResource, Long> organGestor;
    	
    	private List<Long> interessats;
        private Map<Long, String> annexos = new HashMap<>();
        @Transient @ResourceField(enumType = true)
        private String tipusDocument;
    }
    
    @Getter
    @Setter
    public static class RebutjarAnotacioForm implements Serializable {
    	@NotNull
    	private String motiu;
    }
}