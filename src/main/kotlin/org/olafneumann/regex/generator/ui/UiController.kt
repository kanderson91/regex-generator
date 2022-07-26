package org.olafneumann.regex.generator.ui

import kotlinx.browser.window
import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.olafneumann.regex.generator.regex.RecognizerMatch
import org.olafneumann.regex.generator.regex.RecognizerRegistry
import org.olafneumann.regex.generator.regex.TextSplitter
import org.olafneumann.regex.generator.ui.html.CookieBanner
import org.olafneumann.regex.generator.ui.html.WizardDisplay


class UiController : DisplayContract.Controller {
    private val view: DisplayContract.View = HtmlView(this, MAX_INPUT_LENGTH)
    override var matchPresenters = listOf<MatchPresenter>()
    override var wizardDisplay = WizardDisplay(TextSplitter(view.inputText), this)

    init {
        // remove warning that is not required
        view.hideShortenWarning(immediately = true)

        // if copy is not available: remove copy button
        if (window.navigator.clipboard == undefined) {
            view.hideCopyButton()
        }

        // handle the cookie banner
        CookieBanner.initialize(
            hasUserConsent = { ApplicationSettings.hasUserConsent },
            setUserConsent = { ApplicationSettings.hasUserConsent = it }
        )

        // Prepare UI
        view.options = ApplicationSettings.viewOptions
        view.applyInitParameters(defaultText = VAL_EXAMPLE_INPUT)
    }

    private fun List<RecognizerMatch>.toPresentation(): MatchPresenter =
        matchPresenters.findLast {
            it.recognizerMatches.containsAll(this)
                    && this.containsAll(it.recognizerMatches)
        } ?: MatchPresenter(this)

    override fun onButtonHelpClick() = view.showUserGuide(false)
    fun showInitialUserGuide() = view.showUserGuide(true)

    override fun onInputTextChanges(newInput: String) {
        val matches = RecognizerRegistry.findMatches(newInput)
        val matchGroups = matches.groupBy { it.ranges }.values
        matchPresenters = matchGroups.map { it.toPresentation() }

        view.showMatchingRecognizers(newInput, matchPresenters)

        //New stuff
        val textSplitter = TextSplitter(newInput)
        println(textSplitter.words)
        println(textSplitter.punctuationMarks)

        wizardDisplay = WizardDisplay(textSplitter, this)
        view.showResultingPattern(wizardDisplay.getOutputPattern())
    }

    override fun onWizardChanges() {
        println(wizardDisplay.getOutputPattern().pattern)
        view.showResultingPattern(wizardDisplay.getOutputPattern())
    }

    override fun onSuggestionClick(recognizerMatch: RecognizerMatch) {
        val matchPresenter = matchPresenters.find { it.recognizerMatches.contains(recognizerMatch) }
        if (matchPresenter == null || matchPresenter.deactivated) {
            return
        }
        // determine selected state of the presenter
        matchPresenter.selectedMatch = if (matchPresenter.selected) null else recognizerMatch

        disableNotClickableSuggestions()
    }

    override fun disableNotClickableSuggestions() {
        // find matches to disable
        val selectedMatches = matchPresenters.filter { it.selected }
        matchPresenters.filter { !it.selected }
            .forEach { match -> match.deactivated = selectedMatches.any { match.intersect(it) } }

        computeOutputPattern()
    }


    override fun onOptionsChange(options: RecognizerCombiner.Options) {
        ApplicationSettings.viewOptions = options
        computeOutputPattern()
    }

    private fun computeOutputPattern() {
        val result = RecognizerCombiner.combineMatches(
            view.inputText,
            matchPresenters.mapNotNull { it.selectedMatch }.toList(),
            view.options
        )
        view.showResultingPattern(result)
    }

    override val allRecognizerMatcher: Collection<RecognizerMatch>
        get() = matchPresenters.flatMap { it.recognizerMatches }
    override val selectedRecognizerMatches: Collection<RecognizerMatch>
        get() = matchPresenters.mapNotNull { it.selectedMatch }


    companion object {
        const val VAL_EXAMPLE_INPUT =
            "May 15, 2022"
        private const val MAX_INPUT_LENGTH = 1000
    }
}
