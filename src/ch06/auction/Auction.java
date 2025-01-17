package ch06.auction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Auction implements Serializable {

    private final List<Bid> bids = new ArrayList<>();
    private transient volatile boolean isAuctionStarted;

    public synchronized void addBid(Bid bid) {
        bids.add(bid);
    }

    public synchronized List<Bid> getAllBids() {
        return Collections.unmodifiableList(bids);
    }

    public synchronized Optional<Bid> getHighestBid() {
        return bids.stream().max(Comparator.comparing(Bid::getPrice));
    }

    public void startAuction() {
        isAuctionStarted = true;
    }

    public void stopAuction() {
        isAuctionStarted = false;
    }

    public boolean isAuctionRunning() {
        return isAuctionStarted;
    }

}
