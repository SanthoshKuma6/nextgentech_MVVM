package com.nextgenapp.nextgentech.data.api

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nextgenapp.nextgentech.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitApi {
    private var retrofit: Retrofit? = null

    /**
     * OkHttpClient used to specify the timeout for the api request and to add the
     * interceptor.
     */
    private fun okHttpClientSever(context: Context): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.MINUTES)
            .readTimeout(3, TimeUnit.MINUTES)
            .writeTimeout(3, TimeUnit.MINUTES)
            .build()

    /**
     * retrofitInstance builds the networks call by adding the baseurl and preferred clients,
     * gson conversion factory and call adapter
     */

    var gson: Gson? = GsonBuilder()
        .setLenient()
        .create()

    fun retrofitInstance(context: Context): Retrofit =
        retrofit
            ?: Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson!!))
                .baseUrl(BuildConfig.API_URL)
                .client(okHttpClientSever(context = context))
                .build()
}