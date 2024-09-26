<<<<<<< HEAD
public class JSOffsetMapper extends ChildOffsetMapper implements IChildOffsetMapper { public static final String MODE_NORMAL = "__mode_normal" ; public static final String MODE_NEW = "__mode_new" ; public static final String MODE_INVOKING = "__mode_invoking" ; public static final String MODE_STRING = "__mode_string" ; public static final String NOT_AN_IDENTIFIER = "__not_an_identifier" ; public static final String NOT_INVOKING = "__not_invoking" ; private static IObject undef = ObjectBase . UNDEFINED ; private String mode = MODE_NORMAL ; private JSFileLanguageService fileLangService ; private LexemeBasedEnvironmentLoader loader ; public JSOffsetMapper ( IParentOffsetMapper parent ) { super ( parent ) ; } private static Environment getEnvironment ( ) { return ( Environment ) JSLanguageEnvironment . getInstance ( ) . getRuntimeEnvironment ( ) ; } public static Property lookupTypeFromNameHash ( String fullname , IScope scope , int offset , JSOffsetMapper jsfe ) { synchronized ( getEnvironment ( ) ) { if ( fullname . length ( ) == 0 ) { return null ; } Property result = null ; String [ ] names = fullname . split ( "\\." ) ; IObject obj = scope ; for ( int i = 0 ; i < names . length ; i ++ ) { String name = names [ i ] ; boolean isMethodCall = false ; if ( name . endsWith ( "()" ) ) { isMethodCall = true ; name = name . substring ( 0 , name . length ( ) - 2 ) ; } if ( i == 0 && names . length > 1 ) { obj = scope . getVariableValue ( name , jsfe . getFileIndex ( ) , offset ) . getInstance ( getEnvironment ( ) , jsfe . getFileIndex ( ) , offset ) ; } else if ( i < names . length - 1 && names . length > 1 ) { obj = obj . getPropertyValue ( name , jsfe . getFileIndex ( ) , offset ) . getInstance ( getEnvironment ( ) , jsfe . getFileIndex ( ) , offset ) ; } else { result = getPropertyInScope ( obj , name ) ; } if ( obj == undef ) { return null ; } if ( isMethodCall ) { IDocumentation doc = obj . getDocumentation ( ) ; if ( doc instanceof FunctionDocumentation ) { FunctionDocumentation fdoc = ( FunctionDocumentation ) doc ; String [ ] rettypes = fdoc . getReturn ( ) . getTypes ( ) ; if ( rettypes . length > 0 ) { String rettype = rettypes [ 0 ] ; obj = jsfe . lookupReturnTypeFromNameHash ( rettype , jsfe . getGlobal ( ) ) ; if ( obj != null ) { obj = obj . getPropertyValue ( "prototype" , jsfe . getFileIndex ( ) , offset ) ; } } } else { } } } return result ; } } public static String getIdentName ( int position , LexemeList lexemeList ) { String name = "" ; while ( position >= 0 ) { Lexeme curLexeme = lexemeList . get ( position ) ; switch ( curLexeme . typeIndex ) { case JSTokenTypes . IDENTIFIER : case JSTokenTypes . DOT : name = curLexeme . getText ( ) + name ; position -- ; break ; case JSTokenTypes . RPAREN : position = - 1 ; break ; default : position = - 1 ; break ; } } return name ; } public static Property getPropertyInScope ( IObject object , String propName ) { Property result = object . getProperty ( propName ) ; if ( result != null || ! ( object instanceof IScope ) ) { return result ; } IScope scope = ( ( IScope ) object ) . getParentScope ( ) ; while ( scope != null ) { result = scope . getProperty ( propName ) ; if ( result != null ) { break ; } scope = scope . getParentScope ( ) ; } return result ; } public String getMode ( int offset ) { getArgIndexAndCalculateMode ( ) ; return mode ; } public String getNameHash ( int lexemeIndex ) { String name = NOT_AN_IDENTIFIER ; int position = lexemeIndex ; int parenCount = 0 ; int bracketCount = 0 ; boolean wasSeparator = true ; int lastTokenType = - 1 ; while ( position >= 0 ) { Lexeme curLexeme = getLexemeList ( ) . get ( position ) ; if ( name . equals ( NOT_AN_IDENTIFIER ) ) { if ( curLexeme . typeIndex == JSTokenTypes . NEW ) { mode = MODE_NEW ; name = MODE_NEW ; break ; } else if ( curLexeme . typeIndex == JSTokenTypes . STRING ) { name = MODE_STRING ; } else { name = "" ; } } switch ( curLexeme . typeIndex ) { case JSTokenTypes . NEW : mode = MODE_NEW ; name = MODE_NEW + name ; wasSeparator = true ; position = 0 ; break ; case JSTokenTypes . LPAREN : wasSeparator = true ; if ( parenCount == 0 ) { if ( position > 0 ) { Lexeme prevLex = getLexemeList ( ) . get ( position - 1 ) ; if ( prevLex . getCategoryIndex ( ) == TokenCategories . KEYWORD && prevLex . typeIndex != JSTokenTypes . TYPEOF ) { return name ; } else { position = 0 ; break ; } } } parenCount -- ; name = curLexeme . getText ( ) + name ; break ; case JSTokenTypes . IDENTIFIER : if ( lastTokenType != JSTokenTypes . IDENTIFIER ) { name = curLexeme . getText ( ) + name ; wasSeparator = false ; } else { position = 0 ; } break ; case JSTokenTypes . DOT : wasSeparator = true ; name = curLexeme . getText ( ) + name ; break ; case JSTokenTypes . RPAREN : wasSeparator = true ; name = ")" + name ; int startParenCount = parenCount ; parenCount ++ ; while ( -- position > 0 ) { Lexeme lx = getLexemeList ( ) . get ( position ) ; if ( lx . typeIndex == JSTokenTypes . LPAREN ) { parenCount -- ; } else if ( lx . typeIndex == JSTokenTypes . RPAREN ) { parenCount ++ ; } if ( startParenCount == parenCount ) { name = "(" + name ; break ; } } break ; case JSTokenTypes . LBRACKET : wasSeparator = true ; if ( bracketCount == 0 && position > 0 ) { position = 0 ; break ; } bracketCount -- ; name = "[" + name ; break ; case JSTokenTypes . RBRACKET : wasSeparator = true ; name = "]" + name ; int startBracketCount = bracketCount ; bracketCount ++ ; while ( -- position > 0 ) { Lexeme lx = getLexemeList ( ) . get ( position ) ; if ( lx . typeIndex == JSTokenTypes . LBRACKET ) { bracketCount -- ; } else if ( lx . typeIndex == JSTokenTypes . RBRACKET ) { bracketCount ++ ; } if ( startBracketCount == bracketCount ) { name = "[" + name ; break ; } } break ; case JSTokenTypes . WHITESPACE : break ; case JSTokenTypes . SEMICOLON : position = 0 ; break ; default : if ( curLexeme . getCategoryIndex ( ) == TokenCategories . KEYWORD ) { if ( wasSeparator ) { name = curLexeme . getText ( ) + name ; } wasSeparator = false ; } if ( position > 0 ) { if ( getLexemeList ( ) . get ( position - 1 ) . typeIndex != JSTokenTypes . DOT ) { position = 0 ; } } else { position = 0 ; } break ; } if ( curLexeme . isAfterEOL ( ) ) { if ( ! name . startsWith ( "." ) ) break ; } lastTokenType = curLexeme . typeIndex ; position -- ; } if ( name . startsWith ( "." ) ) { name = NOT_AN_IDENTIFIER ; } return name ; } public String getArgAssistNameHash ( ) { if ( this . getCurrentLexeme ( ) == null ) { return NOT_INVOKING ; } String name = NOT_INVOKING ; int position = getCurrentLexemeIndex ( ) ; int parenCount = 0 ; boolean wasSeparator = true ; boolean foundSoloLParen = false ; try { int curOffset = this . getCurrentLexeme ( ) . offset + 1 ; String src = this . getFileService ( ) . getSource ( ) ; int startLine = curOffset ; if ( startLine >= src . length ( ) ) { startLine = src . length ( ) - 1 ; } for ( ; startLine > 0 ; startLine -- ) { if ( src . charAt ( startLine ) == '\n' ) { startLine ++ ; break ; } } if ( startLine < 0 || curOffset > src . length ( ) - 1 ) { return NOT_INVOKING ; } if ( startLine > curOffset ) { return NOT_INVOKING ; } char [ ] lineChars = src . substring ( startLine , curOffset ) . toCharArray ( ) ; int left = 0 ; int right = 0 ; for ( int i = 0 ; i < lineChars . length ; i ++ ) { if ( lineChars [ i ] == '(' ) { left ++ ; } else if ( lineChars [ i ] == ')' ) { right ++ ; } } if ( left <= right ) { return NOT_INVOKING ; } } catch ( Exception e ) { return NOT_INVOKING ; } while ( position >= 0 ) { Lexeme curLexeme = getLexemeList ( ) . get ( position ) ; if ( name . equals ( NOT_INVOKING ) ) { name = "" ; } switch ( curLexeme . typeIndex ) { case JSTokenTypes . LPAREN : wasSeparator = true ; if ( parenCount == 0 ) { foundSoloLParen = true ; if ( position > 0 ) { Lexeme prevLex = getLexemeList ( ) . get ( position - 1 ) ; if ( prevLex . getCategoryIndex ( ) == TokenCategories . KEYWORD && prevLex . typeIndex != JSTokenTypes . TYPEOF ) { return NOT_INVOKING ; } } } else if ( parenCount < 0 ) { position = 0 ; break ; } name = curLexeme . getText ( ) + name ; parenCount -- ; break ; case JSTokenTypes . IDENTIFIER : if ( foundSoloLParen ) { name = curLexeme . getText ( ) + name ; wasSeparator = false ; } break ; case JSTokenTypes . COMMA : if ( foundSoloLParen ) { position = 0 ; } else { wasSeparator = true ; name = curLexeme . getText ( ) + name ; } break ; case JSTokenTypes . DOT : if ( foundSoloLParen ) { wasSeparator = true ; name = curLexeme . getText ( ) + name ; } break ; case JSTokenTypes . RPAREN : if ( foundSoloLParen ) { wasSeparator = true ; name = ")" + name ; } int startParenCount = parenCount ; parenCount ++ ; while ( -- position > 0 ) { Lexeme lx = getLexemeList ( ) . get ( position ) ; if ( lx . typeIndex == JSTokenTypes . LPAREN ) { parenCount -- ; } else if ( lx . typeIndex == JSTokenTypes . RPAREN ) { parenCount ++ ; } if ( startParenCount == parenCount ) { if ( foundSoloLParen ) { name = "(" + name ; } break ; } } break ; case JSTokenTypes . WHITESPACE : break ; default : if ( curLexeme . getCategoryIndex ( ) == TokenCategories . KEYWORD ) { if ( wasSeparator && foundSoloLParen ) { name = curLexeme . getText ( ) + name ; wasSeparator = false ; } } else if ( curLexeme . getCategoryIndex ( ) == TokenCategories . LITERAL ) { break ; } position = 0 ; break ; } if ( curLexeme . isAfterEOL ( ) ) { break ; } position -- ; } if ( name . startsWith ( "." ) ) { name = NOT_INVOKING ; } return name ; } public IObject lookupReturnTypeFromNameHash ( String fullname , IScope scope ) { return lookupReturnTypeFromNameHash ( fullname , scope , false ) ; } public IObject lookupReturnTypeFromNameHash ( String fullname , IScope scope , boolean searchForward ) { synchronized ( this . getFileService ( ) ) { if ( fullname . length ( ) == 0 ) { return null ; } String [ ] names = fullname . split ( "\\." ) ; IObject obj = scope ; int offset = 0 ; if ( this . getCurrentLexeme ( ) != null ) { offset = this . getCurrentLexeme ( ) . offset ; } int fileIndex = this . getFileIndex ( ) ; for ( int i = 0 ; i < names . length ; i ++ ) { String name = names [ i ] ; boolean isMethodCall = false ; if ( name . endsWith ( "()" ) ) { isMethodCall = true ; name = name . substring ( 0 , name . length ( ) - 2 ) ; } boolean isArrayCall = false ; if ( name . endsWith ( "[]" ) ) { isArrayCall = true ; name = name . substring ( 0 , name . length ( ) - 2 ) ; } if ( i == 0 ) { if ( name . equals ( "this" ) && obj instanceof IScope ) { boolean hasDocReturn = false ; IFunction enclFn = ( ( IScope ) obj ) . getEnclosingFunction ( ) ; if ( enclFn != null && enclFn instanceof JSFunction ) { JSFunction fn = ( JSFunction ) enclFn ; IDocumentation doc = fn . getDocumentation ( ) ; if ( doc instanceof PropertyDocumentation ) { PropertyDocumentation pdoc = ( PropertyDocumentation ) doc ; if ( pdoc instanceof FunctionDocumentation && ( ( FunctionDocumentation ) pdoc ) . getIsConstructor ( ) ) { obj = fn . getPropertyValue ( "prototype" , fileIndex , offset ) ; } else { String rettype = fn . getMemberOf ( ) ; if ( rettype != null && ! rettype . equals ( "" ) ) { hasDocReturn = true ; if ( rettype . indexOf ( "." ) > - 1 ) { obj = lookupNamespaceFromNameHash ( rettype ) ; } else { obj = lookupReturnTypeFromNameHash ( rettype , getGlobal ( ) ) ; } if ( obj != null ) { obj = obj . getPropertyValue ( "prototype" , fileIndex , offset ) ; } } } } if ( ! hasDocReturn ) { obj = fn . getGuessedMemberObject ( ) ; } } } else { obj = scope . getVariableValue ( name , fileIndex , offset ) . getInstance ( getEnvironment ( ) , fileIndex , offset ) ; if ( ( obj == null || obj == ObjectBase . UNDEFINED ) && searchForward ) { obj = scope . getVariableValue ( name , Integer . MAX_VALUE , Integer . MAX_VALUE ) . getInstance ( getEnvironment ( ) , Integer . MAX_VALUE , Integer . MAX_VALUE ) ; } } } else { IObject temp = obj . getPropertyValue ( name , fileIndex , offset ) . getInstance ( getEnvironment ( ) , fileIndex , offset ) ; if ( ( temp == null || temp == ObjectBase . UNDEFINED ) && searchForward ) { temp = obj . getPropertyValue ( name , Integer . MAX_VALUE , Integer . MAX_VALUE ) . getInstance ( getEnvironment ( ) , Integer . MAX_VALUE , Integer . MAX_VALUE ) ; } obj = temp ; } if ( obj == null || obj == ObjectBase . UNDEFINED ) { return null ; } boolean isFunction = obj instanceof IFunction ; if ( isArrayCall || isMethodCall || ( ! isFunction && i == names . length - 1 && fullname . endsWith ( "." ) ) ) { IDocumentation doc = obj . getDocumentation ( ) ; if ( doc instanceof PropertyDocumentation ) { PropertyDocumentation pdoc = ( PropertyDocumentation ) doc ; String [ ] rettypes = pdoc . getReturn ( ) . getTypes ( ) ; if ( rettypes . length > 0 ) { String rettype = rettypes [ 0 ] ; if ( isArrayCall && rettypes . length > 1 ) { rettype = rettypes [ 1 ] ; } if ( rettype . indexOf ( "." ) > - 1 ) { obj = lookupNamespaceFromNameHash ( rettype ) ; } else { obj = lookupReturnTypeFromNameHash ( rettype , getGlobal ( ) ) ; } if ( obj != null && ! name . equals ( "Math" ) ) { obj = obj . getPropertyValue ( "prototype" , fileIndex , offset ) ; } } } else { if ( obj instanceof JSFunction ) { JSFunction fnObj = ( JSFunction ) obj ; obj = fnObj . invoke ( getEnvironment ( ) , new IObject [ 0 ] , fileIndex , fnObj . getRange ( ) ) ; } } } } return obj ; } } private IObject lookupNamespaceFromNameHash ( String fullname ) { if ( fullname . length ( ) == 0 ) { return null ; } String [ ] names = fullname . split ( "\\." ) ; IScope scope = getGlobal ( ) ; IObject obj = scope ; int offset = this . getCurrentLexeme ( ) . offset ; int fileIndex = this . getFileIndex ( ) ; for ( int i = 0 ; i < names . length ; i ++ ) { String name = names [ i ] ; if ( i == 0 ) { obj = scope . getVariableValue ( name , fileIndex , offset ) . getInstance ( getEnvironment ( ) , fileIndex , offset ) ; } else { obj = obj . getPropertyValue ( name , fileIndex , offset ) . getInstance ( getEnvironment ( ) , fileIndex , offset ) ; } if ( obj == ObjectBase . UNDEFINED ) { return null ; } } return obj ; } public int getArgIndexAndCalculateMode ( ) { int commaCount = 0 ; mode = MODE_NORMAL ; int pos = getCurrentLexemeIndex ( ) ; int parenCount = 0 ; boolean wasNewline = false ; while ( pos >= 0 && pos < getLexemeList ( ) . size ( ) ) { Lexeme curLexeme = getLexemeList ( ) . get ( pos ) ; if ( wasNewline ) { break ; } if ( curLexeme . isAfterEOL ( ) ) { wasNewline = true ; } switch ( curLexeme . typeIndex ) { case JSTokenTypes . COMMA : if ( parenCount == 0 ) { commaCount ++ ; } break ; case JSTokenTypes . RPAREN : parenCount ++ ; break ; case JSTokenTypes . DOT : case JSTokenTypes . WHITESPACE : break ; case JSTokenTypes . SEMICOLON : mode = MODE_NORMAL ; pos = - 1 ; break ; case JSTokenTypes . LPAREN : parenCount -- ; if ( parenCount < 0 ) { mode = MODE_INVOKING ; pos = - 1 ; } break ; default : break ; } pos -- ; } return commaCount ; } public int getFileIndex ( ) { return this . getParseState ( ) . getFileIndex ( ) ; } public JSScope getGlobal ( ) { return getEnvironment ( ) . getGlobal ( ) ; } public IScope getScope ( Lexeme lex , IScope defaultScope ) { return this . getEnvironmentLoader ( ) . getScope ( lex . offset , defaultScope ) ; } private LexemeBasedEnvironmentLoader getEnvironmentLoader ( ) { if ( loader == null ) { loader = this . getFileLanguageService ( ) . getEnvironmentLoader ( ) ; } return loader ; } private JSFileLanguageService getFileLanguageService ( ) { if ( fileLangService == null ) { fileLangService = JSFileLanguageService . getJSFileLanguageService ( getFileService ( ) ) ; } return fileLangService ; } public IParseState getParseState ( ) { return this . getFileLanguageService ( ) . getParseState ( ) ; } public ICodeLocation findTarget ( Lexeme lexeme ) { String fullName = this . getNameHash ( getLexemeList ( ) . getLexemeIndex ( lexeme ) ) ; if ( fullName . indexOf ( '(' ) > - 1 ) { fullName = fullName . substring ( 0 , fullName . lastIndexOf ( '(' ) ) ; } IScope scope = this . getScope ( lexeme , this . getGlobal ( ) ) ; if ( fullName . startsWith ( JSOffsetMapper . MODE_NEW ) ) { fullName = fullName . substring ( JSOffsetMapper . MODE_NEW . length ( ) ) ; } IObject object = this . lookupReturnTypeFromNameHash ( fullName , scope , true ) ; Property prop = JSOffsetMapper . lookupTypeFromNameHash ( fullName , scope , lexeme . getEndingOffset ( ) , this ) ; ICodeLocation loc = findTargetFromName ( object , prop ) ; return loc ; } public static ICodeLocation findTargetFromName ( IObject object , Property prop ) { if ( object == null || prop == null ) { return null ; } int offset = object . getStartingOffset ( ) ; if ( offset < 0 ) { offset = Integer . MAX_VALUE + object . getStartingOffset ( ) ; } OrderedObjectCollection assignments = prop . getAssignments ( ) ; if ( assignments == null || assignments . size ( ) == 0 ) return null ; CodeLocation loc = null ; for ( int i = 0 ; i < assignments . size ( ) ; i ++ ) { OrderedObject orderedObject = assignments . get ( i ) ; if ( orderedObject == null || orderedObject . object != object ) continue ; IWorkbenchWindow window = JSPlugin . getDefault ( ) . getWorkbench ( ) . getActiveWorkbenchWindow ( ) ; IWorkbench workbench = window . getWorkbench ( ) ; IWorkbenchPage page = workbench . getActiveWorkbenchWindow ( ) . getActivePage ( ) ; IEditorReference [ ] editorReferences = page . getEditorReferences ( ) ; for ( int j = 0 ; j < editorReferences . length ; j ++ ) { IEditorPart editor = editorReferences [ j ] . getEditor ( true ) ; IEditorSite site = editor . getEditorSite ( ) ; IWorkbenchPart part = site . getPart ( ) ; if ( part instanceof IUnifiedEditor ) { IUnifiedEditor ue = ( IUnifiedEditor ) part ; IFileService context = ue . getFileContext ( ) ; int fi = context . getParseState ( ) . getFileIndex ( ) ; if ( orderedObject . fileIndex == fi ) { IEditorInput input = editor . getEditorInput ( ) ; String path = CoreUIUtils . getPathFromEditorInput ( input ) ; Lexeme lx = context . getLexemeList ( ) . getCeilingLexeme ( offset ) ; if ( lx != null ) { loc = new CodeLocation ( path , lx ) ; return loc ; } else { loc = new CodeLocation ( path , lx ) ; } } } } if ( loc != null ) { break ; } int fileIndex = orderedObject . fileIndex ; String path = FileContextManager . getURIFromFileIndex ( fileIndex ) ; FileService context = FileContextManager . get ( path ) ; if ( context == null ) { break ; } LexemeList ll = context . getLexemeList ( ) ; if ( ll == null ) { break ; } Lexeme lx = ll . getCeilingLexeme ( offset ) ; loc = new CodeLocation ( path , lx ) ; break ; } return loc ; } } 
=======
public final class DatetimeLocal extends AbstractDatetime { public static final DatetimeLocal THE_INSTANCE = new DatetimeLocal ( ) ; private static final Pattern THE_PATTERN = Pattern . compile ( "^([0-9]{4,})-([0-9]{2})-([0-9]{2})[T ]([0-9]{2}):([0-9]{2})(?::([0-9]{2})(?:\\.[0-9]{1,3})?)?$" ) ; private DatetimeLocal ( ) { super ( ) ; } protected final Pattern getPattern ( ) { return THE_PATTERN ; } @ Override public String getName ( ) { return "local datetime" ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
