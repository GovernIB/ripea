package es.caib.ripea.back.helper;

import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.service.ExpedientPeticioService;

import javax.servlet.http.HttpServletRequest;

public class AnotacionsPendentsHelper {

    private static final String REQUEST_PARAMETER_ANOTACIONS_PENDENTS_COUNT = "AnotacionsPendentsHelper.countAnotacionsPendents";
    public static final String SESSION_ATTRIBUTE_ROL_ACTUAL = "RolHelper.rol.actual";

    private static ExpedientPeticioService expedientPeticioService = null;

    public static Long countAnotacionsPendents(
            HttpServletRequest request,
            ExpedientPeticioService expedientPeticioService) {
        CounterLifetime counter = (CounterLifetime) request.getSession().getAttribute(REQUEST_PARAMETER_ANOTACIONS_PENDENTS_COUNT);
        Long count = counter != null ? counter.getCounter() : null;

        if (count == null && !RequestHelper.isError(request) && expedientPeticioService != null && (RolHelper.isRolActualUsuari(request) || RolHelper.isRolActualAdministrador(request) || RolHelper.isRolActualAdministradorOrgan(request))) {
            if (AnotacionsPendentsHelper.expedientPeticioService == null) {
                AnotacionsPendentsHelper.expedientPeticioService = expedientPeticioService;
            }
            EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
            String rolActual = (String) request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
            if (entitatActual != null) {
                count = Long.valueOf(expedientPeticioService.countAnotacionsPendents(entitatActual.getId(), rolActual, EntitatHelper.getOrganGestorActualId(request)));
                request.getSession().setAttribute(
                        REQUEST_PARAMETER_ANOTACIONS_PENDENTS_COUNT,
                        new CounterLifetime(
                                count,
                                expedientPeticioService.getPeriodeActualitzacioContadorAnotacionsPendents()
                        )
                );
            }
        }
        return count;
    }

    public static Long countAnotacionsPendents(HttpServletRequest request) {
        CounterLifetime counterLifetime = (CounterLifetime) request.getSession().getAttribute(REQUEST_PARAMETER_ANOTACIONS_PENDENTS_COUNT);
        if (counterLifetime == null || counterLifetime.getCounter() == null) {
            return countAnotacionsPendents(request, expedientPeticioService);
        }
        return counterLifetime.getCounter();
    }

    public static void resetCounterAnotacionsPendents(HttpServletRequest request) {
        request.getSession().removeAttribute(REQUEST_PARAMETER_ANOTACIONS_PENDENTS_COUNT);
    }

    public static class CounterLifetime {
        private Long counter;
        private long lifetime;
        private long maxLifetime;

        public CounterLifetime(long counter, long lifetime) {
            this.counter = counter;
            this.lifetime = lifetime;
            this.maxLifetime = System.currentTimeMillis() + lifetime * 1000;
        }

        public Long getCounter() {
            if (System.currentTimeMillis() < maxLifetime)
                return counter;
            return null;
        }

//        public void updateCounter(long counter) {
//            this.counter = counter;
//            this.maxLifetime = System.currentTimeMillis() + lifetime * 1000;
//        }

    }
}
