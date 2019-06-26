/**
 * 
 */
package es.caib.ripea.core.entity;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.distribucio.ws.backofficeintegracio.FirmaPerfil;
import es.caib.distribucio.ws.backofficeintegracio.FirmaTipus;
import es.caib.distribucio.ws.backofficeintegracio.NtiEstadoElaboracio;
import es.caib.distribucio.ws.backofficeintegracio.NtiEstadoElaboracion;
import es.caib.distribucio.ws.backofficeintegracio.NtiOrigen;
import es.caib.distribucio.ws.backofficeintegracio.NtiTipoDocumento;
import es.caib.distribucio.ws.backofficeintegracio.SicresTipoDocumento;
import es.caib.distribucio.ws.backofficeintegracio.SicresValidezDocumento;
import es.caib.ripea.core.api.dto.RegistreAnnexEstatEnumDto;
import es.caib.ripea.core.audit.RipeaAuditable;

/**
 * Classe del model de dades que representa un document
 * d'una anotació al registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name = "ipa_registre_annex")
@EntityListeners(AuditingEntityListener.class)
public class RegistreAnnexEntity extends RipeaAuditable<Long> {

	
	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "contingut")
	private byte[] contingut;
	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "firma_contingut")
	private byte[] firmaContingut;
	@Enumerated(EnumType.STRING)
	@Column(name = "firma_perfil", length = 4)
	private FirmaPerfil firmaPerfil;
	@Column(name = "firma_tamany")
	private long firmaTamany;
	@Enumerated(EnumType.STRING)
	@Column(name = "firma_tipus", length = 4)
	private FirmaTipus firmaTipus;
	@Column(name = "nom", length = 80, nullable = false)
	private String nom;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "nti_fecha_captura", nullable = false)
	private Date ntiFechaCaptura;
	@Enumerated(EnumType.STRING)
	@Column(name = "nti_origen", length = 20, nullable = false)
	private NtiOrigen ntiOrigen;
	@Enumerated(EnumType.STRING)
	@Column(name = "nti_tipo_doc", length = 20, nullable = false)
	private NtiTipoDocumento ntiTipoDocumental;
	@Column(name = "observacions", length = 50)
	private String observacions;
	@Enumerated(EnumType.STRING)
	@Column(name = "sicres_tipo_doc", length = 20 , nullable = false)
	private SicresTipoDocumento sicresTipoDocumento;
	@Enumerated(EnumType.STRING)
	@Column(name = "sicres_validez_doc", length = 30)
	private SicresValidezDocumento sicresValidezDocumento;
	@Enumerated(EnumType.STRING)
	@Column(name = "nti_estado_elaboracio", length = 50, nullable = false)
	private NtiEstadoElaboracion ntiEstadoElaboracion;
	@Column(name = "tamany", nullable = false)
	private long tamany;
	@Column(name = "tipus_mime", length = 30)
	private String tipusMime;
	@Column(name = "titol", length = 200, nullable = false)
	private String titol;
	@Column(name = "uuid", length = 100)
	private String uuid;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "estat", length = 20, nullable = false)
	private RegistreAnnexEstatEnumDto estat;
	@Column(name = "error", length = 4000)
	private String error;
	
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "registre_id")
	@ForeignKey(name = "dis_interessat_registre_fk")
	private RegistreEntity registre;
	
	
	
	
	public static Builder getBuilder(
			 String nom,
			 Date ntiFechaCaptura,
			 NtiOrigen ntiOrigen,
			 NtiTipoDocumento ntiTipoDocumental,
			 SicresTipoDocumento sicresTipoDocumento,
			 String titol,
			 RegistreEntity registre,
			 NtiEstadoElaboracion ntiEstadoElaboracion) {
		return new Builder(
				 nom,
				 ntiFechaCaptura,
				 ntiOrigen,
				 ntiTipoDocumental,
				 sicresTipoDocumento,
				 titol,
				 registre,
				 ntiEstadoElaboracion);
	}

	/**
	 * Builder per a crear noves instàncies d'aquesta classe.
	 * 
	 * @author Limit Tecnologies <limit@limit.es>
	 */
	public static class Builder {
		RegistreAnnexEntity built;

		Builder(
				 String nom,
				 Date ntiFechaCaptura,
				 NtiOrigen ntiOrigen,
				 NtiTipoDocumento ntiTipoDocumental,
				 SicresTipoDocumento sicresTipoDocumento,
				 String titol,
				 RegistreEntity registre,
				 NtiEstadoElaboracion ntiEstadoElaboracion) {
			built = new RegistreAnnexEntity();
			built.nom = nom;
			built.ntiFechaCaptura = ntiFechaCaptura;
			built.ntiOrigen = ntiOrigen;
			built.ntiTipoDocumental = ntiTipoDocumental;
			built.sicresTipoDocumento = sicresTipoDocumento;
			built.titol = titol;
			built.registre = registre;
			built.estat = RegistreAnnexEstatEnumDto.CREAT;
			built.ntiEstadoElaboracion = ntiEstadoElaboracion;
		}
		
		public Builder contingut(byte[] contingut) {
			built.contingut = contingut;
			return this;
		}
		public Builder firmaContingut(byte[] firmaContingut) {
			built.firmaContingut = firmaContingut;
			return this;
		}
		public Builder ntiTipoDocumental(NtiTipoDocumento ntiTipoDocumental) {
			built.ntiTipoDocumental = ntiTipoDocumental;
			return this;
		}
		public Builder sicresTipoDocumento(SicresTipoDocumento sicresTipoDocumento) {
			built.sicresTipoDocumento = sicresTipoDocumento;
			return this;
		}
		public Builder observacions(String observacions) {
			built.observacions = observacions;
			return this;
		}
		public Builder sicresValidezDocumento(SicresValidezDocumento sicresValidezDocumento) {
			built.sicresValidezDocumento = sicresValidezDocumento;
			return this;
		}
		public Builder tipusMime(String tipusMime) {
			built.tipusMime = tipusMime;
			return this;
		}
		public Builder uuid(String uuid) {
			built.uuid = uuid;
			return this;
		}
		public RegistreAnnexEntity build() {
			return built;
		}
	}
	
	
	public RegistreEntity getRegistre() {
		return registre;
	}
	public void updateRegistre(RegistreEntity registre) {
		this.registre = registre;
	}
	public void updateContingut(byte[] contingut) {
		this.contingut = contingut;
	}
	public void updateFirmaContingut(byte[] firmaContingut) {
		this.firmaContingut = firmaContingut;
	}
	public void updateFirmaPerfil(FirmaPerfil firmaPerfil) {
		this.firmaPerfil = firmaPerfil;
	}
	public void updateFirmaTamany(long firmaTamany) {
		this.firmaTamany = firmaTamany;
	}
	public void updateFirmaTipus(FirmaTipus firmaTipus) {
		this.firmaTipus = firmaTipus;
	}
	public void updateNom(String nom) {
		this.nom = nom;
	}
	public void updateNtiFechaCaptura(Date ntiFechaCaptura) {
		this.ntiFechaCaptura = ntiFechaCaptura;
	}
	public void updateNtiOrigen(NtiOrigen ntiOrigen) {
		this.ntiOrigen = ntiOrigen;
	}
	public void updateNtiTipoDocumental(NtiTipoDocumento ntiTipoDocumental) {
		this.ntiTipoDocumental = ntiTipoDocumental;
	}
	public void updateObservacions(String observacions) {
		this.observacions = observacions;
	}
	public void updateSicresTipoDocumento(SicresTipoDocumento sicresTipoDocumento) {
		this.sicresTipoDocumento = sicresTipoDocumento;
	}
	public void updateSicresValidezDocumento(SicresValidezDocumento sicresValidezDocumento) {
		this.sicresValidezDocumento = sicresValidezDocumento;
	}
	public void updateTamany(long tamany) {
		this.tamany = tamany;
	}
	public void updateTipusMime(String tipusMime) {
		this.tipusMime = tipusMime;
	}
	public void updateTitol(String titol) {
		this.titol = titol;
	}
	public void updateUuid(String uuid) {
		this.uuid = uuid;
	}
	public byte[] getContingut() {
		return contingut;
	}
	public byte[] getFirmaContingut() {
		return firmaContingut;
	}
	public FirmaPerfil getFirmaPerfil() {
		return firmaPerfil;
	}
	public long getFirmaTamany() {
		return firmaTamany;
	}
	public FirmaTipus getFirmaTipus() {
		return firmaTipus;
	}
	public String getNom() {
		return nom;
	}
	public Date getNtiFechaCaptura() {
		return ntiFechaCaptura;
	}
	public NtiOrigen getNtiOrigen() {
		return ntiOrigen;
	}
	public NtiTipoDocumento getNtiTipoDocumental() {
		return ntiTipoDocumental;
	}
	public String getObservacions() {
		return observacions;
	}
	public SicresTipoDocumento getSicresTipoDocumento() {
		return sicresTipoDocumento;
	}
	public SicresValidezDocumento getSicresValidezDocumento() {
		return sicresValidezDocumento;
	}
	public long getTamany() {
		return tamany;
	}
	public String getTipusMime() {
		return tipusMime;
	}
	public String getTitol() {
		return titol;
	}
	public String getUuid() {
		return uuid;
	}
	public RegistreAnnexEstatEnumDto getEstat() {
		return estat;
	}
	public void updateEstat(RegistreAnnexEstatEnumDto estat) {
		this.estat = estat;
	}
	public String getError() {
		return error;
	}
	public void updateError(String error) {
		this.error = StringUtils.abbreviate(
				error,
				4000);
	}
	
	public NtiEstadoElaboracion getNtiEstadoElaboracion() {
		return ntiEstadoElaboracion;
	}
	
	
	
	
}
