<<<<<<< HEAD
public class ZoomableTimeline < T > extends ViewPanel implements ClickListener , DblClickListener { private class BackdropClickConverter implements ClickListener , DblClickListener { public void onClick ( Widget sender ) { if ( getFocusBackdrop ( ) . getLastClickEventCtrl ( ) ) { fireUserEvent ( ) ; } else { setSelected ( null , false ) ; } } public void onDblClick ( Widget sender ) { fireUserEvent ( ) ; } private void fireUserEvent ( ) { if ( backdropListener != null ) { int x = getFocusBackdrop ( ) . getLastClickClientX ( ) ; int y = getFocusBackdrop ( ) . getLastClickClientY ( ) ; backdropListener . onBackdropUserEvent ( x , y ) ; } } } private BackdropListener backdropListener ; private static final int NUM_LABELS = 5 ; private static final int WINDOW_GUTTER = 7 ; private static final int X_SPREAD = 600 ; private static final int Y_SPREAD = 30 ; private ZoomLevel currentZoom ; private int height ; private List < ProteanLabel > labelList = new ArrayList < ProteanLabel > ( ) ; private Image magBig ; private Image magSmall ; private ZoomLevel oldZoom ; private TimelineRemembersPosition selectedRP ; private GWTSortedMap < TimeLineObj < T > , Object > sorted = new GWTSortedMap < TimeLineObj < T > , Object > ( ) ; private Label whenlabel ; private int width ; private int yEnd ; private int [ ] ySlots ; private boolean ySlotsDirty = false ; private int ySpread ; private int yStart ; private SimplePanel editWidget ; private TimelineController timelineController ; public ZoomableTimeline ( int width , int height , TimelineController timelineController ) { super ( ) ; this . height = height ; this . width = width ; this . timelineController = timelineController ; init ( ) ; setStylePrimaryName ( "ZoomableTL" ) ; setPixelSize ( width , height ) ; setDoYTranslate ( false ) ; setDoZoom ( true ) ; currentZoom = ZoomLevel . Year ; currentScale = currentZoom . getScale ( ) ; createDecorations ( ) ; drawHUD ( ) ; setBackground ( currentScale ) ; BackdropClickConverter bdClickListener = new BackdropClickConverter ( ) ; getFocusBackdrop ( ) . addDblClickListener ( bdClickListener ) ; getFocusBackdrop ( ) . addClickListener ( bdClickListener ) ; } public void setBackdropListener ( BackdropListener backdropListener ) { this . backdropListener = backdropListener ; } public void add ( List < TimeLineObj < T > > timeObjects ) { Log . debug ( "!!!!!Zoom add " + timeObjects . size ( ) + " sorted size " + sorted . size ( ) ) ; for ( TimeLineObj < T > timeLineObj : timeObjects ) { sorted . put ( timeLineObj , null ) ; } super . clear ( ) ; initYSlots ( false ) ; Log . debug ( "addObj " + sorted . size ( ) ) ; for ( TimeLineObj < T > tlo : sorted . keySet ( ) ) { TimelineRemembersPosition rp = timelineController . getTimeLineObjFactory ( ) . getWidget ( this , timelineController , tlo ) ; int slot = getBestSlotFor ( rp ) ; int top = yStart + ( slot * ySpread ) ; rp . setTop ( top ) ; if ( slot < 0 ) { rp . getWidget ( ) . setVisible ( false ) ; } addObject ( rp ) ; } for ( int i = - NUM_LABELS ; i < NUM_LABELS ; i ++ ) { ProteanLabel ll = new ProteanLabel ( i , yStart - 15 ) ; labelList . add ( ll ) ; addObject ( ll ) ; } setCenterOfView ( ) ; updateLabels ( ) ; redraw ( ) ; } private void setCenterOfView ( ) { if ( ! sorted . isEmpty ( ) ) { TimeLineObj < T > last = sorted . getKeyList ( ) . get ( sorted . size ( ) - 1 ) ; Log . debug ( "last " + last ) ; if ( last != null ) { centerOn ( last . getLeft ( ) , 0 ) ; return ; } } centerOn ( TimeLineObj . getLeftForDate ( new Date ( ) ) , 0 ) ; } @ Override public void clear ( ) { super . clear ( ) ; sorted . clear ( ) ; } private void createDecorations ( ) { whenlabel = new Label ( ) ; magBig = ConstHolder . images . magnifyingBig ( ) . createImage ( ) ; magBig . addClickListener ( new ClickListener ( ) { public void onClick ( Widget arg0 ) { zoomIn ( ) ; } } ) ; magSmall = ConstHolder . images . magnifyingSmall ( ) . createImage ( ) ; magSmall . addClickListener ( new ClickListener ( ) { public void onClick ( Widget arg0 ) { zoomOut ( ) ; } } ) ; editWidget = new SimplePanel ( ) ; add ( editWidget ) ; add ( magSmall ) ; add ( whenlabel ) ; add ( magBig ) ; } private void drawHUD ( ) { int center = width / 2 - 50 ; center -= 50 ; int y = yEnd + 30 ; setWidgetPosition ( magSmall , center - 40 , y - 15 ) ; setWidgetPosition ( whenlabel , center , y ) ; setWidgetPosition ( magBig , center + 70 , y - 15 ) ; setWidgetPosition ( editWidget , center + 115 , y - 20 ) ; } private int getBestSlotFor ( TimelineRemembersPosition rp ) { int i = 0 ; int mywidth = ( int ) ( rp . getWidth ( ) / ( double ) getXSpread ( ) / currentScale ) ; for ( ; i < ySlots . length ; i ++ ) { int lastLeftForThisSlot = ySlots [ i ] ; if ( lastLeftForThisSlot < rp . getLeft ( ) ) { ySlots [ i ] = ( int ) ( rp . getLeft ( ) + mywidth ) ; return i ; } } return - 1 ; } @ Override protected int getHeight ( ) { return height ; } public Widget getWidget ( ) { return this ; } @ Override protected int getWidth ( ) { return width ; } @ Override protected int getXSpread ( ) { return X_SPREAD ; } private void init ( ) { yStart = 25 ; yEnd = height - 60 ; ySpread = Y_SPREAD ; ySlots = new int [ ( yEnd - yStart ) / ySpread ] ; initYSlots ( ) ; } private void initYSlots ( ) { initYSlots ( true ) ; } private void initYSlots ( boolean dirty ) { Log . debug ( "ZoomableTimeline.initYSlots(" + dirty + ")" ) ; ySlotsDirty = dirty ; for ( int i = 0 ; i < ySlots . length ; i ++ ) { ySlots [ i ] = Integer . MIN_VALUE ; } } @ Override protected void moveOccurredCallback ( ) { updateLabels ( ) ; ySlotsDirty = false ; } protected void objectHasMoved ( RemembersPosition o , int halfWidth , int halfHeight , int centerX , int centerY ) { if ( o instanceof TimelineRemembersPosition ) { o . zoomToScale ( currentScale ) ; TimelineRemembersPosition tlw = ( TimelineRemembersPosition ) o ; if ( ySlotsDirty ) { int slot = getBestSlotFor ( tlw ) ; if ( slot < 0 ) { o . getWidget ( ) . setVisible ( false ) ; } else { tlw . setTop ( yStart + ( slot * ySpread ) ) ; o . getWidget ( ) . setVisible ( true ) ; } } } } public void onClick ( Widget sender ) { TimelineRemembersPosition rp = ( TimelineRemembersPosition ) sender ; setSelected ( rp , true ) ; } public void onDblClick ( Widget sender ) { } @ Override protected void postZoomCallback ( double currentScale ) { updateLabels ( ) ; } public void resize ( int newWidth , int newHeight ) { width = newWidth ; height = newHeight ; init ( ) ; setPixelSize ( width , height ) ; drawHUD ( ) ; redraw ( ) ; } @ Override protected void setBackground ( double scale ) { ZoomLevel newZoom = ZoomLevel . getZoomForScale ( scale ) ; setBackground ( newZoom ) ; } protected void setBackground ( ZoomLevel zoomLevel ) { if ( oldZoom != null ) { removeStyleDependentName ( oldZoom . getCssClass ( ) ) ; } addStyleDependentName ( currentZoom . getCssClass ( ) ) ; Log . debug ( "SetBackground: " + currentZoom . getCssClass ( ) + "::" + getStyleName ( ) ) ; } public Date setDateFromDrag ( TimeLineObj tlo , TimelineRemembersPosition rp , int clientX , boolean leftSide , boolean doSave ) { clientX -= getAbsoluteLeft ( ) + WINDOW_GUTTER ; Date rtn = null ; if ( leftSide ) { rtn = tlo . setStartDateToX ( getPositionXFromGUIX ( clientX ) ) ; } else { rtn = tlo . setEndDateToX ( getPositionXFromGUIX ( clientX ) ) ; } if ( doSave ) { timelineController . onTLOChange ( tlo ) ; } redraw ( rp ) ; return rtn ; } private void setSelected ( TimelineRemembersPosition rp , boolean selected ) { if ( selected ) { unselect ( ) ; selectedRP = rp ; selectedRP . addStyleDependentName ( "Selected" ) ; timelineController . setSelected ( rp . getTLO ( ) ) ; } else { unselect ( ) ; } } public void showOnly ( List < TimeLineObj < T > > timeObjects ) { clear ( ) ; add ( timeObjects ) ; } @ Override protected void unselect ( ) { editWidget . setVisible ( false ) ; if ( selectedRP != null ) { selectedRP . removeStyleDependentName ( "Selected" ) ; } selectedRP = null ; } private void updateLabels ( ) { int ii = getCenterX ( ) ; Date d2 = TimeLineObj . getDateFromViewPanelX ( ii ) ; whenlabel . setText ( ZoomLevel . Month . getDfFormat ( ) . format ( d2 ) ) ; DateTimeFormat format = currentZoom . getDfFormat ( ) ; for ( ProteanLabel label : labelList ) { label . setCenter ( d2 , currentZoom ) ; } } public void zoom ( int upDown ) { double oldScale = currentScale ; oldZoom = currentZoom ; Log . debug ( "previous zoom: " + currentZoom + " css: " + currentZoom . getCssClass ( ) ) ; ZoomLevel newZoom = null ; if ( upDown > 0 ) { newZoom = ZoomLevel . zoomOutOneFrom ( currentZoom ) ; } else { newZoom = ZoomLevel . zoomInOneFrom ( currentZoom ) ; } if ( newZoom != null ) { currentZoom = newZoom ; } currentScale = currentZoom . getScale ( ) ; Log . debug ( "new zoom: " + currentZoom + " css: " + currentZoom . getCssClass ( ) ) ; initYSlots ( ) ; finishZoom ( oldScale ) ; } @ Override public void zoomIn ( ) { zoom ( - 1 ) ; } @ Override public void zoomOut ( ) { zoom ( 1 ) ; } public Date getDateFromGUIX ( int x ) { return TimeLineObj . getDateFromViewPanelX ( getPositionXFromGUIX ( x ) ) ; } public void showStatus ( Widget widget ) { editWidget . clear ( ) ; editWidget . add ( widget ) ; editWidget . setVisible ( true ) ; } } 
=======
class ElementPattern extends Pattern { private Pattern p ; private final NameClass origNameClass ; private NameClass nameClass ; private boolean expanded = false ; private boolean checkedRestrictions = false ; private final Locator loc ; ElementPattern ( NameClass nameClass , Pattern p , Locator loc ) { super ( false , ELEMENT_CONTENT_TYPE , combineHashCode ( ELEMENT_HASH_CODE , nameClass . hashCode ( ) , p . hashCode ( ) ) ) ; this . nameClass = nameClass ; this . origNameClass = nameClass ; this . p = p ; this . loc = loc ; } void checkRestrictions ( int context , DuplicateAttributeDetector dad , Alphabet alpha ) throws RestrictionViolationException { if ( alpha != null ) alpha . addElement ( origNameClass ) ; if ( checkedRestrictions ) return ; switch ( context ) { case DATA_EXCEPT_CONTEXT : throw new RestrictionViolationException ( "data_except_contains_element" ) ; case LIST_CONTEXT : throw new RestrictionViolationException ( "list_contains_element" ) ; case ATTRIBUTE_CONTEXT : throw new RestrictionViolationException ( "attribute_contains_element" ) ; } checkedRestrictions = true ; try { p . checkRestrictions ( ELEMENT_CONTEXT , new DuplicateAttributeDetector ( ) , null ) ; } catch ( RestrictionViolationException e ) { checkedRestrictions = false ; e . maybeSetLocator ( loc ) ; throw e ; } } Pattern expand ( SchemaPatternBuilder b ) { if ( ! expanded ) { expanded = true ; p = p . expand ( b ) ; if ( p . isNotAllowed ( ) ) nameClass = new NullNameClass ( ) ; } return this ; } boolean samePattern ( Pattern other ) { if ( ! ( other instanceof ElementPattern ) ) return false ; ElementPattern ep = ( ElementPattern ) other ; return nameClass . equals ( ep . nameClass ) && p == ep . p ; } void checkRecursion ( int depth ) throws SAXException { p . checkRecursion ( depth + 1 ) ; } < T > T apply ( PatternFunction < T > f ) { return f . caseElement ( this ) ; } void setContent ( Pattern p ) { this . p = p ; } Pattern getContent ( ) { return p ; } NameClass getNameClass ( ) { return nameClass ; } Locator getLocator ( ) { return loc ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
