package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.registre.RegistreAnnexFirmaTipusEnum;
import es.caib.ripea.service.intf.registre.RegistreAnnexNtiEstadoElaboracionEnum;
import es.caib.ripea.service.intf.registre.RegistreAnnexNtiOrigenEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Transient;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(quickFilterFields = { "nom" }, descriptionField = "observacions")
public class RegistreAnnexResource extends BaseAuditableResource<Long> {

    private String firmaPerfil;
    private long firmaTamany;
    private RegistreAnnexFirmaTipusEnum firmaTipus;
    private String firmaNom;
    private String nom;

    private Date ntiFechaCaptura;
    private RegistreAnnexNtiOrigenEnum ntiOrigen;
    private NtiTipoDocumentoEnumDto ntiTipoDocumental;
    private RegistreAnnexNtiEstadoElaboracionEnum ntiEstadoElaboracion;
    private String observacions;

    private SicresTipoDocumentoEnumDto sicresTipoDocumento;
    private SicresValidezDocumentoEnumDto sicresValidezDocumento;

    private long tamany;
    private String tipusMime;
    private String titol;
    private String uuid;

    private RegistreAnnexEstatEnumDto estat;
    private String error;

    private boolean validacioFirmaCorrecte;
    private String validacioFirmaErrorMsg;
    private ArxiuEstatEnumDto annexArxiuEstat;

    private ResourceReference<RegistreResource, Long> registre;
    private ResourceReference<DocumentResource, Long> document;

    public String getFitxerExtension() {
        if (nom != null) {
            return nom.substring(
                    nom.lastIndexOf('.') + 1,
                    nom.length());
        } else {
            return "";
        }
    }
}
