/**
 * 
 */
package es.caib.ripea.core.helper;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.dto.AlertaDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.ExecucioMassivaContingutDto;
import es.caib.ripea.core.api.dto.ExecucioMassivaContingutDto.ExecucioMassivaEstatDto;
import es.caib.ripea.core.api.dto.ExecucioMassivaDto;
import es.caib.ripea.core.api.dto.InteressatAdministracioDto;
import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.dto.InteressatPersonaFisicaDto;
import es.caib.ripea.core.api.dto.InteressatPersonaJuridicaDto;
import es.caib.ripea.core.api.dto.RegistreAnnexDetallDto;
import es.caib.ripea.core.entity.AlertaEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.ExecucioMassivaContingutEntity;
import es.caib.ripea.core.entity.InteressatAdministracioEntity;
import es.caib.ripea.core.entity.InteressatEntity;
import es.caib.ripea.core.entity.InteressatPersonaFisicaEntity;
import es.caib.ripea.core.entity.InteressatPersonaJuridicaEntity;
import es.caib.ripea.core.entity.RegistreAnnexEntity;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;

/**
 * Helper per a convertir entre diferents formats de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ConversioTipusHelper {

	private MapperFactory mapperFactory;

	public ConversioTipusHelper() {
		mapperFactory = new DefaultMapperFactory.Builder().build();
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<DateTime, Date>() {
					public Date convert(DateTime source, Type<? extends Date> destinationClass) {
						return source.toDate();
					}
				});
		/*mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<ArxiuEntity, ArxiuDto>() {
					public ArxiuDto convert(ArxiuEntity source, Type<? extends ArxiuDto> destinationClass) {
						ArxiuDto target = new ArxiuDto();
						target.setId(source.getId());
						target.setNom(source.getNom());
						target.setUnitatCodi(source.getUnitatCodi());
						target.setActiu(source.isActiu());
						return target;
					}
				});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<ArxiuEntity, ArxiuDto>() {
					public ArxiuDto convert(ArxiuEntity source, Type<? extends ArxiuDto> destinationClass) {
						ArxiuDto target = new ArxiuDto();
						target.setId(source.getId());
						target.setNom(source.getNom());
						target.setUnitatCodi(source.getUnitatCodi());
						target.setActiu(source.isActiu());
						return target;
					}
				});*/
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<ExecucioMassivaContingutEntity, ExecucioMassivaContingutDto>() {
					public ExecucioMassivaContingutDto convert(ExecucioMassivaContingutEntity source, Type<? extends ExecucioMassivaContingutDto> destinationClass) {
						ExecucioMassivaContingutDto target = new ExecucioMassivaContingutDto();
						target.setDataInici(source.getDataInici());
						target.setDataFi(source.getDataFi());
						target.setEstat(ExecucioMassivaEstatDto.valueOf(source.getEstat().name()));
						target.setError(source.getError());
						target.setOrdre(source.getOrdre());
						target.setExecucioMassiva(convertir(source.getExecucioMassiva(), ExecucioMassivaDto.class));
						if (source.getContingut() instanceof DocumentEntity)
							target.setContingut(convertir((DocumentEntity)source.getContingut(), DocumentDto.class));
						return target;
					}
				});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<AlertaEntity, AlertaDto>() {
					public AlertaDto convert(AlertaEntity source, Type<? extends AlertaDto> destinationClass) {
						AlertaDto target = new AlertaDto();
						target.setId(source.getId());
						target.setText(source.getText());
						target.setError(source.getError());
						target.setLlegida(source.getLlegida().booleanValue());
						target.setContingutId(source.getContingut().getId());
						return target;
					}
				});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<RegistreAnnexEntity, RegistreAnnexDetallDto>() {
					public RegistreAnnexDetallDto convert(RegistreAnnexEntity source, Type<? extends RegistreAnnexDetallDto> destinationClass) {
						RegistreAnnexDetallDto target = new RegistreAnnexDetallDto();
						target.setId(source.getId());
						target.setTitol(source.getTitol());
						target.setFitxerNom(source.getTitol());
						target.setFitxerTamany(source.getFitxerTamany());
						target.setFitxerTipusMime(source.getFitxerTipusMime());
						target.setDataCaptura(source.getDataCaptura());
						target.setLocalitzacio(source.getLocalitzacio());
						if (source.getOrigenCiutadaAdmin() != null)
							target.setOrigenCiutadaAdmin(source.getOrigenCiutadaAdmin().toString());
						if (source.getNtiTipusDocument() != null)
							target.setNtiTipusDocument(source.getNtiTipusDocument().toString());
						if (source.getSicresTipusDocument() != null)
							target.setSicresTipusDocument(source.getSicresTipusDocument().toString());
						if (source.getNtiElaboracioEstat() != null)
							target.setNtiElaboracioEstat(source.getNtiElaboracioEstat().toString());
						target.setObservacions(source.getObservacions());
						target.setFirmaMode(source.getFirmaMode());
						target.setTimestamp(source.getTimestamp());
						target.setValidacioOCSP(source.getValidacioOCSP());
						target.setFitxerArxiuUuid(source.getFitxerArxiuUuid());
						return target;
					}
				});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<InteressatEntity, InteressatDto>() {
					public InteressatDto convert(InteressatEntity source, Type<? extends InteressatDto> destinationClass) {
						InteressatDto target = null;
						if(source instanceof HibernateProxy) {
							HibernateProxy hibernateProxy = (HibernateProxy) source;
							LazyInitializer initializer = hibernateProxy.getHibernateLazyInitializer();
							source = (InteressatEntity)initializer.getImplementation();
						}
						if (source instanceof  InteressatAdministracioEntity) {
							target = new InteressatAdministracioDto();
							((InteressatAdministracioDto)target).setOrganCodi(((InteressatAdministracioEntity)source).getOrganCodi());
						} else if (source instanceof  InteressatPersonaFisicaEntity) {
							target = new InteressatPersonaFisicaDto();
							((InteressatPersonaFisicaDto)target).setNom(((InteressatPersonaFisicaEntity)source).getNom());
							((InteressatPersonaFisicaDto)target).setLlinatge1(((InteressatPersonaFisicaEntity)source).getLlinatge1());
							((InteressatPersonaFisicaDto)target).setLlinatge2(((InteressatPersonaFisicaEntity)source).getLlinatge2());
						} else if (source instanceof  InteressatPersonaJuridicaEntity) {
							target = new InteressatPersonaJuridicaDto();
							((InteressatPersonaJuridicaDto)target).setRaoSocial(((InteressatPersonaJuridicaEntity)source).getRaoSocial());
						} 
						target.setId(source.getId());
						target.setDocumentNum(source.getDocumentNum());
						target.setPais(source.getPais());
						target.setProvincia(source.getProvincia());
						target.setMunicipi(source.getMunicipi());
						target.setAdresa(source.getAdresa());
						target.setCodiPostal(source.getCodiPostal());
						target.setEmail(source.getEmail());
						target.setTelefon(source.getTelefon());
						target.setObservacions(source.getObservacions());
						target.setPreferenciaIdioma(source.getPreferenciaIdioma());
						target.setNotificacioAutoritzat(source.isNotificacioAutoritzat());
						target.setRepresentantId(source.getRepresentantId());
						target.setRepresentantIdentificador(source.getRepresentantIdentificador());
						return target;
					}
				});
	}

	public <T> T convertir(Object source, Class<T> targetType) {
		if (source == null)
			return null;
		return getMapperFacade().map(source, targetType);
	}
	public <T> List<T> convertirList(List<?> items, Class<T> targetType) {
		if (items == null)
			return null;
		return getMapperFacade().mapAsList(items, targetType);
	}
	public <T> Set<T> convertirSet(Set<?> items, Class<T> targetType) {
		if (items == null)
			return null;
		return getMapperFacade().mapAsSet(items, targetType);
	}



	private MapperFacade getMapperFacade() {
		return mapperFactory.getMapperFacade();
	}

}
