package profile.service

import com.syncspace.profile.grpc.GetProfileRequest
import com.syncspace.profile.grpc.GetProfilesBatchRequest
import com.syncspace.profile.grpc.GetProfilesBatchResponse
import com.syncspace.profile.grpc.UpsertProfileRequest
import com.syncspace.profile.grpc.UserProfileResponse
import com.syncspace.profile.grpc.UserProfileServiceGrpcKt
import io.grpc.Status
import io.grpc.StatusException
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import profile.db.DatabaseFactory.dbQuery
import profile.db.ProfilesTable

// gRPC Coroutine Implementation
class ProfileServiceImpl : UserProfileServiceGrpcKt.UserProfileServiceCoroutineImplBase() {
    override suspend fun getProfile(request: GetProfileRequest): UserProfileResponse {
        val row =
            dbQuery {
                ProfilesTable
                    .selectAll()
                    .where { ProfilesTable.userId eq request.userId }
                    .singleOrNull()
            } ?: throw StatusException(Status.NOT_FOUND.withDescription("Profile not found"))

        return row.toProtoResponse()
    }

    suspend fun getProfilesBatch(request: GetProfilesBatchRequest): GetProfilesBatchResponse {
        val rows =
            dbQuery {
                ProfilesTable
                    .selectAll()
                    .where { ProfilesTable.userId inList request.userIdsList }
                    .toList()
            }

        return GetProfilesBatchResponse
            .newBuilder()
            .addAllProfiles(rows.map { it.toProtoResponse() })
            .build()
    }

    override suspend fun upsertProfile(request: UpsertProfileRequest): UserProfileResponse {
        dbQuery {
            val exists =
                ProfilesTable
                    .selectAll()
                    .where { ProfilesTable.userId eq request.userId }
                    .count() > 0
            if (exists) {
                ProfilesTable.update({ ProfilesTable.userId eq request.userId }) {
                    it[username] = request.username
                    it[displayName] = request.displayName
                    it[avatarUrl] = request.avatarUrl
                    it[bio] = request.bio
                }
            } else {
                ProfilesTable.insert {
                    it[userId] = request.userId
                    it[username] = request.username
                    it[displayName] = request.displayName
                    it[avatarUrl] = request.avatarUrl
                    it[bio] = request.bio
                }
            }
        }
        return getProfile(GetProfileRequest.newBuilder().setUserId(request.userId).build())
    }

    private fun ResultRow.toProtoResponse(): UserProfileResponse =
        UserProfileResponse
            .newBuilder()
            .setUserId(this[ProfilesTable.userId])
            .setUsername(this[ProfilesTable.username])
            .setDisplayName(this[ProfilesTable.displayName])
            .setAvatarUrl(this[ProfilesTable.avatarUrl])
            .setBio(this[ProfilesTable.bio])
            .build()
}
