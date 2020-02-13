package task;

import util.DBUtil;

import java.io.*;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;

/**
 * 1.初始化数据库
 * 调用DBUtil的getconnection()方法
 * 2.读取SQL文件
 *
 * 3.执行SQL语句来初始化表
 */

public class DBInit {

    public static String[] readSQL() {
        try {
            //通过ClassLoader获取
            InputStream is = DBInit.class.getClassLoader()
                            .getResourceAsStream("init.sql");
            //字节流转换为字符流
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(is,"UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                if(line.contains("--")){//去掉--注释的代码
                    line = line.substring(0,line.indexOf("--"));
                }
                sb.append(line);
            }
            String[] sqls = sb.toString().split(";");
            return sqls;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("读取sql文件错误");
        }
    }



    public static void init() {
        Connection connection = null;
        Statement statement = null;
        try {
            //1.建立数据库连接
            connection = DBUtil.getconnection();
            //2.创建SQL语句执行对象 Statement
            statement = connection.createStatement();
            String[] sqls = readSQL();
            for (String sql : sqls) {
                //3.执行SQL语句
                statement.executeUpdate(sql);
            }
            //4.获取并处理结果集
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("初始化数据库表操作失败",e);
        }finally{
            //5.释放资源
            DBUtil.close(connection, statement);
        }
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(readSQL()));
        init();
    }
}
