<<<<<<< HEAD
class AsyncImagePanel extends JComponent { private static final int INDICATION_BORDER = 0 ; private static final int INDICATION_FADE = 1 ; private static final int INDICATION_FRAME = 2 ; private static final int INDICATION_TYPE = INDICATION_FRAME ; private static final int STATUS_INCEPTION = - 1 ; private static final int STATUS_LOADING = 0 ; private static final int STATUS_LOADED = 1 ; private static final int STATUS_FAILED = 2 ; private static final AlphaComposite COMPOSITE_MODE_1 = AlphaComposite . getInstance ( AlphaComposite . SRC_OVER , 1f ) ; private static final AlphaComposite COMPOSITE_MODE_2 = AlphaComposite . getInstance ( AlphaComposite . SRC_OVER , 0.5f ) ; private final URL imageURL ; private final ImageHandler observer ; private int maxWidth ; private int maxHeight ; private Image image ; private Image rescaledImage ; private Image badge ; private int status ; private boolean secondMode ; private boolean overrideFirstMode ; public AsyncImagePanel ( URL aImageURL , int aWidth , int aHeight , Border aBorder , boolean aSecondMode ) { if ( aImageURL == null ) throw new NullPointerException ( Strings . error ( "unspecified.url" ) ) ; imageURL = aImageURL ; image = null ; rescaledImage = null ; observer = new ImageHandler ( ) ; secondMode = aSecondMode ; overrideFirstMode = false ; status = STATUS_INCEPTION ; setBorder ( aBorder ) ; setImageSize ( new Dimension ( aWidth , aHeight ) ) ; } public void setSecondMode ( boolean aSecondMode ) { if ( secondMode != aSecondMode ) { secondMode = aSecondMode ; repaint ( ) ; } } public void setBadge ( Image badge ) { this . badge = badge ; repaint ( ) ; } public void setImageSize ( Dimension dim ) { maxWidth = dim . width ; maxHeight = dim . height ; int width = maxWidth ; int height = maxHeight ; Border border = getBorder ( ) ; if ( border != null ) { Insets borderInsets = border . getBorderInsets ( this ) ; width += borderInsets . left + borderInsets . right ; height += borderInsets . top + borderInsets . bottom ; } setPreferredSize ( new Dimension ( width , height ) ) ; if ( STATUS_LOADED == status ) { rescaledImage = null ; loadImage ( ) ; } } private void loadImage ( ) { status = STATUS_LOADING ; image = ImageFetcher . load ( imageURL ) ; if ( image . getHeight ( observer ) != - 1 ) status = STATUS_LOADED ; } protected void paintComponent ( Graphics g ) { if ( status == STATUS_INCEPTION ) loadImage ( ) ; if ( INDICATION_TYPE == INDICATION_FADE && g instanceof Graphics2D ) { ( ( Graphics2D ) g ) . setComposite ( ! overrideFirstMode && secondMode ? COMPOSITE_MODE_2 : COMPOSITE_MODE_1 ) ; } switch ( status ) { case STATUS_LOADING : paintLoading ( g ) ; break ; case STATUS_LOADED : paintLoaded ( g ) ; break ; default : paintFailed ( g ) ; break ; } } private void paintLoading ( Graphics g ) { g . setColor ( Color . LIGHT_GRAY ) ; paintPlaceholder ( g ) ; } private void paintFailed ( Graphics g ) { g . setColor ( Color . GRAY ) ; paintPlaceholder ( g ) ; } private void paintPlaceholder ( Graphics g ) { Border border = getBorder ( ) ; Insets insets = border . getBorderInsets ( this ) ; int il = insets . left ; int it = insets . top ; g . fillRect ( il , it , maxWidth , maxHeight ) ; getBorder ( ) . paintBorder ( this , g , il , it , maxWidth , maxHeight ) ; int imgWidth = maxWidth ; int imgHeight = maxHeight ; paintBadge ( g , imgWidth , imgHeight ) ; } private void paintBadge ( Graphics g , int imgWidth , int imgHeight ) { if ( badge != null ) { int bwidth = badge . getWidth ( null ) ; int bheight = badge . getHeight ( null ) ; if ( bwidth > 0 && bheight > 0 && imgWidth > bwidth && imgHeight > bheight ) { Border border = getBorder ( ) ; Insets insets = border . getBorderInsets ( this ) ; int x = maxWidth / 2 + imgWidth / 2 - bwidth - 2 ; int y = maxHeight / 2 + imgHeight / 2 - bheight - 2 ; g . drawImage ( badge , insets . left + x , insets . top + y , bwidth , bheight , null ) ; } } } private void paintLoaded ( Graphics g ) { if ( rescaledImage == null ) { rescaledImage = rescaleImage ( image ) ; if ( rescaledImage != null ) image = null ; } if ( rescaledImage != null ) { int width = rescaledImage . getWidth ( observer ) ; int height = rescaledImage . getHeight ( observer ) ; if ( width == - 1 || height == - 1 ) { paintLoading ( g ) ; } else { Border border = getBorder ( ) ; Insets insets = border . getBorderInsets ( this ) ; int picX = ( maxWidth - width ) / 2 + insets . left ; int picY = ( maxHeight - height ) / 2 + insets . top ; g . drawImage ( rescaledImage , picX , picY , width , height , observer ) ; if ( INDICATION_TYPE == INDICATION_BORDER && g instanceof Graphics2D ) { ( ( Graphics2D ) g ) . setComposite ( ! overrideFirstMode && secondMode ? COMPOSITE_MODE_2 : COMPOSITE_MODE_1 ) ; } border . paintBorder ( this , g , picX , picY , width , height ) ; if ( INDICATION_TYPE == INDICATION_FRAME && ! secondMode ) { g . setColor ( Color . WHITE ) ; g . drawRect ( picX + 1 , picY + 1 , width - 2 , height - 2 ) ; g . setColor ( Color . BLACK ) ; g . drawRect ( picX , picY , width , height ) ; } paintBadge ( g , width , height ) ; } } } private Image rescaleImage ( Image src ) { Image dest = null ; int imgWidth = src . getWidth ( observer ) ; if ( imgWidth != - 1 ) { int imgHeight = src . getHeight ( observer ) ; if ( imgHeight != - 1 ) { int picWidth = imgWidth ; int picHeight = imgHeight ; if ( picWidth > maxWidth || picHeight > maxHeight ) { picWidth = imgWidth > imgHeight ? maxWidth : ( imgWidth * maxHeight / imgHeight ) ; picHeight = imgWidth > imgHeight ? ( imgHeight * maxWidth / imgWidth ) : maxHeight ; } dest = src . getScaledInstance ( picWidth , picHeight , Image . SCALE_SMOOTH ) ; } } return dest ; } protected void paintBorder ( Graphics g ) { } protected void processMouseEvent ( MouseEvent e ) { super . processMouseEvent ( e ) ; switch ( e . getID ( ) ) { case MouseEvent . MOUSE_ENTERED : overrideFirstMode = true ; repaint ( ) ; break ; case MouseEvent . MOUSE_EXITED : overrideFirstMode = false ; repaint ( ) ; break ; default : break ; } } private class ImageHandler implements ImageObserver { public boolean imageUpdate ( Image img , int infoflags , int x , int y , int width , int height ) { boolean loaded = ( infoflags & ( ALLBITS | ERROR | ABORT ) ) != 0 ; if ( loaded ) { status = ( infoflags & ALLBITS ) != 0 ? STATUS_LOADED : STATUS_FAILED ; repaint ( 0 ) ; } return ! loaded ; } } } 
=======
abstract class StringPattern extends Pattern { StringPattern ( int hc ) { super ( false , DATA_CONTENT_TYPE , hc ) ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
