package debug;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public class Logger {
    private final Messager mMessanger;

    public Logger(Messager messager) {
        mMessanger = messager;
    }

    void error(Element element, String message, Throwable e) {
        String exception = null;
        if (e != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintWriter writer = new PrintWriter(baos, true);
            e.printStackTrace(writer);
            exception = baos.toString();
        }
        String message2 = "DebugValues > " + message + (exception != null ? (" Exception: " + exception) : "");
        if (element == null) {
            mMessanger.printMessage(Diagnostic.Kind.ERROR, message2);
        } else {
            mMessanger.printMessage(Diagnostic.Kind.ERROR, message2, element);
        }
    }

    void info(String message) {
        mMessanger.printMessage(Diagnostic.Kind.NOTE, "DebugValues > " + message);
    }
}
