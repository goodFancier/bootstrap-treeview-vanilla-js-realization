package Shopper.ORM.Services;

import java.util.concurrent.TimeUnit;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.jboss.ejb3.annotation.TransactionTimeout;

import Shopper.ORM.Dto.PlanForecastValueDto;
import Shopper.ORM.Repositories.PlanForecastValuesRepository;
import Shopper.ORM.Validators.IEntityValidator;

@Singleton
@Startup
@Lock(LockType.READ)
@TransactionTimeout(unit = TimeUnit.DAYS, value = 7)
public class PlanForecastValuesDataService extends ORMService<PlanForecastValueDto>
{
	@Inject
	private PlanForecastValuesRepository planForecastValuesRepository;
	
	@Override
	protected PlanForecastValuesRepository getRepository()
	{
		return planForecastValuesRepository;
	}
	
	@Override
	public IEntityValidator<PlanForecastValueDto> getValidator(PlanForecastValueDto object) throws Exception
	{
		return null;
	}
	
	public PlanForecastValueDto findByRecID(Long recID)
	{
		return getRepository().findById(recID);
	}
}