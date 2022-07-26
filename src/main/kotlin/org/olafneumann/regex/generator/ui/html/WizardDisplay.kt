@file:Suppress("MaxLineLength")
package org.olafneumann.regex.generator.ui.html

import kotlinx.browser.document
import kotlinx.html.dom.create
import kotlinx.html.js.option
import kotlinx.html.js.select
import kotlinx.html.js.onChangeFunction
import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.olafneumann.regex.generator.regex.TextSplitter
import org.olafneumann.regex.generator.ui.DisplayContract
import org.olafneumann.regex.generator.ui.HtmlHelper
import org.olafneumann.regex.generator.ui.HtmlView
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.HTMLTableElement
import org.w3c.dom.asList
import org.w3c.dom.get

class WizardDisplay(textSplitter: TextSplitter, private val presenter: DisplayContract.Controller) {

    private val wizardTable = HtmlHelper.getElementById<HTMLTableElement>(HtmlView.ID_WIZARD_TABLE)
    private val columns : List<String>

    private val digitsPattern: Regex = Regex("\\d+")
    private val lettersPattern: Regex = Regex("[a-zA-Z]+")
    private val digitPattern: Regex = Regex("\\d")
    private val letterPattern: Regex = Regex("[a-zA-Z]")
    private val digitOrLetterPattern : Regex = Regex("\\w")
    private val punctuationPatternString : String = "\\W+"

    private var exactMatchSelectors = mutableListOf<HTMLSelectElement>()
    private var quantifierSelectors = mutableListOf<HTMLSelectElement>()
    private var exactMatchSelections = mutableListOf<String?>()
    private var quantifierSelections = mutableListOf<String?>()

    init {
        columns = textSplitter.words!!
        //Reset the table
        wizardTable.rows.asList().size
        for (i in wizardTable.rows.length - 1 downTo 0) {
            wizardTable.deleteRow(i)
        }
        addTextChunksToTopRow()
        addExactMatchRow()
        addQuantifierRow()
    }

    private fun addTextChunksToTopRow() {
        var textRow = wizardTable.insertRow(0)
        for ((index, chunk) in columns.withIndex()) {
            var cell = textRow.insertCell(index)
            cell.innerText = chunk
        }
    }

    private fun addExactMatchRow() {
        var row = wizardTable.insertRow(1)
        for ((index, chunk) in columns.withIndex()) {
            var cell = row.insertCell(index)
            var selector : HTMLSelectElement = addExactMatchSelector(chunk, index)
            cell.appendChild(addExactMatchSelector(chunk, index))
            exactMatchSelectors.add(selector)
            exactMatchSelections.add(selector.options[selector.selectedIndex]?.textContent)
        }
    }

    private fun addExactMatchSelector(text : String, index : Int): HTMLSelectElement{
        var exact = document.create.option(content = "exact phrase \"$text\"")
        var charType = "numbers or letters"
        if (digitsPattern.matches(text)) {
            charType = "numbers"
        } else if (lettersPattern.matches(text)) {
            charType = "letters"
        }
        var any = document.create.option(content = "any $charType")

        var selector = document.create.select {}
        selector.onchange = {
            println("on change")
            println("exact selected ${exact.selected}")
            quantifierSelectors[index].disabled = exact.selected
            quantifierSelectors[index].hidden = exact.selected
            exactMatchSelections[index] = selector.options[selector.selectedIndex]?.textContent
            presenter.onWizardChanges()
        }
        selector.add(exact)
        selector.add(any)

        return selector
    }

    private fun addQuantifierRow() {
        var row = wizardTable.insertRow(2)
        for ((index, chunk) in columns.withIndex()) {
            var cell = row.insertCell(index)
            var selector : HTMLSelectElement = addQuantifierSelector(chunk, index)
            cell.appendChild(selector)
            quantifierSelectors.add(selector)
            quantifierSelections.add(selector.options[selector.selectedIndex]?.textContent)
        }
    }

    private fun addQuantifierSelector(text : String, index : Int): HTMLSelectElement{
        val anyLength = document.create.option(content = "any length")
        val sameLength = document.create.option(content = "exactly ${text.length}")
        var selector = document.create.select {}
        selector.onchange = {
            quantifierSelections[index] = selector.options[selector.selectedIndex]?.textContent
            presenter.onWizardChanges()
        }
        selector.add(anyLength)
        selector.add(sameLength)

        //This selector should be disabled if "Exact Phrase" is selected above, which it is by default
        selector.disabled = true
        selector.hidden = true
        return selector
    }

    fun getOutputPattern() : RecognizerCombiner.RegularExpression {

        var parts = mutableListOf<RecognizerCombiner.RegularExpressionPart>()
        //In RLS Protect Docs you need to start a search with = if you want to use a regex
        parts.add(RecognizerCombiner.RegularExpressionPart(IntRange(-1, -1), "="))

        for ((index, chunk) in columns.withIndex()) {
            var pattern = ""
            val exactMatchSelection = exactMatchSelections[index]

            if (exactMatchSelection?.startsWith("exact phrase") == true) {
                pattern += chunk
            } else {
                if (exactMatchSelection?.endsWith("numbers") == true) {
                    pattern += digitPattern.pattern
                } else if (exactMatchSelection?.endsWith("letters") == true) {
                    pattern += letterPattern.pattern
                } else {
                    pattern += digitOrLetterPattern.pattern
                }

                val quantifierSelection = quantifierSelections[index]

                if (quantifierSelection?.startsWith("exactly") == true) {
                    pattern += "{${chunk.length}}"
                } else {
                    pattern += "+"
                }
            }

            //TODO put the correct range in this object
            val part = RecognizerCombiner.RegularExpressionPart(IntRange(-1, -1), pattern)
            parts.add(part)


            //Add a part for punctuation in between chunks
            if (index < columns.size - 1) {
                val puncPart = RecognizerCombiner.RegularExpressionPart(IntRange(-1, -1), punctuationPatternString)
                parts.add(puncPart)
            }
        }
        return RecognizerCombiner.RegularExpression(parts)
    }
}
