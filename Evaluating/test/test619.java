public class XMLParser extends XMLParserBase { private static final int [ ] elementEndSet = new int [ ] { XMLTokenTypes . GREATER_THAN , XMLTokenTypes . SLASH_GREATER_THAN } ; private Stack < IParseNode > _elementStack ; static { Arrays . sort ( elementEndSet ) ; } public XMLParser ( ) throws ParserInitializationException { this ( XMLMimeType . MimeType ) ; } public XMLParser ( String mimeType ) throws ParserInitializationException { super ( mimeType ) ; this . _elementStack = new Stack < IParseNode > ( ) ; } private void closeElement ( ) { if ( this . _currentElement != null ) { this . _currentElement . includeLexemeInRange ( this . currentLexeme ) ; } if ( this . _elementStack . size ( ) > 0 ) { this . _currentElement = this . _elementStack . pop ( ) ; } else { this . _currentElement = null ; } } private XMLParseNode createNode ( int type , Lexeme startingLexeme ) { return ( XMLParseNode ) this . getParseNodeFactory ( ) . createParseNode ( type , startingLexeme ) ; } private void openElement ( XMLElementNode element ) { if ( this . _currentElement != null ) { this . _currentElement . appendChild ( element ) ; } this . _elementStack . push ( this . _currentElement ) ; this . _currentElement = element ; } public synchronized void parseAll ( IParseNode parentNode ) throws LexerException { this . _elementStack . clear ( ) ; this . _currentElement = parentNode ; ILexer lexer = this . getLexer ( ) ; lexer . setLanguageAndGroup ( this . getLanguage ( ) , DEFAULT_GROUP ) ; try { this . parseText ( false ) ; } catch ( ParseException e ) { lexer . setGroup ( DEFAULT_GROUP ) ; } while ( this . isEOS ( ) == false ) { try { switch ( this . currentLexeme . typeIndex ) { case XMLTokenTypes . CDATA_START : this . parseCDATASection ( ) ; break ; case XMLTokenTypes . COMMENT : this . parseText ( false ) ; break ; case XMLTokenTypes . DOCTYPE_DECL : this . parseDocTypeDeclaration ( ) ; break ; case XMLTokenTypes . END_TAG : this . parseEndTag ( ) ; break ; case XMLTokenTypes . PI_OPEN : this . parsePI ( ) ; this . parseText ( false ) ; break ; case XMLTokenTypes . START_TAG : this . parseStartTag ( ) ; break ; case XMLTokenTypes . XML_DECL : this . parseXMLDeclaration ( ) ; break ; default : this . advance ( ) ; } } catch ( ParseException e ) { lexer . setGroup ( DEFAULT_GROUP ) ; } } } private void parseAttribute ( ) throws ParseException , LexerException { String name = this . currentLexeme . getText ( ) ; this . assertAndAdvance ( XMLTokenTypes . NAME , "error.attribute" ) ; this . assertAndAdvance ( XMLTokenTypes . EQUAL , "error.attribute.equal" ) ; this . assertType ( XMLTokenTypes . STRING , "error.attribute.value" ) ; if ( this . currentLexeme . getCategoryIndex ( ) != TokenCategories . ERROR ) { String value = this . currentLexeme . getText ( ) ; char firstChar = value . charAt ( 0 ) ; int quoteType = QuoteType . NONE ; if ( firstChar == '"' ) { value = value . substring ( 1 , value . length ( ) - 1 ) ; quoteType = QuoteType . DOUBLE_QUOTE ; } else if ( firstChar == '\'' ) { value = value . substring ( 1 , value . length ( ) - 1 ) ; quoteType = QuoteType . SINGLE_QUOTE ; } this . _currentElement . setAttribute ( name , value ) ; IParseNodeAttribute attr = this . _currentElement . getAttributeNode ( name ) ; attr . setQuoteType ( quoteType ) ; } this . advance ( ) ; } private void parseCDATASection ( ) throws LexerException , ParseException { ILexer lexer = this . getLexer ( ) ; lexer . setGroup ( CDATA_SECTION_GROUP ) ; this . assertAndAdvance ( XMLTokenTypes . CDATA_START , "error.cdata" ) ; this . assertAndAdvance ( XMLTokenTypes . CDATA_END , "error.cdata.close" ) ; } private void parseEndTag ( ) throws LexerException , ParseException { this . assertAndAdvance ( XMLTokenTypes . END_TAG , "error.tag.end" ) ; this . closeElement ( ) ; this . parseText ( true ) ; } private void parsePI ( ) throws LexerException , ParseException { ILexer lexer = this . getLexer ( ) ; lexer . setGroup ( PROCESSING_INSTRUCTION_GROUP ) ; this . assertAndAdvance ( XMLTokenTypes . PI_OPEN , "error.pi" ) ; this . assertAndAdvance ( XMLTokenTypes . CDATA_END , "error.pi.close" ) ; } private void parseStartTag ( ) throws ParseException , LexerException { this . assertType ( XMLTokenTypes . START_TAG , "error.tag.start" ) ; XMLElementNode element = ( XMLElementNode ) this . createNode ( XMLParseNodeTypes . ELEMENT , this . currentLexeme ) ; this . openElement ( element ) ; this . advance ( ) ; while ( this . isEOS ( ) == false && this . inSet ( elementEndSet ) == false ) { this . parseAttribute ( ) ; } switch ( this . currentLexeme . typeIndex ) { case XMLTokenTypes . GREATER_THAN : break ; case XMLTokenTypes . SLASH_GREATER_THAN : this . closeElement ( ) ; break ; default : throwParseError ( "error.tag.start.unclosed" ) ; } parseText ( false ) ; } private XMLDeclarationNode parseXMLDeclaration ( ) throws LexerException , ParseException { this . getLexer ( ) . setGroup ( XML_DECLARATION_GROUP ) ; XMLDeclarationNode decl = ( XMLDeclarationNode ) this . createNode ( XMLParseNodeTypes . DECLARATION , this . currentLexeme ) ; this . assertAndAdvance ( XMLTokenTypes . XML_DECL , "error.xml.declaration" ) ; decl . setVersion ( this . currentLexeme . getText ( ) ) ; if ( this . _currentElement instanceof ParseRootNode ) { this . _currentElement . appendChild ( decl ) ; } this . assertAndAdvance ( XMLTokenTypes . VERSION , "error.xml.declaration.version" ) ; if ( this . isType ( XMLTokenTypes . ENCODING ) ) { decl . setEncoding ( this . currentLexeme . getText ( ) ) ; this . advance ( ) ; } if ( this . isType ( XMLTokenTypes . STANDALONE ) ) { decl . setStandalone ( this . currentLexeme . getText ( ) ) ; this . advance ( ) ; } this . getLexer ( ) . setGroup ( DEFAULT_GROUP ) ; this . assertAndAdvance ( XMLTokenTypes . QUESTION_GREATER_THAN , "error.xml.declaration.close" ) ; return decl ; } } 