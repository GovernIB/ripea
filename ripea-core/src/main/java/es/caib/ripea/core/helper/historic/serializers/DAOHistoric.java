package es.caib.ripea.core.helper.historic.serializers;

import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.historic.HistoricExpedientDto;
import es.caib.ripea.core.api.dto.historic.HistoricInteressatDto;
import es.caib.ripea.core.api.dto.historic.HistoricTipusEnumDto;
import es.caib.ripea.core.api.dto.historic.HistoricUsuariDto;
import es.caib.ripea.core.helper.historic.ExportacioHelper;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricEntitatSerializer.RegistreEntitatDiari;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricEntitatSerializer.RegistreEntitatMensual;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricEntitatSerializer.RootEntitat;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricEntitatSerializer.RootEntitatDiari;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricEntitatSerializer.RootEntitatMensual;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricInteressatSerializer.RegistreInteressatDiari;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricInteressatSerializer.RegistreInteressatMensual;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricInteressatSerializer.RegistresInteressatDiari;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricInteressatSerializer.RegistresInteressatMensual;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricInteressatSerializer.RootInteressats;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricInteressatSerializer.RootInteressatsDiari;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricInteressatSerializer.RootInteressatsMensual;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricOrganGestorSerializer.RegistreOrganGestor;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricOrganGestorSerializer.RegistresOrganGestorDiari;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricOrganGestorSerializer.RegistresOrganGestorMensual;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricOrganGestorSerializer.RootOrganGestors;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricOrganGestorSerializer.RootOrganGestorsDiari;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricOrganGestorSerializer.RootOrganGestorsMensual;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricUsuariSerializer.RegistreUsuariDiari;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricUsuariSerializer.RegistreUsuariMensual;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricUsuariSerializer.RegistresUsuariDiari;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricUsuariSerializer.RegistresUsuariMensual;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricUsuariSerializer.RootUsuaris;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricUsuariSerializer.RootUsuarisDiari;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricUsuariSerializer.RootUsuarisMensual;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DAOHistoric {

	public static RootOrganGestors mapRegistreOrganGestor(Map<Date, Map<OrganGestorDto, HistoricExpedientDto>> dades, HistoricTipusEnumDto tipusAgrupament) {
		
		
		if (tipusAgrupament == HistoricTipusEnumDto.DIARI) {
			List<RegistresOrganGestorDiari> registres = new ArrayList<>();
			for (Date data : dades.keySet()) {
				List<RegistreOrganGestor> regOrgans = new ArrayList<>();
				
				for (OrganGestorDto organGestor : dades.get(data).keySet()) {
					HistoricExpedientDto historic = dades.get(data).get(organGestor);
					RegistreOrganGestor registre = new RegistreOrganGestor();
					BeanUtils.copyProperties(historic, registre);
					registre.nomOrganGestor = organGestor.getNom() + " - " + organGestor.getCodi();
					regOrgans.add(registre);
				}

				registres.add(new RegistresOrganGestorDiari(data, regOrgans));
			}
			
			return new RootOrganGestorsDiari(registres);
		} else {
			
			List<RegistresOrganGestorMensual> registres = new ArrayList<>();
			for (Date data : dades.keySet()) {
				List<RegistreOrganGestor> regOrgans = new ArrayList<>();
				
				for (OrganGestorDto organGestor : dades.get(data).keySet()) {
					HistoricExpedientDto historic = dades.get(data).get(organGestor);
					RegistreOrganGestor registre = new RegistreOrganGestor();
					BeanUtils.copyProperties(historic, registre);
					registre.nomOrganGestor = organGestor.getNom() + " - " + organGestor.getCodi();
					regOrgans.add(registre);
				}

				registres.add(new RegistresOrganGestorMensual(ExportacioHelper.getMesNom(data), regOrgans));
			}
			
			return new RootOrganGestorsMensual(registres);
			
		}

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
	
	public static RootUsuaris mapRegistresUsuaris(Map<String, List<HistoricUsuariDto>> dades, HistoricTipusEnumDto tipusAgrupament) {
		if (tipusAgrupament == HistoricTipusEnumDto.DIARI) {
			List<RegistresUsuariDiari> registres = new ArrayList<>();
			for (String codiUser : dades.keySet()) {
				RegistresUsuariDiari regUser = new RegistresUsuariDiari(codiUser);
				List<HistoricUsuariDto> listHistorics = dades.get(codiUser);
				for (HistoricUsuariDto historic : listHistorics) {
					RegistreUsuariDiari registre = new RegistreUsuariDiari();
					BeanUtils.copyProperties(historic, registre);	
					regUser.addRegistre(registre);
				}
				
				registres.add(regUser);			
			}
			return new RootUsuarisDiari(registres);
		} else {
			List<RegistresUsuariMensual> registres = new ArrayList<>();
			for (String codiUser : dades.keySet()) {
				RegistresUsuariMensual regUser = new  RegistresUsuariMensual(codiUser);
				List<HistoricUsuariDto> listHistorics = dades.get(codiUser);
				for (HistoricUsuariDto historic : listHistorics) {
					RegistreUsuariMensual registre = new RegistreUsuariMensual();
					BeanUtils.copyProperties(historic, registre);	
					registre.setMes(historic.getMesNom());
					regUser.addRegistre(registre);
				}
				registres.add(regUser);			
			}
			return new RootUsuarisMensual(registres);
			
		}
	}
	
	public static RootInteressats mapRegistresInteressats(Map<String, List<HistoricInteressatDto>> dades, HistoricTipusEnumDto tipusAgrupament) {
		if (tipusAgrupament == HistoricTipusEnumDto.DIARI) {
			
			List<RegistresInteressatDiari> registres = new ArrayList<>();
			for (String docNum : dades.keySet()) {
				RegistresInteressatDiari regUser = new RegistresInteressatDiari(docNum);
				List<HistoricInteressatDto> listHistorics = dades.get(docNum);
				for (HistoricInteressatDto historic : listHistorics) {
					RegistreInteressatDiari registre = new RegistreInteressatDiari();
					BeanUtils.copyProperties(historic, registre);	
					regUser.addRegistre(registre);
				}
				
				registres.add(regUser);			
			}
			return new RootInteressatsDiari(registres);
		} else {
			List<RegistresInteressatMensual> registres = new ArrayList<>();
			for (String docNum : dades.keySet()) {
				RegistresInteressatMensual regUser = new  RegistresInteressatMensual(docNum);
				List<HistoricInteressatDto> listHistorics = dades.get(docNum);
				for (HistoricInteressatDto historic : listHistorics) {
					RegistreInteressatMensual registre = new RegistreInteressatMensual();
					BeanUtils.copyProperties(historic, registre);
					registre.setMes(historic.getMesNom());
					regUser.addRegistre(registre);
				}
				
				registres.add(regUser);			
			}
			return new RootInteressatsMensual(registres);
		}
	}
	
	public static RootEntitat mapRegistresEntitat(List<HistoricExpedientDto> dades, HistoricTipusEnumDto tipusAgrupament) {
		
		if (tipusAgrupament == HistoricTipusEnumDto.DIARI) {
			List<RegistreEntitatDiari> registres = new ArrayList<>();
			for (HistoricExpedientDto historic : dades) {
				RegistreEntitatDiari registre = new RegistreEntitatDiari();
				BeanUtils.copyProperties(historic, registre);	
				registres.add(registre);
			}
			return new RootEntitatDiari(registres);
		} else {
			List<RegistreEntitatMensual> registres = new ArrayList<>();
			for (HistoricExpedientDto historic : dades) {
				RegistreEntitatMensual registre = new RegistreEntitatMensual();
				BeanUtils.copyProperties(historic, registre);	
				registre.setMes(historic.getMesNom());
				registres.add(registre);
			}
			return new RootEntitatMensual(registres);
			
		}

	}
	
	
	
	
}