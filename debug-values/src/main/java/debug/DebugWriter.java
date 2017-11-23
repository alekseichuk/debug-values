package debug;

import java.io.IOException;
import java.util.List;
import java.util.Map;

abstract class DebugWriter {

    abstract void write(List<Field> fields,
                        Map<String, Map<String, Field>> groupToFieldMap,
                        Map<String, List<Field>> classToFieldMap) throws IOException;
}
