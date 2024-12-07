public class Customer implements Runnable {
    private final TicketPool ticketPool;
    private final int customerRetrievalRate;
    private final int quantity;

    public Customer(TicketPool ticketPool, int customerRetrievalRate, int quantity) {
        this.ticketPool = ticketPool;
        this.customerRetrievalRate = customerRetrievalRate;
        this.quantity = quantity;
    }

//    @Override
//    public void run() {
//        for (int i = 0; i < quantity; i++) {
//            Ticket ticket = ticketPool.buyTicket();
//            System.out.println("Ticket purchased by - " + Thread.currentThread().getName() + ": " + ticket);
//            try {
//                Thread.sleep(customerRetrievalRate * 1000L);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                throw new RuntimeException("Thread interrupted: " + e.getMessage());
//            }
//        }
//    }

    @Override
    public void run() {
        for (int i = 0; i < quantity && Main.isRunning(); i++) {
            Ticket ticket = ticketPool.buyTicket();
            System.out.println("Ticket purchased by - " + Thread.currentThread().getName() + ": " + ticket);
            try {
                Thread.sleep(customerRetrievalRate * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Customer interrupted.");
            }
        }
    }

}
