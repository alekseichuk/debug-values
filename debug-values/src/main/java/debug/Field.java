package debug;

class Field {
    final String javaName;
    final String shortJavaName;
    final String name;
    final String comment;
    final ValueType type;
    final int[] values;
    Object defaultValue;

    public Field(String javaName, String shortJavaName, String name, String comment, ValueType type, int[] values) {
        this.javaName = javaName;
        this.shortJavaName = shortJavaName;
        this.comment = comment;
        this.name = name;
        this.type = type;
        this.values = values;
    }

    @Override
    public String toString() {
        return type
                + " field='" + javaName
                + "', comment='" + comment
                + "', name='" + name + "';";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Field field = (Field) o;

        return javaName.equals(field.javaName);

    }

    @Override
    public int hashCode() {
        return javaName.hashCode();
    }
}
