public class MyWicketApp extends SpringWebApplication { public MyWicketApp ( ) { } @ Override protected void init ( ) { super . init ( ) ; SpringComponentInjector i = new SpringComponentInjector ( this ) ; addComponentInstantiationListener ( i ) ; } @ Override public Class < ? extends Page > getHomePage ( ) { return MyHomePage . class ; } public Authenticator getAuthenticator ( ) { return ( Authenticator ) internalGetApplicationContext ( ) . getBean ( "authenticator" ) ; } } 