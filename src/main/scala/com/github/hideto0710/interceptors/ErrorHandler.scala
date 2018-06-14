package com.github.hideto0710.interceptors

import java.util.logging.Logger

import io.grpc._

class ErrorHandler extends ServerInterceptor {
  private val logger = Logger.getLogger(classOf[Logging].getName)

  override def interceptCall[ReqT, RespT](
                                           serverCall: ServerCall[ReqT, RespT],
                                           headers: Metadata,
                                           next: ServerCallHandler[ReqT, RespT]
                                         ): ServerCall.Listener[ReqT] = {
    val listener = next.startCall(serverCall, headers)
    new ForwardingServerCallListener.SimpleForwardingServerCallListener[ReqT](listener) {
      override def onMessage(message: ReqT): Unit = {
        try {
          super.onMessage(message)
        } catch {
          case e: Exception =>
            closeWithException(e, headers)
        }
      }

      override def onHalfClose(): Unit = {
        try {
          super.onHalfClose()
        } catch {
          case e: Exception =>
            closeWithException(e, headers)
        }
      }

      override def onCancel(): Unit = {
        try {
          super.onCancel()
        } catch {
          case e: Exception =>
            closeWithException(e, headers)
        }
      }

      override def onComplete(): Unit = {
        try {
          super.onComplete()
        } catch {
          case e: Exception =>
            closeWithException(e, headers)
        }
      }

      override def onReady(): Unit = {
        try {
          super.onReady()
        } catch {
          case e: Exception =>
            closeWithException(e, headers)
        }
      }

      private def closeWithException(t: Exception, requestHeader: Metadata) {
        logger.severe(t.getMessage)
      }
    }
  }
}
