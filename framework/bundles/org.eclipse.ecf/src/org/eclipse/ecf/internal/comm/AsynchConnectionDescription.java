package org.eclipse.ecf.internal.comm;

public class AsynchConnectionDescription {

    protected String name;
    protected String instantiatorClass;

    protected IAsynchConnectionInstantiator instantiator;
    protected int hashCode = 0;
    protected ClassLoader classLoader = null;

    public AsynchConnectionDescription(ClassLoader loader,

    String name, String instantiatorClass) {
        if (name == null)
            throw new RuntimeException(new InstantiationException(
                    "stagecontainer description name cannot be null"));
        this.classLoader = loader;
        this.name = name;
        this.instantiatorClass = instantiatorClass;
        this.hashCode = name.hashCode();
    }
    public AsynchConnectionDescription(String name,
            IAsynchConnectionInstantiator inst) {
        this.instantiator = inst;
        this.classLoader = this.instantiator.getClass().getClassLoader();
        this.instantiatorClass = this.instantiatorClass.getClass().getName();
        this.hashCode = name.hashCode();
    }
    public String getName() {
        return name;
    }
    public ClassLoader getClassLoader() {
        return classLoader;
    }
    public boolean equals(Object other) {
        if (!(other instanceof AsynchConnectionDescription))
            return false;
        AsynchConnectionDescription scd = (AsynchConnectionDescription) other;
        return scd.name.equals(name);
    }

    public int hashCode() {
        return hashCode;
    }

    public String toString() {
        StringBuffer b = new StringBuffer("AsynchConnectionDescription[");
        b.append(name).append(";");
        b.append(instantiatorClass).append("]");
        return b.toString();
    }

    protected IAsynchConnectionInstantiator getInstantiator() 
    throws ClassNotFoundException, InstantiationException,
    IllegalAccessException {
        synchronized (this) {
            if (instantiator == null)
                initializeInstantiator(classLoader);
            return instantiator;
        }
    }

    protected void initializeInstantiator(ClassLoader cl)
            throws ClassNotFoundException, InstantiationException,
            IllegalAccessException {
        if (cl == null)
            cl = this.getClass().getClassLoader();
        // Load instantiator class
        Class clazz = Class.forName(instantiatorClass, true, cl);
        // Make new instance
        instantiator = (IAsynchConnectionInstantiator) clazz.newInstance();
    }

}