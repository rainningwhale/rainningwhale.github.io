package cn.huzhihong.dao.Impl;

import cn.huzhihong.dao.UserDao;
import cn.huzhihong.domain.Login;
import cn.huzhihong.domain.User;
import cn.huzhihong.util.JDBCutils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserDaoImpl implements UserDao {

    private JdbcTemplate template = new JdbcTemplate(JDBCutils.getDataSource());
    @Override
    public List<User> findAll() {

        //使用JDBC完成数据库操作
        //1.定义sql
        String sql = "select * from user";
        List<User> users = template.query(sql, new BeanPropertyRowMapper<User>(User.class));

        return users;
    }

    @Override
    public Login findUserByUsernameAndPassword(String username, String password){

        try {
            String sql = "select * from Login where username = ? and password = ?";
             Login login = template.queryForObject(sql,new BeanPropertyRowMapper<Login>(Login.class),username,password);
            return login;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public void add(User user) {
        //1.定义sql
        String sql = "insert into user values(null,?,?,?,?,?,?)";
        //2.执行sql
        template.update(sql,user.getName(),user.getGender(),user.getAge(),user.getAddress(),user.getQq(),user.getEmail());
    }

    @Override
    public void delete(int parseInt) {
        //1.定义sql
        String sql = "delete from user where id = ?";
        //2.执行sql
        template.update(sql,parseInt);
    }

    @Override
    public User findById(int id) {
        String sql = "select * from user where id = ?";
        return template.queryForObject(sql,new BeanPropertyRowMapper<User>(User.class),id);

    }

    @Override
    public void Update(User user) {
        //1.定义sql
        String sql = "update user set  gender = ? , age=?, address=? ,qq=?, email=? where id=?";
        template.update(sql,user.getGender(),user.getAge(),user.getAddress(),user.getQq(),user.getEmail(),user.getId());
    }

    @Override
    public int findTotalCount(Map<String, String[]> condition) {
//        String sql = "select count(*) from user";

        //1.定义模板初始化sql
        String sql = "select count(*) from user where 1 = 1";
        StringBuilder sb = new StringBuilder(sql);
        //2.遍历map
        Set<String> keySet = condition.keySet();
        //定义参数的集合
        List<Object> params = new ArrayList<Object>();
        for (String key : keySet) {
            //排除分页条件参数
            if("currentPage".equals(key) || "rows".equals(key)){
                continue;
            }
            //获取value
            String value = condition.get(key)[0];
            //判断value是否有值
            if(value != null && !"".equals(value)){
                //有值
                sb.append(" and "+key+" like ? ");
                params.add("%"+value+"%"); //?条件的值

            }
        }

        System.out.println(sb.toString());
        System.out.println(params);
//        return template.queryForObject(sql,Integer.class);
        return template.queryForObject(sb.toString(),Integer.class,params.toArray());

    }

    @Override
    public List<User> findByPage(int start, int rows, Map<String, String[]> condition) {
        String sql = "select * from user where 1 = 1";

        StringBuilder sb = new StringBuilder(sql);
        //2.遍历map
        Set<String> keySet = condition.keySet();
        //定义参数的集合
        List<Object> params = new ArrayList<Object>();
        for (String key : keySet) {
            //排除分页条件参数
            if("currentPage".equals(key) || "rows".equals(key)){
                continue;
            }
            //获取value
            String value = condition.get(key)[0];
            //判断value是否有值
            if(value != null && !"".equals(value)){
                //有值
                sb.append(" and "+key+" like ? ");
                params.add("%"+value+"%"); //?条件的值

            }
        }

        //添加分页查询
        sb.append(" limit ? , ? ");
        //添加分页查询参数值
        params.add(start);
        params.add(rows);
        sql=sb.toString();
        System.out.println(sql);
        System.out.println(params);
        return template.query(sql,new BeanPropertyRowMapper<User>(User.class),params.toArray());
    }
}
