package boot.spring.mapper;

import java.util.List;

import boot.spring.po.Country;

public interface CountryMapper {
    int deleteByPrimaryKey(Short countryId);

    int insert(Country record);

    Country selectByPrimaryKey(Short countryId);

    int updateByPrimaryKeySelective(Country record);
    
    List<Country> selectCountrys();
}