package com.hap.common.center.util;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author "hulei"
 *
 */
public class ArrayUtil {

    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    /**
     * 检查是否为空
     * @param objectArr
     * @return
     */
    public static boolean isEmpty(Object[] objectArr) {
        return ((null == objectArr) || (0 == objectArr.length));
    }
    /**
     * 检查是否为空
     * @return
     */
    public static boolean isEmpty(int[] intArr) {
        return ((null == intArr) || (0 == intArr.length));
    }
    /**
     * 检查非空
     * @param objectArr
     * @return
     */
    public static boolean isNotEmpty(Object[] objectArr) {
        return (!(isEmpty(objectArr)));
    }
    /**
     * 检查是否全为空，全为空才返回true，否则返回false
     * @param objectArr
     * @return
     */
    public static boolean isAllNull(Object[] objectArr) {
        boolean flag = true;

        if (null != objectArr) {
            int length = objectArr.length;

            for (int i = 0; i < length; ++i) {
                if (null != objectArr[i]) {
                    flag = false;

                    break;
                }
            }
        }

        return flag;
    }
    /**
     * 检查是否部分为空，有空则返回true
     * @param objectArr
     * @return
     */
    public static boolean isSomeNull(Object[] objectArr) {
        boolean flag = false;

        if (null != objectArr) {
            int length = objectArr.length;

            for (int i = 0; i < length; ++i) {
                if (null == objectArr[i]) {
                    flag = true;

                    break;
                }
            }
        }

        return flag;
    }
    /**
     * 增加到数组
     * @param objectArr
     * @param object
     * @return
     */
    public static Object[] add(Object[] objectArr, Object object) {
        Object[] newObjectArr = null;

        int length = objectArr.length;
        newObjectArr = new Object[length + 1];
        System.arraycopy(objectArr, 0, newObjectArr, 0, length);
        newObjectArr[length] = object;

        return newObjectArr;
    }
    /**
     * 增加到数组
     * @param objectArr
     * @param object
     * @param transferArr
     * @return
     */
    public static Object[] add(Object[] objectArr, Object object,
                               Object[] transferArr) {
        Object[] newObjectArr = null;

        int length = objectArr.length;
        newObjectArr = new Object[length + 1];
        System.arraycopy(objectArr, 0, newObjectArr, 0, length);
        newObjectArr[length] = object;

        List list = Arrays.asList(newObjectArr);
        transferArr = list.toArray(transferArr);

        return transferArr;
    }
    /**
     * 增加到数组
     * @param objectArr1
     * @param objectArr2
     * @return
     */
    public static Object[] addAll(Object[] objectArr1, Object[] objectArr2) {
        Object[] newObjectArr = null;

        int length1 = objectArr1.length;
        int length2 = objectArr2.length;
        newObjectArr = new Object[length1 + length2];
        System.arraycopy(objectArr1, 0, newObjectArr, 0, length1);
        System.arraycopy(objectArr2, 0, newObjectArr, length1, length2);

        return newObjectArr;
    }
    /**
     * 增加到数组
     * @param objectArr1
     * @param objectArr2
     * @param transferArr
     * @return
     */
    public static Object[] addAll(Object[] objectArr1, Object[] objectArr2,
                                  Object[] transferArr) {
        Object[] newObjectArr = null;

        int length1 = objectArr1.length;
        int length2 = objectArr2.length;
        newObjectArr = new Object[length1 + length2];
        System.arraycopy(objectArr1, 0, newObjectArr, 0, length1);
        System.arraycopy(objectArr2, 0, newObjectArr, length1, length2);

        List list = Arrays.asList(newObjectArr);
        transferArr = list.toArray(transferArr);

        return transferArr;
    }

    public static void debugPrint(PrintStream pr, Object[] arr) {
        if (null != arr) {
            List list = new ArrayList(Arrays.asList(arr));
            pr.println(list);
        }
    }

    /**org.apache.commons.lang3.ArrayUtils
     * chengjun
     * 数组中添加结果
     * @param array
     * @param element
     * @param newArrayComponentType
     * @return
     */
    public static <T>Object[] add(final Object[] array, final Object element,  Class<?> newArrayComponentType) {
        final Object[] newArray = (Object[])copyArrayGrow1(array, Integer.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    private static Object copyArrayGrow1(final Object array, final Class<?> newArrayComponentType) {
        if (array != null) {
            final int arrayLength = Array.getLength(array);
            final Object newArray = Array.newInstance(array.getClass().getComponentType(), arrayLength + 1);
            System.arraycopy(array, 0, newArray, 0, arrayLength);
            return newArray;
        }
        return Array.newInstance(newArrayComponentType, 1);
    }

    public static final int INDEX_NOT_FOUND = -1;
    /**org.apache.commons.lang3.ArrayUtils
     * chengjun
     * @param array
     * @param objectToFind
     * @return
     */
    public static boolean contains(final Object[] array, final Object objectToFind) {
        return indexOf(array, objectToFind) != INDEX_NOT_FOUND;
    }
    /**org.apache.commons.lang3.ArrayUtils
     * chengjun
     * @param array
     * @param objectToFind
     * @return
     */
    public static int indexOf(final Object[] array, final Object objectToFind) {
        return indexOf(array, objectToFind, 0);
    }
    /**org.apache.commons.lang3.ArrayUtils
     * chengjun
     * @param array
     * @param objectToFind
     * @param startIndex
     * @return
     */
    public static int indexOf(final Object[] array, final Object objectToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (objectToFind == null) {
            for (int i = startIndex; i < array.length; i++) {
                if (array[i] == null) {
                    return i;
                }
            }
        } else if (array.getClass().getComponentType().isInstance(objectToFind)) {
            for (int i = startIndex; i < array.length; i++) {
                if (objectToFind.equals(array[i])) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }
    /**org.apache.commons.lang3.ArrayUtils
     * chengjun
     * @param array
     * @param element
     * @return
     */
    public static <T> T[] removeElement(final T[] array, final Object element) {
        final int index = indexOf(array, element);
        if (index == INDEX_NOT_FOUND) {
            return clone(array);
        }
        return remove(array, index);
    }
    /**org.apache.commons.lang3.ArrayUtils
     * chengjun
     * @param array
     * @return
     */
    public static <T> T[] clone(final T[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    @SuppressWarnings("unchecked") // remove() always creates an array of the same type as its input
    public static <T> T[] remove(final T[] array, final int index) {
        return (T[]) remove((Object) array, index);
    }

    private static Object remove(final Object array, final int index) {
        final int length = getLength(array);
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
        }

        final Object result = Array.newInstance(array.getClass().getComponentType(), length - 1);
        System.arraycopy(array, 0, result, 0, index);
        if (index < length - 1) {
            System.arraycopy(array, index + 1, result, index, length - index - 1);
        }

        return result;
    }

    public static int getLength(final Object array) {
        if (array == null) {
            return 0;
        }
        return Array.getLength(array);
    }



}

