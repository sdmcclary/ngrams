<<<<<<< HEAD
public class ProfileManager implements IUpdaterThreadUpdateable { public static int APPLY_PROFILE_DELAY = 1000 ; public static final String DEFAULT_PROFILE_NAME = "Default Profile" ; static final String staticProtocol = "static://" ; static final String titleLabel = " (Auto-created)" ; public static final String DEFAULT_PROFILE_PATH = staticProtocol + DEFAULT_PROFILE_NAME ; Profile currentProfile = null ; ArrayList listeners = new ArrayList ( ) ; ArrayList appliedListeners = new ArrayList ( ) ; HashMap profiles = new HashMap ( ) ; private HashMap languageEnvironments = new HashMap ( ) ; private HashMap languageFactories = new HashMap ( ) ; private UpdaterThread _applyProfilesThread = null ; private HashMap changeListenerHash = new HashMap ( ) ; public ProfileManager ( ) { this ( true ) ; } public ProfileManager ( boolean threaded ) { if ( threaded ) { startProfileThread ( ) ; } loadStaticProfiles ( ) ; } private void startProfileThread ( ) { _applyProfilesThread = new UpdaterThread ( this , APPLY_PROFILE_DELAY , Messages . ProfileManager_ApplyProfiles ) ; _applyProfilesThread . start ( ) ; } private void loadStaticProfiles ( ) { boolean defaultCreated = false ; try { Preferences prefs = UnifiedEditorsPlugin . getDefault ( ) . getPluginPreferences ( ) ; String profilesList = prefs . getString ( Profile . getProfileListKey ( ) ) ; if ( profilesList . length ( ) == 0 ) { return ; } String [ ] list = profilesList . split ( "," ) ; for ( int i = 0 ; i < list . length ; i ++ ) { if ( list [ i ] . length ( ) > 0 ) { Trace . info ( Messages . ProfileManager_LoadingProfile + list [ i ] ) ; String [ ] parts = list [ i ] . split ( "=" ) ; String name = parts [ 0 ] ; String path = parts [ 1 ] ; createProfile ( name , path ) ; if ( list [ i ] . equals ( DEFAULT_PROFILE_NAME ) ) { defaultCreated = true ; } } } } catch ( Exception ex ) { } finally { if ( ! defaultCreated ) { createProfile ( DEFAULT_PROFILE_NAME , DEFAULT_PROFILE_PATH ) ; } this . setCurrentProfile ( DEFAULT_PROFILE_PATH ) ; } } public Profile createProfile ( String name , String path ) { return createProfile ( name , path , false ) ; } public Profile createProfile ( String name , String path , boolean dynamic ) { Profile profile = new Profile ( name , path , dynamic ) ; addProfile ( profile ) ; return profile ; } public void addProfile ( Profile profile ) { IProfileChangeListener pcl = new IProfileChangeListener ( ) { public void onProfileChanged ( Profile p ) { fireProfileChangeEvent ( p ) ; applyProfiles ( ) ; } } ; changeListenerHash . put ( profile . getURI ( ) , pcl ) ; profile . addProfileChangeListener ( pcl ) ; profiles . put ( profile . getURI ( ) , profile ) ; fireProfileChangeEvent ( profile ) ; } public void removeProfile ( String path ) { IProfileChangeListener pcl = null ; if ( changeListenerHash . containsKey ( path ) ) { pcl = ( IProfileChangeListener ) changeListenerHash . get ( path ) ; changeListenerHash . remove ( path ) ; } if ( profiles . containsKey ( path ) ) { Profile p = getProfile ( path ) ; p . clear ( ) ; if ( pcl != null ) { p . removeProfileChangeListener ( pcl ) ; } profiles . remove ( path ) ; fireProfileChangeEvent ( p ) ; applyProfiles ( ) ; } } public void setCurrentProfile ( String path ) { Profile p = getProfile ( path ) ; if ( p == null ) { p = getDefaultProfile ( ) ; } setCurrentProfile ( p ) ; } public Profile getDefaultProfile ( ) { return ( Profile ) profiles . get ( DEFAULT_PROFILE_PATH ) ; } public void setCurrentProfile ( Profile profile ) { this . currentProfile = profile ; fireProfileChangeEvent ( this . currentProfile ) ; applyProfiles ( ) ; } public Profile getCurrentProfile ( ) { return this . currentProfile ; } public boolean isCurrentProfile ( Profile profile ) { return this . currentProfile == profile ; } public void addLanguageSupport ( String mimeType , ILanguageEnvironment lang , IFileServiceFactory factory ) { languageEnvironments . put ( mimeType , lang ) ; if ( ! languageFactories . containsKey ( mimeType ) ) { languageFactories . put ( mimeType , factory ) ; } } public Profile [ ] getProfiles ( ) { Profile [ ] array = ( Profile [ ] ) profiles . values ( ) . toArray ( new Profile [ 0 ] ) ; return array ; } public String [ ] getProfilePaths ( ) { return ( String [ ] ) profiles . keySet ( ) . toArray ( new String [ 0 ] ) ; } public Profile getProfile ( String path ) { return ( Profile ) profiles . get ( path ) ; } public int getTotalFileCount ( ) { Profile [ ] profiles = getProfiles ( ) ; int count = 0 ; for ( int i = 0 ; i < profiles . length ; i ++ ) { count += profiles [ i ] . getURIs ( ) . length ; } return count ; } public void fireProfileChangeEvent ( Profile p ) { for ( int i = 0 ; i < listeners . size ( ) ; i ++ ) { IProfileChangeListener listener = ( IProfileChangeListener ) listeners . get ( i ) ; listener . onProfileChanged ( p ) ; } } public void addProfileChangeListener ( IProfileChangeListener l ) { listeners . add ( l ) ; } public void removeProfileChangeListener ( IProfileChangeListener l ) { listeners . remove ( l ) ; } public void fireProfileAppliedEvent ( ProfileURI p , boolean state ) { for ( int i = 0 ; i < listeners . size ( ) ; i ++ ) { IProfileAppliedListener listener = ( IProfileAppliedListener ) appliedListeners . get ( i ) ; listener . onProfileApplied ( p , state ) ; } } public void addProfileAppliedListener ( IProfileAppliedListener l ) { appliedListeners . add ( l ) ; } public void removeProfileChangeListener ( IProfileAppliedListener l ) { appliedListeners . remove ( l ) ; } public void onUpdaterThreadUpdate ( ) { resetIconStatus ( ) ; resetEnvironment ( ) ; resetAndApply ( ) ; } private void resetIconStatus ( ) { if ( currentProfile != null ) { ProfileURI [ ] paths = currentProfile . getURIsIncludingChildren ( ) ; for ( int i = 0 ; i < paths . length ; i ++ ) { fireProfileAppliedEvent ( paths [ i ] , false ) ; } } } public void resetEnvironment ( ) { Collection langs = languageEnvironments . values ( ) ; for ( Iterator iter = langs . iterator ( ) ; iter . hasNext ( ) ; ) { ILanguageEnvironment lang = ( ILanguageEnvironment ) iter . next ( ) ; lang . cleanEnvironment ( ) ; } for ( ProfileFileTypeInfo info : ProfileFileTypeManager . getInstance ( ) . getAllInfos ( ) ) { if ( info . processor != null ) { info . processor . cleanEnvironment ( ) ; } } } private void resetAndApply ( ) { if ( currentProfile == null ) { return ; } HashSet allURIs = new HashSet ( Arrays . asList ( FileContextManager . getKeySet ( ) ) ) ; String activeEditorURI = CoreUIUtils . getActiveEditorURI ( ) ; HashSet currentProfileURIs = new HashSet ( Arrays . asList ( currentProfile . getURIsIncludingChildrenAsStrings ( ) ) ) ; allURIs . remove ( activeEditorURI ) ; allURIs . remove ( currentProfileURIs ) ; deactivateFileContexts ( allURIs ) ; reindexCurrentProfile ( ) ; reindexActiveEditor ( activeEditorURI , currentProfileURIs ) ; } private void deactivateFileContexts ( HashSet allURIs ) { for ( Iterator iter = allURIs . iterator ( ) ; iter . hasNext ( ) ; ) { String uri = ( String ) iter . next ( ) ; FileService fileContext = FileContextManager . get ( uri ) ; if ( fileContext != null ) { IParseState parseState = fileContext . getParseState ( ) ; if ( parseState != null ) { parseState . setFileIndex ( FileContextManager . DEFAULT_FILE_INDEX ) ; } } } } private void reindexActiveEditor ( String activeEditorURI , HashSet currentProfileURIs ) { FileService activeFileContext = FileContextManager . get ( activeEditorURI ) ; if ( activeFileContext != null && currentProfileURIs . contains ( activeEditorURI ) == false ) { IParseState parseState = activeFileContext . getParseState ( ) ; if ( parseState != null ) { parseState . setFileIndex ( FileContextManager . CURRENT_FILE_INDEX ) ; } activeFileContext . forceContentChangedEvent ( ) ; } } private void reindexCurrentProfile ( ) { int fileIndex = 0 ; String [ ] currentProfileURIs = currentProfile . getURIsIncludingChildrenAsStrings ( ) ; String [ ] openEditorsArray = CoreUIUtils . getOpenEditorPaths ( ) ; Set < String > openEditors = new HashSet < String > ( Arrays . asList ( openEditorsArray ) ) ; for ( String uri : currentProfileURIs ) { if ( openEditorsArray . length > 0 ) { String extension = FileUtils . getExtension ( uri ) ; ProfileFileTypeInfo info = ProfileFileTypeManager . getInstance ( ) . getInfo ( extension ) ; if ( info != null && info . processor != null ) { if ( info . processor . processFile ( uri , fileIndex ++ ) ) { fireProfileAppliedEvent ( new ProfileURI ( uri , currentProfile ) , true ) ; continue ; } } String mimeType = this . computeMIMEType ( uri ) ; if ( mimeType == null ) { IdeLog . logInfo ( UnifiedEditorsPlugin . getDefault ( ) , StringUtils . format ( Messages . ProfileManager_MimeTypeError , uri ) ) ; } else { FileService fileContext = FileContextManager . get ( uri ) ; if ( fileContext == null || ( fileContext . getSourceProvider ( ) instanceof DocumentSourceProvider && openEditors . contains ( uri ) == false ) ) { IFileServiceFactory factory = ( IFileServiceFactory ) this . languageFactories . get ( mimeType ) ; if ( factory == null ) { IdeLog . logError ( UnifiedEditorsPlugin . getDefault ( ) , StringUtils . format ( Messages . ProfileManager_ServiceFactoryError , mimeType ) ) ; continue ; } String path = CoreUIUtils . getPathFromURI ( uri ) ; File file = new File ( path ) ; if ( file . exists ( ) == false ) { continue ; } FileSourceProvider fsp = new FileSourceProvider ( file ) ; fileContext = factory . createFileService ( fsp ) ; FileContextManager . add ( uri , fileContext ) ; } fileContext . getParseState ( ) . setFileIndex ( fileIndex ++ ) ; fileContext . doFullParse ( ) ; fileContext . forceContentChangedEvent ( ) ; } } fireProfileAppliedEvent ( new ProfileURI ( uri , currentProfile ) , true ) ; } } private String computeMIMEType ( String uri ) { String mimeType = null ; if ( uri . toLowerCase ( ) . endsWith ( ".js" ) || uri . toLowerCase ( ) . endsWith ( ".sdoc" ) ) { mimeType = "text/javascript" ; } return mimeType ; } public void applyProfiles ( ) { if ( _applyProfilesThread != null ) { _applyProfilesThread . setDirty ( ) ; } else { onUpdaterThreadUpdate ( ) ; } } public void refreshEnvironment ( ) { Profile [ ] profiles = getProfiles ( ) ; for ( int i = 0 ; i < profiles . length ; i ++ ) { fireProfileChangeEvent ( profiles [ i ] ) ; } applyProfiles ( ) ; } public Profile makeProfileStatic ( Profile profile ) { String profileName = profile . getName ( ) ; String [ ] fileListArray = profile . getURIsAsStrings ( ) ; String path = profile . getURI ( ) ; boolean wasSelected = false ; if ( path == this . getCurrentProfile ( ) . getURI ( ) ) { wasSelected = true ; } String newPath = staticProtocol + path ; removeProfile ( path ) ; if ( profileName . indexOf ( titleLabel ) != - 1 ) { profileName = profileName . substring ( 0 , profileName . length ( ) - titleLabel . length ( ) ) ; } Profile newProfile = createProfile ( profileName , newPath ) ; newProfile . addURIs ( fileListArray ) ; if ( wasSelected ) { setCurrentProfile ( newProfile . getURI ( ) ) ; } return newProfile ; } } 
=======
public final class ScriptDocumentation extends Script { private enum State { BEFORE_DOCUMENTATION , SLASH , IN_COMMENT , IN_LINE_COMMENT , STAR } public static final ScriptDocumentation THE_INSTANCE = new ScriptDocumentation ( ) ; private ScriptDocumentation ( ) { super ( ) ; } @ Override public void checkValid ( CharSequence literal ) throws DatatypeException { State state = State . BEFORE_DOCUMENTATION ; for ( int i = 0 ; i < literal . length ( ) ; i ++ ) { char c = literal . charAt ( i ) ; switch ( state ) { case BEFORE_DOCUMENTATION : switch ( c ) { case ' ' : case '\t' : case '\n' : continue ; case '/' : if ( i == literal . length ( ) - 1 ) { throw newDatatypeException ( "Expected asterisk or slash but content ended with a " + "single slash instead." ) ; } state = State . SLASH ; continue ; default : throw newDatatypeException ( "Expected space, tab, newline, or slash but found “" + c + "” instead." ) ; } case SLASH : switch ( c ) { case '*' : state = State . IN_COMMENT ; continue ; case '/' : state = State . IN_LINE_COMMENT ; continue ; default : throw newDatatypeException ( "Expected asterisk or slash but found “" + c + "” instead." ) ; } case IN_COMMENT : switch ( c ) { case '*' : state = State . STAR ; continue ; default : continue ; } case STAR : switch ( c ) { case '/' : state = State . BEFORE_DOCUMENTATION ; continue ; default : continue ; } case IN_LINE_COMMENT : switch ( c ) { case '\n' : state = State . BEFORE_DOCUMENTATION ; continue ; default : continue ; } default : throw newDatatypeException ( "Content ended prematurely." ) ; } } if ( state == State . IN_LINE_COMMENT ) { throw newDatatypeException ( "Content contains a line starting with" + " the character sequence “//” but not ending" + " with a newline." ) ; } if ( state == State . IN_COMMENT || state == State . STAR ) { throw newDatatypeException ( "Content contains the character" + " sequence “/*” without a later occurrence of" + " the character sequence “*/”." ) ; } super . checkValid ( literal ) ; return ; } @ Override public String getName ( ) { return "script documentation" ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
