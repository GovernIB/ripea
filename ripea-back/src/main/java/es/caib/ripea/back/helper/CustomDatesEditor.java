package es.caib.ripea.back.helper;

import org.springframework.util.StringUtils;

import java.beans.PropertyEditorSupport;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class CustomDatesEditor extends PropertyEditorSupport {

	private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	public CustomDatesEditor() {
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		
		// when converting values to String[] object to pass to the view spring calls method getAsText() of this class 
		// (in this case the method of org.springframework.beans.propertyeditors.CustomDateEditor is not called by spring that's why this class was added)
		// but when converting values to the Date[] object to pass the the model spring calls org.springframework.beans.propertyeditors.CustomDateEditor.setAsText() instead of this method
		// when converting one value to Date[] object to pass to the model spring calls this method
		// when converting one value to String[] object to pass to the view spring calls method getAsText() of this class 
		
		if (!StringUtils.hasText(text)) {
			// Treat empty String as null value.
			setValue(null);
		} else if (text != null && text.length() != 10) {
			throw new IllegalArgumentException(
					"Could not parse date: it is not exactly" + 10 + "characters long");
		} else {
			try {
				setValue(this.dateFormat.parse(text));
			}
			catch (ParseException ex) {
				throw new IllegalArgumentException("Could not parse date: " + ex.getMessage(), ex);
			}
		}

	}

	@Override
	public String getAsText() {
		
		Object valor = getValue();
		String valuesString = "";
		
		if (valor!=null) {
			List<Date> listE = Arrays.asList((Date[]) getValue());
			
			for (int i = 0; i < listE.size(); i++) {
				if (i != 0) {
					valuesString += ",";
				}
				valuesString += this.dateFormat.format(listE.get(i));
			}
		}
		return valuesString;
	}

}