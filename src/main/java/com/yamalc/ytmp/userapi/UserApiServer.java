package com.yamalc.ytmp.userapi;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

import com.yamalc.ytmp.grpc.user.AuthenticateRequest;
import com.yamalc.ytmp.grpc.user.AuthenticateResponse;
import com.yamalc.ytmp.grpc.user.UserGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class UserApiServer {
    Logger logger = Logger.getLogger(getClass().getName());

    Server server;

    public static void main(String... args) throws IOException, InterruptedException {
        UserApiServer userApiServer = new UserApiServer();
        userApiServer.start();

        //simpleServer.blockUntilShutdown();

        System.console().readLine("> Enter stop.");

        userApiServer.stop();
    }

    public void start() throws IOException {
        server =
                ServerBuilder
                        .forPort(8081)
                        .addService(new AuthenticateServiceImpl())
                        .build()
                        .start();

        logger.info("start gRPC server.");
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
            logger.info("shutdown gRPC server.");
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static class AuthenticateServiceImpl extends UserGrpc.UserImplBase {
        Logger logger = Logger.getLogger(getClass().getName());

        @Override
        public void authenticate(AuthenticateRequest request,
                         StreamObserver<AuthenticateResponse> responseObserver) {
            logger.info(String.format("request: id = %s", request.getId()));

            boolean result = dbAuthenticate(request);
            logger.info(String.format("result: %b", result));

            AuthenticateResponse authenticateResponse =
                    AuthenticateResponse
                            .newBuilder()
                            .setAuthenticateResult(result)
                            .build();

            responseObserver.onNext(authenticateResponse);
            responseObserver.onCompleted();
        }
        private boolean dbAuthenticate(AuthenticateRequest request) {
            boolean result = Objects.equals(request.getId(), "user2");
            return result;
        }
    }
}
