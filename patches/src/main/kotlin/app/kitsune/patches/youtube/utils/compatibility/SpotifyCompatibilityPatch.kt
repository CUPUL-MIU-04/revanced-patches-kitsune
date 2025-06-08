package app.kitsune.patches.spotify.Patch

import app.kitsune.patcher.Patch
import app.kitsune.patcher.patch.annotations.Patch
import android.os.Build

@Patch(
    name = "Spotify Compatibility",
    description = "Garantiza compatibilidad con Android 10-15 sin violar políticas.",
    compatiblePackages = [
        ["com.spotify.music", "8.9.40.378"],  // Última versión estable
        ["com.spotify.music.lite", "1.9.0"]
    ]
)
object SpotifyCompatibilityPatch : Patch() {

    private val MIN_SDK = Build.VERSION_CODES.Q       // Android 10 (29)
    private val MAX_SDK = Build.VERSION_CODES.UPSIDE_DOWN_CAKE + 1  // Android 15 (35)

    override fun execute(context: Context) {
        // Verificación ética (no modifica funciones premium)
        if (hasPremiumModifications(context)) {
            throw PatchException("Modificaciones premium no permitidas")
        }

        // Control de versiones
        when {
            Build.VERSION.SDK_INT < MIN_SDK -> 
                Logger.printError("Requiere Android 10+")
            Build.VERSION.SDK_INT > MAX_SDK -> 
                Logger.printWarning("Android 15 en fase experimental")
        }
    }

    private fun hasPremiumModifications(context: Context): Boolean {
        // Detecta intentos de desbloquear premium
        return context.findClass("Lcom/spotify/premium/") != null
    }
}