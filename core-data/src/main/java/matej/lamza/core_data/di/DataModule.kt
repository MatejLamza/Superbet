package matej.lamza.core_data.di

import matej.lamza.core_data.repository.BetshopRepository
import matej.lamza.core_data.repository.BetshopRepositoryImpl
import org.koin.dsl.module

val dataModule = module {
    single<BetshopRepository> { BetshopRepositoryImpl(betshopService = get()) }
}
