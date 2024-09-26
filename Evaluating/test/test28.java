public class HtmlFormatter { private static MessageFormat uptodateFormat = getFormat ( "uptodateFormat" ) ; private static MessageFormat newLocallyFormat = getFormat ( "newLocallyFormat" ) ; private static MessageFormat addedLocallyFormat = getFormat ( "addedLocallyFormat" ) ; private static MessageFormat modifiedLocallyFormat = getFormat ( "modifiedLocallyFormat" ) ; private static MessageFormat removedLocallyFormat = getFormat ( "removedLocallyFormat" ) ; private static MessageFormat deletedLocallyFormat = getFormat ( "deletedLocallyFormat" ) ; private static MessageFormat excludedFormat = getFormat ( "excludedFormat" ) ; private static MessageFormat conflictFormat = getFormat ( "conflictFormat" ) ; private static final int STATUS_TEXT_ANNOTABLE = StatusInfo . STATUS_NOTVERSIONED_EXCLUDED | StatusInfo . STATUS_NOTVERSIONED_NEWLOCALLY | StatusInfo . STATUS_VERSIONED_UPTODATE | StatusInfo . STATUS_VERSIONED_MODIFIEDLOCALLY | StatusInfo . STATUS_VERSIONED_CONFLICT | StatusInfo . STATUS_VERSIONED_REMOVEDLOCALLY | StatusInfo . STATUS_VERSIONED_DELETEDLOCALLY | StatusInfo . STATUS_VERSIONED_ADDEDLOCALLY ; private static final Pattern lessThan = Pattern . compile ( "<" ) ; private static HtmlFormatter instance ; private String emptyFormat ; private Boolean needRevisionForFormat ; private MessageFormat format ; public static HtmlFormatter getInstance ( ) { if ( instance == null ) { instance = new HtmlFormatter ( ) ; } return instance ; } private HtmlFormatter ( ) { initDefaults ( ) ; } private void initDefaults ( ) { Field [ ] fields = HtmlFormatter . class . getDeclaredFields ( ) ; for ( int i = 0 ; i < fields . length ; i ++ ) { String name = fields [ i ] . getName ( ) ; if ( name . endsWith ( "Format" ) ) { initDefaultColor ( name . substring ( 0 , name . length ( ) - 6 ) ) ; } } refresh ( ) ; } public void refresh ( ) { String string = GitModuleConfig . getDefault ( ) . getAnnotationFormat ( ) ; if ( string != null && ! string . trim ( ) . equals ( "" ) ) { needRevisionForFormat = isRevisionInAnnotationFormat ( string ) ; string = string . replaceAll ( "\\{revision\\}" , "\\{0\\}" ) ; string = string . replaceAll ( "\\{status\\}" , "\\{1\\}" ) ; string = string . replaceAll ( "\\{folder\\}" , "\\{2\\}" ) ; format = new MessageFormat ( string ) ; emptyFormat = format . format ( new String [ ] { "" , "" , "" } , new StringBuffer ( ) , null ) . toString ( ) . trim ( ) ; } } public static boolean isRevisionInAnnotationFormat ( String str ) { if ( str . indexOf ( "{revision}" ) != - 1 ) { return true ; } else { return false ; } } private void initDefaultColor ( String name ) { String color = System . getProperty ( "git.color." + name ) ; if ( color == null ) { return ; } setAnnotationColor ( name , color ) ; } private void setAnnotationColor ( String name , String colorString ) { try { Field field = HtmlFormatter . class . getDeclaredField ( name + "Format" ) ; MessageFormat msgFormat = new MessageFormat ( "<font color=\"" + colorString + "\">{0}</font><font color=\"#999999\">{1}</font>" ) ; field . set ( null , msgFormat ) ; } catch ( Exception e ) { throw new IllegalArgumentException ( "Invalid color name" ) ; } } public String annotateNameHtml ( File file , StatusInfo info ) { return annotateNameHtml ( file . getName ( ) , info , file ) ; } public String annotateNameHtml ( String name , StatusInfo mostImportantInfo , File mostImportantFile ) { name = htmlEncode ( name ) ; String textAnnotation ; boolean annotationsVisible = VersioningSupport . getPreferences ( ) . getBoolean ( VersioningSupport . PREF_BOOLEAN_TEXT_ANNOTATIONS_VISIBLE , false ) ; int status = mostImportantInfo . getStatus ( ) ; if ( annotationsVisible && mostImportantFile != null && ( status & STATUS_TEXT_ANNOTABLE ) != 0 ) { if ( format != null ) { textAnnotation = formatAnnotation ( mostImportantInfo , mostImportantFile ) ; } else { String sticky = null ; if ( status == StatusInfo . STATUS_VERSIONED_UPTODATE && sticky == null ) { textAnnotation = "" ; } else if ( status == StatusInfo . STATUS_VERSIONED_UPTODATE ) { textAnnotation = " [" + sticky + "]" ; } else if ( sticky == null ) { String statusText = mostImportantInfo . getShortStatusText ( ) ; if ( ! statusText . equals ( "" ) ) { textAnnotation = " [" + mostImportantInfo . getShortStatusText ( ) + "]" ; } else { textAnnotation = "" ; } } else { textAnnotation = " [" + mostImportantInfo . getShortStatusText ( ) + "; " + sticky + "]" ; } } } else { textAnnotation = "" ; } if ( textAnnotation . length ( ) > 0 ) { textAnnotation = NbBundle . getMessage ( HtmlFormatter . class , "textAnnotation" , textAnnotation ) ; } if ( 0 != ( status & StatusInfo . STATUS_NOTVERSIONED_EXCLUDED ) ) { return excludedFormat . format ( new Object [ ] { name , textAnnotation } ) ; } else if ( 0 != ( status & StatusInfo . STATUS_VERSIONED_DELETEDLOCALLY ) ) { return deletedLocallyFormat . format ( new Object [ ] { name , textAnnotation } ) ; } else if ( 0 != ( status & StatusInfo . STATUS_VERSIONED_REMOVEDLOCALLY ) ) { return removedLocallyFormat . format ( new Object [ ] { name , textAnnotation } ) ; } else if ( 0 != ( status & StatusInfo . STATUS_NOTVERSIONED_NEWLOCALLY ) ) { return newLocallyFormat . format ( new Object [ ] { name , textAnnotation } ) ; } else if ( 0 != ( status & StatusInfo . STATUS_VERSIONED_ADDEDLOCALLY ) ) { return addedLocallyFormat . format ( new Object [ ] { name , textAnnotation } ) ; } else if ( 0 != ( status & StatusInfo . STATUS_VERSIONED_MODIFIEDLOCALLY ) ) { return modifiedLocallyFormat . format ( new Object [ ] { name , textAnnotation } ) ; } else if ( 0 != ( status & StatusInfo . STATUS_VERSIONED_UPTODATE ) ) { return uptodateFormat . format ( new Object [ ] { name , textAnnotation } ) ; } else if ( 0 != ( status & StatusInfo . STATUS_VERSIONED_CONFLICT ) ) { return conflictFormat . format ( new Object [ ] { name , textAnnotation } ) ; } else if ( 0 != ( status & StatusInfo . STATUS_NOTVERSIONED_NOTMANAGED ) ) { return name ; } else if ( status == StatusInfo . STATUS_UNKNOWN ) { return name ; } else { throw new IllegalArgumentException ( "Uncomparable status: " + status ) ; } } private static MessageFormat getFormat ( String key ) { String format = NbBundle . getMessage ( HtmlFormatter . class , key ) ; return new MessageFormat ( format ) ; } private String htmlEncode ( String name ) { if ( name . indexOf ( '<' ) == - 1 ) { return name ; } return lessThan . matcher ( name ) . replaceAll ( "&lt;" ) ; } private String formatAnnotation ( StatusInfo info , File file ) { String statusString = "" ; int status = info . getStatus ( ) ; if ( status != StatusInfo . STATUS_VERSIONED_UPTODATE ) { statusString = info . getShortStatusText ( ) ; } String revisionString = "" ; String binaryString = "" ; if ( needRevisionForFormat ) { if ( ( status & StatusInfo . STATUS_NOTVERSIONED_EXCLUDED ) == 0 ) { try { File root = Git . getInstance ( ) . getTopmostManagedParent ( file ) ; Repository repo = Git . getInstance ( ) . getRepository ( root ) ; ObjectId branch = repo . resolve ( repo . getFullBranch ( ) ) ; String absPath = file . getAbsolutePath ( ) ; String relPath = absPath . replace ( root . getAbsolutePath ( ) , "" ) ; RevWalk walk = new RevWalk ( repo ) ; RevCommit start = walk . parseCommit ( branch ) ; TreeFilter filter = PathFilter . create ( relPath ) ; walk . setTreeFilter ( filter ) ; walk . markStart ( start ) ; for ( RevCommit commit : walk ) { revisionString = commit . getId ( ) . name ( ) ; break ; } walk . dispose ( ) ; } catch ( IOException ex ) { NotifyDescriptor notification = new NotifyDescriptor . Message ( ex , NotifyDescriptor . ERROR_MESSAGE ) ; DialogDisplayer . getDefault ( ) . notifyLater ( notification ) ; } } } String stickyString = null ; if ( stickyString == null ) { stickyString = "" ; } Object [ ] arguments = new Object [ ] { revisionString , statusString , stickyString , } ; String annotation = format . format ( arguments , new StringBuffer ( ) , null ) . toString ( ) . trim ( ) ; if ( annotation . equals ( emptyFormat ) ) { return "" ; } else { return " " + annotation ; } } public String annotateFolderNameHtml ( String name , StatusInfo mostImportantInfo , File mostImportantFile ) { String nameHtml = htmlEncode ( name ) ; if ( mostImportantInfo . getStatus ( ) == StatusInfo . STATUS_NOTVERSIONED_EXCLUDED ) { return excludedFormat . format ( new Object [ ] { nameHtml , "" } ) ; } String fileName = mostImportantFile . getName ( ) ; if ( fileName . equals ( name ) ) { return uptodateFormat . format ( new Object [ ] { nameHtml , "" } ) ; } fileName = null ; File repo = Git . getInstance ( ) . getTopmostManagedParent ( mostImportantFile ) ; if ( repo != null && repo . equals ( mostImportantFile ) ) { if ( ! repo . getName ( ) . equals ( name ) ) { fileName = repo . getName ( ) ; } } if ( fileName != null ) { return uptodateFormat . format ( new Object [ ] { nameHtml , " [" + fileName + "]" } ) ; } else { return uptodateFormat . format ( new Object [ ] { nameHtml , "" } ) ; } } } 