package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
            @ResourceConfigArtifact(
                    type = ResourceArtifactType.ACTION,
                    code = DocumentNotificacioResource.ACTION_ELIMINAR,
                    formClass = Serializable.class,
                    requiresId = true),
			@ResourceConfigArtifact(
					type = ResourceArtifactType.REPORT,
					code = DocumentNotificacioResource.ACTION_DESCARREGAR_JUSTIFICANT,
					formClass = DocumentNotificacioResource.MassiveAction.class),				
        }
)
public class DocumentNotificacioResource extends DocumentEnviamentResource {

	private static final long serialVersionUID = -1924617628889102191L;
	
	public static final String ACTION_ACTUALITZAR_ESTAT_CODE	= "ACTUALITZAR_ESTAT";
    public static final String ACTION_DESCARREGAR_JUSTIFICANT	= "DESCARREGAR_JUSTIFICANT";
    public static final String ACTION_ELIMINAR					= "DELETE_NOTIFICACIO";

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
    
    @Getter
    @Setter
    public static class MassiveAction implements Serializable {
		private static final long serialVersionUID = 2652981509368050812L;
		@NotNull
        @NotEmpty
        private List<Long> ids;
        private boolean massivo = false;
    }
}