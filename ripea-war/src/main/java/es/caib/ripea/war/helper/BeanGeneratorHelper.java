/**
 * 
 */
package es.caib.ripea.war.helper;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.dto.DadaDto;
import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.dto.MetaDadaTipusEnumDto;
import es.caib.ripea.core.api.dto.MultiplicitatEnumDto;
import es.caib.ripea.core.api.service.MetaDadaService;
import es.caib.ripea.core.entity.DadaEntity;
import net.sf.cglib.beans.BeanGenerator;

/**
 * Utilitat per a generar objectes mitjançant cglib.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class BeanGeneratorHelper {

	@Autowired
	private MetaDadaService metaDadaService;



	public Object generarCommandDadesNode(
			Long entitatId,
			Long nodeId,
			List<DadaDto> dades) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		List<MetaDadaDto> metaDades = metaDadaService.findByNode(
				entitatId,
				nodeId);
		String[] noms = new String[metaDades.size()];
		Class<?>[] tipus = new Class<?>[metaDades.size()];
		Object[] valors = (dades != null) ? new Object[metaDades.size()] : null;
		for (int i = 0; i < metaDades.size(); i++) {
			MetaDadaDto metaDada = metaDades.get(i);
			noms[i] = metaDada.getCodi();
			boolean isMultiple = (MultiplicitatEnumDto.M_0_N.equals(metaDada.getMultiplicitat()) || MultiplicitatEnumDto.M_1_N.equals(metaDada.getMultiplicitat()));
			List<Object> dadaValors = new ArrayList<Object>();
			if (dades != null) {
				for (DadaDto dada: dades) {
					if (noms[i].equals(dada.getMetaDada().getCodi())) {
						dadaValors.add(dada.getValor());
					}
				}
			}
			
			
			switch (metaDada.getTipus()) {
			case BOOLEA:
				if (dadaValors.isEmpty() && metaDada.getValorBoolea() != null ) {
					dadaValors.add(metaDada.getValorBoolea());
				}
				
				tipus[i] = (isMultiple) ? Boolean[].class : Boolean.class;
				if (valors != null) {
					if (isMultiple) 
						valors[i] = dadaValors.toArray(new Boolean[dadaValors.size()]);
					else
						valors[i] = (dadaValors.size() > 0) ? dadaValors.get(0) : null;
				}
				break;
			case DATA:
				if (dadaValors.isEmpty() && metaDada.getValorData() != null ) {
					dadaValors.add(metaDada.getValorData());
				}
				
				tipus[i] = (isMultiple) ? Date[].class : Date.class;
				if (valors != null) {
					if (isMultiple) 
						valors[i] = dadaValors.toArray(new Date[dadaValors.size()]);
					else
						valors[i] = (dadaValors.size() > 0) ? dadaValors.get(0) : null;
				}
				break;
			case FLOTANT:
				if (dadaValors.isEmpty() && metaDada.getValorFlotant() != null ) {
					dadaValors.add(metaDada.getValorFlotant());
				}
				
				tipus[i] = (isMultiple) ? Double[].class : Double.class;
				if (valors != null) {
					if (isMultiple) 
						valors[i] = dadaValors.toArray(new Double[dadaValors.size()]);
					else
						valors[i] = (dadaValors.size() > 0) ? dadaValors.get(0) : null;
				}
				break;
			case IMPORT:
				if (dadaValors.isEmpty() && metaDada.getValorImport() != null ) {
					dadaValors.add(metaDada.getValorImport());
				}
				tipus[i] = (isMultiple) ? BigDecimal[].class : BigDecimal.class;
				if (valors != null) {
					if (isMultiple) 
						valors[i] = dadaValors.toArray(new BigDecimal[dadaValors.size()]);
					else
						valors[i] = (dadaValors.size() > 0) ? dadaValors.get(0) : null;
				}
				break;
			case SENCER:
				if (dadaValors.isEmpty() && metaDada.getValorSencer() != null ) {
					dadaValors.add(metaDada.getValorSencer());
				}
				tipus[i] = (isMultiple) ? Long[].class : Long.class;
				if (valors != null) {
					if (isMultiple) 
						valors[i] = dadaValors.toArray(new Long[dadaValors.size()]);
					else
						valors[i] = (dadaValors.size() > 0) ? dadaValors.get(0) : null;
				}
				break;
			case TEXT:
				if (dadaValors.isEmpty() && metaDada.getValorString() != null && !metaDada.getValorString().isEmpty()) {
					dadaValors.add(metaDada.getValorString());
				}
				tipus[i] = (isMultiple) ? String[].class : String.class;
				if (valors != null) {
					if (isMultiple) 
						valors[i] = dadaValors.toArray(new String[dadaValors.size()]);
					else
						valors[i] = (dadaValors.size() > 0) ? dadaValors.get(0) : null;
				}
				break;
			default:
				tipus[i] = (isMultiple) ? String[].class : String.class;
				if (valors != null) {
					if (isMultiple) 
						valors[i] = dadaValors.toArray(new String[dadaValors.size()]);
					else
						valors[i] = (dadaValors.size() > 0) ? dadaValors.get(0) : null;
				}
				break;
			}
			
		}
		return generarCommand(noms, tipus, valors);
	}

	public Object generarCommandDadaNode(
			Long entitatId,
			Long nodeId,
			DadaDto dada) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {		
		String nom;
		Class<?> tipus;
		Object valor = (dada != null) ? new Object() : null;

		MetaDadaDto metaDada = metaDadaService.findById(
				entitatId, 
				nodeId, 
				dada.getMetaDada().getId());
		nom = metaDada.getCodi();
		boolean isMultiple = (MultiplicitatEnumDto.M_0_N.equals(metaDada.getMultiplicitat()) || MultiplicitatEnumDto.M_1_N.equals(metaDada.getMultiplicitat()));
		Object dadaValor = new Object();
		if (dada != null) {
			if (nom.equals(dada.getMetaDada().getCodi())) {
				dadaValor = dada.getValor();
			}
		}
		switch (metaDada.getTipus()) {
			case BOOLEA:
				tipus = (isMultiple) ? Boolean[].class : Boolean.class;
				if (valor != null) {
					valor = (dadaValor != null ) ? dadaValor : null;
				}
			break;
			case DATA:
				tipus = (isMultiple) ? Date[].class : Date.class;
				if (valor != null) {
					valor = (dadaValor != null) ? dadaValor : null;
				}
			break;
			case FLOTANT:
				tipus = (isMultiple) ? Double[].class : Double.class;
				if (valor != null) {
					valor = (dadaValor != null) ? dadaValor : null;
				}
				break;
			case IMPORT:
				tipus = (isMultiple) ? BigDecimal[].class : BigDecimal.class;
				if (valor != null) {
					valor = (dadaValor != null) ? dadaValor : null;
				}
				break;
			case SENCER:
				tipus = (isMultiple) ? Long[].class : Long.class;
				if (valor != null) {
					valor = (dadaValor != null) ? dadaValor : null;
				}
				break;
			case TEXT:
				tipus = (isMultiple) ? String[].class : String.class;
				if (valor != null) {
					valor = (dadaValor != null) ? dadaValor : null;
				}
				break;
			default:
				tipus = (isMultiple) ? String[].class : String.class;
				if (valor != null) {
					valor = (dadaValor != null) ? dadaValor : null;
				}
				break;
			}
			
		return generarCommandUnic(nom, tipus, valor);
	}
	private Object generarCommand(
			String[] noms,
			Class<?>[] tipus,
			Object[] valors) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		BeanGenerator bg = new BeanGenerator();
		for (int i = 0; i < noms.length; i++) {
			bg.addProperty(
					noms[i],
					tipus[i]);
		}
		Object command = bg.create();
		if (valors != null) {
			for (int i = 0; i < noms.length; i++) {
				PropertyUtils.setSimpleProperty(
						command,
						noms[i],
						valors[i]);
			}
		}
		return command;
	}
	
	private Object generarCommandUnic(
			String nom,
			Class<?> tipus,
			Object valor) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		BeanGenerator bg = new BeanGenerator();
		bg.addProperty(
					nom,
					tipus);
		
		Object command = bg.create();
		if (valor != null) {
			PropertyUtils.setSimpleProperty(
						command,
						nom,
						valor);
		}
		
		return command;
	}

}
