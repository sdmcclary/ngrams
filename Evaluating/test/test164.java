public abstract class AbstractFeedDisplay extends JPanel implements IFeedDisplay { private static final Logger LOG = Logger . getLogger ( AbstractFeedDisplay . class . getName ( ) ) ; private boolean popupTriggered ; protected enum SelectionMode { SINGLE , TOGGLE , RANGE } private final List < IFeedDisplayListener > listeners ; private final IFeedDisplayConfig config ; protected final ArticlesGroup [ ] groups ; protected final FeedDisplayModel model ; protected final NoContentPanel noContentPanel ; private final ValueModel pageModel ; protected JViewport viewport ; protected URL hoveredLink ; protected IArticleDisplay selectedDisplay ; protected List < IArticleDisplay > selectedDisplays ; private boolean articleSelectionSource ; protected AbstractFeedDisplay ( IFeedDisplayConfig aConfig , ValueModel pageModel , ValueModel pageCountModel ) { this . pageModel = pageModel ; listeners = new CopyOnWriteArrayList < IFeedDisplayListener > ( ) ; config = aConfig ; if ( aConfig != null ) { config . setListener ( new ConfigListener ( ) ) ; model = new FeedDisplayModel ( pageCountModel ) ; onFilterChange ( ) ; model . addListener ( new ModelListener ( ) ) ; groups = new ArticlesGroup [ model . getGroupsCount ( ) ] ; for ( int i = 0 ; i < model . getGroupsCount ( ) ; i ++ ) { groups [ i ] = new ArticlesGroup ( model . getGroupName ( i ) , config . getGroupPopupAdapter ( ) ) ; groups [ i ] . setFont ( config . getGroupDividerFont ( ) ) ; } updateGroupsSettings ( ) ; updateSortingOrder ( ) ; selectedDisplays = new IdentityList < IArticleDisplay > ( ) ; selectedDisplay = null ; hoveredLink = null ; noContentPanel = new NoContentPanel ( getNoContentPanelMessage ( ) ) ; noContentPanel . setBackground ( config . getDisplayBGColor ( ) ) ; updateNoContentPanel ( ) ; } else { model = null ; groups = null ; noContentPanel = null ; } } protected static SelectionMode eventToMode ( MouseEvent e ) { SelectionMode mode = SelectionMode . SINGLE ; int mod = e . getModifiersEx ( ) ; int ctrl = MouseEvent . CTRL_DOWN_MASK ; int shift = MouseEvent . SHIFT_DOWN_MASK ; if ( ( mod & ctrl ) == ctrl ) { mode = SelectionMode . TOGGLE ; } else if ( ( mod & shift ) == shift ) { mode = SelectionMode . RANGE ; } return mode ; } public void setPage ( int page ) { if ( model != null ) { model . setPage ( page ) ; pageModel . setValue ( page ) ; scrollToTop ( ) ; } } public void setPageSize ( int size ) { if ( model != null ) model . setPageSize ( size ) ; } private void updateSortingOrder ( ) { IFeed feed = model . getFeed ( ) ; if ( feed != null && feed . getAscendingSorting ( ) != null ) { setAscending ( feed . getAscendingSorting ( ) ) ; } else setAscending ( config . isAscendingSorting ( ) ) ; } public boolean isArticleSelectionSource ( ) { return articleSelectionSource ; } private void updateNoContentPanel ( ) { if ( noContentPanel == null || config == null ) return ; boolean visible = ! config . showEmptyGroups ( ) && ! model . hasVisibleArticles ( ) ; noContentPanel . setVisible ( visible ) ; if ( visible ) noContentPanel . setMessage ( getNoContentPanelMessage ( ) ) ; } protected String getNoContentPanelMessage ( ) { return null ; } protected Rectangle rectToNoContentBounds ( Rectangle r ) { return r ; } public void prepareForDismiss ( ) { if ( model != null ) model . prepareToDismiss ( ) ; if ( config != null ) config . setListener ( null ) ; setViewport ( null ) ; } public void setViewport ( JViewport aViewport ) { viewport = aViewport ; if ( noContentPanel != null ) noContentPanel . setViewport ( aViewport ) ; } public JComponent getComponent ( ) { return this ; } public void addListener ( IFeedDisplayListener l ) { if ( ! listeners . contains ( l ) ) listeners . add ( l ) ; } public void removeListener ( IFeedDisplayListener l ) { listeners . remove ( l ) ; } protected void fireArticleSelected ( IArticle lead , IArticle [ ] selectedArticles ) { for ( IFeedDisplayListener l : listeners ) l . articleSelected ( lead , selectedArticles ) ; } protected void fireLinkHovered ( URL link ) { for ( IFeedDisplayListener l : listeners ) l . linkHovered ( link ) ; } protected void fireLinkClicked ( URL link ) { for ( IFeedDisplayListener l : listeners ) l . linkClicked ( link ) ; } protected void fireFeedJumpLinkClicked ( IFeed feed ) { for ( IFeedDisplayListener l : listeners ) l . feedJumpLinkClicked ( feed ) ; } protected void fireZoomIn ( ) { for ( IFeedDisplayListener l : listeners ) l . onZoomIn ( ) ; } protected void fireZoomOut ( ) { for ( IFeedDisplayListener l : listeners ) l . onZoomOut ( ) ; } public IFeedDisplayConfig getConfig ( ) { return config ; } public void focus ( ) { requestFocusInWindow ( ) ; } public boolean requestFocusInWindow ( ) { boolean focused = true ; if ( ( selectedDisplay == null && ! this . selectFirstArticle ( getConfig ( ) . getViewMode ( ) ) ) || ! selectedDisplay . focus ( ) ) { focused = super . requestFocusInWindow ( ) ; } return focused ; } private IArticleDisplay findDisplay ( IArticle aArticle , int aGroup , int aIndexInGroup ) { IArticleDisplay display ; int index = getDisplayIndex ( aGroup , aIndexInGroup ) ; Component cmp = index < getComponentCount ( ) ? getComponent ( index ) : null ; display = cmp == null || ! ( cmp instanceof IArticleDisplay ) ? null : ( IArticleDisplay ) cmp ; if ( display != null && display . getArticle ( ) != aArticle ) { getLogger ( ) . severe ( MessageFormat . format ( Strings . error ( "ui.wrong.article.has.been.found" ) , aGroup , aIndexInGroup ) ) ; display = null ; } if ( display == null ) { getLogger ( ) . severe ( MessageFormat . format ( Strings . error ( "ui.missing.display" ) , index , aGroup , aIndexInGroup ) ) ; display = findArticleDisplay ( aArticle ) ; } if ( display == null ) getLogger ( ) . severe ( Strings . error ( "ui.display.was.not.found" ) ) ; return display ; } protected IArticleDisplay findArticleDisplay ( IArticle aArticle ) { IArticleDisplay aDisplay = null ; for ( int i = 0 ; aDisplay == null && i < getComponentCount ( ) ; i ++ ) { Component cmp = getComponent ( i ) ; if ( cmp instanceof IArticleDisplay ) { IArticleDisplay dsp = ( IArticleDisplay ) cmp ; if ( dsp . getArticle ( ) == aArticle ) aDisplay = dsp ; } } return aDisplay ; } protected abstract Logger getLogger ( ) ; public boolean selectNextArticle ( int mode ) { return selectNextArticle ( mode , selectedDisplay ) ; } public boolean selectFirstArticle ( int mode ) { boolean selected = selectNextArticle ( mode , null ) ; if ( selected ) ensureSelectedViewDisplayed ( ) ; return selected ; } public boolean selectPreviousArticle ( int mode ) { return selectPreviousArticle ( selectedDisplay , mode ) ; } public boolean selectLastArticle ( int mode ) { boolean selected = selectPreviousArticle ( null , mode ) ; if ( selected ) ensureSelectedViewDisplayed ( ) ; return selected ; } public void setFeed ( IFeed feed ) { selectDisplay ( null , false , SelectionMode . SINGLE ) ; model . setFeed ( feed ) ; updateSortingOrder ( ) ; if ( viewport != null ) { if ( viewport instanceof JumplessViewport ) ( ( JumplessViewport ) viewport ) . resetStoredPosition ( ) ; scrollTo ( new Rectangle ( viewport . getWidth ( ) , viewport . getHeight ( ) ) ) ; } } public String getSelectedText ( ) { return null ; } public void repaintHighlights ( ) { Iterator it = new ArticleDisplayIterator ( ) ; while ( it . hasNext ( ) ) { IArticleDisplay display = ( IArticleDisplay ) it . next ( ) ; display . updateHighlights ( ) ; } } public void repaintSentimentsColorCodes ( ) { Iterator it = new ArticleDisplayIterator ( ) ; while ( it . hasNext ( ) ) { IArticleDisplay display = ( IArticleDisplay ) it . next ( ) ; display . updateColorCode ( ) ; } } private boolean selectNextArticle ( int mode , IArticleDisplay currentDisplay ) { boolean selected = false ; IArticleDisplay display = findNextDisplay ( currentDisplay , mode ) ; if ( display != null ) { selectDisplay ( display , true , SelectionMode . SINGLE ) ; selected = true ; } else { int pages = model . getPagesCount ( ) ; int page = model . getPage ( ) ; if ( page < pages - 1 ) { setPage ( page + 1 ) ; selected = selectFirstArticle ( mode ) ; } } return selected ; } private boolean selectPreviousArticle ( IArticleDisplay currentDisplay , int mode ) { boolean selected = false ; IArticleDisplay display = findPrevDisplay ( currentDisplay , mode ) ; if ( display != null ) { selectDisplay ( display , true , SelectionMode . SINGLE ) ; selected = true ; } else { int page = model . getPage ( ) ; if ( page > 0 ) { setPage ( page - 1 ) ; selected = selectLastArticle ( mode ) ; } } return selected ; } protected void selectDisplay ( IArticleDisplay display , boolean forceScroll , SelectionMode mode ) { boolean fireEvent = selectDisplayWithoutEvent ( display , forceScroll , mode ) ; if ( fireEvent ) { try { articleSelectionSource = true ; fireArticleSelected ( getSelectedArticle ( ) , getSelectedArticles ( ) ) ; } finally { articleSelectionSource = false ; } } } private IArticle [ ] getSelectedArticles ( ) { IArticle [ ] articles = new IArticle [ selectedDisplays . size ( ) ] ; int i = 0 ; for ( IArticleDisplay display : selectedDisplays ) { articles [ i ++ ] = display . getArticle ( ) ; } return articles ; } protected boolean selectDisplayWithoutEvent ( IArticleDisplay display , boolean forceScroll , SelectionMode mode ) { boolean fireEvent ; if ( mode == SelectionMode . SINGLE ) { fireEvent = processSingleSelectionMode ( display , forceScroll ) ; } else if ( mode == SelectionMode . TOGGLE ) { fireEvent = processToggleSelectionMode ( display ) ; } else { if ( selectedDisplay == null || selectedDisplay == display ) { fireEvent = processSingleSelectionMode ( display , forceScroll ) ; } else { int newLeadIndex = indexOf ( display . getComponent ( ) ) ; int oldLeadIndex = indexOf ( selectedDisplay . getComponent ( ) ) ; if ( newLeadIndex == - 1 || oldLeadIndex == - 1 ) { fireEvent = processToggleSelectionMode ( display ) ; } else { IArticleDisplay [ ] current = selectedDisplays . toArray ( new IArticleDisplay [ selectedDisplays . size ( ) ] ) ; selectedDisplays . clear ( ) ; int min = Math . min ( oldLeadIndex , newLeadIndex ) ; int max = Math . max ( oldLeadIndex , newLeadIndex ) ; for ( int i = min ; i <= max ; i ++ ) { Component comp = getComponent ( i ) ; if ( comp instanceof IArticleDisplay ) { IArticleDisplay disp = ( IArticleDisplay ) comp ; disp . setSelected ( true ) ; selectedDisplays . add ( disp ) ; } } for ( IArticleDisplay disp : current ) { if ( ! selectedDisplays . contains ( disp ) ) disp . setSelected ( false ) ; } selectedDisplay = display ; fireEvent = true ; } } } if ( selectedDisplay != null ) requestFocusInWindow ( ) ; return fireEvent ; } private boolean processToggleSelectionMode ( IArticleDisplay display ) { boolean fireEvent ; boolean displayIsSelected = selectedDisplays . contains ( display ) ; display . setSelected ( ! displayIsSelected ) ; if ( displayIsSelected ) { selectedDisplays . remove ( display ) ; if ( display == selectedDisplay ) { selectedDisplay = selectedDisplays . size ( ) == 0 ? null : selectedDisplays . get ( 0 ) ; } } else { selectedDisplays . add ( display ) ; selectedDisplay = display ; } fireEvent = true ; return fireEvent ; } private boolean processSingleSelectionMode ( IArticleDisplay display , boolean forceScroll ) { boolean fireEvent ; fireEvent = selectedDisplays . size ( ) > 1 || selectedDisplay != display ; for ( IArticleDisplay disp : selectedDisplays ) if ( disp != display ) disp . setSelected ( false ) ; selectedDisplays . clear ( ) ; if ( display != null ) selectedDisplays . add ( display ) ; if ( display != selectedDisplay ) { selectedDisplay = display ; if ( selectedDisplay != null ) { selectedDisplay . setSelected ( true ) ; if ( forceScroll || hoveredLink == null ) ensureSelectedViewDisplayed ( ) ; } } return fireEvent ; } private int indexOf ( Component component ) { int index = - 1 ; Component [ ] components = getComponents ( ) ; for ( int i = 0 ; index == - 1 && i < components . length ; i ++ ) { if ( components [ i ] == component ) index = i ; } return index ; } private IArticleDisplay findNextDisplay ( IArticleDisplay currentDisplay , int aMode ) { IArticleDisplay nextDisplay = null ; int currentIndex = currentDisplay == null ? - 1 : indexOf ( currentDisplay . getComponent ( ) ) ; for ( int i = currentIndex + 1 ; nextDisplay == null && i < getComponentCount ( ) ; i ++ ) { Component comp = getComponent ( i ) ; if ( comp instanceof IArticleDisplay ) { IArticleDisplay display = ( IArticleDisplay ) comp ; if ( display . getComponent ( ) . isVisible ( ) && fitsMode ( display . getArticle ( ) , aMode ) ) { nextDisplay = display ; } } } return nextDisplay ; } private IArticleDisplay findPrevDisplay ( IArticleDisplay currentDisplay , int aMode ) { IArticleDisplay prevDisplay = null ; int currentIndex = currentDisplay == null ? getComponentCount ( ) : indexOf ( currentDisplay . getComponent ( ) ) ; for ( int i = currentIndex - 1 ; prevDisplay == null && i >= 0 ; i -- ) { Component comp = getComponent ( i ) ; if ( comp instanceof IArticleDisplay ) { IArticleDisplay display = ( IArticleDisplay ) comp ; if ( display . getComponent ( ) . isVisible ( ) && fitsMode ( display . getArticle ( ) , aMode ) ) { prevDisplay = ( IArticleDisplay ) comp ; } } } return prevDisplay ; } private void onArticlesRemoved ( ) { Component [ ] components = getComponents ( ) ; for ( Component component : components ) { if ( component instanceof ArticlesGroup ) { ( ( ArticlesGroup ) component ) . unregisterAll ( ) ; } else if ( component instanceof IArticleDisplay ) { remove ( component ) ; IArticleDisplay display = ( ( IArticleDisplay ) component ) ; IArticle article = display . getArticle ( ) ; article . removeListener ( display . getArticleListener ( ) ) ; } } updateNoContentPanel ( ) ; scrollToTop ( ) ; } private void scrollToTop ( ) { Rectangle rect = getVisibleRect ( ) ; rect . y = 0 ; scrollTo ( rect ) ; } private void onArticleAdded ( IArticle aArticle , int aGroup , int aIndexInGroup ) { updateNoContentPanel ( ) ; IArticleDisplay display = createNewArticleDisplay ( aArticle ) ; display . addHyperlinkListener ( new LinkListener ( ) ) ; Component component = display . getComponent ( ) ; component . setVisible ( false ) ; int index = getDisplayIndex ( aGroup , aIndexInGroup ) ; try { add ( component , index ) ; groups [ aGroup ] . register ( display ) ; aArticle . addListener ( display . getArticleListener ( ) ) ; } catch ( Exception e ) { LOG . log ( Level . SEVERE , "Failed to add article at: " + index + " (group=" + aGroup + ", ingroup=" + aIndexInGroup + ", groupIndex=" + indexOf ( groups [ aGroup ] ) + ", components=" + getComponentCount ( ) + ")" ) ; } } private void onArticleRemoved ( IArticle aArticle , int aGroup , int aIndexInGroup ) { IArticleDisplay display = findDisplay ( aArticle , aGroup , aIndexInGroup ) ; if ( display == null ) return ; Component dispComponent = display . getComponent ( ) ; boolean wasVisible = dispComponent . isVisible ( ) ; dispComponent . setVisible ( false ) ; if ( selectedDisplay != null ) { if ( selectedDisplay == display ) { selectDisplay ( null , false , SelectionMode . SINGLE ) ; } else if ( wasVisible ) { Rectangle boundsDis = dispComponent . getBounds ( ) ; Rectangle boundsView = viewport . getViewRect ( ) ; int delta = boundsView . y - boundsDis . y ; if ( delta > 0 ) { boundsView . y -= delta ; scrollTo ( boundsView ) ; } } } remove ( dispComponent ) ; groups [ aGroup ] . unregister ( display ) ; aArticle . removeListener ( display . getArticleListener ( ) ) ; updateNoContentPanel ( ) ; } protected abstract IArticleDisplay createNewArticleDisplay ( IArticle aArticle ) ; private void setHoveredHyperLink ( URL link ) { if ( hoveredLink == link ) return ; hoveredLink = link ; fireLinkHovered ( hoveredLink ) ; } public void setAscending ( boolean asc ) { model . setAscending ( asc ) ; for ( int i = 0 ; i < groups . length ; i ++ ) { groups [ i ] . setName ( model . getGroupName ( i ) ) ; } } protected void collapseAll ( boolean aCollapsing ) { if ( model . getArticlesCount ( ) > 0 ) { for ( int i = 0 ; i < getComponentCount ( ) ; i ++ ) { Component component = getComponent ( i ) ; if ( component instanceof IArticleDisplay ) { IArticleDisplay display = ( IArticleDisplay ) component ; display . setCollapsed ( aCollapsing ) ; } } if ( aCollapsing ) requestFocus ( ) ; else requestFocusInWindow ( ) ; } } public void cycleViewModeForward ( ) { cycleViewMode ( true , true ) ; } public void cycleViewModeBackward ( ) { cycleViewMode ( true , false ) ; } protected void cycleViewMode ( boolean global , boolean forward ) { int cvm = 0 ; if ( selectedDisplay != null ) { cvm = selectedDisplay . getViewMode ( ) ; } else { IArticleDisplay display = findNextDisplay ( null , INavigationModes . MODE_NORMAL ) ; cvm = ( display != null ) ? display . getViewMode ( ) : config . getViewMode ( ) ; } int nvm = cvm + ( forward ? 1 : - 1 ) ; if ( nvm < MODE_MINIMAL ) nvm = MODE_FULL ; else if ( nvm > MODE_FULL ) nvm = MODE_MINIMAL ; if ( global ) { Iterator < IArticleDisplay > it = new ArticleDisplayIterator ( ) ; while ( it . hasNext ( ) ) it . next ( ) . setViewMode ( nvm ) ; } else if ( selectedDisplay != null ) { selectedDisplay . setViewMode ( nvm ) ; } } protected void collapseSelected ( boolean aCollapsing ) { if ( selectedDisplays . size ( ) > 0 ) { for ( IArticleDisplay display : selectedDisplays ) display . setCollapsed ( aCollapsing ) ; if ( aCollapsing ) requestFocus ( ) ; else requestFocusInWindow ( ) ; } } private void updateGroupsSettings ( ) { for ( ArticlesGroup group : groups ) { group . setCanBeVisible ( config . showGroups ( ) ) ; group . setVisibleIfEmpty ( config . showEmptyGroups ( ) ) ; } updateNoContentPanel ( ) ; } private void scrollTo ( final Rectangle rect ) { Container parent = getParent ( ) ; if ( parent != null && parent instanceof IScrollContoller ) { ( ( IScrollContoller ) parent ) . scrollTo ( rect ) ; } else scrollRectToVisible ( rect ) ; } private int getDisplayIndex ( int aGroup , int aIndexInGroup ) { return indexOf ( groups [ aGroup ] ) + aIndexInGroup + 1 ; } private IArticle getSelectedArticle ( ) { return selectedDisplay == null ? null : selectedDisplay . getArticle ( ) ; } private void ensureSelectedViewDisplayed ( ) { Rectangle portRect = viewport . getViewRect ( ) ; Component component = selectedDisplay . getComponent ( ) ; Rectangle rect = component . getBounds ( ) ; boolean includesGroup = false ; if ( config . showGroups ( ) ) { int index = indexOf ( component ) ; if ( index > 0 && getComponent ( index - 1 ) instanceof ArticlesGroup ) { component = getComponent ( index - 1 ) ; Rectangle groupRect = component . getBounds ( ) ; rect . setBounds ( groupRect . x , groupRect . y , rect . width , rect . height + ( rect . y - groupRect . y ) ) ; includesGroup = true ; } } int portY = ( int ) portRect . getY ( ) ; int portH = ( int ) portRect . getHeight ( ) ; int viewY = ( int ) rect . getY ( ) ; int viewH = ( int ) rect . getHeight ( ) ; int portB = portY + portH ; int viewB = viewY + viewH ; boolean invisible = viewB < portY || viewY > portB ; boolean coveringPort = ! invisible && viewY <= portY && viewB >= portB ; if ( coveringPort ) { rect = null ; } else if ( invisible || viewH > portH || viewY < portY || viewB > portB ) { if ( ! includesGroup ) rect . y = Math . max ( rect . y - 10 , 0 ) ; rect . width = viewport . getWidth ( ) ; rect . height = viewport . getHeight ( ) ; } if ( viewport instanceof JumplessViewport ) { JumplessViewport jvp = ( JumplessViewport ) viewport ; jvp . resetStoredPosition ( ) ; } if ( rect != null ) scrollTo ( rect ) ; } private boolean fitsMode ( IArticle aArticle , int aMode ) { return aMode == INavigationModes . MODE_NORMAL || ( aMode == INavigationModes . MODE_UNREAD && ! aArticle . isRead ( ) ) ; } public void selectArticle ( IArticle article ) { int newPage = model . ensureArticleVisibility ( article ) ; if ( newPage != - 1 ) pageModel . setValue ( newPage ) ; final IArticleDisplay display = findArticleDisplay ( article ) ; if ( display != null ) { SwingUtilities . invokeLater ( new Runnable ( ) { public void run ( ) { selectDisplayWithoutEvent ( display , true , SelectionMode . SINGLE ) ; } } ) ; } } public void repaintIfInMode ( boolean briefMode ) { } private class ModelListener implements IFeedDisplayModelListener { public void articlesRemoved ( ) { onArticlesRemoved ( ) ; } public void articleAdded ( IArticle article , int group , int indexInGroup ) { onArticleAdded ( article , group , indexInGroup ) ; } public void articleRemoved ( IArticle article , int group , int indexInGroup ) { onArticleRemoved ( article , group , indexInGroup ) ; } } protected class ArticleDisplayIterator implements Iterator < IArticleDisplay > { private final Component [ ] components ; private int nextView ; public ArticleDisplayIterator ( ) { components = getComponents ( ) ; nextView = findNextView ( - 1 ) ; } public boolean hasNext ( ) { return nextView != - 1 ; } public IArticleDisplay next ( ) { IArticleDisplay display = null ; if ( nextView != - 1 ) { display = ( IArticleDisplay ) components [ nextView ] ; nextView = findNextView ( nextView ) ; } return display ; } private int findNextView ( int aViewIndex ) { int next = - 1 ; for ( int i = aViewIndex + 1 ; next == - 1 && i < components . length ; i ++ ) { Component comp = components [ i ] ; if ( comp instanceof IArticleDisplay ) next = i ; } return next ; } public void remove ( ) { throw new UnsupportedOperationException ( ) ; } } protected void processMouseEvent ( MouseEvent e ) { super . processMouseEvent ( e ) ; Object component = getComponentForMouseEvent ( e ) ; switch ( e . getID ( ) ) { case MouseEvent . MOUSE_PRESSED : popupTriggered = false ; if ( component instanceof IArticleDisplay ) { IArticleDisplay articleDisplay = ( IArticleDisplay ) component ; if ( ! e . isPopupTrigger ( ) || ! selectedDisplays . contains ( articleDisplay ) ) { selectDisplay ( articleDisplay , false , eventToMode ( e ) ) ; } MouseListener popup = ( hoveredLink != null ) ? getLinkPopupAdapter ( ) : getViewPopupAdapter ( ) ; if ( popup != null ) popup . mousePressed ( e ) ; popupTriggered = e . isPopupTrigger ( ) ; } else requestFocus ( ) ; break ; case MouseEvent . MOUSE_RELEASED : if ( component instanceof IArticleDisplay ) { MouseListener popup = ( hoveredLink != null ) ? getLinkPopupAdapter ( ) : getViewPopupAdapter ( ) ; if ( popup != null ) popup . mouseReleased ( e ) ; } break ; case MouseEvent . MOUSE_CLICKED : if ( SwingUtilities . isLeftMouseButton ( e ) && component instanceof IArticleDisplay ) { URL link = null ; IArticle article = null ; if ( hoveredLink != null ) { link = hoveredLink ; } else if ( e . getClickCount ( ) == 2 ) { article = ( ( IArticleDisplay ) component ) . getArticle ( ) ; link = article . getLink ( ) ; } if ( link != null && ! popupTriggered ) fireLinkClicked ( link ) ; if ( article != null ) { GlobalModel model = GlobalModel . SINGLETON ; GlobalController . readArticles ( true , model . getSelectedGuide ( ) , model . getSelectedFeed ( ) , article ) ; } } break ; default : break ; } } protected Object getComponentForMouseEvent ( MouseEvent e ) { return e . getSource ( ) ; } protected MouseListener getViewPopupAdapter ( ) { return null ; } protected MouseListener getLinkPopupAdapter ( ) { return null ; } private void forwardMouseWheelHigher ( MouseWheelEvent e ) { int newX , newY ; newX = e . getX ( ) + getX ( ) ; newY = e . getY ( ) + getY ( ) ; Container parent = getParent ( ) ; if ( parent == null ) return ; newX += parent . getX ( ) ; newY += parent . getY ( ) ; MouseWheelEvent newMWE = new MouseWheelEvent ( parent , e . getID ( ) , e . getWhen ( ) , e . getModifiers ( ) , newX , newY , e . getClickCount ( ) , e . isPopupTrigger ( ) , e . getScrollType ( ) , e . getScrollAmount ( ) , e . getWheelRotation ( ) ) ; parent . dispatchEvent ( newMWE ) ; } @ Override protected void processMouseWheelEvent ( MouseWheelEvent e ) { if ( ( e . getModifiersEx ( ) & KeyEvent . CTRL_DOWN_MASK ) != 0 && e . getScrollAmount ( ) != 0 ) { boolean in = e . getWheelRotation ( ) > 0 ; if ( in ) fireZoomIn ( ) ; else fireZoomOut ( ) ; } else forwardMouseWheelHigher ( e ) ; } private class LinkListener implements HyperlinkListener { public void hyperlinkUpdate ( HyperlinkEvent e ) { HyperlinkEvent . EventType type = e . getEventType ( ) ; if ( type != HyperlinkEvent . EventType . ACTIVATED ) { URL link = ( type == HyperlinkEvent . EventType . ENTERED ) ? e . getURL ( ) : null ; setHoveredHyperLink ( link ) ; JComponent textPane = ( JComponent ) e . getSource ( ) ; String tooltip = getHoveredLinkTooltip ( link , textPane ) ; textPane . setToolTipText ( tooltip ) ; } } } protected String getHoveredLinkTooltip ( URL link , JComponent textPane ) { return null ; } protected class ConfigListener implements PropertyChangeListener { public void propertyChange ( PropertyChangeEvent evt ) { onConfigPropertyChange ( evt . getPropertyName ( ) ) ; } } protected void onConfigPropertyChange ( String name ) { if ( IFeedDisplayConfig . THEME . equals ( name ) ) { onThemeChange ( ) ; } else if ( IFeedDisplayConfig . FILTER . equals ( name ) ) { if ( ArticleFilterProtector . canSwitchTo ( config . getFilter ( ) ) ) onFilterChange ( ) ; } else if ( IFeedDisplayConfig . MODE . equals ( name ) ) { onViewModeChange ( ) ; } else if ( IFeedDisplayConfig . SORT_ORDER . equals ( name ) ) { updateSortingOrder ( ) ; } else if ( IFeedDisplayConfig . GROUPS_VISIBLE . equals ( name ) || IFeedDisplayConfig . EMPTY_GROUPS_VISIBLE . equals ( name ) ) { updateGroupsSettings ( ) ; } else if ( IFeedDisplayConfig . FONT_BIAS . equals ( name ) ) { onFontBiasChange ( ) ; } } protected void onFilterChange ( ) { model . setFilter ( config . getFilter ( ) ) ; updateNoContentPanel ( ) ; } protected void onThemeChange ( ) { if ( noContentPanel != null ) noContentPanel . setBackground ( config . getDisplayBGColor ( ) ) ; Iterator it = new ArticleDisplayIterator ( ) ; while ( it . hasNext ( ) ) ( ( IArticleDisplay ) it . next ( ) ) . onThemeChange ( ) ; for ( ArticlesGroup group : groups ) group . setFont ( config . getGroupDividerFont ( ) ) ; } protected void onViewModeChange ( ) { Iterator it = new ArticleDisplayIterator ( ) ; while ( it . hasNext ( ) ) ( ( IArticleDisplay ) it . next ( ) ) . onViewModeChange ( ) ; } private void onFontBiasChange ( ) { Iterator it = new ArticleDisplayIterator ( ) ; while ( it . hasNext ( ) ) ( ( IArticleDisplay ) it . next ( ) ) . onFontBiasChange ( ) ; } private class NoContentPanel extends JPanel { private final ViewportSizeMonitor monitor ; private final JLabel lbMessage ; private JViewport viewport ; public NoContentPanel ( String aMessage ) { setLayout ( new FormLayout ( "5dlu, center:p:grow, 5dlu" , "5dlu:grow, p, 5dlu:grow" ) ) ; CellConstraints cc = new CellConstraints ( ) ; lbMessage = new JLabel ( aMessage ) ; add ( lbMessage , cc . xy ( 2 , 2 ) ) ; monitor = new ViewportSizeMonitor ( ) ; } public void setBackground ( Color bg ) { super . setBackground ( bg ) ; if ( lbMessage != null ) lbMessage . setBackground ( bg ) ; } public void setViewport ( JViewport aViewport ) { if ( viewport != null ) viewport . removeComponentListener ( monitor ) ; viewport = aViewport ; if ( viewport != null ) { onViewportResize ( ) ; viewport . addComponentListener ( monitor ) ; } } private void onViewportResize ( ) { Rectangle viewRect = rectToNoContentBounds ( viewport . getViewRect ( ) ) ; Dimension size = new Dimension ( viewRect . width , viewRect . height ) ; setMinimumSize ( size ) ; setPreferredSize ( size ) ; setBounds ( viewRect ) ; } public void setMessage ( String msg ) { lbMessage . setText ( msg ) ; } private class ViewportSizeMonitor extends ComponentAdapter { public void componentResized ( ComponentEvent e ) { onViewportResize ( ) ; } } } } 