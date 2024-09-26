<<<<<<< HEAD
public class HTMLFileLanguageService extends BaseFileLanguageService { private HTMLOffsetMapper offsetMapper ; private IMetadataEnvironment environment ; HTMLEnvironmentLoader loader ; private boolean isProfileMember ; private IFileContextListener _delayedFileListener ; private static boolean isFirstConnection = true ; public HTMLFileLanguageService ( FileService fileService , IParseState parseState , IParser parser , IParentOffsetMapper mapper ) { super ( fileService , parseState , parser , mapper ) ; createOffsetMapper ( mapper ) ; this . environment = ( IMetadataEnvironment ) HTMLLanguageEnvironment . getInstance ( ) . getRuntimeEnvironment ( ) ; loader = new HTMLEnvironmentLoader ( ( IRuntimeEnvironment ) this . environment ) ; final IParseState finalParseState = parseState ; _delayedFileListener = new IFileContextListener ( ) { public void onContentChanged ( FileContextContentEvent evt ) { IFileService context = getFileContext ( ) ; if ( context != null ) { loader . reloadEnvironment ( finalParseState , context . getLexemeList ( ) , finalParseState . getFileIndex ( ) ) ; } else { throw new IllegalStateException ( Messages . HTMLFileLanguageService_IFileContextShouldNotBeNull ) ; } } } ; this . fileService . addDelayedFileListener ( _delayedFileListener ) ; } public String getDocumentationTitleFromLexeme ( Lexeme lexeme ) { String title = "HTML Editor" ; if ( lexeme == null ) { return "HTML Editor" ; } else { String titleLower = lexeme . getText ( ) . toLowerCase ( ) ; if ( lexeme . typeIndex == HTMLTokenTypes . START_TAG ) { title = "'" + titleLower + ">' tag" ; } else if ( lexeme . typeIndex == HTMLTokenTypes . END_TAG ) { title = titleLower . replaceAll ( "</" , "" ) ; title = "'<" + title + ">' tag" ; } else if ( lexeme . typeIndex == HTMLTokenTypes . NAME ) { FieldMetadata el = ( FieldMetadata ) environment . getGlobalFields ( ) . get ( titleLower ) ; if ( el != null ) { title = "'" + titleLower + "' attribute" ; } else { EventMetadata fm = ( EventMetadata ) environment . getGlobalEvents ( ) . get ( titleLower ) ; if ( fm != null ) { title = "'" + titleLower + "' event" ; } } } } return title ; } public HelpResource [ ] getDocumentationResourcesFromLexeme ( Lexeme lexeme ) { if ( lexeme == null ) { return new HelpResource [ 0 ] ; } else { ArrayList topics = new ArrayList ( ) ; ArrayList generalTopics = new ArrayList ( ) ; String lowerName = lexeme . getText ( ) . toLowerCase ( ) ; if ( lexeme . typeIndex == HTMLTokenTypes . START_TAG ) { addStartTagHelpTopics ( topics , generalTopics , lowerName ) ; } else if ( lexeme . typeIndex == HTMLTokenTypes . END_TAG ) { addEndTagHelpTopics ( topics , generalTopics , lowerName ) ; } else if ( lexeme . typeIndex == HTMLTokenTypes . NAME ) { addAttributeHelpTopics ( topics , generalTopics , lowerName ) ; addEventHelpTopics ( topics , generalTopics , lowerName ) ; IFileService context = getFileContext ( ) ; Lexeme openTag = HTMLUtils . getTagOpenLexeme ( lexeme . offset , context . getLexemeList ( ) ) ; if ( openTag != null ) { String lowerTagName = openTag . getText ( ) . toLowerCase ( ) ; addStartTagHelpTopics ( topics , generalTopics , lowerTagName ) ; } } if ( generalTopics . size ( ) == 0 ) { HelpResource index = new HelpResource ( "HTML Reference" , "/com.aptana.ide.documentation/html/reference/api/HTML.index.html" ) ; generalTopics . add ( index ) ; } for ( Iterator iter = generalTopics . iterator ( ) ; iter . hasNext ( ) ; ) { topics . add ( iter . next ( ) ) ; } return ( HelpResource [ ] ) topics . toArray ( new HelpResource [ 0 ] ) ; } } private void addAttributeHelpTopics ( ArrayList topics , ArrayList generalTopics , String lowerName ) { FieldMetadata el = ( FieldMetadata ) environment . getGlobalFields ( ) . get ( lowerName ) ; String anchor = "" ; if ( el != null ) { anchor = el . getName ( ) ; String url = "/com.aptana.ide.documentation/html/reference/api/HTML.field." + el . getName ( ) + ".html" ; HelpResource hr = new HelpResource ( "'" + el . getName ( ) + "' Attribute" , url ) ; topics . add ( hr ) ; HelpResource index = new HelpResource ( "HTML Attribute Reference" , "/com.aptana.ide.documentation/html/reference/api/HTML.index-fields.html#" + anchor ) ; generalTopics . add ( index ) ; } } private void addEventHelpTopics ( ArrayList topics , ArrayList generalTopics , String lowerName ) { EventMetadata fm = ( EventMetadata ) environment . getGlobalEvents ( ) . get ( lowerName ) ; String anchor = "" ; if ( fm != null ) { anchor = fm . getName ( ) ; String url = "/com.aptana.ide.documentation/html/reference/api/HTML.event." + fm . getName ( ) + ".html" ; HelpResource hr = new HelpResource ( "'" + fm . getName ( ) + "' Event" , url ) ; topics . add ( hr ) ; HelpResource index = new HelpResource ( "HTML Event Reference" , "/com.aptana.ide.documentation/html/reference/api/HTML.index-events.html#" + anchor ) ; generalTopics . add ( index ) ; } } private void addStartTagHelpTopics ( ArrayList topics , ArrayList generalTopics , String lowerName ) { lowerName = lowerName . replaceAll ( "<" , "" ) ; ElementMetadata el = environment . getElement ( lowerName ) ; String anchor = "" ; if ( el != null ) { anchor = el . getFullName ( ) ; String url = "/com.aptana.ide.documentation/html/reference/api/HTML.element." + el . getFullName ( ) + ".html" ; HelpResource hr = new HelpResource ( "<" + lowerName + "> Element" , url ) ; topics . add ( hr ) ; } HelpResource index = new HelpResource ( "HTML Element Reference" , "/com.aptana.ide.documentation/html/reference/api/HTML.index-elements.html#" + anchor ) ; generalTopics . add ( index ) ; } private void addEndTagHelpTopics ( ArrayList topics , ArrayList generalTopics , String lowerName ) { lowerName = lowerName . replaceAll ( "</" , "" ) ; ElementMetadata el = environment . getElement ( lowerName ) ; String anchor = "" ; if ( el != null ) { anchor = el . getFullName ( ) ; String url = "/com.aptana.ide.documentation/html/reference/api/HTML.element." + el . getFullName ( ) + ".html" ; HelpResource hr = new HelpResource ( "<" + lowerName + "> Element" , url ) ; topics . add ( hr ) ; } HelpResource index = new HelpResource ( "HTML Element Reference" , "/com.aptana.ide.documentation/html/reference/api/HTML.index-elements.html#" + anchor ) ; generalTopics . add ( index ) ; } public String getDocumentationFromLexeme ( Lexeme lexeme ) { if ( lexeme == null ) { return StringUtils . EMPTY ; } else { String docs = StringUtils . EMPTY ; String lowerName = lexeme . getText ( ) . toLowerCase ( ) ; if ( lexeme . typeIndex == HTMLTokenTypes . START_TAG ) { lowerName = lowerName . replaceAll ( "<" , "" ) ; ElementMetadata el = environment . getElement ( lowerName ) ; if ( el != null ) { docs += StringUtils . format ( Messages . HTMLFileLanguageService_InformationAvailableHTML , new String [ ] { el . getName ( ) , el . getDescription ( ) } ) ; } else { docs += StringUtils . format ( Messages . HTMLFileLanguageService_NoInformationAvailableHTML , lexeme . getType ( ) ) ; } } else if ( lexeme . typeIndex == HTMLTokenTypes . END_TAG ) { lowerName = lowerName . replaceAll ( "</" , "" ) ; ElementMetadata el = environment . getElement ( lowerName ) ; if ( el != null ) { docs += StringUtils . format ( Messages . HTMLFileLanguageService_InformationAvailableEndTagHTML , new String [ ] { el . getName ( ) , el . getDescription ( ) } ) ; } else { docs += StringUtils . format ( Messages . HTMLFileLanguageService_NoInformationAvailableHTML , lexeme . getType ( ) ) ; } } else if ( lexeme . typeIndex == HTMLTokenTypes . NAME ) { FieldMetadata el = ( FieldMetadata ) environment . getGlobalFields ( ) . get ( lowerName ) ; if ( el != null ) { docs += StringUtils . format ( Messages . HTMLFileLanguageService_InformationAvailableHTML , new String [ ] { el . getName ( ) , el . getDescription ( ) } ) ; } } else if ( lexeme . typeIndex == HTMLTokenTypes . STRING ) { docs += Messages . HTMLFileLanguageService_StringLiteralHTML ; docs += lexeme . getText ( ) ; } else { docs += "" ; } return docs ; } } public Lexeme getValidDocumentationLexeme ( Lexeme lexeme ) { return findPreviousValidLexeme ( lexeme ) ; } private Lexeme findPreviousValidLexeme ( Lexeme lexeme ) { if ( isLexemeOfInterest ( lexeme ) ) { return lexeme ; } LexemeList ll = getFileContext ( ) . getLexemeList ( ) ; if ( ll == null ) { return null ; } Lexeme newLexeme = null ; int index = ll . getLexemeIndex ( lexeme ) ; while ( index > 0 ) { Lexeme l = ll . get ( index ) ; if ( l == null ) { return null ; } if ( isLexemeOfInterest ( l ) ) { newLexeme = l ; break ; } index -- ; } return newLexeme ; } private boolean isLexemeOfInterest ( Lexeme lexeme ) { return ( lexeme . typeIndex == HTMLTokenTypes . NAME || lexeme . getCategoryIndex ( ) == TokenCategories . KEYWORD || lexeme . typeIndex == HTMLTokenTypes . START_TAG || lexeme . typeIndex == HTMLTokenTypes . END_TAG ) ; } public String getDefaultLanguage ( ) { return HTMLMimeType . MimeType ; } public IOffsetMapper getOffsetMapper ( ) { return offsetMapper ; } public void createOffsetMapper ( IParentOffsetMapper parent ) { offsetMapper = new HTMLOffsetMapper ( parent ) ; } public static HTMLFileLanguageService getHTMLFileLanguageService ( IFileService fileContext ) { HTMLFileLanguageService languageService = ( HTMLFileLanguageService ) fileContext . getLanguageService ( HTMLMimeType . MimeType ) ; if ( languageService == null ) { throw new IllegalStateException ( Messages . HTMLFileLanguageService_NoHTMLLanguageServiceAvailable ) ; } return languageService ; } public void connectSourceProvider ( IFileSourceProvider sourceProvider ) { if ( isFirstConnection ) { isFirstConnection = false ; } } public void disconnectSourceProvider ( IFileSourceProvider sourceProvider ) { if ( _delayedFileListener != null ) { this . fileService . addDelayedFileListener ( _delayedFileListener ) ; _delayedFileListener = null ; } loader . unloadEnvironment ( this . getParseState ( ) . getFileIndex ( ) ) ; offsetMapper . dispose ( ) ; offsetMapper = null ; loader = null ; environment = null ; } public boolean isProfileMember ( ) { return this . isProfileMember ; } public void setProfileMember ( boolean isProfileMember ) { this . isProfileMember = isProfileMember ; } public void reset ( boolean resetFileIndex ) { } } 
=======
abstract class AbstractRel extends AbstractDatatype { @ Override public void checkValid ( CharSequence literal ) throws DatatypeException { Set < String > tokensSeen = new HashSet < String > ( ) ; StringBuilder builder = new StringBuilder ( ) ; int len = literal . length ( ) ; for ( int i = 0 ; i < len ; i ++ ) { char c = literal . charAt ( i ) ; if ( isWhitespace ( c ) && builder . length ( ) > 0 ) { checkToken ( builder , i , tokensSeen ) ; builder . setLength ( 0 ) ; } else { builder . append ( toAsciiLowerCase ( c ) ) ; } } if ( builder . length ( ) > 0 ) { checkToken ( builder , len , tokensSeen ) ; } } private void checkToken ( StringBuilder builder , int i , Set < String > tokensSeen ) throws DatatypeException { String token = builder . toString ( ) ; if ( tokensSeen . contains ( token ) ) { throw newDatatypeException ( i - 1 , "Duplicate keyword " , token , "." ) ; } tokensSeen . add ( token ) ; if ( ! isRegistered ( token ) ) { try { Html5DatatypeLibrary dl = new Html5DatatypeLibrary ( ) ; Iri iri = ( Iri ) dl . createDatatype ( "iri" ) ; iri . checkValid ( token ) ; } catch ( DatatypeException e ) { throw newDatatypeException ( i - 1 , "The string " , token , " is not a registered keyword or absolute URL." ) ; } } } protected abstract boolean isRegistered ( String token ) ; } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
