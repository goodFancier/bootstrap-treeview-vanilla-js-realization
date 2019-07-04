package Shopper.Portal.Converter.Filter;

import javax.faces.convert.EnumConverter;
import javax.faces.convert.FacesConverter;

import Shopper.Enum.PlanForecastEnum.PlanForecastMetric;

@FacesConverter(value = "FilterPlanMetricConverter")
public class FilterPlanMetricConverter extends EnumConverter
{
	public FilterPlanMetricConverter()
	{
		super(PlanForecastMetric.class);
	}
}
