/**
 * 
 */
package es.caib.ripea.war.command;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

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
import es.caib.ripea.war.helper.ConversioTipusHelper;
import es.caib.ripea.war.validation.InteressatDocument;
import es.caib.ripea.war.validation.InteressatEmail;
import es.caib.ripea.war.validation.InteressatNoRepetit;
import es.caib.ripea.war.validation.InteressatPais;

/**
 * Command per al manteniment d'interessats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@InteressatEmail(groups = {PersonaFisica.class, PersonaJuridica.class, Administracio.class})
@InteressatNoRepetit(groups = {PersonaFisica.class, PersonaJuridica.class, Administracio.class})
@InteressatPais(groups = {PersonaFisica.class, PersonaJuridica.class, Administracio.class})
@InteressatDocument(groups = {PersonaFisica.class, PersonaJuridica.class})
public class InteressatCommand  {

//	public static final String TIPUS_CIUTADA = "C";
//	public static final String TIPUS_ADMINISTRACIO = "A";

	protected Long id;
	@NotNull(groups = {PersonaFisica.class, PersonaJuridica.class, Administracio.class})
	protected Long entitatId;
	@NotEmpty(groups = {PersonaFisica.class})
	@Size(max = 30, groups = {PersonaFisica.class}, message = "max.size")
	protected String nom;
	@NotEmpty(groups = {PersonaFisica.class})
	@Size(max = 30, groups = {PersonaFisica.class}, message = "max.size")
	protected String llinatge1;
	@Size(max = 30, groups = {PersonaFisica.class}, message = "max.size")
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
//	@DocumentIdentitat(groups = {PersonaFisica.class, PersonaJuridica.class})
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
	
	@NotNull
	protected InteressatTipusEnumDto tipus;
	protected boolean comprovat = false;
	
	
	// Camps de filtre (No s'utilitzen al fer submit)
	protected String filtreCodiDir3;
	protected String filtreDenominacio;
	protected String filtreNivellAdministracio;
	protected String filtreComunitat;
	protected String filtreProvincia;
	protected String filtreLocalitat;
	protected Boolean filtreArrel;
	
	protected Long expedientId;
	
	public Long getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom != null ? nom.trim() : null;
	}
	public String getLlinatge1() {
		return llinatge1;
	}
	public void setLlinatge1(String llinatge1) {
		this.llinatge1 = llinatge1 != null ? llinatge1.trim() : null;
	}
	public String getLlinatge2() {
		return llinatge2;
	}
	public void setLlinatge2(String llinatge2) {
		this.llinatge2 = llinatge2 != null ? llinatge2.trim() : null;
	}
	public String getRaoSocial() {
		return raoSocial;
	}
	public void setRaoSocial(String raoSocial) {
		this.raoSocial = raoSocial != null ? raoSocial.trim() : null;
	}
	public String getOrganCodi() {
		return organCodi;
	}
	public void setOrganCodi(String organCodi) {
		this.organCodi = organCodi != null ? organCodi.trim() : null;
	}
	public InteressatDocumentTipusEnumDto getDocumentTipus() {
		return documentTipus;
	}
	public void setDocumentTipus(InteressatDocumentTipusEnumDto documentTipus) {
		this.documentTipus = documentTipus;
	}
	public Boolean getIncapacitat() {
		return incapacitat;
	}
	public void setIncapacitat(Boolean incapacitat) {
		this.incapacitat = incapacitat;
	}
	public String getDocumentNum() {
		return documentNum;
	}
	public void setDocumentNum(String documentNum) {
		this.documentNum = documentNum != null ? documentNum.trim() : null;
	}
	public String getPais() {
		return pais;
	}
	public void setPais(String pais) {
		this.pais = pais != null ? pais.trim() : null;
	}
	public String getProvincia() {
		return provincia;
	}
	public void setProvincia(String provincia) {
		this.provincia = provincia != null ? provincia.trim() : null;
	}
	public String getMunicipi() {
		return municipi;
	}
	public void setMunicipi(String municipi) {
		this.municipi = municipi != null ? municipi.trim() : null;
	}
	public String getAdresa() {
		return adresa;
	}
	public void setAdresa(String adresa) {
		this.adresa = adresa != null ? adresa.trim() : null;
	}
	public String getCodiPostal() {
		return codiPostal;
	}
	public void setCodiPostal(String codiPostal) {
		this.codiPostal = codiPostal != null ? codiPostal.trim() : null;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email != null ? email.trim() : null;
	}
	public String getTelefon() {
		return telefon;
	}
	public void setTelefon(String telefon) {
		this.telefon = telefon != null ? telefon.trim() : null;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions != null ? observacions.trim() : null;
	}
	public InteressatIdiomaEnumDto getPreferenciaIdioma() {
		return preferenciaIdioma;
	}
	public void setPreferenciaIdioma(InteressatIdiomaEnumDto preferenciaIdioma) {
		this.preferenciaIdioma = preferenciaIdioma;
	}
	public Boolean getNotificacioAutoritzat() {
		return notificacioAutoritzat;
	}
	public void setNotificacioAutoritzat(Boolean notificacioAutoritzat) {
		this.notificacioAutoritzat = notificacioAutoritzat;
	}
	public InteressatTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(InteressatTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public boolean isComprovat() {
		return comprovat;
	}
	public void setComprovat(boolean comprovat) {
		this.comprovat = comprovat;
	}

	public String getFiltreCodiDir3() {
		return filtreCodiDir3;
	}
	public void setFiltreCodiDir3(String filtreCodiDir3) {
		this.filtreCodiDir3 = filtreCodiDir3 != null ? filtreCodiDir3.trim() : null;
	}
	public String getFiltreDenominacio() {
		return filtreDenominacio;
	}
	public void setFiltreDenominacio(String filtreDenominacio) {
		this.filtreDenominacio = filtreDenominacio != null ? filtreDenominacio.trim() : null;
	}
	public String getFiltreNivellAdministracio() {
		return filtreNivellAdministracio;
	}
	public void setFiltreNivellAdministracio(String filtreNivellAdministracio) {
		this.filtreNivellAdministracio = filtreNivellAdministracio != null ? filtreNivellAdministracio.trim() : null;
	}
	public String getFiltreComunitat() {
		return filtreComunitat;
	}
	public void setFiltreComunitat(String filtreComunitat) {
		this.filtreComunitat = filtreComunitat != null ? filtreComunitat.trim() : null;
	}
	public String getFiltreProvincia() {
		return filtreProvincia;
	}
	public void setFiltreProvincia(String filtreProvincia) {
		this.filtreProvincia = filtreProvincia != null ? filtreProvincia.trim() : null;
	}
	public String getFiltreLocalitat() {
		return filtreLocalitat;
	}
	public void setFiltreLocalitat(String filtreLocalitat) {
		this.filtreLocalitat = filtreLocalitat != null ? filtreLocalitat.trim() : null;
	}
	public Boolean getFiltreArrel() {
		return filtreArrel;
	}
	public void setFiltreArrel(Boolean filtreArrel) {
		this.filtreArrel = filtreArrel;
	}
	public String getOrganNom() {
		return organNom;
	}
	public void setOrganNom(String organNom) {
		this.organNom = organNom != null ? organNom.trim() : null;
	}
	public static InteressatCommand asCommand(InteressatDto dto) {
		InteressatCommand command = ConversioTipusHelper.convertir(
				dto,
				InteressatCommand.class);
		return command;
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

	public Boolean getEntregaDeh() {
		return entregaDeh;
	}
	public void setEntregaDeh(Boolean entregaDeh) {
		this.entregaDeh = entregaDeh;
	}

	public Boolean getEntregaDehObligat() {
		return entregaDehObligat;
	}
	public void setEntregaDehObligat(Boolean entregaDehObligat) {
		this.entregaDehObligat = entregaDehObligat;
	}

	public interface PersonaFisica {}
	public interface PersonaJuridica {}
	public interface Administracio {}

}
