package org.olafneumann.regex.generator.js

import org.w3c.dom.HTMLElement
import kotlin.js.Json
import kotlin.js.json

@JsName("$")
external fun jQuery(id: String): JQuery
@JsName("$")
external fun jQuery(element: HTMLElement): JQuery

@Suppress("TooManyFunctions")
external class JQuery {
    @Suppress("UnusedPrivateMember")
    fun on(type: String, callback: () -> Unit)
    fun slideDown(): JQuery
    fun slideUp(): JQuery
    fun hide(): JQuery
    fun parent(): JQuery
    fun remove(): JQuery

    @Suppress("UnusedPrivateMember")
    fun find(selector: String): JQuery

    @Suppress("UnusedPrivateMember")
    fun attr(name: String, newValue: Any = definedExternally): Any?
    @Suppress("UnusedPrivateMember")
    fun css(css: Any)
    fun height(): Int
    @Suppress("UnusedPrivateMember")
    fun each(function: ((Int, HTMLElement) -> Unit))

    @Suppress("UnusedPrivateMember")
    fun animate(properties: Json, duration: Int = definedExternally, easing: String = definedExternally)
    fun stop()

    fun popover(options: Json)
}

class PopoverOptions(
    private val container: String = "body",
    private val contentString: String? = null,
    private val contentElement: HTMLElement? = null,
    private val html: Boolean = false,
    private val placement: String = "right",
    private val title: String = "",
    private val trigger: String = "click"
) {
    fun toJson()
        = json(
            "container" to container,
            "content" to (contentString ?: contentElement),
            "html" to html,
            "placement" to placement,
            "title" to title,
            "trigger" to trigger
        )
}
