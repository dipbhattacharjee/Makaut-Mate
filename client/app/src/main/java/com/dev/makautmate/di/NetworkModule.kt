package com.dev.makautmate.di

import com.dev.makautmate.data.remote.BookApiService
import com.dev.makautmate.data.remote.OpenLibraryApiService
import com.dev.makautmate.data.remote.PortalApiService
import com.dev.makautmate.data.remote.YoutubeApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://your-python-api.com/") // Replace with your actual API URL
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providePortalApiService(retrofit: Retrofit): PortalApiService {
        return retrofit.create(PortalApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideBookApiService(okHttpClient: OkHttpClient): BookApiService {
        return Retrofit.Builder()
            .baseUrl(BookApiService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BookApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideOpenLibraryApiService(okHttpClient: OkHttpClient): OpenLibraryApiService {
        return Retrofit.Builder()
            .baseUrl(OpenLibraryApiService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenLibraryApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideYoutubeApiService(okHttpClient: OkHttpClient): YoutubeApiService {
        return Retrofit.Builder()
            .baseUrl(YoutubeApiService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(YoutubeApiService::class.java)
    }
}
