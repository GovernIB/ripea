package es.caib.ripea.core.persistence.entity;

import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.ConsultaPinbalEstatEnumDto;
import lombok.*;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "consulta_pinbal")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsultaPinbalEntity extends RipeaAuditable<Long> {

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "entitat_id")
	@ForeignKey(name = BaseConfig.DB_PREFIX + "entitat_pinbal_fk")
	protected EntitatEntity entitat;
	
	@Column(name = "servei", length = 64, nullable = false)
	private String servei;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "estat", length = 10, nullable = false)
	private ConsultaPinbalEstatEnumDto estat;
	
	@Column(name = "pinbal_idpeticion", length = 64)
	private String pinbalIdpeticion;
	
	@Column(name = "error", length = 4000)
	private String error;
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "expedient_id")
	@ForeignKey(name = BaseConfig.DB_PREFIX + "expedient_pinbal_fk")
	private ExpedientEntity expedient;
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "metaexpedient_id")
	@ForeignKey(name = BaseConfig.DB_PREFIX + "metaexp_pinbal_fk")
	private MetaExpedientEntity metaExpedient;
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "document_id")
	@ForeignKey(name = BaseConfig.DB_PREFIX + "document_pinbal_fk")
	private DocumentEntity document;
	
	private static final long serialVersionUID = -7640819122688201637L;
}