package es.caib.ripea.service.intf.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Transient;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ContenidorFiltreOpcionsEsborratEnum;
import es.caib.ripea.service.intf.dto.ContingutTipusEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        quickFilterFields = { "nom" },
        descriptionField = "nom",
        artifacts = {
				@ResourceConfigArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = ContingutResource.PERSPECTIVE_AUDIT_CODE),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ContingutResource.ACTION_DELETE_CODE,
                        formClass = NodeResource.MassiveAction.class),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ContingutResource.ACTION_RECUPERAR_CODE,
                        formClass = NodeResource.MassiveAction.class),        
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.FILTER,
                        code = ContingutResource.FILTER_CODE,
                        formClass = ContingutResource.FilterForm.class),
        })
public class ContingutResource extends BaseAuditableResource<Long> {

    public static final String FILTER_CODE = "FILTER";
    public static final String PERSPECTIVE_PATH_CODE = "PATH";
    public static final String PERSPECTIVE_AUDIT_CODE = "AUDITORIA";
    public static final String ACTION_DELETE_CODE = "DELETE";
    public static final String ACTION_RECUPERAR_CODE = "RECUPERAR";

	@NotNull
	@Size(max = 1024)
	protected String nom;
//	@NotNull
	protected ContingutTipusEnumDto tipus;
	protected int esborrat = 0;
	protected Date esborratData;
	@Size(max = 36)
	protected String arxiuUuid;
	protected Date arxiuDataActualitzacio;
	protected Date arxiuIntentData;
	protected int arxiuReintents;
	protected int ordre;
	@Size(max = 80)
	protected String numeroRegistre;
	protected boolean arxiuPropagat;

    @Transient private boolean conteDocumentsDefinitius;
    @Transient private List<Long> treePath;
    @Transient private boolean hasDocumentsFills;
    @Transient private int numMoviments;

    @Transient private String numero;
    @Transient private Enum<?> estat;
    @Transient private String ntiVersion;
    @Transient private String ntiIdentificador;
    @Transient private String ntiOrgano;
    @Transient private Enum<?> ntiOrigen;
    @Transient private Enum<?> ntiEstadoElaboracion;
    @Transient private Date dataCaptura;
    @Transient private String ntiTipoDocumental;
    @Transient private ResourceReference<MetaNodeResource, Long> metaNode;

//	@NotNull
	protected ResourceReference<EntitatResource, Long> entitat;
	protected ResourceReference<ExpedientResource, Long> expedient;
	protected ResourceReference<ContingutResource, Long> pare;

    @Getter
    @Setter
    public static class FilterForm implements Serializable {
        private String nom;
        private ResourceReference<UsuariResource, String> createdBy;
        private ContingutTipusEnumDto tipus;
//        private ResourceReference<MetaExpedientResource, Long> metaExpedient;
//        private ResourceReference<MetaDocumentResource, Long> metaDocument;
        private Date dataEsborratInici;
        private Date dataEsborratFi;
        private ContenidorFiltreOpcionsEsborratEnum esborrat = ContenidorFiltreOpcionsEsborratEnum.NOMES_NO_ESBORRATS;
        private ResourceReference<ExpedientResource, Long> expedient;
        private Date dataInici;
        private Date dataFi;
    }
	private boolean ordrePatch;
	public Long getOrdreLong() {
		return (long)ordre;
	}
}