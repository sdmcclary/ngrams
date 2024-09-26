<<<<<<< HEAD
public abstract class ProblemsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage , IAddItemListener { private static Image fWarningImage = UnifiedEditorsPlugin . getImageDescriptor ( "icons/warning.png" ) . createImage ( ) ; protected IWorkbench workbench ; private TableEditor _tableEditor ; private CheckboxTableViewer _validatorViewer ; protected Control createContents ( Composite parent ) { Composite composite = new Composite ( parent , SWT . NULL ) ; GridLayout layout = new GridLayout ( 1 , true ) ; layout . marginHeight = 0 ; layout . marginWidth = 0 ; composite . setLayout ( layout ) ; composite . setLayoutData ( new GridData ( GridData . FILL_BOTH ) ) ; createValidatorPreferenceControls ( composite ) ; Group errorFilter = new Group ( composite , SWT . NONE ) ; GridLayout gridLayout = new GridLayout ( ) ; gridLayout . numColumns = 1 ; errorFilter . setLayout ( gridLayout ) ; errorFilter . setLayoutData ( new GridData ( GridData . FILL , GridData . FILL , true , true ) ) ; errorFilter . setText ( CodeAssistMessages . ProblemsPreferencePage_ProblemViewFilters ) ; _tableEditor = new TableEditor ( errorFilter , SWT . NULL , true ) ; _tableEditor . setDescription ( CodeAssistMessages . ProblemsPreferencePage_ProblemsDescription ) ; GridData data = new GridData ( GridData . FILL_BOTH ) ; data . heightHint = 150 ; _tableEditor . setLayoutData ( data ) ; IPreferenceStore store = doGetPreferenceStore ( ) ; String editors = store . getString ( doGetPreferenceString ( ) ) ; ErrorDescriptor [ ] descriptors = ErrorDescriptor . deserializeErrorDescriptors ( editors ) ; List < Object > items = new ArrayList < Object > ( Arrays . asList ( descriptors ) ) ; _tableEditor . setLabelProvider ( new TableLabelProvider ( ) ) ; _tableEditor . addAddItemListener ( this ) ; new TableColumn ( _tableEditor . getTable ( ) , SWT . LEFT ) ; _tableEditor . setItems ( items ) ; workbench . getHelpSystem ( ) . setHelp ( parent , IWorkbenchHelpContextIds . FILE_EDITORS_PREFERENCE_PAGE ) ; applyDialogFont ( _tableEditor ) ; return composite ; } private void createValidatorPreferenceControls ( Composite parent ) { Composite displayArea = new Composite ( parent , SWT . NONE ) ; GridLayout layout = new GridLayout ( ) ; layout . numColumns = 1 ; layout . makeColumnsEqualWidth = false ; layout . marginHeight = 0 ; layout . marginWidth = 0 ; layout . verticalSpacing = 10 ; layout . horizontalSpacing = 0 ; displayArea . setLayout ( layout ) ; GridData data = new GridData ( GridData . FILL , GridData . FILL , true , true ) ; displayArea . setLayoutData ( data ) ; Group validators = new Group ( displayArea , SWT . NONE ) ; GridLayout gridLayout = new GridLayout ( ) ; gridLayout . numColumns = 1 ; validators . setLayout ( gridLayout ) ; validators . setLayoutData ( new GridData ( GridData . FILL , GridData . FILL , true , true ) ) ; validators . setText ( CodeAssistMessages . ProblemsPreferencePage_Validators ) ; Table table = new Table ( validators , SWT . CHECK | SWT . BORDER ) ; table . setFont ( parent . getFont ( ) ) ; table . setLayoutData ( new GridData ( GridData . FILL_BOTH ) ) ; _validatorViewer = new CheckboxTableViewer ( table ) ; _validatorViewer . setContentProvider ( new ArrayContentProvider ( ) ) ; _validatorViewer . setLabelProvider ( new LabelProvider ( ) ) ; data = new GridData ( GridData . FILL , GridData . FILL , true , true ) ; data . heightHint = 100 ; data . widthHint = 140 ; table . setLayoutData ( data ) ; addvalidators ( ) ; String stored_validators = this . getPreferenceStore ( ) . getString ( IPreferenceConstants . VALIDATORS_LIST ) ; restoreCheckedValidators ( stored_validators ) ; } private void restoreCheckedValidators ( String stored_validators ) { if ( stored_validators . equals ( IPreferenceConstants . VALIDATORS_NONE ) ) { } else if ( stored_validators . length ( ) == 0 ) { List < String > validatorNames = ( List < String > ) _validatorViewer . getInput ( ) ; _validatorViewer . setCheckedElements ( validatorNames . toArray ( new String [ validatorNames . size ( ) ] ) ) ; } else { String [ ] validators = stored_validators . split ( "," ) ; List < String > validatorNames = ( List < String > ) _validatorViewer . getInput ( ) ; String name ; int size = validatorNames . size ( ) ; int j ; for ( int i = 0 ; i < size ; ++ i ) { name = validatorNames . get ( i ) ; for ( j = 0 ; j < validators . length ; ++ j ) { if ( name . equals ( validators [ j ] ) ) { _validatorViewer . setChecked ( name , true ) ; break ; } } if ( j == validators . length ) { _validatorViewer . setChecked ( name , false ) ; } } } } private void addvalidators ( ) { String mimeType = getMimeType ( ) ; ValidatorManager validatiorManager = ValidatorManager . getInstance ( ) ; ValidatorRef [ ] validators = validatiorManager . getValidators ( mimeType ) ; if ( validators != null && validators . length > 0 ) { List < String > validatorNames = new ArrayList < String > ( ) ; for ( int i = 0 ; i < validators . length ; i ++ ) { validatorNames . add ( validators [ i ] . getName ( ) ) ; } _validatorViewer . setInput ( validatorNames ) ; } } protected abstract String getMimeType ( ) ; public void dispose ( ) { _tableEditor . removeAddItemListener ( this ) ; super . dispose ( ) ; } protected abstract IPreferenceStore doGetPreferenceStore ( ) ; protected abstract Plugin doGetPlugin ( ) ; protected String doGetPreferenceString ( ) { return IPreferenceConstants . IGNORE_PROBLEMS ; } public void init ( IWorkbench aWorkbench ) { this . workbench = aWorkbench ; } protected void performDefaults ( ) { super . performDefaults ( ) ; IPreferenceStore store = doGetPreferenceStore ( ) ; String editors = store . getDefaultString ( doGetPreferenceString ( ) ) ; ErrorDescriptor [ ] descriptors = ErrorDescriptor . deserializeErrorDescriptors ( editors ) ; List < Object > items = new ArrayList < Object > ( Arrays . asList ( descriptors ) ) ; _tableEditor . setItems ( items ) ; String stored_validators = this . getPreferenceStore ( ) . getDefaultString ( IPreferenceConstants . VALIDATORS_LIST ) ; restoreCheckedValidators ( stored_validators ) ; } public Object addItem ( ) { ErrorDescriptorInfoDialog dialog = new ErrorDescriptorInfoDialog ( getControl ( ) . getShell ( ) ) ; if ( dialog . open ( ) == Window . OK ) { String message = dialog . getMessage ( ) ; ErrorDescriptor ed = new ErrorDescriptor ( ) ; ed . setMessage ( message ) ; return ed ; } return null ; } public boolean performOk ( ) { IPreferenceStore store = doGetPreferenceStore ( ) ; List < Object > items = _tableEditor . getItems ( ) ; store . setValue ( doGetPreferenceString ( ) , ErrorDescriptor . serializeErrorDescriptors ( ( ErrorDescriptor [ ] ) items . toArray ( new ErrorDescriptor [ 0 ] ) ) ) ; List < String > validatorItems = new ArrayList < String > ( ) ; Object [ ] checkedItems = _validatorViewer . getCheckedElements ( ) ; if ( checkedItems . length > 0 ) { for ( int i = 0 ; i < checkedItems . length ; i ++ ) { validatorItems . add ( checkedItems [ i ] . toString ( ) ) ; } store . setValue ( IPreferenceConstants . VALIDATORS_LIST , StringUtils . join ( "," , validatorItems . toArray ( new String [ 0 ] ) ) ) ; } else { store . setValue ( IPreferenceConstants . VALIDATORS_LIST , IPreferenceConstants . VALIDATORS_NONE ) ; } doGetPlugin ( ) . savePluginPreferences ( ) ; return true ; } public Object editItem ( Object item ) { if ( item instanceof ErrorDescriptor ) { ErrorDescriptorInfoDialog dialog = new ErrorDescriptorInfoDialog ( getControl ( ) . getShell ( ) ) ; ErrorDescriptor ed = ( ErrorDescriptor ) item ; dialog . setItem ( ed ) ; if ( dialog . open ( ) == Window . OK ) { ed . setMessage ( dialog . getMessage ( ) ) ; return ed ; } return null ; } else { return null ; } } public class TableLabelProvider implements ITableLabelProvider { public Image getColumnImage ( Object element , int columnIndex ) { Image image = null ; switch ( columnIndex ) { case 0 : image = fWarningImage ; break ; default : break ; } return image ; } public String getColumnText ( Object element , int columnIndex ) { String name = StringUtils . EMPTY ; ErrorDescriptor ed = ( ErrorDescriptor ) element ; switch ( columnIndex ) { case 0 : name = ed . getMessage ( ) ; break ; case 1 : name = ed . getFolderPath ( ) ; break ; case 2 : name = ed . getFileName ( ) ; break ; default : break ; } return name ; } public void addListener ( ILabelProviderListener listener ) { } public void dispose ( ) { } public boolean isLabelProperty ( Object element , String property ) { return false ; } public void removeListener ( ILabelProviderListener listener ) { } } } 
=======
public class MetaName extends AbstractDatatype { private static final String [ ] VALID_NAMES = { "aglsterms.act" , "aglsterms.accessibility" , "aglsterms.accessmode" , "aglsterms.aggregationlevel" , "aglsterms.availability" , "aglsterms.case" , "aglsterms.category" , "aglsterms.datelicensed" , "aglsterms.documenttype" , "aglsterms.function" , "aglsterms.isbasisfor" , "aglsterms.isbasedon" , "aglsterms.jurisdiction" , "aglsterms.mandate" , "aglsterms.protectivemarking" , "aglsterms.regulation" , "aglsterms.servicetype" , "alexaverifyid" , "apple-mobile-web-app-capable" , "apple-mobile-web-app-status-bar-style" , "application-name" , "author" , "baiduspider" , "bug.component" , "bug.product" , "bug.short_desc" , "csrf-param" , "csrf-token" , "dc.date.issued" , "dc.language" , "dcterms.abstract" , "dcterms.accessrights" , "dcterms.accrualmethod" , "dcterms.accrualperiodicity" , "dcterms.accrualpolicy" , "dcterms.alternative" , "dcterms.audience" , "dcterms.available" , "dcterms.bibliographiccitation" , "dcterms.conformsto" , "dcterms.contributor" , "dcterms.coverage" , "dcterms.created" , "dcterms.creator" , "dcterms.date" , "dcterms.dateaccepted" , "dcterms.datecopyrighted" , "dcterms.datesubmitted" , "dcterms.description" , "dcterms.educationlevel" , "dcterms.extent" , "dcterms.format" , "dcterms.hasformat" , "dcterms.haspart" , "dcterms.hasversion" , "dcterms.identifier" , "dcterms.instructionalmethod" , "dcterms.isformatof" , "dcterms.ispartof" , "dcterms.isreferencedby" , "dcterms.isreplacedby" , "dcterms.isrequiredby" , "dcterms.issued" , "dcterms.isversionof" , "dcterms.language" , "dcterms.license" , "dcterms.mediator" , "dcterms.medium" , "dcterms.modified" , "dcterms.provenance" , "dcterms.publisher" , "dcterms.references" , "dcterms.relation" , "dcterms.replaces" , "dcterms.requires" , "dcterms.rights" , "dcterms.rightsholder" , "dcterms.source" , "dcterms.spatial" , "dcterms.subject" , "dcterms.tableofcontents" , "dcterms.temporal" , "dcterms.title" , "dcterms.type" , "dcterms.valid" , "description" , "designer" , "essaydirectory" , "format-detection" , "fragment" , "generator" , "geo.a1" , "geo.a2" , "geo.a3" , "geo.country" , "geo.lmk" , "geo.placename" , "geo.position" , "geo.region" , "globrix.bathrooms" , "globrix.bedrooms" , "globrix.condition" , "globrix.features" , "globrix.instruction" , "globrix.latitude" , "globrix.longitude " , "globrix.outsidespace" , "globrix.parking" , "globrix.period" , "globrix.poa" , "globrix.postcode" , "globrix.price" , "globrix.priceproximity" , "globrix.tenure" , "globrix.type" , "globrix.underoffer" , "google-site-verification" , "googlebot" , "icbm" , "itemsperpage" , "keywords" , "meta_date" , "mobile-web-app-capable" , "msapplication-config" , "msapplication-navbutton-color" , "msapplication-starturl" , "msapplication-task" , "msapplication-tilecolor" , "msapplication-tileimage" , "msapplication-tooltip" , "msapplication-window" , "msvalidate.01" , "norton-safeweb-site-verification" , "rating" , "referrer" , "review_date" , "revisit-after" , "rights-standard" , "robots" , "skype_toolbar" , "slurp" , "startindex" , "startver" , "teoma" , "twitter:app:country " , "twitter:app:id:googleplay" , "twitter:app:id:ipad " , "twitter:app:id:iphone" , "twitter:app:url:googleplay" , "twitter:app:url:ipad" , "twitter:app:url:iphone" , "twitter:card" , "twitter:creator" , "twitter:creator:id" , "twitter:description" , "twitter:domain" , "twitter:image" , "twitter:image0" , "twitter:image1" , "twitter:image2" , "twitter:image3" , "twitter:image:height" , "twitter:image:src" , "twitter:image:width" , "twitter:site" , "twitter:site:id" , "twitter:title" , "twitter:url" , "verify-v1" , "viewport" , "wot-verification" , "wt.ac" , "wt.ad" , "wt.cg_n" , "wt.cg_s" , "wt.mc_id" , "wt.si_p" , "wt.sv" , "wt.ti" , "y_key" , "yandex-verification" , "zoomcategory" , "zoomimage" , "zoompageboost" , "zoomtitle" , "zoomwords" } ; public static final MetaName THE_INSTANCE = new MetaName ( ) ; private MetaName ( ) { super ( ) ; } @ Override public void checkValid ( CharSequence literal ) throws DatatypeException { String token = toAsciiLowerCase ( literal ) ; if ( Arrays . binarySearch ( VALID_NAMES , token ) < 0 ) { throw newDatatypeException ( "Keyword " , token , " is not registered." ) ; } } @ Override public String getName ( ) { return "metadata name" ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
