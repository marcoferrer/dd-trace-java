package datadog.trace.instrumentation.springwebflux;

import static java.util.Collections.singletonMap;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

import com.google.auto.service.AutoService;
import datadog.trace.agent.tooling.Instrumenter;
import java.util.Map;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

@AutoService(Instrumenter.class)
public final class HandlerFunctionAdapterInstrumentation extends Instrumenter.Default {

  public static final String PACKAGE =
      HandlerFunctionAdapterInstrumentation.class.getPackage().getName();

  public HandlerFunctionAdapterInstrumentation() {
    super("spring-webflux", "spring-webflux-functional");
  }

  @Override
  public ElementMatcher<TypeDescription> typeMatcher() {
    return named("org.springframework.web.reactive.function.server.support.HandlerFunctionAdapter");
  }

  @Override
  public Map<? extends ElementMatcher<? super MethodDescription>, String> transformers() {
    return singletonMap(
        isMethod()
            .and(isPublic())
            .and(named("handle"))
            .and(takesArgument(0, named("org.springframework.web.server.ServerWebExchange")))
            .and(takesArguments(2)),
        // Cannot reference class directly here because it would lead to class load failure on Java7
        PACKAGE + ".HandlerFunctionAdapterAdvice");
  }
}
