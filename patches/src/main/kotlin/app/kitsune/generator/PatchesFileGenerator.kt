package app.kitsune.generator

import app.kitsune.patcher.patch.Patch

internal interface PatchesFileGenerator {
    fun generate(patches: Set<Patch<*>>)
}
