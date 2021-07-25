package jpabook.jpashop.exception;

public class NotEnoughStockException extends RuntimeException{

    //runtimeexception의 메소드를 override 해주는 것임

    public NotEnoughStockException() {
        super();
    }

    public NotEnoughStockException(String message) {
        super(message);
    }

    public NotEnoughStockException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnoughStockException(Throwable cause) {
        super(cause);
    }


}
