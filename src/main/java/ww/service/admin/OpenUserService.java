package ww.service.admin;

import java.util.List;

import org.springframework.web.servlet.ModelAndView;
import ww.service.BaseService;
import ww.common.SqlMap;

import model.OpenUser;
import ww.dao.OpenUserMapper;

public interface OpenUserService extends BaseService {
	
	public OpenUserMapper getMapper();

	public List<OpenUser> getList();
	
	public List<OpenUser> getList(String whereAndOrderby);
	
	public int getCount(String where);
	
	public ModelAndView getPaging(Integer page,String where);

	public List<SqlMap> selectSql(String sql);
	
	public OpenUser getById(java.lang.Integer id);
	
	public boolean isExist(java.lang.Integer id);
	
	public boolean Save(OpenUser data);
	
	public boolean Delete(java.lang.Integer id);
	
}
