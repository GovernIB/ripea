package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.InteressatTipusEnum;
import es.caib.ripea.service.intf.registre.RegistreInteressatDocumentTipusEnum;
import es.caib.ripea.service.intf.registre.RegistreInteressatTipusEnum;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Transient;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(
        quickFilterFields = { "nom" },
        descriptionField = "codiNom",
        artifacts = {
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = RegistreInteressatResource.PERSPECTIVE_REPRESENTANT_CODE),
        }
)
public class RegistreInteressatResource extends BaseAuditableResource<Long> {

    public static final String PERSPECTIVE_REPRESENTANT_CODE = "REPRESENTANT";

    private String adresa;
    private String canal;
    private String cp;
    private String documentNumero;
    private RegistreInteressatDocumentTipusEnum documentTipus;
    private String email;
    private String llinatge1;
    private String llinatge2;
    private String municipiCodi;
    private String nom;
    private String observacions;
    private String paisCodi;
    private String provinciaCodi;
    private String pais;
    private String provincia;
    private String municipi;
    private String raoSocial;
    private String telefon;
    private RegistreInteressatTipusEnum tipus;
    private String organCodi;

    @Transient private RegistreInteressatResource representantInfo;
    private ResourceReference<RegistreInteressatResource, Long> representant;
    private ResourceReference<RegistreResource, Long> registre;

    @Transient
    public String getCodiNom() {
        switch (tipus){
            case ADMINISTRACIO:
                return Utils.getCodiNom(InteressatTipusEnum.InteressatAdministracioEntity, this.documentNumero, this.nom, this.llinatge1, this.llinatge2, this.raoSocial, this.organCodi);
            case PERSONA_FISICA:
                return Utils.getCodiNom(InteressatTipusEnum.InteressatPersonaFisicaEntity, this.documentNumero, this.nom, this.llinatge1, this.llinatge2, this.raoSocial, this.organCodi);
            case PERSONA_JURIDICA:
                return Utils.getCodiNom(InteressatTipusEnum.InteressatPersonaJuridicaEntity, this.documentNumero, this.nom, this.llinatge1, this.llinatge2, this.raoSocial, this.organCodi);
            default:
                return Utils.getCodiNom(null, this.documentNumero, this.nom, this.llinatge1, this.llinatge2, this.raoSocial, this.organCodi);
        }
    }

    @Transient
    public String getNomComplet() {
        switch (tipus){
            case ADMINISTRACIO:
                return Utils.getNomComplet(InteressatTipusEnum.InteressatAdministracioEntity, this.nom, this.llinatge1, this.llinatge2, this.raoSocial, this.organCodi);
            case PERSONA_FISICA:
                return Utils.getNomComplet(InteressatTipusEnum.InteressatPersonaFisicaEntity, this.nom, this.llinatge1, this.llinatge2, this.raoSocial, this.organCodi);
            case PERSONA_JURIDICA:
                return Utils.getNomComplet(InteressatTipusEnum.InteressatPersonaJuridicaEntity, this.nom, this.llinatge1, this.llinatge2, this.raoSocial, this.organCodi);
            default:
                return Utils.getNomComplet(null, this.nom, this.llinatge1, this.llinatge2, this.raoSocial, this.organCodi);
        }
    }
}
