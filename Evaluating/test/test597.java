public class JSLexemeUtils { private LexemeList lexemeList ; private Lexeme currentLexeme ; private int currentLexemeIndex ; public JSLexemeUtils ( LexemeList lexemeList ) { this . lexemeList = lexemeList ; } public LexemeList getLexemeList ( ) { return lexemeList ; } public Lexeme getCurrentLexeme ( ) { return currentLexeme ; } public int getCurrentLexemeIndex ( ) { return currentLexemeIndex ; } public int getLexemeIndexFromDocumentOffset ( int offset ) { if ( offset < 0 || lexemeList . size ( ) == 0 ) { return - 1 ; } int index = lexemeList . getLexemeIndex ( offset - 1 ) ; if ( index < 1 ) { if ( index < - 1 ) { index = - index - 2 ; } else { index = 0 ; } } return index ; } public int getLexemeFloorIndex ( int offset ) { return lexemeList . getLexemeFloorIndex ( offset ) ; } public int getLexemeCeilingIndex ( int offset ) { return lexemeList . getLexemeCeilingIndex ( offset ) ; } public Lexeme getLexemeFromDocumentOffset ( int offset ) { int index = getLexemeIndexFromDocumentOffset ( offset ) ; if ( index > - 1 ) { return lexemeList . get ( index ) ; } else { return null ; } } public void calculateCurrentLexeme ( int offset ) { currentLexemeIndex = getLexemeIndexFromDocumentOffset ( offset ) ; if ( currentLexemeIndex > - 1 ) { currentLexeme = lexemeList . get ( currentLexemeIndex ) ; } else { currentLexeme = null ; } } public Lexeme getNextIdentifier ( int index ) { for ( int i = index ; i < lexemeList . size ( ) ; i ++ ) { Lexeme lexeme = lexemeList . get ( i ) ; if ( lexeme . typeIndex != TokenCategories . WHITESPACE && lexeme . typeIndex != JSTokenTypes . IDENTIFIER ) { return null ; } if ( lexeme . typeIndex == JSTokenTypes . IDENTIFIER ) { return lexeme ; } } return null ; } public Lexeme getPreviousIdentifier ( int index ) { for ( int i = index ; i >= 0 ; i -- ) { Lexeme lexeme = lexemeList . get ( i ) ; if ( lexeme . typeIndex != TokenCategories . WHITESPACE && lexeme . typeIndex != JSTokenTypes . IDENTIFIER ) { return null ; } if ( lexeme . typeIndex == JSTokenTypes . IDENTIFIER ) { return lexeme ; } } return null ; } public String getNextTypeIdentifier ( int startIndex ) { String name = "" ; int index = startIndex ; int size = lexemeList . size ( ) ; Lexeme lexeme = lexemeList . get ( index ) ; while ( index < size && ( lexeme . typeIndex == JSTokenTypes . IDENTIFIER || lexeme . typeIndex == JSTokenTypes . DOT ) ) { name += lexeme . getText ( ) ; index ++ ; if ( index < size ) { lexeme = lexemeList . get ( index ) ; } } return name . equals ( "" ) ? null : name ; } public String getTypeAfterEqualNew ( int index ) { int equalsIndex = findNextTokenType ( index , JSTokenTypes . EQUAL ) ; if ( equalsIndex == - 1 ) { return null ; } int newIndex = findNextTokenType ( equalsIndex + 1 , JSTokenTypes . NEW ) ; if ( newIndex == - 1 ) { return null ; } int identifierIndex = findNextTokenType ( equalsIndex + 1 , JSTokenTypes . IDENTIFIER ) ; if ( identifierIndex == - 1 ) { return null ; } return getNextTypeIdentifier ( identifierIndex ) ; } public int getIndexAfterTypeIdentifier ( int index ) { index ++ ; int equalsIndex = findNextTokenType ( index , JSTokenTypes . EQUAL ) ; if ( equalsIndex == - 1 ) { return - 1 ; } int identifierIndex = findNextTokenType ( equalsIndex + 1 , JSTokenTypes . IDENTIFIER ) ; return identifierIndex ; } public int findNextTokenType ( int startIndex , int typeIndex ) { int index = startIndex ; int size = lexemeList . size ( ) ; if ( index >= size ) { return - 1 ; } Lexeme lexeme = lexemeList . get ( index ) ; while ( index < size || lexeme . getCategoryIndex ( ) == TokenCategories . WHITESPACE ) { if ( lexeme . typeIndex == typeIndex ) { return index ; } index ++ ; if ( index < size ) { lexeme = lexemeList . get ( index ) ; } else { break ; } } return - 1 ; } public int isNextTokenType ( int startIndex , int typeIndex ) { int index = startIndex ; int size = lexemeList . size ( ) ; if ( index >= size || index < 0 ) { return - 1 ; } Lexeme lexeme = lexemeList . get ( index ) ; while ( index < size && lexeme . getCategoryIndex ( ) == TokenCategories . WHITESPACE ) { index ++ ; if ( index < size ) { lexeme = lexemeList . get ( index ) ; } else { break ; } } if ( lexeme . typeIndex == typeIndex ) { return lexeme . offset ; } else { return - 1 ; } } public int isPrevTokenType ( int startIndex , int typeIndex ) { int index = startIndex ; int size = lexemeList . size ( ) ; if ( index >= size || index < 0 ) { return - 1 ; } Lexeme lexeme = lexemeList . get ( index ) ; while ( index <= 0 && lexeme . getCategoryIndex ( ) == TokenCategories . WHITESPACE ) { index -- ; if ( index >= 0 ) { lexeme = lexemeList . get ( index ) ; } else { break ; } } if ( lexeme . typeIndex == typeIndex ) { return lexeme . offset ; } else { return - 1 ; } } public JSFunctionInfo getFunctionInfo ( int currentIndex ) { return getFunctionInfo ( currentIndex , false ) ; } private JSFunctionInfo getFunctionInfo ( int currentIndex , boolean recurse ) { for ( int i = currentIndex ; i < lexemeList . size ( ) ; i ++ ) { Lexeme lexeme = lexemeList . get ( i ) ; if ( lexeme . typeIndex == JSTokenTypes . IDENTIFIER ) { String params = findParameters ( i ) ; String parent = null ; if ( ! recurse ) { parent = findParentFunction ( i ) ; } if ( parent == null ) { parent = "" ; } else { parent = parent + "." ; } JSFunctionInfo fi = new JSFunctionInfo ( parent + lexeme . getText ( ) , lexeme . offset , params ) ; fi . nameOffset = lexeme . offset ; return fi ; } if ( lexeme . typeIndex == JSTokenTypes . LPAREN ) { String params = findParameters ( i - 1 ) ; int listIdx = currentIndex - 1 ; for ( ; listIdx > 0 ; -- listIdx ) { Lexeme lx1 = lexemeList . get ( listIdx ) ; if ( lx1 . typeIndex == JSTokenTypes . EQUAL ) { String parent = null ; if ( ! recurse ) { parent = findParentFunction ( listIdx ) ; } JSFunctionInfo fi = findIdentifierBeforeEqual ( listIdx ) ; if ( fi != null ) { if ( parent != null ) { fi . name = parent + "." + fi . name ; } fi . params = params ; return fi ; } else { return null ; } } else if ( lx1 . typeIndex == JSTokenTypes . COLON ) { for ( ; listIdx > 0 ; -- listIdx ) { Lexeme lx2 = lexemeList . get ( listIdx ) ; if ( lx2 . typeIndex == JSTokenTypes . STRING || lx2 . typeIndex == JSTokenTypes . IDENTIFIER ) { String name = lx2 . getText ( ) ; if ( lx2 . typeIndex == JSTokenTypes . STRING ) { name = name . substring ( 1 , name . length ( ) - 1 ) ; } String parentName = null ; if ( ! recurse ) { parentName = findParentFunction ( listIdx , 1 ) ; } if ( parentName != null ) { JSFunctionInfo fi = new JSFunctionInfo ( parentName + "." + name , lx2 . offset , params ) ; fi . nameOffset = lx2 . offset ; return fi ; } else { JSFunctionInfo fi = new JSFunctionInfo ( name , lx2 . offset , params ) ; fi . nameOffset = lx2 . offset ; return fi ; } } } } } } } return null ; } public int findDocOffset ( int currentIndex ) { if ( currentIndex > 0 ) { Lexeme lx = lexemeList . get ( -- currentIndex ) ; if ( lx . typeIndex == JSTokenTypes . DOCUMENTATION ) { return lx . offset ; } } return - 1 ; } private String findParameters ( int index ) { String params = "" ; boolean found = false ; for ( int i = index ; i < lexemeList . size ( ) ; i ++ ) { Lexeme lexeme = lexemeList . get ( i ) ; if ( lexeme . typeIndex == JSTokenTypes . LPAREN ) { while ( lexeme . typeIndex != JSTokenTypes . RPAREN && i < lexemeList . size ( ) ) { lexeme = lexemeList . get ( i ++ ) ; params += lexeme . getText ( ) . trim ( ) ; found = true ; } if ( found ) { break ; } } } if ( ! found || params . trim ( ) . length ( ) == 0 ) { return "()" ; } else { return params ; } } private String findParentFunction ( int currentIndex ) { return findParentFunction ( currentIndex , 0 ) ; } private String findParentFunction ( int currentIndex , int startDepth ) { int level = startDepth ; while ( currentIndex > 0 ) { Lexeme lx = lexemeList . get ( -- currentIndex ) ; if ( lx . getLanguage ( ) . equals ( JSMimeType . MimeType ) == false ) { continue ; } if ( lx . typeIndex == JSTokenTypes . RCURLY ) { level ++ ; } else if ( lx . typeIndex == JSTokenTypes . LCURLY ) { level -- ; } if ( level < startDepth ) { for ( ; currentIndex > 0 ; -- currentIndex ) { Lexeme lx1 = lexemeList . get ( currentIndex ) ; if ( lx . getLanguage ( ) . equals ( JSMimeType . MimeType ) == false ) { continue ; } if ( lx1 . typeIndex == JSTokenTypes . EQUAL ) { String id = findIdentifierBeforeEqual ( currentIndex ) . name ; String parent = findParentFunction ( currentIndex ) ; if ( parent != null ) { return parent + "." + id ; } else { return id ; } } else if ( lx1 . typeIndex == JSTokenTypes . FUNCTION ) { String name = getFunctionInfo ( currentIndex , true ) . name ; String parent = findParentFunction ( currentIndex ) ; if ( parent != null ) { return parent + "." + name ; } else { return name ; } } } } } return null ; } public JSFunctionInfo findIdentifierBeforeEqual ( int currentIndex ) { while ( currentIndex >= 0 ) { Lexeme lx2 = lexemeList . get ( currentIndex -- ) ; if ( lx2 . typeIndex == JSTokenTypes . IDENTIFIER ) { String name = "" ; int offset = - 1 ; while ( lx2 . typeIndex == JSTokenTypes . IDENTIFIER || lx2 . typeIndex == JSTokenTypes . DOT || lx2 . typeIndex == JSTokenTypes . THIS ) { if ( lx2 . typeIndex == JSTokenTypes . THIS ) { name = name . substring ( 1 ) ; } else { name = lx2 . getText ( ) + name ; } offset = lx2 . offset ; int index = currentIndex -- ; if ( lx2 . isAfterEOL ( ) ) { break ; } if ( index >= 0 ) { lx2 = lexemeList . get ( index ) ; } else { break ; } } JSFunctionInfo fi = new JSFunctionInfo ( name , offset ) ; fi . nameOffset = offset ; return fi ; } } return null ; } } 