package es.caib.ripea.service.intf.model;

import java.util.Date;

import org.springframework.data.annotation.Transient;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ExpedientPeticioAccioEnumDto;
import es.caib.ripea.service.intf.dto.ExpedientPeticioEstatEnumDto;
import es.caib.ripea.service.intf.dto.ExpedientPeticioEstatViewEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(
        quickFilterFields = { "identificador" },
        descriptionField = "identificador",
        artifacts = {
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = ExpedientPeticioResource.PERSPECTIVE_REGISTRE_CODE),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = ExpedientPeticioResource.PERSPECTIVE_ESTAT_VIEW_CODE),             
        }
)
public class ExpedientPeticioResource extends BaseAuditableResource<Long> {

    public static final String PERSPECTIVE_REGISTRE_CODE = "REGISTRE";
    public static final String PERSPECTIVE_ESTAT_VIEW_CODE = "ESTAT_VIEW";

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

    @Transient private RegistreResource registreInfo;
    @Transient private ExpedientPeticioEstatViewEnumDto estatView;
}