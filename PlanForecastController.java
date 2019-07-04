package Shopper.Portal.Controller.PlanForecast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.persistence.Query;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LegendPlacement;
import org.primefaces.model.chart.LineChartModel;

import Shopper.Common.Date.DateFormats;
import Shopper.Common.String.StringUtils;
import Shopper.Enum.Analysis.AnalysisScenarioEnum;
import Shopper.Enum.PlanForecastEnum.PlanForecastMetric;
import Shopper.Enum.PlanForecastEnum.PlanForecastTimePeriodEnum;
import Shopper.Enum.PlanForecastEnum.PlanForecastType;
import Shopper.ORM.Dto.BrandDto;
import Shopper.ORM.Dto.PlanForecastValueDto;
import Shopper.ORM.Dto.RetailerDto;
import Shopper.ORM.Dto.ShopDto;
import Shopper.ORM.Services.DataServiceFactory;
import Shopper.ORM.Services.PlanForecastValuesDataService;
import Shopper.ORM.Services.RetailersDataService;
import Shopper.Portal.Common.Exception.FacesPortalException;
import Shopper.Portal.Common.Exception.PortalExceptionHandler;
import Shopper.Portal.Controller.CustomLazyDataModel;
import Shopper.Portal.Controller.EntityListController;
import Shopper.Portal.Controller.Annotation.CanAdd;
import Shopper.Portal.Controller.Annotation.CanDelete;
import Shopper.Portal.Controller.Annotation.CanEdit;
import Shopper.Portal.Session.SessionInfo;
import lombok.Getter;
import lombok.Setter;

@ManagedBean(name = "PlanForecastController")
@ViewScoped
@CanDelete
@CanEdit
@CanAdd
public class PlanForecastController extends EntityListController<PlanForecastValueDto>
{
	public PlanForecastController()
	{
		timeInverbalConditions = new String();
	}
	
	@Getter
	@Setter
	private Boolean isDeleteButtonDisabled = true;
	
	private PlanForecastValueDto selectedEntity;
	
	@Override
	public PlanForecastValueDto checkSelectedEntity() throws FacesPortalException
	{
		PlanForecastValueDto entity = selectedEntity;
		if(entity == null)
			throw new FacesPortalException("Не выделена ни одна запись.");
		return entity;
	}
	
	public void selectEntity(SelectEvent event)
	{
		selectedEntity = ((PlanForecastValueDto)event.getObject());
		if(selectedEntity != null)
			isDeleteButtonDisabled = false;
	}
	
	private RetailerDto retailer;
	
	@Setter
	private RetailerDto planRetailer;
	
	public RetailerDto getPlanRetailer()
	{
		if(this.planRetailer == null)
			this.planRetailer = SessionInfo.getInstance().getIsAdminPermissions()? this.getRetailerDataService().getRepository().getAll().get(0): SessionInfo.getInstance().getUser().getRetailer();
		return this.planRetailer;
	}
	
	@Getter
	private BigDecimal planValue;
	
	public void setPlanValue(BigDecimal planValue)
	{
		this.planValue = planValue;
	}
	
	@Getter
	@Setter
	private Date planValueDate;
	
	@Getter
	private String dataTablePlanYear;
	
	@Getter
	private PlanForecastType planType;
	
	@Getter
	private PlanForecastMetric planMetric;
	
	@Getter
	private PlanForecastTimePeriodEnum planTimePeriod;
	
	@Getter
	@Setter
	private ShopDto planShop;
	
	@Getter
	@Setter
	private BrandDto planBrand;
	
	public void setDataTablePlanYear(String dataTablePlanYear)
	{
		this.dataTablePlanYear = dataTablePlanYear;
	}
	
	public void setPlanType(PlanForecastType planType)
	{
		this.planType = planType;
	}
	
	public void setPlanMetric(PlanForecastMetric planMetric)
	{
		this.planMetric = planMetric;
	}
	
	public void setPlanTimePeriod(PlanForecastTimePeriodEnum planTimePeriod)
	{
		this.planTimePeriod = planTimePeriod;
	}
	
	public Boolean getIsOneOfPropertiesIsEmpty()
	{
		return StringUtils.isRealyEmpty(dataTablePlanYear) || planType == null || planMetric == null || planTimePeriod == null;
	}
	
	@Getter
	@Setter
	public static String timeInverbalConditions;
	
	@Getter
	@Setter
	public static String shopsConditions;
	
	public RetailerDto getRetailer()
	{
		if(this.retailer == null)
			this.retailer = SessionInfo.getInstance().getIsAdminPermissions()? this.getRetailerDataService().getRepository().getAll().get(0): SessionInfo.getInstance().getUser().getRetailer();
		return this.retailer;
	}
	
	public void setRetailer(RetailerDto retailer)
	{
		this.retailer = retailer;
	}
	
	@Override
	protected CustomLazyDataModel<PlanForecastValueDto> createLazyModel()
	{
		return null;
	}
	
	@Override
	public PlanForecastValuesDataService getDataService()
	{
		return DataServiceFactory.intance().getPlanForecastValuesDataService();
	}
	
	public RetailersDataService getRetailerDataService()
	{
		return DataServiceFactory.intance().getRetailersService();
	}
	
	@Override
	public String getParentPage()
	{
		return "/pages/analytics/planning";
	}
	
	@Override
	protected String getPageName()
	{
		return "/pages/analytics/plan";
	}
	
	@Getter
	private String planYear;
	
	public void setPlanYear(String planYear)
	{
		this.planYear = planYear;
	}
	
	public Boolean getIsPlanYearEmpty()
	{
		return StringUtils.isRealyEmpty(planYear);
	}
	
	private List<PlanForecastValueDto> planValueList = new ArrayList<>();
	
	@SuppressWarnings("unchecked")
	public List<PlanForecastValueDto> getPlanValues()
	{
		List<PlanForecastValueDto> planList = new ArrayList<>();
		StringBuilder sql = new StringBuilder(String.format("SELECT * FROM `analytics.plansandforecasts.values` a WHERE a.Retailer = %s", retailer.getRecID(), timeInverbalConditions));
		if(!StringUtils.isRealyEmpty(timeInverbalConditions))
			sql.append(String.format(" AND (%s)", timeInverbalConditions));
		if(!StringUtils.isRealyEmpty(shopsConditions))
			sql.append(String.format(" AND (%s)", shopsConditions));
		if(!StringUtils.isRealyEmpty(timeInverbalConditions) || !StringUtils.isRealyEmpty(shopsConditions))
		{
			Query query = DataServiceFactory.intance().getStockBuyerReadedDataService().getEm().createNativeQuery(
				sql.toString(),
					PlanForecastValueDto.class);
			query.getResultList().stream().forEach((record) -> {
				planList.add(((PlanForecastValueDto)(record)));
			});
		}
		else
			planList.addAll(DataServiceFactory.intance().getPlanForecastValuesDataService().getAll());
		planValueList = planList;
		return planList;
	}
	
	public void setValue(BigDecimal value, Long recID) throws FacesPortalException
	{
		PlanForecastValueDto planForecastValueDto = DataServiceFactory.intance().getPlanForecastValuesDataService().findByRecID(recID);
		planForecastValueDto.setValue(value);
		try
		{
			DataServiceFactory.intance().getPlanForecastValuesDataService().update(planForecastValueDto);
		}
		catch(Exception e)
		{
			PortalExceptionHandler.handle(e);
			throw new FacesPortalException("Ошибка сохранения планового значения. Попробуйте ещё раз или перезагрузите страницу");
		}
	}
	
	public void setMetricValue(PlanForecastMetric metric, Long recID) throws FacesPortalException
	{
		PlanForecastValueDto planForecastValueDto = DataServiceFactory.intance().getPlanForecastValuesDataService().findByRecID(recID);
		planForecastValueDto.setMetric(metric);
		try
		{
			DataServiceFactory.intance().getPlanForecastValuesDataService().update(planForecastValueDto);
		}
		catch(Exception e)
		{
			PortalExceptionHandler.handle(e);
			throw new FacesPortalException("Ошибка сохранения значения метрики. Попробуйте ещё раз или перезагрузите страницу");
		}
	}
	
	public void setTimePeriod(PlanForecastTimePeriodEnum timePeriod, Long recID) throws FacesPortalException
	{
		PlanForecastValueDto planForecastValueDto = DataServiceFactory.intance().getPlanForecastValuesDataService().findByRecID(recID);
		planForecastValueDto.setTimePeriod(timePeriod);
		try
		{
			DataServiceFactory.intance().getPlanForecastValuesDataService().update(planForecastValueDto);
		}
		catch(Exception e)
		{
			PortalExceptionHandler.handle(e);
			throw new FacesPortalException("Ошибка сохранения временного интервала. Попробуйте ещё раз или перезагрузите страницу");
		}
	}
	
	public void setShopValue(ShopDto shop, Long recID) throws FacesPortalException
	{
		PlanForecastValueDto planForecastValueDto = DataServiceFactory.intance().getPlanForecastValuesDataService().findByRecID(recID);
		planForecastValueDto.setShop(shop);
		try
		{
			DataServiceFactory.intance().getPlanForecastValuesDataService().update(planForecastValueDto);
		}
		catch(Exception e)
		{
			PortalExceptionHandler.handle(e);
			throw new FacesPortalException("Ошибка сохранения временного интервала. Попробуйте ещё раз или перезагрузите страницу");
		}
	}
	
	public void setBrandValue(BrandDto brand, Long recID) throws FacesPortalException
	{
		PlanForecastValueDto planForecastValueDto = DataServiceFactory.intance().getPlanForecastValuesDataService().findByRecID(recID);
		planForecastValueDto.setBrand(brand);
		try
		{
			DataServiceFactory.intance().getPlanForecastValuesDataService().update(planForecastValueDto);
		}
		catch(Exception e)
		{
			PortalExceptionHandler.handle(e);
			throw new FacesPortalException("Ошибка сохранения временного интервала. Попробуйте ещё раз или перезагрузите страницу");
		}
	}
	
	public void setValueDate(Date date, Long recID) throws FacesPortalException
	{
		PlanForecastValueDto planForecastValueDto = DataServiceFactory.intance().getPlanForecastValuesDataService().findByRecID(recID);
		planForecastValueDto.setDate(date);
		try
		{
			DataServiceFactory.intance().getPlanForecastValuesDataService().update(planForecastValueDto);
		}
		catch(Exception e)
		{
			PortalExceptionHandler.handle(e);
			throw new FacesPortalException("Ошибка сохранения даты планового значения. Попробуйте ещё раз или перезагрузите страницу");
		}
	}
	
	public void removePlanValue(Long recID) throws FacesPortalException
	{
		PlanForecastValueDto planForecastValueDto = DataServiceFactory.intance().getPlanForecastValuesDataService().findByRecID(recID);
		try
		{
			DataServiceFactory.intance().getPlanForecastValuesDataService().delete(planForecastValueDto);
		}
		catch(Exception e)
		{
			PortalExceptionHandler.handle(e);
			throw new FacesPortalException("Не удалось удалить плановое значение");
		}
	}
	
	public void addPlanValue() throws FacesPortalException
	{
		PlanForecastValueDto planForecastValueDto = new PlanForecastValueDto();
		planForecastValueDto.setType(PlanForecastType.Plan);
		planForecastValueDto.setRetailer(retailer);
		try
		{
			DataServiceFactory.intance().getPlanForecastValuesDataService().create(planForecastValueDto);
		}
		catch(Exception e)
		{
			PortalExceptionHandler.handle(e);
			throw new FacesPortalException("Не удалось добавить плановое значение");
		}
	}
	
	@Getter(lazy = true)
	private final List<SelectItem> allPlanYears = initAllPlans();
	
	private List<SelectItem> initAllPlans()
	{
		SelectItemGroup g1 = new SelectItemGroup("Текущие планы");
		g1.setSelectItems(new SelectItem[]{new SelectItem(AnalysisScenarioEnum.ReturnBuyer, AnalysisScenarioEnum.ReturnBuyer.getTitle()), new SelectItem(AnalysisScenarioEnum.IncreaseSKU, AnalysisScenarioEnum.IncreaseSKU.getTitle()), new SelectItem(AnalysisScenarioEnum.IncreaseClassinessOfGoods, AnalysisScenarioEnum.IncreaseClassinessOfGoods.getTitle())});
		ArrayList<SelectItem> result = new ArrayList<SelectItem>();
		result.add(g1);
		return result;
	}
	
	public void initChartSeries(
		LineChartModel chartModel,
		PlanForecastMetric metric,
		PlanForecastTimePeriodEnum timePeriod)
	{
		ChartSeries planValuesSeries = new ChartSeries();
		Collections.sort(planValueList, new Comparator<PlanForecastValueDto>()
		{
			@Override
			public int compare(PlanForecastValueDto o1, PlanForecastValueDto o2)
			{
				if(o1.getDate() == null || o2.getDate() == null)
					return 0;
				return o1.getDate().compareTo(o2.getDate());
			}
		});
		for(int i = 0; i < planValueList.size(); i++)
			if(planValueList.get(i).getMetric() != null && planValueList.get(i).getMetric().equals(metric) && planValueList.get(i).getTimePeriod() != null && planValueList.get(i).getTimePeriod().equals(timePeriod))
				if(planValueList.get(i).getDate() != null && planValueList.get(i).getValue() != null)
					planValuesSeries.set(
						DateFormats.instance().getRussianMonthFormat().format(planValueList.get(i).getDate()),
							planValueList.get(i).getValue());
		if(planValuesSeries.getData().isEmpty())
			planValuesSeries.set(0, 0);
		planValuesSeries.setLabel(retailer.getDisplayName());
		chartModel.addSeries(planValuesSeries);
		CategoryAxis axisX = new CategoryAxis("Месяцы");
		axisX.setTickAngle(0);
		chartModel.getAxes().put(AxisType.X, axisX);
	}
	
	@Getter
	@Setter
	private PlanForecastMetric chartFilterMetric = PlanForecastMetric.Traffic;
	
	@Getter
	@Setter
	private PlanForecastTimePeriodEnum chartFilterTimePeriod = PlanForecastTimePeriodEnum.Yearly;
	
	public enum DetailingObjectsEnum
	{
		Retailer("Торговая сеть"), Brands("Бренды"), Shops("Магазины");
		DetailingObjectsEnum(String title)
		{
			this.title = title;
		}
		
		@Getter
		private String title;
		
		public static DetailingObjectsEnum getByString(String state)
		{
			if(state == null)
				return null;
			for(DetailingObjectsEnum element: DetailingObjectsEnum.values())
				if(element.name().equalsIgnoreCase(state) || element.getTitle().equalsIgnoreCase(state))
					return element;
			return null;
		}
		
		@Override
		public String toString()
		{
			return getTitle();
		}
	}
	
	@Getter
	@Setter
	private DetailingObjectsEnum detailingObjects;
	
	@Getter(lazy = true)
	private final List<SelectItem> allDetailingObjects = initAlldetailingObjects();
	
	private List<SelectItem> initAlldetailingObjects()
	{
		ArrayList<SelectItem> result = new ArrayList<SelectItem>();
		result.add(new SelectItem(DetailingObjectsEnum.Retailer, DetailingObjectsEnum.Retailer.getTitle()));
		result.add(new SelectItem(DetailingObjectsEnum.Brands, DetailingObjectsEnum.Brands.getTitle()));
		result.add(new SelectItem(DetailingObjectsEnum.Shops, DetailingObjectsEnum.Shops.getTitle()));
		return result;
	}
	
	public LineChartModel getPlanValuesChartModel()
	{
		return createPlanValuesChartModel(chartFilterMetric, chartFilterTimePeriod);
	}
	
	public LineChartModel createPlanValuesChartModel(PlanForecastMetric metric, PlanForecastTimePeriodEnum timePeriod)
	{
		LineChartModel dateModel = new LineChartModel();
		dateModel.setAnimate(true);
		dateModel.setShadow(false);
		initChartSeries(dateModel, metric, timePeriod);
		dateModel.setLegendPosition("s");
		dateModel.setLegendCols(3);
		dateModel.setLegendPlacement(LegendPlacement.OUTSIDEGRID);
		dateModel.getAxis(AxisType.Y).setLabel("Показатель плановых значений");
		dateModel.getAxis(AxisType.Y).setTickFormat("%'d");
		return dateModel;
	}
}
