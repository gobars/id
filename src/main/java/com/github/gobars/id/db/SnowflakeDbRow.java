package com.github.gobars.id.db;

import com.github.gobars.id.Snowflake;
import com.github.gobars.id.WorkerId;
import com.github.gobars.id.conf.Conf;

public class SnowflakeDbRow extends Snowflake {
  private final WorkerIdDbRow workerIdDb;

  public SnowflakeDbRow(Conf conf, WorkerIdDbRow workerIdDb) {
    super(conf, workerIdDb.workerId());
    this.workerIdDb = workerIdDb;
  }

  @Override
  protected void rotateBackwardId(long cur) {
    this.workerId = workerIdDb.workerId();
  }
}
