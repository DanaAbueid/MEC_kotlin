package com.mec.mec.API
import com.mec.mec.request.LoginRequest
import com.mec.mec.request.SignUpRequest
import com.mec.mec.model.AuthResponse
import com.mec.mec.model.Customer
import com.mec.mec.model.Employee
import com.mec.mec.model.Task
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {

    @GET("customersManagement/customers")
    suspend fun getCustomers(): List<Customer>

    @GET("maintenance/task/date")
    suspend fun getTasksByDate(@Query("date") date: String): List<Task>

    @GET
    suspend fun getTasks(@Url url: String): List<Task>

    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): AuthResponse

    @POST("auth/signup")
    suspend fun signUp(@Body signUpRequest: SignUpRequest): AuthResponse

    @GET("employee/basicInfo")
    suspend fun getUserBasicInfo(): List<Employee>

    @GET("employeesManagement/task/employee/{userID}")
    suspend fun getAllTasksForEmployee(@Path("userID") userId: Long): List<Task>

    @GET("employee/allTasksDependsOfEmployeeDone/{userID}/{done}")
    suspend fun getAllTasksDependsOfEmployeeDone(
        @Path("userID") userId: Long,
        @Path("done") done: Boolean
    ): List<Task>

    @GET("employeesManagement/task/approval/{approval}/{userID}")
    suspend fun getAllTasksByApproval(
        @Path("approval") approval: Boolean,
        @Path("userID") userId: Long
    ): List<Task>

    @GET("employee/allTodayTasks/{userID}")
    suspend fun getAllTodayTasks(@Path("userID") userId: Long): List<Task>

    @GET("employee/allTasksDependsOfEmployeeDoneAndDate/{userID}/{done}/{date}")
    suspend fun getAllTasksDependsOfEmployeeDoneAndDate(
        @Path("userID") userId: Long,
        @Path("done") done: Boolean,
        @Path("date") date: String
    ): List<Task>

    @GET("employee/allDoneAndApprovedTasks/{userID}/{approval}/{done}")
    suspend fun getAllDoneAndApprovedTasks(
        @Path("userID") userId: Long,
        @Path("approval") approval: Boolean,
        @Path("done") done: Boolean
    ): List<Task>
}


object RetrofitInstance {
    private const val BASE_URL = "http://34.234.65.167:8080/api/v1/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
