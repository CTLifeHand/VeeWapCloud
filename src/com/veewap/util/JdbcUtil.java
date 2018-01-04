package com.veewap.util;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class JdbcUtil {
	private JdbcUtil() {
	}
	
	private static Properties p = new Properties();
	private static DataSource dataSource = null;
	
	// 静态方法
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			p.load(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("db.properties"));
			TCUtil.path = p.getProperty("path");
//			System.out.println(p.getProperty("path"));
			dataSource = DruidDataSourceFactory.createDataSource(p);
		} catch (IOException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//释放资源
		public static void close(Connection conn, Statement st, ResultSet rs) {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (st != null) {
						st.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (conn != null) {
							conn.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	
	public static Connection getConn() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	
}
