package main.java.com.hodgepodge.framework.media;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface TLVField {

    public short index();

}
