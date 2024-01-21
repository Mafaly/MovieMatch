package com.mafaly.moviematch.di.modules

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale
import java.util.concurrent.TimeUnit

internal val remoteModule = module {
    single(
        named(TMDBApiRetrofitClient)
    ) {
        createRetrofit(get(), get<TMDBJsonConf>().baseUrl)
    }

    single {
        createOkHttpClient(
            bearerToken = get<TMDBJsonConf>().apiToken,
            locale = androidContext().resources.configuration.locales.get(0)
        )
    }
}

private fun createOkHttpClient(bearerToken: String = "", locale: Locale): OkHttpClient {
    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.BODY

    return OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .addInterceptor(interceptor)
        .addInterceptor { chain ->
            val newRequest: Request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $bearerToken")
                .build()
            chain.proceed(newRequest)
        }
        .addInterceptor { chain ->
            val original = chain.request()
            val originalHttpUrl = original.url
            val url = originalHttpUrl.newBuilder()
                .addQueryParameter("language", locale.toLanguageTag())
                .addQueryParameter("region", locale.country)
                .build()
            val request = original.newBuilder()
                .url(url)
                .build()
            chain.proceed(request)
        }
        .build()
}


fun createRetrofit(
    okHttpClient: OkHttpClient,
    baseUrl: String,
): Retrofit {
    val gsonConverter =
        GsonConverterFactory.create(
            GsonBuilder().create()
        )

    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(gsonConverter)
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .client(okHttpClient)
        .build()
}

inline fun <reified T> createWebService(retrofit: Retrofit): T {
    return retrofit.create(T::class.java)
}

const val TMDBApiRetrofitClient: String = "tmdbApiRetrofitClient"