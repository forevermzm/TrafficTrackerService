package exceptions;

public class RequestChecker {
    public static void checkRequestInput(boolean valid, String errorMessage) {
        if (!valid) {
            throw new BadRequestException(errorMessage);
        }
    }
}
