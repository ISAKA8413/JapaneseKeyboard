package com.kazumaproject.markdownhelperkeyboard.ai

import org.json.JSONObject

internal data class AiModelInfo(val name: String, val flags: Int, val projector: Boolean = false) {
    fun encode(): String = JSONObject().put("name", name).put("flags", flags)
        .put("projector", projector).toString()

    companion object {
        fun decode(value: String): AiModelInfo = JSONObject(value).let {
            AiModelInfo(it.optString("name"), it.optInt("flags"), it.optBoolean("projector"))
        }
    }
}

/** All methods except cancel run on the service actor, outside the IME process. */
internal interface AiRuntimeBackend : AutoCloseable {
    val modalityFlags: Int
    fun inspect(path: String): AiModelInfo
    fun initialize(path: String, backend: String, projectorPath: String): String
    fun resetCancellation()
    fun generate(prompt: String, mediaPath: String, mediaType: Int, instruction: String = "", targetLanguage: String = ""): String
    fun cancel()
}

/**
 * 【自作の改造】ai-runtime（GgufEngine）を外したので、GGUF 形式の Gemma は使えない。
 * 呼ばれたら「使えない」と分かるエラーを返す（LiteRT 形式のモデルは今まで通り使える）。
 */
internal class GgufRuntimeBackend : AiRuntimeBackend {
    override val modalityFlags: Int = 0

    private fun unsupported(): Nothing =
        throw UnsupportedOperationException("この自作版では GGUF 形式の AI モデルは使えません（LiteRT 形式を使ってください）")

    override fun inspect(path: String): AiModelInfo = unsupported()
    override fun initialize(path: String, backend: String, projectorPath: String): String = unsupported()
    override fun resetCancellation() {}
    override fun generate(prompt: String, mediaPath: String, mediaType: Int, instruction: String, targetLanguage: String): String =
        unsupported()
    override fun cancel() {}
    override fun close() {}
}
