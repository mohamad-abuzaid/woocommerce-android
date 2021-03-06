package com.woocommerce.android.ui.imageviewer

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.woocommerce.android.di.ActivityScope
import com.woocommerce.android.di.ViewModelAssistedFactory
import com.woocommerce.android.ui.base.UIMessageResolver
import com.woocommerce.android.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
internal abstract class ImageViewerModule {
    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideDefaultArgs(): Bundle? {
            return null
        }
    }

    @ActivityScope
    @Binds
    abstract fun provideUiMessageResolver(uiIMessageResolver: ImageViewerUIMessageResolver): UIMessageResolver

    @Binds
    @IntoMap
    @ViewModelKey(ImageViewerViewModel::class)
    abstract fun bindFactory(factory: ImageViewerViewModel.Factory): ViewModelAssistedFactory<out ViewModel>

    @Binds
    abstract fun bindSavedStateRegistryOwner(fragment: ImageViewerActivity): SavedStateRegistryOwner
}
