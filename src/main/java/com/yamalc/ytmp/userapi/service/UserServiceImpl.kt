package com.yamalc.ytmp.userapi.service

import com.yamalc.ytmp.grpc.user.AuthenticateRequest
import com.yamalc.ytmp.grpc.user.AuthenticateResponse
import com.yamalc.ytmp.grpc.user.AuthenticateResponseType
import com.yamalc.ytmp.grpc.user.UserGrpc
import com.yamalc.ytmp.userapi.domain.Users
import com.yamalc.ytmp.userapi.mapper.UsersMapper
import io.grpc.stub.StreamObserver
import org.lognet.springboot.grpc.GRpcService
import java.util.logging.Logger

@GRpcService
class UserService(val usersMapper: UsersMapper) : UserGrpc.UserImplBase() {
    private var logger: Logger = Logger.getLogger(javaClass.name)
    override fun authenticate(request: AuthenticateRequest,
                              responseObserver: StreamObserver<AuthenticateResponse>) {
        logger.info(String.format("request: id = %s", request.id))
        val result = dbAuthenticate(request)
        logger.info(String.format("result: %b", result))
        val authenticateResponse = AuthenticateResponse
                .newBuilder()
                .setAuthenticateResult(result)
                .build()
        responseObserver.onNext(authenticateResponse)
        responseObserver.onCompleted()
    }

    private fun dbAuthenticate(request: AuthenticateRequest): AuthenticateResponseType {
        val user: Users = usersMapper.select(request.id)
        if (user == null) {
            logger.info("存在しないユーザIDです")
            return AuthenticateResponseType.NOT_EXISTS
        }
        if (request.password != user.password) {
            logger.info("パスワードが間違っています。")
            return AuthenticateResponseType.WRONG_PASSWORD
        }
        return AuthenticateResponseType.OK
    }
}