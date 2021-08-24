package es.caib.ripea.core.helper;

import java.util.Date;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TascaHelper {
	@Autowired
	private ConfigHelper configHelper;

	public boolean shouldNotifyAboutDeadline(Date dataLimit) {
		boolean shouldNotifyAboutDeadline = false;
		if (dataLimit != null) {
			int preavisDataLimitEnDies = configHelper.getAsInt("es.caib.ripea.tasca.preavisDataLimitEnDies");
			if ((new Date()).after(new DateTime(dataLimit).minusDays(preavisDataLimitEnDies).toDate())) {
				shouldNotifyAboutDeadline = true;
			}
		}
		return shouldNotifyAboutDeadline;
	}

}
