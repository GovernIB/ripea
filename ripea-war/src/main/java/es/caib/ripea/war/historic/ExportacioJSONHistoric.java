package es.caib.ripea.war.historic;


import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.historic.HistoricExpedientDto;
import es.caib.ripea.core.api.dto.historic.HistoricInteressatDto;
import es.caib.ripea.core.api.dto.historic.HistoricTipusEnumDto;
import es.caib.ripea.core.api.dto.historic.HistoricUsuariDto;
import es.caib.ripea.war.historic.serializers.DAOHistoric;
import es.caib.ripea.war.historic.serializers.HistoricEntitatSerializer.RootEntitat;
import es.caib.ripea.war.historic.serializers.HistoricInteressatSerializer.RootInteressats;
import es.caib.ripea.war.historic.serializers.HistoricOrganGestorSerializer.RootOrganGestors;
import es.caib.ripea.war.historic.serializers.HistoricUsuariSerializer.RootUsuaris;

public class ExportacioJSONHistoric {
	
	public byte[] convertDadesEntitat(
			List<HistoricExpedientDto> dades, HistoricTipusEnumDto tipusAgrupament) throws JAXBException {
		RootEntitat root = DAOHistoric.mapRegistresEntitat(dades, tipusAgrupament);
		return convertJSON(root);
	}
	
	public byte[] convertDadesOrgansGestors(
			Map<Date, Map<OrganGestorDto, HistoricExpedientDto>> dades, HistoricTipusEnumDto tipusAgrupament) throws JAXBException {
		RootOrganGestors root = DAOHistoric.mapRegistreOrganGestor(dades, tipusAgrupament);
		return convertJSON(root);
	}

	public byte[] convertDadesUsuaris(Map<String, List<HistoricUsuariDto>> dades, HistoricTipusEnumDto tipusAgrupament) throws JAXBException {
		RootUsuaris root = DAOHistoric.mapRegistresUsuaris(dades, tipusAgrupament);
		return convertJSON(root);
	}

	public byte[] convertDadesInteressats(Map<String, List<HistoricInteressatDto>> dades, HistoricTipusEnumDto tipusAgrupament) throws JAXBException {
		RootInteressats root = DAOHistoric.mapRegistresInteressats(dades, tipusAgrupament);
		return convertJSON(root);
	}
	
	private  byte[]convertJSON(Object dades) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			return mapper.writeValueAsBytes(dades);
		} catch (JsonProcessingException e) {
			return new byte[0];
		}
	}

//
//	public byte[] rawJacksonConversion(Object dades) {
//		// https://www.baeldung.com/jackson-xml-serialization-and-deserialization
//		XmlMapper xmlMapper = new XmlMapper();
//		try {
//			return xmlMapper.writeValueAsBytes(dades);
//		} catch (JsonProcessingException e) {
//			return new byte[0];
//		}
//	}
}
