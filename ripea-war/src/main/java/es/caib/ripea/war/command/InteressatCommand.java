/**
 * 
 */
package es.caib.ripea.war.command;

import es.caib.ripea.core.api.dto.InteressatAdministracioDto;
import es.caib.ripea.core.api.dto.InteressatDocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.dto.InteressatIdiomaEnumDto;
import es.caib.ripea.core.api.dto.InteressatPersonaFisicaDto;
import es.caib.ripea.core.api.dto.InteressatPersonaJuridicaDto;
import es.caib.ripea.core.api.dto.InteressatTipusEnumDto;
import es.caib.ripea.war.command.InteressatCommand.Administracio;
import es.caib.ripea.war.command.InteressatCommand.PersonaFisica;
import es.caib.ripea.war.command.InteressatCommand.PersonaJuridica;
import es.caib.ripea.war.command.InteressatCommand.Repres;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import es.caib.ripea.war.validation.InteressatDocument;
import es.caib.ripea.war.validation.InteressatEmail;
import es.caib.ripea.war.validation.InteressatNoRepetit;
import es.caib.ripea.war.validation.InteressatPais;
import es.caib.ripea.war.validation.RepresentantNotSameInteressat;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


/**
 * Command per al manteniment d'interessats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@InteressatEmail(groups = {PersonaFisica.class, PersonaJuridica.class, Administracio.class})
@InteressatNoRepetit(groups = {PersonaFisica.class, PersonaJuridica.class, Administracio.class})
@InteressatPais(groups = {PersonaFisica.class, PersonaJuridica.class, Administracio.class})
@InteressatDocument(groups = {PersonaFisica.class, PersonaJuridica.class})
@RepresentantNotSameInteressat(groups = {Repres.class})
@Getter @Setter
public class InteressatCommand  {

//	public static final String TIPUS_CIUTADA = "C";
//	public static final String TIPUS_ADMINISTRACIO = "A";

	protected Long id;
	@NotNull(groups = {PersonaFisica.class, PersonaJuridica.class, Administracio.class})
	protected Long entitatId;
	@NotEmpty(groups = {PersonaFisica.class})
	@Size(max = 30, groups = {PersonaFisica.class}, message = "max.size")
    @Pattern(regexp = "^[A-Za-zŽžÀ-ÖØ-Ýà-öø-ÿ ]*$", groups = {PersonaFisica.class}, message = "only.letters")
	protected String nom;
	@NotEmpty(groups = {PersonaFisica.class})
	@Size(max = 30, groups = {PersonaFisica.class}, message = "max.size")
    @Pattern(regexp = "^[A-Za-zŽžÀ-ÖØ-Ýà-öø-ÿ ]*$", groups = {PersonaFisica.class}, message = "only.letters")
	protected String llinatge1;
	@Size(max = 30, groups = {PersonaFisica.class}, message = "max.size")
    @Pattern(regexp = "^[A-Za-zŽžÀ-ÖØ-Ýà-öø-ÿ ]*$", groups = {PersonaFisica.class}, message = "only.letters")
	protected String llinatge2;
	@NotEmpty(groups = {PersonaJuridica.class})
	@Size(max = 80, groups = {PersonaJuridica.class}, message = "max.size")
	protected String raoSocial;
	@NotEmpty(groups = {Administracio.class})
	@Size(max = 9, groups = {Administracio.class}, message = "max.size")
	protected String organCodi;
	protected String organNom;
	
	@NotNull(groups = {PersonaFisica.class, PersonaJuridica.class})
	protected InteressatDocumentTipusEnumDto documentTipus;
	@NotEmpty(groups = {PersonaFisica.class, PersonaJuridica.class})
	@Size(max = 9, groups = {PersonaFisica.class, PersonaJuridica.class}, message = "max.size")
	protected String documentNum;
	
	@Size(max = 4, groups={PersonaFisica.class, PersonaJuridica.class, Administracio.class}, message = "max.size")
	protected String pais;
	@Size(max = 2, groups = {PersonaFisica.class, PersonaJuridica.class, Administracio.class}, message = "max.size")
	protected String provincia;
	@Size(max = 5, groups = {PersonaFisica.class, PersonaJuridica.class, Administracio.class}, message = "max.size")
	protected String municipi;
	@Size(max = 160, groups = {PersonaFisica.class, PersonaJuridica.class, Administracio.class}, message = "max.size")
	protected String adresa;
	@Size(max = 5, groups = {PersonaFisica.class, PersonaJuridica.class, Administracio.class}, message = "max.size")
	protected String codiPostal;
	@Size(max = 160, groups = {PersonaFisica.class, PersonaJuridica.class, Administracio.class}, message = "max.size")
	protected String email;
	@Size(max = 20, groups = {PersonaFisica.class, PersonaJuridica.class, Administracio.class}, message = "max.size")
	protected String telefon;
	@Size(max = 160, groups = {PersonaFisica.class, PersonaJuridica.class, Administracio.class}, message = "max.size")
	protected String observacions;
	protected InteressatIdiomaEnumDto preferenciaIdioma;
	protected Boolean notificacioAutoritzat;

	private Boolean entregaDeh;
	private Boolean entregaDehObligat;
	
	private Boolean incapacitat;
	
	private Boolean ambOficinaSir;
	
	@NotNull
	protected InteressatTipusEnumDto tipus;
	protected boolean comprovat = false;
	protected String formulariAnterior;
	
	// Camps de filtre (No s'utilitzen al fer submit)
	protected String filtreCodiDir3;
	protected String filtreDenominacio;
	protected String filtreNivellAdministracio;
	protected String filtreComunitat;
	protected String filtreProvincia;
	protected String filtreLocalitat;
	protected Boolean filtreArrel;
	
	protected Long expedientId;
	protected Long interessatId;	// Per quan es un representant

	public void setNom(String nom) {
		this.nom = nom != null ? nom.trim() : null;
	}
	public void setLlinatge1(String llinatge1) {
		this.llinatge1 = getTrimmedStringField(llinatge1);
	}
	public void setLlinatge2(String llinatge2) {
		this.llinatge2 = getTrimmedStringField(llinatge2);
	}
	public void setRaoSocial(String raoSocial) {
		this.raoSocial = getTrimmedStringField(raoSocial);
	}
	public void setOrganCodi(String organCodi) {
		this.organCodi = getTrimmedStringField(organCodi);
	}
	public void setDocumentNum(String documentNum) {
		this.documentNum = getTrimmedStringField(documentNum);
	}
	public void setPais(String pais) {
		this.pais = getTrimmedStringField(pais);
	}
	public void setProvincia(String provincia) {
		this.provincia = getTrimmedStringField(provincia);
	}
	public void setMunicipi(String municipi) {
		this.municipi = getTrimmedStringField(municipi);
	}
	public void setAdresa(String adresa) {
		this.adresa = getTrimmedStringField(adresa);
	}
	public void setCodiPostal(String codiPostal) {
		this.codiPostal = getTrimmedStringField(codiPostal);
	}
	public void setEmail(String email) {
		this.email = getTrimmedStringField(email);
	}
	public void setTelefon(String telefon) {
		this.telefon = getTrimmedStringField(telefon);
	}
	public void setObservacions(String observacions) {
		this.observacions = getTrimmedStringField(observacions);
	}

	public void setFiltreCodiDir3(String filtreCodiDir3) {
		this.filtreCodiDir3 = getTrimmedStringField(filtreCodiDir3);
	}
	public void setFiltreDenominacio(String filtreDenominacio) {
		this.filtreDenominacio = getTrimmedStringField(filtreDenominacio);
	}
	public void setFiltreNivellAdministracio(String filtreNivellAdministracio) {
		this.filtreNivellAdministracio = getTrimmedStringField(filtreNivellAdministracio);
	}
	public void setFiltreComunitat(String filtreComunitat) {
		this.filtreComunitat = getTrimmedStringField(filtreComunitat);
	}
	public void setFiltreProvincia(String filtreProvincia) {
		this.filtreProvincia = getTrimmedStringField(filtreProvincia);
	}
	public void setFiltreLocalitat(String filtreLocalitat) {
		this.filtreLocalitat = getTrimmedStringField(filtreLocalitat);
	}
	public void setOrganNom(String organNom) {
		this.organNom = getTrimmedStringField(organNom);
	}

    private String getTrimmedStringField(String value) {
        return value != null ? value.trim() : null;
    }

	public static InteressatCommand asCommand(InteressatDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				InteressatCommand.class);
	}
	public static InteressatPersonaFisicaDto asPersonaFisicaDto(InteressatCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				InteressatPersonaFisicaDto.class);
	}
	public static InteressatPersonaJuridicaDto asPersonaJuridicaDto(InteressatCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				InteressatPersonaJuridicaDto.class);
	}
	public static InteressatAdministracioDto asAdministracioDto(InteressatCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				InteressatAdministracioDto.class);
	}

	public boolean isPersonaFisica() {
		if (tipus == null)
			return true;
		return InteressatTipusEnumDto.PERSONA_FISICA.equals(tipus);
	}
	public boolean isPersonaJuridica() {
		return InteressatTipusEnumDto.PERSONA_JURIDICA.equals(tipus);
	}
	public boolean isAdministracio() {
		return InteressatTipusEnumDto.ADMINISTRACIO.equals(tipus);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public interface PersonaFisica {}
	public interface PersonaJuridica {}
	public interface Administracio {}
	public interface Repres {}

}
