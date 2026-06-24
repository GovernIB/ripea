package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceField;
import es.caib.ripea.service.intf.base.model.BaseResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ContingutVistaEnumDto;
import es.caib.ripea.service.intf.dto.IdiomaEnumDto;
import es.caib.ripea.service.intf.dto.InterficieUsuariEnumDto;
import es.caib.ripea.service.intf.dto.MoureDestiVistaEnumDto;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(quickFilterFields = { "codi", "nom", "nif" }, descriptionField = "codiAndNom")
public class UsuariResource extends BaseResource<String> {

	private static final long serialVersionUID = -3198881636735062393L;
	
	@NotNull
	@Size(max = 64)
	private String codi;
	@NotNull
	@Size(max = 200)
	private String nom;
	@NotNull
	@Size(max = 9)
	private String nif;
	@Size(max = 200)
	private String email;
	@Size(max = 200)
	private String emailAlternatiu;
	private IdiomaEnumDto idioma;
	private boolean inicialitzat = false;
	@Size(max = 64)
	private String rolActual;
	private ContingutVistaEnumDto vistaActual;
    // Nivell de foscor del tema, 0 (clar) a 100 (fosc).
    private int nivellFosc = 0;
    @Size(max = 7)
    private String colorPrincipal = "#337ab7";
    @Size(max = 7)
    private String colorSecundari = "#f1efef";

    @ResourceField(enumType = true)
	private Long numElementsPagina;

    public String getNumElementsPagina() {
        return numElementsPagina != null ? numElementsPagina.toString() : null;
    }
    public void setNumElementsPagina(String value) {
        this.numElementsPagina = (value != null && !value.isEmpty()) ? Long.parseLong(value) : null;
    }

	private boolean rebreEmailsAgrupats = true;
	private boolean rebreAvisosNovesAnotacions = true;
	private boolean rebreEmailsCanviEstatRevisio = true;
	private boolean expedientListDataDarrerEnviament = false;
	private boolean expedientListAgafatPer = true;
	private boolean expedientListInteressats = true;
	private boolean expedientListComentaris = true;
	private boolean expedientListGrup = false;

	private ResourceReference<MetaExpedientResource, Long> procediment;
	private ResourceReference<EntitatResource, Long> entitatPerDefecte;
	private ResourceReference<EntitatResource, Long> entitatActual;

	private boolean expedientExpandit = true;

    @Transient
    private List<String> rols;

    public String getCodiAndNom() {
    	return nom + " (" + codi +")";
    }

    public String getNomAndNif() {
    	return nom + " (" + Utils.nifMask(nif) +")";
    }

	private MoureDestiVistaEnumDto vistaMoureActual = MoureDestiVistaEnumDto.LLISTA;

	private InterficieUsuariEnumDto interficieUsuari = InterficieUsuariEnumDto.REACT;

	@Override
	public String getId() {
		return this.getCodi();
	}

	@Override
	public void setId(String id) {
		this.codi = id;
	}
}