package boot.spring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import boot.spring.pagemodel.CityGrid;
import boot.spring.po.City;
import boot.spring.po.Country;
import boot.spring.service.CityService;


@Controller
public class CityController {
	@Autowired
	CityService cityservice;
	
	
	@RequestMapping(value="/getcitys",method = RequestMethod.GET)
	@ResponseBody
	CityGrid getcitys(@RequestParam("current") int current,@RequestParam("rowCount") int rowCount){
		int total=cityservice.getCitylist().size();
		List<City> list=cityservice.getpagecitylist(current,rowCount);
		CityGrid grid=new CityGrid();
		grid.setCurrent(current);
		grid.setRowCount(rowCount);
		grid.setRows(list);
		grid.setTotal(total);
		return grid;
	}
	
	@RequestMapping(value="/city",method = RequestMethod.GET)
	String city(){
		return "city";
	}
	
	@RequestMapping(value="/country",method = RequestMethod.GET)
	String country(){
		return "country";
	}
	
	@RequestMapping(value="/getchainacity",method = RequestMethod.GET)
	@ResponseBody
	CityGrid getchinacity(@RequestParam("current") int current,@RequestParam("rowCount") int rowCount){
		List<City> citys=cityservice.getCountryCity("China");
		int total=citys.size();
		List<City> clist=cityservice.getpageCountryCity("China", current, rowCount);
		CityGrid grid=new CityGrid();
		grid.setCurrent(current);
		grid.setRowCount(rowCount);
		grid.setRows(clist);
		grid.setTotal(total);
		return grid;
	}
	
	
	@RequestMapping(value="/countrycity",method = RequestMethod.GET)
	@ResponseBody
	Country getcountrycity(@RequestParam("country")String country){
		Country a=cityservice.getCountryCitys(country);
		return a;
	}
	
}
