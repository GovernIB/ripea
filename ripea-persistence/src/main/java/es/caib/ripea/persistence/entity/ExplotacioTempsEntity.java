package es.caib.ripea.persistence.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import es.caib.comanda.ms.estadistica.model.DiaSetmanaEnum;
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
}