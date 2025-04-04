package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(
        quickFilterFields = { "nom" },
        descriptionField = "nom",
        artifacts = {
            @ResourceConfigArtifact(
                type = ResourceArtifactType.ACTION,
                code = DocumentNotificacioResource.ACTION_ACTUALITZAR_ESTAT_CODE,
                requiresId = true),
        }
)
public class DocumentNotificacioResource extends DocumentEnviamentResource {

    public static final String ACTION_ACTUALITZAR_ESTAT_CODE = "ACTUALITZAR_ESTAT";

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

    private ResourceReference<OrganGestorResource, Long> emisor;
}