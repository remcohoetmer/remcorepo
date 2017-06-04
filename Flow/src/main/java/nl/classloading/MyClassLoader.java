package nl.classloading;

import reflection.Special;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;

public class MyClassLoader extends ClassLoader {

  public MyClassLoader(ClassLoader parent) {
    super(parent);
  }

  public Class loadClass(String name) throws ClassNotFoundException {
    if (!"reflection.Special".equals(name))
      return super.loadClass(name);

    String url = "file:/home/remco/git/remcorepo/Flow/target/classes/reflection/Special.class";
    try {
      URL myUrl = new URL(url);
      URLConnection connection = myUrl.openConnection();
      try (InputStream input = connection.getInputStream()) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int data = input.read();
        while (data != -1) {
          buffer.write(data);
          data = input.read();
        }
        input.close();

        byte[] classData = buffer.toByteArray();
        return defineClass("reflection.Special", classData, 0, classData.length);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) throws
    ClassNotFoundException,
    IllegalAccessException,
    InstantiationException {

    ClassLoader parentClassLoader = MyClassLoader.class.getClassLoader();
    MyClassLoader classLoader1 = new MyClassLoader(parentClassLoader);
    Class specialClass1 = classLoader1.loadClass("reflection.Special");

    MyClassLoader classLoader2 = new MyClassLoader(parentClassLoader);
    Class specialClass2 = classLoader2.loadClass("reflection.Special");


    Object special1 = specialClass1.newInstance();
    Object special2 = specialClass2.newInstance();

    System.out.println("specialClass: " + Special.class);
    System.out.println("specialClass1: " + specialClass1);
    System.out.println("specialClass2: " + specialClass2);


    try {
      Method method = special1.getClass().getMethod("doThat", new Class[] { String.class});
      String result = (String) method.invoke(special1, new Object[]{"The task"});

      System.out.println("Result special1: " + result);
      System.out.println("Result special2: " + ((SpecialIF)special2).doThat("Dishes"));
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }

  }
}