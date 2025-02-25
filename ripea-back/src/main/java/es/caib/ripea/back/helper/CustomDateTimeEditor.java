package es.caib.ripea.back.helper;

import java.beans.PropertyEditorSupport;
import java.util.Calendar;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class CustomDateTimeEditor extends PropertyEditorSupport {

	private final DateTimeFormatter formatter;

	public CustomDateTimeEditor(String format) {
		this.formatter = DateTimeFormat.forPattern(format);
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(formatter.parseDateTime(text).toDate());
        LocalDateTime localDateTime = LocalDateTime.fromCalendarFields(cal);
        setValue(localDateTime);
	}

	@Override
	public String getAsText() {
        LocalDateTime value = (LocalDateTime) getValue();
        return (value != null ? value.toString(formatter) : "");
	}

}