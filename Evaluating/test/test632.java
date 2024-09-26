public class GeneralPreferencePage extends TabbedFieldEditorPreferencePage implements IWorkbenchPreferencePage { private Scale pianoKeySlider ; protected ColorSelector occurrenceColor ; protected Button enableOccurrences ; private Button spaces ; private Button tabs ; private IPropertyChangeListener tabWidthListener = new IPropertyChangeListener ( ) { public void propertyChange ( PropertyChangeEvent event ) { if ( spaces != null && ! spaces . isDisposed ( ) ) { spaces . setText ( StringUtils . format ( Messages . GeneralPreferencePage_UseSpaces , event . getNewValue ( ) ) ) ; } } } ; public GeneralPreferencePage ( ) { super ( GRID ) ; setPreferenceStore ( UnifiedEditorsPlugin . getDefault ( ) . getPreferenceStore ( ) ) ; setDescription ( Messages . GeneralPreferencePage_PreferenceDescription ) ; } public void createFieldEditors ( ) { addTab ( Messages . GeneralPreferencePage_General ) ; Composite appearanceComposite = getFieldEditorParent ( ) ; Composite group = com . aptana . ide . core . ui . preferences . GeneralPreferencePage . createGroup ( appearanceComposite , Messages . GeneralPreferencePage_Formatting ) ; Composite occurrenceComp = new Composite ( group , SWT . NONE ) ; GridLayout occLayout = new GridLayout ( 2 , false ) ; occLayout . marginWidth = 0 ; occLayout . marginHeight = 0 ; occurrenceComp . setLayout ( occLayout ) ; occurrenceComp . setLayoutData ( new GridData ( SWT . FILL , SWT . FILL , true , false ) ) ; enableOccurrences = new Button ( occurrenceComp , SWT . CHECK ) ; enableOccurrences . addSelectionListener ( new SelectionAdapter ( ) { public void widgetSelected ( SelectionEvent e ) { occurrenceColor . setEnabled ( enableOccurrences . getSelection ( ) ) ; } } ) ; enableOccurrences . setSelection ( getPreferenceStore ( ) . getBoolean ( IPreferenceConstants . COLORIZER_TEXT_HIGHLIGHT_ENABLED ) ) ; enableOccurrences . setText ( Messages . GeneralPreferencePage_MarkOccurrences ) ; occurrenceColor = new ColorSelector ( occurrenceComp ) ; occurrenceColor . setEnabled ( enableOccurrences . getSelection ( ) ) ; occurrenceColor . setColorValue ( PreferenceConverter . getColor ( getPreferenceStore ( ) , IPreferenceConstants . COLORIZER_TEXT_HIGHLIGHT_BACKGROUND_COLOR ) ) ; addField ( new BooleanFieldEditor ( IPreferenceConstants . INSERT_ON_TAB , Messages . GeneralPreferencePage_InsertSelectedProposal , group ) ) ; addField ( new RadioGroupFieldEditor ( AbstractTextEditor . PREFERENCE_NAVIGATION_SMART_HOME_END , Messages . GeneralPreferencePage_HomeEndBehavior , 1 , new String [ ] [ ] { { Messages . GeneralPreferencePage_ToggleBetween , "true" } , { Messages . GeneralPreferencePage_JumpsStartEnd , "false" } } , appearanceComposite , true ) ) ; group = com . aptana . ide . core . ui . preferences . GeneralPreferencePage . createGroup ( appearanceComposite , Messages . GeneralPreferencePage_LBL_Colorization ) ; Composite pianoKeyComp = new Composite ( group , SWT . NONE ) ; GridLayout pkcLayout = new GridLayout ( 3 , false ) ; pkcLayout . marginWidth = 0 ; pkcLayout . marginHeight = 0 ; pianoKeyComp . setLayout ( pkcLayout ) ; pianoKeyComp . setLayoutData ( new GridData ( SWT . FILL , SWT . FILL , true , false ) ) ; Label pianoKeyLabel = new Label ( pianoKeyComp , SWT . LEFT ) ; pianoKeyLabel . setText ( Messages . GeneralPreferencePage_LBL_PianoKeyColorDifference ) ; GridData pklData = new GridData ( SWT . FILL , SWT . FILL , true , false ) ; pklData . horizontalSpan = 3 ; pianoKeyLabel . setLayoutData ( pklData ) ; Label less = new Label ( pianoKeyComp , SWT . LEFT ) ; less . setText ( Messages . GeneralPreferencePage_LBL_Less ) ; pianoKeySlider = new Scale ( pianoKeyComp , SWT . HORIZONTAL ) ; pianoKeySlider . setIncrement ( 5 ) ; pianoKeySlider . setMinimum ( 1 ) ; pianoKeySlider . setMaximum ( 50 ) ; pianoKeySlider . setSelection ( getPreferenceStore ( ) . getInt ( IPreferenceConstants . PIANO_KEY_DIFFERENCE ) ) ; Label more = new Label ( pianoKeyComp , SWT . LEFT ) ; more . setText ( Messages . GeneralPreferencePage_LBL_More ) ; Composite wsGroup = com . aptana . ide . core . ui . preferences . GeneralPreferencePage . createGroup ( appearanceComposite , Messages . GeneralPreferencePage_TabInsertion ) ; Composite wsComp = new Composite ( wsGroup , SWT . NONE ) ; GridLayout wsLayout = new GridLayout ( 3 , false ) ; wsLayout . marginWidth = 0 ; wsLayout . marginHeight = 0 ; wsComp . setLayout ( wsLayout ) ; wsComp . setLayoutData ( new GridData ( SWT . FILL , SWT . FILL , true , false ) ) ; tabs = new Button ( wsComp , SWT . RADIO ) ; Composite spaceComp = new Composite ( wsComp , SWT . NONE ) ; wsLayout = new GridLayout ( 2 , false ) ; wsLayout . marginWidth = 0 ; wsLayout . marginHeight = 0 ; wsLayout . horizontalSpacing = 0 ; spaceComp . setLayout ( wsLayout ) ; spaceComp . setLayoutData ( new GridData ( SWT . FILL , SWT . FILL , true , false ) ) ; spaces = new Button ( spaceComp , SWT . RADIO ) ; final Link currentTabSize = new Link ( spaceComp , SWT . NONE ) ; IPreferenceStore store = EditorsPlugin . getDefault ( ) . getPreferenceStore ( ) ; int size = store . getInt ( AbstractDecoratedTextEditorPreferenceConstants . EDITOR_TAB_WIDTH ) ; spaces . setText ( StringUtils . format ( Messages . GeneralPreferencePage_UseSpaces , size ) ) ; tabs . setText ( Messages . GeneralPreferencePage_UseTabs ) ; store . addPropertyChangeListener ( tabWidthListener ) ; currentTabSize . setText ( Messages . GeneralPreferencePage_EditLink ) ; currentTabSize . addSelectionListener ( new SelectionAdapter ( ) { public void widgetSelected ( SelectionEvent e ) { ( ( IWorkbenchPreferenceContainer ) getContainer ( ) ) . openPage ( "org.eclipse.ui.preferencePages.GeneralTextEditor" , null ) ; } } ) ; boolean useSpaces = getPreferenceStore ( ) . getBoolean ( IPreferenceConstants . INSERT_SPACES_FOR_TABS ) ; spaces . setSelection ( useSpaces ) ; tabs . setSelection ( ! useSpaces ) ; tabs . addSelectionListener ( new SelectionAdapter ( ) { public void widgetSelected ( SelectionEvent e ) { spaces . setSelection ( ! tabs . getSelection ( ) ) ; } } ) ; spaces . addSelectionListener ( new SelectionAdapter ( ) { public void widgetSelected ( SelectionEvent e ) { tabs . setSelection ( ! spaces . getSelection ( ) ) ; } } ) ; Link link = new Link ( appearanceComposite , SWT . NONE ) ; link . setText ( Messages . GeneralPreferencePage_GeneralTextEditorPrefLink ) ; link . addSelectionListener ( new SelectionAdapter ( ) { public void widgetSelected ( SelectionEvent e ) { ( ( IWorkbenchPreferenceContainer ) getContainer ( ) ) . openPage ( "org.eclipse.ui.preferencePages.GeneralTextEditor" , null ) ; } } ) ; addTab ( Messages . GeneralPreferencePage_Advanced ) ; appearanceComposite = getFieldEditorParent ( ) ; addField ( new BooleanFieldEditor ( IPreferenceConstants . ENABLE_WORD_WRAP , Messages . GeneralPreferencePage_EnableWordWrap , appearanceComposite ) ) ; appearanceComposite = getFieldEditorParent ( ) ; addField ( new IntegerFieldEditor ( IPreferenceConstants . COLORIZER_MAXCOLUMNS , Messages . GeneralPreferencePage_MaxColorizeColumns , appearanceComposite , 4 ) ) ; group = com . aptana . ide . core . ui . preferences . GeneralPreferencePage . createGroup ( appearanceComposite , Messages . GeneralPreferencePage_CodeAssist ) ; addField ( new IntegerFieldEditor ( IPreferenceConstants . CONTENT_ASSIST_DELAY , Messages . GeneralPreferencePage_DelayBeforeShowing , group , 4 ) ) ; } public void init ( IWorkbench workbench ) { } public void dispose ( ) { IPreferenceStore store = EditorsPlugin . getDefault ( ) . getPreferenceStore ( ) ; store . removePropertyChangeListener ( tabWidthListener ) ; super . dispose ( ) ; } protected void performDefaults ( ) { enableOccurrences . setSelection ( getPreferenceStore ( ) . getDefaultBoolean ( IPreferenceConstants . COLORIZER_TEXT_HIGHLIGHT_ENABLED ) ) ; occurrenceColor . setColorValue ( PreferenceConverter . getDefaultColor ( getPreferenceStore ( ) , IPreferenceConstants . COLORIZER_TEXT_HIGHLIGHT_BACKGROUND_COLOR ) ) ; occurrenceColor . setEnabled ( enableOccurrences . getSelection ( ) ) ; boolean useSpaces = getPreferenceStore ( ) . getDefaultBoolean ( IPreferenceConstants . INSERT_SPACES_FOR_TABS ) ; spaces . setSelection ( useSpaces ) ; tabs . setSelection ( ! useSpaces ) ; pianoKeySlider . setSelection ( getPreferenceStore ( ) . getDefaultInt ( IPreferenceConstants . PIANO_KEY_DIFFERENCE ) ) ; super . performDefaults ( ) ; } public boolean performOk ( ) { getPreferenceStore ( ) . setValue ( IPreferenceConstants . COLORIZER_TEXT_HIGHLIGHT_ENABLED , enableOccurrences . getSelection ( ) ) ; getPreferenceStore ( ) . setValue ( IPreferenceConstants . INSERT_SPACES_FOR_TABS , spaces . getSelection ( ) ) ; PreferenceConverter . setValue ( getPreferenceStore ( ) , IPreferenceConstants . COLORIZER_TEXT_HIGHLIGHT_BACKGROUND_COLOR , occurrenceColor . getColorValue ( ) ) ; getPreferenceStore ( ) . setValue ( IPreferenceConstants . PIANO_KEY_DIFFERENCE , pianoKeySlider . getSelection ( ) ) ; LanguageColorizer . fireColorizationEvent ( ) ; return super . performOk ( ) ; } } 