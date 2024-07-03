package com.mec.mec.api
import com.mec.mec.request.LoginRequest
import com.mec.mec.request.SignUpRequest
import com.mec.mec.model.AuthResponse
import com.mec.mec.model.Customer
import com.mec.mec.model.Employee
import com.mec.mec.model.NewTaskRequest
import com.mec.mec.model.Task
import com.mec.mec.model.UpdateApproval
import com.mec.mec.model.UpdateNotes
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {

    @GET("customersManagement/customers")
    suspend fun getCustomers(): List<Customer>

    @GET("maintenance/task/date")
    suspend fun getTasksByDate(@Query("date") date: String): List<Task>

   // @GET
 //   suspend fun getTasks(@Url url: String): List<Task>
    @GET
    suspend fun getTasks(@Url url: String): Response<List<Task>>

    @POST("auth/authenticate")
    suspend fun login(@Body loginRequest: LoginRequest): AuthResponse

    @POST("auth/register")
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


    @PUT("customersManagement/editCustomer/{name}/{location}/{elevatorType}/{panel}")
    suspend fun editCustomer(
        @Path("name") name: String,
        @Path("location") location: String,
        @Path("elevatorType") elevatorType: String,
        @Path("panel") panel: String
    ): Response<Void>

    @PUT("employee/editEmployeeNotes/{taskID}/{note}")
    suspend fun editMangerToEmployeeNote(
        @Path("taskID") taskID: Long,
        @Path("note") note: String,
        ): Response<Void>

    @PUT("employee/editTaskDone/{taskID}/true")
    suspend fun editDone(
        @Path("taskID") taskID: Long,
    ): Response<Void>

    @PUT("maintenance/managerNotes")
    suspend fun editManagerNote(
        @Body updateNotes: UpdateNotes
    ): Response<Void>

    @PUT("maintenance/employeeNotes")
    suspend fun editEmployeeNote(
        @Body updateNotes: UpdateNotes
    ): Response<Void>

    @PUT("employee/editEmpolyeeNotes/{note}")
    suspend fun editEmployeeNotePath(
        @Path("note") note: String,
    ): Response<Void>


    @PUT("maintenance/editApproval")
    suspend fun editTaskApproval(
        @Body updateApproval: UpdateApproval
    ): Response<Void>
    @DELETE("customersManagement/delete/customer")
    suspend fun deleteCustomer(@Body id: Long): Response<Void>
    @POST("maintenance/newTask")
    suspend fun addNewTask(@Body newTask: NewTaskRequest): Response<Void>
    @POST("customersManagement/addNewCus")
    suspend fun addCustomer(@Body customer: Customer): Response<ResponseBody>

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
