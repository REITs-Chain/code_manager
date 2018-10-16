package ww.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import ww.common.SqlMap;

import model.AuthEvidence;

@Repository
public interface AuthEvidenceMapper {
	public AuthEvidence getById(java.lang.Integer id);
	
    public List<model.AuthEvidence> getByUserId(java.lang.Integer userId);
    public List<model.AuthEvidence> getByOrginFile(java.lang.String orginFile);
    public List<model.AuthEvidence> getByNewFile(java.lang.String newFile);


    public List<AuthEvidence> getAll();
    public List<AuthEvidence> getList(String whereAndOrderby);
    public List<AuthEvidence> getList2(String where,String orderby,int limit_begin,int limit_len);
    public List<AuthEvidence> getList3(String where,int limit_begin,int limit_len);
    public List<AuthEvidence> getList4(String orderby,int limit_begin,int limit_len);
    public List<SqlMap> selectSql(String sql);
    public int getCount(String where);
    public int insert(AuthEvidence obj);
    public void update(AuthEvidence obj);
	public void delete(java.lang.Integer id);
	public void deleteWhere(String where);
}
