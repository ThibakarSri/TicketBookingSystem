public class Customer implements Runnable {
    private final TicketPool ticketPool;
    private final int customerRetrievalRate;
    private final int maxQuantity ;



    public Customer(TicketPool ticketPool, int customerRetrievalRate, int maxQuantity ) {
        this.ticketPool = ticketPool;
        this.customerRetrievalRate = customerRetrievalRate;
        this.maxQuantity  = maxQuantity ;
    }

//    @Override
//    public void run() {
//        for (int i = 0; i < maxQuantity ; i++) {
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
        for (int i = 0; i < maxQuantity  && Main.isRunning(); i++) {
            Ticket ticket = ticketPool.buyTicket();
            Logger.log("Ticket bought by - " + Thread.currentThread().getName() + " - current size is - " + ticketPool.getTicketQueueSize() + " - Ticket is - " + ticket);
            //System.out.println("Ticket purchased by - " + Thread.currentThread().getName() + ": " + ticket);
            try {
                Thread.sleep(customerRetrievalRate * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                //System.out.println("Customer interrupted.");
                Logger.log("Customer interrupted: " + Thread.currentThread().getName());
            }
        }
    }

}
