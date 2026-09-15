package runtests;

import runtests.lib.Testable;
import runtests.lib.Specification;
import runtests.lib.Report;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.*;
import java.util.*;

public class RunTests {

    public static void main(String[] args) {
        String className;
        System.out.println("Enter the name of the Java class you want to test");
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
            className = r.readLine();
        } 
        catch(IOException e){
            System.out.println("Unable to read the class name.");
            return;
        }
        
        try { 
            // load the class
            Class c = Class.forName(className);
            
            // create an instance of the loaded class
            Object instance = c.getDeclaredConstructor().newInstance();
            
            // iterate over all methods (getMethods doesn't return private methods)
            for (Method method : c.getMethods()) {
                
                // checks methods that have the annotations @Testable and @Specification
                if (!method.isAnnotationPresent(Testable.class)
                        || !method.isAnnotationPresent(Specification.class)) {
                    continue;
                }
                
                Specification spec = method.getAnnotation(Specification.class);
                
                // check that the number of arguments specified in the annotation matches the number of parameters of the method
                if (!checkArgumentNumber(method, spec)) {
                    Report.report(Report.TEST_RESULT.WrongArgs, method.getName(), spec);
                    continue;
                }

                Class<?>[] types = new Class<?>[spec.argTypes().length];
                // convert the argument types specified as strings into Java Class objects
                try {
                    for (int i = 0; i < spec.argTypes().length; i++) {
                        types[i] = fromStringToType(spec.argTypes()[i]);
                    }
                } 
                catch (IllegalArgumentException e) {
                    Report.report(Report.TEST_RESULT.WrongArgs, method.getName(), spec);
                    continue;
                }
                
                // check that the types specified in the annotation match the method parameter types
                if (!checkArguments(method, types)) {
                    Report.report(Report.TEST_RESULT.WrongArgs, method.getName(), spec);
                    continue;
                }
                
                Object[] values = new Object[spec.argValues().length];
                // convert the argument values from strings into objects of the required types
                try {
                    for (int i = 0; i < spec.argValues().length; i++) {
                        values[i] = parseValue(types[i], spec.argValues()[i]);
                    }
                } 
                catch (IllegalArgumentException e) {
                    Report.report(Report.TEST_RESULT.WrongArgs, method.getName(), spec);
                    continue;
                }

                // check that the method return type matches the type specified in the specification and the type of the result value
                if (!checkResult(method, spec)) {
                    Report.report(Report.TEST_RESULT.WrongResultType, method.getName(), spec);
                    continue;
                }
                
                // invoke the method and check its result
                try {
                    Object result = method.invoke(instance, values);

                    String expected = spec.resVal();
                    String got = (result == null) ? "" : result.toString();

                    if (got.equals(expected)) {
                        Report.report(Report.TEST_RESULT.TestSucceeded, method.getName(), spec);
                    } 
                    else {
                        Report.report(Report.TEST_RESULT.TestFailed, method.getName(), spec);
                    }
                } 
                catch (IllegalAccessException e) {
                    System.out.println("The method '" + method.getName() + "' cannot be accessed.");
                } 
                catch (IllegalArgumentException e) {
                    System.out.println("Invalid arguments passed to method '" + method.getName() + "'.");
                } 
                catch (InvocationTargetException e) {
                    System.out.println("The method '" + method.getName() + "' threw an exception.");
                }  
            }
        }

        catch (ClassNotFoundException e) {
            System.out.println("Class not found: " + className);
        } 
        catch (NoSuchMethodException e) {
            System.out.println("The class does not have a default constructor.");
        } 
        catch (InstantiationException e) {
            System.out.println("Could not create an instance of the class.");
        } 
        catch (IllegalAccessException e) {
            System.out.println("The constructor or method cannot be accessed.");
        } 
        catch (IllegalArgumentException e) {
            System.out.println("Invalid arguments passed to the constructor.");
        }
        catch (InvocationTargetException e) {
            System.out.println("The invoked constructor or method threw an exception.");
        }
    }
    
    /**
     * 
     * @param method the method to be tested
     * @param spec the specification that describes the arguments and the result of the method along with their types
     * @return true if the number of method parameters matches both the number of specified argument types and argument values
     *         false otherwise
     */
    private static boolean checkArgumentNumber(Method method, Specification spec) {
        int parameterNumber = method.getParameterCount();

        return parameterNumber == spec.argTypes().length
                && parameterNumber == spec.argValues().length;
    }
    
    /**
     * 
     * @param method the method to be tested
     * @param spec   the specification that describes the arguments and the result of the method along with their types
     * @param types  the arguments' types
     * 
     * @return       true if the specified argument types match the method's parameter types
     *               false otherwise
     */
    private static boolean checkArguments(Method method, Class<?>[] types) {
        Class<?>[] paramTypes = method.getParameterTypes();
        return Arrays.equals(types, paramTypes);
    }
    
    /**
     * 
     * @param method the method to be tested
     * @param spec   the specification that describes the arguments and the result of the method along with their types
     * 
     * @return       true if the method's return type and result value are compatible with the specification
     *               false otherwise
     */
    private static boolean checkResult(Method method, Specification spec) {
        if(method.getReturnType() == void.class && spec.resVal().equals("")){
            return true;
        }
        Class<?> expectedType;
        try {
            expectedType = fromStringToType(spec.resType());
        } catch(ClassNotFoundException e) {
            return false;
        }
        
        if(expectedType != method.getReturnType()) {
            return false;
        }
        
        try {
            parseValue(expectedType, spec.resVal());
        } catch(Exception e) {
            return false;
        }      
        
        return true;
    }
    
    /**
     *
     * @param type  the string representation of the type
     * 
     * @return      the object corresponding to the specified type
     * @throws      ClassNotFoundException if the type cannot be resolved to a valid class
     */
    private static Class<?> fromStringToType(String type) throws ClassNotFoundException {
        switch (type) {
            case "int": return int.class;
            case "double": return double.class;
            case "bool": return boolean.class;
            case "string": return String.class;
            default: 
                throw new ClassNotFoundException("Unsupported type: " + type);
        }
    }
    
    /**
     * 
     * @param type  the expected target type
     * @param value the string representation of the value to be parsed
     * 
     * @return an object of the specified type containing the parsed value
     * @throws IllegalArgumentException if the type is not supported or if the value cannot be converted to the specified type
     */
    private static Object parseValue(Class<?> type, String value) {
        if(type == int.class) return Integer.valueOf(value);
        if(type == double.class) return Double.valueOf(value);
        if(type == boolean.class) {
            if("true".equals(value) || "false".equals(value)) {
                return Boolean.valueOf(value);
            }
            else {
                throw new IllegalArgumentException();
            }
        }
        if(type == String.class) return value;

        throw new IllegalArgumentException();
    }
    
}
