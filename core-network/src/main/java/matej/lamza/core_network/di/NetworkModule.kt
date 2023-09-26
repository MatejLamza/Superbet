package matej.lamza.core_network.di

import com.skydoves.sandwich.adapters.ApiResponseCallAdapterFactory
import matej.lamza.core_network.service.BetshopService
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val networkModule = module {
    single {
        // TODO move out to config
        Retrofit.Builder()
            .baseUrl("https://interview.superology.dev")
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
            .build()
    }

    single { get<Retrofit>().create(BetshopService::class.java) }
}
