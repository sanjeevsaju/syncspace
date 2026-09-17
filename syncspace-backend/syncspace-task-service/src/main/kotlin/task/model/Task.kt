package task.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

// 1. Stored in MongoDB
data class TaskDocument(
    @BsonId val id: String = ObjectId().toHexString(),
    val title: String,
    val description: String,
    val status: String = "TODO",
    val assigneeIds: List<String> = emptyList(),
)

// 2. Returned via GraphQL (includes nested profile objects resolved via gRPC)
data class UserProfileDTO(val userId: String, val username: String, val displayName: String, val avatarUrl: String)

data class TaskResponse(
    val id: String,
    val title: String,
    val description: String,
    val status: String,
    val assignees: List<UserProfileDTO>,
)
