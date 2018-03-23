/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.ripea.core.entity.ContingutMovimentEmailEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus ContingutMoviment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ContingutMovimentEmailRepository extends JpaRepository<ContingutMovimentEmailEntity, Long> {

	public List<ContingutMovimentEmailEntity> findByEnviamentAgrupatFalseOrderByDestinatariAscBustiaAsc();
	public List<ContingutMovimentEmailEntity> findByEnviamentAgrupatTrueOrderByDestinatariAscBustiaAsc();
}
