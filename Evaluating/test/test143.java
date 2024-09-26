public class SearchEngine { private static final Executor executor ; private final SearchResult result ; private GuidesSet guidesSet ; static { executor = new ThreadedExecutor ( ) ; } public SearchEngine ( ) { result = new SearchResult ( ) ; } public void setGuidesSet ( GuidesSet aGuidesSet ) { guidesSet = aGuidesSet ; } public ISearchResult getResult ( ) { return result ; } public void setSearchText ( final String text , final boolean pinnedArticlesOnly ) { result . removeAll ( ) ; if ( guidesSet != null && StringUtils . isNotEmpty ( text ) ) { Runnable task = new Runnable ( ) { public void run ( ) { doSearch ( text . trim ( ) , pinnedArticlesOnly ) ; } } ; try { executor . execute ( task ) ; } catch ( InterruptedException e ) { task . run ( ) ; } } else finished ( ) ; } private void doSearch ( String text , boolean pinnedArticlesOnly ) { try { SearchMatcher matcher = createMatcher ( text , pinnedArticlesOnly ) ; int guidesCnt = guidesSet . getGuidesCount ( ) ; for ( int g = 0 ; g < guidesCnt ; g ++ ) { IGuide guide = guidesSet . getGuideAt ( g ) ; if ( matcher . matches ( guide ) ) addItem ( guide ) ; } FeedsList feedsList = guidesSet . getFeedsList ( ) ; for ( int f = 0 ; f < feedsList . getFeedsCount ( ) ; f ++ ) { IFeed feed = feedsList . getFeedAt ( f ) ; if ( matcher . matches ( feed ) ) addItem ( feed ) ; } for ( int f = 0 ; f < feedsList . getFeedsCount ( ) ; f ++ ) { IFeed feed = feedsList . getFeedAt ( f ) ; if ( feed instanceof DataFeed ) { IArticle [ ] articles = feed . getArticles ( ) ; for ( IArticle article : articles ) { if ( matcher . matches ( article ) ) addItem ( article ) ; } } } } finally { finished ( ) ; } } private void finished ( ) { result . fireFinished ( ) ; } private void addItem ( final Object item ) { result . addItem ( item ) ; } static SearchMatcher createMatcher ( String pattern , boolean aPinnedArticlesOnly ) { SearchMatcher matcher ; if ( isComplexSeachPattern ( pattern ) ) { matcher = new RegexMatcher ( pattern , aPinnedArticlesOnly ) ; } else { matcher = new SimpleMatcher ( pattern , aPinnedArticlesOnly ) ; } return matcher ; } public static boolean isComplexSeachPattern ( String pattern ) { return pattern . indexOf ( '"' ) != - 1 || pattern . indexOf ( '*' ) != - 1 || pattern . indexOf ( '+' ) != - 1 ; } static abstract class SearchMatcher { private final boolean pinnedArticlesOnly ; protected SearchMatcher ( boolean pinnedArticlesOnly ) { this . pinnedArticlesOnly = pinnedArticlesOnly ; } public boolean matches ( IGuide guide ) { return ! pinnedArticlesOnly && matches ( guide . getTitle ( ) . toLowerCase ( ) ) ; } public boolean matches ( IFeed feed ) { return ! pinnedArticlesOnly && matches ( feed . getTitle ( ) . toLowerCase ( ) ) ; } public boolean matches ( IArticle article ) { if ( pinnedArticlesOnly && ! article . isPinned ( ) ) return false ; String title = article . getTitle ( ) . toLowerCase ( ) ; boolean matches = matches ( title ) ; if ( ! matches ) { matches = matches ( TextProcessor . toPlainText ( article . getPlainText ( ) ) ) ; } return matches ; } protected abstract boolean matches ( String text ) ; } private static class SimpleMatcher extends SearchMatcher { private final String pattern ; public SimpleMatcher ( String aPattern , boolean aPinnedArticlesOnly ) { super ( aPinnedArticlesOnly ) ; pattern = aPattern ; } protected boolean matches ( String text ) { return text . indexOf ( pattern ) != - 1 ; } } private static class RegexMatcher extends SearchMatcher { private final Pattern pattern ; public RegexMatcher ( String aPattern , boolean aPinnedArticlesOnly ) { super ( aPinnedArticlesOnly ) ; String regex = StringUtils . keywordsToPattern ( aPattern . toLowerCase ( ) . trim ( ) ) ; pattern = regex == null ? null : Pattern . compile ( regex , Pattern . CASE_INSENSITIVE ) ; } protected boolean matches ( String text ) { return pattern != null && pattern . matcher ( text ) . find ( ) ; } } private static class SearchResult implements ISearchResult { private final List < ResultItem > items = new ArrayList < ResultItem > ( ) ; private final List < ISearchResultListener > listeners = new ArrayList < ISearchResultListener > ( ) ; public int getItemsCount ( ) { return items . size ( ) ; } public ResultItem getItem ( int index ) { return items . get ( index ) ; } public void addChangesListener ( ISearchResultListener l ) { if ( ! listeners . contains ( l ) ) listeners . add ( l ) ; } public void removeChangesListener ( ISearchResultListener l ) { listeners . remove ( l ) ; } public void removeAll ( ) { items . clear ( ) ; fireItemsRemoved ( ) ; } public void addItem ( Object item ) { ResultItem it = new ResultItem ( item ) ; boolean present = items . contains ( it ) ; if ( ! present ) { items . add ( it ) ; fireItemAdded ( it ) ; } } private void fireItemAdded ( ResultItem item ) { int index = items . indexOf ( item ) ; for ( ISearchResultListener listener : listeners ) { listener . itemAdded ( this , item , index ) ; } } private void fireItemsRemoved ( ) { for ( ISearchResultListener listener : listeners ) { listener . itemsRemoved ( this ) ; } } public void fireFinished ( ) { for ( ISearchResultListener listener : listeners ) { listener . finished ( this ) ; } } } } 