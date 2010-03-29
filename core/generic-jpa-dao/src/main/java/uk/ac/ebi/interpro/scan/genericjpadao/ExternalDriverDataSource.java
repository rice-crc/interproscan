package uk.ac.ebi.interpro.scan.genericjpadao;

import org.springframework.jdbc.datasource.AbstractDriverBasedDataSource;

import javax.sql.DataSource;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Datasource which loads a driver from a specified jar file.
 */

public class ExternalDriverDataSource extends AbstractDriverBasedDataSource {
    private volatile boolean loaded;
    private SQLException failure;
    private Driver driver;
    private String driverJar;
    private String driverClassName;
    private final Object driverLoadLock=new Object();

    private static Map<String,Driver> driverCache=new HashMap<String,Driver>();

    /**
     * Set location of jar file and optionally the class name of the JDBC driver.
     * path/to/driver.jar
     * path/to/driver.jar:className
     *
     * If the className is not supplied then the driver class will be located from
     * the file META-INF/services/java.sql.Driver in the jar.
     *
     * @param driverJar file name of jar file containing driver.
     */
    public void setDriverJar(String driverJar) {
        this.driverJar = driverJar;
    }

    /**
     * Set driver class.
     * This should only be set to a class in the default class path.
     * This will be overridden by a specified driverJar
     *
     * @param driverClassName class name of driver class
     */
    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }


    /**
     * Get the driver class.
     *
     * Load from the specified jar file,
     * or if not specified,
     * the context class loader of the calling method.
     *
     * @return the driver class
     * @throws SQLException wrapping any error in class construction of the driver
     */
    public Driver getDriver() throws SQLException {

        if (!loaded) {
            synchronized (driverLoadLock) {
                try {
                    loadDriver();
                } catch (SQLException e) {
                    failure=e;
                } catch (Exception e) {
                    failure=new SQLException("Unable to load driver",e);
                }
                loaded=true;
            }
        }

        if (failure!=null) throw failure;
        return driver;


    }

    private void loadDriver() throws IOException, SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {

        driverClassName=setNullIfEmpty(driverClassName);

        driverJar=setNullIfEmpty(driverJar);

        if (driverJar==null && driverClassName==null) {
            throw new IllegalStateException("ExternalDriverDataSource must have a driverJar or a driverClassName set");
        }


        String key=driverJar;
        if (key==null) key=driverClassName;

        driver=driverCache.get(key);
        if (driver==null) {
            ClassLoader driverClassLoader;
            if (driverJar == null) {
                driverClassLoader = Thread.currentThread().getContextClassLoader();
            } else {
                String[] parts=driverJar.split(":",2);
                if (parts.length==1) {
                    driverClassName=null;
                } else {
                    driverClassName=parts[1];
                }

                URL driverFileURL=new File(parts[0]).toURI().toURL();
                URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{driverFileURL});
                if (driverClassName==null)  {
                    driverClassName=discoverDriverClassName(urlClassLoader);
                }
                driverClassLoader=urlClassLoader;
            }

            if (driverClassName==null) throw new SQLException("Driver class name not specified or discovered");
            driver=(Driver) driverClassLoader.loadClass(driverClassName).newInstance();

            driverCache.put(key,driver);
        }
    }


    /**
     * Set to null if input contains only whitespace
     *
     * @param in string to be checked
     * @return null or the input string
     */
    private String setNullIfEmpty(String in) {
        if (in!=null && in.trim().length()==0) {
            return null;
        }
        return in;
    }


    /**
     * Read class name from the service definition.
     * Reads from META-INF/services/java.sql.Driver
     *
     * @param urlClassLoader from which to load the driver definition
     * @return class name of driver class, or null if not found.
     * @throws IOException on failure to read
     */
    private String discoverDriverClassName(URLClassLoader urlClassLoader) throws IOException {
        String className=null;
        URL resource = urlClassLoader.findResource("META-INF/services/java.sql.Driver");
        if (resource!=null) {
            InputStream is= resource.openStream();
            BufferedReader br=new BufferedReader(new InputStreamReader(is));
            className=br.readLine();
            br.close();
        }
        return className;
    }


    @Override
    public String toString() {
        return "ExternalDriverDataSource{" +
                "driverJar='" + driverJar + '\'' +
                ", driverClassName='" + driverClassName + '\'' +
                ", loaded=" + loaded +
                '}';
    }

    @Override
    protected Connection getConnectionFromDriver(Properties properties) throws SQLException {
        return getDriver().connect(getUrl(), properties);
    }
}
