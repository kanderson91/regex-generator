package org.olafneumann.regex.generator.ui.html

import com.benasher44.uuid.uuid4
import kotlinx.browser.document
import kotlinx.html.*
import kotlinx.html.dom.create
import kotlinx.html.injector.CustomCapture
import kotlinx.html.injector.InjectByClassName
import kotlinx.html.injector.InjectCapture
import kotlinx.html.injector.inject
import kotlinx.html.js.div
import kotlinx.html.js.onInputFunction
import org.olafneumann.regex.generator.js.Popover
import org.olafneumann.regex.generator.regex.RecognizerMatch
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLLabelElement
import org.w3c.dom.get
import kotlin.properties.Delegates

class CapturingGroupPopover(
    element: HTMLElement,
    private val match: RecognizerMatch,
    private val recalculationTrigger: () -> Unit
) {
    companion object {
        private const val CLASS_CAP_CHECK = "rg-capturing-group-check"
        private const val CLASS_CAP_NAME = "rg-capturing-group-name"
        private const val CLASS_CAP_NAME_DIV = "rg-capturing-group-name-div"
    }

    private val id = uuid4().toString().replace('-', '_')
    private val idCheck = id + "_check"
    private val idName = id + "_name"
    private val idNameDiv = id + "_name_div"
    val popover: Popover
    private val elements = Elements()

    init {
        popover = Popover(
            element = element,
            contentElement = document.create.inject(elements, listOf(
                InjectByClassName(CLASS_CAP_CHECK) to Elements::checkbox,
                InjectByClassName(CLASS_CAP_NAME) to Elements::nameText,
                InjectById(idNameDiv) to Elements::nameDiv
            )).div {
                div {
                    label {
                        this.htmlFor = idCheck
                        +"Capturing group:"
                    }
                    +" "
                    input(type = InputType.checkBox, classes= CLASS_CAP_CHECK) {
                        this.id = idCheck
                        checked = match.isCapturingGroup
                        onInputFunction = { _ ->
                            match.isCapturingGroup = elements.checkbox.checked
                            showNameDiv(show = match.isCapturingGroup)
                            triggerRegexRecalculation()
                        }
                    }
                }
                div {
                    id = idNameDiv
                    label {
                        this.htmlFor = idName
                        +"Name: "
                    }
                    input(type = InputType.text, classes = CLASS_CAP_NAME) {
                        this.id = idName
                        value = match.capturingGroupName
                        onInputFunction = { _ ->
                            match.capturingGroupName = elements.nameText.value.trim()
                            triggerRegexRecalculation()
                        }
                    }
                }
            },
            html = true,
            trigger = "manual",
        )
        showNameDiv(show = match.isCapturingGroup)
    }

    private fun showNameDiv(show: Boolean) {
        elements.nameDiv.style.display = if (show) "block" else "none"
    }

    private fun triggerRegexRecalculation() {
        recalculationTrigger()
    }

    private class InjectById(private val id: String) : CustomCapture {
        override fun apply(element: HTMLElement): Boolean = element.id == id
    }

    private class Elements {
        var checkbox: HTMLInputElement by Delegates.notNull()
        var nameText: HTMLInputElement by Delegates.notNull()
        var nameDiv: HTMLElement by Delegates.notNull()
    }
}