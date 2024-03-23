package frc.lib;
 
@FunctionalInterface
public interface BooleanObjectConsumer<T> {
    void accept(boolean b, T t);
}
