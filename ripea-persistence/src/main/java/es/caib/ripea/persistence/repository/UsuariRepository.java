package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.UsuariEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UsuariRepository extends JpaRepository<UsuariEntity, String> {
	
	public UsuariEntity findByCodi(String codi);

	public UsuariEntity findByNif(String nif);

	public List<UsuariEntity> findByProcediment(MetaExpedientEntity procediment);

	@Query(   "select "
			+ "    u "
			+ "from "
			+ "    UsuariEntity u "
			+ "where "
			+ "    lower(u.nom) like concat('%', lower(?1), '%') "
			+ "order by "
			+ "    u.nom desc")
	public List<UsuariEntity> findByText(String text);
}