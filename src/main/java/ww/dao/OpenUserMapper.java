package ww.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import ww.common.SqlMap;

import model.OpenUser;

@Repository
public interface OpenUserMapper {
	public OpenUser getById(java.lang.Integer id);
	
    public List<model.OpenUser> getByPhoneNum(java.lang.String phoneNum);
    public List<model.OpenUser> getByPassword(java.lang.String password);
    public List<model.OpenUser> getByType(java.lang.Integer type);
    public List<model.OpenUser> getByRealName(java.lang.String realName);
    public List<model.OpenUser> getByIdNum(java.lang.String idNum);
    public List<model.OpenUser> getByPhoto1(java.lang.String photo1);
    public List<model.OpenUser> getByPhoto2(java.lang.String photo2);
    public List<model.OpenUser> getByLinkMan(java.lang.String linkMan);
    public List<model.OpenUser> getByLinkPhone(java.lang.String linkPhone);
    public List<model.OpenUser> getByStatus(java.lang.Integer status);


    public List<OpenUser> getAll();
    public List<OpenUser> getList(String whereAndOrderby);
    public List<OpenUser> getList2(String where,String orderby,int limit_begin,int limit_len);
    public List<OpenUser> getList3(String where,int limit_begin,int limit_len);
    public List<OpenUser> getList4(String orderby,int limit_begin,int limit_len);
    public List<SqlMap> selectSql(String sql);
    public int getCount(String where);
    public int insert(OpenUser obj);
    public void update(OpenUser obj);
	public void delete(java.lang.Integer id);
	public void deleteWhere(String where);
}
