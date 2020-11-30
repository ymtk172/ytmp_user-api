package com.yamalc.ytmp.userapi.service

import com.yamalc.ytmp.grpc.user.*
import com.yamalc.ytmp.userapi.mapper.UsersMapper
import io.grpc.Status
import io.grpc.protobuf.ProtoUtils
import io.grpc.stub.StreamObserver
import org.lognet.springboot.grpc.GRpcService
import org.springframework.beans.factory.annotation.Value
import java.io.IOException
import java.util.logging.Logger
import javax.validation.ConstraintViolation
import javax.validation.Validator

@GRpcService
class UserServiceImpl(private val usersMapper: UsersMapper, val validator: Validator) : UserGrpc.UserImplBase() {

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

    @Value("#{\${grpcprops.UserInfo}}")
    val userInfoRequestKeyMap: Map<String, Int>? = null

    override fun registerUserInfo(request: UserInfoRequest,
                                  responseObserver: StreamObserver<RegisterUserInfoResponse>) {
        request.userInfoList.forEachIndexed { index, userInfo ->
            val constraintViolations: Set<ConstraintViolation<UserInfo>> = validator.validate<UserInfo>(userInfo)
            if(constraintViolations.isNotEmpty()) {
                val errorDetailBuilder = ErrorDetail.newBuilder()
                        .setCode(ErrorCode.VALIDATION_ERROR)
                        .setMessage("Validation failed.")
                constraintViolations.forEach {
                    val errorInfo = ErrorInfo.newBuilder()
                            .setBusinessErrorCode("VALIDATION_FAILED")
                            .setErrorLineNumber(index.toString())
                            .setErrorField(userInfoRequestKeyMap!!.getOrElse(it.propertyPath.toString()) { 0 }) //定義誤りで見つからなければ0を入れる
                            .setErrorDescription(it.message)
                            .build()
                    errorDetailBuilder.addErrorInfo(errorInfo)
                }
                val metadata = io.grpc.Metadata()
                val errorDetail = errorDetailBuilder.build()
                val key: io.grpc.Metadata.Key<ErrorDetail> = ProtoUtils.keyForProto(errorDetail)
                metadata.put(key, errorDetail)
                val e = Status.INTERNAL
                        .withDescription("Validation failed occurred.")
                        .asRuntimeException(metadata)
                responseObserver.onError(e)
                return
            }
            logger.info(String.format("... Inserting request: id = %s", userInfo.id))
            try {
                usersMapper.insert(userInfo.id, userInfo.displayName)
            } catch (e: IOException) {
                ResultType.FAILURE
                val e2 = Status.UNAVAILABLE
                        .withDescription("IOException occurred. DB might not be available.")
                        .asRuntimeException()
                responseObserver.onError(e2)
                return
            }
        }
        val userInfoResponse = RegisterUserInfoResponse
                .newBuilder()
                .setResult(ResultType.SUCCESS)
                .build()
        responseObserver.onNext(userInfoResponse)
        responseObserver.onCompleted()
    }
}
