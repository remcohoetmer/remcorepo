package nl.cerios.demo.processor;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class Group {
  int id;
  String remco;

  public Group(int id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "Group {" +
      "id='" + id + '\'' +
      ", remco='" + remco + '\'' +
      '}';
  }
}

public class FlatMapManyTest {

  final static Logger logger = Logger.getLogger(FlatMapManyTest.class.getName());

  @Test
  public void testHandle() {
    Flux<Group> g = Flux.just(new Group(1), new Group(2), new Group(3));
    Mono<List<Group>> list = g.collectList();
    Flux<Group> flux = list.flatMapMany(Flux::fromIterable);
    flux.log("flatMap", Level.FINE).subscribe();

  }

  static Flux<Group> f(Group group) {
    if (group.id != 2)
      return Flux.just(group);
    else
      return Flux.never();//Flux.just(group);
  }

  @Test
  public void testConcat() throws InterruptedException {
    Flux<Long> pulse = Flux.interval(Duration.ofMillis(100));
    Flux<Group> groups = Flux.just(new Group(1), new Group(2), new Group(3));
    Flux<Group> g = pulse.zipWith(groups).map(tuple -> tuple.getT2());
    Flux<Group> list = g.flatMap(FlatMapManyTest::f);
    list.log("flatMap", Level.FINE).subscribe();
//    Flux<Group> list = g.flatMap(FlatMapManyTest::f);
    g.log("g flatMap", Level.FINE).subscribe();

    Flux<Group> list2 = g.concatMap(FlatMapManyTest::f);
    list.log("concatMap", Level.FINE).subscribe();

    Thread.sleep(2000);
  }

}
