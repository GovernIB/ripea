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
import es.caib.ripea.core.api.dto.MetaNodeMetaDadaDto;
import es.caib.ripea.core.api.dto.MultiplicitatEnumDto;
import es.caib.ripea.core.api.service.MetaDadaService;
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
		List<MetaNodeMetaDadaDto> contingutMetaDades = metaDadaService.findByNode(
				entitatId,
				nodeId);
		String[] noms = new String[contingutMetaDades.size()];
		Class<?>[] tipus = new Class<?>[contingutMetaDades.size()];
		Object[] valors = (dades != null) ? new Object[contingutMetaDades.size()] : null;
		for (int i = 0; i < contingutMetaDades.size(); i++) {
			MetaNodeMetaDadaDto metaDada = contingutMetaDades.get(i);
			noms[i] = metaDada.getMetaDada().getCodi();
			boolean isMultiple = (MultiplicitatEnumDto.M_0_N.equals(metaDada.getMultiplicitat()) || MultiplicitatEnumDto.M_1_N.equals(metaDada.getMultiplicitat()));
			List<Object> dadaValors = new ArrayList<Object>();
			if (dades != null) {
				for (DadaDto dada: dades) {
					if (noms[i].equals(dada.getMetaDada().getCodi())) {
						dadaValors.add(dada.getValor());
					}
				}
			}
			switch (metaDada.getMetaDada().getTipus()) {
			case BOOLEA:
				tipus[i] = (isMultiple) ? Boolean[].class : Boolean.class;
				if (valors != null) {
					if (isMultiple) 
						valors[i] = dadaValors.toArray(new Boolean[dadaValors.size()]);
					else
						valors[i] = (dadaValors.size() > 0) ? dadaValors.get(0) : null;
				}
				break;
			case DATA:
				tipus[i] = (isMultiple) ? Date[].class : Date.class;
				if (valors != null) {
					if (isMultiple) 
						valors[i] = dadaValors.toArray(new Date[dadaValors.size()]);
					else
						valors[i] = (dadaValors.size() > 0) ? dadaValors.get(0) : null;
				}
				break;
			case FLOTANT:
				tipus[i] = (isMultiple) ? Double[].class : Double.class;
				if (valors != null) {
					if (isMultiple) 
						valors[i] = dadaValors.toArray(new Double[dadaValors.size()]);
					else
						valors[i] = (dadaValors.size() > 0) ? dadaValors.get(0) : null;
				}
				break;
			case IMPORT:
				tipus[i] = (isMultiple) ? BigDecimal[].class : BigDecimal.class;
				if (valors != null) {
					if (isMultiple) 
						valors[i] = dadaValors.toArray(new BigDecimal[dadaValors.size()]);
					else
						valors[i] = (dadaValors.size() > 0) ? dadaValors.get(0) : null;
				}
				break;
			case SENCER:
				tipus[i] = (isMultiple) ? Long[].class : Long.class;
				if (valors != null) {
					if (isMultiple) 
						valors[i] = dadaValors.toArray(new Long[dadaValors.size()]);
					else
						valors[i] = (dadaValors.size() > 0) ? dadaValors.get(0) : null;
				}
				break;
			case TEXT:
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

}
