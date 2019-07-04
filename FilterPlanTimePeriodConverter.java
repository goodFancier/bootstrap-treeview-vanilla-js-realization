package Shopper.Portal.Converter.Filter;

import javax.faces.convert.EnumConverter;
import javax.faces.convert.FacesConverter;

import Shopper.Enum.PlanForecastEnum.PlanForecastTimePeriodEnum;

@FacesConverter(value = "FilterPlanTimePeriodConverter")
public class FilterPlanTimePeriodConverter extends EnumConverter
{
	public FilterPlanTimePeriodConverter()
	{
		super(PlanForecastTimePeriodEnum.class);
	}
}
