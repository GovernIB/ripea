package es.caib.ripea.war.historic;

import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.historic.HistoricExpedientDto;
import es.caib.ripea.core.api.dto.historic.HistoricInteressatDto;
import es.caib.ripea.core.api.dto.historic.HistoricTipusEnumDto;
import es.caib.ripea.core.api.dto.historic.HistoricUsuariDto;
import es.caib.ripea.war.historic.serializers.DAOHistoric;
import es.caib.ripea.war.historic.serializers.HistoricEntitatSerializer.RootEntitat;
import es.caib.ripea.war.historic.serializers.HistoricEntitatSerializer.RootEntitatDiari;
import es.caib.ripea.war.historic.serializers.HistoricEntitatSerializer.RootEntitatMensual;
import es.caib.ripea.war.historic.serializers.HistoricInteressatSerializer.RootInteressats;
import es.caib.ripea.war.historic.serializers.HistoricInteressatSerializer.RootInteressatsDiari;
import es.caib.ripea.war.historic.serializers.HistoricInteressatSerializer.RootInteressatsMensual;
import es.caib.ripea.war.historic.serializers.HistoricOrganGestorSerializer.RootOrganGestors;
import es.caib.ripea.war.historic.serializers.HistoricOrganGestorSerializer.RootOrganGestorsDiari;
import es.caib.ripea.war.historic.serializers.HistoricOrganGestorSerializer.RootOrganGestorsMensual;
import es.caib.ripea.war.historic.serializers.HistoricUsuariSerializer.RootUsuaris;
import es.caib.ripea.war.historic.serializers.HistoricUsuariSerializer.RootUsuarisDiari;
import es.caib.ripea.war.historic.serializers.HistoricUsuariSerializer.RootUsuarisMensual;

@Component
public class ExportacioXMLHistoric {

	public byte[] convertDadesEntitat(
			List<HistoricExpedientDto> dades, HistoricTipusEnumDto tipusAgrupament) throws JAXBException {
		RootEntitat root = DAOHistoric.mapRegistresEntitat(dades, tipusAgrupament);
		
		JAXBContext context = JAXBContext.newInstance(tipusAgrupament == HistoricTipusEnumDto.DIARI ? RootEntitatDiari.class : RootEntitatMensual.class);
		return this.getXMLBytes(context, root);
	}
	public byte[] convertDadesOrgansGestors(
			Map<Date, Map<OrganGestorDto, HistoricExpedientDto>> dades, HistoricTipusEnumDto tipusAgrupament) throws JAXBException {
		RootOrganGestors root = DAOHistoric.mapRegistreOrganGestor(dades, tipusAgrupament);
		JAXBContext context = JAXBContext.newInstance(tipusAgrupament == HistoricTipusEnumDto.DIARI ? RootOrganGestorsDiari.class : RootOrganGestorsMensual.class);
		return this.getXMLBytes(context, root);
	}

	public byte[] convertDadesUsuaris(Map<String, List<HistoricUsuariDto>> dades, HistoricTipusEnumDto tipusAgrupament) throws JAXBException {
		RootUsuaris root = DAOHistoric.mapRegistresUsuaris(dades, tipusAgrupament);
		JAXBContext context = JAXBContext.newInstance(tipusAgrupament == HistoricTipusEnumDto.DIARI ? RootUsuarisDiari.class : RootUsuarisMensual.class);
		return this.getXMLBytes(context, root);
	}

	public byte[] convertDadesInteressats(Map<String, List<HistoricInteressatDto>> dades, HistoricTipusEnumDto tipusAgrupament) throws JAXBException {
		RootInteressats root = DAOHistoric.mapRegistresInteressats(dades, tipusAgrupament);
		JAXBContext context = JAXBContext.newInstance(tipusAgrupament == HistoricTipusEnumDto.DIARI ? RootInteressatsDiari.class : RootInteressatsMensual.class);
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
