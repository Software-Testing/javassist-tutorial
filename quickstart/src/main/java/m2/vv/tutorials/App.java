package m2.vv.tutorials;

import java.lang.Throwable;
import javassist.*;

public class App 
{
    public static void main(String[] args )
    {
        try {
            modifyToString(args);
        }

        catch(Throwable exc) {
            System.out.println("Oh, no! Something went wrong.");
            System.out.println(exc.getMessage());
            exc.printStackTrace();
        }

    }

    public static void createPointClass(String[] args) throws Throwable {

        ClassPool pool = ClassPool.getDefault();
        CtClass pointClass = pool.makeClass("Point");


        // Two different ways to attach a field
        CtField xField = CtField.make("private int x", pointClass);
        CtField yField = new CtField(CtClass.intType, "y", pointClass);
        yField.setModifiers(Modifier.PRIVATE);

        pointClass.addField(xField);
        pointClass.addField(yField);

        // Ways to add a setter
        pointClass.addMethod(CtNewMethod.make("public int getY() { return y; }", pointClass));
        pointClass.addMethod(CtNewMethod.getter("getX", xField));

        pointClass.addConstructor(CtNewConstructor.make("public Point(int x, int y) { this.x = x; this.y = y; }", pointClass));

        pointClass.writeFile();
    }

    public static void logLoadedClasses(String[] args) throws Throwable {
        ClassPool pool = ClassPool.getDefault();

        Loader loader = new Loader(pool);

        Translator logger = new Translator() {

            public void start(ClassPool pool) throws NotFoundException, CannotCompileException {
                System.out.println("Started");
            }

            public void onLoad(ClassPool pool, String classname) throws NotFoundException, CannotCompileException {
                System.out.println(classname);
            }
        };


        CtClass theClass;



        loader.addTranslator(pool, logger);
        pool.appendClassPath("input/target/classes");
        loader.run("m2.vv.tutorials.QuotesApp", args);

    }

    public static void modifyToString(String[] args) throws Throwable {

        ClassPool pool = ClassPool.getDefault();

        Loader loader = new Loader(pool);

        Translator modifier = new Translator() {

            public void start(ClassPool pool) throws NotFoundException, CannotCompileException {

            }

            public void onLoad(ClassPool pool, String classname) throws NotFoundException, CannotCompileException {

                CtClass targetClass = pool.get(classname);

                try {
                    CtMethod toStringMethod = targetClass.getDeclaredMethod("toString");
                    toStringMethod.setName("__original__toString");
                    CtMethod newToString = CtMethod.make("public String toString() {String result = __original__toString(); if(result.length() > 20) return result.substring(0,17) + \"...\"; return result;}", targetClass);

                    targetClass.addMethod(newToString);
                }
                catch(NotFoundException exc) {
                    //System.out.println(exc);
                }

            }
        };

        loader.addTranslator(pool, modifier);
        pool.appendClassPath("input/target/classes");
        loader.run("m2.vv.tutorials.QuotesApp", args);
    }
}
