package es.caib.ripea.persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import es.caib.ripea.service.intf.dto.MultiplicitatEnumDto;

/**
 * Tests de l'equals de MetaNodeEntity i MetaDocumentEntity amb proxies de Hibernate.
 *
 * Quan un tipus de document ja és a la sessió com a proxy (p.ex. creat per la relació LAZY
 * DocumentEntity.metaDocument), Hibernate retorna aquest proxy tant a les relacions EAGER com a les consultes, i
 * proxy.equals(proxy) es delega a objecteReal.equals(proxy). Abans es comparava getClass() i els camps del proxy, i
 * el resultat era false: la validació de l'expedient no trobava el document del tipus obligatori.
 *
 * El proxy se simula amb un mock de Mockito que implementa HibernateProxy: igual que el proxy real, és una subclasse
 * generada amb els camps sense inicialitzar.
 */
class MetaNodeEntityEqualsTest {

	private MetaDocumentEntity metaDocument;

	@BeforeEach
	void setUp() {
		EntitatEntity entitat = EntitatEntity.getBuilder("ENT", "Entitat", null, "00000000T", "A00000000").build();
		ReflectionTestUtils.setField(entitat, "id", 1L);
		metaDocument = MetaDocumentEntity.getBuilder(
				entitat,
				"JUST_NOTIF",
				"Justificant de notificació",
				MultiplicitatEnumDto.M_1,
				null,
				null,
				null,
				"TD09",
				false,
				null,
				0).build();
		ReflectionTestUtils.setField(metaDocument, "id", 10L);
	}

	@Test
	void equals_ambProxyInicialitzatDeLaMateixaEntitat_retornaTrue() {
		MetaDocumentEntity proxy = crearProxy(10L, false, metaDocument);

		assertThat(metaDocument.equals(proxy)).isTrue();
	}

	@Test
	void equals_ambProxyInicialitzatDUnaAltraEntitat_retornaFalse() {
		MetaDocumentEntity altre = MetaDocumentEntity.getBuilder(
				metaDocument.getEntitat(),
				"ALTRE",
				"Altre tipus",
				MultiplicitatEnumDto.M_0_N,
				null,
				null,
				null,
				"TD99",
				false,
				null,
				1).build();
		ReflectionTestUtils.setField(altre, "id", 11L);
		MetaDocumentEntity proxy = crearProxy(11L, false, altre);

		assertThat(metaDocument.equals(proxy)).isFalse();
	}

	@Test
	void equals_ambProxySenseInicialitzarDeLaMateixaEntitat_comparaPerId() {
		MetaDocumentEntity proxy = crearProxy(10L, true, null);

		assertThat(metaDocument.equals(proxy)).isTrue();
	}

	@Test
	void equals_ambProxySenseInicialitzarDUnaAltraEntitat_retornaFalse() {
		MetaDocumentEntity proxy = crearProxy(11L, true, null);

		assertThat(metaDocument.equals(proxy)).isFalse();
	}

	private MetaDocumentEntity crearProxy(Long id, boolean senseInicialitzar, MetaDocumentEntity implementacio) {
		MetaDocumentEntity proxy = mock(
				MetaDocumentEntity.class,
				withSettings().extraInterfaces(HibernateProxy.class));
		LazyInitializer lazyInitializer = mock(LazyInitializer.class);
		when(((HibernateProxy) proxy).getHibernateLazyInitializer()).thenReturn(lazyInitializer);
		when(lazyInitializer.isUninitialized()).thenReturn(senseInicialitzar);
		when(lazyInitializer.getImplementation()).thenReturn(implementacio);
		when(proxy.getId()).thenReturn(id);
		return proxy;
	}

}
