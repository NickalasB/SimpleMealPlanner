package com.zonkey.simplemealplanner.di

import com.zonkey.simplemealplanner.activity.CreateRecipeActivity
import com.zonkey.simplemealplanner.activity.MainActivity
import com.zonkey.simplemealplanner.activity.RecipeDetailActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(subcomponents = [])
abstract class ActivityBindingModule {
  @ContributesAndroidInjector
  internal abstract fun contributeMainActivity(): MainActivity

  @ContributesAndroidInjector
  internal abstract fun contributeRecipeDetailActivity(): RecipeDetailActivity

  @ContributesAndroidInjector
  internal abstract fun contributeCreateRecipeActivity(): CreateRecipeActivity
}