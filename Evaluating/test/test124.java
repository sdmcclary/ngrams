<<<<<<< HEAD
public class AddGuideDialog extends BasicGuideDialog { private JComboBox iconsList = new JComboBox ( ) ; private Picker picker ; private JPanel pane ; private JPanel reloadPanel ; public AddGuideDialog ( Frame owner , boolean aPublishingAvailable , int aPublishingLimit , boolean aPublishingLimitReached ) { super ( owner , Strings . message ( "add.guide.dialog.title" ) , aPublishingAvailable , aPublishingLimit , aPublishingLimitReached ) ; enableEvents ( AWTEvent . WINDOW_EVENT_MASK ) ; } protected IGuide getGuide ( ) { return null ; } protected void processWindowEvent ( WindowEvent e ) { if ( e . getID ( ) == WindowEvent . WINDOW_OPENED ) { tfTitle . requestFocusInWindow ( ) ; } } protected JComponent buildContent ( ) { JPanel content = new JPanel ( new BorderLayout ( ) ) ; content . add ( buildBody ( ) , BorderLayout . CENTER ) ; content . add ( buildButtons ( ) , BorderLayout . SOUTH ) ; return content ; } private JComponent buildButtons ( ) { UIFButton btnSelect = createAcceptButton ( Strings . message ( "add.guide.add" ) , true ) ; UIFButton btnCancel = createCancelButton ( ) ; JPanel panel = ButtonBarFactory . buildOKCancelBar ( btnSelect , btnCancel ) ; panel . setBorder ( Borders . BUTTON_BAR_GAP_BORDER ) ; return panel ; } protected JComponent buildHeader ( ) { return new HeaderPanelExt ( Strings . message ( "add.guide.dialog.title" ) , Strings . message ( "add.guide.dialog.header" ) ) ; } private JComponent buildBody ( ) { JTabbedPane pane = new JTabbedPane ( ) ; pane . addTab ( Strings . message ( "add.guide.feeds" ) , buildFeedsTab ( ) ) ; pane . addTab ( Strings . message ( "guide.dialog.readinglists" ) , buildReadingListsTab ( ) ) ; pane . addTab ( Strings . message ( "guide.dialog.publishing" ) , buildPublishingTab ( ) ) ; if ( NotificationArea . isSupported ( ) ) { pane . addTab ( Strings . message ( "guide.dialog.notifications" ) , buildNotificationsTab ( ) ) ; } BBFormBuilder builder = new BBFormBuilder ( "pref, 4dlu, pref:grow, 7dlu, pref" ) ; builder . append ( Strings . message ( "guide.dialog.title" ) , tfTitle , iconsList ) ; builder . appendUnrelatedComponentsGapRow ( 2 ) ; builder . appendRow ( "min:grow" ) ; builder . append ( pane , 5 , CellConstraints . FILL , CellConstraints . FILL ) ; return builder . getPanel ( ) ; } private JComponent buildFeedsTab ( ) { BBFormBuilder builder = new BBFormBuilder ( "pref:grow" ) ; builder . setDefaultDialogBorder ( ) ; builder . append ( Strings . message ( "add.guide.feeds.wording" ) , 1 ) ; builder . appendRelatedComponentsGapRow ( 2 ) ; builder . appendRow ( "min:grow" ) ; builder . append ( pane , 1 , CellConstraints . FILL , CellConstraints . FILL ) ; return builder . getPanel ( ) ; } public String getGuideTitle ( ) { return tfTitle . getText ( ) ; } public String getIconKey ( ) { return ( String ) iconsList . getSelectedItem ( ) ; } public boolean isAutoFeedDiscovery ( ) { return false ; } public String open ( GuidesSet set ) { if ( pane == null ) { pane = new JPanel ( new BorderLayout ( ) ) ; iconsList . setModel ( model ) ; iconsList . setRenderer ( renderer ) ; } Set usedIconKeys = set . getGuidesIconKeys ( ) ; int index = GuideIcons . findUnusedIconName ( usedIconKeys ) ; if ( index < 0 ) index = 0 ; iconsList . setSelectedIndex ( index ) ; setPresentTitles ( set . getGuidesTitles ( ) ) ; setVisibleView ( ) ; setReadingLists ( new ReadingList [ 0 ] ) ; boolean en = GlobalModel . SINGLETON . getUserPreferences ( ) . isNotificationsEnabled ( ) ; chAllowNotifications . setSelected ( true ) ; chAllowNotifications . setEnabled ( en ) ; super . openDialog ( set ) ; String selectedUrls = "" ; if ( picker != null ) { CollectionItem [ ] selected = picker . getSelectedCollectionItems ( ) ; String [ ] urls = new String [ selected . length ] ; for ( int i = 0 ; i < selected . length ; i ++ ) { CollectionItem item = selected [ i ] ; urls [ i ] = item . getXmlURL ( ) ; } selectedUrls = StringUtils . join ( urls , Constants . URL_SEPARATOR ) ; } return selectedUrls ; } private void setVisibleView ( ) { boolean isPickerVisible = picker != null ; pane . removeAll ( ) ; pane . add ( isPickerVisible ? picker : getReloadPanel ( ) , BorderLayout . CENTER ) ; validate ( ) ; repaint ( ) ; } private synchronized Component getReloadPanel ( ) { if ( reloadPanel == null ) { String text = Strings . message ( "click.here.to.load.our.collection.of.interesting.feeds" ) ; String overText = Strings . message ( "load.our.collection.of.interesting.feeds" ) ; ActionLabel label = new ActionLabel ( text , new ReloadAction ( ) , overText ) ; label . setForeground ( Color . BLUE ) ; reloadPanel = new JPanel ( new FormLayout ( "pref:grow" , "pref:grow" ) ) ; reloadPanel . add ( label , new CellConstraints ( ) . xy ( 1 , 1 , "c, c" ) ) ; } return reloadPanel ; } protected String validateTitle ( ) { String message = null ; final String title = tfTitle . getText ( ) ; if ( title == null || title . trim ( ) . length ( ) == 0 ) { message = Strings . message ( "guide.dialog.validation.empty.title" ) ; } else if ( presentTitles . contains ( title ) ) { message = Strings . message ( "guide.dialog.validation.already.present" ) ; } return message ; } private class ReloadAction extends AbstractAction { public ReloadAction ( ) { super ( Strings . message ( "add.guide.reload.feeds" ) ) ; } public void actionPerformed ( ActionEvent e ) { if ( picker == null ) { picker = new Picker ( ) ; picker . addCollection ( ServerService . getStartingPointsURL ( ) , Strings . message ( "collection.collections" ) , true , Picker . ITEM_TYPE_FEED , false ) ; picker . addCollection ( ServerService . getExpertsURL ( ) , Strings . message ( "collection.experts" ) , true , Picker . ITEM_TYPE_FEED , true ) ; setVisibleView ( ) ; } } } } 
=======
class ListPattern extends Pattern { private final Pattern p ; private final Locator locator ; ListPattern ( Pattern p , Locator locator ) { super ( false , DATA_CONTENT_TYPE , combineHashCode ( LIST_HASH_CODE , p . hashCode ( ) ) ) ; this . p = p ; this . locator = locator ; } Pattern expand ( SchemaPatternBuilder b ) { Pattern ep = p . expand ( b ) ; if ( ep != p ) return b . makeList ( ep , locator ) ; else return this ; } void checkRecursion ( int depth ) throws SAXException { p . checkRecursion ( depth ) ; } boolean samePattern ( Pattern other ) { return ( other instanceof ListPattern && p == ( ( ListPattern ) other ) . p ) ; } < T > T apply ( PatternFunction < T > f ) { return f . caseList ( this ) ; } void checkRestrictions ( int context , DuplicateAttributeDetector dad , Alphabet alpha ) throws RestrictionViolationException { switch ( context ) { case DATA_EXCEPT_CONTEXT : throw new RestrictionViolationException ( "data_except_contains_list" ) ; case START_CONTEXT : throw new RestrictionViolationException ( "start_contains_list" ) ; case LIST_CONTEXT : throw new RestrictionViolationException ( "list_contains_list" ) ; } try { p . checkRestrictions ( LIST_CONTEXT , dad , null ) ; } catch ( RestrictionViolationException e ) { e . maybeSetLocator ( locator ) ; throw e ; } } Pattern getOperand ( ) { return p ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
