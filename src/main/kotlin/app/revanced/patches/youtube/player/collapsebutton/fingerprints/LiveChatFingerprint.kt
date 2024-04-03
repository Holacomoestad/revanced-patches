package app.revanced.patches.youtube.player.collapsebutton.fingerprints

import app.revanced.patches.youtube.utils.resourceid.SharedResourceIdPatch.LiveChatButton
import app.revanced.util.fingerprint.LiteralValueFingerprint
import com.android.tools.smali.dexlib2.Opcode

internal object LiveChatFingerprint : LiteralValueFingerprint(
    opcodes = listOf(Opcode.NEW_INSTANCE),
    literalSupplier = { LiveChatButton }
)