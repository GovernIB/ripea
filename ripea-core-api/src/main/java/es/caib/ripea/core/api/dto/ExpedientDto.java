/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;



/**
 * Informació d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientDto extends NodeDto {


	private Long id;
	private ExpedientEstatEnumDto estat;
	private Date tancatData;
	private String tancatMotiu;
	private int any;
	private long sequencia;
	private String codi;
	private String ntiVersion;
	private String ntiIdentificador;
	private String ntiOrgano;
	private String ntiOrganoDescripcio;
	private Date ntiFechaApertura;
	private String ntiClasificacionSia;
	private boolean sistraPublicat;
	private String sistraUnitatAdministrativa;
	private String sistraClau;
	private UsuariDto agafatPer;
	private String numero;
	private boolean conteDocumentsFirmats;
	private long numComentaris;
	private ExpedientEstatDto expedientEstat;
	private Long expedientEstatId;
	private Long expedientEstatNextInOrder;
	private boolean usuariActualWrite;
	private boolean peticions;
	private boolean processatOk;
	private boolean tasques;

	public boolean isTasques() {
		return tasques;
	}
	public void setTasques(boolean tasques) {
		this.tasques = tasques;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public ExpedientEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(ExpedientEstatEnumDto estat) {
		this.estat = estat;
	}
	public Date getTancatData() {
		return tancatData;
	}
	public void setTancatData(Date tancatData) {
		this.tancatData = tancatData;
	}
	public String getTancatMotiu() {
		return tancatMotiu;
	}
	public void setTancatMotiu(String tancatMotiu) {
		this.tancatMotiu = tancatMotiu;
	}
	public int getAny() {
		return any;
	}
	public void setAny(int any) {
		this.any = any;
	}
	public long getSequencia() {
		return sequencia;
	}
	public void setSequencia(long sequencia) {
		this.sequencia = sequencia;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNtiVersion() {
		return ntiVersion;
	}
	public void setNtiVersion(String ntiVersion) {
		this.ntiVersion = ntiVersion;
	}
	public String getNtiIdentificador() {
		return ntiIdentificador;
	}
	public void setNtiIdentificador(String ntiIdentificador) {
		this.ntiIdentificador = ntiIdentificador;
	}
	public String getNtiOrgano() {
		return ntiOrgano;
	}
	public void setNtiOrgano(String ntiOrgano) {
		this.ntiOrgano = ntiOrgano;
	}
	public String getNtiOrganoDescripcio() {
		return ntiOrganoDescripcio;
	}
	public void setNtiOrganoDescripcio(String ntiOrganoDescripcio) {
		this.ntiOrganoDescripcio = ntiOrganoDescripcio;
	}
	public Date getNtiFechaApertura() {
		return ntiFechaApertura;
	}
	public void setNtiFechaApertura(Date ntiFechaApertura) {
		this.ntiFechaApertura = ntiFechaApertura;
	}
	public String getNtiClasificacionSia() {
		return ntiClasificacionSia;
	}
	public void setNtiClasificacionSia(String ntiClasificacionSia) {
		this.ntiClasificacionSia = ntiClasificacionSia;
	}
	public boolean isSistraPublicat() {
		return sistraPublicat;
	}
	public void setSistraPublicat(boolean sistraPublicat) {
		this.sistraPublicat = sistraPublicat;
	}
	public String getSistraUnitatAdministrativa() {
		return sistraUnitatAdministrativa;
	}
	public void setSistraUnitatAdministrativa(String sistraUnitatAdministrativa) {
		this.sistraUnitatAdministrativa = sistraUnitatAdministrativa;
	}
	public String getSistraClau() {
		return sistraClau;
	}
	public void setSistraClau(String sistraClau) {
		this.sistraClau = sistraClau;
	}
	public UsuariDto getAgafatPer() {
		return agafatPer;
	}
	public void setAgafatPer(UsuariDto agafatPer) {
		this.agafatPer = agafatPer;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public boolean isConteDocumentsFirmats() {
		return conteDocumentsFirmats;
	}
	public void setConteDocumentsFirmats(boolean conteDocumentsFirmats) {
		this.conteDocumentsFirmats = conteDocumentsFirmats;
	}
	public long getNumComentaris() {
		return numComentaris;
	}
	public void setNumComentaris(long numComentaris) {
		this.numComentaris = numComentaris;
	}
	public ExpedientEstatDto getExpedientEstat() {
		return expedientEstat;
	}
	public void setExpedientEstat(ExpedientEstatDto expedientEstat) {
		this.expedientEstat = expedientEstat;
	}
	public Long getExpedientEstatId() {
		return expedientEstatId;
	}
	public void setExpedientEstatId(Long expedientEstatId) {
		this.expedientEstatId = expedientEstatId;
	}
	public Long getExpedientEstatNextInOrder() {
		return expedientEstatNextInOrder;
	}
	public void setExpedientEstatNextInOrder(Long expedientEstatNextInOrder) {
		this.expedientEstatNextInOrder = expedientEstatNextInOrder;
	}
	public boolean isUsuariActualWrite() {
		return usuariActualWrite;
	}
	public void setUsuariActualWrite(boolean usuariActualWrite) {
		this.usuariActualWrite = usuariActualWrite;
	}
	public boolean isPeticions() {
		return peticions;
	}
	public void setPeticions(boolean peticions) {
		this.peticions = peticions;
	}

	public MetaExpedientDto getMetaExpedient() {
		return (MetaExpedientDto)getMetaNode();
	}

	public boolean isAgafat() {
		return agafatPer != null;
	}

	protected ExpedientDto copiarContenidor(ContingutDto original) {
		ExpedientDto copia = new ExpedientDto();
		copia.setId(original.getId());
		copia.setNom(original.getNom());
		return copia;
	}
	public boolean isProcessatOk() {
		return processatOk;
	}
	public void setProcessatOk(boolean processatOk) {
		this.processatOk = processatOk;
	}
	
	public String getNomINumero() {
		return this.nom + " (" + this.numero + ")";
	}

}