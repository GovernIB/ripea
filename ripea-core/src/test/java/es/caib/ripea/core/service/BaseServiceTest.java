/**
 * 
 */
package es.caib.ripea.core.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.service.EntitatService;
import es.caib.ripea.core.api.service.MetaDadaService;
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.repository.UsuariRepository;
import static org.junit.Assert.fail;
/**
 * Tests per al servei d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseServiceTest {

	protected static final String CODI_UNITAT_ARREL = "00000000T";
	protected static final String CODI_UNITAT_FILLA = "12345678Z";

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private EntitatService entitatService;
	@Autowired
	private MetaDadaService metaDadaService;
	@Autowired
	private MetaDocumentService metaDocumentService;
	@Autowired
	private MetaExpedientService metaExpedientService;

	@Autowired
	private  UsuariRepository usuariRepository;

	@Transactional
	protected void autenticarUsuari(String usuariCodi) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(usuariCodi);
		Authentication authToken = new UsernamePasswordAuthenticationToken(
				userDetails.getUsername(),
				userDetails.getPassword(),
				userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        
        UsuariEntity usuariEntity = usuariRepository.getOne(usuariCodi);
		if (usuariEntity == null) {
			usuariRepository.save(UsuariEntity.getBuilder(usuariCodi,
					usuariCodi,
					"NIF",
					usuariCodi +
							"@gmail.com",
					"CA").build());
		}

	}

	protected void testCreantElements(
			TestAmbElementsCreats test,
			Object... elements) {
		List<Object> elementsCreats = new ArrayList<Object>();
		Long entitatId = null;
		try {
			for (Object element: elements) {
				if (element instanceof EntitatDto) {
					autenticarUsuari("super");
					EntitatDto entitatCreada = entitatService.create((EntitatDto)element);
					entitatId = entitatCreada.getId();
					elementsCreats.add(entitatCreada);
					if (((EntitatDto)element).getPermisos() != null) {
						for (PermisDto permis: ((EntitatDto)element).getPermisos()) {
							entitatService.updatePermisSuper(
									entitatCreada.getId(),
									permis);
						}
					}
				} else {
					autenticarUsuari("admin");
					if (entitatId != null) {
						if (element instanceof MetaExpedientDto) {
							MetaExpedientDto metaExpedientCreat = metaExpedientService.create(entitatId,
									(MetaExpedientDto) element);
							elementsCreats.add(metaExpedientCreat);
							if (((MetaExpedientDto) element).getPermisos() != null) {
								for (PermisDto permis : ((MetaExpedientDto) element).getPermisos()) {
									metaExpedientService.permisUpdate(entitatId,
											metaExpedientCreat.getId(),
											permis);
								}
							}
						} else if (element instanceof MetaDocumentDto) {
							MetaDocumentDto metaDocumentCreat = metaDocumentService.create(entitatId,
									((MetaExpedientDto) elementsCreats.get(1)).getId(),
									(MetaDocumentDto) element,
									null,
									null,
									null);
							elementsCreats.add(metaDocumentCreat);
							if (((MetaDocumentDto) element).getPermisos() != null) {
								for (PermisDto permis : ((MetaDocumentDto) element).getPermisos()) {
									metaExpedientService.permisUpdate(entitatId,
											metaDocumentCreat.getId(),
											permis);
								}
							}
						} else if (element instanceof MetaDadaDto) {
							elementsCreats.add(metaDadaService.create(entitatId,
									((MetaExpedientDto) elementsCreats.get(1)).getId(),
									(MetaDadaDto) element));
						}
					} else {
						throw new RuntimeException("No s'ha especificat cap entitat");
					}
				}
			}
			test.executar(elementsCreats);
		} catch (Exception ex) {
			System.out.println("El test ha produït una excepció:");
			ex.printStackTrace(System.out);
			fail("Test shouldnytthrow exception");
		} finally {
			Collections.reverse(elementsCreats);
			for (Object element: elementsCreats) {
				autenticarUsuari("admin");
				if (element instanceof EntitatDto) {
					autenticarUsuari("super");
					entitatService.delete(
							((EntitatDto)element).getId());
					entitatId = null;
				} else if (element instanceof MetaDadaDto) {
					metaDadaService.delete(
							entitatId,
							null, // TODO
							((MetaDadaDto)element).getId());
				} else if (element instanceof MetaDocumentDto) {
					metaDocumentService.delete(
							entitatId,
							null, // TODO
							((MetaDocumentDto)element).getId());
				} else if (element instanceof MetaExpedientDto) {
					metaExpedientService.delete(
							entitatId,
							((MetaExpedientDto)element).getId());
				}
			}
		}
	}



	abstract class TestAmbElementsCreats {
		public abstract void executar(List<Object> elementsCreats);
	}

}
