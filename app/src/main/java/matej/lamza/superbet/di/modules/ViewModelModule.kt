package matej.lamza.superbet.di.modules

import matej.lamza.superbet.ui.MapViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MapViewModel(get()) }
}
