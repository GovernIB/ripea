package es.caib.ripea.service.intf.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Transient;

import es.caib.ripea.service.intf.base.annotation.ResourceArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.MetaExpedientRevisioEstatEnumDto;
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
		quickFilterFields = { "codi", "nom" },
		descriptionField = "nom",
		artifacts = {
				@ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = MetaExpedientTascaResource.PERSPECTIVE_COUNT_VALIDACIONS),
				@ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = MetaExpedientTascaResource.PERSPECTIVE_REVISIO_ESTAT),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = MetaExpedientTascaResource.ACTION_REORDENAR_CODE,
                        formClass = Integer.class,
                        requiresId = true),
        })
public class MetaExpedientTascaResource extends BaseAuditableResource<Long> {

	public static final String PERSPECTIVE_COUNT_VALIDACIONS = "COUNT_VALIDACIONS";
	public static final String PERSPECTIVE_REVISIO_ESTAT = "REVISIO_ESTAT";
    public static final String ACTION_REORDENAR_CODE = "REORDENAR";

    @NotNull private String codi;
    @NotNull private String nom;
    @NotNull private int ordre;
    @NotNull private String descripcio;
    private ResourceReference<UsuariResource, String> responsable;
    private boolean activa = true;
    private Date dataLimit;
    @SuppressWarnings("unused")
    private String dataLimitString;
    private Integer duracio = 10;
    @SuppressWarnings("unused")
    private String duracioFormat;
    private PrioritatEnumDto prioritat = PrioritatEnumDto.B_NORMAL;

    private ResourceReference<MetaExpedientEstatResource, Long> estatCrearTasca;
    private ResourceReference<MetaExpedientEstatResource, Long> estatFinalitzarTasca;
    private ResourceReference<MetaExpedientResource, Long> metaExpedient;

    @Transient private int numValidacio;
    @Transient private String estatColorCrearTasca;
    @Transient private String estatColorFinalitzarTasca;
    @Transient private MetaExpedientRevisioEstatEnumDto metaExpedientRevisioEstat;

    //Nomes per importacio de procediments
    private List<MetaExpedientTascaValidacioResource> validacionsImportacio = new ArrayList<>();

    @Transient private boolean importar = true;
}
