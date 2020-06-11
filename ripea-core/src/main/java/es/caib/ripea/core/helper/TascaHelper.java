package es.caib.ripea.core.helper;

import java.util.Date;

import org.joda.time.DateTime;

public class TascaHelper {
	
	public static boolean shouldNotifyAboutDeadline(Date dataLimit) {
		boolean shouldNotifyAboutDeadline = false;
		if (dataLimit != null) {
			String preavisDataLimitEnDiesProp = PropertiesHelper.getProperties().getProperty("es.caib.ripea.tasca.preavisDataLimitEnDies");
			Integer preavisDataLimitEnDies;
			if (preavisDataLimitEnDiesProp != null) {
				preavisDataLimitEnDies = new Integer(preavisDataLimitEnDiesProp).intValue();
			} else {
				preavisDataLimitEnDies = new Integer(2);
			}
			if ((new Date()).after(new DateTime(dataLimit).minusDays(preavisDataLimitEnDies).toDate())) {
				shouldNotifyAboutDeadline = true;
			}
		}
		return shouldNotifyAboutDeadline;
	}

}
