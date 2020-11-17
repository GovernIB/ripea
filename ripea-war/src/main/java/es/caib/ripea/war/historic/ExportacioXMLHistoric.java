package es.caib.ripea.war.historic;

import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

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
		JAXBContext context = JAXBContext.newInstance(RootEntitat.class);
		return this.getXMLBytes(context, root);
	}
	public byte[] convertDadesOrgansGestors(
			Map<Date, Map<OrganGestorDto, HistoricExpedientDto>> dades) throws JAXBException {
		RootOrganGestors root = DAOHistoric.mapRegistreOrganGestor(dades);
		JAXBContext context = JAXBContext.newInstance(RootOrganGestors.class);
		return this.getXMLBytes(context, root);
	}

	public byte[] convertDadesUsuaris(Map<String, List<HistoricUsuariDto>> dades) throws JAXBException {
		RootUsuaris root = DAOHistoric.mapRegistresUsuaris(dades);
		JAXBContext context = JAXBContext.newInstance(RootUsuaris.class);
		return this.getXMLBytes(context, root);
	}

	public byte[] convertDadesInteressats(Map<String, List<HistoricInteressatDto>> dades) throws JAXBException {
		RootInteressats root = DAOHistoric.mapRegistresInteressats(dades);
		JAXBContext context = JAXBContext.newInstance(RootInteressats.class);
		return this.getXMLBytes(context, root);
	}

//	private byte[] rawJacksonConversion(Object dades) {
//		XmlMapper xmlMapper = new XmlMapper();
//		xmlMapper.getFactory().getXMLOutputFactory().setProperty("javax.xml.stream.isRepairingNamespaces", false);
//		try {
//			return xmlMapper.writeValueAsBytes(dades);
//		} catch (JsonProcessingException e) {
//			return new byte[0];
//		}
//	}

	private byte[] getXMLBytes(JAXBContext context, Object root) throws JAXBException {
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		StringWriter sw = new StringWriter();
		m.marshal(root, sw);
		return sw.toString().getBytes();
	}


}
