package es.caib.ripea.war.historic.serializers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;

import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.historic.HistoricExpedientDto;
import es.caib.ripea.core.api.dto.historic.HistoricInteressatDto;
import es.caib.ripea.core.api.dto.historic.HistoricUsuariDto;
import es.caib.ripea.war.historic.serializers.HistoricEntitatSerializer.RegistreEntitat;
import es.caib.ripea.war.historic.serializers.HistoricEntitatSerializer.RootEntitat;
import es.caib.ripea.war.historic.serializers.HistoricInteressatSerializer.RegistreInteressat;
import es.caib.ripea.war.historic.serializers.HistoricInteressatSerializer.RegistresInteressat;
import es.caib.ripea.war.historic.serializers.HistoricInteressatSerializer.RootInteressats;
import es.caib.ripea.war.historic.serializers.HistoricOrganGestorSerializer.RegistreOrganGestor;
import es.caib.ripea.war.historic.serializers.HistoricOrganGestorSerializer.RegistresOrganGestor;
import es.caib.ripea.war.historic.serializers.HistoricOrganGestorSerializer.RootOrganGestors;
import es.caib.ripea.war.historic.serializers.HistoricUsuariSerializer.RegistreUsuari;
import es.caib.ripea.war.historic.serializers.HistoricUsuariSerializer.RegistresUsuari;
import es.caib.ripea.war.historic.serializers.HistoricUsuariSerializer.RootUsuaris;

public class DAOHistoric {

	public static RootOrganGestors mapRegistreOrganGestor(Map<Date, Map<OrganGestorDto, HistoricExpedientDto>> dades) {
		List<RegistresOrganGestor> registres = new ArrayList<>();
		for (Date data : dades.keySet()) {
			List<RegistreOrganGestor> regOrgans = new ArrayList<>();
			for (OrganGestorDto organGestor : dades.get(data).keySet()) {
				HistoricExpedientDto historic = dades.get(data).get(organGestor);
				RegistreOrganGestor registre = new RegistreOrganGestor();
				BeanUtils.copyProperties(historic, registre);
				registre.nomOrganGestor = organGestor.getNom() + " - " + organGestor.getCodi();
				regOrgans.add(registre);
			}
			registres.add(new RegistresOrganGestor(data, regOrgans));
		}
		
		return new RootOrganGestors(registres);		
	}
	
	public static List<RegistreOrganGestor> mapRegistresActualsOrganGestors(Map<OrganGestorDto, HistoricExpedientDto> dades) {
		List<RegistreOrganGestor> registres = new ArrayList<>();
		for (OrganGestorDto organGestor : dades.keySet()) {
			HistoricExpedientDto historic = dades.get(organGestor);
			RegistreOrganGestor registre = new RegistreOrganGestor();
			BeanUtils.copyProperties(historic, registre);
			registre.nomOrganGestor = organGestor.getNom() + " - " + organGestor.getCodi();
			registres.add(registre);
		}

		return registres;		
	}
	
	public static RootUsuaris mapRegistresUsuaris(Map<String, List<HistoricUsuariDto>> dades) {
		List<RegistresUsuari> registres = new ArrayList<>();
		for (String codiUser : dades.keySet()) {
			RegistresUsuari regUser = new  RegistresUsuari(codiUser);
			List<HistoricUsuariDto> listHistorics = dades.get(codiUser);
			for (HistoricUsuariDto historic : listHistorics) {
				RegistreUsuari registre = new RegistreUsuari();
				BeanUtils.copyProperties(historic, registre);	
				regUser.addRegistre(registre);
			}
			
			registres.add(regUser);			
		}
		return new RootUsuaris(registres);
	}
	
	public static RootInteressats mapRegistresInteressats(Map<String, List<HistoricInteressatDto>> dades) {
		List<RegistresInteressat> registres = new ArrayList<>();
		for (String docNum : dades.keySet()) {
			RegistresInteressat regUser = new  RegistresInteressat(docNum);
			List<HistoricInteressatDto> listHistorics = dades.get(docNum);
			for (HistoricInteressatDto historic : listHistorics) {
				RegistreInteressat registre = new RegistreInteressat();
				BeanUtils.copyProperties(historic, registre);	
				regUser.addRegistre(registre);
			}
			
			registres.add(regUser);			
		}
		return new RootInteressats(registres);
	}
	
	public static RootEntitat mapRegistresEntitat(List<HistoricExpedientDto> dades) {
		List<RegistreEntitat> registres = new ArrayList<>();
		for (HistoricExpedientDto historic : dades) {
			RegistreEntitat registre = new RegistreEntitat();
			BeanUtils.copyProperties(historic, registre);	
			registres.add(registre);
		}
		
		return new RootEntitat(registres);
	}
}
