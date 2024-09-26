public final class UifUtilities { private static final Logger LOG = Logger . getLogger ( UifUtilities . class . getName ( ) ) ; private static final ThreadLocal < Boolean > EDT_FLAG = new ThreadLocal < Boolean > ( ) ; private static final JLabel FONT_LABEL = new JLabel ( ) ; private UifUtilities ( ) { } public static Frame findOwnerFrame ( Object obj ) { return ( obj instanceof Component ) ? findComponentOwnerFrame ( ( Component ) obj ) : findComponentOwnerFrame ( null ) ; } public static Frame findComponentOwnerFrame ( Component component ) { Frame owner ; if ( component == null ) { owner = Application . getDefaultParentFrame ( ) ; } else { if ( component instanceof Frame ) { owner = ( Frame ) component ; } else { owner = findComponentOwnerFrame ( component . getParent ( ) ) ; } } return owner ; } public static boolean isEDT ( ) { Boolean bool ; synchronized ( EDT_FLAG ) { bool = EDT_FLAG . get ( ) ; if ( bool == null ) { bool = SwingUtilities . isEventDispatchThread ( ) ; EDT_FLAG . set ( bool ) ; } } return bool ; } public static TableColumn setTableColWidth ( JTable table , int column , int width ) { TableColumn col = table . getColumnModel ( ) . getColumn ( column ) ; col . setMinWidth ( width ) ; col . setMaxWidth ( width ) ; return col ; } public static Font smallerFont ( Component component ) { Font font = component . getFont ( ) ; component . setFont ( applyFontBias ( font , - 1 ) ) ; return font ; } public static Font applyFontBias ( Font font , int bias ) { if ( font == null ) throw new NullPointerException ( Strings . error ( "unspecified.font" ) ) ; if ( bias != 0 ) { float oldSize = font . getSize2D ( ) ; font = font . deriveFont ( oldSize + bias ) ; } return font ; } public static void delegateEventToParent ( Component component , MouseEvent e ) { delegateEventToParent ( component , e , false ) ; } public static void delegateEventToParent ( Component component , MouseEvent e , boolean direct ) { Component parent = component . getParent ( ) ; delegateEventToParent ( component , parent , e , direct ) ; } public static void delegateEventToParent ( Component component , Component parent , MouseEvent e , boolean direct ) { if ( parent != null ) { if ( direct ) { e = new MouseEvent ( component , e . getID ( ) , e . getWhen ( ) , e . getModifiers ( ) , e . getX ( ) , e . getY ( ) , e . getClickCount ( ) , e . isPopupTrigger ( ) , e . getButton ( ) ) ; } else { Point point = e . getPoint ( ) ; SwingUtilities . convertPointToScreen ( point , component ) ; SwingUtilities . convertPointFromScreen ( point , parent ) ; e = new MouseEvent ( parent , e . getID ( ) , e . getWhen ( ) , e . getModifiers ( ) , ( int ) point . getX ( ) , ( int ) point . getY ( ) , e . getClickCount ( ) , e . isPopupTrigger ( ) , e . getButton ( ) ) ; } parent . dispatchEvent ( e ) ; } } public static void delegateEventToParent ( Component component , AWTEvent e ) { Component parent = component . getParent ( ) ; if ( parent != null ) parent . dispatchEvent ( e ) ; } public static void setPreferredWidth ( JComponent comp , int width ) { Dimension size = comp . getPreferredSize ( ) ; size . width = width ; comp . setPreferredSize ( size ) ; } public static int estimateWidth ( Font font , String msg ) { return FONT_LABEL . getFontMetrics ( font ) . stringWidth ( msg ) ; } public static void drawAAString ( Graphics g , String text , int x , int y ) { Graphics2D g2 = ( System . getProperty ( "swing.aatext" ) != null && g instanceof Graphics2D ) ? ( Graphics2D ) g : null ; Object oldAAValue = null ; if ( g2 != null ) { oldAAValue = g2 . getRenderingHint ( RenderingHints . KEY_TEXT_ANTIALIASING ) ; g2 . setRenderingHint ( RenderingHints . KEY_TEXT_ANTIALIASING , RenderingHints . VALUE_TEXT_ANTIALIAS_ON ) ; } g . drawString ( text , x , y ) ; if ( g2 != null ) { g2 . setRenderingHint ( RenderingHints . KEY_TEXT_ANTIALIASING , oldAAValue ) ; } } public static String fontToString ( Font fnt ) { String str = null ; if ( fnt != null ) { String strStyle ; if ( fnt . isBold ( ) ) { strStyle = fnt . isItalic ( ) ? "bolditalic" : "bold" ; } else { strStyle = fnt . isItalic ( ) ? "italic" : "plain" ; } str = fnt . getFamily ( ) + "-" + strStyle + "-" + fnt . getSize ( ) ; } return str ; } public static String colorToHex ( Color color ) { if ( color == null ) return "" ; int red = color . getRed ( ) ; int green = color . getGreen ( ) ; int blue = color . getBlue ( ) ; StringBuffer str = new StringBuffer ( "#" ) ; if ( red < 16 ) str . append ( "0" ) ; str . append ( Integer . toHexString ( red ) ) ; if ( green < 16 ) str . append ( "0" ) ; str . append ( Integer . toHexString ( green ) ) ; if ( blue < 16 ) str . append ( "0" ) ; str . append ( Integer . toHexString ( blue ) ) ; return str . toString ( ) ; } public static void setTextColor ( HTMLDocument doc , String styleName , Color color ) { StyleConstants . setForeground ( doc . getStyle ( styleName ) , color ) ; } public static void setFontAttributes ( HTMLDocument doc , String styleName , Font font ) { setFontAttributes ( doc . getStyle ( styleName ) , font ) ; String css = "ul, ol { font: normal " + font . getSize ( ) + "pt " + font . getFamily ( ) + " }" ; loadStyles ( doc , css ) ; } private static void loadStyles ( HTMLDocument doc , String css ) { try { doc . getStyleSheet ( ) . loadRules ( new StringReader ( css ) , null ) ; } catch ( IOException e ) { LOG . log ( Level . SEVERE , "Couldn't load new stylesheet" , e ) ; } } public static void setFontAttributes ( Style style , Font font ) { StyleConstants . setFontFamily ( style , font . getFontName ( ) ) ; style . addAttribute ( StyleConstants . CharacterConstants . Size , Integer . toString ( font . getSize ( ) ) ) ; } public static void installTextStyle ( JEditorPane control , String styleName ) { HTMLDocument doc = ( HTMLDocument ) control . getDocument ( ) ; doc . setCharacterAttributes ( 0 , control . getDocument ( ) . getLength ( ) , doc . getStyle ( styleName ) , false ) ; } public static boolean invokeAndWait ( Runnable task ) { return invokeAndWait ( task , null , null ) ; } public static boolean invokeAndWait ( Runnable task , String failMessage , Level failureLevel ) { boolean errorless = false ; if ( UifUtilities . isEDT ( ) ) { task . run ( ) ; } else { try { SwingUtilities . invokeAndWait ( task ) ; errorless = true ; } catch ( Throwable e ) { if ( failMessage != null && failureLevel != null ) LOG . log ( failureLevel , failMessage , e ) ; } } return errorless ; } public static void setDependency ( final JCheckBox master , final JCheckBox slave ) { if ( master == null || slave == null ) return ; slave . setEnabled ( master . isSelected ( ) ) ; master . addActionListener ( new ActionListener ( ) { public void actionPerformed ( ActionEvent e ) { slave . setEnabled ( master . isSelected ( ) ) ; } } ) ; } public static void setEditorFont ( JEditorPane editor , Font font ) { Document document = editor . getDocument ( ) ; if ( document instanceof HTMLDocument ) { HTMLDocument htmlDocument = ( HTMLDocument ) document ; String css = "body { font: normal " + font . getSize ( ) + "pt " + font . getFamily ( ) + " }" ; loadStyles ( htmlDocument , css ) ; } } public static JLabel makeBasicPlanIcon ( boolean visible ) { return makePlanIcon ( visible , "plan.basic.icon" , "ptb.prefs.basic.tooltip" ) ; } public static JLabel makePublisherPlanIcon ( boolean visible ) { return makePlanIcon ( visible , "plan.publisher.icon" , "ptb.prefs.pub.tooltip" ) ; } private static JLabel makePlanIcon ( boolean visible , String rsIcon , String rsTooltip ) { JLabel icn ; if ( visible ) { icn = new JLabel ( ResourceUtils . getIcon ( rsIcon ) ) ; icn . setToolTipText ( Strings . message ( rsTooltip ) ) ; } else icn = new JLabel ( "" ) ; return icn ; } public static void removeTypeSelectionListener ( JList list ) { KeyListener [ ] kls = list . getListeners ( KeyListener . class ) ; for ( KeyListener kl : kls ) { String name = kl . getClass ( ) . getName ( ) ; if ( name . endsWith ( "BasicListUI$Handler" ) ) list . removeKeyListener ( kl ) ; } } public static JComponent boldFont ( JComponent component ) { if ( component == null ) return null ; component . setFont ( component . getFont ( ) . deriveFont ( Font . BOLD ) ) ; return component ; } } 