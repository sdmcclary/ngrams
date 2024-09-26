<<<<<<< HEAD
public final class GenerateDocs { private GenerateDocs ( ) { } public static String generateXML ( JSParseState parseState , String fileName ) { String xml = null ; JSContentProvider cp = new JSContentProvider ( ) ; try { IParseNode results = parseState . getParseResults ( ) ; Object [ ] nodes = cp . getElements ( results ) ; xml = getXML ( cp , nodes , parseState , fileName ) ; } catch ( Exception ex ) { IdeLog . logError ( DocgenPlugin . getDefault ( ) , Messages . GenerateDocs_ERR_GenerateXML , ex ) ; } return xml ; } public static String generateHTMLFromXML ( String xml , String docRoot , String fileName , InputStream schemaStream ) { StringReader sw = new StringReader ( xml ) ; try { String filePath = docRoot ; Path p = new Path ( filePath ) ; String indexPath = p . append ( "index.html" ) . toOSString ( ) ; dump ( xml , indexPath + ".xml" ) ; transform ( sw , schemaStream , fileName , indexPath ) ; return "file://" + indexPath ; } catch ( TransformerException ex ) { IdeLog . logError ( DocgenPlugin . getDefault ( ) , Messages . GenerateDocs_ERR_TransformDoc , ex ) ; } catch ( IOException ex ) { IdeLog . logError ( DocgenPlugin . getDefault ( ) , Messages . GenerateDocs_ERR_TransformDoc , ex ) ; } return null ; } protected static PropertyDocumentation getFunctionDocumentation ( JSParseState parseState , int offset ) { if ( parseState != null ) { IDocumentationStore store = parseState . getDocumentationStore ( ) ; return ( PropertyDocumentation ) store . getDocumentationFromOffset ( offset ) ; } return null ; } protected static IDocumentation getPropertyDocumentation ( JSParseState parseState , Lexeme lexeme ) { if ( lexeme == null ) { return null ; } if ( parseState != null ) { IDocumentationStore store = parseState . getDocumentationStore ( ) ; return ( IDocumentation ) store . getDocumentationFromOffset ( lexeme . offset + lexeme . getLength ( ) ) ; } return null ; } public static String getXML ( JSContentProvider cp , Object [ ] nodes , JSParseState ps , String fileName ) { SourceWriter writer = new SourceWriter ( ) ; writer . println ( "<?xml-stylesheet type=\"text/xsl\" href=\"docs.xsl\"?><javascript fileName=\"" + fileName + "\">" ) ; writer . increaseIndent ( ) ; getXML ( writer , cp , nodes , ps , null ) ; writer . println ( "</javascript>" ) ; return writer . toString ( ) ; } public static void getXML ( SourceWriter writer , JSContentProvider cp , Object [ ] nodes , JSParseState ps , String prefix ) { if ( nodes == null ) { return ; } for ( int i = 0 ; i < nodes . length ; i ++ ) { Object item = nodes [ i ] ; if ( item instanceof JSOutlineItem ) { JSOutlineItem jsItem = ( JSOutlineItem ) item ; PropertyDocumentation fd = getRelatedDocumentation ( ps , jsItem ) ; String newPrefix = "" ; if ( prefix == null ) { newPrefix = fd . getName ( ) ; } else { newPrefix = prefix + "." + fd . getName ( ) ; } if ( prefix != null && ( newPrefix . endsWith ( ".prototype" ) || prefix . endsWith ( ".prototype" ) ) ) { fd . setIsInstance ( true ) ; } startOutlineItem ( writer , ( FunctionDocumentation ) fd , jsItem ) ; Object [ ] children = cp . getChildren ( jsItem ) ; getXML ( writer , cp , children , ps , newPrefix ) ; endOutlineItem ( writer , jsItem ) ; } } } private static String typeAsStringType ( int type ) { String returnString = null ; switch ( type ) { case JSOutlineItemType . FUNCTION : { returnString = "Function" ; break ; } case JSOutlineItemType . ARRAY : { returnString = "Array" ; break ; } case JSOutlineItemType . BOOLEAN : { returnString = "Boolean" ; break ; } case JSOutlineItemType . NULL : { returnString = "null" ; break ; } case JSOutlineItemType . NUMBER : { returnString = "Number" ; break ; } case JSOutlineItemType . REGEX : { returnString = "Regex" ; break ; } case JSOutlineItemType . STRING : { returnString = "String" ; break ; } case JSOutlineItemType . PROPERTY : case JSOutlineItemType . OBJECT_LITERAL : default : { returnString = "Object" ; break ; } } return returnString ; } private static String typeAsString ( int type ) { String returnString = null ; switch ( type ) { case JSOutlineItemType . FUNCTION : { returnString = "function" ; break ; } case JSOutlineItemType . OBJECT_LITERAL : { returnString = "object_literal" ; break ; } case JSOutlineItemType . PROPERTY : case JSOutlineItemType . ARRAY : case JSOutlineItemType . BOOLEAN : case JSOutlineItemType . NULL : case JSOutlineItemType . NUMBER : case JSOutlineItemType . REGEX : case JSOutlineItemType . STRING : { returnString = "property" ; break ; } default : { returnString = "unknown" ; break ; } } return returnString ; } private static void endOutlineItem ( SourceWriter writer , JSOutlineItem jsItem ) { writer . decreaseIndent ( ) ; writer . printlnWithIndent ( "</" + typeAsString ( jsItem . getType ( ) ) + ">" ) ; } private static void startOutlineItem ( SourceWriter writer , FunctionDocumentation documentation , JSOutlineItem jsItem ) { writer . printWithIndent ( "<" + typeAsString ( jsItem . getType ( ) ) ) . print ( " name=\"" + stripTags ( documentation . getName ( ) ) + "\"" ) ; writeAttributes ( writer , documentation , jsItem ) ; writer . println ( ">" ) ; writer . increaseIndent ( ) ; if ( documentation != null ) { writeDocumentation ( writer , documentation ) ; writeParameters ( writer , documentation ) ; writeExamples ( writer , documentation ) ; writeAliases ( writer , documentation ) ; writeSeeAlso ( writer , documentation ) ; if ( jsItem . getType ( ) == JSOutlineItemType . FUNCTION ) { writeTypes ( writer , documentation ) ; } } } private static void writeTypes ( SourceWriter writer , FunctionDocumentation documentation ) { if ( documentation . getReturn ( ) != null && documentation . getReturn ( ) . getTypes ( ) . length > 0 ) { writer . printlnWithIndent ( "<return-types>" ) ; writer . increaseIndent ( ) ; for ( int i = 0 ; i < documentation . getReturn ( ) . getTypes ( ) . length ; i ++ ) { String array_element = documentation . getReturn ( ) . getTypes ( ) [ i ] ; writer . printWithIndent ( "<return-type" ) . println ( " type=\"" + array_element + "\" />" ) ; } writer . decreaseIndent ( ) ; writer . printlnWithIndent ( "</return-types>" ) ; } } private static void writeAttributes ( SourceWriter writer , FunctionDocumentation documentation , JSOutlineItem jsItem ) { if ( documentation . getIsInstance ( ) ) { writer . print ( " scope=\"instance\"" ) ; } else { writer . print ( " scope=\"static\"" ) ; } writer . print ( " constructor=\"" + documentation . getIsConstructor ( ) + "\"" ) ; writer . print ( " deprecated=\"" + documentation . getIsDeprecated ( ) + "\"" ) ; writer . print ( " private=\"" + documentation . getIsPrivate ( ) + "\"" ) ; writer . print ( " protected=\"" + documentation . getIsProtected ( ) + "\"" ) ; writer . print ( " ignored=\"" + documentation . getIsIgnored ( ) + "\"" ) ; writer . print ( " internal=\"" + documentation . getIsInternal ( ) + "\"" ) ; if ( documentation . getReturn ( ) != null && documentation . getReturn ( ) . getTypes ( ) . length > 0 ) { writer . print ( " type=\"" + documentation . getReturn ( ) . getTypes ( ) [ 0 ] + "\"" ) ; } else { writer . print ( " type=\"" + typeAsStringType ( jsItem . getType ( ) ) + "\"" ) ; } } private static void writeParameters ( SourceWriter writer , FunctionDocumentation documentation ) { TypedDescription [ ] params = documentation . getParams ( ) ; if ( documentation . getParams ( ) != null && documentation . getParams ( ) . length > 0 ) { writer . printlnWithIndent ( "<parameters>" ) ; writer . increaseIndent ( ) ; for ( int i = 0 ; i < params . length ; i ++ ) { TypedDescription description = params [ i ] ; writer . printWithIndent ( "<parameter" ) . print ( " name=\"" + stripTags ( description . getName ( ) ) + "\"" ) ; if ( description . getTypes ( ) . length > 0 ) { writer . print ( " type=\"" + description . getTypes ( ) [ 0 ] + "\"" ) ; } else { writer . print ( " type=\"Object\"" ) ; } writer . println ( ">" ) ; if ( description . getDescription ( ) != null && ! description . getDescription ( ) . trim ( ) . equals ( "" ) ) { writer . increaseIndent ( ) ; writeDocumentation ( writer , description ) ; writer . decreaseIndent ( ) ; } writer . printlnWithIndent ( "</parameter>" ) ; } writer . decreaseIndent ( ) ; writer . printlnWithIndent ( "</parameters>" ) ; } } private static void writeExamples ( SourceWriter writer , FunctionDocumentation documentation ) { String [ ] params = documentation . getExamples ( ) ; if ( params != null && params . length > 0 ) { writer . printlnWithIndent ( "<examples>" ) ; writer . increaseIndent ( ) ; for ( int i = 0 ; i < params . length ; i ++ ) { String description = params [ i ] ; description = StringUtils . replace ( description , "<" , "&lt;" ) ; description = StringUtils . replace ( description , ">" , "&gt;" ) ; writer . printWithIndent ( "<example>" ) ; writer . increaseIndent ( ) ; writer . printWithIndent ( description ) ; writer . decreaseIndent ( ) ; writer . printWithIndent ( "</example>" ) ; } writer . decreaseIndent ( ) ; writer . printlnWithIndent ( "</examples>" ) ; } } private static void writeSeeAlso ( SourceWriter writer , FunctionDocumentation documentation ) { String [ ] params = documentation . getSees ( ) ; if ( params != null && params . length > 0 ) { writer . printlnWithIndent ( "<references>" ) ; writer . increaseIndent ( ) ; for ( int i = 0 ; i < params . length ; i ++ ) { String description = params [ i ] ; writer . printWithIndent ( "<reference" ) . print ( " name=\"" + stripTags ( description ) + "\" />" ) ; } writer . decreaseIndent ( ) ; writer . printlnWithIndent ( "</references>" ) ; } } private static void writeAliases ( SourceWriter writer , FunctionDocumentation documentation ) { TypedDescription params = documentation . getAliases ( ) ; if ( params != null && params . getTypes ( ) . length > 0 ) { writer . printlnWithIndent ( "<aliases>" ) ; writer . increaseIndent ( ) ; String [ ] types = params . getTypes ( ) ; for ( int i = 0 ; i < types . length ; i ++ ) { String description = types [ i ] ; writer . printWithIndent ( "<alias" ) . print ( " name=\"" + stripTags ( description ) + "\" />" ) ; } writer . decreaseIndent ( ) ; writer . printlnWithIndent ( "</aliases>" ) ; } } private static void writeDocumentation ( SourceWriter writer , PropertyDocumentation fd ) { if ( fd . getDescription ( ) != null && ! fd . getDescription ( ) . trim ( ) . equals ( "" ) ) { writer . printWithIndent ( "<description>" ) . println ( stripTags ( fd . getDescription ( ) . trim ( ) ) + "</description>" ) ; } } private static void writeDocumentation ( SourceWriter writer , TypedDescription fd ) { if ( fd . getDescription ( ) != null && ! fd . getDescription ( ) . trim ( ) . equals ( "" ) ) { writer . printWithIndent ( "<description>" ) . println ( stripTags ( fd . getDescription ( ) . trim ( ) ) + "</description>" ) ; } } private static String stripTags ( String text ) { String replaced = StringUtils . replace ( text , "&" , "&amp;" ) ; replaced = StringUtils . replace ( replaced , "<" , "&lt;" ) ; replaced = StringUtils . replace ( replaced , ">" , "&gt;" ) ; replaced = StringUtils . replace ( replaced , "\"" , "&quot;" ) ; return StringUtils . replace ( replaced , "'" , "&apos;" ) ; } private static PropertyDocumentation getRelatedDocumentation ( JSParseState ps , JSOutlineItem jsItem ) { int startOffset = jsItem . getStartingOffset ( ) ; LexemeList ll = ps . getLexemeList ( ) ; int startIndex = ll . getLexemeIndex ( startOffset ) ; Lexeme next = ll . get ( startIndex + 1 ) ; PropertyDocumentation fd = null ; if ( ! jsItem . getLabel ( ) . equals ( "prototype" ) && ( next == null || next . getToken ( ) . getTypeIndex ( ) != JSTokenTypes . DOT ) ) { fd = searchForDocumentation ( ps , ll , startIndex ) ; } if ( fd == null ) { fd = new FunctionDocumentation ( ) ; fd . setIsIgnored ( true ) ; } setDocumentationName ( fd , jsItem ) ; if ( jsItem . getType ( ) == JSOutlineItemType . FUNCTION ) { FunctionDocumentation fd2 = ( FunctionDocumentation ) fd ; fd2 . setIsMethod ( true ) ; } return fd ; } private static PropertyDocumentation searchForDocumentation ( JSParseState ps , LexemeList ll , int startIndex ) { PropertyDocumentation fd = null ; for ( int k = startIndex ; k > startIndex - 10 ; k -- ) { if ( k < 0 ) { break ; } Lexeme l = ll . get ( k ) ; fd = getFunctionDocumentation ( ps , l . getEndingOffset ( ) ) ; if ( fd != null ) { break ; } } return fd ; } private static void setDocumentationName ( PropertyDocumentation fd , JSOutlineItem jsItem ) { if ( fd . getName ( ) == null || fd . getName ( ) . equals ( "" ) ) { if ( jsItem . getType ( ) == JSOutlineItemType . FUNCTION ) { IRange range = null ; if ( range instanceof IParseNode ) { IParseNode pn = ( IParseNode ) range ; fd . setName ( pn . getAttribute ( "name" ) ) ; } else { fd . setName ( stripParens ( jsItem . getLabel ( ) ) ) ; } } else { fd . setName ( stripParens ( jsItem . getLabel ( ) ) ) ; } } } public static String stripParens ( String label ) { if ( label . indexOf ( '(' ) > 0 ) { return label . substring ( 0 , label . indexOf ( '(' ) ) ; } else { return label ; } } public static void exportResource ( String folderPath , String fileName ) { InputStream zipStream = DocgenPlugin . class . getResourceAsStream ( "/com/aptana/ide/js/docgen/resources/" + fileName ) ; ( new File ( folderPath ) ) . mkdirs ( ) ; FileUtils . writeStreamToFile ( zipStream , folderPath + "/" + fileName ) ; } public static void exportImage ( String folderPath , String fileName ) { InputStream zipStream = DocgenPlugin . class . getResourceAsStream ( "/com/aptana/ide/js/docgen/resources/images/" + fileName ) ; ( new File ( folderPath ) ) . mkdirs ( ) ; FileUtils . writeStreamToFile ( zipStream , folderPath + "/" + fileName ) ; } public static String getFilePrefix ( IFile file ) { if ( file == null ) { return StringUtils . EMPTY ; } String extension = "." + file . getFileExtension ( ) ; return file . getName ( ) . replaceAll ( extension , StringUtils . EMPTY ) ; } public static String getParentFolder ( IFile file ) { IPath path = file . getLocation ( ) ; if ( path . hasTrailingSeparator ( ) ) { return path . toString ( ) ; } else { IPath newPath = path . removeLastSegments ( 1 ) ; return newPath . toString ( ) ; } } public static void transform ( StringReader sw , InputStream inputStreamXsl , String fileNamePrefix , String filePath ) throws TransformerException , TransformerConfigurationException { TransformerFactory tfactory = TransformerFactory . newInstance ( ) ; Transformer transformer = tfactory . newTransformer ( new StreamSource ( inputStreamXsl ) ) ; transformer . setParameter ( "fileNamePrefix" , fileNamePrefix ) ; transformer . transform ( new StreamSource ( sw ) , new StreamResult ( new File ( filePath ) ) ) ; } public static void dump ( String text , String filePath ) throws IOException { File outFile = new File ( filePath ) ; FileWriter out = new FileWriter ( outFile ) ; out . write ( text ) ; out . close ( ) ; } } 
=======
public interface MessageHandler { int handleMessage ( Message msg ) ; } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
