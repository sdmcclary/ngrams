public class ResolverIOException extends IOException { private final ResolverException resolverException ; public ResolverIOException ( ResolverException resolverException ) { this . resolverException = resolverException ; } public ResolverException getResolverException ( ) { return resolverException ; } } 