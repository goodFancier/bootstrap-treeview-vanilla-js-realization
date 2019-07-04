package Shopper.ORM.Dto;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import Shopper.Enum.PlanForecastEnum.PlanForecastMetric;
import Shopper.Enum.PlanForecastEnum.PlanForecastTimePeriodEnum;
import Shopper.Enum.PlanForecastEnum.PlanForecastType;
import Shopper.ORM.ORMDto;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "`analytics.plansandforecasts.values`")
@Getter
@Setter
public class PlanForecastValueDto extends ORMDto
{
	private static final long serialVersionUID = 2696108474842748611L;
	
	/**
	 * Значение плана
	 */
	@Column(name = "Value", nullable = true, precision = 10, scale = 2)
	private BigDecimal value;
	
	/**
	 * Дата
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "Date", nullable = false)
	private Date date;
	
	/**
	 * Магазин
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "Shop", nullable = true)
	private ShopDto shop;
	
	/**
	 * Бренд
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "Brand", nullable = true)
	private BrandDto brand;
	
	/**
	 * Торговая сеть
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "Retailer", nullable = true)
	private RetailerDto retailer;
	
	/**
	 * Временной охват
	 */
	@Column(name = "TimePeriod", columnDefinition = "enum('Yearly', 'Quarterly', 'Monthly', 'Weekly', 'Daily', 'Hourly')", nullable = true)
	@Enumerated(EnumType.STRING)
	private PlanForecastTimePeriodEnum timePeriod;
	
	/**
	 * Тип (План/прогноз)
	 */
	@Column(name = "Type", columnDefinition = "enum('Plan','Forecast')", nullable = true)
	@Enumerated(EnumType.STRING)
	private PlanForecastType type;
	
	/**
	 * Метрика
	 */
	@Column(name = "Metric", columnDefinition = "enum('Traffic','Conversion','AverageCheck','CheckFrequency')", nullable = true)
	@Enumerated(EnumType.STRING)
	private PlanForecastMetric metric;
}
