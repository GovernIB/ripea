/**
 * 
 */
package es.caib.ripea.core.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.audit.RipeaAuditable;
import lombok.Getter;

/**
 * Classe del model de dades que representa una dada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity @Getter
@Table(	name = "ipa_portafirmes_block")
@EntityListeners(AuditingEntityListener.class)
public class PortafirmesBlockEntity extends RipeaAuditable<Long> {

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "document_enviament_id")
	@ForeignKey(name = "ipa_signblk_docenvdoc_fk")
	private DocumentPortafirmesEntity enviament;
	
	@Column(name = "blk_order", length = 19, nullable = false)
	protected int order;
	
	@OneToMany(mappedBy = "portafirmesBlock", cascade = CascadeType.ALL)
	protected Set<PortafirmesBlockInfoEntity> signers;
	
	@Version
	private long version = 0;
	
	/**
	 * Obté el Builder per a crear objectes de tipus signatura-block
	 * 
	 * @param enviament
	 *            L'enviament al qual pertany aquest bloc de firmes.
	 * @param order
	 *            L'ordre d'aquest bloc
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(
			DocumentPortafirmesEntity enviament,
			int	order) {
		return new Builder(
				enviament,
				order);
	}

	/**
	 * Builder per a crear noves instàncies d'aquesta classe.
	 * 
	 * @author Limit Tecnologies <limit@limit.es>
	 */
	public static class Builder {
		PortafirmesBlockEntity built;
		Builder(
				DocumentPortafirmesEntity enviament,
				int	order) {
			built = new PortafirmesBlockEntity();
			built.enviament = enviament;
			built.order = order;
		}
		public PortafirmesBlockEntity build() {
			return built;
		}
	}

	private static final long serialVersionUID = 6868728463324827171L;

}
