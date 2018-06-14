name := "scala-grpc"

version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
  "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
  "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
  "io.grpc" % "grpc-all" % scalapb.compiler.Version.grpcJavaVersion
)

PB.targets in Compile := Seq(scalapb.gen() -> (sourceManaged in Compile).value)
PB.protoSources in Compile += (baseDirectory in LocalRootProject).value / "protobuf"
