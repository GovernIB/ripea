package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.MetaExpedientTascaValidacioEntity;
import es.caib.ripea.service.intf.dto.ItemValidacioTascaEnum;
import es.caib.ripea.service.intf.dto.TipusValidacioTascaEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface MetaExpedientTascaValidacioRepository extends JpaRepository<MetaExpedientTascaValidacioEntity, Long> {
	public List<MetaExpedientTascaValidacioEntity> findByMetaExpedientTascaId(Long metaExpedientTascaId);
	public List<MetaExpedientTascaValidacioEntity> findByItemValidacioAndTipusValidacioAndItemIdAndMetaExpedientTascaId(
			ItemValidacioTascaEnum itemValidacio,
			TipusValidacioTascaEnum tipusValidacio,
			Long itemId,
			Long metaExpedientTascaId);
	public List<MetaExpedientTascaValidacioEntity> findByItemValidacioAndItemId(
			ItemValidacioTascaEnum itemValidacio,
			Long itemId);
}