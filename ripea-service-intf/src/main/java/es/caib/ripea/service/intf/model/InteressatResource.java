package es.caib.ripea.service.intf.model;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Transient;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceField;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.Resource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.InteressatDocumentTipusEnumDto;
import es.caib.ripea.service.intf.dto.InteressatIdiomaEnumDto;
import es.caib.ripea.service.intf.dto.InteressatTipusEnum;
import es.caib.ripea.service.intf.resourcevalidation.InteressatValid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@InteressatValid(groups = {Resource.OnCreate.class, Resource.OnUpdate.class})
@ResourceConfig(
        quickFilterFields = { "documentNum", "nom" },
        descriptionField = "codiNom",
        artifacts = {
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = InteressatResource.PERSPECTIVE_REPRESENTANT_CODE),	              
        }
)
public class InteressatResource extends BaseAuditableResource<Long> {

    public static final String PERSPECTIVE_REPRESENTANT_CODE = "REPRESENTANT";

	@NotNull
	protected InteressatTipusEnum tipus = InteressatTipusEnum.InteressatPersonaFisicaEntity;
    @NotNull
	@Size(max = 30)
	protected String nom;
    @NotNull
	@Size(max = 30)
	protected String llinatge1;
	@Size(max = 30)
	protected String llinatge2;
	@Size(max = 80)
	protected String raoSocial;
	@Size(max = 9)
	protected String organCodi;
	@Size(max = 256)
	protected String organNom;
	protected Boolean ambOficinaSir;
	
	@NotNull
	protected InteressatDocumentTipusEnumDto documentTipus = InteressatDocumentTipusEnumDto.NIF;
	@NotNull
	@Size(max = 17)
    @ResourceField(onChangeActive = true)
	protected String documentNum;
	@Size(max = 4)
	protected String pais;
	@Size(max = 2)
	protected String provincia;
	@Size(max = 5)
	protected String municipi;
	@Size(max = 160)
	protected String adresa;
	@Size(max = 5)
	protected String codiPostal;
	@Size(max = 160)
	protected String email;
	@Size(max = 20)
	protected String telefon;
	@Size(max = 160)
	protected String observacions;
	protected InteressatIdiomaEnumDto preferenciaIdioma = InteressatIdiomaEnumDto.CA;
	@NotNull
	protected boolean notificacioAutoritzat;
	@NotNull
	protected boolean esRepresentant;	
	protected Boolean entregaDeh;
	protected Boolean entregaDehObligat;	
	protected Boolean incapacitat;	
	protected boolean arxiuPropagat;
	protected Date arxiuIntentData;
	protected int arxiuReintents;
	
	@NotNull
	private ResourceReference<ExpedientResource, Long> expedient;
	private ResourceReference<InteressatResource, Long> representant;
    
	@Transient private InteressatResource representantInfo;
    @Transient private ResourceReference<InteressatResource, Long> representat;
    @Transient private boolean hasRepresentats;

    @Transient
	public String getCodiNom() {
        switch (this.tipus) {
            case InteressatPersonaFisicaEntity:
                return documentNum + " - " + getNomComplet();
            default:
                return getNomComplet();
        }
    }
    
    @Transient
	public String getNomComplet() {
		switch (this.tipus) {
		case InteressatPersonaFisicaEntity:
			StringBuilder sb = new StringBuilder();
			if (nom != null) {
				sb.append(nom);
			}
			if (llinatge1 != null) {
				sb.append(" ");
				sb.append(llinatge1);
				if (llinatge2 != null) {
					sb.append(" ");
					sb.append(llinatge2);
				}
			}
			return sb.toString();	
		case InteressatPersonaJuridicaEntity:
			return raoSocial;
		case InteressatAdministracioEntity:
			return organCodi;
		default:
			return null;
		}
	}
}