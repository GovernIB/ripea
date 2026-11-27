package es.caib.ripea.service.resourcehelper.inici;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.sql.SQLTimeoutException;

import javax.persistence.PersistenceException;
import javax.persistence.QueryTimeoutException;

import org.junit.jupiter.api.Test;

/**
 * Detecció del límit de temps de les consultes de la pantalla d'inici, tal com arriba d'Hibernate, de Spring o del
 * controlador JDBC (Oracle i PostgreSQL), i errors que no ho són.
 */
class ConsultesIniciTest {

	@Test
	void tempsEsgotatJpa() {
		assertThat(ConsultesInici.esTempsEsgotat(new QueryTimeoutException("temps"))).isTrue();
	}

	@Test
	void tempsEsgotatSpring() {
		assertThat(ConsultesInici.esTempsEsgotat(new org.springframework.dao.QueryTimeoutException("temps"))).isTrue();
	}

	@Test
	void tempsEsgotatJdbcDinsUnaAltraExcepcio() {
		RuntimeException error = new IllegalStateException("embolcall",
				new PersistenceException("hibernate", new SQLTimeoutException("ORA-01013")));
		assertThat(ConsultesInici.esTempsEsgotat(error)).isTrue();
	}

	@Test
	void sentenciaCanceladaOracle() {
		SQLException oracle = new SQLException("ORA-01013: user requested cancel of current operation", "72000", 1013);
		assertThat(ConsultesInici.esTempsEsgotat(new PersistenceException(oracle))).isTrue();
	}

	@Test
	void sentenciaCanceladaPostgres() {
		SQLException postgres = new SQLException("canceling statement due to statement timeout", "57014");
		assertThat(ConsultesInici.esTempsEsgotat(new PersistenceException(postgres))).isTrue();
	}

	@Test
	void altresErrorsNoSonTempsEsgotat() {
		assertThat(ConsultesInici.esTempsEsgotat(new IllegalStateException("error"))).isFalse();
		assertThat(ConsultesInici.esTempsEsgotat(new PersistenceException(new SQLException("ORA-00942", "42000", 942)))).isFalse();
		assertThat(ConsultesInici.esTempsEsgotat(null)).isFalse();
	}

}
