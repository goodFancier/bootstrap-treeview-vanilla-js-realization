package Shopper.Enum.PlanForecastEnum;

import lombok.Getter;

public enum PlanForecastType
{
	Plan("План"), Forecast("Прогноз");
	PlanForecastType(String title)
	{
		this.title = title;
	}
	
	@Getter
	private String title;
	
	public static PlanForecastType getByString(String planType)
	{
		if(planType == null)
			return null;
		for(PlanForecastType element: PlanForecastType.values())
			if(element.name().equalsIgnoreCase(planType) || element.getTitle().equalsIgnoreCase(planType))
				return element;
		return null;
	}
	
	@Override
	public String toString()
	{
		return getTitle();
	}
}
