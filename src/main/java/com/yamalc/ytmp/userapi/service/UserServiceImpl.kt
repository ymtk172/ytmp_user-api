package com.yamalc.ytmp.userapi.service

import com.yamalc.ytmp.grpc.user.*
import com.yamalc.ytmp.userapi.domain.UserProperties
import com.yamalc.ytmp.userapi.mapper.UsersMapper
import io.grpc.stub.StreamObserver
import org.lognet.springboot.grpc.GRpcService
import java.io.IOException
import java.util.logging.Logger

@GRpcService
class UserServiceImpl(private val usersMapper: UsersMapper) : UserGrpc.UserImplBase() {
    private var logger: Logger = Logger.getLogger(javaClass.name)
    override fun getUserInfo(request: UserIdRequest,
                             responseObserver: StreamObserver<UserInfoResponse>) {
        logger.info(String.format("request: id = %s", request.id))
        val result: UserProperties = try {
            usersMapper.select(request.id)
        } catch (e: IOException) {
            println("DB access error occurred")
            throw e
        }
        val userInfoResponse = UserInfoResponse
                .newBuilder()
                .setId(result.user_id)
                .setDisplayName(result.display_name)
                .build()
        responseObserver.onNext(userInfoResponse)
        responseObserver.onCompleted()
    }

    override fun registerUserInfo(request: UserInfoRequest,
                                  responseObserver: StreamObserver<RegisterUserInfoResponse>) {
        logger.info(String.format("request: id = %s", request.id))
        val result: String = try {
            val result: Int = usersMapper.insert(request.id, request.displayName)
            println(request)
            "success"
        } catch (e: IOException) {
            "DB access error occurred"
        }
        val userInfoResponse = RegisterUserInfoResponse
                .newBuilder()
                .setResultCode(result)
                .build()
        responseObserver.onNext(userInfoResponse)
        responseObserver.onCompleted()
    }
}
