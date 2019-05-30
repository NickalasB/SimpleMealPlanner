package com.zonkey.simplemealplanner.activity

import android.Manifest
import android.Manifest.permission
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Email
import android.transition.Transition
import android.transition.Transition.TransitionListener
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.zonkey.simplemealplanner.R
import com.zonkey.simplemealplanner.adapter.FROM_FAVORITE
import com.zonkey.simplemealplanner.adapter.FULL_RECIPE
import com.zonkey.simplemealplanner.firebase.DefaultFirebaseAuthRepository
import com.zonkey.simplemealplanner.firebase.FirebaseRecipeRepository
import com.zonkey.simplemealplanner.firebase.NOTIFICATION_FULL_RECIPE
import com.zonkey.simplemealplanner.model.DayOfWeek
import com.zonkey.simplemealplanner.model.Recipe
import com.zonkey.simplemealplanner.model.User
import com.zonkey.simplemealplanner.utils.UiUtils
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_recipe_detail.detail_collapsing_toolbar
import kotlinx.android.synthetic.main.activity_recipe_detail.detail_favorite_button
import kotlinx.android.synthetic.main.activity_recipe_detail.detail_recipe_image
import kotlinx.android.synthetic.main.activity_recipe_detail.detail_recipe_parent_layout
import kotlinx.android.synthetic.main.activity_recipe_detail.detail_save_to_meal_plan_button
import kotlinx.android.synthetic.main.activity_recipe_detail.detail_share_button
import kotlinx.android.synthetic.main.activity_recipe_detail.detailed_recipe_card_view
import timber.log.Timber
import javax.inject.Inject


private const val RC_SIGN_IN_DETAIL = 200
private const val RC_CONTACT_PICKER = 300
private const val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1000
internal const val PREFS_FIRST_TIME_KEY = "firstTime"
internal const val PREFS_SEEN_SHARE_TUTORIAL_KEY = "hasSeenShareTutorial"

class RecipeDetailActivity : AppCompatActivity(), RecipeDetailView {

  @Inject
  lateinit var firebaseRepo: FirebaseRecipeRepository

  @Inject
  lateinit var firebaseAuthRepository: DefaultFirebaseAuthRepository

  @Inject
  lateinit var uiUtils: UiUtils

  private lateinit var presenter: RecipeDetailActivityPresenter
  private lateinit var sharedPreferences: SharedPreferences
  private lateinit var recipe: Recipe
  override var isSavedRecipe = false
  override var addedToMealPlan = false
  private var selectedDay: String = ""
  private var fromFavoriteClick = false
  private var fromMealPlanClick = false
  private var destinationUserEmail = ""
  private var destinationUserDisplayName = ""
  private var firstTimeInActivity = true
  private var hasSeenShareButtonTutorial = false
  override var contactPermissionGranted = false

  companion object {
    fun buildIntent(context: Context): Intent = Intent(context, RecipeDetailActivity::class.java)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    AndroidInjection.inject(this)
    setContentView(R.layout.activity_recipe_detail)

    sharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
    firstTimeInActivity = sharedPreferences.getBoolean(PREFS_FIRST_TIME_KEY, true)
    hasSeenShareButtonTutorial = sharedPreferences.getBoolean(PREFS_SEEN_SHARE_TUTORIAL_KEY, false)

    presenter = RecipeDetailActivityPresenter(this, firebaseRepo, firebaseAuthRepository)

    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      postponeEnterTransition()
    }

    val recipeFromMainActivity = Gson().fromJson(intent.getStringExtra(FULL_RECIPE), Recipe::class.java)

    if (recipeFromMainActivity != null) {
      recipe = recipeFromMainActivity
    } else {
      recipe = Gson().fromJson(intent.getStringExtra(NOTIFICATION_FULL_RECIPE), Recipe::class.java)
    }

    loadRecipeImage(recipe)

    detail_collapsing_toolbar.title = recipe.label

    detail_collapsing_toolbar.setCollapsedTitleTextColor(
        ContextCompat.getColor(this, R.color.lightBackground))

    detailed_recipe_card_view.setRecipeDetailCardItems(recipe)

    setupMealPlanDialog(recipe)

    presenter.setupMealPlanButtonText(recipe)

    presenter.setShareButtonBackground(recipe.mealPlan)

    contactPermissionGranted = ContextCompat.checkSelfPermission(this,
        Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED

  }

  override fun onResume() {
    super.onResume()
    setUpTutorialsAndButtonsAfterTransitionAnimation()
  }

  override fun setShareButtonBackground(@DrawableRes shareButtonResId: Int) {
    detail_share_button.background = ContextCompat.getDrawable(this, shareButtonResId)
  }

  private fun loadRecipeImage(recipe: Recipe) {
    Glide.with(this)
        .load(recipe.image)
        .listener(object : RequestListener<Drawable> {
          override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?,
              isFirstResource: Boolean): Boolean {
            return false
          }

          override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?,
              dataSource: DataSource?, isFirstResource: Boolean): Boolean {
            if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
              startPostponedEnterTransition()
            }
            return false
          }

        })
        .into(detail_recipe_image)
  }

  private fun setupFavoriteButton(recipe: Recipe) {
    isSavedRecipe = intent.getBooleanExtra(FROM_FAVORITE, false)
    presenter.setUpFavoriteButton(isSavedRecipe, firstTimeInActivity)
    detail_favorite_button.setOnClickListener {
      handleFavoriteButtonClick(recipe)
    }
  }

  override fun showFavoriteButtonAndMealPlanButtonTutorialCircles() {
    TapTargetSequence(this).targets(
        TapTarget.forView(
            findViewById<LottieAnimationView>(R.id.detail_favorite_button),
            getString(R.string.favorite_button_tutorial_title),
            getString(R.string.favorite_button_tutorial_message))
            .outerCircleColor(R.color.colorAccent)
            .outerCircleAlpha(0.96f)
            .transparentTarget(true)
            .titleTextSize(36)
            .titleTextColor(R.color.whiteText)
            .descriptionTextColor(R.color.whiteText)
            .descriptionTextAlpha(1f)
            .drawShadow(true)
            .cancelable(true)
            .tintTarget(true)
            .targetRadius(40),

        TapTarget.forView(
            findViewById<LottieAnimationView>(R.id.detail_save_to_meal_plan_button),
            getString(R.string.meal_plan_button_tutorial_title),
            getString(R.string.meal_plan_button_tutorial_message))
            .outerCircleColor(R.color.colorPrimary)
            .outerCircleAlpha(0.96f)
            .transparentTarget(true)
            .titleTextSize(36)
            .titleTextColor(R.color.whiteText)
            .descriptionTextColor(R.color.whiteText)
            .descriptionTextAlpha(1f)
            .drawShadow(true)
            .cancelable(true)
            .tintTarget(true)
            .targetRadius(70)

    ).start()
  }

  override fun showShareButtonTutorialCircle() {
    TapTargetView.showFor(this,
        TapTarget.forView(
            findViewById<View>(R.id.detail_share_button),
            getString(R.string.share_button_tutorial_title),
            getString(R.string.share_button_tutorial_message))
            .outerCircleColor(R.color.colorAccent)
            .outerCircleAlpha(0.96f)
            .transparentTarget(true)
            .titleTextSize(36)
            .titleTextColor(R.color.whiteText)
            .descriptionTextColor(R.color.whiteText)
            .descriptionTextAlpha(1f)
            .drawShadow(true)
            .cancelable(true)
            .tintTarget(true)
            .targetRadius(40)
    )
    sharedPreferences.edit().putBoolean(PREFS_SEEN_SHARE_TUTORIAL_KEY, true).apply()
  }

  override fun setIsFirstTimeInActivity(isFirstTime: Boolean) {
    sharedPreferences.edit().putBoolean(PREFS_FIRST_TIME_KEY, isFirstTime).apply()
  }

  private fun handleFavoriteButtonClick(recipe: Recipe) {
    fromFavoriteClick = true
    presenter.onFavoriteButtonClicked(
        isSignedIn = firebaseAuthRepository.currentUser != null,
        savedRecipe = isSavedRecipe,
        recipe = recipe)
  }

  private fun setupMealPlanDialog(recipe: Recipe) {
    detail_save_to_meal_plan_button.setOnClickListener {
      val builder = AlertDialog.Builder(this)
      builder.setTitle(getString(R.string.detail_meal_plan_dialog_title))
      val days: Array<String> = DayOfWeek.values().map { it.name }.toTypedArray()
      selectedDay = days[0]
      builder.setSingleChoiceItems(days, 0) { _, which ->
        selectedDay = days[which]
      }
      builder.setPositiveButton(getString(R.string.common_ok)) { dialog, _ ->
        fromMealPlanClick = true
        presenter.onMealPlanDialogPositiveButtonClicked(
            isSignedIn = firebaseAuthRepository.currentUser != null,
            recipe = recipe,
            addedToMealPlan = addedToMealPlan,
            selectedDay = selectedDay,
            isSavedRecipe = isSavedRecipe)

        dialog.dismiss()
      }
      builder.setNegativeButton(getString(R.string.common_back)) { dialog, _ ->
        dialog.dismiss()
      }
      builder.create().show()
    }
  }

  private fun setUpTutorialsAndButtonsAfterTransitionAnimation() {
    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      val sharedElementEnterTransition = window.sharedElementEnterTransition
      sharedElementEnterTransition.addListener(object : TransitionListener {
        override fun onTransitionStart(transition: Transition) {
          //NoOp
        }

        override fun onTransitionEnd(transition: Transition) {
          setUpButtonsAndTutorials()
        }

        override fun onTransitionCancel(transition: Transition) {
          //NoOp
        }

        override fun onTransitionPause(transition: Transition) {
          //NoOp
        }

        override fun onTransitionResume(transition: Transition) {
          //NoOp
        }
      })
    } else {
      setUpButtonsAndTutorials() // still need to show tutorials and set click listeners
    }
  }

  private fun setUpButtonsAndTutorials() {
    setUpShareButton(contactPermissionGranted, (recipe.mealPlan || addedToMealPlan))
    setupFavoriteButton(recipe)
  }

  override fun setUpShareButton(permissionGranted: Boolean, addedToMealPlan: Boolean) {

    presenter.showShareButtonTutorial(addedToMealPlan, hasSeenShareButtonTutorial)

    detail_share_button.setOnClickListener {
      onShareButtonClicked(permissionGranted, addedToMealPlan)
    }
  }

  private fun onShareButtonClicked(permissionGranted: Boolean, savedToMealPLan: Boolean) {
    isSavedRecipe = intent.getBooleanExtra(FROM_FAVORITE, false)
    presenter.onShareButtonClicked(permissionGranted, savedToMealPLan)
  }

  override fun handlePermissionRequest() {
    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
            permission.READ_CONTACTS)) {

      val builder = AlertDialog.Builder(this)
      builder.setTitle(getString(R.string.detail_permission_dialog_title))
      builder.setMessage(getString(R.string.detail_permission_dialog_message))
      builder.setPositiveButton(getString(R.string.common_ok)) { dialog, _ ->
        requestContactPermission()
        dialog.dismiss()
      }
      builder.setNegativeButton(getString(R.string.common_back)) { dialog, _ ->
        dialog.dismiss()
      }
      builder.create().show()

    } else {
      requestContactPermission()
    }
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
      grantResults: IntArray) {
    when (requestCode) {
      MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
        if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
          launchContactPicker()
          setShareButtonBackground(R.drawable.ic_share_index_blue_24dp)
        } else {
          setShareButtonBackground(R.drawable.ic_share_disabled_24dp)
        }
        return
      }
    }
  }

  override fun launchContactPicker() {
    val contactPickerIntent = Intent(Intent.ACTION_PICK,
        ContactsContract.CommonDataKinds.Email.CONTENT_URI)
    startActivityForResult(contactPickerIntent, RC_CONTACT_PICKER)
  }

  override fun showSnackbar(
      snackbarStringRes: Int,
      snackbarString: String,
      snackbarstringParameter: String) {
    uiUtils.showSnackbar(
        view = detail_recipe_parent_layout,
        snackbarStringRes = snackbarStringRes,
        snackbarString = snackbarString,
        snackbarStringParameter = snackbarstringParameter)
  }

  private fun requestContactPermission() {
    ActivityCompat.requestPermissions(this, arrayOf(permission.READ_CONTACTS),
        MY_PERMISSIONS_REQUEST_READ_CONTACTS)
  }

  override fun setMealPlanButtonText(mealPlanButtonStringRes: Int, selectedDayString: String?) {
    val favoriteButtonText = when {
      mealPlanButtonStringRes != 0 -> getString(mealPlanButtonStringRes)
      !selectedDayString.isNullOrBlank() -> selectedDayString
      else -> ""
    }
    detail_save_to_meal_plan_button.text = favoriteButtonText
  }

  override fun showRecipeDetailSnackBar(snackBarStringRes: Int, snackBarString: String?,
      dayOfWeek: String?) {
    val snackBarText = when {
      snackBarStringRes != 0 -> getString(snackBarStringRes, dayOfWeek)
      !snackBarString.isNullOrBlank() -> snackBarString
      else -> ""
    }
   uiUtils.showSnackbar(view = detail_recipe_parent_layout, snackbarString = snackBarText)
  }

  override fun setFavoritedButtonAnimationDirection(speed: Float) {
    detail_favorite_button.speed = speed
    detail_favorite_button.playAnimation()
  }

  //TODO this needs some work
  override fun onDestroy() {
    super.onDestroy()
    firebaseRepo.purgeUnsavedRecipe(recipe)
  }

  override fun launchUIAuthActivity() {
    startActivityForResult(firebaseAuthRepository.authActivityIntent(), RC_SIGN_IN_DETAIL)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    when (requestCode) {
      RC_SIGN_IN_DETAIL -> {
        val response = IdpResponse.fromResultIntent(data)

        if (resultCode == Activity.RESULT_OK) {
          if (fromFavoriteClick) {
            presenter.onFavoriteButtonClicked(
                isSignedIn = true,
                savedRecipe = isSavedRecipe,
                recipe = recipe)
          }
          if (fromMealPlanClick) {
            presenter.onMealPlanDialogPositiveButtonClicked(
                isSignedIn = true,
                recipe = recipe,
                addedToMealPlan = addedToMealPlan,
                selectedDay = selectedDay,
                isSavedRecipe = isSavedRecipe)
          }

        } else {
          when (response?.error?.errorCode) {
            ErrorCodes.NO_NETWORK -> uiUtils.showSnackbar(
                view = detail_recipe_parent_layout,
                snackbarStringRes = R.string.no_network_error_message)
            else -> uiUtils.showSnackbar(
                view = detail_recipe_parent_layout,
                snackbarStringRes = R.string.generic_error_message
            )
          }
          Timber.e(response?.error, "Failed to log-in")
        }
      }
      RC_CONTACT_PICKER -> {
        if (resultCode == Activity.RESULT_OK) {
          showSharingDialog(data)
        }
      }
    }
  }

  private fun showSharingDialog(data: Intent?) {
    setDestinationUserInfoFromContacts(data)
    val builder = AlertDialog.Builder(this)
    builder.setTitle(getString(R.string.share_dialog_title))
    builder.setMessage(getString(R.string.share_dialog_message, destinationUserEmail))
    builder.setPositiveButton(getString(R.string.common_ok)) { dialog, _ ->
      writeRecipeToSharedUserDb(destinationUserEmail, destinationUserDisplayName)
      dialog.dismiss()
    }
    builder.setNegativeButton(getString(R.string.common_back)) { dialog, _ ->
      dialog.dismiss()
    }
    builder.create().show()
  }

  private fun setDestinationUserInfoFromContacts(data: Intent?) {
    data?.let {
      val contactData = it.data
      val cursor = contentResolver.query(
          contactData ?: Uri.EMPTY,
          null,
          null,
          null,
          null)
      cursor?.run {
        if (cursor.moveToFirst()) {
          destinationUserEmail = cursor.getString(cursor.getColumnIndex(Email.DATA))
          destinationUserDisplayName = cursor.getString(
              cursor.getColumnIndex(Email.DISPLAY_NAME_PRIMARY))
          cursor.close()
        }
      }
    }
  }

  private fun writeRecipeToSharedUserDb(destinationEmail: String, destinationUserName: String?) {
    firebaseRepo.usersReference
        .addListenerForSingleValueEvent(object : ValueEventListener {
          override fun onDataChange(snapshot: DataSnapshot) {

            val userToShareWith = snapshot.children.map { it.getValue(User::class.java) }
                .firstOrNull { registeredUser ->
                  registeredUser?.email.equals(destinationEmail, ignoreCase = true)
                }

            presenter.saveRecipeToSharedDB(
                userToShareWith = userToShareWith,
                recipe = recipe,
                dayOfWeek = DayOfWeek.valueOf(detail_save_to_meal_plan_button.text.toString()),
                destinationUserName = destinationUserName,
                destinationEmail = destinationEmail)
          }

          override fun onCancelled(error: DatabaseError) {
            Timber.e(error.toException(), "Failed to share recipe")
            uiUtils.showSnackbar(
                view = detail_recipe_parent_layout,
                snackbarStringRes = R.string.share_snackbar_error_text)
          }
        })
  }
}
