package es.caib.ripea.service.intf.model;

import java.util.Date;

import javax.validation.constraints.NotNull;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.InteressatDocumentTipusEnumDto;
import es.caib.ripea.service.intf.dto.InteressatIdiomaEnumDto;
import es.caib.ripea.service.intf.dto.InteressatTipusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(quickFilterFields = { "documentNum", "nom" }, descriptionField = "nom")
public class InteressatResource extends BaseResource<Long> {

	protected InteressatTipusEnum tipus;
	
	protected String nom;
	protected String llinatge1;
	protected String llinatge2;

	protected String raoSocial;
	
	protected String organCodi;
	protected String organNom;
	protected Boolean ambOficinaSir;
	
	@NotNull
	protected InteressatDocumentTipusEnumDto documentTipus;
	@NotNull
	protected String documentNum;
	protected String pais;
	protected String provincia;
	protected String municipi;
	protected String adresa;
	protected String codiPostal;
	protected String email;
	protected String telefon;
	protected String observacions;
	protected InteressatIdiomaEnumDto preferenciaIdioma;
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
