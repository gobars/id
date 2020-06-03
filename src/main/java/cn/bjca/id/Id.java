package cn.bjca.id;

import lombok.experimental.UtilityClass;
import lombok.val;

/**
 * ID生成器入口类。
 *
 * @author bingoohuang
 */
@UtilityClass
public class Id {
  private Next next;

  static {
    val c = new WorkerIdComposite(new WorkerIdEnv(), new WorkerIdIp(), new WorkerIdRandom());
    configure(new Snowflake(new Snowflake.Conf(), c.workerId()));
  }

  public void configure(Next next) {
    Id.next = next;
  }

  /**
   * 获得下一个ID.
   *
   * @return 下一个ID.
   */
  public long next() {
    return next.next();
  }
}
