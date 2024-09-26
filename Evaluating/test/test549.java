<<<<<<< HEAD
public class AptanaSignInDialog extends TitleAreaDialog implements ModifyListener , SelectionListener { private static final String IMAGE = "icons/aptana_dialog_tag.png" ; private static final String FORGOT_PASSWORD = "http://id.aptana.com/reset_password" ; private static final String WHAT_IS_APTANA_ID = "http://www.aptana.com/aptana_id" ; private static final String CREATE_ID = "https://id.aptana.com/register" ; private Text username ; private Text password ; private Link forgotPassword ; private Link whatIsIt ; private Link createId ; private final boolean allowAnonymous ; private KeyAdapter signInKeyAdapter = new KeyAdapter ( ) { public void keyPressed ( KeyEvent e ) { if ( ( e . character == '\r' || e . character == '\n' ) && getButton ( IDialogConstants . OK_ID ) . getEnabled ( ) ) { signIn ( ) ; } } } ; public AptanaSignInDialog ( Shell parentShell ) { this ( parentShell , false ) ; } public AptanaSignInDialog ( Shell parentShell , boolean allowAnonymous ) { super ( parentShell ) ; setShellStyle ( getDefaultOrientation ( ) | SWT . RESIZE | SWT . APPLICATION_MODAL | SWT . DIALOG_TRIM ) ; setHelpAvailable ( false ) ; this . allowAnonymous = allowAnonymous ; } public void modifyText ( ModifyEvent e ) { setErrorMessage ( null ) ; getButton ( IDialogConstants . OK_ID ) . setEnabled ( true ) ; } public void widgetSelected ( SelectionEvent e ) { Object source = e . getSource ( ) ; if ( source == forgotPassword ) { CoreUIUtils . openBrowserURL ( FORGOT_PASSWORD ) ; } else if ( source == whatIsIt ) { CoreUIUtils . openBrowserURL ( WHAT_IS_APTANA_ID ) ; } else if ( source == createId ) { CoreUIUtils . openBrowserURL ( CREATE_ID ) ; } } public void widgetDefaultSelected ( SelectionEvent e ) { } protected void configureShell ( Shell newShell ) { super . configureShell ( newShell ) ; newShell . setText ( Messages . AptanaSignInWidget_Title ) ; } protected Control createDialogArea ( Composite parent ) { setTitle ( Messages . AptanaSignInWidget_LBL_MainTitle ) ; setTitleImage ( CoreUIPlugin . getImage ( IMAGE ) ) ; setMessage ( Messages . AptanaSignInWidget_LBL_Subtitle ) ; Composite main = new Composite ( parent , SWT . NONE ) ; main . setLayout ( new GridLayout ( 2 , false ) ) ; GridData gridData = new GridData ( SWT . FILL , SWT . FILL , true , true ) ; main . setLayoutData ( gridData ) ; createMiddleColumn ( main ) ; createRightColumn ( main ) ; return main ; } protected void createButtonsForButtonBar ( Composite parent ) { if ( allowAnonymous ) { createButton ( parent , IDialogConstants . IGNORE_ID , Messages . AptanaSignInDialog_LBL_Anonymous , false ) ; } super . createButtonsForButtonBar ( parent ) ; getButton ( IDialogConstants . OK_ID ) . setText ( Messages . AptanaSignInWidget_LBL_SignIn ) ; getButton ( IDialogConstants . OK_ID ) . setEnabled ( false ) ; } protected Point getInitialSize ( ) { return getShell ( ) . computeSize ( SWT . DEFAULT , SWT . DEFAULT , true ) ; } protected void buttonPressed ( int buttonId ) { if ( IDialogConstants . IGNORE_ID == buttonId ) { setReturnCode ( IDialogConstants . IGNORE_ID ) ; close ( ) ; } else { super . buttonPressed ( buttonId ) ; } } protected void okPressed ( ) { signIn ( ) ; } private void signIn ( ) { if ( ! validate ( ) ) { return ; } final User user = new User ( username . getText ( ) . toLowerCase ( ) , password . getText ( ) , null , null , null , null , null ) ; try { user . setDefaultLocation ( new URL ( AptanaUser . LOGINS ) ) ; user . setServiceProvider ( new RESTServiceProvider ( ) ) ; user . setRequestBuilder ( new UserRequestBuilder ( ) ) ; AptanaUser . signOut ( ) ; user . update ( ) ; if ( user . hasLocation ( ) ) { user . update ( ) ; AptanaUser . signIn ( username . getText ( ) . toLowerCase ( ) , password . getText ( ) , user . getLocation ( ) , user . getId ( ) ) ; super . okPressed ( ) ; return ; } if ( user . getLastServiceErrors ( ) != null && user . getLastServiceErrors ( ) . getItems ( ) . length > 0 ) { String message = user . getLastServiceErrors ( ) . getItems ( ) [ 0 ] . getMessage ( ) ; if ( message . length ( ) > 1 ) { message = message . substring ( 0 , 1 ) . toUpperCase ( ) + message . substring ( 1 , message . length ( ) ) ; } setErrorMessage ( message ) ; } else if ( ! OnlineDetectionService . isAvailable ( new URL ( WHAT_IS_APTANA_ID ) ) ) { setErrorMessage ( Messages . AptanaSignInDialog_Label_ErrorOffline ) ; } else { setErrorMessage ( Messages . AptanaSignInWidget_Label_ErrorVerified ) ; } } catch ( MalformedURLException e ) { setErrorMessage ( Messages . AptanaSignInWidget_Label_ErrorVerified ) ; } getButton ( IDialogConstants . OK_ID ) . setEnabled ( false ) ; } private boolean validate ( ) { if ( username . getText ( ) . trim ( ) . length ( ) == 0 ) { setErrorMessage ( Messages . AptanaSignInWidget_LBL_ErrorUsername ) ; getButton ( IDialogConstants . OK_ID ) . setEnabled ( false ) ; return false ; } if ( password . getText ( ) . trim ( ) . length ( ) == 0 ) { setErrorMessage ( Messages . AptanaSignInWidget_LBL_ErrorPassword ) ; getButton ( IDialogConstants . OK_ID ) . setEnabled ( false ) ; return false ; } setErrorMessage ( null ) ; getButton ( IDialogConstants . OK_ID ) . setEnabled ( true ) ; return true ; } private Composite createMiddleColumn ( Composite parent ) { Composite middle = new Composite ( parent , SWT . NONE ) ; middle . setLayout ( new GridLayout ( 2 , false ) ) ; middle . setLayoutData ( new GridData ( SWT . FILL , SWT . FILL , true , true ) ) ; Label label = new Label ( middle , SWT . LEFT ) ; label . setText ( Messages . AptanaSignInWidget_LBL_Username ) ; label . setLayoutData ( new GridData ( SWT . FILL , SWT . FILL , false , false ) ) ; username = new Text ( middle , SWT . BORDER | SWT . SINGLE ) ; GridData gridData = new GridData ( SWT . FILL , SWT . FILL , true , false ) ; gridData . widthHint = 125 ; username . setLayoutData ( gridData ) ; username . addModifyListener ( this ) ; username . addKeyListener ( signInKeyAdapter ) ; username . forceFocus ( ) ; label = new Label ( middle , SWT . LEFT ) ; label . setText ( Messages . AptanaSignInWidget_LBL_Password ) ; label . setLayoutData ( new GridData ( SWT . FILL , SWT . FILL , false , false ) ) ; password = new Text ( middle , SWT . BORDER | SWT . SINGLE | SWT . PASSWORD ) ; gridData = new GridData ( SWT . FILL , SWT . FILL , true , false ) ; gridData . widthHint = 125 ; password . setLayoutData ( gridData ) ; password . addModifyListener ( this ) ; password . addKeyListener ( signInKeyAdapter ) ; return middle ; } private Composite createRightColumn ( Composite parent ) { Composite right = new Composite ( parent , SWT . NONE ) ; right . setLayout ( new GridLayout ( ) ) ; right . setLayoutData ( new GridData ( SWT . FILL , SWT . FILL , false , true ) ) ; whatIsIt = new Link ( right , SWT . NONE ) ; whatIsIt . setText ( "<a>" + Messages . AptanaSignInWidget_IDLink + "</a>" ) ; whatIsIt . addSelectionListener ( this ) ; forgotPassword = new Link ( right , SWT . NONE ) ; forgotPassword . setText ( "<a>" + Messages . AptanaSignInWidget_PasswordLink + "</a>" ) ; forgotPassword . addSelectionListener ( this ) ; createId = new Link ( right , SWT . NONE ) ; createId . setText ( "<a>" + Messages . AptanaSignInWidget_LBL_CreateID + "</a>" ) ; GridData gridData = new GridData ( SWT . FILL , SWT . FILL , true , false ) ; gridData . verticalIndent = 5 ; createId . setLayoutData ( gridData ) ; createId . addSelectionListener ( this ) ; return right ; } } 
=======
public class MetaName extends AbstractDatatype { private static final String [ ] VALID_NAMES = { "aglsterms.act" , "aglsterms.accessibility" , "aglsterms.accessmode" , "aglsterms.aggregationlevel" , "aglsterms.availability" , "aglsterms.case" , "aglsterms.category" , "aglsterms.datelicensed" , "aglsterms.documenttype" , "aglsterms.function" , "aglsterms.isbasisfor" , "aglsterms.isbasedon" , "aglsterms.jurisdiction" , "aglsterms.mandate" , "aglsterms.protectivemarking" , "aglsterms.regulation" , "aglsterms.servicetype" , "alexaverifyid" , "apple-mobile-web-app-capable" , "apple-mobile-web-app-status-bar-style" , "application-name" , "author" , "baiduspider" , "bug.component" , "bug.product" , "bug.short_desc" , "csrf-param" , "csrf-token" , "dc.date.issued" , "dc.language" , "dcterms.abstract" , "dcterms.accessrights" , "dcterms.accrualmethod" , "dcterms.accrualperiodicity" , "dcterms.accrualpolicy" , "dcterms.alternative" , "dcterms.audience" , "dcterms.available" , "dcterms.bibliographiccitation" , "dcterms.conformsto" , "dcterms.contributor" , "dcterms.coverage" , "dcterms.created" , "dcterms.creator" , "dcterms.date" , "dcterms.dateaccepted" , "dcterms.datecopyrighted" , "dcterms.datesubmitted" , "dcterms.description" , "dcterms.educationlevel" , "dcterms.extent" , "dcterms.format" , "dcterms.hasformat" , "dcterms.haspart" , "dcterms.hasversion" , "dcterms.identifier" , "dcterms.instructionalmethod" , "dcterms.isformatof" , "dcterms.ispartof" , "dcterms.isreferencedby" , "dcterms.isreplacedby" , "dcterms.isrequiredby" , "dcterms.issued" , "dcterms.isversionof" , "dcterms.language" , "dcterms.license" , "dcterms.mediator" , "dcterms.medium" , "dcterms.modified" , "dcterms.provenance" , "dcterms.publisher" , "dcterms.references" , "dcterms.relation" , "dcterms.replaces" , "dcterms.requires" , "dcterms.rights" , "dcterms.rightsholder" , "dcterms.source" , "dcterms.spatial" , "dcterms.subject" , "dcterms.tableofcontents" , "dcterms.temporal" , "dcterms.title" , "dcterms.type" , "dcterms.valid" , "description" , "designer" , "essaydirectory" , "format-detection" , "fragment" , "generator" , "geo.a1" , "geo.a2" , "geo.a3" , "geo.country" , "geo.lmk" , "geo.placename" , "geo.position" , "geo.region" , "globrix.bathrooms" , "globrix.bedrooms" , "globrix.condition" , "globrix.features" , "globrix.instruction" , "globrix.latitude" , "globrix.longitude " , "globrix.outsidespace" , "globrix.parking" , "globrix.period" , "globrix.poa" , "globrix.postcode" , "globrix.price" , "globrix.priceproximity" , "globrix.tenure" , "globrix.type" , "globrix.underoffer" , "google-site-verification" , "googlebot" , "icbm" , "itemsperpage" , "keywords" , "meta_date" , "mobile-web-app-capable" , "msapplication-config" , "msapplication-navbutton-color" , "msapplication-starturl" , "msapplication-task" , "msapplication-tilecolor" , "msapplication-tileimage" , "msapplication-tooltip" , "msapplication-window" , "msvalidate.01" , "norton-safeweb-site-verification" , "rating" , "referrer" , "review_date" , "revisit-after" , "rights-standard" , "robots" , "skype_toolbar" , "slurp" , "startindex" , "startver" , "teoma" , "twitter:app:country " , "twitter:app:id:googleplay" , "twitter:app:id:ipad " , "twitter:app:id:iphone" , "twitter:app:url:googleplay" , "twitter:app:url:ipad" , "twitter:app:url:iphone" , "twitter:card" , "twitter:creator" , "twitter:creator:id" , "twitter:description" , "twitter:domain" , "twitter:image" , "twitter:image0" , "twitter:image1" , "twitter:image2" , "twitter:image3" , "twitter:image:height" , "twitter:image:src" , "twitter:image:width" , "twitter:site" , "twitter:site:id" , "twitter:title" , "twitter:url" , "verify-v1" , "viewport" , "wot-verification" , "wt.ac" , "wt.ad" , "wt.cg_n" , "wt.cg_s" , "wt.mc_id" , "wt.si_p" , "wt.sv" , "wt.ti" , "y_key" , "yandex-verification" , "zoomcategory" , "zoomimage" , "zoompageboost" , "zoomtitle" , "zoomwords" } ; public static final MetaName THE_INSTANCE = new MetaName ( ) ; private MetaName ( ) { super ( ) ; } @ Override public void checkValid ( CharSequence literal ) throws DatatypeException { String token = toAsciiLowerCase ( literal ) ; if ( Arrays . binarySearch ( VALID_NAMES , token ) < 0 ) { throw newDatatypeException ( "Keyword " , token , " is not registered." ) ; } } @ Override public String getName ( ) { return "metadata name" ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
