### docs/Development/Creating-Patches.md
```markdown
# Creando Nuevos Parches

## Estructura básica
```kotlin
@Patch("Kitsune-Enhancer")
class EnhancerPatch : BytecodePatch(
    listOf(
        "Lcom/google/android/apps/youtube/PlayerActivity;"
    )
) {
    override fun execute(context: BytecodeContext) {
        // Implementación del parche
    }
}