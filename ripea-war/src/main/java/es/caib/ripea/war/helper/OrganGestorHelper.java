/**
 * 
 */
package es.caib.ripea.war.helper;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.service.OrganGestorService;

/**
 * Utilitat per a gestionar les entitats de l'usuari actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class OrganGestorHelper {

    private static final String REQUEST_PARAMETER_CANVI_ORGAN = "canviOrganGestor";
    private static final String REQUEST_ATTRIBUTE_ORGANS = "OrganGestorHelper.organGestors";
    private static final String SESSION_ATTRIBUTE_ORGAN_ACTUAL = "OrganGestorHelper.organGestorActual";

    public static void processarCanviOrganGestor(HttpServletRequest request,
                                                 OrganGestorService organGestorService) {
        String canviOrgan = request.getParameter(REQUEST_PARAMETER_CANVI_ORGAN);
        if (canviOrgan != null && canviOrgan.length() > 0) {
            LOGGER.debug("Processant canvi entitat (id=" + canviOrgan + ")");
            try {
                Long canviOGId = new Long(canviOrgan);
                if (canviOGId == -1) {
                    canviOrganGestorActual(request, null);
                } else {
                    List<OrganGestorDto> organs = findOrganGestorsAccessibles(request, organGestorService);
                    for (OrganGestorDto og : organs) {
                        if (canviOGId.equals(og.getId())) {
                            canviOrganGestorActual(request, og);
                        }
                    }
                }

            } catch (NumberFormatException ignored) {
            }
        }
    }

    public static List<OrganGestorDto> findOrganGestorsAccessibles(HttpServletRequest request) {
        return findOrganGestorsAccessibles(request, null);
    }

    @SuppressWarnings("unchecked")
    public static List<OrganGestorDto> findOrganGestorsAccessibles(HttpServletRequest request,
                                                                   OrganGestorService organGestorService) {
        List<OrganGestorDto> organGestors = (List<OrganGestorDto>) request
                .getAttribute(REQUEST_ATTRIBUTE_ORGANS);
        if (organGestors == null && organGestorService != null) {
            EntitatDto entitat = EntitatHelper.getEntitatActual(request);
            organGestors = organGestorService.findAllOrganGestorsAccesibles(entitat.getUnitatArrel());
            organGestors = organGestors == null ? new ArrayList<OrganGestorDto>() : organGestors;
            request.setAttribute(REQUEST_ATTRIBUTE_ORGANS, organGestors);
        }
        return organGestors;
    }

    public static OrganGestorDto getOrganGestorActual(HttpServletRequest request) {
        return getOrganGestorActual(request, null);
    }

    public static OrganGestorDto getOrganGestorActual(HttpServletRequest request,
                                                      OrganGestorService organGestorService) {
        OrganGestorDto organActual = (OrganGestorDto) request.getSession()
                .getAttribute(SESSION_ATTRIBUTE_ORGAN_ACTUAL);
        return organActual;
    }

    public static String getRequestParameterCanviOrganGestor() {
        return REQUEST_PARAMETER_CANVI_ORGAN;
    }

    private static void canviOrganGestorActual(HttpServletRequest request, OrganGestorDto organActual) {
        request.getSession().setAttribute(SESSION_ATTRIBUTE_ORGAN_ACTUAL, organActual);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganGestorHelper.class);

}
