package ww.service.admin;

import java.util.List;

import org.springframework.web.servlet.ModelAndView;
import ww.service.BaseService;
import ww.common.SqlMap;

import model.OpenApp;
import ww.dao.OpenAppMapper;

public interface OpenAppService extends BaseService {
	
	public OpenAppMapper getMapper();

	public List<OpenApp> getList();
	
	public List<OpenApp> getList(String whereAndOrderby);
	
	public int getCount(String where);
	
	public ModelAndView getPaging(Integer page,String where);

	public List<SqlMap> selectSql(String sql);
	
	public OpenApp getById(java.lang.Integer id);
	
	public boolean isExist(java.lang.Integer id);
	
	public boolean Save(OpenApp data);
	
	public boolean Delete(java.lang.Integer id);
	
}
