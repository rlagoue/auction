package com.scopic.auction.integration;

import com.scopic.auction.dto.BidDto;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.scopic.auction.utils.CoreMatchers.notOlderThan;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AuctionIntegrationTest extends BaseIntegrationTest {

    public static final Logger LOGGER = LoggerFactory.getLogger(AuctionIntegrationTest.class);

    @Test
    void makeSimpleBidTest() {
        final var itemId = createItem("simple-bid-name", "simple-bid-description");

        var itemDto = fetchItemById(itemId);
        assertEquals(0, itemDto.bids.size());

        final var bidder1 = "makeSimpleBidUser1";

        final var bidValue1 = 20;
        assertEquals("success", makeBid(bidder1, itemId, bidValue1));
        itemDto = fetchItemById(itemId);
        assertEquals(1, itemDto.bids.size());

        assertLastBidIs(bidder1, bidValue1, itemDto.bids);

        String bidder2  = "makeSimpleBidUser2";
        final var bidValue2 = 40;
        assertEquals("success", makeBid(bidder2, itemId, bidValue2));
        itemDto = fetchItemById(itemId);
        assertEquals(2, itemDto.bids.size());

        assertContainsBid(bidder1, bidValue1, itemDto.bids);
        assertLastBidIs(bidder2, bidValue2, itemDto.bids);

        String bidder3  = "makeSimpleBidUser3";
        final var bidValue3 = 30;
        assertEquals("outbidded", makeBid(bidder3, itemId, bidValue3));

        assertContainsBid(bidder1, bidValue1, itemDto.bids);
        assertLastBidIs(bidder2, bidValue2, itemDto.bids);
    }

    @Test
    public void autoBidTest() {
        final var itemId = createItem("auto-bid-name", "auto-bid-description");

        var itemDto = fetchItemById(itemId);
        assertEquals(0, itemDto.bids.size());

        final var bidder1 = "autoBidUser1";
        setupHeaders(bidder1);
        setMaxBidAmount(20, bidder1);
        activateAutoBidOn(itemId);

        itemDto = fetchItemById(itemId);
        assertEquals(1, itemDto.bids.size());
        assertLastBidIs(bidder1, 1, itemDto.bids);

        final var bidder2 = "autoBidUser2";
        setupHeaders(bidder2);
        setMaxBidAmount(40, bidder2);
        activateAutoBidOn(itemId);

        itemDto = fetchItemById(itemId);
        assertEquals(2, itemDto.bids.size());
        assertContainsBid(bidder1, 1, itemDto.bids);
        assertLastBidIs(bidder2, 21, itemDto.bids);

        final var bidder3 = "autoBidUser3";
        setupHeaders(bidder3);
        setMaxBidAmount(25, bidder3);
        activateAutoBidOn(itemId);

        itemDto = fetchItemById(itemId);
        assertEquals(4, itemDto.bids.size());
        assertContainsBid(bidder1, 1, itemDto.bids);
        assertContainsBid(bidder2, 21, itemDto.bids);
        assertContainsBid(bidder3, 25, itemDto.bids);
        assertLastBidIs(bidder2, 26, itemDto.bids);
    }


    @Test
    public void autoBidWhenActivatingAutoBidBeforeSetMaxBidAmountTest() {
        final var itemId = createItem("auto-bid-name", "auto-bid-description");

        var itemDto = fetchItemById(itemId);
        assertEquals(0, itemDto.bids.size());

        final var bidder1 = "autoBidActivationBeforeUser1";
        setupHeaders(bidder1);
        activateAutoBidOn(itemId);
        itemDto = fetchItemById(itemId);
        assertEquals(0, itemDto.bids.size());

        setMaxBidAmount(20, bidder1);
        itemDto = fetchItemById(itemId);
        assertEquals(1, itemDto.bids.size());
        assertLastBidIs(bidder1, 1, itemDto.bids);

        final var bidder2 = "autoBidActivationBeforeUser2";
        setupHeaders(bidder2);
        activateAutoBidOn(itemId);
        itemDto = fetchItemById(itemId);
        assertEquals(1, itemDto.bids.size());
        assertLastBidIs(bidder1, 1, itemDto.bids);

        setMaxBidAmount(40, bidder2);
        itemDto = fetchItemById(itemId);
        assertEquals(2, itemDto.bids.size());
        assertContainsBid(bidder1, 1, itemDto.bids);
        assertLastBidIs(bidder2, 21, itemDto.bids);

        final var bidder3 = "autoBidActivationBeforeUser3";
        assertEquals("outbidded", makeBid(bidder3, itemId, 25));
        itemDto = fetchItemById(itemId);
        assertEquals(4, itemDto.bids.size());
        assertContainsBid(bidder1, 1, itemDto.bids);
        assertContainsBid(bidder2, 21, itemDto.bids);
        assertContainsBid(bidder3, 25, itemDto.bids);
        assertLastBidIs(bidder2, 26, itemDto.bids);

        final var bidder4 = "autoBidActivationBeforeUser4";
        assertEquals("success", makeBid(bidder4, itemId, 42));
        itemDto = fetchItemById(itemId);
        assertEquals(5, itemDto.bids.size());
        assertContainsBid(bidder1, 1, itemDto.bids);
        assertContainsBid(bidder2, 21, itemDto.bids);
        assertContainsBid(bidder3, 25, itemDto.bids);
        assertContainsBid(bidder2, 26, itemDto.bids);
        assertLastBidIs(bidder4, 42, itemDto.bids);
    }

    private void activateAutoBidOn(String itemId) {
        testRestTemplate.postForObject("/activate-auto-bid/{itemId}", null, Void.class, itemId);
    }

    private void assertContainsBid(String username, int bidValue, List<BidDto> bids) {
        assertTrue(bids.stream().anyMatch(bid -> bid.user.username.equals(username) && bid.amount.value.intValue() == bidValue));
    }

    private void assertLastBidIs(String user, int bidValue, List<BidDto> bids) {
        final var bid = bids.stream().sorted((a, b) -> b.time.compareTo(a.time)).findFirst().orElseThrow();
        assertBidIs(user, bidValue, bid);
    }
    private void assertBidIs(String makeSimpleBidUser, int bidValue, BidDto bid) {
        assertNotNull(bid.id);
        assertEquals(bidValue, bid.amount.value);
        assertEquals("USD", bid.amount.currency);
        assertEquals(makeSimpleBidUser, bid.user.username);
        LOGGER.info("bid time: " + bid.time);
        assertThat(bid.time, notOlderThan(2000));
    }

    private String makeBid(String username, String itemId, int bidValue) {
        setupHeaders(username);
        final var response = testRestTemplate.postForObject(
                "/item/{itemId}/bid/{bid}",
                null,
                String.class,
                itemId,
                bidValue
        );
        return response;
    }

}
