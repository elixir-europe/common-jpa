package eu.crg.ega.microservice.test.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class JpaTestUtils {

  /**
   * Insert data into database.
   *
   * @param sqlScripts : specify the path relative to src/test/resources/
   */
  public static void populateDatabase(DataSource dataSource, String... sqlScripts)
      throws SQLException {

    List<Resource> resources = new ArrayList<Resource>();
    for (String script : sqlScripts) {
      resources.add(new ClassPathResource(script));
    }

    ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
    populator.addScripts(resources.toArray(new Resource[0]));

    Connection connection = null;
    try {
      connection = DataSourceUtils.getConnection(dataSource);
      populator.populate(connection);
    } finally {
      if (connection != null) {
        DataSourceUtils.releaseConnection(connection, dataSource);
      }
    }
  }

}
