package be.home.main.tools;

import org.xml.sax.XMLReader;
import java.io.InputStream;
import org.xml.sax.InputSource;
import java.io.FileInputStream;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.SAXParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;


public class XMLValidator  extends DefaultHandler {

    protected static final String VALIDATION_FEATURE_ID = "http://xml.org/sax/features/validation";
    protected static final String SCHEMA_VALIDATION_FEATURE_ID = "http://apache.org/xml/features/validation/schema";
    protected static final String DEFAULT_PARSER_NAME = "org.apache.xerces.parsers.SAXParser";
    protected static final boolean DEFAULT_VALIDATION = false;
    protected static final boolean DEFAULT_SCHEMA_VALIDATION = false;
    protected long fElements;
    protected long fAttributes;
    protected long fCharacters;
    protected long fIgnorableWhitespace;
    protected long fTagCharacters;
    protected long fOtherCharacters;
    protected int fErrors;
    protected static StringBuffer output;

            public static void main(final String[] args) {
                //
                // System.out.println(validate(args[0]));
                String filename = "C:\\Temp\\pmt\\FULL_ESH_160745.xml";
                System.out.println(validate(filename));
            }

            public void printResults(final String uri, final long time, final long memory) {
                XMLValidator.output.append(String.valueOf(String.valueOf(new StringBuffer("\nTime: ").append(time).append(" ms"))));
                if (memory != Long.MIN_VALUE) {
                    XMLValidator.output.append(String.valueOf(String.valueOf(new StringBuffer("\nMemory used: ").append(memory).append(" bytes"))));
                }
                XMLValidator.output.append("\nXML Elements: ".concat(String.valueOf(String.valueOf(this.fElements))));
                XMLValidator.output.append("\nXML Attributes: ".concat(String.valueOf(String.valueOf(this.fAttributes))));
                XMLValidator.output.append("\nCharacters: ".concat(String.valueOf(String.valueOf(this.fCharacters))));
                XMLValidator.output.append("\n");
                if (this.fErrors > 0) {
                    XMLValidator.output.append(String.valueOf(String.valueOf(new StringBuffer("\nRESULT : INVALID (").append(this.fErrors).append(" errors)"))));
                }
                else {
                    XMLValidator.output.append("\nRESULT : VALID");
                }
            }

            public void startDocument() throws SAXException {
                this.fElements = 0L;
                this.fAttributes = 0L;
                this.fCharacters = 0L;
                this.fIgnorableWhitespace = 0L;
                this.fTagCharacters = 0L;
            }

            public void startElement(final String uri, final String local, final String raw, final Attributes attrs) throws SAXException {
                ++this.fElements;
                ++this.fTagCharacters;
                this.fTagCharacters += raw.length();
                if (attrs != null) {
                    final int attrCount = attrs.getLength();
                    this.fAttributes += attrCount;
                    for (int i = 0; i < attrCount; ++i) {
                        ++this.fTagCharacters;
                        this.fTagCharacters += attrs.getQName(i).length();
                        ++this.fTagCharacters;
                        ++this.fTagCharacters;
                        this.fOtherCharacters += attrs.getValue(i).length();
                        ++this.fTagCharacters;
                    }
                }
                ++this.fTagCharacters;
            }

            public void characters(final char[] ch, final int start, final int length) throws SAXException {
                this.fCharacters += length;
            }

            public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
                this.fIgnorableWhitespace += length;
            }

            public void processingInstruction(final String target, final String data) throws SAXException {
                this.fTagCharacters += 2;
                this.fTagCharacters += target.length();
                if (data != null && data.length() > 0) {
                    ++this.fTagCharacters;
                    this.fOtherCharacters += data.length();
                }
                this.fTagCharacters += 2;
            }

            public void warning(final SAXParseException ex) throws SAXException {
                this.printError("Warning", ex);
            }

            public void error(final SAXParseException ex) throws SAXException {
                this.printError("Error", ex);
            }

            public void fatalError(final SAXParseException ex) throws SAXException {
                this.printError("Fatal Error", ex);
            }

            protected void printError(final String type, final SAXParseException ex) {
                ++this.fErrors;
                XMLValidator.output.append("[");
                XMLValidator.output.append(type);
                XMLValidator.output.append("] ");
                if (ex == null) {
                    XMLValidator.output.append("\n!!!");
                }
                String systemId = ex.getSystemId();
                if (systemId != null) {
                    final int index = systemId.lastIndexOf(47);
                    if (index != -1) {
                        systemId = systemId.substring(index + 1);
                    }
                    XMLValidator.output.append(systemId);
                }
                XMLValidator.output.append(':');
                XMLValidator.output.append(ex.getLineNumber());
                XMLValidator.output.append(':');
                XMLValidator.output.append(ex.getColumnNumber());
                XMLValidator.output.append(": ");
                XMLValidator.output.append(ex.getMessage());
                XMLValidator.output.append("\n");
            }

            public static String validate(final String file) {
                XMLValidator.output.delete(0, XMLValidator.output.length());
                final XMLValidator validator = new XMLValidator();
                XMLReader parser = null;
                final boolean validation = true;
                final boolean schemaValidation = true;
                try {
                    SAXParserFactory factory = SAXParserFactory.newInstance();
                    parser = factory.newSAXParser().getXMLReader();
                    //parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
                    //parser = XMLReaderFactory.createXMLReader("javax.xml.parsers.SAXParser");

                }
                catch (Exception e) {
                    //XMLValidator.output.append("\nerror: Unable to instantiate parser (org.apache.xerces.parsers.SAXParser)");
                }
                try {
                    parser.setFeature("http://xml.org/sax/features/validation", validation);
                }
                catch (SAXException e2) {
                    XMLValidator.output.append("\nwarning: Parser does not support feature (http://xml.org/sax/features/validation)");
                }
                try {
                    parser.setFeature("http://apache.org/xml/features/validation/schema", schemaValidation);
                }
                catch (SAXNotRecognizedException e3) {
                    XMLValidator.output.append("\nwarning: Parser does not support feature (http://apache.org/xml/features/validation/schema)");
                }
                catch (SAXNotSupportedException e4) {
                    XMLValidator.output.append("\nwarning: Parser does not support feature (http://apache.org/xml/features/validation/schema)");
                }
                parser.setContentHandler(validator);
                parser.setErrorHandler(validator);
                try {
                    final long timeBefore = System.currentTimeMillis();
                    final long memoryBefore = Runtime.getRuntime().freeMemory();
                    final FileInputStream stream = new FileInputStream(file);
                    final InputSource input = new InputSource(stream);
                    parser.parse(input);
                    final long memoryAfter = Runtime.getRuntime().freeMemory();
                    final long timeAfter = System.currentTimeMillis();
                    final long time = timeAfter - timeBefore;
                    final long memory = memoryBefore - memoryAfter;
                    validator.printResults(file, time, memory);
                }
                catch (SAXParseException ex) {}
                catch (Exception e) {
                    XMLValidator.output.append("\nerror: Parse error occurred - ".concat(String.valueOf(String.valueOf(e.getMessage()))));
                    Exception se = e;
                    if (e instanceof SAXException) {
                        se = ((SAXException)e).getException();
                    }
                    if (se != null) {
                        se.printStackTrace(System.err);
                    }
                    else {
                        e.printStackTrace(System.err);
                    }
                }
                String res = XMLValidator.output.toString();
                if (res.length() > 4000) {
                    res = String.valueOf(String.valueOf(res.substring(0, 3997))).concat("...");
                }
                return res;
            }

            static {
                XMLValidator.output = new StringBuffer();
            }

}
