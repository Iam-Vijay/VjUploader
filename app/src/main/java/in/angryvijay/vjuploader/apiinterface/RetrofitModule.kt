package `in`.angryvijay.vjuploader.apiinterface

import `in`.angryvijay.tubetamil.apiinterface.APIInterface
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by Vijay on 12/15/2021.
 */
@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    @Provides
    @Singleton
    fun provideOkhttpClient():OkHttpClient{
        return OkHttpClient
            .Builder()
            .readTimeout(60,TimeUnit.SECONDS)
            .connectTimeout(60,TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("interceptor")
    fun provideInterceptor(httpLoggingInterceptor: HttpLoggingInterceptor):OkHttpClient{
        return OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build()
    }

    @Provides
    @Singleton
    fun provideGsonconvertorFactory():GsonConverterFactory{
        val gson =  GsonBuilder()
            .setLenient()
            .create()
        return GsonConverterFactory.create(gson)
    }

    @Provides
    @Singleton
    fun providelogginginterceptor():HttpLoggingInterceptor{
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }

    @Provides
    @Singleton
    fun getRetrofitProvider(gsonConverterFactory: GsonConverterFactory,okHttpClient: OkHttpClient):Retrofit{


        return Retrofit.Builder().baseUrl("https://tubetamil.online/Mobile/").addConverterFactory(gsonConverterFactory)
            .addCallAdapterFactory(CoroutineCallAdapterFactory()).client(okHttpClient).build()
    }
    @Provides
    @Singleton
    fun provideApiinterface(retrofit: Retrofit):APIInterface{
        return retrofit.create(APIInterface::class.java)
    }
}