package es.caib.ripea.service.resourcehelper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.service.intf.dto.PermisosPerExpedientsDto;

/**
 * Tests unitaris per a ExpedientPermisosFiltreHelper: composició del filtre complet per rol i aplicació del
 * prefix de camí que fan servir els llistats d'enviaments (Portafirmes i NOTIB).
 */
class ExpedientPermisosFiltreHelperTest {

	private static final String PREFIX = ExpedientPermisosFiltreHelper.PREFIX_EXPEDIENT;

	private final ExpedientPermisosFiltreHelper helper = new ExpedientPermisosFiltreHelper();

	private static PermisosPerExpedientsDto permisosTotesLesVies() {
		PermisosPerExpedientsDto permisos = new PermisosPerExpedientsDto();
		permisos.setIdsMetaExpedientsPermesos(Arrays.asList(1L, 2L));
		permisos.setIdsOrgansPermesos(Arrays.asList(10L));
		permisos.setIdsMetaExpedientOrganPairsPermesos(Arrays.asList(20L));
		permisos.setIdsOrgansAmbProcedimentsComunsPermesos(Arrays.asList(30L));
		permisos.setIdsProcedimentsComuns(Arrays.asList(40L));
		permisos.setIdsGrupsPermesos(Arrays.asList(50L));
		return permisos;
	}

	@Test
	void filtrePermisos_senseprefix_esLaComposicioDeViesIRestriccions() {
		PermisosPerExpedientsDto permisos = permisosTotesLesVies();

		Filter esperat = FilterBuilder.and(
				helper.filtreVies(permisos),
				helper.filtreRestriccioPermisDirecte(permisos),
				helper.filtreRestriccioOrgans(permisos),
				helper.filtreRestriccioGrups(permisos));

		assertThat(helper.filtrePermisos(permisos, "tothom", "").generate()).isEqualTo(esperat.generate());
	}

	@Test
	void filtrePermisos_ambPrefix_totsElsCampsPassenPerExpedient() {
		PermisosPerExpedientsDto permisos = permisosTotesLesVies();

		String senseprefix = helper.filtrePermisos(permisos, "tothom", "").generate();
		String ambPrefix = helper.filtrePermisos(permisos, "tothom", PREFIX).generate();

		// Mateix filtre amb cada camí precedit de "expedient."
		assertThat(ambPrefix).isEqualTo(afegirPrefix(senseprefix));
		assertThat(ambPrefix).contains(PREFIX + "grup");
		assertThat(ambPrefix).contains("exists(" + PREFIX + "metaexpedientOrganGestorPares");
	}

	@Test
	void filtrePermisos_admin_noAplicaCapViaNiRestriccio() {
		PermisosPerExpedientsDto permisos = new PermisosPerExpedientsDto();
		permisos.setIdsMetaExpedientsPermesos(Arrays.asList(1L, 2L));

		assertThat(helper.filtrePermisos(permisos, "IPA_ADMIN", PREFIX)).isNull();
		assertThat(helper.filtrePermisos(permisos, "IPA_ADMIN_LECTURA", PREFIX)).isNull();
		assertThat(helper.isCapPermis(new PermisosPerExpedientsDto(), "IPA_ADMIN")).isFalse();
	}

	@Test
	void isCapPermis_usuariSensePermisos_noHaDeVeureRes() {
		PermisosPerExpedientsDto permisos = new PermisosPerExpedientsDto();
		permisos.setIdsMetaExpedientsPermesos(Collections.emptyList());

		assertThat(helper.isCapPermis(permisos, "tothom")).isTrue();
	}

	@Test
	void filtrePermisos_usuariSenseGrups_nomesExpedientsSenseGrup() {
		PermisosPerExpedientsDto permisos = new PermisosPerExpedientsDto();
		permisos.setIdsMetaExpedientsPermesos(Arrays.asList(1L));

		String filtre = helper.filtrePermisos(permisos, "tothom", PREFIX).generate();

		assertThat(filtre).contains(PREFIX + "grup is null");
	}

	/** Afegeix el prefix a cada camí de camp del filtre (identificadors seguits de '.', ' IS', ' IN', '!' o ':'). */
	private static String afegirPrefix(String filtre) {
		Pattern camp = Pattern.compile("(?<![\\w.'])(metaExpedient|organGestor|metaexpedientOrganGestorPares|grup)(?=[.\\s!:])");
		Matcher m = camp.matcher(filtre);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, PREFIX + m.group(1));
		}
		m.appendTail(sb);
		return sb.toString();
	}
}
