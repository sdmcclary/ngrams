public class SyncOut extends AbstractSynchronization { private static final Logger LOG = Logger . getLogger ( SyncOut . class . getName ( ) ) ; private static final String THREAD_NAME_PING = "Ping RL" ; private static final MessageFormat RL_PUB_URL = new MessageFormat ( "http://www.blogbridge.com/rl/{0,number,#}/{1}.opml" ) ; public SyncOut ( GlobalModel aModel ) { super ( aModel ) ; } protected Stats doSynchronization ( IProgressListener progress , String aEmail , String aPassword ) { SyncOutStats stats = new SyncOutStats ( ) ; try { if ( servicePreferences . isSyncFeeds ( ) ) { if ( progress != null ) progress . processStep ( Strings . message ( "service.sync.out.saving.guides.and.feeds" ) ) ; storeFeeds ( aEmail , aPassword , stats ) ; if ( progress != null ) progress . processStepCompleted ( ) ; } pingGuides ( ) ; if ( servicePreferences . isSyncPreferences ( ) ) { if ( progress != null ) progress . processStep ( Strings . message ( "service.sync.out.saving.preferences" ) ) ; storePreferences ( aEmail , aPassword , stats ) ; if ( progress != null ) progress . processStepCompleted ( ) ; } servicePreferences . setLastSyncOutStatus ( ServicePreferences . SYNC_STATUS_SUCCESS ) ; servicePreferences . setLastSyncOutFeedsCount ( model . getGuidesSet ( ) . countFeeds ( ) ) ; } catch ( ServerServiceException e1 ) { servicePreferences . setLastSyncOutStatus ( ServicePreferences . SYNC_STATUS_FAILURE ) ; if ( e1 . getCause ( ) != null ) { LOG . log ( Level . SEVERE , Strings . error ( "sync.error.during.sync.out" ) , e1 ) ; stats . registerFailure ( null ) ; } else { stats . registerFailure ( e1 . getMessage ( ) ) ; } } servicePreferences . setLastSyncOutDate ( new Date ( ) ) ; return stats ; } private void storePreferences ( String aEmail , String aPassword , SyncOutStats aStats ) throws ServerServiceException { final Hashtable < String , Object > prefs = new Hashtable < String , Object > ( ) ; String expressions = StringUtils . join ( ImageBlocker . getExpressions ( ) . iterator ( ) , "\n" ) ; prefs . put ( ImageBlocker . KEY , StringUtils . toUTF8 ( expressions ) ) ; SentimentsConfig . syncOut ( prefs ) ; storeGeneralPreferences ( prefs ) ; storeGuidesPreferences ( prefs ) ; storeFeedsPreferences ( prefs ) ; storeArticlesPreferences ( prefs ) ; storeTagsPreferences ( prefs ) ; storeReadingListsPreferences ( prefs ) ; storeAdvancedPreferences ( prefs ) ; storeWhatsHotPreferences ( prefs ) ; storeTwitterPreferences ( prefs ) ; Manager . storeState ( prefs ) ; setLong ( prefs , "timestamp" , System . currentTimeMillis ( ) ) ; ServerService . syncStorePrefs ( aEmail , aPassword , prefs ) ; aStats . savedPreferences = prefs . size ( ) ; } private void storeFeeds ( String aEmail , String aPassword , SyncOutStats aStats ) throws ServerServiceException { GuidesSet guidesSet = model . getGuidesSet ( ) ; Map < DirectFeed , Integer > feedHashes = calculateFeedHashes ( guidesSet ) ; OPMLGuideSet opmlSet = Converter . convertToOPML ( guidesSet , "BlogBridge Feeds" ) ; Document doc = new Exporter ( true ) . export ( opmlSet ) ; String opml = Transformation . documentToString ( doc ) ; int userId = ServerService . syncStore ( aEmail , aPassword , opml ) ; updatePublishedListsURLs ( guidesSet , userId ) ; guidesSet . onSyncOutCompletion ( ) ; updateFeedsWithHashes ( feedHashes ) ; GlobalController . SINGLETON . getDeletedFeedsRepository ( ) . purge ( ) ; StandardGuide [ ] guides = guidesSet . getStandardGuides ( null ) ; aStats . savedGuides = guides . length ; aStats . savedFeeds = countFeeds ( guides ) ; } static void updateFeedsWithHashes ( Map < DirectFeed , Integer > hashes ) { for ( Map . Entry < DirectFeed , Integer > en : hashes . entrySet ( ) ) { DirectFeed feed = en . getKey ( ) ; int hash = en . getValue ( ) ; feed . setSyncHash ( hash ) ; } } static Map < DirectFeed , Integer > calculateFeedHashes ( GuidesSet set ) { Map < DirectFeed , Integer > hashes = new IdentityHashMap < DirectFeed , Integer > ( ) ; List < IFeed > feeds = set . getFeeds ( ) ; for ( IFeed feed : feeds ) { if ( feed instanceof DirectFeed ) { DirectFeed dfeed = ( DirectFeed ) feed ; int hash = dfeed . calcSyncHash ( ) ; hashes . put ( dfeed , hash ) ; } } return hashes ; } private void updatePublishedListsURLs ( GuidesSet set , int userId ) { long publishingTime = System . currentTimeMillis ( ) ; int count = set . getGuidesCount ( ) ; for ( int i = 0 ; i < count ; i ++ ) { IGuide guide = set . getGuideAt ( i ) ; String publishingTitle = guide . getPublishingTitle ( ) ; if ( guide . isPublishingEnabled ( ) && StringUtils . isNotEmpty ( publishingTitle ) ) { String url = RL_PUB_URL . format ( new Object [ ] { userId , StringUtils . encodeForURL ( publishingTitle ) } ) ; guide . setPublishingURL ( url ) ; guide . setLastPublishingTime ( publishingTime ) ; } } } protected String getProcessStartMessage ( ) { return prepareProcessStartMessage ( Strings . message ( "service.sync.message.synchronizing" ) , Strings . message ( "service.sync.message.preferences" ) , Strings . message ( "service.sync.message.guides.and.feeds" ) , Strings . message ( "service.sync.message.with.blogbridge.service" ) ) ; } private static int countFeeds ( IGuide [ ] guides ) { int cnt = 0 ; for ( IGuide guide : guides ) cnt += guide . getFeedsCount ( ) ; return cnt ; } public static class SyncOutStats extends Stats { private int savedGuides = - 1 ; private int savedFeeds = - 1 ; private int savedPreferences = - 1 ; protected String getCustomText ( ) { StringBuffer buf = new StringBuffer ( ) ; if ( savedGuides > 0 ) buf . append ( MessageFormat . format ( Strings . message ( "service.sync.out.status.guides.saved" ) , savedGuides ) ) ; if ( savedFeeds > 0 ) buf . append ( MessageFormat . format ( Strings . message ( "service.sync.out.status.feeds.saved" ) , savedFeeds ) ) ; if ( savedPreferences > 0 ) buf . append ( MessageFormat . format ( Strings . message ( "service.sync.out.status.preference.saved" ) , savedPreferences ) ) ; return buf . toString ( ) ; } } private void storeGeneralPreferences ( Map prefs ) { UserPreferences up = model . getUserPreferences ( ) ; setBoolean ( prefs , UserPreferences . PROP_CHECKING_FOR_UPDATES_ON_STARTUP , up . isCheckingForUpdatesOnStartup ( ) ) ; setBoolean ( prefs , UserPreferences . PROP_SHOW_TOOLBAR , up . isShowToolbar ( ) ) ; setBoolean ( prefs , UserPreferences . PROP_MARK_READ_WHEN_CHANGING_CHANNELS , up . isMarkReadWhenChangingChannels ( ) ) ; setBoolean ( prefs , UserPreferences . PROP_MARK_READ_WHEN_CHANGING_GUIDES , up . isMarkReadWhenChangingGuides ( ) ) ; setBoolean ( prefs , UserPreferences . PROP_MARK_READ_AFTER_DELAY , up . isMarkReadAfterDelay ( ) ) ; setInt ( prefs , UserPreferences . PROP_MARK_READ_AFTER_SECONDS , up . getMarkReadAfterSeconds ( ) ) ; setInt ( prefs , UserPreferences . PROP_RSS_POLL_MIN , up . getRssPollInterval ( ) ) ; setInt ( prefs , UserPreferences . PROP_PURGE_COUNT , up . getPurgeCount ( ) ) ; setBoolean ( prefs , UserPreferences . PROP_PRESERVE_UNREAD , up . isPreserveUnread ( ) ) ; } private void storeGuidesPreferences ( Map prefs ) { UserPreferences up = model . getUserPreferences ( ) ; FeedRenderingSettings frs = model . getGlobalRenderingSettings ( ) ; setBoolean ( prefs , UserPreferences . PROP_PING_ON_RL_PUBLICATION , up . isPingOnReadingListPublication ( ) ) ; setString ( prefs , UserPreferences . PROP_PING_ON_RL_PUBLICATION_URL , up . getPingOnReadingListPublicationURL ( ) ) ; setBoolean ( prefs , RenderingSettingsNames . IS_BIG_ICON_IN_GUIDES , frs . isBigIconInGuides ( ) ) ; setBoolean ( prefs , "showUnreadInGuides" , frs . isShowUnreadInGuides ( ) ) ; setBoolean ( prefs , RenderingSettingsNames . IS_ICON_IN_GUIDES_SHOWING , frs . isShowIconInGuides ( ) ) ; setBoolean ( prefs , RenderingSettingsNames . IS_TEXT_IN_GUIDES_SHOWING , frs . isShowTextInGuides ( ) ) ; setInt ( prefs , UserPreferences . PROP_GUIDE_SELECTION_MODE , up . getGuideSelectionMode ( ) ) ; } private void storeFeedsPreferences ( Map prefs ) { UserPreferences up = model . getUserPreferences ( ) ; FeedRenderingSettings frs = model . getGlobalRenderingSettings ( ) ; setBoolean ( prefs , "showStarz" , frs . isShowStarz ( ) ) ; setBoolean ( prefs , "showUnreadInFeeds" , frs . isShowUnreadInFeeds ( ) ) ; setBoolean ( prefs , "showActivityChart" , frs . isShowActivityChart ( ) ) ; setFilterColor ( prefs , FeedClass . DISABLED ) ; setFilterColor ( prefs , FeedClass . INVALID ) ; setFilterColor ( prefs , FeedClass . LOW_RATED ) ; setFilterColor ( prefs , FeedClass . READ ) ; setFilterColor ( prefs , FeedClass . UNDISCOVERED ) ; setBoolean ( prefs , UserPreferences . PROP_SORTING_ENABLED , up . isSortingEnabled ( ) ) ; setInt ( prefs , UserPreferences . PROP_SORT_BY_CLASS_1 , up . getSortByClass1 ( ) ) ; setInt ( prefs , UserPreferences . PROP_SORT_BY_CLASS_2 , up . getSortByClass2 ( ) ) ; setBoolean ( prefs , UserPreferences . PROP_REVERSED_SORT_BY_CLASS_1 , up . isReversedSortByClass1 ( ) ) ; setBoolean ( prefs , UserPreferences . PROP_REVERSED_SORT_BY_CLASS_2 , up . isReversedSortByClass2 ( ) ) ; } private void storeArticlesPreferences ( Map prefs ) { UserPreferences up = model . getUserPreferences ( ) ; FeedRenderingSettings frs = model . getGlobalRenderingSettings ( ) ; setBoolean ( prefs , "groupingEnabled" , frs . isGroupingEnabled ( ) ) ; setBoolean ( prefs , "suppressingOlderThan" , frs . isSuppressingOlderThan ( ) ) ; setBoolean ( prefs , "displayingFullTitles" , frs . isDisplayingFullTitles ( ) ) ; setBoolean ( prefs , "sortingAscending" , frs . isSortingAscending ( ) ) ; setInt ( prefs , "suppressOlderThan" , frs . getSuppressOlderThan ( ) ) ; setBoolean ( prefs , UserPreferences . PROP_COPY_LINKS_IN_HREF_FORMAT , up . isCopyLinksInHrefFormat ( ) ) ; setBoolean ( prefs , "showEmptyGroups" , frs . isShowEmptyGroups ( ) ) ; setBoolean ( prefs , UserPreferences . PROP_BROWSE_ON_DBL_CLICK , up . isBrowseOnDblClick ( ) ) ; setBoolean ( prefs , UserPreferences . PROP_AUTO_EXPAND_MINI , up . isAutoExpandMini ( ) ) ; up . getViewModePreferences ( ) . store ( prefs ) ; } private void storeTagsPreferences ( Map prefs ) { UserPreferences up = model . getUserPreferences ( ) ; setInt ( prefs , UserPreferences . PROP_TAGS_STORAGE , up . getTagsStorage ( ) ) ; setString ( prefs , UserPreferences . PROP_TAGS_DELICIOUS_USER , up . getTagsDeliciousUser ( ) ) ; setString ( prefs , UserPreferences . PROP_TAGS_DELICIOUS_PASSWORD , up . getTagsDeliciousPassword ( ) ) ; setBoolean ( prefs , UserPreferences . PROP_TAGS_AUTOFETCH , up . isTagsAutoFetch ( ) ) ; setBoolean ( prefs , UserPreferences . PROP_PIN_TAGGING , up . isPinTagging ( ) ) ; setString ( prefs , UserPreferences . PROP_PIN_TAGS , up . getPinTags ( ) ) ; } private void storeReadingListsPreferences ( Map prefs ) { UserPreferences up = model . getUserPreferences ( ) ; setLong ( prefs , UserPreferences . PROP_READING_LIST_UPDATE_PERIOD , up . getReadingListUpdatePeriod ( ) ) ; setInt ( prefs , UserPreferences . PROP_ON_READING_LIST_UPDATE_ACTIONS , up . getOnReadingListUpdateActions ( ) ) ; setBoolean ( prefs , UserPreferences . PROP_UPDATE_FEEDS , up . isUpdateFeeds ( ) ) ; setBoolean ( prefs , UserPreferences . PROP_UPDATE_READING_LISTS , up . isUpdateReadingLists ( ) ) ; } private void storeAdvancedPreferences ( Map prefs ) { UserPreferences up = model . getUserPreferences ( ) ; StarzPreferences sp = model . getStarzPreferences ( ) ; setInt ( prefs , UserPreferences . PROP_FEED_SELECTION_DELAY , up . getFeedSelectionDelay ( ) ) ; setBoolean ( prefs , UserPreferences . PROP_AA_TEXT , up . isAntiAliasText ( ) ) ; setInt ( prefs , StarzPreferences . PROP_TOP_ACTIVITY , sp . getTopActivity ( ) ) ; setInt ( prefs , StarzPreferences . PROP_TOP_HIGHLIGHTS , sp . getTopHighlights ( ) ) ; setBoolean ( prefs , UserPreferences . PROP_SHOW_TOOLBAR_LABELS , up . isShowToolbarLabels ( ) ) ; setBoolean ( prefs , UserPreferences . PROP_SHOW_UNREAD_BUTTON_MENU , up . isShowUnreadButtonMenu ( ) ) ; setInt ( prefs , UserPreferences . PROP_FEED_IMPORT_LIMIT , up . getFeedImportLimit ( ) ) ; } private void storeWhatsHotPreferences ( Map prefs ) { UserPreferences up = model . getUserPreferences ( ) ; setString ( prefs , UserPreferences . PROP_WH_IGNORE , up . getWhIgnore ( ) ) ; setBoolean ( prefs , UserPreferences . PROP_WH_NOSELFLINKS , up . isWhNoSelfLinks ( ) ) ; setBoolean ( prefs , UserPreferences . PROP_WH_SUPPRESS_SAME_SOURCE_LINKS , up . isWhSuppressSameSourceLinks ( ) ) ; setString ( prefs , UserPreferences . PROP_WH_TARGET_GUIDE , up . getWhTargetGuide ( ) ) ; setLong ( prefs , UserPreferences . PROP_WH_SETTINGS_CHANGE_TIME , up . getWhSettingsChangeTime ( ) ) ; } private void storeTwitterPreferences ( Map prefs ) { TwitterPreferences tp = model . getUserPreferences ( ) . getTwitterPreferences ( ) ; setBoolean ( prefs , TwitterPreferences . PROP_TWITTER_ENABLED , tp . isEnabled ( ) ) ; setString ( prefs , TwitterPreferences . PROP_TWITTER_SCREEN_NAME , tp . getScreenName ( ) ) ; setString ( prefs , TwitterPreferences . PROP_TWITTER_ACCESS_TOKEN , tp . getAccessToken ( ) ) ; setString ( prefs , TwitterPreferences . PROP_TWITTER_TOKEN_SECRET , tp . getTokenSecret ( ) ) ; setBoolean ( prefs , TwitterPreferences . PROP_TWITTER_PROFILE_PICS , tp . isProfilePics ( ) ) ; setBoolean ( prefs , TwitterPreferences . PROP_TWITTER_PASTE_LINK , tp . isPasteLink ( ) ) ; } public static void setBoolean ( Map prefs , String name , boolean value ) { setString ( prefs , name , Boolean . toString ( value ) ) ; } public static void setInt ( Map prefs , String name , int value ) { setString ( prefs , name , Integer . toString ( value ) ) ; } private static void setLong ( Map prefs , String name , long value ) { setString ( prefs , name , Long . toString ( value ) ) ; } private static void setFilterColor ( Map prefs , int feedClass ) { FeedDisplayModeManager fdmm = FeedDisplayModeManager . getInstance ( ) ; Color color = fdmm . getColor ( feedClass ) ; setString ( prefs , "cdmm." + feedClass , UifUtilities . colorToHex ( color ) ) ; } public static void setString ( Map prefs , String name , String value ) { byte [ ] bytes = StringUtils . toUTF8 ( value ) ; prefs . put ( name , bytes == null ? new byte [ 0 ] : bytes ) ; } private void pingGuides ( ) { Thread thPing = new Thread ( new Runnable ( ) { public void run ( ) { GlobalModel model = GlobalController . SINGLETON . getModel ( ) ; UserPreferences prefs = model . getUserPreferences ( ) ; String url = prefs . getPingOnReadingListPublicationURL ( ) . trim ( ) ; if ( prefs . isPingOnReadingListPublication ( ) && url . length ( ) > 0 && url . indexOf ( "%u" ) != - 1 ) { IGuide [ ] publishedGuides = collectGuides ( model . getGuidesSet ( ) ) ; pingGuides ( publishedGuides , url ) ; } } } , THREAD_NAME_PING ) ; thPing . start ( ) ; } private void pingGuides ( IGuide [ ] guides , String url ) { for ( IGuide guide : guides ) { String realURL = url . replaceAll ( "%u" , guide . getPublishingURL ( ) ) ; try { ping ( new URL ( realURL ) ) ; } catch ( Throwable e ) { LOG . log ( Level . WARNING , Strings . error ( "sync.failed.to.ping.reading.list.service" ) , e ) ; } } } private void ping ( URL url ) throws IOException { InputStream stream = url . openStream ( ) ; stream . read ( ) ; stream . close ( ) ; } private IGuide [ ] collectGuides ( GuidesSet set ) { StandardGuide [ ] guides = set . getStandardGuides ( null ) ; java . util . List < StandardGuide > rl = new ArrayList < StandardGuide > ( ) ; for ( StandardGuide guide : guides ) { String url = guide . getPublishingURL ( ) ; if ( guide . isPublishingEnabled ( ) && url != null && url . trim ( ) . length ( ) > 0 ) rl . add ( guide ) ; } return rl . toArray ( new IGuide [ rl . size ( ) ] ) ; } } 