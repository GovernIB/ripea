/**
 * 
 */
package es.caib.ripea.core.entity;

import es.caib.distribucio.rest.client.domini.FirmaPerfil;
import es.caib.distribucio.rest.client.domini.FirmaTipus;
import es.caib.distribucio.rest.client.domini.NtiEstadoElaboracion;
import es.caib.distribucio.rest.client.domini.NtiOrigen;
import es.caib.distribucio.rest.client.domini.NtiTipoDocumento;
import es.caib.distribucio.rest.client.domini.SicresTipoDocumento;
import es.caib.distribucio.rest.client.domini.SicresValidezDocumento;
import es.caib.ripea.core.api.dto.ArxiuEstatEnumDto;
import es.caib.ripea.core.api.dto.RegistreAnnexEstatEnumDto;
import es.caib.ripea.core.audit.RipeaAuditable;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * Classe del model de dades que representa un document
 * d'una anotació al registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Getter
@Table(	name = "ipa_registre_annex")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class RegistreAnnexEntity extends RipeaAuditable<Long> {

//	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "contingut")
	private byte[] contingut;
//	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "firma_contingut")
	private byte[] firmaContingut;
	@Enumerated(EnumType.STRING)
	@Column(name = "firma_perfil", length = 20)
	private FirmaPerfil firmaPerfil;
	@Column(name = "firma_tamany")
	private long firmaTamany;
	@Enumerated(EnumType.STRING)
	@Column(name = "firma_tipus", length = 10)
	private FirmaTipus firmaTipus;
	@Column(name = "firma_nom", length = 80)
	private String firmaNom;
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
	@Column(name = "tipus_mime", length = 255)
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

	@Column(name = "val_ok")
	private boolean validacioCorrecte;
	@Column(name = "val_error")
	private String validacioError;
	@Enumerated(EnumType.STRING)
	@Column(name = "annex_estat")
	private ArxiuEstatEnumDto annexEstat;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "registre_id")
	@ForeignKey(name = "ipa_annex_registre_fk")
	private RegistreEntity registre;
	
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "document_id")
	@ForeignKey(name = "ipa_annex_document_fk")
	private DocumentEntity document;
	
	
	public static Builder getBuilder(
			 String nom,
			 Date ntiFechaCaptura,
			 NtiOrigen ntiOrigen,
			 NtiTipoDocumento ntiTipoDocumental,
			 SicresTipoDocumento sicresTipoDocumento,
			 String titol,
			 RegistreEntity registre,
			 NtiEstadoElaboracion ntiEstadoElaboracion, 
			 long tamany) {
		return new Builder(
				 nom,
				 ntiFechaCaptura,
				 ntiOrigen,
				 ntiTipoDocumental,
				 sicresTipoDocumento,
				 titol,
				 registre,
				 ntiEstadoElaboracion, 
				 tamany);
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
				 NtiEstadoElaboracion ntiEstadoElaboracion, 
				 long tamany) {
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
			built.validacioCorrecte = true;
			built.tamany = tamany;
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
		public Builder firmaNom(String firmaNom) {
			built.firmaNom = firmaNom;
			return this;
		}
		public Builder validacioCorrecte(Boolean validacioCorrecte) {
			built.validacioCorrecte = validacioCorrecte != null ? validacioCorrecte : true;
			return this;
		}
		public Builder validacioError(String validacioError) {
			built.validacioError = validacioError;
			return this;
		}
		public Builder annexEstat(ArxiuEstatEnumDto annexEstat) {
			built.annexEstat = annexEstat;
			return this;
		}
		public RegistreAnnexEntity build() {
			return built;
		}
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
	public void updateEstat(RegistreAnnexEstatEnumDto estat) {
		this.estat = estat;
	}
	public void updateError(String error) {
		this.error = StringUtils.abbreviate(
				error,
				1000);
	}
	public void updateDocument(DocumentEntity document) {
		this.document = document;
	}
	public void updateAnnexEstat(ArxiuEstatEnumDto annexEstat) {
		this.annexEstat = annexEstat;
	}
	
	
	
	
}
