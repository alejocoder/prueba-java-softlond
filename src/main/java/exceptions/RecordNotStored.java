package exceptions;

public class RecordNotStored extends RuntimeException {

    public RecordNotStored(String message) {
        super(message);
    }

}
