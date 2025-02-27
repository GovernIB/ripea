package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.PortafirmesBlockEntity;
import es.caib.ripea.persistence.entity.PortafirmesBlockInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface PortafirmesBlockInfoRepository extends JpaRepository<PortafirmesBlockInfoEntity, Long> {
	PortafirmesBlockInfoEntity findBySignerIdAndPortafirmesBlock(
			String signerId,
			PortafirmesBlockEntity portafirmesBlock);
	PortafirmesBlockInfoEntity findBySignerCodi(String signerCodi);
}