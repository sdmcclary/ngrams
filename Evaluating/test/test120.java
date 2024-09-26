public class AdvancedPreferencesPanel extends JPanel { private static final String PAT_IP = "([1-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-4])" ; private static final String PAT_SN = "[a-z][a-z0-9]+" ; private static final String PAC_REC = "\\s*(" + PAT_IP + "(\\." + PAT_IP + "){3}|" + PAT_SN + "(\\." + PAT_SN + ")*)\\s*" ; private static final Pattern PAT_VALID_PROXY_EX = Pattern . compile ( "^" + PAC_REC + "(," + PAC_REC + ")*$" , Pattern . CASE_INSENSITIVE ) ; private UserPreferences userPrefs ; private StarzPreferences starzPrefs ; private final FeedRenderingSettings feedRS ; private ValueModel triggerChannel ; private JCheckBox chShowTipsBox ; private JCheckBox chDoUpdatesCheck ; private JTextField tfInternetBrowser ; private JCheckBox chProxyEnabled ; private JTextField tfProxyHost ; private JLabel lbProxyPort ; private JSpinner spinProxyPort ; private JLabel lbProxyExclusions ; private JTextField tfProxyExclusions ; private int initActivityLimit ; private int initHightlightsLimit ; private JCheckBox chAAText ; private JCheckBox chShowUnreadButtonMenu ; private JSpinner spinFeedImportLimit ; private JTextField tfPingURL ; private JCheckBox chPing ; private JRadioButton rbGSMFirst ; private JRadioButton rbGSMLastSeen ; private JRadioButton rbGSMNoFeed ; private JCheckBox chHideOldArticles ; private JCheckBox chDisplayFullTitles ; private JCheckBox chSortingAscending ; private JTextField tfSuppressOlderThan ; private JCheckBox chCopyLinksInHREFFormat ; private JCheckBox chBrowseOnTitleDblClick ; private JCheckBox chShowAppIconInSystray ; private JCheckBox chMinimizeToSystray ; private JComboBox cbBIDMode ; private JCheckBox chAlwaysUseEnglish ; public AdvancedPreferencesPanel ( UserPreferences aUserPrefs , StarzPreferences aStarzPrefs , FeedRenderingSettings aFeedRS , ValueModel aTriggerChannel ) { feedRS = aFeedRS ; this . userPrefs = aUserPrefs ; this . starzPrefs = aStarzPrefs ; this . triggerChannel = aTriggerChannel ; initComponents ( triggerChannel ) ; build ( ) ; } private void initComponents ( ValueModel triggerChannel ) { chShowTipsBox = ComponentsFactory . createCheckBox ( Strings . message ( "userprefs.tab.general.show.tip.of.the.day" ) , new ToggleButtonAdapter ( new BufferedValueModel ( TipOfTheDayDialog . showingTipsModel ( ) , triggerChannel ) ) ) ; String msg = Strings . message ( "userprefs.tab.general.check.for.new.versions" ) ; if ( ApplicationLauncher . isAutoUpdatesEnabled ( ) ) { chDoUpdatesCheck = ComponentsFactory . createCheckBox ( msg , new ToggleButtonAdapter ( new BufferedValueModel ( new PropertyAdapter ( userPrefs , UserPreferences . PROP_CHECKING_FOR_UPDATES_ON_STARTUP ) , triggerChannel ) ) ) ; } else { msg = msg . replaceAll ( "&" , "" ) ; chDoUpdatesCheck = new JCheckBox ( msg ) ; chDoUpdatesCheck . setSelected ( true ) ; chDoUpdatesCheck . setEnabled ( false ) ; } chAlwaysUseEnglish = ComponentsFactory . createCheckBox ( Strings . message ( "userprefs.tab.advanced.always.use.english" ) , new ToggleButtonAdapter ( new BufferedValueModel ( new PropertyAdapter ( userPrefs , UserPreferences . PROP_ALWAYS_USE_ENGLISH ) , triggerChannel ) ) ) ; tfInternetBrowser = new JTextField ( ) ; tfInternetBrowser . setDocument ( new DocumentAdapter ( new BufferedValueModel ( new PropertyAdapter ( userPrefs , UserPreferences . PROP_INTERNET_BROWSER ) , triggerChannel ) ) ) ; if ( BrowserLauncher . isUsingJWSBrowser ( ) ) { tfInternetBrowser . setEnabled ( false ) ; tfInternetBrowser . setToolTipText ( Strings . message ( "userprefs.tab.advanced.browser.tooltip.disabled" ) ) ; } else { tfInternetBrowser . setEnabled ( true ) ; tfInternetBrowser . setToolTipText ( Strings . message ( "userprefs.tab.advanced.browser.tooltip.enabled" ) ) ; } initProxyComponents ( triggerChannel ) ; SpinnerModelAdapter spinModelFeedSelectionDelay = new SpinnerModelAdapter ( new BoundedRangeAdapter ( new BufferedValueModel ( new PropertyAdapter ( userPrefs , UserPreferences . PROP_FEED_SELECTION_DELAY ) , triggerChannel ) , 0 , 0 , 1000 ) ) ; spinModelFeedSelectionDelay . setStepSize ( 100 ) ; chAAText = createCheckBox ( userPrefs , Strings . message ( "userprefs.tab.advanced.antialiased.fonts" ) , UserPreferences . PROP_AA_TEXT ) ; saveInitialLimits ( ) ; triggerChannel . addValueChangeListener ( new PropertyChangeListener ( ) { public void propertyChange ( PropertyChangeEvent evt ) { if ( Boolean . TRUE . equals ( evt . getNewValue ( ) ) ) doRepaintChannelList ( ) ; } } ) ; chShowUnreadButtonMenu = createCheckBox ( userPrefs , Strings . message ( "userprefs.tab.advanced.show.the.unread.buttons.menu" ) , UserPreferences . PROP_SHOW_UNREAD_BUTTON_MENU ) ; SpinnerModelAdapter spinModelFeedImportLimit = new SpinnerModelAdapter ( new BoundedRangeAdapter ( new BufferedValueModel ( new PropertyAdapter ( userPrefs , UserPreferences . PROP_FEED_IMPORT_LIMIT ) , triggerChannel ) , 1 , 1 , UserPreferences . MAX_FEED_IMPORT_LIMITATION ) ) ; spinModelFeedImportLimit . setStepSize ( 100 ) ; spinFeedImportLimit = new JSpinner ( spinModelFeedImportLimit ) ; initGuideComponents ( triggerChannel ) ; chHideOldArticles = createCheckBox ( feedRS , Strings . message ( "userprefs.tab.articles.hide.articles.older.than" ) , "suppressingOlderThan" ) ; chDisplayFullTitles = createCheckBox ( feedRS , Strings . message ( "userprefs.tab.articles.display.full.titles" ) , "displayingFullTitles" ) ; chSortingAscending = createCheckBox ( feedRS , Strings . message ( "userprefs.tab.articles.sort.earlier.articles.first" ) , "sortingAscending" ) ; tfSuppressOlderThan = new JTextField ( ) ; tfSuppressOlderThan . setDocument ( new DocumentAdapter ( new BufferedValueModel ( new PropertyAdapter ( feedRS , "suppressOlderThanString" ) , triggerChannel ) ) ) ; chCopyLinksInHREFFormat = createCheckBox ( userPrefs , Strings . message ( "userprefs.tab.articles.use.href.format.for.links.copied.to.clipboard" ) , UserPreferences . PROP_COPY_LINKS_IN_HREF_FORMAT ) ; chBrowseOnTitleDblClick = createCheckBox ( userPrefs , Strings . message ( "userprefs.tab.articles.browse.on.double.click.over.the.title" ) , UserPreferences . PROP_BROWSE_ON_DBL_CLICK ) ; chShowAppIconInSystray = createCheckBox ( userPrefs , Strings . message ( "userprefs.tab.advanced.show.appicon.in.systray" ) , UserPreferences . PROP_SHOW_APPICON_IN_SYSTRAY ) ; chMinimizeToSystray = createCheckBox ( userPrefs , Strings . message ( "userprefs.tab.advanced.minimize.to.systray" ) , UserPreferences . PROP_MINIMIZE_TO_SYSTRAY ) ; ValueModel modeModel = new BufferedValueModel ( new BIDModeToStringConverter ( new PropertyAdapter ( userPrefs , UserPreferences . PROP_DIB_MODE ) ) , triggerChannel ) ; cbBIDMode = new JComboBox ( new ComboBoxAdapter ( BIDModeToStringConverter . MODES , modeModel ) ) ; } private void initGuideComponents ( ValueModel triggerChannel ) { ValueModel gsmModel = new BufferedValueModel ( new PropertyAdapter ( userPrefs , UserPreferences . PROP_GUIDE_SELECTION_MODE ) , triggerChannel ) ; rbGSMFirst = createRadioButton ( gsmModel , UserPreferences . GSM_FIRST_FEED , Strings . message ( "userprefs.tab.guides.select.first.feed" ) ) ; rbGSMLastSeen = createRadioButton ( gsmModel , UserPreferences . GSM_LAST_SEEN_FEED , Strings . message ( "userprefs.tab.guides.select.last.seen.feed" ) ) ; rbGSMNoFeed = createRadioButton ( gsmModel , UserPreferences . GSM_NO_FEED , Strings . message ( "userprefs.tab.guides.select.no.feed" ) ) ; chPing = createCheckBox ( userPrefs , Strings . message ( "userprefs.tab.guides.ping.url" ) , UserPreferences . PROP_PING_ON_RL_PUBLICATION ) ; chPing . addActionListener ( new ActionListener ( ) { public void actionPerformed ( ActionEvent e ) { updatePingURLState ( ) ; } } ) ; tfPingURL = new JTextField ( ) ; tfPingURL . setDocument ( new DocumentAdapter ( new BufferedValueModel ( new PropertyAdapter ( userPrefs , UserPreferences . PROP_PING_ON_RL_PUBLICATION_URL ) , triggerChannel ) ) ) ; updatePingURLState ( ) ; String ttPing = Strings . message ( "userprefs.tab.guides.ping.wording" ) ; chPing . setToolTipText ( ttPing ) ; tfPingURL . setToolTipText ( ttPing ) ; } private void updatePingURLState ( ) { tfPingURL . setEnabled ( chPing . isSelected ( ) ) ; } private JRadioButton createRadioButton ( ValueModel model , int mode , String caption ) { return ComponentsFactory . createRadioButton ( caption , new RadioButtonAdapter ( model , mode ) ) ; } private void initProxyComponents ( ValueModel triggerChannel ) { chProxyEnabled = createCheckBox ( userPrefs , Strings . message ( "userprefs.tab.advanced.use.proxy" ) , UserPreferences . PROP_PROXY_ENABLED ) ; tfProxyHost = new JTextField ( ) ; tfProxyHost . setDocument ( new DocumentAdapter ( new BufferedValueModel ( new PropertyAdapter ( userPrefs , UserPreferences . PROP_PROXY_HOST ) , triggerChannel ) ) ) ; lbProxyPort = ComponentsFactory . createLabel ( Strings . message ( "userprefs.tab.advanced.use.proxy.port" ) ) ; spinProxyPort = new JSpinner ( new SpinnerModelAdapter ( new BoundedRangeAdapter ( new BufferedValueModel ( new PropertyAdapter ( userPrefs , UserPreferences . PROP_PROXY_PORT ) , triggerChannel ) , 80 , 80 , 65536 ) ) ) ; spinProxyPort . setEditor ( new JSpinner . NumberEditor ( spinProxyPort , "#" ) ) ; lbProxyExclusions = ComponentsFactory . createLabel ( Strings . message ( "userprefs.tab.advanced.no.proxy.for" ) ) ; tfProxyExclusions = new JTextField ( ) ; tfProxyExclusions . setDocument ( new DocumentAdapter ( new BufferedValueModel ( new PropertyAdapter ( userPrefs , UserPreferences . PROP_PROXY_EXCLUSIONS ) , triggerChannel ) ) ) ; StateUpdatingToggleListener . install ( chProxyEnabled , tfProxyHost , lbProxyPort , spinProxyPort , lbProxyExclusions , tfProxyExclusions ) ; } private JCheckBox createCheckBox ( Object obj , String label , String propertyName ) { return ComponentsFactory . createCheckBox ( label , new ToggleButtonAdapter ( new BufferedValueModel ( new PropertyAdapter ( obj , propertyName ) , triggerChannel ) ) ) ; } private void saveInitialLimits ( ) { initActivityLimit = starzPrefs . getTopActivity ( ) ; initHightlightsLimit = starzPrefs . getTopHighlights ( ) ; } private void doRepaintChannelList ( ) { if ( starzPrefs . getTopActivity ( ) != initActivityLimit || starzPrefs . getTopHighlights ( ) != initHightlightsLimit ) { saveInitialLimits ( ) ; GlobalController . SINGLETON . getMainFrame ( ) . getFeedsPanel ( ) . repaint ( ) ; } } private void build ( ) { JPanel panel = new VertialScrollablePanel ( ) ; String version = System . getProperty ( "java.vm.version" ) ; boolean is15 = version != null && version . startsWith ( "1.5" ) ; BBFormBuilder builder = new BBFormBuilder ( "7dlu, p, 2dlu, 40dlu, 70dlu:grow" , panel ) ; builder . setDefaultDialogBorder ( ) ; builder . appendSeparator ( Strings . message ( "userprefs.tab.advanced.separator.on.startup" ) ) ; builder . setLeadingColumnOffset ( 1 ) ; builder . append ( chShowTipsBox , 4 ) ; builder . append ( chDoUpdatesCheck , 4 ) ; builder . append ( chAlwaysUseEnglish , 4 ) ; builder . setLeadingColumnOffset ( 1 ) ; builder . appendSeparator ( Strings . message ( "userprefs.tab.advanced.separator.network" ) ) ; builder . nextColumn ( ) ; builder . append ( Strings . message ( "userprefs.tab.advanced.browser" ) , tfInternetBrowser , 2 ) ; builder . append ( chProxyEnabled ) ; builder . append ( buildProxyPanel ( ) , 2 ) ; builder . append ( lbProxyExclusions ) ; builder . append ( tfProxyExclusions , 2 ) ; tfProxyExclusions . setToolTipText ( Strings . message ( "userprefs.tab.advanced.no.proxy.for.notes" ) ) ; builder . appendSeparator ( Strings . message ( "userprefs.tab.advanced.separator.interface" ) ) ; builder . nextLine ( ) ; builder . append ( Strings . message ( "userprefs.tab.advanced.feed.import.limit" ) , spinFeedImportLimit ) ; builder . nextLine ( ) ; builder . append ( chShowUnreadButtonMenu , 4 ) ; if ( is15 ) builder . append ( chAAText , 4 ) ; if ( NotificationArea . isSupported ( ) ) { if ( ! SystemUtils . IS_OS_MAC ) builder . append ( chShowAppIconInSystray , 4 ) ; if ( OSSettings . isMinimizeToSystraySupported ( ) ) builder . append ( chMinimizeToSystray , 4 ) ; } if ( SystemUtils . IS_OS_MAC ) builder . append ( Strings . message ( "userprefs.tab.advanced.dock.icon" ) , cbBIDMode , 2 ) ; builder . appendSeparator ( Strings . message ( "userprefs.tab.guides.on.selection" ) ) ; builder . append ( rbGSMFirst , 4 ) ; builder . append ( rbGSMLastSeen , 4 ) ; builder . append ( rbGSMNoFeed , 4 ) ; builder . appendSeparator ( Strings . message ( "userprefs.tab.guides.on.publication" ) ) ; builder . append ( chPing ) ; builder . append ( tfPingURL , 2 ) ; builder . appendSeparator ( Strings . message ( "userprefs.tab.advanced.articles" ) ) ; builder . append ( chSortingAscending , 4 ) ; builder . append ( chHideOldArticles , tfSuppressOlderThan ) ; builder . nextLine ( ) ; builder . append ( chDisplayFullTitles , 4 ) ; builder . append ( chCopyLinksInHREFFormat , 4 ) ; builder . append ( chBrowseOnTitleDblClick , 4 ) ; setLayout ( new BorderLayout ( ) ) ; JScrollPane sp = new JScrollPane ( panel ) ; sp . setBorder ( BorderFactory . createEmptyBorder ( ) ) ; sp . setHorizontalScrollBarPolicy ( JScrollPane . HORIZONTAL_SCROLLBAR_NEVER ) ; add ( sp , BorderLayout . CENTER ) ; } private JPanel buildProxyPanel ( ) { BBFormBuilder builder = new BBFormBuilder ( "50dlu:grow, 10dlu, pref, 2dlu, 30dlu" ) ; builder . append ( tfProxyHost ) ; builder . append ( lbProxyPort , spinProxyPort ) ; lbProxyPort . setLabelFor ( spinProxyPort ) ; return builder . getPanel ( ) ; } public String checkValidity ( ) { String msg = null ; String txt = tfProxyExclusions . getText ( ) ; if ( ! isValidProxyExclusions ( txt ) ) { msg = "Please correct the list of Proxy Exclusions.\n" + "It should be the comma-separated list of IP\n" + "addresses or site names." ; } return msg ; } static boolean isValidProxyExclusions ( String txt ) { return StringUtils . isEmpty ( txt ) || PAT_VALID_PROXY_EX . matcher ( txt ) . matches ( ) ; } private static class BIDModeToStringConverter extends AbstractConverter { public static final String [ ] MODES = { Strings . message ( "userprefs.tab.advanced.dock.icon.invisible" ) , Strings . message ( "userprefs.tab.advanced.dock.icon.unread.articles" ) , Strings . message ( "userprefs.tab.advanced.dock.icon.unread.feeds" ) } ; public BIDModeToStringConverter ( ValueModel model ) { super ( model ) ; } public Object convertFromSubject ( Object object ) { int mode = ( Integer ) object ; return MODES [ mode ] ; } public void setValue ( Object object ) { int index = - 1 ; for ( int i = 0 ; index < 0 && i < MODES . length ; i ++ ) { String mode = MODES [ i ] ; if ( mode . equals ( object ) ) index = i ; } subject . setValue ( index ) ; } } } 