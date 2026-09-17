package profile

import io.grpc.ServerBuilder
import profile.db.DatabaseFactory
import profile.service.ProfileServiceImpl

fun main() {
    // Initialize PostgreSQL profile_db connection
    DatabaseFactory.init()

    val port = 50051
    val server =
        ServerBuilder
            .forPort(port)
            .addService(ProfileServiceImpl())
            .build()

    println("gRPC Profile Service started, listening on $port")
    server.start()
    server.awaitTermination()
}
