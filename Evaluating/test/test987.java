public class HtmlParser00 implements HtmlParser { Reader input ; Writer output ; public void parse ( Reader input , Writer output ) throws Exception { this . input = input ; this . output = output ; int c ; while ( - 1 != ( c = input . read ( ) ) ) { output . write ( c ) ; } } public Reader getInput ( ) { return input ; } public void setInput ( Reader input ) { this . input = input ; } public Writer getOutput ( ) { return output ; } public void setOutput ( Writer output ) { this . output = output ; } } 