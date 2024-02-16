package com.example.mobilnaaplikacija.did

data class Param(val name: String, val value: String?)

data class DID(
    var didString: String,
    var method: String,
    var id: String,
    val idStrings: MutableList<String>,
    var path: String,
    val pathSegments: MutableList<String>,
    var query: String,
    var fragment: String
)

class DIDParser(private val input: String) {
    private var currentIndex = 0
    private var out = DID("","", "", mutableListOf(), "", mutableListOf(), "", "")

    private fun checkLength(): DIDParser {
        val inputLength = input.length

        if (inputLength < 7) {
            throw IllegalArgumentException("Input length is less than 7")
        }

        parseScheme()
        return this
    }

    private fun parseScheme(): DIDParser {
        val currentIndex = 3

        if (input.substring(0, currentIndex + 1) != "did:") {
            throw IllegalArgumentException("Input does not begin with 'did:' prefix")
        }

        this.currentIndex = currentIndex
        parseMethod()
        return this
    }

    private fun parseMethod(): DIDParser {
        val inputLength = input.length
        var currentIndex = this.currentIndex + 1
        var startIndex = currentIndex

        while (currentIndex < inputLength) {
            val char = input[currentIndex]

            if (char == ':') {
                if (currentIndex == startIndex) {
                    throw IllegalArgumentException("Method is empty")
                }
                break
            }

            if (!char.isDigit() && !char.isLowerCase()) {
                throw IllegalArgumentException("Character is not a-z or 0-9")
            }

            currentIndex++
        }

        this.currentIndex = currentIndex
        out.method = input.substring(startIndex, currentIndex)
        parseID()
        return this
    }

    private fun parseID(): DIDParser {
        val inputLength = input.length
        var currentIndex = this.currentIndex + 1
        val idstartIndex = currentIndex
        var startIndex = currentIndex

        while (currentIndex < inputLength) {
            val char = input[currentIndex]

            if (char == ':') {
                if(currentIndex == startIndex){
                    throw IllegalArgumentException("ID is empty")
                }
                out.idStrings.add(input.substring(startIndex,currentIndex))
                startIndex = currentIndex + 1
                currentIndex += 1
                continue
            }

            if (char == '/') {
                this.currentIndex = currentIndex
                parsePath()
                break
            }

            if (char == '?') {
                this.currentIndex = currentIndex
                parseQuery()
                break
            }

            if (char == '#') {
                this.currentIndex = currentIndex
                parseFragment()
                break
            }

            if (!char.isLetterOrDigit() && char != '.' && char != '-') {
                throw IllegalArgumentException("Character is not a-z, 0-9, ., or -")
            }

            currentIndex++
        }

        if (currentIndex == startIndex) {
            throw IllegalArgumentException("ID string must be at least one character long")
        }
        out.idStrings.add(input.substring(startIndex,currentIndex))
        out.id = input.substring(idstartIndex,currentIndex)
        out.didString = input.substring(0,currentIndex)
        this.currentIndex = currentIndex

        return this
    }

    private fun parsePath(): DIDParser {
        val inputLength = input.length
        var currentIndex = this.currentIndex + 1
        val pstartIndex = currentIndex
        var startIndex = currentIndex

        while (currentIndex < inputLength) {
            val char = input[currentIndex]

            if (char == '?') {
                this.currentIndex = currentIndex
                parseQuery()
                break
            }

            if (char == '#') {
                this.currentIndex = currentIndex
                parseFragment()
                break
            }

            if (char == '/') {
                if (currentIndex == startIndex) {
                    throw IllegalArgumentException("Path segment must not be empty")
                }

                out.pathSegments.add(input.substring(startIndex, currentIndex))
                currentIndex++
                startIndex = currentIndex
                continue
            }

            if (!char.isLetterOrDigit() && char != '.' && char != '-' && char != '_' && char != ':' && char != '~' && char != '%' && char != '@' && char != '!' && char != '$' && char != '&' && char != '\'' && char != '(' && char != ')' && char != '*' && char != '+' && char != ',' && char != ';' && char != '=' && char != ':') {
                throw IllegalArgumentException("Character is not allowed in path segment - $char")
            }

            currentIndex++
        }

        if (currentIndex != startIndex) {
            out.pathSegments.add(input.substring(startIndex, currentIndex))
            out.path = input.substring(pstartIndex, currentIndex)
        }

        this.currentIndex = currentIndex
        return nullParser()
    }

    private fun parseQuery(): DIDParser {
        val inputLength = input.length
        var currentIndex = this.currentIndex + 1
        val startIndex = currentIndex

        while (currentIndex < inputLength) {
            val char = input[currentIndex]

            if (char == '#') {
                this.currentIndex = currentIndex
                parseFragment()
                break
            }

            if (!char.isLetterOrDigit() && char != '.' && char != '-' && char != '_' && char != ':' && char != '~' && char != '%' && char != '@' && char != '!' && char != '$' && char != '&' && char != '\'' && char != '(' && char != ')' && char != '*' && char != '+' && char != ',' && char != ';' && char != '=' && char != ':') {
                throw IllegalArgumentException("Character is not allowed in query - $char")
            }

            currentIndex++
        }

        if (currentIndex != startIndex) {
            out.query = input.substring(startIndex, currentIndex)
        }

        this.currentIndex = currentIndex
        return nullParser()
    }

    private fun parseFragment(): DIDParser {
        val inputLength = input.length
        var currentIndex = this.currentIndex + 1
        val startIndex = currentIndex

        while (currentIndex < inputLength) {
            val char = input[currentIndex]

            if (!char.isLetterOrDigit() && char != '.' && char != '-' && char != '_' && char != ':' && char != '~' && char != '%' && char != '@' && char != '!' && char != '$' && char != '&' && char != '\'' && char != '(' && char != ')' && char != '*' && char != '+' && char != ',' && char != ';' && char != '=' && char != ':') {
                throw IllegalArgumentException("Character is not allowed in fragment - $char")
            }

            currentIndex++
        }

        if (currentIndex != startIndex) {
            out.fragment = input.substring(startIndex, currentIndex)
        }

        this.currentIndex = currentIndex
        return nullParser()
    }

    private fun nullParser(): DIDParser {
        return this
    }

    fun parse(): DID {
        checkLength()

        return out
    }

}

fun main(){
      val did = DIDParser("did:ethipfs:0x0f7773CE819dFDF650a6b646E8d34aF63d5E40C4:ETF-Beograd-BU/b/h/a?id=10&test=true#key-one").parse()
        System.out.println(did.didString)
}