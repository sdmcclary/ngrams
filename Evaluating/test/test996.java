public class Client { public static void main ( String [ ] args ) throws DocumentProcessorException { new Client ( ) . run ( ) ; } private Currency chf = Currency . getInstance ( "CHF" ) ; public void run ( ) throws DocumentProcessorException { ClassPathResource resource = new ClassPathResource ( "spring.xml" ) ; BeanFactory factory = new XmlBeanFactory ( resource ) ; Document doc = ( Document ) factory . getBean ( "doc" ) ; doc . setType ( Type . ORDER ) ; doc . setCur ( chf ) ; doc . setReference ( "client document" ) ; doc . setId ( "123" ) ; Item item = ( Item ) factory . getBean ( "item" ) ; item . setCur ( chf ) ; item . setCent ( 1230l ) ; item . setId ( "123" ) ; item . setDesc ( "ein item" ) ; doc . addItem ( item ) ; DocumentProcessor proc = ( DocumentProcessor ) factory . getBean ( "chain" ) ; doc = proc . processDocument ( doc ) ; } } 