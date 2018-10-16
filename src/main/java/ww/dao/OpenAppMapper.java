package ww.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import ww.common.SqlMap;

import model.OpenApp;

@Repository
public interface OpenAppMapper {
	public OpenApp getById(java.lang.Integer id);
	
    public List<model.OpenApp> getByOpenUserId(java.lang.Integer openUserId);
    public List<model.OpenApp> getByAppName(java.lang.String appName);
    public List<model.OpenApp> getByAppId(java.lang.String appId);
    public List<model.OpenApp> getBySecure(java.lang.String secure);
    public List<model.OpenApp> getByIcon(java.lang.String icon);


    public List<OpenApp> getAll();
    public List<OpenApp> getList(String whereAndOrderby);
    public List<OpenApp> getList2(String where,String orderby,int limit_begin,int limit_len);
    public List<OpenApp> getList3(String where,int limit_begin,int limit_len);
    public List<OpenApp> getList4(String orderby,int limit_begin,int limit_len);
    public List<SqlMap> selectSql(String sql);
    public int getCount(String where);
    public int insert(OpenApp obj);
    public void update(OpenApp obj);
	public void delete(java.lang.Integer id);
	public void deleteWhere(String where);
}
