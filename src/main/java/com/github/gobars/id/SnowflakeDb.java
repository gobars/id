package com.github.gobars.id;

public class SnowflakeDb extends Snowflake {
  private final WorkerIdDb workerIdDb;

  public SnowflakeDb(Conf conf, WorkerIdDb workerIdDb) {
    super(conf, workerIdDb.reason("start").workerId());
    this.workerIdDb = workerIdDb;
  }

  @Override
  protected void rotateBackwardId(long cur) {
    workerIdDb.reason("backwards");
    this.workerId = workerIdDb.workerId();
  }
}
