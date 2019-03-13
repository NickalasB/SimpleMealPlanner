package com.zonkey.simplemealplanner.activity

import android.Manifest
import android.Manifest.permission
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.provider.ContactsContract
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.zonkey.simplemealplanner.R
import com.zonkey.simplemealplanner.adapter.FROM_FAVORITE
import com.zonkey.simplemealplanner.adapter.FULL_RECIPE
import com.zonkey.simplemealplanner.firebase.DefaultFirebaseAuthRepository
import com.zonkey.simplemealplanner.firebase.FirebaseRecipeRepository
import com.zonkey.simplemealplanner.model.DayOfWeek
import com.zonkey.simplemealplanner.model.Recipe
import com.zonkey.simplemealplanner.model.User
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

class RecipeDetailActivity : AppCompatActivity(), RecipeDetailView {

  @Inject
  lateinit var firebaseRepo: FirebaseRecipeRepository

  @Inject
  lateinit var firebaseAuthRepository: DefaultFirebaseAuthRepository

  private val TAG = this::class.java.simpleName

  private lateinit var presenter: RecipeDetailActivityPresenter

  override var isSavedRecipe = false

  override var addedToMealPlan = false

  private lateinit var recipe: Recipe

  private var selectedDay: String = ""

  private var favoriteClick = false

  private var mealPlanClick = false

  private var destinationUserEmail = ""

  private var destinationUserDisplayName = ""

  companion object {
    fun buildIntent(context: Context): Intent = Intent(context, RecipeDetailActivity::class.java)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    AndroidInjection.inject(this)
    setContentView(R.layout.activity_recipe_detail)

    presenter = RecipeDetailActivityPresenter(this, firebaseRepo)

    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      postponeEnterTransition()
    }

    recipe = Gson().fromJson(intent.getStringExtra(FULL_RECIPE), Recipe::class.java)
    loadRecipeImage(recipe)

    detail_collapsing_toolbar.title = recipe.label

    detail_collapsing_toolbar.setCollapsedTitleTextColor(
        ContextCompat.getColor(this, R.color.lightBackground))

    detailed_recipe_card_view.setRecipeDetailCardItems(recipe)

    setupFavoriteButton(recipe)

    setupMealPlanDialog(recipe)

    presenter.setUpMealPlanButtonText(recipe)

    val contactPermissionGranted = ContextCompat.checkSelfPermission(this,
        Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED

    setUpShareButton(contactPermissionGranted)

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
    presenter.setSavedRecipeIcon(isSavedRecipe)

    detail_favorite_button.setOnClickListener {
      favoriteClick = true
      presenter.onFavoriteButtonClicked(
          isSignedIn = firebaseAuthRepository.currentUser != null,
          savedRecipe = isSavedRecipe,
          recipe = recipe)
    }
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
        mealPlanClick = true
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

  private fun setUpShareButton(permissionGranted: Boolean) {

    //ToDo handle disabling of button from the start if needed

    detail_share_button.setOnClickListener {
      if (!permissionGranted) {
        handlePermissionRequest()
      } else {
        launchContactPicker()
      }
    }
  }

  private fun handlePermissionRequest() {
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
        } else {
          detail_share_button.background = ContextCompat.getDrawable(this,
              R.drawable.ic_share_disabled_24dp)
        }
        return
      }
    }
  }

  private fun launchContactPicker() {
    val contactPickerIntent = Intent(Intent.ACTION_PICK,
        ContactsContract.CommonDataKinds.Email.CONTENT_URI)
    startActivityForResult(contactPickerIntent, RC_CONTACT_PICKER)
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
    showSnackbar(snackbarString = snackBarText)
  }

  override fun setFavoritedButtonIcon(icon: Int) {
    detail_favorite_button.background = ContextCompat.getDrawable(this, icon)
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
          if (favoriteClick) {
            presenter.onFavoriteButtonClicked(true, isSavedRecipe, recipe)
          }
          if (mealPlanClick) {
            presenter.onMealPlanDialogPositiveButtonClicked(
                isSignedIn = true,
                recipe = recipe,
                addedToMealPlan = addedToMealPlan,
                selectedDay = selectedDay,
                isSavedRecipe = isSavedRecipe)
          }

        } else {
          Timber.e(response?.error, "Failed to log-in")
        }
      }
      RC_CONTACT_PICKER -> {
        if (resultCode == Activity.RESULT_OK) {
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
                destinationUserEmail = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                destinationUserDisplayName = cursor.getString(
                    cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Email.DISPLAY_NAME_PRIMARY))
                writeRecipeToSharedUserDb(destinationUserEmail, destinationUserDisplayName)
                cursor.close()
              }
            }
          }
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
                destinationUserName = destinationUserName,
                destinationEmail = destinationEmail)
          }

          override fun onCancelled(error: DatabaseError) {
            Timber.e(error.toException(), "Failed to share recipe from $TAG")
            showSnackbar(snackbarStringRes = R.string.share_snackbar_error_text)
          }
        })
  }

  override fun showSnackbar(snackbarStringRes: Int, snackbarString: String,
      snackbarstringParameter: String) {
    val snackBarText = when {
      snackbarStringRes != 0 -> getString(snackbarStringRes, snackbarstringParameter)
      else -> snackbarString
    }
    Snackbar.make(detail_recipe_parent_layout, snackBarText, Snackbar.LENGTH_LONG).show()
  }
}
