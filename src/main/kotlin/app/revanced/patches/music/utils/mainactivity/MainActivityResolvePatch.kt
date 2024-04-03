package app.revanced.patches.music.utils.mainactivity

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.music.utils.integrations.Constants.UTILS_PATH
import app.revanced.patches.music.utils.mainactivity.fingerprints.MainActivityFingerprint
import app.revanced.util.exception
import com.android.tools.smali.dexlib2.iface.ClassDef

object MainActivityResolvePatch : BytecodePatch(
    setOf(MainActivityFingerprint)
) {
    lateinit var mainActivityClassDef: ClassDef
    private lateinit var onCreateMethod: MutableMethod

    override fun execute(context: BytecodeContext) {
        MainActivityFingerprint.result?.let {
            mainActivityClassDef = it.classDef
            onCreateMethod = it.mutableMethod
        } ?: throw MainActivityFingerprint.exception
    }

    fun injectInit(
        methods: String,
        descriptor: String
    ) {
        onCreateMethod.addInstruction(
            2,
            "invoke-static/range {p0 .. p0}, $UTILS_PATH/$methods;->$descriptor(Landroid/app/Activity;)V"
        )
    }
}