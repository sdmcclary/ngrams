public class ItemViewer extends StructuredViewer { static String DARK_COLOR = "com.netifera.platform.ui.DARK_COLOR_LIST" ; static { int shift = "carbon" . equals ( SWT . getPlatform ( ) ) ? - 25 : - 10 ; final Color lightColor = PlatformUI . getWorkbench ( ) . getDisplay ( ) . getSystemColor ( SWT . COLOR_LIST_BACKGROUND ) ; RGB darkRGB = new RGB ( Math . max ( 0 , lightColor . getRed ( ) + shift ) , Math . max ( 0 , lightColor . getGreen ( ) + shift ) , Math . max ( 0 , lightColor . getBlue ( ) + shift ) ) ; JFaceResources . getColorRegistry ( ) . put ( DARK_COLOR , darkRGB ) ; } private Map < Object , Widget > itemMap = new ConcurrentHashMap < Object , Widget > ( ) ; private IItemProvider itemProvider ; private List < Object > selection = new ArrayList < Object > ( ) ; private Composite control ; private ScrolledComposite scrolled ; private volatile boolean busy ; public ItemViewer ( Composite parent , int style ) { int height = JFaceResources . getDefaultFont ( ) . getFontData ( ) [ 0 ] . getHeight ( ) ; scrolled = new ScrolledComposite ( parent , SWT . H_SCROLL | SWT . V_SCROLL | SWT . BORDER ) ; scrolled . setLayout ( new FillLayout ( ) ) ; control = new Composite ( scrolled , SWT . NONE ) ; control . setBackground ( parent . getDisplay ( ) . getSystemColor ( SWT . COLOR_LIST_BACKGROUND ) ) ; scrolled . setContent ( control ) ; ColumnLayout columnLayout = new ColumnLayout ( ) ; columnLayout . maxNumColumns = 1 ; columnLayout . minNumColumns = 1 ; columnLayout . horizontalSpacing = 1 ; columnLayout . verticalSpacing = 0 ; columnLayout . leftMargin = 0 ; columnLayout . rightMargin = 0 ; columnLayout . topMargin = 0 ; columnLayout . bottomMargin = 0 ; control . setLayout ( columnLayout ) ; scrolled . addControlListener ( new ControlAdapter ( ) { public void controlResized ( ControlEvent e ) { Rectangle r = scrolled . getClientArea ( ) ; scrolled . setMinSize ( control . computeSize ( r . width , SWT . DEFAULT ) ) ; } } ) ; scrolled . getVerticalBar ( ) . setIncrement ( height * 2 ) ; scrolled . setExpandHorizontal ( true ) ; scrolled . setExpandVertical ( true ) ; } public void dispose ( ) { if ( itemProvider != null ) { itemProvider . dispose ( ) ; itemProvider = null ; } } private Widget createItemWidget ( Object element ) { if ( itemProvider == null ) { return null ; } Widget item = itemProvider . getItem ( element ) ; if ( item == null ) { return null ; } final Object finalElement = element ; final Control itemControl = ( ( Control ) item ) ; ( ( Control ) item ) . addFocusListener ( new FocusListener ( ) { public void focusGained ( FocusEvent e ) { if ( e . widget . getData ( ) != null ) { setSelection ( new StructuredSelection ( e . widget . getData ( ) ) , true ) ; } else { setSelection ( new StructuredSelection ( finalElement ) , true ) ; } } public void focusLost ( FocusEvent e ) { } } ) ; MouseListener mouseListener = new MouseListener ( ) { public void mouseDown ( MouseEvent e ) { itemControl . forceFocus ( ) ; } public void mouseDoubleClick ( MouseEvent e ) { } public void mouseUp ( MouseEvent e ) { } } ; itemControl . addMouseListener ( mouseListener ) ; itemMap . put ( element , item ) ; return item ; } protected synchronized void internalRefresh ( Object element ) { Assert . isNotNull ( itemProvider ) ; if ( control . isDisposed ( ) ) { return ; } if ( element == null || element . equals ( getRoot ( ) ) ) { internalRefreshAll ( ) ; return ; } if ( itemMap . containsKey ( element ) ) { Widget item = itemMap . get ( element ) ; if ( ! item . isDisposed ( ) ) { doUpdateItem ( item , element , true ) ; } else { itemMap . remove ( element ) ; } } else { if ( this . getComparator ( ) != null ) { internalRefreshAll ( ) ; } else { Widget item = createItemWidget ( element ) ; ( ( Control ) item ) . setBackground ( Display . getCurrent ( ) . getSystemColor ( SWT . COLOR_YELLOW ) ) ; } setSelection ( new StructuredSelection ( element ) , true ) ; } } private void internalRefreshAll ( ) { if ( isBusy ( ) || control . isDisposed ( ) || getInput ( ) == null || getContentProvider ( ) == null ) { return ; } setBusy ( true ) ; control . setRedraw ( false ) ; control . getParent ( ) . setRedraw ( false ) ; for ( Object element : itemMap . keySet ( ) ) { Widget item = itemMap . get ( element ) ; item . dispose ( ) ; itemMap . remove ( element ) ; } Object [ ] elements = getSortedChildren ( getInput ( ) ) ; assertElementsNotNull ( elements ) ; int i = elements . length % 2 ; for ( Object element : elements ) { Widget item = createItemWidget ( element ) ; ( ( Control ) item ) . setBackground ( getItemBackgroundColor ( i ) ) ; i ++ ; } control . layout ( true ) ; scrolled . setMinSize ( control . computeSize ( scrolled . getClientArea ( ) . width , SWT . DEFAULT ) ) ; control . setRedraw ( true ) ; control . getParent ( ) . setRedraw ( true ) ; setBusy ( false ) ; } private Color getItemBackgroundColor ( int i ) { if ( i % 2 == 0 ) { return Display . getCurrent ( ) . getSystemColor ( SWT . COLOR_LIST_BACKGROUND ) ; } else { return JFaceResources . getColorRegistry ( ) . get ( DARK_COLOR ) ; } } protected boolean isBusy ( ) { return busy ; } protected void setBusy ( boolean busy ) { this . busy = busy ; } public IItemProvider getItemProvider ( ) { return itemProvider ; } public void setLabelProvider ( IBaseLabelProvider labelProvider ) { Assert . isTrue ( labelProvider instanceof ILabelProvider ) ; super . setLabelProvider ( labelProvider ) ; if ( itemProvider != null ) { itemProvider . setLabelProvider ( ( ILabelProvider ) labelProvider ) ; } } public void setItemProvider ( IItemProvider itemProvider ) { itemProvider . setParent ( control ) ; this . itemProvider = itemProvider ; if ( getLabelProvider ( ) != null ) { itemProvider . setLabelProvider ( ( ILabelProvider ) getLabelProvider ( ) ) ; } } protected void inputChanged ( Object input , Object oldInput ) { if ( input == oldInput ) { return ; } selection . clear ( ) ; setSelection ( StructuredSelection . EMPTY ) ; for ( Object element : itemMap . keySet ( ) ) { itemMap . get ( element ) . dispose ( ) ; itemMap . remove ( element ) ; } internalRefreshAll ( ) ; } public Control getControl ( ) { return control ; } public void update ( Object element , String [ ] properties ) { refresh ( element ) ; } private void setSelectionInternal ( Object element , boolean reveal ) { if ( element != null && itemMap . containsKey ( element ) ) { Widget item = itemMap . get ( element ) ; if ( reveal && item instanceof Control && ! item . isDisposed ( ) ) { ( ( Control ) item ) . setFocus ( ) ; } selection . clear ( ) ; selection . add ( element ) ; } } public void reveal ( Object element ) { setSelectionInternal ( element , true ) ; } @ SuppressWarnings ( "unchecked" ) protected List getSelectionFromWidget ( ) { return selection ; } @ SuppressWarnings ( "unchecked" ) protected void setSelectionToWidget ( List list , boolean reveal ) { if ( list . isEmpty ( ) ) { return ; } setSelectionInternal ( list . get ( 0 ) , reveal ) ; } protected Widget doFindInputItem ( Object element ) { return null ; } protected Widget doFindItem ( Object element ) { return itemMap . containsKey ( element ) ? itemMap . get ( element ) : null ; } protected void doUpdateItem ( Widget item , Object element , boolean fullMap ) { itemProvider . updateItem ( item , element ) ; control . layout ( true , true ) ; } } 