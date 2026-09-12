package com.example.syncspace.data.remote

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.example.syncspace.CreateTaskMutation
import com.example.syncspace.DeleteTaskMutation
import com.example.syncspace.GetTasksQuery
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRemoteDataSource @Inject constructor(
    private val apolloClient: ApolloClient
) {
    fun getTasks(): ApolloCall<GetTasksQuery.Data> =
        apolloClient.query(GetTasksQuery())

    fun createTask(
        title: String,
        description: String,
        assigneeIds: List<String>
    ): ApolloCall<CreateTaskMutation.Data> =
        apolloClient.mutation(
            CreateTaskMutation(
                title = title,
                description = description,
                assigneeIds = assigneeIds
            )
        )

    fun deleteTask(id: String): ApolloCall<DeleteTaskMutation.Data> =
        apolloClient.mutation(
            DeleteTaskMutation(id = id)
        )
}