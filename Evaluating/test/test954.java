public class Bitmap { public static Raster of ( Component c ) throws Exception { BufferedImage image = new BufferedImage ( c . getWidth ( ) , c . getHeight ( ) , BufferedImage . TYPE_INT_RGB ) ; Graphics2D graphics = image . createGraphics ( ) ; graphics . setColor ( Color . WHITE ) ; graphics . fillRect ( 0 , 0 , c . getWidth ( ) , c . getHeight ( ) ) ; c . paint ( graphics ) ; graphics . dispose ( ) ; return image . getRaster ( ) ; } } 