import java.lang.Exception

fun returnValue() {
    val value = readln().toInt()
    val n: Number

    try {
        // code that may throw an exception
    } catch (e: RuntimeException) {
        // ...
    } catch (e: Exception) {
        // ...
    }

    try {
        // code that may throw an exception
    } catch (e: Exception) {
        // ...
    } catch (e: RuntimeException) {
        // ...
    }
    try {
        // code that may throw an exception
    } catch 1: (e: ArithmeticException) {
        // ...
    } catch 2: (e: RuntimeException) {
        // ...
    } catch 3: (e: Exception) {
        // ...
    }
}