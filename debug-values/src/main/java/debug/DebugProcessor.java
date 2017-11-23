package debug;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static debug.Constants.DEBUG_ANNOTATION_CLASS;
import static debug.Constants.DEBUG_CLASS_FULL_NAME;

@SupportedAnnotationTypes(DEBUG_ANNOTATION_CLASS)
public class DebugProcessor extends AbstractProcessor {
    private Map<String, Map<String, Field>> mGroupToFieldMap = new HashMap<>();
    private Map<String, List<Field>> mClassToFieldMap = new HashMap<>();
    private List<Field> mFields = new LinkedList<>();

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Logger logger = new Logger(processingEnv.getMessager());

        logger.info("Starting..." + annotations);
        if (annotations == null || annotations.isEmpty()) {
            return false;
        }

        Set<? extends Element> debugValues = roundEnv.getElementsAnnotatedWith(DebugValue.class);
        logger.info("Fields: " + debugValues);
        if (debugValues == null || debugValues.isEmpty()) {
            return false;
        }

        for (Element e : debugValues) {
            if (!checkModifiers(e)) {
                logger.error(e, "Field must not be private, static or final", null);
                continue;
            }
            DebugValue debug = e.getAnnotation(DebugValue.class);
            TypeElement cls = (TypeElement) e.getEnclosingElement();
            String className = cls.getQualifiedName().toString();
            String javaShortName = e.getSimpleName().toString();
            String javaName = className + "." + javaShortName;
            String group = isEmpty(debug.group()) ? className : debug.group();
            int[] values = debug.values();
            ValueType type = ValueType.from(e.asType().toString());
            if (type == null) {
                logger.error(e, "Wrong type. Use primitive types or their object counterparts", null);
                return false;
            }
            Field field = new Field(javaName, javaShortName, debug.name(), debug.comment(), type, values);

            mFields.add(field);

            if (!mClassToFieldMap.containsKey(className)) {
                mClassToFieldMap.put(className, new LinkedList<Field>());
            }
            mClassToFieldMap.get(className).add(field);

            if (!mGroupToFieldMap.containsKey(group)) {
                mGroupToFieldMap.put(group, new HashMap<String, Field>());
            }
            mGroupToFieldMap.get(group).put(javaName, field);

            logger.info("Added " + field);
        }
        try {
            logger.info("Target " + DEBUG_CLASS_FULL_NAME);
            Filer filer = processingEnv.getFiler();
            logger.info("Filer is " + filer);
            DebugWriter debugWriter = new PlainTextWriter(filer, logger);
            logger.info("Writer's been init");
            debugWriter.write(mFields, mGroupToFieldMap, mClassToFieldMap);
        } catch (Exception e) {
            logger.error(null, "Can't init PlainTextWriter for " + DEBUG_CLASS_FULL_NAME, e);
        }

        return false;
    }

    private boolean checkModifiers(Element element) {
        return !(element.getModifiers().contains(Modifier.FINAL)
                || element.getModifiers().contains(Modifier.STATIC)
                || element.getModifiers().contains(Modifier.PRIVATE));
    }

    private boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
