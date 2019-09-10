package boot.spring.service;

import java.util.List;
import java.util.Optional;

import boot.spring.po.City;
import boot.spring.po.Country;


public interface CityService {
	// mybatis的方法
	List<City> getCitylist();
	List<City> getpagecitylist(int pagenum, int pagesize);
	List<City> getCountryCity(String Countryname);
	List<City> getpageCountryCity(String Countryname,int pagenum, int pagesize);
	Country getCountryCitys(String Countryname);//一对多
	
}
