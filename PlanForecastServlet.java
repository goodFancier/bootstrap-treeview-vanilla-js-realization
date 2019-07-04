package Shopper.Portal.Servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;

import com.google.gson.Gson;

import Shopper.Common.Date.DateUtils;
import Shopper.Common.Exception.ExceptionHandler;
import Shopper.Common.String.StringUtils;
import Shopper.ORM.Dto.BrandDto;
import Shopper.ORM.Dto.PlanForecastValueDto;
import Shopper.ORM.Dto.RetailerDto;
import Shopper.ORM.Dto.ShopDto;
import Shopper.ORM.Services.DataServiceFactory;
import Shopper.Portal.Controller.PlanForecast.PlanForecastController;

@WebServlet("/planForecastServlet/*")
public class PlanForecastServlet extends HttpServlet
{
	private static final long serialVersionUID = 1853320662391076525L;
	
	public PlanForecastServlet()
	{
		super();
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		try
		{
			processRequest(request, response);
		}
		catch(Exception e)
		{
			ExceptionHandler.handle(e);
		}
	}
	
	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String sqlTimeConditions = getTimeParametrList(request);
		PlanForecastController.timeInverbalConditions = sqlTimeConditions;
		String sqlShopsConditions = getShopParametrList(request);
		PlanForecastController.shopsConditions = sqlShopsConditions;
		if(StringUtils.isRealyEmpty(sqlTimeConditions) && StringUtils.isRealyEmpty(sqlShopsConditions))
		{
			String requestValue = getParametr(request);
			RetailerDto retailer = DataServiceFactory.intance().getRetailersService().findById(Long.parseLong(requestValue));
			List<PlanForecastValueDto> planValueList = DataServiceFactory.intance().getPlanForecastValuesDataService().getByRetailer(retailer);
			List<Map<String, String[]>> simpleList = new ArrayList<>();
			List<Map<String, Map<String, String[]>>> brandsJSON = new ArrayList<>();
			Map<String, String[]> planMap = new HashMap<>();
			Set<String> years = new HashSet<>();
			for(PlanForecastValueDto dto: planValueList)
				if(dto.getDate() != null && (dto.getRetailer() == retailer || dto.getRetailer() == null))
					years.add(DateUtils.getYearFromDate(dto.getDate()));
			planMap.put("year", years.toArray(new String[years.size()]));
			List<BrandDto> brandList = DataServiceFactory.intance().getBrandService().getByRetailer(retailer);
			Map<String, Map<String, String[]>> brandMap = new HashMap<>();
			Map<String, String[]> shopMap = new HashMap<>();
			for(BrandDto brand: brandList)
			{
				List<ShopDto> shopList = DataServiceFactory.intance().getShopsService().getByRetailer(retailer, brand);
				shopMap.put(brand.getName(), shopList.stream().map(ShopDto::getName).collect(Collectors.toList()).toArray(new String[shopList.size()]));
			}
			brandMap.put("brands", shopMap);
			simpleList.add(planMap);
			brandsJSON.add(brandMap);
			List<String> jsonList = new ArrayList<>();
			if(!simpleList.isEmpty())
				jsonList.add(new Gson().toJson(simpleList));
			if(!brandsJSON.isEmpty())
				jsonList.add(new Gson().toJson(brandsJSON));
			if(jsonList.isEmpty())
				jsonList.add(new Gson().toJson("noData"));
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(String.join("%", jsonList));
		}
	}
	
	private String getParametr(HttpServletRequest request) throws Exception
	{
		if(request == null)
			throw new Exception("request is null");
		String result = null;
		result = request.getParameter("retailerID");
		if(!StringUtils.isRealyEmpty(result))
			return result;
		else
			return null;
	}
	
	private String getTimeParametrList(HttpServletRequest request) throws Exception
	{
		List<String> sqlConditionList = new ArrayList<>();
		StringBuilder timeConditions = new StringBuilder();
		if(request == null)
			throw new Exception("request is null");
		JSONObject psItem;
		JSONArray jsonArray;
		String previousMonthValue = "";
		String previousWeekValueStart = "";
		String previousWeekValueEnd = "";
		String previousDateOfWeek = "";
		String year = "";
		final String OR = "OR";
		if(request.getParameter("checkNodesArray") != null)
		{
			jsonArray = new JSONArray(request.getParameter("checkNodesArray"));
			for(int i = 0; i < jsonArray.length(); i++)
			{
				psItem = (JSONObject)jsonArray.get(i);
				String key = psItem.keys().next();
				Object value = psItem.get(key);
				switch(psItem.keys().next())
				{
					case "year":
						year = value.toString();
						if(i + 1 == jsonArray.length() || ((JSONObject)jsonArray.get(i + 1)).keys().next().equals("year"))
							sqlConditionList.add(String.format(" a.Date BETWEEN '%s.01.01' AND '%s.12.31' ", year, year));
						break;
					case "quarter":
						if(i + 1 == jsonArray.length() || ((JSONObject)jsonArray.get(i + 1)).keys().next().equals("quarter") || ((JSONObject)jsonArray.get(i + 1)).keys().next().equals("year"))
							sqlConditionList.add(String.format(" a.Date BETWEEN '%s.%s' AND '%s.%s' + INTERVAL 1 QUARTER ", year, value, year, value));
						break;
					case "month":
						if(i + 1 == jsonArray.length() || ((JSONObject)jsonArray.get(i + 1)).keys().next().equals("month") || ((JSONObject)jsonArray.get(i + 1)).keys().next().equals("quarter") || ((JSONObject)jsonArray.get(i + 1)).keys().next().equals("year"))
							sqlConditionList.add(String.format(" a.Date BETWEEN '%s.%s.01' AND '%s.%s.01' + INTERVAL 1 MONTH ", year, value, year, value));
						previousMonthValue = value.toString();
						break;
					case "week":
						String[] weekDates = value.toString().split(" - ");
						if(i + 1 == jsonArray.length() || ((JSONObject)jsonArray.get(i + 1)).keys().next().equals("week") || ((JSONObject)jsonArray.get(i + 1)).keys().next().equals("month") || ((JSONObject)jsonArray.get(i + 1)).keys().next().equals("quarter") || ((JSONObject)jsonArray.get(i + 1)).keys().next().equals("year"))
							sqlConditionList.add(String.format(" a.Date BETWEEN '%s.%s.%s' AND '%s.%s.%s' ", year, previousMonthValue, weekDates[0], year, previousMonthValue, weekDates[1]));
						previousWeekValueStart = String.format("%s.%s.%s", year, previousMonthValue, weekDates[0]);
						previousWeekValueEnd = String.format("%s.%s.%s", year, previousMonthValue, weekDates[1]);
						break;
					case "dayOfWeek":
						if(i + 1 == jsonArray.length() || !((JSONObject)jsonArray.get(i + 1)).keys().next().equals("hour"))
							sqlConditionList.add(String.format(" a.Date BETWEEN '%s' AND '%s' AND WEEKDAY(a.Date) = %s ", previousWeekValueStart, previousWeekValueEnd, value));
						previousDateOfWeek = value.toString();
						break;
					case "hour":
						sqlConditionList.add(String.format(" a.Date BETWEEN '%s' AND '%s' AND WEEKDAY(a.Date) = %s AND HOUR(a.Date) = '%s' ", previousWeekValueStart, previousWeekValueEnd, previousDateOfWeek, value));
						break;
				}
			}
		}
		for(int i = 0; i < sqlConditionList.size(); i++)
		{
			timeConditions.append(sqlConditionList.get(i));
			if(i != sqlConditionList.size() - 1)
				timeConditions.append(OR);
		}
		return timeConditions.toString();
	}
	
	private String getShopParametrList(HttpServletRequest request) throws Exception
	{
		List<String> sqlConditionList = new ArrayList<>();
		StringBuilder shopConditions = new StringBuilder();
		if(request == null)
			throw new Exception("request is null");
		JSONObject psItem;
		JSONArray jsonArray;
		String previousMonthValue = "";
		String previousWeekValueStart = "";
		String previousWeekValueEnd = "";
		String previousDateOfWeek = "";
		String year = "";
		final String OR = "OR";
		if(request.getParameter("shopsArray") != null)
		{
			jsonArray = new JSONArray(request.getParameter("shopsArray"));
			for(int i = 0; i < jsonArray.length(); i++)
			{
				psItem = (JSONObject)jsonArray.get(i);
				String key = psItem.keys().next();
				Object value = psItem.get(key);
				switch(psItem.keys().next())
				{
					case "shop":
						sqlConditionList.add(String.format(" a1.Date BETWEEN '%s.01.01' AND '%s.12.31' ", year, year));
						break;
					case "quarter":
						if(i + 1 == jsonArray.length() || ((JSONObject)jsonArray.get(i + 1)).keys().next().equals("quarter") || ((JSONObject)jsonArray.get(i + 1)).keys().next().equals("year"))
							sqlConditionList.add(String.format(" a.Date BETWEEN '%s.%s' AND '%s.%s' + INTERVAL 1 QUARTER ", year, value, year, value));
						break;
					case "month":
						if(i + 1 == jsonArray.length() || ((JSONObject)jsonArray.get(i + 1)).keys().next().equals("month") || ((JSONObject)jsonArray.get(i + 1)).keys().next().equals("quarter") || ((JSONObject)jsonArray.get(i + 1)).keys().next().equals("year"))
							sqlConditionList.add(String.format(" a.Date BETWEEN '%s.%s.01' AND '%s.%s.01' + INTERVAL 1 MONTH ", year, value, year, value));
						previousMonthValue = value.toString();
						break;
					case "week":
						String[] weekDates = value.toString().split(" - ");
						if(i + 1 == jsonArray.length() || ((JSONObject)jsonArray.get(i + 1)).keys().next().equals("week") || ((JSONObject)jsonArray.get(i + 1)).keys().next().equals("month") || ((JSONObject)jsonArray.get(i + 1)).keys().next().equals("quarter") || ((JSONObject)jsonArray.get(i + 1)).keys().next().equals("year"))
							sqlConditionList.add(String.format(" a.Date BETWEEN '%s.%s.%s' AND '%s.%s.%s' ", year, previousMonthValue, weekDates[0], year, previousMonthValue, weekDates[1]));
						previousWeekValueStart = String.format("%s.%s.%s", year, previousMonthValue, weekDates[0]);
						previousWeekValueEnd = String.format("%s.%s.%s", year, previousMonthValue, weekDates[1]);
						break;
					case "dayOfWeek":
						if(i + 1 == jsonArray.length() || !((JSONObject)jsonArray.get(i + 1)).keys().next().equals("hour"))
							sqlConditionList.add(String.format(" a.Date BETWEEN '%s' AND '%s' AND WEEKDAY(a.Date) = %s ", previousWeekValueStart, previousWeekValueEnd, value));
						previousDateOfWeek = value.toString();
						break;
					case "hour":
						sqlConditionList.add(String.format(" a.Date BETWEEN '%s' AND '%s' AND WEEKDAY(a.Date) = %s AND HOUR(a.Date) = '%s' ", previousWeekValueStart, previousWeekValueEnd, previousDateOfWeek, value));
						break;
				}
			}
		}
		for(int i = 0; i < sqlConditionList.size(); i++)
		{
			shopConditions.append(sqlConditionList.get(i));
			if(i != sqlConditionList.size() - 1)
				shopConditions.append(OR);
		}
		return shopConditions.toString();
	}
}
