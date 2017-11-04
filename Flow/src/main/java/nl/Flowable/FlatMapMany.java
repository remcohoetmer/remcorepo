package nl.Flowable;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class FlatMapMany {

  public static void main(String args[]) {

    Flux<String> flux = Flux.fromIterable(Arrays.asList(new String[]{"Remco", "Roger", "Todd", "Sander"}));
    Mono<List<String>> mono = Mono.just(Arrays.asList(new String[]{"Remco", "Roger", "Todd", "Sander"}));
    Mono<Flux<String>> flux1= mono.map(Flux::fromIterable);
    Flux<String> v= flux1.flatMapMany(Function.identity());
    v.doOnNext(System.err::println).subscribe();
  }

}
