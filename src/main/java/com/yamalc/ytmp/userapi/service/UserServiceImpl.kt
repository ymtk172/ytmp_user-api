package com.yamalc.ytmp.userapi.service

import com.yamalc.ytmp.grpc.user.*
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
        var displayName = ""
        val resultType = try {
            val result = usersMapper.select(request.id)
            if (result == null ) {
                ResultType.NOT_EXISTS
            } else {
                displayName = result.display_name
                ResultType.SUCCESS
            }
        } catch (e: IOException) {
            println("DB access error occurred")
            throw e
        }
        val userInfoResponse = UserInfoResponse
                .newBuilder()
                .setResult(resultType)
                .setId(request.id)
                .setDisplayName(displayName)
                .build()
        responseObserver.onNext(userInfoResponse)
        responseObserver.onCompleted()
    }

    override fun registerUserInfo(request: UserInfoRequest,
                                  responseObserver: StreamObserver<RegisterUserInfoResponse>) {
        logger.info(String.format("request: id = %s", request.id))
        val resultType = try {
            val result: Int = usersMapper.insert(request.id, request.displayName)
            println(result)
            if (result != 0) {
                ResultType.SUCCESS
            } else {
                ResultType.ALREADY_EXISTS
            }
        } catch (e: IOException) {
            ResultType.FAILURE
        }
        val userInfoResponse = RegisterUserInfoResponse
                .newBuilder()
                .setResult(resultType)
                .build()
        responseObserver.onNext(userInfoResponse)
        responseObserver.onCompleted()
    }
}
