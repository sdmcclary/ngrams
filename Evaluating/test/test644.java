public class LanguageStructureProvider implements ITreeContentProvider , ITableLabelProvider { private String hashToken ( IToken token ) { return token . getLanguage ( ) + "::" + token . getCategory ( ) + "::" + token . getType ( ) ; } public class Category { private Map tokens ; private Map styles ; private String name ; private ColorizationStyle style ; public Category ( String name ) { this . name = name ; tokens = new HashMap ( ) ; styles = new HashMap ( ) ; this . style = null ; } public void setStyle ( ColorizationStyle style ) { this . style = style ; } public ColorizationStyle getStyle ( ) { return this . style ; } public void addTokenStyle ( IToken token , ColorizationStyle style ) { if ( token != null ) { styles . put ( hashToken ( token ) , style ) ; } } public void removeTokenStyle ( IToken token ) { if ( token != null ) { styles . remove ( hashToken ( token ) ) ; } } public ColorizationStyle getTokenStyle ( IToken token ) { ColorizationStyle style = null ; if ( token != null ) { style = ( ColorizationStyle ) styles . get ( hashToken ( token ) ) ; } return style ; } public void addToken ( IToken token ) { tokens . put ( hashToken ( token ) , token ) ; } public IToken getToken ( String hash ) { if ( tokens . containsKey ( hash ) ) { return ( IToken ) tokens . get ( hash ) ; } return null ; } public Collection getTokens ( ) { return tokens . values ( ) ; } public String getName ( ) { return name ; } public String getDisplayName ( ) { String display = name . toLowerCase ( ) ; display = display . substring ( 0 , 1 ) . toUpperCase ( ) + display . substring ( 1 , display . length ( ) ) ; return display ; } public Map getStyles ( ) { return styles ; } } private Map categories ; private Map regions ; private Map imageMap ; private Color backgroundColor ; private Color lineHighlightColor ; private Color caretColor ; private Color selectionForegroundColor ; private Color selectionBackgroundColor ; private Color foldingBackgroundColor ; private Color foldingForegroundColor ; private String language ; public LanguageStructureProvider ( String language ) { this . language = language ; categories = new HashMap ( ) ; imageMap = new HashMap ( ) ; regions = new HashMap ( ) ; backgroundColor = null ; lineHighlightColor = null ; caretColor = null ; selectionForegroundColor = null ; selectionBackgroundColor = null ; loadProvider ( ) ; } public void loadProvider ( ) { TokenList tl = LanguageRegistry . getTokenList ( language ) ; if ( tl != null ) { for ( int i = 0 ; i < tl . size ( ) ; i ++ ) { IToken curr = ( IToken ) tl . get ( i ) ; this . addToken ( curr ) ; } } LanguageColorizer lc = LanguageRegistry . getLanguageColorizer ( language ) ; if ( lc != null ) { Iterator colorizers = lc . getTokenColorizers ( ) . iterator ( ) ; while ( colorizers . hasNext ( ) ) { TokenColorizer curr = ( TokenColorizer ) colorizers . next ( ) ; ColorizationStyle currStyle = curr . getBaseColorization ( ) ; ColorizationStyle clone = new ColorizationStyle ( currStyle ) ; this . addStyle ( curr . getToken ( ) , clone ) ; Iterator regions = curr . getRegions ( ) . iterator ( ) ; Map regionMap = new HashMap ( ) ; while ( regions . hasNext ( ) ) { Region region = ( Region ) regions . next ( ) ; Region cloneRegion = new Region ( region ) ; ColorizationStyle regionStyle = region . getStyle ( ) ; ColorizationStyle regionCloneStyle = new ColorizationStyle ( regionStyle ) ; cloneRegion . setStyle ( regionCloneStyle ) ; cloneRegion . setName ( region . getName ( ) ) ; regionMap . put ( cloneRegion . getName ( ) , cloneRegion ) ; this . addRegion ( curr . getToken ( ) , cloneRegion ) ; } } colorizers = lc . getCategoryColorizers ( ) . iterator ( ) ; while ( colorizers . hasNext ( ) ) { CategoryColorizer curr = ( CategoryColorizer ) colorizers . next ( ) ; ColorizationStyle clone = new ColorizationStyle ( curr . getStyle ( ) ) ; Category category = this . getCategory ( curr . getName ( ) ) ; category . setStyle ( clone ) ; } this . setBackgroundColor ( lc . getBackground ( ) ) ; this . setCaretColor ( lc . getCaretColor ( ) ) ; this . setLineHighlightColor ( lc . getLineHighlightColor ( ) ) ; this . setSelectionForegroundColor ( lc . getSelectionForeground ( ) ) ; this . setSelectionBackgroundColor ( lc . getSelectionBackground ( ) ) ; this . setFoldingBackgroundColor ( lc . getFoldingBg ( ) ) ; this . setFoldingForegroundColor ( lc . getFoldingFg ( ) ) ; } } public void clearStyles ( ) { Iterator cats = categories . values ( ) . iterator ( ) ; while ( cats . hasNext ( ) ) { Category cat = ( Category ) cats . next ( ) ; cat . setStyle ( null ) ; cat . getStyles ( ) . clear ( ) ; } Iterator regs = regions . values ( ) . iterator ( ) ; while ( regs . hasNext ( ) ) { Map map = ( Map ) regs . next ( ) ; map . clear ( ) ; } } public void addStyle ( IToken token , ColorizationStyle style ) { Category cat = getCategory ( token . getCategory ( ) ) ; cat . addTokenStyle ( token , style ) ; } public void removeStyle ( IToken token ) { Category cat = getCategory ( token . getCategory ( ) ) ; cat . removeTokenStyle ( token ) ; Map regionMap = ( Map ) regions . get ( token ) ; regionMap . clear ( ) ; } public void removeRegion ( IToken token , String name ) { Map regionMap = ( Map ) regions . get ( token ) ; regionMap . remove ( name ) ; } public void addRegion ( IToken token , Region region ) { Map regionMap = ( Map ) regions . get ( token ) ; regionMap . put ( region . getName ( ) , region ) ; } public Map getRegions ( IToken token ) { return ( ( Map ) regions . get ( token ) ) ; } public Map getTokenStyles ( ) { Map all = new HashMap ( ) ; Iterator cats = categories . values ( ) . iterator ( ) ; while ( cats . hasNext ( ) ) { Category cat = ( Category ) cats . next ( ) ; all . putAll ( cat . getStyles ( ) ) ; } return all ; } public Collection getTokens ( ) { Collection all = new ArrayList ( ) ; Iterator cats = categories . values ( ) . iterator ( ) ; while ( cats . hasNext ( ) ) { Category cat = ( Category ) cats . next ( ) ; all . addAll ( cat . getTokens ( ) ) ; } return all ; } public Map getCategoryStyles ( ) { Map all = new HashMap ( ) ; Iterator cats = categories . values ( ) . iterator ( ) ; while ( cats . hasNext ( ) ) { Category cat = ( Category ) cats . next ( ) ; all . put ( cat , cat . getStyle ( ) ) ; } return all ; } public ColorizationStyle getStyle ( IToken token ) { Category cat = getCategory ( token . getCategory ( ) ) ; return cat . getTokenStyle ( token ) ; } public Category getCategory ( String category ) { Category cat = null ; if ( categories . containsKey ( category ) ) { cat = ( Category ) categories . get ( category ) ; } return cat ; } public void addToken ( IToken token ) { Category category = null ; if ( ! categories . containsKey ( token . getCategory ( ) ) ) { category = new Category ( token . getCategory ( ) ) ; ColorizationStyle style = new ColorizationStyle ( ) ; style . setForegroundColor ( UnifiedColorManager . getInstance ( ) . getColor ( new RGB ( 0 , 0 , 0 ) ) ) ; style . setName ( category . getName ( ) ) ; category . setStyle ( style ) ; categories . put ( token . getCategory ( ) , category ) ; } else { category = ( Category ) categories . get ( token . getCategory ( ) ) ; } if ( category . getToken ( hashToken ( token ) ) == null ) { category . addToken ( token ) ; } regions . put ( token , new HashMap ( ) ) ; } public Object [ ] getChildren ( Object parentElement ) { if ( parentElement instanceof Category ) { List list = new ArrayList ( ( ( Category ) parentElement ) . getTokens ( ) ) ; Collections . sort ( list , new Comparator ( ) { public int compare ( Object o1 , Object o2 ) { String s1 = ( ( IToken ) o1 ) . getDisplayName ( ) ; String s2 = ( ( IToken ) o2 ) . getDisplayName ( ) ; return s1 . compareTo ( s2 ) ; } } ) ; return list . toArray ( ) ; } else { return new Object [ 0 ] ; } } public Object getParent ( Object element ) { if ( element instanceof IToken ) { IToken token = ( IToken ) element ; return getCategory ( token . getCategory ( ) ) ; } return null ; } public boolean hasChildren ( Object element ) { return element instanceof Category ; } public Object [ ] getElements ( Object inputElement ) { List list = new ArrayList ( categories . values ( ) ) ; Collections . sort ( list , new Comparator ( ) { public int compare ( Object o1 , Object o2 ) { String s1 = ( ( Category ) o1 ) . getDisplayName ( ) ; String s2 = ( ( Category ) o2 ) . getDisplayName ( ) ; return s1 . compareTo ( s2 ) ; } } ) ; return list . toArray ( ) ; } public void dispose ( ) { } public void disposeImages ( ) { Iterator iter = imageMap . values ( ) . iterator ( ) ; while ( iter . hasNext ( ) ) { ( ( Image ) iter . next ( ) ) . dispose ( ) ; } } public void inputChanged ( Viewer viewer , Object oldInput , Object newInput ) { } public void removeListener ( ILabelProviderListener listener ) { } public Image getColumnImage ( Object element , int columnIndex ) { ColorizationStyle style = null ; if ( element instanceof IToken ) { style = getStyle ( ( IToken ) element ) ; } else if ( element instanceof Category ) { style = ( ( Category ) element ) . getStyle ( ) ; } if ( style != null ) { if ( columnIndex == 1 && element instanceof IToken ) { return UnifiedEditorsPlugin . getImage ( "icons/checked.gif" ) ; } else if ( columnIndex == 3 && element instanceof IToken ) { if ( ! getRegions ( ( IToken ) element ) . isEmpty ( ) ) { return UnifiedEditorsPlugin . getImage ( "icons/region.gif" ) ; } } else if ( columnIndex == 4 ) { if ( style != null ) { Color fg = style . getForegroundColor ( ) ; Image img = null ; if ( ! imageMap . containsKey ( fg . getRGB ( ) ) ) { img = new Image ( Display . getCurrent ( ) , 16 , 16 ) ; GC gc = new GC ( img ) ; gc . setBackground ( fg ) ; gc . fillRectangle ( 1 , 1 , 13 , 13 ) ; gc . setForeground ( UnifiedColorManager . getInstance ( ) . getColor ( new RGB ( 0 , 0 , 0 ) ) ) ; gc . drawRectangle ( 1 , 1 , 13 , 13 ) ; gc . dispose ( ) ; imageMap . put ( fg . getRGB ( ) , img ) ; } else { img = ( Image ) imageMap . get ( fg . getRGB ( ) ) ; } return img ; } } else if ( columnIndex == 5 ) { if ( style != null && style . isBold ( ) ) { return UnifiedEditorsPlugin . getImage ( "icons/bold_on.gif" ) ; } else { return UnifiedEditorsPlugin . getImage ( "icons/bold_off.gif" ) ; } } else if ( columnIndex == 6 ) { if ( style != null && style . isItalic ( ) ) { return UnifiedEditorsPlugin . getImage ( "icons/italic_on.gif" ) ; } else { return UnifiedEditorsPlugin . getImage ( "icons/italic_off.gif" ) ; } } else if ( columnIndex == 7 ) { if ( style != null && style . isUnderline ( ) ) { return UnifiedEditorsPlugin . getImage ( "icons/underline_on.gif" ) ; } else { return UnifiedEditorsPlugin . getImage ( "icons/underline_off.gif" ) ; } } } else { if ( columnIndex == 1 && element instanceof IToken ) { return UnifiedEditorsPlugin . getImage ( "icons/unchecked.gif" ) ; } } return null ; } public String getColumnText ( Object element , int columnIndex ) { if ( columnIndex == 0 ) { if ( element instanceof Category ) { return ( ( Category ) element ) . getDisplayName ( ) ; } } else if ( columnIndex == 2 ) { if ( element instanceof IToken ) { return ( ( IToken ) element ) . getDisplayName ( ) ; } } return "" ; } public void addListener ( ILabelProviderListener listener ) { } public boolean isLabelProperty ( Object element , String property ) { return false ; } public void removeAll ( ) { categories . clear ( ) ; imageMap . clear ( ) ; regions . clear ( ) ; backgroundColor = null ; lineHighlightColor = null ; caretColor = null ; selectionForegroundColor = null ; selectionBackgroundColor = null ; } public void buildLanguageColorizer ( LanguageColorizer lc , String prefId ) { Map tokenStyles = this . getTokenStyles ( ) ; Iterator tokens = this . getTokens ( ) . iterator ( ) ; while ( tokens . hasNext ( ) ) { IToken curr = ( IToken ) tokens . next ( ) ; ColorizationStyle cloneStyle = ( ColorizationStyle ) tokenStyles . get ( hashToken ( curr ) ) ; if ( cloneStyle != null ) { ColorizationStyle newStyle = new ColorizationStyle ( cloneStyle ) ; TokenColorizer colorizer = lc . getTokenColorizer ( curr ) ; if ( colorizer == null ) { colorizer = new TokenColorizer ( ) ; colorizer . setToken ( curr ) ; lc . addTokenColorizer ( colorizer ) ; } colorizer . setBaseColorization ( newStyle ) ; Map regionMap = ( Map ) this . getRegions ( curr ) ; Iterator regions = regionMap . values ( ) . iterator ( ) ; while ( regions . hasNext ( ) ) { Region region = ( Region ) regions . next ( ) ; Region cloneRegion = new Region ( region ) ; cloneRegion . setName ( region . getName ( ) ) ; ColorizationStyle newRegionStyle = new ColorizationStyle ( region . getStyle ( ) ) ; newRegionStyle . setName ( curr . getCategory ( ) + "_" + curr . getType ( ) + "_" + region . getName ( ) ) ; cloneRegion . setStyle ( newRegionStyle ) ; colorizer . addColorization ( cloneRegion ) ; } Iterator existingRegions = colorizer . getRegions ( ) . iterator ( ) ; while ( existingRegions . hasNext ( ) ) { Region currRegion = ( Region ) existingRegions . next ( ) ; if ( ! regionMap . containsKey ( currRegion . getName ( ) ) ) { existingRegions . remove ( ) ; } } } } Iterator colorizers = lc . getTokenColorizers ( ) . iterator ( ) ; while ( colorizers . hasNext ( ) ) { TokenColorizer curr = ( TokenColorizer ) colorizers . next ( ) ; if ( ! tokenStyles . containsKey ( hashToken ( curr . getToken ( ) ) ) ) { colorizers . remove ( ) ; } } Map categoryStyles = this . getCategoryStyles ( ) ; Iterator styles = categoryStyles . keySet ( ) . iterator ( ) ; while ( styles . hasNext ( ) ) { Category category = ( Category ) styles . next ( ) ; ColorizationStyle curr = ( ColorizationStyle ) categoryStyles . get ( category ) ; ColorizationStyle newStyle = new ColorizationStyle ( curr ) ; CategoryColorizer colorizer = lc . getCategoryColorizer ( category . getName ( ) ) ; if ( colorizer == null ) { colorizer = new CategoryColorizer ( ) ; colorizer . setName ( category . getName ( ) ) ; lc . addCategoryColorizer ( colorizer ) ; } colorizer . setStyle ( newStyle ) ; } colorizers = lc . getCategoryColorizers ( ) . iterator ( ) ; while ( colorizers . hasNext ( ) ) { CategoryColorizer curr = ( CategoryColorizer ) colorizers . next ( ) ; Category cat = this . getCategory ( curr . getName ( ) ) ; if ( ! categoryStyles . containsKey ( cat ) ) { colorizers . remove ( ) ; } } lc . setBackground ( this . getBackgroundColor ( ) ) ; lc . setCaretColor ( this . getCaretColor ( ) ) ; lc . setLineHighlightColor ( this . getLineHighlightColor ( ) ) ; lc . setSelectionBackground ( this . getSelectionBackgroundColor ( ) ) ; lc . setSelectionForeground ( this . getSelectionForegroundColor ( ) ) ; lc . setFoldingBg ( this . getFoldingBackgroundColor ( ) ) ; lc . setFoldingFg ( this . getFoldingForegroundColor ( ) ) ; try { ( new ColorizerWriter ( ) ) . buildColorizationPreference ( lc , lc . getLanguage ( ) , prefId ) ; UnifiedEditorsPlugin . getDefault ( ) . getPreferenceStore ( ) . firePropertyChangeEvent ( "Colorization saved" , "Colorization saved" , "Colorization saved" ) ; } catch ( LexerException e ) { IdeLog . logError ( UnifiedEditorsPlugin . getDefault ( ) , e . getMessage ( ) ) ; } } public Color getBackgroundColor ( ) { return backgroundColor ; } public void setBackgroundColor ( Color backgroundColor ) { this . backgroundColor = backgroundColor ; } public Color getCaretColor ( ) { return caretColor ; } public void setCaretColor ( Color caretColor ) { this . caretColor = caretColor ; } public Color getLineHighlightColor ( ) { return lineHighlightColor ; } public void setLineHighlightColor ( Color lineHighlightColor ) { this . lineHighlightColor = lineHighlightColor ; } public Color getSelectionBackgroundColor ( ) { return selectionBackgroundColor ; } public void setSelectionBackgroundColor ( Color selectionBackgroundColor ) { this . selectionBackgroundColor = selectionBackgroundColor ; } public Color getSelectionForegroundColor ( ) { return selectionForegroundColor ; } public void setSelectionForegroundColor ( Color selectionForegroundColor ) { this . selectionForegroundColor = selectionForegroundColor ; } public String getLanguage ( ) { return language ; } public void setLanguage ( String language ) { this . language = language ; } public void buildColorizationFile ( File file ) throws LexerException { if ( getLanguage ( ) == null ) { return ; } LanguageColorizer colorizer = LanguageRegistry . getLanguageColorizer ( getLanguage ( ) ) ; ( new ColorizerWriter ( ) ) . buildColorizationFile ( colorizer , getLanguage ( ) , file ) ; } public void importColorization ( File file ) { AttributeSniffer sniffer = new AttributeSniffer ( "colorizer" , "language" ) ; try { sniffer . read ( new FileInputStream ( file ) ) ; if ( getLanguage ( ) != null && getLanguage ( ) . equals ( sniffer . getMatchedValue ( ) ) ) { LanguageRegistry . importColorization ( file , getLanguage ( ) ) ; this . clearStyles ( ) ; this . loadProvider ( ) ; } else { } } catch ( Exception e ) { } } public void resetToLanguageDefaults ( ) { if ( getLanguage ( ) != null ) { this . clearStyles ( ) ; LanguageRegistry . restoreDefaultColorization ( getLanguage ( ) ) ; this . loadProvider ( ) ; } } public void refreshTokens ( ) { TokenList tl = LanguageRegistry . getTokenList ( language ) ; if ( tl != null ) { for ( int i = 0 ; i < tl . size ( ) ; i ++ ) { IToken curr = ( IToken ) tl . get ( i ) ; Category category = this . getCategory ( curr . getCategory ( ) ) ; if ( category == null || category . getToken ( curr . getType ( ) ) == null ) { this . addToken ( curr ) ; } } } } public Color getFoldingBackgroundColor ( ) { return foldingBackgroundColor ; } public void setFoldingBackgroundColor ( Color foldingBackgroundColor ) { this . foldingBackgroundColor = foldingBackgroundColor ; } public Color getFoldingForegroundColor ( ) { return foldingForegroundColor ; } public void setFoldingForegroundColor ( Color foldingForegroundColor ) { this . foldingForegroundColor = foldingForegroundColor ; } } 