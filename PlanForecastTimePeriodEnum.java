package Shopper.Enum.PlanForecastEnum;

import lombok.Getter;

public enum PlanForecastTimePeriodEnum
{
	Yearly("Годовой"), Quarterly("Квартальный"), Monthly("Месячный"), Weekly("Недельный"), Daily("Подневной"), Hourly("Часовой");
	PlanForecastTimePeriodEnum(String title)
	{
		this.title = title;
	}
	
	@Getter
	private String title;
	
	public static PlanForecastTimePeriodEnum getByString(String timePeriod)
	{
		if(timePeriod == null)
			return null;
		for(PlanForecastTimePeriodEnum element: PlanForecastTimePeriodEnum.values())
			if(element.name().equalsIgnoreCase(timePeriod) || element.getTitle().equalsIgnoreCase(timePeriod))
				return element;
		return null;
	}
	
	@Override
	public String toString()
	{
		return getTitle();
	}
}
