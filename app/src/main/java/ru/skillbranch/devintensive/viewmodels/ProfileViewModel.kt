package ru.skillbranch.devintensive.viewmodels

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.skillbranch.devintensive.models.Profile
import ru.skillbranch.devintensive.repositories.PreferencesRepository

class ProfileViewModel : ViewModel() {

    private val repository: PreferencesRepository = PreferencesRepository
    private val profileData = MutableLiveData<Profile>()
    private val appTheme = MutableLiveData<Int>()
    private val isRepositoryError = MutableLiveData<Boolean>()

    init {
        Log.d("M_ProfileViewModel", "init view model")
        profileData.value = repository.getProfile()
        appTheme.value = repository.getAppTheme()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("M_ProfileViewModel", "view model cleared")
    }

    fun getProfileData(): LiveData<Profile> = profileData

    fun getTheme(): LiveData<Int> = appTheme

    fun isRepositoryValid(): LiveData<Boolean> = isRepositoryError

    fun saveProfileData(profile: Profile) {
        repository.saveProfile(profile)
        profileData.value = profile
    }

    fun switchTheme() {
        if (appTheme.value == AppCompatDelegate.MODE_NIGHT_YES) {
            appTheme.value = AppCompatDelegate.MODE_NIGHT_NO
        } else {
            appTheme.value = AppCompatDelegate.MODE_NIGHT_YES
        }

        repository.saveAppTheme(appTheme.value!!)
    }

    fun repositoryValidation(url: String) {
        val regexRepo =
            Regex("^(https://)?(www.)?(github.com/)(?!${getRegexExceptions()})[a-zA-Z0-9_-]+(/)?$")

        isRepositoryError.value = url.isEmpty() || regexRepo.matches(url)
    }

    private fun getRegexExceptions(): String {
        val exceptions = arrayOf(
            "enterprise", "features", "topics", "collections", "trending", "events", "join",
            "pricing", "nonprofit", "customer-stories", "security", "login", "marketplace"
        )
        return exceptions.joinToString("|")
    }
}