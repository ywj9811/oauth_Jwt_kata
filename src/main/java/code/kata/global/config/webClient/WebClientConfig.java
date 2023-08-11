package code.kata.global.config.webClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.LoggingCodecSupport;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
@Slf4j
public class WebClientConfig {
    @Bean
    public WebClient webClient() {
        /**
         * HttpClient 를 변경하거나 ConnectionTimeOut 과 같은 설정값을 변경하려면
         * WebClient.builder().clientConnector() 를 통해
         * Reactor Netty의 HttpClient 를 직접 설정해 줘야 합니다.
         */

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(getHttpClient())) // 응답 시간 제한
                .exchangeStrategies(getExchangeStrategies())
                .filter(getRequestProcessor())
                .filter(getResponseProcessor())
                .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36")
                .build();
    }

    private static ExchangeFilterFunction getResponseProcessor() {
        return ExchangeFilterFunction.ofResponseProcessor(
                clientResponse -> {
                    clientResponse.headers().asHttpHeaders().forEach((name, values) -> values.forEach(value -> log.debug("{} : {}", name, value)));
                    return Mono.just(clientResponse);
                }
        );
    }

    private static ExchangeFilterFunction getRequestProcessor() {
        return ExchangeFilterFunction.ofRequestProcessor(
                clientRequest -> {
                    log.debug("Request: {} {}", clientRequest.method(), clientRequest.url());
                    clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.debug("{} : {}", name, value)));
                    return Mono.just(clientRequest);
                }
        );
    }

    private static HttpClient getHttpClient() {
        /**
         * Request 또는 Response 데이터에 대해 조작을 하거나 추가 작업을 하기 위해서는 WebClient.builder().filter() 메소드를 이용해야함
         *
         * ExchangeFilterFunction.ofRequestProcessor() 와
         * ExchangeFilterFunction.ofResponseProcessor() 를 통해
         * clientRequest 와 clientResponse 를 변경하거나 출력할 수 있습니다.
         */
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000) //연결 시간 제한
                .doOnConnected(connection -> connection
                        .addHandlerLast(new ReadTimeoutHandler(3))
                        .addHandlerLast(new WriteTimeoutHandler(3))) //읽기, 쓰기 제한 시간
                .responseTimeout(Duration.ofSeconds(2));
    }

    private static ExchangeStrategies getExchangeStrategies() {
        /**
         * size가 기본 256KB 따라서 더 늘리기 위해서는 다음과 같이 지정을 해줘야 함
         */
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024 * 50))
                .build();
        /**
         * Debug 레벨 일 때 form Data 와 Trace 레벨 일 때 header 정보는 민감한 정보를 포함하고 있기 때문에,
         * 기본 WebClient 설정에서는 위 정보를 로그에서 확인할 수 가 없음
         * 개발 진행 시 Request/Response 정보를 상세히 확인하기 위해서는
         * ExchageStrateges 와 logging level 설정을 통해 로그 확인이 가능하도록 해 주는 것이 좋다.
         */
        /**
         * ExchangeStrategies 를 통해 setEnableLoggingRequestDetails(boolen enable) 을 true 로 설정해 주고
         * application.yaml 에 개발용 로깅 레벨은 DEBUG 로 설정하자.
         *
         * logging:
         *   level:
         *     org.springframework.web.reactive.function.client.ExchangeFunctions: DEBUG
         */
        exchangeStrategies
                .messageWriters().stream()
                .filter(LoggingCodecSupport.class::isInstance)
                .forEach(writer -> ((LoggingCodecSupport)writer).setEnableLoggingRequestDetails(true));
        return exchangeStrategies;
    }
}
