<<<<<<< HEAD
public class GuidesSet { private final FeedsList feedsList ; private final List < IGuide > guides ; private final List < IGuidesSetListener > listeners ; private final GuidesListener guidesListener ; public GuidesSet ( ) { feedsList = new FeedsList ( ) ; guides = new IdentityList < IGuide > ( ) ; listeners = new CopyOnWriteArrayList < IGuidesSetListener > ( ) ; guidesListener = new GuidesListener ( ) ; } public FeedsList getFeedsList ( ) { return feedsList ; } public synchronized void add ( IGuide guide ) { add ( - 1 , guide ) ; } public synchronized void add ( int index , IGuide guide ) { add ( index , guide , true ) ; } public synchronized void add ( int index , IGuide guide , boolean lastInBatch ) { if ( guide == null ) throw new NullPointerException ( Strings . error ( "unspecified.guide" ) ) ; if ( ! guides . contains ( guide ) ) { if ( index > - 1 ) guides . add ( index , guide ) ; else guides . add ( guide ) ; guide . addListener ( guidesListener ) ; int count = guide . getFeedsCount ( ) ; for ( int i = 0 ; i < count ; i ++ ) { feedsList . add ( guide . getFeedAt ( i ) ) ; } fireGuideAdded ( guide , lastInBatch ) ; } } public synchronized int remove ( IGuide guide ) { if ( guide == null ) throw new NullPointerException ( Strings . error ( "unspecified.guide" ) ) ; int index = guides . indexOf ( guide ) ; if ( index != - 1 ) { guide . removeChildren ( ) ; guides . remove ( guide ) ; guide . removeListener ( guidesListener ) ; fireGuideRemoved ( guide , index ) ; } return index ; } public synchronized IGuide getGuideAt ( int index ) { return guides . get ( index ) ; } public synchronized int getGuidesCount ( ) { return guides . size ( ) ; } public synchronized int indexOf ( IGuide guide ) { if ( guide == null ) throw new NullPointerException ( Strings . error ( "unspecified.guide" ) ) ; return guides . indexOf ( guide ) ; } public synchronized StandardGuide [ ] getStandardGuides ( StandardGuide guide ) { ArrayList < StandardGuide > list = new ArrayList < StandardGuide > ( getGuidesCount ( ) ) ; for ( IGuide iGuide : guides ) { if ( iGuide instanceof StandardGuide && iGuide != guide ) list . add ( ( StandardGuide ) iGuide ) ; } return list . toArray ( new StandardGuide [ list . size ( ) ] ) ; } public synchronized Set < String > getGuidesTitles ( ) { Set < String > titles = new HashSet < String > ( getGuidesCount ( ) ) ; for ( IGuide guide : guides ) titles . add ( guide . getTitle ( ) ) ; return titles ; } public synchronized Set < String > getGuidesIconKeys ( ) { Set < String > keys = new HashSet < String > ( ) ; for ( IGuide guide : guides ) { String iconKey = guide . getIconKey ( ) ; if ( iconKey != null ) keys . add ( iconKey ) ; } return keys ; } public synchronized void setRead ( boolean read ) { for ( IGuide guide : guides ) guide . setRead ( read ) ; } public DirectFeed findDirectFeed ( URL xmlUrl ) { return feedsList . findDirectFeed ( xmlUrl ) ; } public IFeed findFeed ( IFeed feed ) { IFeed result ; if ( feed instanceof DirectFeed ) { result = findDirectFeed ( ( ( DirectFeed ) feed ) . getXmlURL ( ) ) ; } else if ( feed instanceof SearchFeed ) { result = findSearchFeed ( ( ( SearchFeed ) feed ) . getQuery ( ) ) ; } else { QueryFeed qfeed = ( QueryFeed ) feed ; result = findQueryFeed ( qfeed . getQueryType ( ) , qfeed . getParameter ( ) ) ; } return result ; } public QueryFeed findQueryFeed ( QueryType type , String parameter ) { return feedsList . findQueryFeed ( type , parameter ) ; } public SearchFeed findSearchFeed ( Query query ) { return feedsList . findSearchFeed ( query ) ; } public synchronized Collection < IGuide > findGuidesByTitle ( String title ) { if ( title == null ) throw new NullPointerException ( Strings . error ( "unspecified.title" ) ) ; List < IGuide > guidesCol = new ArrayList < IGuide > ( ) ; for ( IGuide guide : guides ) { if ( guide . getTitle ( ) . equals ( title ) ) guidesCol . add ( guide ) ; } return guidesCol ; } public synchronized void relocateGuide ( IGuide guide , int position ) { if ( guide == null ) throw new NullPointerException ( Strings . error ( "unspecified.guide" ) ) ; int oldIndex = guides . indexOf ( guide ) ; if ( oldIndex == - 1 ) throw new IllegalArgumentException ( Strings . error ( "guide.does.not.belong.to.the.set" ) ) ; int maxPos = guides . size ( ) - 1 ; if ( position < 0 || position > maxPos ) throw new IndexOutOfBoundsException ( MessageFormat . format ( Strings . error ( "new.guide.position.is.out.of.range" ) , maxPos ) ) ; if ( oldIndex != position ) { if ( guides . remove ( guide ) ) { guides . add ( position , guide ) ; fireGuideMoved ( guide , oldIndex , position ) ; } } } public void addListener ( IGuidesSetListener listener ) { if ( listener == null ) throw new NullPointerException ( Strings . error ( "unspecified.listener" ) ) ; if ( ! listeners . contains ( listener ) ) listeners . add ( listener ) ; } public void removeListener ( IGuidesSetListener listener ) { if ( listener == null ) throw new NullPointerException ( Strings . error ( "unspecified.listener" ) ) ; listeners . remove ( listener ) ; } private void fireGuideAdded ( IGuide guide , boolean lastInBatch ) { for ( IGuidesSetListener listener : listeners ) listener . guideAdded ( this , guide , lastInBatch ) ; } private void fireGuideRemoved ( IGuide guide , int index ) { for ( IGuidesSetListener listener : listeners ) listener . guideRemoved ( this , guide , index ) ; } private void fireGuideMoved ( IGuide guide , int oldIndex , int newIndex ) { for ( IGuidesSetListener listener : listeners ) listener . guideMoved ( this , guide , oldIndex , newIndex ) ; } public synchronized void clear ( ) { int count = getGuidesCount ( ) ; for ( int i = count - 1 ; i >= 0 ; i -- ) { remove ( getGuideAt ( i ) ) ; } } public synchronized void clean ( ) { for ( IGuide guide : guides ) guide . clean ( ) ; } public synchronized int countFeeds ( ) { int count = 0 ; int guidesCount = getGuidesCount ( ) ; for ( int i = 0 ; i < guidesCount ; i ++ ) count += getGuideAt ( i ) . getFeedsCount ( ) ; return count ; } public Collection < URL > getFeedsXmlURLs ( ) { int count = feedsList . getFeedsCount ( ) ; Set < URL > urls = new TreeSet < URL > ( new StringComparator < URL > ( ) ) ; for ( int i = 0 ; i < count ; i ++ ) { IFeed feed = feedsList . getFeedAt ( i ) ; if ( feed instanceof NetworkFeed ) { URL xmlURL = ( ( NetworkFeed ) feed ) . getXmlURL ( ) ; if ( xmlURL != null ) urls . add ( xmlURL ) ; } } return urls ; } public IGuide getGuideByPublishingTitle ( String publishingTitle ) { IGuide guide = null ; for ( int i = 0 ; guide == null && i < guides . size ( ) ; i ++ ) { IGuide iguide = guides . get ( i ) ; String title = iguide . getPublishingTitle ( ) ; if ( title != null && title . equalsIgnoreCase ( publishingTitle ) ) guide = iguide ; } return guide ; } public List < IFeed > getFeeds ( ) { List < IFeed > feedsList = new ArrayList < IFeed > ( ) ; StandardGuide [ ] allGuides = getStandardGuides ( null ) ; for ( StandardGuide guide : allGuides ) { IFeed [ ] feeds = guide . getFeeds ( ) ; for ( IFeed feed : feeds ) if ( ! feedsList . contains ( feed ) ) feedsList . add ( feed ) ; } return feedsList ; } public synchronized IGuide [ ] getGuides ( ) { return guides . toArray ( new IGuide [ guides . size ( ) ] ) ; } private void onSyncCompletion ( boolean syncOut ) { long time = System . currentTimeMillis ( ) ; StandardGuide [ ] allGuides = getStandardGuides ( null ) ; for ( StandardGuide guide : allGuides ) guide . onSyncCompletion ( time , syncOut ) ; } public void onSyncOutCompletion ( ) { onSyncCompletion ( true ) ; } public void onSyncInCompletion ( ) { onSyncCompletion ( false ) ; } public synchronized int countPublishedGuides ( ) { int cnt = 0 ; for ( IGuide guide : guides ) { if ( guide . isPublishingEnabled ( ) ) cnt ++ ; } return cnt ; } public void invalidateFeedVisibilityCaches ( ) { List < IFeed > feeds = feedsList . getFeeds ( ) ; for ( IFeed feed : feeds ) { feed . invalidateVisibilityCache ( ) ; } } public static void replaceFeed ( IFeed feed , IFeed replacement ) { if ( feed instanceof DirectFeed && replacement instanceof DirectFeed ) { DirectFeed dfeeds = ( DirectFeed ) feed ; DirectFeed dreplacement = ( DirectFeed ) replacement ; ReadingList [ ] lists = dfeeds . getReadingLists ( ) ; for ( ReadingList list : lists ) { list . remove ( dfeeds ) ; list . add ( dreplacement ) ; } } IGuide [ ] parentGuides = feed . getParentGuides ( ) ; for ( IGuide parentGuide : parentGuides ) { if ( parentGuide . remove ( feed ) ) parentGuide . add ( replacement ) ; } } public StandardGuide findGuideByID ( Long id ) { if ( id != null ) { for ( IGuide g : guides ) { if ( g . getID ( ) == id && ( g instanceof StandardGuide ) ) return ( StandardGuide ) g ; } } return null ; } public IFeed findFeedByID ( long id ) { List < IFeed > feeds = getFeedsList ( ) . getFeeds ( ) ; for ( IFeed feed : feeds ) { if ( id == feed . getID ( ) ) return feed ; } return null ; } private class GuidesListener extends GuideAdapter { public void feedAdded ( IGuide guide , IFeed feed ) { feedsList . add ( feed ) ; } public void feedRemoved ( FeedRemovedEvent event ) { IFeed feed = event . getFeed ( ) ; if ( ! feed . isDynamic ( ) && feed . getParentGuides ( ) . length == 0 ) feedsList . remove ( feed ) ; } } } 
=======
public interface NameClassVisitor { void visitChoice ( NameClass nc1 , NameClass nc2 ) ; void visitNsName ( String ns ) ; void visitNsNameExcept ( String ns , NameClass nc ) ; void visitAnyName ( ) ; void visitAnyNameExcept ( NameClass nc ) ; void visitName ( Name name ) ; void visitNull ( ) ; void visitError ( ) ; } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
