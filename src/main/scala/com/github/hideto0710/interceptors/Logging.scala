package com.github.hideto0710.interceptors

import java.util.logging.Logger

import io.grpc._

class Logging extends ServerInterceptor {
  private val logger = Logger.getLogger(classOf[Logging].getName)

  override def interceptCall[ReqT, RespT](
                                           serverCall: ServerCall[ReqT, RespT],
                                           headers: Metadata,
                                           next: ServerCallHandler[ReqT, RespT]
                                         ): ServerCall.Listener[ReqT] = {

    val wrapperCall: ServerCall[ReqT, RespT] = new ForwardingServerCall.SimpleForwardingServerCall[ReqT, RespT](serverCall) {
      override def request(numMessages: Int): Unit = {
        logger.info(
          s"Request: ${headers.toString}, ${serverCall.getAttributes.toString}, ${serverCall.getMethodDescriptor.getFullMethodName}"
        )
        super.request(numMessages)
      }

      override def sendMessage(message: RespT): Unit = {
        logger.info(s"Response: ${message.toString}")
        super.sendMessage(message)
      }

      override def close(status: Status, trailers: Metadata): Unit = {
        logger.info(s"Response: ${status.toString}")
        super.close(status, trailers)
      }
    }
    next.startCall(wrapperCall, headers)
  }
}
