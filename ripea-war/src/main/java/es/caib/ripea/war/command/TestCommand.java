/**
 * 
 */
package es.caib.ripea.war.command;

import java.util.Arrays;
import java.util.Date;
import java.util.List;



public class TestCommand {


	private String text;
	
	private Date dataSingle;
	
	private Date [] dates = {
			    new Date(), 
			    new Date()
			 };
	
	private List<Date> datesList = Arrays.asList( new Date(), new Date());
	

	public Date getDataSingle() {
		return dataSingle;
	}

	public void setDataSingle(Date dataSingle) {
		this.dataSingle = dataSingle;
	}

	public String getText() {
		return text;
	}

	public void setText(
			String text) {
		this.text = text;
	}

	public Date[] getDates() {
		return dates;
	}

	public void setDates(
			Date[] dates) {
		this.dates = dates;
	}

	public List<Date> getDatesList() {
		return datesList;
	}

	public void setDatesList(
			List<Date> datesList) {
		this.datesList = datesList;
	}



}
