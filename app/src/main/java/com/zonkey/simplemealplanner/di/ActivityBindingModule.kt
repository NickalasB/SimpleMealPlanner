package com.zonkey.simplemealplanner.di

import com.zonkey.simplemealplanner.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(subcomponents = [])
abstract class ActivityBindingModule {
    @ContributesAndroidInjector
    internal abstract fun contributeMainActivity(): MainActivity
}