// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    // Add the dependency for the Google services Gradle plugin
    id("com.google.gms.google-services") version "4.4.4" apply false
    id("org.sonarqube") version "7.2.3.7755"
}
sonar {
  properties {
    property("sonar.projectKey", "UWP-Bonfire_Bonfire-Android")
    property("sonar.organization", "uwp-bonfire")
  }
}
