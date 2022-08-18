/**
 * 
 */
package es.caib.ripea.core.helper;

import es.caib.ripea.core.aggregation.HistoricAggregation;
import es.caib.ripea.core.aggregation.HistoricExpedientAggregation;
import es.caib.ripea.core.aggregation.HistoricUsuariAggregation;
import es.caib.ripea.core.api.dto.*;
import es.caib.ripea.core.api.dto.ExecucioMassivaContingutDto.ExecucioMassivaEstatDto;
import es.caib.ripea.core.api.dto.historic.HistoricExpedientDto;
import es.caib.ripea.core.api.dto.historic.HistoricInteressatDto;
import es.caib.ripea.core.api.dto.historic.HistoricUsuariDto;
import es.caib.ripea.core.entity.*;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Helper per a convertir entre diferents formats de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ConversioTipusHelper {

	private MapperFactory mapperFactory;

	@Autowired
	private ContingutHelper contingutHelper;
	@Autowired
	private ExpedientHelper expedientHelper;
	
	@Autowired
	private TascaHelper tascaHelper;
	public ConversioTipusHelper() {
		mapperFactory = new DefaultMapperFactory.Builder().build();
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<DateTime, Date>() {
					public Date convert(DateTime source, Type<? extends Date> destinationClass) {
						return source.toDate();
					}
				});
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
							target.setDocumentNom(((DocumentEntity)source.getContingut()).getNom());
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
				new CustomConverter<PermisDto, PermisOrganGestorDto>() {
					public PermisOrganGestorDto convert(PermisDto source, Type<? extends PermisOrganGestorDto> destinationClass) {
						PermisOrganGestorDto target = new PermisOrganGestorDto();
						target.setId(source.getId());
						target.setPrincipalNom(source.getPrincipalNom());
						target.setPrincipalCodiNom(source.getPrincipalCodiNom());
						target.setPrincipalTipus(source.getPrincipalTipus());
						target.setRead(source.isRead());
						target.setWrite(source.isWrite());
						target.setCreate(source.isCreate());
						target.setDelete(source.isDelete());
						target.setProcedimentsComuns(source.isProcedimentsComuns());
						target.setAdministration(source.isAdministration());
						target.setAdministrationComuns(source.isAdministrationComuns());
						target.setOrganGestorId(source.getOrganGestorId());
						target.setOrganGestorNom(source.getOrganGestorNom());

						return target;
					}
				});
		
		
//		mapperFactory.getConverterFactory().registerConverter(
//		new CustomConverter<GrupEntity, GrupDto>() {
//			public GrupDto convert(GrupEntity source, Type<? extends GrupDto> destinationClass) {
//				GrupDto target = new GrupDto();
//				target.setId(source.getId());
//				target.setRol(source.getRol());
//				target.setDescripcio(source.getDescripcio());
//
//
//				return target;
//			}
//		});
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<ExpedientTascaEntity, ExpedientTascaDto>() {
					public ExpedientTascaDto convert(ExpedientTascaEntity source, Type<? extends ExpedientTascaDto> destinationClass) {
						ExpedientTascaDto target = new ExpedientTascaDto();
						target.setId(source.getId());
						target.setExpedient((ExpedientDto) contingutHelper.toContingutDto(source.getExpedient()));
						target.setMetaExpedientTasca(convertir(source.getMetaExpedientTasca(), MetaExpedientTascaDto.class));
						target.setResponsables(convertirList(source.getResponsables(), UsuariDto.class));
						target.setResponsableActual(convertir(source.getResponsableActual(), UsuariDto.class));
						target.setDataInici(source.getDataInici());
						target.setDataFi(source.getDataFi());
						target.setEstat(source.getEstat());
						target.setMotiuRebuig(source.getMotiuRebuig());
						target.setCreatedBy(convertir(source.getCreatedBy(), UsuariDto.class));
						target.setDataLimit(source.getDataLimit());
						target.setShouldNotifyAboutDeadline(tascaHelper.shouldNotifyAboutDeadline(source.getDataLimit()));
						target.setNumComentaris(source.getComentaris() == null ? 0L :source.getComentaris().size());
						target.setNumComentaris(source.getComentaris() == null ? 0L :source.getComentaris().size());
						
						Authentication auth = SecurityContextHolder.getContext().getAuthentication();
						boolean usuariActualReposnable = false;
						for (UsuariEntity usuari : source.getResponsables()) {
							if (usuari.getCodi().equals(auth.getName())) {
								usuariActualReposnable = true;
							}
						}
						target.setUsuariActualResponsable(usuariActualReposnable);
						return target;
					}
				});
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<MetaExpedientTascaEntity, MetaExpedientTascaDto>() {
					public MetaExpedientTascaDto convert(MetaExpedientTascaEntity source, Type<? extends MetaExpedientTascaDto> destinationClass) {
						MetaExpedientTascaDto target = new MetaExpedientTascaDto();
						target.setActiva(source.isActiva());
						target.setCodi(source.getCodi());
						target.setDataLimit(source.getDataLimit());
						target.setDescripcio(source.getDescripcio());
						target.setEstatIdCrearTasca(source.getEstatCrearTasca() != null ? source.getEstatCrearTasca().getId() : null);
						target.setEstatNomCrearTasca(source.getEstatCrearTasca() != null ? source.getEstatCrearTasca().getNom() : null);
						target.setEstatColorCrearTasca(source.getEstatCrearTasca() != null ? source.getEstatCrearTasca().getColor() : null);
						target.setEstatIdFinalitzarTasca(source.getEstatFinalitzarTasca() != null ? source.getEstatFinalitzarTasca().getId() : null);
						target.setEstatNomFinalitzarTasca(source.getEstatFinalitzarTasca() != null ? source.getEstatFinalitzarTasca().getNom() : null);
						target.setEstatColorFinalitzarTasca(source.getEstatFinalitzarTasca() != null ? source.getEstatFinalitzarTasca().getColor() : null);
						target.setId(source.getId());
						target.setNom(source.getNom());
						target.setResponsable(source.getResponsable());
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
							((InteressatAdministracioDto)target).setOrganNom(((InteressatAdministracioEntity)source).getOrganNom());
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
						target.setDocumentTipus(source.getDocumentTipus());
						target.setPais(source.getPais());
						target.setProvincia(source.getProvincia());
						target.setMunicipi(source.getMunicipi());
						target.setAdresa(source.getAdresa());
						target.setCodiPostal(source.getCodiPostal());
						target.setEmail(source.getEmail());
						target.setTelefon(source.getTelefon());
						target.setObservacions(source.getObservacions());
						target.setPreferenciaIdioma(source.getPreferenciaIdioma());
						target.setRepresentantId(source.getRepresentantId());
						target.setIdentificador(source.getIdentificador());
						target.setRepresentantIdentificador(source.getRepresentantIdentificador());
						target.setEntregaDeh(source.getEntregaDeh());
						target.setEntregaDehObligat(source.getEntregaDehObligat());
						target.setIncapacitat(source.getIncapacitat());
						target.setRepresentant(source.getRepresentant() != null ? convertir(source.getRepresentant(),InteressatDto.class) : null);
						target.setArxiuPropagat(source.isArxiuPropagat());
						target.setRepresentantArxiuPropagat(source.getRepresentant() != null ? source.getRepresentant().isArxiuPropagat() : true);
						target.setExpedientArxiuPropagat(source.getExpedient().getArxiuUuid() != null);
						return target;
					}
				});
		
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<MetaDadaEntity, MetaDadaDto>() {
					public MetaDadaDto convert(MetaDadaEntity source, Type<? extends MetaDadaDto> destinationClass) {
						MetaDadaDto target = new MetaDadaDto();
						target.setId(source.getId());
						target.setCodi(source.getCodi());
						target.setNom(source.getNom());
						target.setTipus(source.getTipus());
						target.setDescripcio(source.getDescripcio());
						target.setMultiplicitat(source.getMultiplicitat());
						target.setReadOnly(source.isReadOnly());
						target.setOrdre(source.getOrdre());
						target.setActiva(source.isActiva());
						
						if (source.getTipus()==MetaDadaTipusEnumDto.BOOLEA) {
							target.setValorBoolea((Boolean) DadaEntity.getDadaValorPerRetornar(source, source.getValor()));
						} else if (source.getTipus()==MetaDadaTipusEnumDto.DATA) {
							target.setValorData((Date) DadaEntity.getDadaValorPerRetornar(source, source.getValor()));
						} else if (source.getTipus()==MetaDadaTipusEnumDto.FLOTANT) {
							target.setValorFlotant((Double) DadaEntity.getDadaValorPerRetornar(source, source.getValor()));
						} else if (source.getTipus()==MetaDadaTipusEnumDto.IMPORT) {
							target.setValorImport((BigDecimal)DadaEntity.getDadaValorPerRetornar(source, source.getValor()));
						} else if (source.getTipus()==MetaDadaTipusEnumDto.SENCER) {
							target.setValorSencer((Long) DadaEntity.getDadaValorPerRetornar(source, source.getValor()));
						}  else if (source.getTipus()==MetaDadaTipusEnumDto.TEXT) {
							target.setValorString((String) DadaEntity.getDadaValorPerRetornar(source, source.getValor()));
						}						
						
						return target;
					}
				});
		
		
		
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<CarpetaEntity, ContingutDto>() {
					@Override
					public CarpetaDto convert(CarpetaEntity source, Type<? extends ContingutDto> destinationType) {
						CarpetaDto target = new CarpetaDto();
						if(source instanceof HibernateProxy) {
							HibernateProxy hibernateProxy = (HibernateProxy) source;
							LazyInitializer initializer = hibernateProxy.getHibernateLazyInitializer();
							source = (CarpetaEntity)initializer.getImplementation();
						}
						target.setArxiuDataActualitzacio(source.getArxiuDataActualitzacio());
						target.setArxiuUuid(source.getArxiuUuid());
						target.setCreatedBy(convertir(
								source.getCreatedBy(), 
								UsuariDto.class));
						target.setCreatedDate(source.getCreatedDate().toDate());
						target.setEntitat(convertir(
								source.getEntitat(),
								EntitatDto.class));
						target.setEsborrat(source.getEsborrat());
						target.setEsborratData(source.getEsborratData());
						target.setId(source.getId());
						target.setLastModifiedBy(convertir(
								source.getLastModifiedBy(), 
								UsuariDto.class));
						target.setLastModifiedDate(source.getLastModifiedDate().toDate());
						target.setNom(source.getNom());
						return target;
					}
				});
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<OrganGestorEntity, Long>() {
					@Override
					public Long convert(OrganGestorEntity source, Type<? extends Long> destinationType) {
						return source.getId();
					}
				});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<EntitatEntity, Long>() {
					@Override
					public Long convert(EntitatEntity source, Type<? extends Long> destinationType) {
						return source.getId();
					}
				});	
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<HistoricExpedientAggregation, HistoricExpedientDto>() {
					@Override
					public HistoricExpedientDto convert(HistoricExpedientAggregation source, Type<? extends HistoricExpedientDto> destinationType) {
						HistoricExpedientDto target = new HistoricExpedientDto();
						target.setData(source.getData());
						target.setNumExpedientsCreats(source.getNumExpedientsCreats());
						target.setNumExpedientsCreatsTotal(source.getNumExpedientsCreatsTotal());
						target.setNumExpedientsTancats(source.getNumExpedientsTancats());
						target.setNumExpedientsTancatsTotal(source.getNumExpedientsTancatsTotal());
						target.setNumDocsSignats(source.getNumDocsSignats());
						target.setNumDocsNotificats(source.getNumDocsNotificats());
						return target;
					}
				});	
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<HistoricUsuariAggregation, HistoricUsuariDto>() {
					@Override
					public HistoricUsuariDto convert(HistoricUsuariAggregation source, Type<? extends HistoricUsuariDto> destinationType) {
						HistoricUsuariDto target = new HistoricUsuariDto(null, null);
						target.setData(source.getData());
						target.setNumExpedientsCreats(source.getNumExpedientsCreats());
						target.setNumExpedientsCreatsTotal(source.getNumExpedientsCreatsTotal());
						target.setNumExpedientsTancats(source.getNumExpedientsTancats());
						target.setNumExpedientsTancatsTotal(source.getNumExpedientsTancatsTotal());
						target.setNumTasquesTramitades(source.getNumTasquesTramitades());
						target.setUsuariCodi(source.getUsuari().getCodi());
						
						return target;
					}
				});	
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<HistoricAggregation, HistoricInteressatDto>() {
					@Override
					public HistoricInteressatDto convert(HistoricAggregation source, Type<? extends HistoricInteressatDto> destinationType) {
						HistoricInteressatDto target = new HistoricInteressatDto(null, null);
						target.setData(source.getData());
						target.setNumExpedientsCreats(source.getNumExpedientsCreats());
						target.setNumExpedientsCreatsTotal(source.getNumExpedientsCreatsTotal());
						target.setNumExpedientsTancats(source.getNumExpedientsTancats());
						target.setNumExpedientsTancatsTotal(source.getNumExpedientsTancatsTotal());
						return target;
					}
				});	
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<ExpedientPeticioEntity, ExpedientPeticioDto>() {
					@Override
					public ExpedientPeticioDto convert(ExpedientPeticioEntity source, Type<? extends ExpedientPeticioDto> destinationType) {
						ExpedientPeticioDto target = new ExpedientPeticioDto();
						target.setId(source.getId());
						target.setRegistre(convertir(source.getRegistre(), RegistreDto.class));
						target.setMetaExpedientId(source.getMetaExpedient() != null ? source.getMetaExpedient().getId() : null);
						target.setMetaExpedientNom(source.getMetaExpedient() != null ? source.getMetaExpedient().getNom() : null);
						target.setEstat(source.getEstat());
						target.setIdentificador(source.getIdentificador());
						target.setExpedientId(source.getExpedient() != null ? source.getExpedient().getId() : null);
						target.setNotificaDistError(source.getNotificaDistError());
						return target;
					}
				});
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<ExpedientPeticioEntity, ExpedientPeticioListDto>() {
					@Override
					public ExpedientPeticioListDto convert(ExpedientPeticioEntity source, Type<? extends ExpedientPeticioListDto> destinationType) {
						ExpedientPeticioListDto target = new ExpedientPeticioListDto();
						target.setId(source.getId());
						RegistreDto registre = new RegistreDto();
						if (source.getRegistre() != null) {
							registre.setIdentificador(source.getRegistre().getIdentificador());
							registre.setData(source.getRegistre().getData());
							registre.setExtracte(source.getRegistre().getExtracte());
							registre.setDestiDescripcio(source.getRegistre().getDestiDescripcio());
							registre.setOrigenRegistreNumero(source.getRegistre().getOrigenRegistreNumero());
						}
						target.setRegistre(registre);
						target.setMetaExpedientId(source.getMetaExpedient() != null ? source.getMetaExpedient().getId() : null);
						target.setMetaExpedientNom(source.getMetaExpedient() != null ? source.getMetaExpedient().getNom() : null);
						target.setEstat(source.getEstat());
						target.setIdentificador(source.getIdentificador());
						target.setExpedientId(source.getExpedient() != null ? source.getExpedient().getId() : null);
						target.setPendentEnviarDistribucio(source.isPendentCanviEstatDistribucio());
						target.setDataAlta(source.getDataAlta());
						
						ExpedientPeticioEstatPendentDistribucioEnumDto estatPendentEnviarDistribucio = null;
						switch (source.getEstat()) {
//						case CREAT:
//							estatPendentEnviarDistribucio = ExpedientPeticioEstatPendentDistribucioEnumDto.CONSULTA_ERROR;
//							break;
						case PENDENT:
							estatPendentEnviarDistribucio = ExpedientPeticioEstatPendentDistribucioEnumDto.PENDENT;
							break;
						case PROCESSAT_PENDENT:
						case PROCESSAT_NOTIFICAT:
							estatPendentEnviarDistribucio = ExpedientPeticioEstatPendentDistribucioEnumDto.ACCEPTAT;
							break;
						case REBUTJAT:
							estatPendentEnviarDistribucio = ExpedientPeticioEstatPendentDistribucioEnumDto.REBUTJAT;
							break;
						}
						target.setEstatPendentEnviarDistribucio(estatPendentEnviarDistribucio);
						return target;
					}
				});
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<DocumentPortafirmesEntity, SeguimentDto>() {
					@Override
					public SeguimentDto convert(DocumentPortafirmesEntity source, Type<? extends SeguimentDto> destinationType) {
						SeguimentDto target = new SeguimentDto();
						target.setId(source.getId());
						target.setExpedientId(source.getExpedient().getId());
						target.setExpedientNom(source.getExpedient().getNom());
						target.setDocumentId(source.getDocument().getId());
						target.setDocumentNom(source.getDocument().getNom());
						target.setPortafirmesEstat(source.getEstat());
						target.setDataEnviament(source.getEnviatData());
						return target;
					}
				});	
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<DocumentNotificacioEntity, SeguimentDto>() {
					@Override
					public SeguimentDto convert(DocumentNotificacioEntity source, Type<? extends SeguimentDto> destinationType) {
						SeguimentDto target = new SeguimentDto();
						target.setId(source.getId());
						target.setExpedientId(source.getExpedient().getId());
						target.setExpedientNom(source.getExpedient().getNom());
						target.setDocumentId(source.getDocument().getId());
						target.setDocumentNom(source.getDocument().getNom());
						target.setNotificacioEstat(source.getNotificacioEstat());
						target.setDataEnviament(source.getCreatedDate().toDate());
						
						
						InteressatEntity destinatari = !source.getDocumentEnviamentInteressats().isEmpty() ? HibernateHelper.deproxy(source.getDocumentEnviamentInteressats().iterator().next().getInteressat()) : null;
						String destinatariNom = "";
						if (destinatari != null) {
							if (destinatari instanceof  InteressatAdministracioEntity) {
								InteressatAdministracioEntity adm = (InteressatAdministracioEntity) destinatari;
								destinatariNom = adm.getOrganCodi() + " - " + adm.getOrganNom();
							} else if (destinatari instanceof  InteressatPersonaFisicaEntity) {
								InteressatPersonaFisicaEntity fis = (InteressatPersonaFisicaEntity) destinatari;
								destinatariNom = fis.getNom() + " " + fis.getLlinatge1() + " " + (fis.getLlinatge2() != null ? fis.getLlinatge2() : "");
							} else if (destinatari instanceof  InteressatPersonaJuridicaEntity) {
								InteressatPersonaJuridicaEntity jur = (InteressatPersonaJuridicaEntity) destinatari;
								destinatariNom = jur.getRaoSocial();
							} 
						}
						target.setDestinataris(destinatariNom);
						return target;
					}
				});	
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<ExpedientTascaEntity, SeguimentDto>() {
					@Override
					public SeguimentDto convert(ExpedientTascaEntity source, Type<? extends SeguimentDto> destinationType) {
						SeguimentDto target = new SeguimentDto();
						target.setId(source.getId());
						target.setExpedientId(source.getExpedient().getId());
						target.setExpedientNom(source.getExpedient().getNom());
						target.setTascaNom(source.getMetaExpedientTasca().getNom());
						target.setData(source.getDataInici());
						List<String> responsablesNom = new ArrayList<String>();
						for (UsuariEntity responsable: source.getResponsables()) {
							responsablesNom.add(responsable.getNom());
						}
						target.setResponsablesNom(StringUtils.join(responsablesNom, ","));
						if (source.getResponsableActual() != null)
							target.setResponsableActualNom(source.getResponsableActual().getNom());
						target.setTascaEstat(source.getEstat());
						return target;
					}
				});	
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<ExpedientEntity, SeguimentArxiuPendentsDto>() {
					@Override
					public SeguimentArxiuPendentsDto convert(ExpedientEntity source, Type<? extends SeguimentArxiuPendentsDto> destinationType) {
						SeguimentArxiuPendentsDto target = new SeguimentArxiuPendentsDto();
						target.setId(source.getId());
						target.setElementNom(source.getNom());
						target.setExpedientNumeroNom(source.getNom() + " (" + expedientHelper.calcularNumero(source) + ")");
						target.setMetaExpedientNom(source.getMetaExpedient() != null ? source.getMetaExpedient().getNom() : null);
						target.setDataDarrerIntent(source.getArxiuIntentData());
						return target;
					}
				});	
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<DocumentEntity, SeguimentArxiuPendentsDto>() {
					@Override
					public SeguimentArxiuPendentsDto convert(DocumentEntity source, Type<? extends SeguimentArxiuPendentsDto> destinationType) {
						SeguimentArxiuPendentsDto target = new SeguimentArxiuPendentsDto();
						target.setId(source.getId());
						target.setExpedientId(source.getExpedient().getId());
						target.setElementNom(source.getNom());
						target.setExpedientNumeroNom(source.getExpedient().getNom() + " (" + expedientHelper.calcularNumero(source.getExpedient()) + ")");
						target.setMetaExpedientNom(source.getExpedient().getMetaExpedient() != null ? source.getExpedient().getMetaExpedient().getNom() : null);
						target.setDataDarrerIntent(source.getArxiuIntentData());
						target.setExpedientArxiuPropagat(source.getExpedient().getArxiuUuid() != null);
						target.setAnnex(source.getAnnexos() != null && !source.getAnnexos().isEmpty());
						return target;
					}
				});	
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<InteressatEntity, SeguimentArxiuPendentsDto>() {
					@Override
					public SeguimentArxiuPendentsDto convert(InteressatEntity source, Type<? extends SeguimentArxiuPendentsDto> destinationType) {
						SeguimentArxiuPendentsDto target = new SeguimentArxiuPendentsDto();
						target.setId(source.getId());
						target.setExpedientId(source.getExpedient().getId());
						if (source instanceof  InteressatAdministracioEntity) {
							target.setElementNom(((InteressatAdministracioEntity)source).getOrganNom());
						} else if (source instanceof  InteressatPersonaFisicaEntity) {
							InteressatPersonaFisicaEntity fis = (InteressatPersonaFisicaEntity)source;
							target.setElementNom(fis.getNom() + " " + fis.getLlinatge1() + " " + fis.getLlinatge2());
						} else if (source instanceof  InteressatPersonaJuridicaEntity) {
							target.setElementNom(((InteressatPersonaJuridicaEntity)source).getRaoSocial());
						} 
						target.setExpedientNumeroNom(source.getExpedient().getNom() + " (" + expedientHelper.calcularNumero(source.getExpedient()) + ")");
						target.setMetaExpedientNom(source.getExpedient().getMetaExpedient() != null ? source.getExpedient().getMetaExpedient().getNom() : null);
						target.setDataDarrerIntent(source.getArxiuIntentData());
						target.setExpedientArxiuPropagat(source.getExpedient().getArxiuUuid() != null);
						return target;
					}
				});

		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<ExecucioMassivaEntity, ExecucioMassivaDto>() {
					@Override
					public ExecucioMassivaDto convert(ExecucioMassivaEntity source, Type<? extends ExecucioMassivaDto> destinationType) {
						ExecucioMassivaDto target = new ExecucioMassivaDto();
						target.setId(source.getId());
						target.setTipus(source.getTipus() != null ? ExecucioMassivaDto.ExecucioMassivaTipusDto.valueOf(source.getTipus().name()): null);
						target.setDataInici(source.getDataInici());
						target.setDataFi(source.getDataFi());
						target.setMotiu(source.getMotiu());
						target.setPrioritat(source.getPrioritat());
						target.setDataCaducitat(source.getDataCaducitat());
						target.setPortafirmesResponsables(source.getPortafirmesResponsables() != null ? source.getPortafirmesResponsables().split(",") : null);
						target.setPortafirmesSequenciaTipus(source.getPortafirmesSequenciaTipus());
						target.setPortafirmesFluxId(source.getPortafirmesFluxId());
						target.setPortafirmesTransaccioId(source.getPortafirmesTransaccioId());
						target.setEnviarCorreu(source.getEnviarCorreu());
						target.setCreatedBy(convertir(source.getCreatedBy(), UsuariDto.class));
						return target;
					}
				});
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<ExpedientEntity, CodiValorDto>() {
					@Override
					public CodiValorDto convert(ExpedientEntity source, Type<? extends CodiValorDto> destinationClass) {
						CodiValorDto target = new CodiValorDto();
						target.setCodi(source.getId().toString());
						target.setValor(source.getNom());
						return target;
					}
				});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<OrganGestorEntity, OrganGestorDto>() {
					@Override
					public OrganGestorDto convert(OrganGestorEntity source, Type<? extends OrganGestorDto> destinationClass) {
						OrganGestorDto target = new OrganGestorDto();
						target.setId(source.getId());
						target.setCodi(source.getCodi());
						target.setNom(source.getNom());
						target.setEntitatId(source.getEntitat() != null ? source.getEntitat().getId().toString() : null);
						target.setEntitatNom(source.getEntitat() != null ? source.getEntitat().getNom() : null);
						target.setPareId(source.getPare() != null ? source.getPare().getId() : null);
						target.setPareCodi(source.getPare() != null ? source.getPare().getCodi() : null);
						target.setPareNom(source.getPare() != null ? source.getPare().getNom() : null);
						target.setGestioDirect(source.isGestioDirect());
						return target;
					}
				});
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<RegistreAnnexEntity, RegistreAnnexDto>() {
					@Override
					public RegistreAnnexDto convert(RegistreAnnexEntity source, Type<? extends RegistreAnnexDto> destinationClass) {
						RegistreAnnexDto target = new RegistreAnnexDto();

						target.setId(source.getId());
						target.setFirmaPerfil(source.getFirmaPerfil() != null ? source.getFirmaPerfil().toString() : null);
						target.setFirmaTipus(source.getFirmaTipus() != null ? source.getFirmaTipus().toString() : null);
						target.setNtiFechaCaptura(source.getNtiFechaCaptura());
						target.setNtiOrigen(source.getNtiOrigen() != null ? source.getNtiOrigen().toString() : null);
						target.setNtiTipoDocumental(source.getNtiTipoDocumental() != null ? source.getNtiTipoDocumental().toString() : null);
						target.setNtiEstadoElaboracion(source.getNtiEstadoElaboracion() != null ? source.getNtiEstadoElaboracion().toString() : null);
						target.setObservacions(source.getObservacions());
						target.setSicresTipoDocumento(source.getSicresTipoDocumento() != null ? source.getSicresTipoDocumento().toString() : null);
						target.setSicresValidezDocumento(source.getSicresValidezDocumento() != null ? source.getSicresValidezDocumento().toString() : null);
						target.setTamany(source.getTamany());
						target.setTipusMime(source.getTipusMime());
						target.setTitol(source.getTitol());
						target.setNom(source.getNom());
						target.setUuid(source.getUuid());
						target.setCreatedDate(source.getCreatedDate() != null ? source.getCreatedDate().toDate() : null);
						target.setEstat(source.getEstat());
						target.setError(source.getError());

						target.setRegistreNumero(source.getRegistre().getIdentificador());
						ExpedientEntity expedient = source.getRegistre().getExpedientPeticions().get(0).getExpedient();
						if (expedient != null) {
							target.setExpedientId(expedient.getId());
							target.setExpedientNumeroNom(expedientHelper.calcularNumero(expedient) + " - " + expedient.getNom());
							target.setExpedientCreatedDate(expedient.getCreatedDate().toDate());
						}
						target.setDocumentId(source.getDocument() != null ? source.getDocument().getId() : null);
						target.setExpedientPeticioId(source.getRegistre().getExpedientPeticions().get(0).getId());
						target.setValidacioCorrecte(source.isValidacioCorrecte());
						target.setValidacioError(source.getValidacioError());
						target.setAnnexEstat(source.getAnnexEstat());
						
						return target;
					}
				});
		
		
		
		
		

//		mapperFactory.classMap(RegistreEntity.class, RegistreDto.class)
//				.byDefault()
//				.register();
//
//		mapperFactory.classMap(RegistreAnnexEntity.class, RegistreAnnexDto.class)
//				.exclude("contingut")
//				.exclude("firmaContingut")
//				.byDefault()
//				.register();
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
