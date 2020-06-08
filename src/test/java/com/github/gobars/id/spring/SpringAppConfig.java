package com.github.gobars.id.spring;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.gobars.id.IdNext;
import com.github.gobars.id.conf.Conf;
import com.github.gobars.id.conf.ConnGetter;
import com.github.gobars.id.db.SnowflakeDb;
import com.github.gobars.id.db.WorkerIdDb;
import javax.sql.DataSource;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringAppConfig {
  @Bean
  public DataSource getDataSource() {
    val dataSource = new DruidDataSource();
    dataSource.setDriverClassName("com.mysql.jdbc.Driver");
    dataSource.setUrl("jdbc:mysql://localhost:3306/id?useSSL=false");
    dataSource.setUsername("root");
    dataSource.setPassword("root");

    return dataSource;
  }

  @Bean
  public IdNext idNext(@Autowired DataSource dataSource) {
    val connGetter = new ConnGetter.DsConnGetter(dataSource);
    val workerIdDb = new WorkerIdDb().table("worker_id").connGetter(connGetter).biz("default");
    val spec = "epoch=20200603,timestampBits=41,backwardBits=0,workerBits=10,seqBits=12,roundMs=1";
    return new SnowflakeDb(Conf.fromSpec(spec), workerIdDb);
  }
}
