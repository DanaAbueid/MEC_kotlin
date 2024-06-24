package com.mec.mec.ui.API
import com.mec.mec.ui.model.Customer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("customersManagement/customers")
    suspend fun getCustomers(): List<Customer>
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
