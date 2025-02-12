/**
 * 
 */
package es.caib.ripea.persistence.entity;

import es.caib.ripea.service.intf.config.BaseConfig;
import lombok.Getter;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Set;

/**
 * Classe del model de dades que representa una dada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity @Getter
@Table(	name = BaseConfig.DB_PREFIX + "portafirmes_block")
@EntityListeners(AuditingEntityListener.class)
public class PortafirmesBlockEntity extends RipeaAuditable<Long> {

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "document_enviament_id")
	@ForeignKey(name = BaseConfig.DB_PREFIX + "signblk_docenvdoc_fk")
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
