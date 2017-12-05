package scheduler.model;

public class CustomerList {

    private static CustomerList firstInstance = null;

    private CustomerList () {}

    public static CustomerList getInstance () {
        if(firstInstance == null) {
            firstInstance = new CustomerList();

            // create customer list.
        }
        return firstInstance;
    }
}
