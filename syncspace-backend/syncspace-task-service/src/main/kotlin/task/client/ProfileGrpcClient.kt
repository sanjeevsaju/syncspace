package task.client

import com.syncspace.profile.grpc.GetProfilesBatchRequest
import com.syncspace.profile.grpc.UserProfileServiceGrpcKt
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import task.model.UserProfileDTO

object ProfileGrpcClient {
    private val host = System.getenv("PROFILE_SERVICE_HOST") ?: "profile"
    private val port = (System.getenv("PROFILE_SERVICE_PORT") ?: "50051").toInt()

    private val channel by lazy {
        ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext() // Internal Docker doesn't require SSL certificates
            .build()
    }

    private val stub by lazy { UserProfileServiceGrpcKt.UserProfileServiceCoroutineStub(channel) }

    suspend fun fetchProfiles(userIds: List<String>): List<UserProfileDTO> = withContext(Dispatchers.IO) {
        if(userIds.isEmpty()) return@withContext emptyList()

        try {
            val request = GetProfilesBatchRequest.newBuilder()
                .addAllUserIds(userIds)
                .build()

            val response = stub.getProfileBatch(request)

            response.profilesList.map { proto ->
                UserProfileDTO(
                    userId = proto.userId,
                    username = proto.username,
                    displayName = proto.displayName,
                    avatarUrl = proto.avatarUrl
                )
            }
        } catch (e: Exception) {
             emptyList()
        }
    }
}