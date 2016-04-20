package be.home.picmgt.model;

import java.io.FileInputStream;
import java.io.PrintStream;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class Validator extends DefaultHandler
{

    public static void main(String args[])
    {
        //System.out.println(validate(args[0]));
    	validate("c:/Temp/INVALID_112538.xml");
    	System.out.println(output);
    }

    public Validator()
    {
    }

    public void printResults(String uri, long time, long memory)
    {
        output.append(String.valueOf(String.valueOf((new StringBuffer("\nTime: ")).append(time).append(" ms"))));
        if(memory != 0x8000000000000000L)
            output.append(String.valueOf(String.valueOf((new StringBuffer("\nMemory used: ")).append(memory).append(" bytes"))));
        output.append("\nXML Elements: ".concat(String.valueOf(String.valueOf(fElements))));
        output.append("\nXML Attributes: ".concat(String.valueOf(String.valueOf(fAttributes))));
        output.append("\nCharacters: ".concat(String.valueOf(String.valueOf(fCharacters))));
        output.append("\n");
        if(fErrors > 0)
            output.append(String.valueOf(String.valueOf((new StringBuffer("\nRESULT : INVALID (")).append(fErrors).append(" errors)"))));
        else
            output.append("\nRESULT : VALID");
    }

    public void startDocument()
        throws SAXException
    {
        fElements = 0L;
        fAttributes = 0L;
        fCharacters = 0L;
        fIgnorableWhitespace = 0L;
        fTagCharacters = 0L;
    }

    public void startElement(String uri, String local, String raw, Attributes attrs)
        throws SAXException
    {
        fElements++;
        fTagCharacters++;
        fTagCharacters += raw.length();
        if(attrs != null)
        {
            int attrCount = attrs.getLength();
            fAttributes += attrCount;
            for(int i = 0; i < attrCount; i++)
            {
                fTagCharacters++;
                fTagCharacters += attrs.getQName(i).length();
                fTagCharacters++;
                fTagCharacters++;
                fOtherCharacters += attrs.getValue(i).length();
                fTagCharacters++;
            }

        }
        fTagCharacters++;
    }

    public void characters(char ch[], int start, int length)
        throws SAXException
    {
        fCharacters += length;
    }

    public void ignorableWhitespace(char ch[], int start, int length)
        throws SAXException
    {
        fIgnorableWhitespace += length;
    }

    public void processingInstruction(String target, String data)
        throws SAXException
    {
        fTagCharacters += 2;
        fTagCharacters += target.length();
        if(data != null && data.length() > 0)
        {
            fTagCharacters++;
            fOtherCharacters += data.length();
        }
        fTagCharacters += 2;
    }

    public void warning(SAXParseException ex)
        throws SAXException
    {
        printError("Warning", ex);
    }

    public void error(SAXParseException ex)
        throws SAXException
    {
        printError("Error", ex);
    }

    public void fatalError(SAXParseException ex)
        throws SAXException
    {
        printError("Fatal Error", ex);
    }

    protected void printError(String type, SAXParseException ex)
    {
        fErrors++;
        output.append("[");
        output.append(type);
        output.append("] ");
        if(ex == null)
            output.append("\n!!!");
        String systemId = ex.getSystemId();
        if(systemId != null)
        {
            int index = systemId.lastIndexOf('/');
            if(index != -1)
                systemId = systemId.substring(index + 1);
            output.append(systemId);
        }
        output.append(':');
        output.append(ex.getLineNumber());
        output.append(':');
        output.append(ex.getColumnNumber());
        output.append(": ");
        output.append(ex.getMessage());
        output.append("\n");
    }

    public static String validate(String file)
    {
        output.delete(0, output.length());
        Validator validator = new Validator();
        XMLReader parser = null;
        boolean validation = true;
        boolean schemaValidation = true;
        try
        {
            parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        }
        catch(Exception e)
        {
            output.append("\nerror: Unable to instantiate parser (org.apache.xerces.parsers.SAXParser)");
        }
        try
        {
            parser.setFeature("http://xml.org/sax/features/validation", validation);
        }
        catch(SAXException e)
        {
            output.append("\nwarning: Parser does not support feature (http://xml.org/sax/features/validation)");
        }
        try
        {
            parser.setFeature("http://apache.org/xml/features/validation/schema", schemaValidation);
        }
        catch(SAXNotRecognizedException e)
        {
            output.append("\nwarning: Parser does not support feature (http://apache.org/xml/features/validation/schema)");
        }
        catch(SAXNotSupportedException e)
        {
            output.append("\nwarning: Parser does not support feature (http://apache.org/xml/features/validation/schema)");
        }
        parser.setContentHandler(validator);
        parser.setErrorHandler(validator);
        try
        {
            long timeBefore = System.currentTimeMillis();
            long memoryBefore = Runtime.getRuntime().freeMemory();
            FileInputStream stream = new FileInputStream(file);
            InputSource input = new InputSource(stream);
            parser.parse(input);
            long memoryAfter = Runtime.getRuntime().freeMemory();
            long timeAfter = System.currentTimeMillis();
            long time = timeAfter - timeBefore;
            long memory = memoryBefore - memoryAfter;
            validator.printResults(file, time, memory);
        }
        catch(SAXParseException saxparseexception) { }
        catch(Exception e)
        {
            output.append("\nerror: Parse error occurred - ".concat(String.valueOf(String.valueOf(e.getMessage()))));
            Exception se = e;
            if(e instanceof SAXException)
                se = ((SAXException)e).getException();
            if(se != null)
                se.printStackTrace(System.err);
            else
                e.printStackTrace(System.err);
        }
        String res = output.toString();
        if(res.length() > 4000)
            res = String.valueOf(String.valueOf(res.substring(0, 3997))).concat("...");
        return res;
    }

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
    protected static StringBuffer output = new StringBuffer();

}
