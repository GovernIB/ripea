package es.caib.ripea.back.command;

import es.caib.ripea.back.helper.ConversioTipusHelper;
import es.caib.ripea.service.intf.dto.PermisOrganGestorDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class PermisOrganGestorCommand extends PermisCommand {

	@NotNull
	private Long organGestorId;

	public PermisOrganGestorCommand() {
		super();
	}

	public PermisOrganGestorCommand(Long organGestorId) {
		super();
		this.organGestorId = organGestorId;
	}

	public static PermisOrganGestorCommand asCommand(PermisOrganGestorDto dto) {
		
		
		// when using orika there was an error only reproducible in PRO:
//	java.lang.NoClassDefFoundError: Lorg/mortbay/component/Container$Listener;
//    at java.lang.Class.getDeclaredFields0(Native Method)
//    at java.lang.Class.privateGetDeclaredFields(Class.java:2509)
//    at java.lang.Class.getDeclaredFields(Class.java:1819)
//    at com.carrotsearch.sizeof.RamUsageEstimator.createCacheEntry(RamUsageEstimator.java:568)
//    at com.carrotsearch.sizeof.RamUsageEstimator.measureSizeOf(RamUsageEstimator.java:532)
//    at com.carrotsearch.sizeof.RamUsageEstimator.sizeOfAll(RamUsageEstimator.java:380)
//    at com.carrotsearch.sizeof.RamUsageEstimator.sizeOfAll(RamUsageEstimator.java:361)
//    at ma.glasnost.orika.StateReporter.humanReadableSizeInMemory(StateReporter.java:48)
//    at ma.glasnost.orika.impl.DefaultMapperFactory.reportCurrentState(DefaultMapperFactory.java:1559)
//    at ma.glasnost.orika.StateReporter.reportCurrentState(StateReporter.java:33)
//    at ma.glasnost.orika.impl.ExceptionUtility.decorate(ExceptionUtility.java:65)
//    at ma.glasnost.orika.impl.DefaultMapperFactory.lookupMapper(DefaultMapperFactory.java:762)
//    at ma.glasnost.orika.impl.DefaultMapperFactory.lookupMapper(DefaultMapperFactory.java:707)
//    at ma.glasnost.orika.impl.MapperFacadeImpl.resolveMapper(MapperFacadeImpl.java:591)
//    at ma.glasnost.orika.impl.MapperFacadeImpl.resolveMappingStrategy(MapperFacadeImpl.java:216)
//    at ma.glasnost.orika.impl.DefaultBoundMapperFacade$BoundStrategyCache.getStrategy(DefaultBoundMapperFacade.java:259)
//    at ma.glasnost.orika.impl.DefaultBoundMapperFacade.map(DefaultBoundMapperFacade.java:167)
//    at ma.glasnost.orika.generated.Orika_PermisOrganGestorDto_PermisOrganGestorCommand_Mapper34992981617608561$68.mapBtoA(Orika_PermisOrganGestorDto_PermisOrganGestorCommand_Mapper34992981617608561$68.java)
//    at ma.glasnost.orika.impl.ReversedMapper.mapAtoB(ReversedMapper.java:65)
//    at ma.glasnost.orika.impl.mapping.strategy.UseCustomMapperStrategy.map(UseCustomMapperStrategy.java:67)
//    at ma.glasnost.orika.impl.MapperFacadeImpl.map(MapperFacadeImpl.java:742)
//    at ma.glasnost.orika.impl.MapperFacadeImpl.map(MapperFacadeImpl.java:721)
//    at es.caib.ripea.war.helper.ConversioTipusHelper.convertir(ConversioTipusHelper.java:28)
//    at es.caib.ripea.war.command.PermisOrganGestorCommand.asCommand(PermisOrganGestorCommand.java:27)


		PermisOrganGestorCommand permisCommand = new PermisOrganGestorCommand();
		permisCommand.setId((Long)dto.getId());
		permisCommand.setOrganGestorId(dto.getOrganGestor().getId());
		permisCommand.setPrincipalTipus(dto.getPrincipalTipus());
		permisCommand.setPrincipalCodiNom(dto.getPrincipalCodiNom());
		permisCommand.setPrincipalNom(dto.getPrincipalNom());
		permisCommand.setCreate(dto.isCreate());
		permisCommand.setRead(dto.isRead());
		permisCommand.setWrite(dto.isWrite());
		permisCommand.setDelete(dto.isDelete());
		permisCommand.setProcedimentsComuns(dto.isProcedimentsComuns());
        permisCommand.setAdministrationComuns(dto.isAdministrationComuns());
		permisCommand.setAdministration(dto.isAdministration());
		permisCommand.setDisseny(dto.isDisseny());
		permisCommand.setSelectAll(false);
		if (permisCommand.isCreate() && permisCommand.isDelete() && permisCommand.isRead() && permisCommand.isWrite())
			permisCommand.setSelectAll(true);

		return permisCommand;
	}
	
	public static PermisOrganGestorDto asDto(PermisOrganGestorCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				PermisOrganGestorDto.class);
	}

}
