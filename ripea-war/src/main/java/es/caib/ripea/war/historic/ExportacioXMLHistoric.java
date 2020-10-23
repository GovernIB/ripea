package es.caib.ripea.war.historic;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import es.caib.ripea.core.api.dto.HistoricExpedientDto;
import es.caib.ripea.core.api.dto.HistoricInteressatDto;
import es.caib.ripea.core.api.dto.HistoricUsuariDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.war.historic.DAOHistoric.RootEntitat;
import es.caib.ripea.war.historic.DAOHistoric.RootInteressats;
import es.caib.ripea.war.historic.DAOHistoric.RootOrganGestors;
import es.caib.ripea.war.historic.DAOHistoric.RootUsuaris;

@Component
public class ExportacioXMLHistoric {

	public byte[] convertDadesEntitat(
			List<HistoricExpedientDto> dades) throws JAXBException {
		RootEntitat root = DAOHistoric.mapRegistresEntitat(dades);
//		JAXBContext context = JAXBContext.newInstance(RootEntitat.class);
		return this.rawJacksonConversion(root);
	}
	public byte[] convertDadesOrgansGestors(
			Map<Date, Map<OrganGestorDto, HistoricExpedientDto>> dades) throws JAXBException {
		RootOrganGestors root = DAOHistoric.mapRegistreOrganGestor(dades);
		return this.rawJacksonConversion(root);
	}

	public byte[] convertDadesUsuaris(Map<String, List<HistoricUsuariDto>> dades) throws JAXBException {
		RootUsuaris root = DAOHistoric.mapRegistresUsuaris(dades);
		return this.rawJacksonConversion(root);
	}

	public byte[] convertDadesInteressats(Map<String, List<HistoricInteressatDto>> dades) throws JAXBException {
		RootInteressats root = DAOHistoric.mapRegistresInteressats(dades);
		return this.rawJacksonConversion(root);
	}

	private byte[] rawJacksonConversion(Object dades) {
		XmlMapper xmlMapper = new XmlMapper();
		xmlMapper.getFactory().getXMLOutputFactory().setProperty("javax.xml.stream.isRepairingNamespaces", false);
		try {
			return xmlMapper.writeValueAsBytes(dades);
		} catch (JsonProcessingException e) {
			return new byte[0];
		}
	}
//
//	private byte[] getXMLBytes(JAXBContext context, Object root) throws JAXBException {
//		Marshaller m = context.createMarshaller();
//		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//
//		StringWriter sw = new StringWriter();
//		m.marshal(root, sw);
//		return sw.toString().getBytes();
//	}


}
