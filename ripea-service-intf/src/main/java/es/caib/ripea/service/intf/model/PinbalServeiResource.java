package es.caib.ripea.service.intf.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceField;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.dto.PinbalServeiDocPermesEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(quickFilterFields = { "codi", "nom" }, descriptionField = "nom")
public class PinbalServeiResource extends BaseAuditableResource<Long> {

	@NotNull
	@Size(max = 64)
	private String codi;
	@Size(max = 256)
	private String nom;
	@NotNull
	private boolean pinbalServeiDocPermesDni;
	@NotNull
	private boolean pinbalServeiDocPermesNif;
	@NotNull
	private boolean pinbalServeiDocPermesCif;
	@NotNull
	private boolean pinbalServeiDocPermesNie;
	@NotNull
	private boolean pinbalServeiDocPermesPas;
	private boolean actiu;

    @Transient
    private List<PinbalServeiDocPermesEnumDto> pinbalServeiDocPermesEnum = new ArrayList<>();

    // Camps del formulari d'alta: el servei es tria entre els de PINBAL i no es desen.
    /**
     * Id d'una entitat o d'un òrgan gestor que consulta PINBAL amb el seu propi CIF (la seqüència d'ids és
     * compartida, així que l'id identifica una sola unitat). Els serveis es consulten a l'entitat de PINBAL que té
     * el mateix CIF.
     */
    @ResourceField(enumType = true, onChangeActive = true)
    @Transient
    private String unitatOrganitzativa;
    /** Codi del servei de PINBAL triat. En seleccionar-lo s'emplenen el codi, el nom, els documents i l'actiu. */
    @ResourceField(enumType = true, onChangeActive = true)
    @Transient
    private String serveiPinbal;
    /** Avís per a l'usuari quan la consulta a PINBAL falla o no retorna cap servei nou. */
    @Transient
    private String avisPinbal;

	private static final long serialVersionUID = 6916266264935291305L;
}