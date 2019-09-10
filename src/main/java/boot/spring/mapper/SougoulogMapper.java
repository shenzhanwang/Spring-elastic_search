package boot.spring.mapper;

import java.util.List;

import boot.spring.po.Sougoulog;

public interface SougoulogMapper {
    int insert(Sougoulog record);
    List<Sougoulog> listSougoulogs(); 
}