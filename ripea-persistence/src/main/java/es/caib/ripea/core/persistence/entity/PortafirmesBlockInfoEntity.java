/**
 * 
 */
package es.caib.ripea.core.persistence.entity;

import es.caib.ripea.service.intf.config.BaseConfig;
import lombok.Getter;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * Classe del model de dades que representa una firma d'un bloc de firmes
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity @Getter
@Table(	name = BaseConfig.DB_PREFIX + "portafirmes_block_info")
@EntityListeners(AuditingEntityListener.class)
public class PortafirmesBlockInfoEntity extends RipeaAuditable<Long> {

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "portafirmes_block_id")
	@ForeignKey(name = BaseConfig.DB_PREFIX + "signinfo_signblk_fk")
	private PortafirmesBlockEntity portafirmesBlock;
	
	@Column(name = "portafirmes_signer_nom", length = 50)
	protected String signerNom;
	
	@Column(name = "portafirmes_signer_codi", length = 50)
	protected String signerCodi;
	
	@Column(name = "portafirmes_signer_id", length = 9)
	protected String signerId;
	
	@Column(name = "signed")
	protected boolean signed = false;
	
	@Column(name = "sign_date")
	protected Date data;
	
	@Version
	private long version = 0;
	
	public void updateSigned(
			boolean signed) {
		this.signed = signed;
	}
	
	public void updateSignDate(Date signDate) {
		this.data = signDate;
	}
	/**
	 * Obté el Builder per a crear objectes de tipus signatura-info
	 * 
	 * @param signaturaBlock
	 *            El block al qual pertany aquesta firma
	 * @param signerCodi
	 *            El codi d'usuari al qual pertany aquesta firma
	 * @param signerId
	 *            El id (administrationId) de l'usuari al qual pertany aquesta firma
	 * @param signed
	 *            Valor per indicar si la firma ja s'ha fet
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(
			PortafirmesBlockEntity signaturaBlock,
			String signerNom,
			String signerCodi,
			String signerId,
			boolean signed) {
		return new Builder(
				signaturaBlock,
				signerNom,
				signerCodi,
				signerId,
				signed);
	}

	/**
	 * Builder per a crear noves instàncies d'aquesta classe.
	 * 
	 * @author Limit Tecnologies <limit@limit.es>
	 */
	public static class Builder {
		PortafirmesBlockInfoEntity built;
		Builder(
				PortafirmesBlockEntity portafirmesBlock,
				String signerNom,
				String signerCodi,
				String signerId,
				boolean signed) {
			built = new PortafirmesBlockInfoEntity();
			built.portafirmesBlock = portafirmesBlock;
			built.signerNom = signerNom;
			built.signerCodi = signerCodi;
			built.signerId = signerId;
			built.signed = signed;
		}
		public PortafirmesBlockInfoEntity build() {
			return built;
		}
	}
	
	private static final long serialVersionUID = -8211033276707193649L;

}
