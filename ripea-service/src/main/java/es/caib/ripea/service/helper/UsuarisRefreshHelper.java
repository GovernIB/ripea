package es.caib.ripea.service.helper;

import es.caib.ripea.persistence.entity.resourceentity.UsuariResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.UsuariResourceRepository;
import es.caib.ripea.service.base.helper.AuthenticationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class UsuarisRefreshHelper {

	private final UsuariResourceRepository usuariRepository;
	private final AuthenticationHelper authenticationHelper;
	private final Map<String, LocalDateTime> usuariLastDBRefresh = new ConcurrentHashMap<>();

	private static final int REFRESH_THRESHOLD_MINUTES = 5;
	private static final int SESSION_THRESHOLD_MINUTES = 30;

	public void refreshCurrentUser() {
		String codi = authenticationHelper.getCurrentUserName();
		LocalDateTime now = LocalDateTime.now();

		AtomicBoolean shouldRun = new AtomicBoolean(false);

		usuariLastDBRefresh.compute(codi, (k, lastRefresh) -> {
			if (lastRefresh == null ||
				lastRefresh.plusMinutes(REFRESH_THRESHOLD_MINUTES).isBefore(now)) {
				shouldRun.set(true);
				return now;
			}
			return lastRefresh;
		});

		if (!shouldRun.get()) {
			return;
		}

		Optional<UsuariResourceEntity> usuariEntityOptional = usuariRepository.findById(codi);
		if (usuariEntityOptional.isEmpty()) {
            return;
		}

        UsuariResourceEntity usuariEntity = usuariEntityOptional.get();
        // Session logic
        if (usuariEntity.getDarreraActivitat() != null &&
            usuariEntity.getDarreraActivitat().plusMinutes(SESSION_THRESHOLD_MINUTES).isBefore(now)) {
            // Closed session
            usuariEntity.setDarrerPeriode(usuariEntity.getDarreraActivitat());
        }
		usuariEntity.setDarreraActivitat(now);
		usuariRepository.save(usuariEntity);
	}

}
