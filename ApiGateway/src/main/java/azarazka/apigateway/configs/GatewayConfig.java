package azarazka.apigateway.configs;

import azarazka.apigateway.service.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
@LoadBalancerClient(name = "lb")
public class GatewayConfig {
    public static final String AUTHORIZATION = "Authorization";
    private final JwtUtils jwtUtils;
    private final String authenticationServiceUri;
    private final String postServiceUri;
    private final String userServiceUri;
    private final String commentServiceUri;

    public GatewayConfig(
            JwtUtils jwtUtils,
            @Value("${app.authentication-service.uri}") String authenticationServiceUri,
            @Value("${app.post-service.uri}") String postServiceUri,
            @Value("${app.user-service.uri}") String userServiceUri,
            @Value("${app.comment-service.uri}") String commentServiceUri) {
        this.jwtUtils = jwtUtils;
        this.authenticationServiceUri = authenticationServiceUri;
        this.postServiceUri = postServiceUri;
        this.userServiceUri = userServiceUri;
        this.commentServiceUri = commentServiceUri;
    }


    @Bean
    public RouterFunction<ServerResponse> discoveryRouterFunction() {
        return GatewayRouterFunctions.route("DiscoveryServer")
                .route(RequestPredicates.path("/eureka/web"), HandlerFunctions.http("http://eureka:password@DiscoveryServer:8761"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> discoveryStaticRouterFunction() {
        return GatewayRouterFunctions.route("discovery-server-static")
                .route(RequestPredicates.path("/eureka/**"), HandlerFunctions.http("http://eureka:password@DiscoveryServer:8761"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> authenticationRouterFunction() {
        return GatewayRouterFunctions.route("AuthenticationService")
                .route(RequestPredicates.path("/auth/**"), HandlerFunctions.http(authenticationServiceUri))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> userRouterFunction() {
        return GatewayRouterFunctions.route("UserService")
                .route(RequestPredicates.path("/api/users/**"), HandlerFunctions.http(userServiceUri))
                .filter(this::authentication)
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> postRouterFunction() {
        return GatewayRouterFunctions.route("PostService")
                .route(RequestPredicates.path("/api/posts/**"), HandlerFunctions.http(postServiceUri))
                .filter(this::authentication)
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> commentRouterFunction() {
        return GatewayRouterFunctions.route("CommentService")
                .route(RequestPredicates.path("/api/comments/**"), HandlerFunctions.http(commentServiceUri))
                .filter(this::authentication)
                .build();
    }

    private ServerResponse authentication(ServerRequest request, HandlerFunction<ServerResponse> next) throws Exception {
        if (authMissing(request)) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
        }

        final String token = request.headers().header(AUTHORIZATION).getFirst();

        if (jwtUtils.isExpired(token)) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
        }
        return next.handle(request);
    }

    private boolean authMissing(ServerRequest request) {
        return request.headers().firstHeader(AUTHORIZATION) == null;
    }
}
