package com.github.hideto0710

import java.util.logging.Logger

import akka.Done
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import com.github.hideto0710.interceptors.{ErrorHandler, Logging}
import io.grpc._

import scala.concurrent.{ExecutionContext, Future}
import com.github.hideto0710.protos.hello.{GreeterGrpc, HelloReply, HelloRequest}
import io.grpc.stub.StreamObserver
import io.grpc.util.TransmitStatusRuntimeExceptionInterceptor

import scala.util.{Failure, Success}


object HelloWorldServer {
  private val logger = Logger.getLogger(classOf[HelloWorldServer].getName)

  def main(args: Array[String]): Unit = {
    val server = new HelloWorldServer(ExecutionContext.global)
    server.start()
    server.blockUntilShutdown()
  }

  private val port = 50051
}

class HelloWorldServer(executionContext: ExecutionContext) { self =>
  private[this] var server: Server = _
  implicit val system: ActorSystem = ActorSystem("HelloWorldServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  private def start(): Unit = {
    server = ServerBuilder
      .forPort(HelloWorldServer.port)
      .addService(
        ServerInterceptors.intercept(
          GreeterGrpc.bindService(new GreeterImpl, executionContext),
          new Logging,
          new ErrorHandler,
          TransmitStatusRuntimeExceptionInterceptor.instance()
        )
      )
      .build
      .start
    HelloWorldServer.logger.info("Server started, listening on " + HelloWorldServer.port)
    sys.addShutdownHook {
      System.err.println("*** shutting down gRPC server since JVM is shutting down")
      self.stop()
      System.err.println("*** server shut down")
    }
  }

  private def stop(): Unit = {
    if (server != null) {
      server.shutdown()
    }
  }

  private def blockUntilShutdown(): Unit = {
    if (server != null) {
      server.awaitTermination()
    }
  }

  private class GreeterImpl extends GreeterGrpc.Greeter {
    override def sayHello(req: HelloRequest): Future[HelloReply] = {
      val reply = HelloReply(message = "Hello " + req.name)
      Future.successful(reply)
    }
    override def sayError(req: HelloRequest): Future[HelloReply] = {
      throw new Exception(req.name)
    }
    override def sayHelloAll(req: HelloRequest, responseObserver: StreamObserver[HelloReply]): Unit = {
      implicit val ec: ExecutionContext = system.dispatcher
      val done: Future[Done] = Source(1 to 100)
        .map(_ / 0)
        .runForeach(n => responseObserver.onNext(HelloReply(message = s"Hello ${req.name}[$n]")))
      done.onComplete {
        case Success(_) => responseObserver.onCompleted()
        case Failure(t) =>
          HelloWorldServer.logger.severe(t.toString)
          responseObserver.onError(t)
      }
    }
  }
}
