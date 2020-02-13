package util;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;
import task.DBInit;

import javax.sql.DataSource;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtil {
    private static volatile DataSource DATA_SOURCE;

    /**
     * 提供获取数据库连接的功能
     * 使用单例模式（多线程安全方式）
     * @return
     */
    private static DataSource getDataSource(){
        if (DATA_SOURCE == null) { // 提高效率，使用volatile可以保证及时的可见性
            synchronized (DBInit.class) {
                if (DATA_SOURCE == null) {
                    //初始化操作，使用volatile关键字静止指令重排序
                    SQLiteConfig config = new SQLiteConfig();
                    config.setDateStringFormat(Util.DATA_PATTERN);
                    DATA_SOURCE = new SQLiteDataSource();
                    ((SQLiteDataSource)DATA_SOURCE).setUrl(getURL());
                }
            }
        }
        return DATA_SOURCE;
    }

    /**
     * 获取数据库URL方法
     * @return
     */
    private static String getURL() {
        String url = null;
        try {
            //获取target编译文件夹的路径
            //通过classLoader.getResource()这样的方法
            //默认的路径为编译文件夹路径（target/class）
              URL classesURL = DBUtil.class.getClassLoader().getResource("./");
            //获取target/classes文件夹的父目录路径
            String dir = new File(classesURL.getPath()).getParent();
            url = "jdbc:sqlite://" + dir + File.separator + "FileSearch.db";
            //new SqliteDateSource(),把这个对象的url设置进去
            //就会创建这个文件，如果文件存在就读取这个文件
            url = URLDecoder.decode(url,"UTF-8");
            System.out.println("获取数据库文件路径" + url);
            return url;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException("获取数据库文件路径失败");
        }

    }
    /**
     * 提供获取数据库连接的方法：
     * 从数据库连接池DataSource.get
     */
    public static Connection getconnection() throws SQLException {
        return getDataSource().getConnection();
    }

    public static void main(String[] args) throws SQLException {
        getconnection();
    }

    public static void close(Connection connection, Statement statement) {
        try {
            if (connection != null)
                connection.close();
            if(statement != null)
                statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("释放数据库资源错误");
        }
    }
    public static void close(Connection connection, Statement statement, ResultSet resultset) {
        try {
            if (connection != null)
                connection.close();
            if(statement != null)
                statement.close();
            if(resultset != null)
                resultset.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("释放数据库资源错误");
        }
    }
}
