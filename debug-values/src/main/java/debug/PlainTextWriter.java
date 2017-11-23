package debug;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Filer;

import static debug.Constants.DEBUG_CLASS_FULL_NAME;
import static debug.Constants.DEBUG_CLASS_SHORT_NAME;
import static debug.Constants.GEN_CLASS_FIELDS_MAP_NAME;
import static debug.Constants.GEN_GROUP_FIELDS_MAP_NAME;
import static debug.Constants.GEN_NAME_FIELD_MAP_NAME;

class PlainTextWriter extends DebugWriter {
    private final Writer mWriter;
    private final Logger mLogger;

    PlainTextWriter(Filer filer, Logger logger) throws IOException {
        mWriter = filer
                .createSourceFile(DEBUG_CLASS_FULL_NAME)
                .openWriter();
        mLogger = logger;
    }

    @Override
    public void write(List<Field> fields,
                      Map<String, Map<String, Field>> groupToFieldMap,
                      Map<String, List<Field>> classToFieldMap) throws IOException {

        mLogger.info("Generation STARTED");

        mWriter.write(CLASS_TEMPLATE
                .replace(CLASS_NAME, DEBUG_CLASS_SHORT_NAME)
                .replace(CLASS_FIELDS, writeFields(fields))
                .replace(CLASS_GROUP_TO_FIELDS, writeGroupToFieldMap(groupToFieldMap))
                .replace(CLASS_CLASS_TO_FIELDS, writeClassToFieldMap(classToFieldMap))
                .replace(CLASS_NAMES_TO_FIELDS, writeNameToFieldMap(fields))
        );

        mWriter.flush();
        mWriter.close();

        mLogger.info("Generation DONE");
    }

    private CharSequence writeFields(List<Field> fields) {
        StringBuilder sb = new StringBuilder();
        for (Field f : fields) {
            sb.append('\t').append(
                    FIELD_TEMPLATE
                            .replace(FIELD_VAR_NAME, convertFieldToVariable(f))
                            .replace(FIELD_JAVA_NAME, f.javaName)
                            .replace(FIELD_SHORT_JAVA_NAME, f.shortJavaName)
                            .replace(FIELD_USER_NAME, f.name)
                            .replace(FIELD_USER_COMMENT, f.comment)
                            .replace(FIELD_TYPE, "ValueType." + f.type.name())
                            .replace(FIELD_INT_VALUES, intArrayOfValue(f.values))
            ).append('\n');
        }
        sb.append('\n');
        return sb.toString();
    }

    private CharSequence writeGroupToFieldMap(Map<String, Map<String, Field>> groupToFieldMap) {
        StringBuilder sb = new StringBuilder();
        for (String group : groupToFieldMap.keySet()) {
            String groupVar = convertFieldToVariable(group + "Group");
            sb.append("\t\t").append(
                    GROUP_VARIABLE_TEMPLATE
                            .replace(GROUP_VAR_NAME, groupVar)
            ).append('\n');

            for (String javaName : groupToFieldMap.get(group).keySet()) {
                sb.append("\t\t").append(
                        ADDING_TO_GROUP_VAR_TEMPLATE
                                .replace(GROUP_VAR_NAME, groupVar)
                                .replace(GROUP_FIELD_JAVA_NAME, javaName)
                                .replace(GROUP_FIELD_NAME, convertFieldToVariable(groupToFieldMap.get(group).get(javaName)))
                ).append('\n');
            }

            sb.append("\t\t").append(
                    ADDING_TO_GLOBAL_GROUP_TEMPLATE
                            .replace(GROUP_NAME, group)
                            .replace(GROUP_VAR_NAME, groupVar)
            ).append("\n\n");

        }
        return sb.toString();
    }

    private CharSequence writeClassToFieldMap(Map<String, List<Field>> classToFieldMap) {
        StringBuilder sb = new StringBuilder();
        StringBuilder fieldsCsv = new StringBuilder();
        for (String clazz : classToFieldMap.keySet()) {
            for (Field field : classToFieldMap.get(clazz)) {
                fieldsCsv
                        .append(fieldsCsv.length() == 0 ? "" : ", ")
                        .append(convertFieldToVariable(field));
            }

            sb.append("\t\t").append(
                    ADDING_TO_CLASS_TO_FIELD_TEMPLATE
                            .replace(JAVA_CLASS_NAME, clazz)
                            .replace(FIELDS_CSV, fieldsCsv)
            ).append('\n');

            fieldsCsv.delete(0, fieldsCsv.length());
        }
        sb.append('\n');
        return sb.toString();
    }

    private CharSequence writeNameToFieldMap(List<Field> fields) {
        StringBuilder sb = new StringBuilder();
        for (Field field : fields) {
            sb.append("\t\t").append(
                    ADDING_FIELD_NAME_TO_FIELD_TEMPLATE
                            .replace(FIELD_JAVA_NAME, field.javaName)
                            .replace(FIELD_VAR_NAME, convertFieldToVariable(field))
            ).append('\n');
        }
        sb.append('\n');
        return sb.toString();
    }

    private static final String CLASS_NAME = "{debug_class_short_name}";
    private static final String CLASS_FIELDS = "{fields_list}";
    private static final String CLASS_GROUP_TO_FIELDS = "{group_to_fields}";
    private static final String CLASS_CLASS_TO_FIELDS = "{class_to_fields}";
    private static final String CLASS_NAMES_TO_FIELDS = "{names_to_fields}";

    private static final String CLASS_TEMPLATE =
            "package debug;\n\n" +
                    "final class " + CLASS_NAME + " {\n" +
                    "\tpublic static final java.util.Map<String, java.util.Map<String, Field>> " + GEN_GROUP_FIELDS_MAP_NAME + " = new java.util.HashMap<String, java.util.Map<String, Field>>();\n" +
                    "\tpublic static final java.util.Map<String, java.util.List<Field>> " + GEN_CLASS_FIELDS_MAP_NAME + " = new java.util.HashMap<String, java.util.List<Field>>();\n" +
                    "\tpublic static final java.util.Map<String, Field> " + GEN_NAME_FIELD_MAP_NAME + " = new java.util.HashMap<String, Field>();\n\n" +

                    CLASS_FIELDS +
                    "\tstatic {\n" +
                    CLASS_GROUP_TO_FIELDS +
                    CLASS_CLASS_TO_FIELDS +
                    CLASS_NAMES_TO_FIELDS +
                    "\t}\n" +
                    "\n" +
                    "}";

    private static final String FIELD_VAR_NAME = "{field_var_name}";
    private static final String FIELD_JAVA_NAME = "{field_java_name}";
    private static final String FIELD_SHORT_JAVA_NAME = "{field_short_java_name}";
    private static final String FIELD_USER_NAME = "{field_user_name}";
    private static final String FIELD_USER_COMMENT = "{field_user_comment}";
    private static final String FIELD_TYPE = "{field_type}";
    private static final String FIELD_INT_VALUES = "{field_int_values}";

    private static final String FIELD_TEMPLATE = "static final Field "
            + FIELD_VAR_NAME
            + " = new Field(\"" + FIELD_JAVA_NAME
            + "\", \"" + FIELD_SHORT_JAVA_NAME
            + "\", \"" + FIELD_USER_NAME
            + "\", \"" + FIELD_USER_COMMENT
            + "\", " + FIELD_TYPE
            + ", " + FIELD_INT_VALUES
            + ");";

    private static final String GROUP_VAR_NAME = "{group_var_name}";
    private static final String GROUP_VARIABLE_TEMPLATE = "java.util.HashMap<String, Field> " + GROUP_VAR_NAME + " = new java.util.HashMap<String, Field>();";

    private static final String GROUP_FIELD_JAVA_NAME = "{group_field_java_name}";
    private static final String GROUP_FIELD_NAME = "{group_field_name}";
    private static final String ADDING_TO_GROUP_VAR_TEMPLATE = GROUP_VAR_NAME + ".put(\"" + GROUP_FIELD_JAVA_NAME + "\", " + GROUP_FIELD_NAME + ");";

    private static final String GROUP_NAME = "{group_name}";
    private static final String ADDING_TO_GLOBAL_GROUP_TEMPLATE = GEN_GROUP_FIELDS_MAP_NAME + ".put(\"" + GROUP_NAME + "\", " + GROUP_VAR_NAME + ");";

    private static final String FIELDS_CSV = "{fields_csv}";
    private static final String JAVA_CLASS_NAME = "{java_class_name}";
    private static final String ADDING_TO_CLASS_TO_FIELD_TEMPLATE = GEN_CLASS_FIELDS_MAP_NAME + ".put(\"" + JAVA_CLASS_NAME + "\", java.util.Arrays.asList(" + FIELDS_CSV + "));";

    private static final String ADDING_FIELD_NAME_TO_FIELD_TEMPLATE = GEN_NAME_FIELD_MAP_NAME + ".put(\"" + FIELD_JAVA_NAME + "\", " + FIELD_VAR_NAME + ");";


    private static String convertFieldToVariable(Field field) {
        return field.javaName.replace('.', '_');
    }

    private static String convertFieldToVariable(String variable) {
        return variable.replace('.', '_').replace(" ", "_");
    }

    private CharSequence intArrayOfValue(int[] values) {
        StringBuilder sb = new StringBuilder("new int[]{");
        for (int i = 0; i < values.length; i++) {
            sb.append(i == 0 ? "" : ", ").append(values[i]);
        }
        sb.append("}");
        return sb.toString();
    }
}
