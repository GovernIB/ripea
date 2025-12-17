package es.caib.ripea.persistence.entity;

import java.time.LocalDate;
import java.time.temporal.WeekFields;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import es.caib.comanda.model.v1.estadistica.DiaSetmanaEnum;
import es.caib.ripea.service.intf.config.BaseConfig;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "explot_temps")
@Getter
@Setter
public class ExplotacioTempsEntity extends RipeaPersistable<Long> {

	@Column(name = "data")
	private LocalDate data;
	
	@Column(name = "anualitat")
	private Integer anualitat;
	
	@Column(name = "mes")
	private Integer mes;
	
	@Column(name = "trimestre")
	private Integer trimestre;
	
	@Column(name = "setmana")
	private Integer setmana;

	@Column(name = "dia")
	private Integer dia;

	@Column(name = "dia_setmana")
	@Enumerated(EnumType.STRING)
	private DiaSetmanaEnum diaSetmana;
	
	public ExplotacioTempsEntity() {
		super();
	}
	
	public ExplotacioTempsEntity(LocalDate data) {
		super();
		this.data = data;
		this.anualitat = data.getYear();
		this.trimestre = data.getMonthValue() / 3;
		this.mes = data.getMonthValue();
		this.setmana = data.get(WeekFields.ISO.weekOfWeekBasedYear());
		this.dia = data.getDayOfMonth();
		switch (data.getDayOfWeek()) {
			case SUNDAY:	this.diaSetmana = DiaSetmanaEnum.DG; break;
			case MONDAY:	this.diaSetmana = DiaSetmanaEnum.DL; break;
			case TUESDAY:	this.diaSetmana = DiaSetmanaEnum.DM; break;
			case WEDNESDAY: this.diaSetmana = DiaSetmanaEnum.DC; break;
			case THURSDAY:	this.diaSetmana = DiaSetmanaEnum.DJ; break;
			case FRIDAY:	this.diaSetmana = DiaSetmanaEnum.DV; break;
			case SATURDAY:	this.diaSetmana = DiaSetmanaEnum.DS; break;
		}
	}
}