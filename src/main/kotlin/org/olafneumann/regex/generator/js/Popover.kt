package org.olafneumann.regex.generator.js

import org.w3c.dom.HTMLElement
import kotlin.js.json

class Popover(
    element: HTMLElement,
    private val container: String = "body",
    private val contentElement: HTMLElement,
    private val placement: String = "right",
    private val title: String = "",
    private val trigger: String = "click"
) {
    private var jquery: JQuery

    init {
        jquery = jQuery(element)
        jquery.popover(createOptionsJson())
    }

    // fun show() = jquery.popover("show")
    // fun hide() = jquery.popover("hide")
    fun dispose() = jquery.popover("dispose")
    fun toggle() = jquery.popover("toggle")

    private fun createOptionsJson()
            = json(
        "container" to container,
        "content" to contentElement,
        "html" to true /*html*/,
        "placement" to placement,
        "title" to title,
        "trigger" to trigger
    )
}
