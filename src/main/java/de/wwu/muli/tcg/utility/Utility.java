package de.wwu.muli.tcg.utility;

public class Utility {
    private Utility() {}

    public static String toFirstLower(String s) {
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }

    public static String toFirstUpper(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static boolean isNull(Object o) { // TODO Adapt if outside of VM
        return o == null;
    }

    public static boolean isFloatingPointClass(Class<?> oc) {
        return Double.class.equals(oc) || Float.class.equals(oc) || double.class.equals(oc) || float.class.equals(oc);
    }

    public static boolean isWrappingClass(Class<?> oc) { // TODO Adapt if outside of VM
        return Integer.class.equals(oc) || Long.class.equals(oc) || Double.class.equals(oc) || Float.class.equals(oc) ||
                Short.class.equals(oc) || Byte.class.equals(oc) || Boolean.class.equals(oc);
    }

    public static boolean isPrimitiveClass(Class<?> oc) { // TODO Adapt if outside of VM
        return int.class.equals(oc) || long.class.equals(oc) || double.class.equals(oc) || float.class.equals(oc) ||
                short.class.equals(oc) || byte.class.equals(oc) || boolean.class.equals(oc);
    }

    public static boolean isStringClass(Class<?> oc) {
        return oc.equals(String.class);
    }
}
