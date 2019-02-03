package com.zonkey.simplemealplanner.di

import com.zonkey.simplemealplanner.RecipeApp
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
      AppDaggerModule::class,
      ActivityBindingModule::class,
      NetworkBindingDaggerModule::class,
      RecipeDaggerModule::class,
      FirebaseBindingDaggerModule::class
    ]
)

interface AppComponent {
  fun inject(recipeApp: RecipeApp)

}