public class TerminalView extends ViewPart implements ITerminalView , ITerminalViewConnectionListener { private static final String PREF_CONNECTORS = "Connectors." ; private static final String STORE_CONNECTION_TYPE = "ConnectionType" ; private static final String STORE_SETTING_SUMMARY = "SettingSummary" ; private static final String STORE_TITLE = "Title" ; public static final String FONT_DEFINITION = "terminal.views.view.font.definition" ; private ISpace space ; private IEntity hostEntity ; protected ITerminalViewControl terminalViewControl ; TerminalViewControlDecorator fCtlDecorator = new TerminalViewControlDecorator ( ) ; protected TerminalViewAction newTerminalAction ; protected TerminalViewAction connectAction ; protected TerminalViewAction scrollLockAction ; protected TerminalViewAction disconnectAction ; protected TerminalViewAction configureAction ; protected TerminalActionCopy fActionEditCopy ; protected TerminalActionCut fActionEditCut ; protected TerminalActionPaste fActionEditPaste ; protected TerminalActionClearAll fActionEditClearAll ; protected TerminalActionSelectAll fActionEditSelectAll ; protected TerminalViewAction toggleInputFieldAction ; protected TerminalPropertyChangeHandler fPropertyChangeHandler ; protected Action selectTerminalAction ; protected Action removeTerminalAction ; protected boolean fMenuAboutToShow ; private SettingsStore fStore ; private final ITerminalViewConnectionManager fMultiConnectionManager = new TerminalViewConnectionManager ( ) ; private final IPropertyChangeListener fPreferenceListener = new IPropertyChangeListener ( ) { public void propertyChange ( PropertyChangeEvent event ) { if ( event . getProperty ( ) . equals ( TerminalPreferencePage . PREF_LIMITOUTPUT ) || event . getProperty ( ) . equals ( TerminalPreferencePage . PREF_BUFFERLINES ) || event . getProperty ( ) . equals ( TerminalPreferencePage . PREF_INVERT_COLORS ) ) { updatePreferences ( ) ; } } } ; private PageBook fPageBook ; class TerminalListener implements ITerminalListener { volatile ITerminalViewConnection fConnection ; void setConnection ( ITerminalViewConnection connection ) { fConnection = connection ; } public void setState ( final TerminalState state ) { runInDisplayThread ( new Runnable ( ) { public void run ( ) { fConnection . setState ( state ) ; if ( fConnection == fMultiConnectionManager . getActiveConnection ( ) ) { updateStatus ( ) ; } } } ) ; } public void setTerminalTitle ( final String title ) { runInDisplayThread ( new Runnable ( ) { public void run ( ) { fConnection . setTerminalTitle ( title ) ; if ( fConnection == fMultiConnectionManager . getActiveConnection ( ) ) { updateSummary ( ) ; } } } ) ; } private void runInDisplayThread ( Runnable runnable ) { if ( Display . findDisplay ( Thread . currentThread ( ) ) != null ) runnable . run ( ) ; else if ( PlatformUI . isWorkbenchRunning ( ) ) PlatformUI . getWorkbench ( ) . getDisplay ( ) . syncExec ( runnable ) ; } } public TerminalView ( ) { Logger . log ( "===============================================================" ) ; fMultiConnectionManager . addListener ( this ) ; } String findUniqueTitle ( String title ) { IWorkbenchPage [ ] pages = getSite ( ) . getWorkbenchWindow ( ) . getPages ( ) ; String id = getViewSite ( ) . getId ( ) ; Set names = new HashSet ( ) ; for ( int i = 0 ; i < pages . length ; i ++ ) { IViewReference [ ] views = pages [ i ] . getViewReferences ( ) ; for ( int j = 0 ; j < views . length ; j ++ ) { IViewReference view = views [ j ] ; if ( id . equals ( view . getId ( ) ) ) { String name = view . getTitle ( ) ; if ( name != null ) names . add ( view . getPartName ( ) ) ; } } } int i = 1 ; String uniqueTitle = title ; while ( true ) { if ( ! names . contains ( uniqueTitle ) ) return uniqueTitle ; uniqueTitle = title + " " + i ++ ; } } private void updatePreferences ( ) { Preferences preferences = Activator . getInstance ( ) . getPluginPreferences ( ) ; int bufferLineLimit = preferences . getInt ( TerminalPreferencePage . PREF_BUFFERLINES ) ; boolean invert = preferences . getBoolean ( TerminalPreferencePage . PREF_INVERT_COLORS ) ; ITerminalViewConnection [ ] conn = fMultiConnectionManager . getConnections ( ) ; for ( int i = 0 ; i < conn . length ; i ++ ) { conn [ i ] . getCtlTerminal ( ) . setBufferLineLimit ( bufferLineLimit ) ; conn [ i ] . getCtlTerminal ( ) . setInvertedColors ( invert ) ; } } public void onTerminalNewTerminal ( ) { Logger . log ( "creating new Terminal instance." ) ; setupControls ( ) ; if ( newConnection ( "New Terminal" ) == null ) { fMultiConnectionManager . removeActive ( ) ; } } public void onTerminalNewView ( ) { try { IViewPart newTerminalView = getSite ( ) . getPage ( ) . showView ( "org.eclipse.tm.terminal.view.TerminalView" , "SecondaryTerminal" + System . currentTimeMillis ( ) , IWorkbenchPage . VIEW_ACTIVATE ) ; if ( newTerminalView instanceof ITerminalView ) { ITerminalConnector c = ( ( TerminalView ) newTerminalView ) . newConnection ( "New Terminal View" ) ; if ( c == null ) { getSite ( ) . getPage ( ) . hideView ( newTerminalView ) ; } } } catch ( PartInitException ex ) { Logger . logException ( ex ) ; } } public void onTerminalConnect ( ) { if ( terminalViewControl . getState ( ) != TerminalState . CLOSED ) return ; if ( terminalViewControl . getTerminalConnector ( ) == null ) setConnector ( showSettingsDialog ( "Settings" ) ) ; terminalViewControl . connectTerminal ( ) ; } private void updateStatus ( ) { updateTerminalConnect ( ) ; updateTerminalDisconnect ( ) ; updateTerminalSettings ( ) ; toggleInputFieldAction . setChecked ( hasCommandInputField ( ) ) ; updateSummary ( ) ; updateTitle ( ) ; } private void updateTitle ( ) { String icon = "terminal_disconnected.png" ; if ( isConnecting ( ) ) icon = "terminal_connecting.png" ; else if ( terminalViewControl . isConnected ( ) ) icon = "terminal_connected.png" ; setTitleImage ( Activator . getInstance ( ) . getImageCache ( ) . get ( "icons/" + icon ) ) ; } private void updateTerminalConnect ( ) { boolean bEnabled = ( terminalViewControl . getState ( ) == TerminalState . CLOSED ) ; connectAction . setEnabled ( bEnabled ) ; } public boolean isConnecting ( ) { return terminalViewControl . getState ( ) == TerminalState . CONNECTING || terminalViewControl . getState ( ) == TerminalState . OPENED ; } public void onTerminalDisconnect ( ) { terminalViewControl . disconnectTerminal ( ) ; } public void updateTerminalDisconnect ( ) { boolean bEnabled = ( ( isConnecting ( ) ) || ( terminalViewControl . isConnected ( ) ) ) ; disconnectAction . setEnabled ( bEnabled ) ; } public void onTerminalSettings ( ) { showSettingsDialog ( null ) ; } public void onTerminalRunAction ( ) { } private ITerminalConnector newConnection ( String title ) { ITerminalConnector c = showSettingsDialog ( title ) ; if ( c != null ) { setConnector ( c ) ; onTerminalConnect ( ) ; } return c ; } private ITerminalConnector showSettingsDialog ( String title ) { ITerminalConnector [ ] connectors = terminalViewControl . getConnectors ( ) ; if ( terminalViewControl . getState ( ) != TerminalState . CLOSED ) connectors = new ITerminalConnector [ 0 ] ; ITerminalConnector c = loadSettings ( new LayeredSettingsStore ( fStore , getPreferenceSettingsStore ( ) ) , connectors ) ; if ( terminalViewControl . getTerminalConnector ( ) != null ) c = terminalViewControl . getTerminalConnector ( ) ; TerminalSettingsDialog dlgTerminalSettings = new TerminalSettingsDialog ( getViewSite ( ) . getShell ( ) , connectors , c ) ; dlgTerminalSettings . setTerminalTitle ( getActiveConnection ( ) . getPartName ( ) ) ; if ( title != null ) dlgTerminalSettings . setTitle ( title ) ; Logger . log ( "opening Settings dialog." ) ; if ( dlgTerminalSettings . open ( ) == Window . CANCEL ) { Logger . log ( "Settings dialog cancelled." ) ; return null ; } Logger . log ( "Settings dialog OK'ed." ) ; saveSettings ( fStore , dlgTerminalSettings . getConnector ( ) ) ; saveSettings ( getPreferenceSettingsStore ( ) , dlgTerminalSettings . getConnector ( ) ) ; setViewTitle ( dlgTerminalSettings . getTerminalTitle ( ) ) ; return dlgTerminalSettings . getConnector ( ) ; } public void setConnector ( ITerminalConnector connector ) { terminalViewControl . setConnector ( connector ) ; } public void updateTerminalSettings ( ) { } private void setViewTitle ( String title ) { setPartName ( title ) ; getActiveConnection ( ) . setPartName ( title ) ; } private void setViewSummary ( String summary ) { setContentDescription ( summary ) ; getViewSite ( ) . getActionBars ( ) . getStatusLineManager ( ) . setMessage ( summary ) ; setTitleToolTip ( getPartName ( ) + ": " + summary ) ; } public void updateSummary ( ) { setViewSummary ( getActiveConnection ( ) . getFullSummary ( ) ) ; } public void onTerminalFontChanged ( ) { Font font = JFaceResources . getFont ( FONT_DEFINITION ) ; ITerminalViewConnection [ ] conn = fMultiConnectionManager . getConnections ( ) ; for ( int i = 0 ; i < conn . length ; i ++ ) { conn [ i ] . getCtlTerminal ( ) . setFont ( font ) ; } } public void createPartControl ( Composite wndParent ) { fPageBook = new PageBook ( wndParent , SWT . NONE ) ; ISettingsStore s = new SettingStorePrefixDecorator ( fStore , "connectionManager" ) ; fMultiConnectionManager . loadState ( s , new ITerminalViewConnectionFactory ( ) { public ITerminalViewConnection create ( ) { return makeViewConnection ( ) ; } } ) ; if ( fMultiConnectionManager . size ( ) == 0 ) { ITerminalViewConnection conn = makeViewConnection ( ) ; fMultiConnectionManager . addConnection ( conn ) ; fMultiConnectionManager . setActiveConnection ( conn ) ; fPageBook . showPage ( terminalViewControl . getRootControl ( ) ) ; } setTerminalControl ( fMultiConnectionManager . getActiveConnection ( ) . getCtlTerminal ( ) ) ; setViewTitle ( findUniqueTitle ( "Terminal" ) ) ; setupActions ( ) ; setupLocalToolBars ( ) ; ITerminalViewConnection [ ] conn = fMultiConnectionManager . getConnections ( ) ; for ( int i = 0 ; i < conn . length ; i ++ ) { setupContextMenus ( conn [ i ] . getCtlTerminal ( ) . getControl ( ) ) ; } setupListeners ( wndParent ) ; legacyLoadState ( ) ; legacySetTitle ( ) ; refresh ( ) ; onTerminalFontChanged ( ) ; } public void dispose ( ) { Logger . log ( "entered." ) ; Activator . getInstance ( ) . getPreferenceStore ( ) . removePropertyChangeListener ( fPreferenceListener ) ; JFaceResources . getFontRegistry ( ) . removeListener ( fPropertyChangeHandler ) ; ITerminalViewConnection [ ] conn = fMultiConnectionManager . getConnections ( ) ; for ( int i = 0 ; i < conn . length ; i ++ ) { conn [ i ] . getCtlTerminal ( ) . disposeTerminal ( ) ; } super . dispose ( ) ; } public void setFocus ( ) { terminalViewControl . setFocus ( ) ; } protected void setupControls ( ) { ITerminalViewConnection conn = makeViewConnection ( ) ; fMultiConnectionManager . addConnection ( conn ) ; fMultiConnectionManager . setActiveConnection ( conn ) ; setupContextMenus ( terminalViewControl . getControl ( ) ) ; } private ITerminalViewConnection makeViewConnection ( ) { ITerminalConnector [ ] connectors = makeConnectors ( ) ; TerminalListener listener = new TerminalListener ( ) ; ITerminalViewControl ctrl = TerminalViewControlFactory . makeControl ( listener , fPageBook , connectors ) ; setTerminalControl ( ctrl ) ; ITerminalViewConnection conn = new TerminalViewConnection ( terminalViewControl ) ; listener . setConnection ( conn ) ; conn . setPartName ( getPartName ( ) ) ; ITerminalConnector connector = loadSettings ( fStore , connectors ) ; ctrl . setConnector ( connector ) ; updatePreferences ( ) ; Activator . getInstance ( ) . getPreferenceStore ( ) . addPropertyChangeListener ( fPreferenceListener ) ; return conn ; } private ITerminalConnector loadSettings ( ISettingsStore store , ITerminalConnector [ ] connectors ) { System . err . println ( "TerminalView.loadSettings" ) ; ITerminalConnector connector = null ; String connectionType = store . get ( STORE_CONNECTION_TYPE ) ; for ( int i = 0 ; i < connectors . length ; i ++ ) { connectors [ i ] . load ( getStore ( store , connectors [ i ] ) ) ; if ( connectors [ i ] . getId ( ) . equals ( connectionType ) ) connector = connectors [ i ] ; } return connector ; } protected ITerminalConnector [ ] makeConnectors ( ) { ITerminalConnector [ ] connectors = TerminalConnectorExtension . makeTerminalConnectors ( ) ; return connectors ; } private PreferenceSettingStore getPreferenceSettingsStore ( ) { return new PreferenceSettingStore ( Activator . getInstance ( ) . getPluginPreferences ( ) , PREF_CONNECTORS ) ; } private void saveSettings ( ISettingsStore store , ITerminalConnector connector ) { if ( connector != null ) { connector . save ( getStore ( store , connector ) ) ; store . put ( STORE_CONNECTION_TYPE , connector . getId ( ) ) ; } } public void init ( IViewSite site , IMemento memento ) throws PartInitException { super . init ( site , memento ) ; fStore = new SettingsStore ( memento ) ; } public void saveState ( IMemento memento ) { super . saveState ( memento ) ; fStore . put ( STORE_TITLE , getPartName ( ) ) ; fMultiConnectionManager . saveState ( new SettingStorePrefixDecorator ( fStore , "connectionManager" ) ) ; fStore . saveState ( memento ) ; } private ISettingsStore getStore ( ISettingsStore store , ITerminalConnector connector ) { return new SettingStorePrefixDecorator ( store , connector . getId ( ) + "." ) ; } protected void setupActions ( ) { selectTerminalAction = new SelectTerminalAction ( fMultiConnectionManager ) ; removeTerminalAction = new RemoveTerminalAction ( fMultiConnectionManager ) ; newTerminalAction = new NewTerminalAction ( this ) ; scrollLockAction = new ScrollLockAction ( this ) ; connectAction = new ConnectAction ( this ) ; disconnectAction = new DisconnectAction ( this ) ; configureAction = new ConfigureAction ( this ) ; fActionEditCopy = new TerminalActionCopy ( fCtlDecorator ) ; fActionEditCut = new TerminalActionCut ( fCtlDecorator ) ; fActionEditPaste = new TerminalActionPaste ( fCtlDecorator ) ; fActionEditClearAll = new TerminalActionClearAll ( fCtlDecorator ) ; fActionEditSelectAll = new TerminalActionSelectAll ( fCtlDecorator ) ; toggleInputFieldAction = new ToggleCommandInputFieldAction ( this ) ; } protected void setupLocalToolBars ( ) { IToolBarManager toolBarMgr = getViewSite ( ) . getActionBars ( ) . getToolBarManager ( ) ; toolBarMgr . add ( connectAction ) ; toolBarMgr . add ( disconnectAction ) ; toolBarMgr . add ( new Separator ( "fixedGroup" ) ) ; toolBarMgr . add ( new Separator ( "fixedGroup" ) ) ; toolBarMgr . add ( configureAction ) ; toolBarMgr . add ( toggleInputFieldAction ) ; toolBarMgr . add ( scrollLockAction ) ; toolBarMgr . add ( new Separator ( "fixedGroup" ) ) ; toolBarMgr . add ( selectTerminalAction ) ; toolBarMgr . add ( newTerminalAction ) ; toolBarMgr . add ( removeTerminalAction ) ; } protected void setupContextMenus ( Control ctlText ) { MenuManager menuMgr ; Menu menu ; TerminalContextMenuHandler contextMenuHandler ; menuMgr = new MenuManager ( "#PopupMenu" ) ; menu = menuMgr . createContextMenu ( ctlText ) ; loadContextMenus ( menuMgr ) ; contextMenuHandler = new TerminalContextMenuHandler ( ) ; ctlText . setMenu ( menu ) ; menuMgr . addMenuListener ( contextMenuHandler ) ; menu . addMenuListener ( contextMenuHandler ) ; } protected void loadContextMenus ( IMenuManager menuMgr ) { menuMgr . add ( fActionEditCopy ) ; menuMgr . add ( fActionEditPaste ) ; menuMgr . add ( new Separator ( ) ) ; menuMgr . add ( fActionEditClearAll ) ; menuMgr . add ( fActionEditSelectAll ) ; menuMgr . add ( new Separator ( ) ) ; menuMgr . add ( toggleInputFieldAction ) ; menuMgr . add ( scrollLockAction ) ; menuMgr . add ( new Separator ( "Additions" ) ) ; } protected void setupListeners ( Composite wndParent ) { fPropertyChangeHandler = new TerminalPropertyChangeHandler ( ) ; JFaceResources . getFontRegistry ( ) . addListener ( fPropertyChangeHandler ) ; } protected class TerminalContextMenuHandler implements MenuListener , IMenuListener { public void menuHidden ( MenuEvent event ) { fMenuAboutToShow = false ; fActionEditCopy . updateAction ( fMenuAboutToShow ) ; } public void menuShown ( MenuEvent e ) { } public void menuAboutToShow ( IMenuManager menuMgr ) { fMenuAboutToShow = true ; fActionEditCopy . updateAction ( fMenuAboutToShow ) ; fActionEditCut . updateAction ( fMenuAboutToShow ) ; fActionEditSelectAll . updateAction ( fMenuAboutToShow ) ; fActionEditPaste . updateAction ( fMenuAboutToShow ) ; fActionEditClearAll . updateAction ( fMenuAboutToShow ) ; } } protected class TerminalPropertyChangeHandler implements IPropertyChangeListener { public void propertyChange ( PropertyChangeEvent event ) { if ( event . getProperty ( ) . equals ( FONT_DEFINITION ) ) { onTerminalFontChanged ( ) ; } } } public boolean hasCommandInputField ( ) { return getActiveConnection ( ) . hasCommandInputField ( ) ; } public void setCommandInputField ( boolean on ) { getActiveConnection ( ) . setCommandInputField ( on ) ; } public boolean isScrollLock ( ) { return terminalViewControl . isScrollLock ( ) ; } public void setScrollLock ( boolean on ) { terminalViewControl . setScrollLock ( on ) ; } private ITerminalViewConnection getActiveConnection ( ) { return fMultiConnectionManager . getActiveConnection ( ) ; } private void setTerminalControl ( ITerminalViewControl ctrl ) { terminalViewControl = ctrl ; fCtlDecorator . setViewContoler ( ctrl ) ; } public void connectionsChanged ( ) { if ( getActiveConnection ( ) != null ) { ITerminalViewControl ctrl = getActiveConnection ( ) . getCtlTerminal ( ) ; if ( terminalViewControl != ctrl ) { setTerminalControl ( ctrl ) ; refresh ( ) ; } } } private void refresh ( ) { fPageBook . showPage ( terminalViewControl . getRootControl ( ) ) ; updateStatus ( ) ; setPartName ( getActiveConnection ( ) . getPartName ( ) ) ; } private void legacyLoadState ( ) { String summary = fStore . get ( STORE_SETTING_SUMMARY ) ; if ( summary != null ) { getActiveConnection ( ) . setSummary ( summary ) ; fStore . put ( STORE_SETTING_SUMMARY , null ) ; } } private void legacySetTitle ( ) { String title = fStore . get ( STORE_TITLE ) ; if ( title != null && title . length ( ) > 0 ) { setViewTitle ( title ) ; fStore . put ( STORE_TITLE , null ) ; } } public void setSpace ( ISpace space ) { this . space = space ; } public ISpace getSpace ( ) { return space ; } public void setHostEntity ( IEntity entity ) { this . hostEntity = entity ; } public IEntity getHostEntity ( ) { return hostEntity ; } } 