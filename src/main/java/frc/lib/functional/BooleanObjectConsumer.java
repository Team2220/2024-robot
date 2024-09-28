package frc.lib.functional;
 
@FunctionalInterface
public interface BooleanObjectConsumer<T> {
    void accept(boolean b, T t);
}
