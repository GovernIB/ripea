/**
 *
 */
package es.caib.ripea.service.config;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * Configuració de cache.
 *
 * Totes les caches es creen dinàmicament amb el nom de l'anotació
 * ({@code @Cacheable}, {@code @CacheEvict}) i comparteixen la mateixa caducitat
 * i mida màxima, excepte les de CACHES_UNA_SETMANA.
 *
 * La caducitat és per entrada (per clau) i es compta des que s'hi guarda el
 * valor: llegir-la no l'allarga.
 *
 * @author Limit Tecnologies
 */
@Configuration
@EnableCaching
public class CacheConfig {

	public static final String ACL_CACHE_NAME = "aclCache";

	private static final long MIDA_MAXIMA = 200;
	private static final long CADUCITAT_MINUTS = 12 * 60;
	private static final long CADUCITAT_SETMANA_DIES = 7;

	/** Caches d'organigrama i rols, que canvien poc i són costoses de recalcular. */
	private static final List<String> CACHES_UNA_SETMANA = Arrays.asList(
			"organismes",
			"descendents",
			"organigrama",
			"codisOrgansFills",
			"organCodisAncestors",
			"rolsDisponiblesEnAcls");

	@Bean
	public CacheManager cacheManager() {
		CaffeineCacheManager cacheManager = new CaffeineCacheManager();
		cacheManager.setCaffeine(Caffeine.newBuilder()
				.expireAfterWrite(CADUCITAT_MINUTS, TimeUnit.MINUTES)
				.maximumSize(MIDA_MAXIMA));
		for (String cacheName: CACHES_UNA_SETMANA) {
			cacheManager.registerCustomCache(
					cacheName,
					Caffeine.newBuilder()
							.expireAfterWrite(CADUCITAT_SETMANA_DIES, TimeUnit.DAYS)
							.maximumSize(MIDA_MAXIMA)
							.build());
		}
		return cacheManager;
	}

}
