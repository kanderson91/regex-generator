package org.olafneumann.regex.generator.regex

class TextSplitter(text : String){

    val punctuationPattern: Regex = Regex("\\W+")
    var words: List<String>? = null
    //private val wordPatternStrings: List<String> = mutableListOf()
    var punctuationMarks: MutableList<MatchResult>? = null
    //private val punctuationMarkPatternStrings: List<String> = mutableListOf()

    init {
        splitStringIntoWords(text)
    }

    private fun splitStringIntoWords(s: String) {
        //If this starts or ends with punctuation it will put empty strings at the start of end of the list
        this.words = s.split(punctuationPattern)
        this.punctuationMarks = punctuationPattern.findAll(s).toMutableList()
    }
}
