/**
 * 
 */
package es.caib.ripea.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.api.dto.ConsultaPinbalEstatEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentPinbalServeiEnumDto;
import es.caib.ripea.core.audit.RipeaAuditable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "ipa_consulta_pinbal")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsultaPinbalEntity extends RipeaAuditable<Long> {

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "entitat_id")
	@ForeignKey(name = "ipa_entitat_pinbal_fk")
	protected EntitatEntity entitat;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "servei", length = 64, nullable = false)
	private MetaDocumentPinbalServeiEnumDto servei;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "estat", length = 10, nullable = false)
	private ConsultaPinbalEstatEnumDto estat;
	
	@Column(name = "pinbal_idpeticion", length = 64)
	private String pinbalIdpeticion;
	
	@Column(name = "error", length = 4000)
	private String error;

	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "expedient_id")
	@ForeignKey(name = "ipa_expedient_pinbal_fk")
	private ExpedientEntity expedient;
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "metaexpedient_id")
	@ForeignKey(name = "ipa_metaexp_pinbal_fk")
	private MetaExpedientEntity metaExpedient;
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "document_id")
	@ForeignKey(name = "ipa_document_pinbal_fk")
	private DocumentEntity document;
	


	private static final long serialVersionUID = 1L;
}
