package Shopper.Enum.PlanForecastEnum;

import lombok.Getter;

public enum PlanForecastMetric
{
	Traffic("Трафик"), Conversion("Конверсия"), AverageCheck("Средний чек"), CheckFrequency("Частота покупок");
	PlanForecastMetric(String title)
	{
		this.title = title;
	}
	
	@Getter
	private String title;
	
	public static PlanForecastMetric getByString(String metric)
	{
		if(metric == null)
			return null;
		for(PlanForecastMetric element: PlanForecastMetric.values())
			if(element.name().equalsIgnoreCase(metric) || element.getTitle().equalsIgnoreCase(metric))
				return element;
		return null;
	}
	
	@Override
	public String toString()
	{
		return getTitle();
	}
}
