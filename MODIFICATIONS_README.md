# Project Modifications - dev-gustavo branch

This branch contains modifications from the original project to enable local development and address build/runtime issues.

## Modifications Summary:

### 1. Android Application Build Fixes & Setup:

*   **Project Structure Correction:**
    *   The `app-android/build.gradle.kts` was originally configured as an application module, while the actual application source was within `app-android/app`. The `app-android/build.gradle.kts` was converted to a top-level build file.
    *   The `app-android/settings.gradle.kts` was updated to correctly include the `:app` module, aligning with a standard multi-module Gradle project structure.
*   **Gradle and Android Gradle Plugin (AGP) Version Update:**
    *   Updated the AGP version from `8.1.4` to `8.13.2` in `app-android/gradle/libs.versions.toml` to address compatibility issues and leverage newer features.
    *   Updated the Gradle version from `8.4` to `9.2.1` in `app-android/gradle/wrapper/gradle-wrapper.properties` for improved build performance and compatibility.
*   **Gradle Daemon Disabled:**
    *   Added `org.gradle.daemon=false` to `app-android/gradle.properties`. This was a workaround to resolve a `jlink` error and subsequent Gradle daemon crashes during the build process.

### 2. Backend and Database Local Setup:

*   **Docker Installation:**
    *   Docker and Docker Compose were installed on the system to facilitate local development of the backend services.
*   **Docker Network Creation:**
    *   The `proxy` Docker network was created using `sudo docker network create proxy` to ensure proper communication between Docker containers as defined in `compose.yml`.
*   **Database Host Configuration:**
    *   The `DB_HOST` in `backend/env/localhost.env` was changed from a remote IP address (`195.200.2.145`) to `localhost`. This ensures the backend connects to the local MySQL instance running via Docker Compose.
*   **SQL Script Syntax Corrections:**
    *   In `backend/sql/tables.sql`, `IF NOT EXISTS` was removed from all `CREATE INDEX` statements to resolve MySQL syntax errors during database setup.
    *   In `backend/sql/populate.sql`, double quotes (`"`) around column names were replaced with backticks (`` ` ``) to conform to standard MySQL identifier quoting.
*   **Database Setup Script Enhancement:**
    *   `backend/sql/DatabaseSetup.js` was modified to include a `DROP DATABASE IF EXISTS` statement. This ensures a clean database state on each setup, preventing "duplicate key" errors.
*   **Backend Code Fix (Table Name Case-Sensitivity):**
    *   In `backend/src/User.js`, the table name `USERS` was changed to `users` to match the case-sensitive table name in the MySQL database, resolving a "Table doesn't exist" error.

## Getting Started (Local Development):

1.  **Ensure Docker is Running:** Make sure the Docker daemon is active on your system.
2.  **Start Backend Services:** Navigate to the `backend/` directory and run `sudo docker compose up -d`.
3.  **Initialize Database:** Run `npm run database-setup` from the `backend/` directory.
4.  **Run Android App:** Build and run the Android application on an emulator or physical device.

You can log in with `alice@example.com` and password `hash1` after the setup is complete.
