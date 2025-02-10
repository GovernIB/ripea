package es.caib.ripea.service.historic;

import es.caib.ripea.service.intf.dto.OrganGestorDto;
import es.caib.ripea.service.intf.dto.historic.HistoricExpedientDto;
import es.caib.ripea.service.intf.dto.historic.HistoricInteressatDto;
import es.caib.ripea.service.intf.dto.historic.HistoricTipusEnumDto;
import es.caib.ripea.service.intf.dto.historic.HistoricUsuariDto;
import es.caib.ripea.service.intf.dto.historic.serializer.HistoricEntitatSerializer.RootEntitat;
import es.caib.ripea.service.intf.dto.historic.serializer.HistoricEntitatSerializer.RootEntitatDiari;
import es.caib.ripea.service.intf.dto.historic.serializer.HistoricEntitatSerializer.RootEntitatMensual;
import es.caib.ripea.service.intf.dto.historic.serializer.HistoricInteressatSerializer.RootInteressats;
import es.caib.ripea.service.intf.dto.historic.serializer.HistoricInteressatSerializer.RootInteressatsDiari;
import es.caib.ripea.service.intf.dto.historic.serializer.HistoricInteressatSerializer.RootInteressatsMensual;
import es.caib.ripea.service.intf.dto.historic.serializer.HistoricOrganGestorSerializer.RootOrganGestors;
import es.caib.ripea.service.intf.dto.historic.serializer.HistoricOrganGestorSerializer.RootOrganGestorsDiari;
import es.caib.ripea.service.intf.dto.historic.serializer.HistoricOrganGestorSerializer.RootOrganGestorsMensual;
import es.caib.ripea.service.intf.dto.historic.serializer.HistoricUsuariSerializer;
import es.caib.ripea.service.intf.dto.historic.serializer.HistoricUsuariSerializer.RootUsuaris;
import es.caib.ripea.service.intf.dto.historic.serializer.HistoricUsuariSerializer.RootUsuarisMensual;
import es.caib.ripea.service.serializers.DAOHistoric;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
		JAXBContext context = JAXBContext.newInstance(tipusAgrupament == HistoricTipusEnumDto.DIARI ? HistoricUsuariSerializer.RootUsuarisDiari.class : RootUsuarisMensual.class);
		return this.getXMLBytes(context, root);
	}

	public byte[] convertDadesInteressats(Map<String, List<HistoricInteressatDto>> dades, HistoricTipusEnumDto tipusAgrupament) throws JAXBException {
		RootInteressats root = DAOHistoric.mapRegistresInteressats(dades, tipusAgrupament);
		JAXBContext context = JAXBContext.newInstance(tipusAgrupament == HistoricTipusEnumDto.DIARI ? RootInteressatsDiari.class : RootInteressatsMensual.class);
		return this.getXMLBytes(context, root);
	}


	private byte[] getXMLBytes(JAXBContext context, Object root) throws JAXBException {
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		StringWriter sw = new StringWriter();
		m.marshal(root, sw);
		return sw.toString().getBytes();
	}


}
