import {
    assertPageTitleIs,
    goToItemDetailsIdentifiedBy,
    goToItemsList,
    login,
    logout
} from "../../common"
import moment from "moment";

function assertInitialState() {
    goToItemsList();
    const page = goToItemDetailsIdentifiedBy("itemBidId1")
        .assertNameIs("itemBid-name_1")
        .assertDescriptionIs("itemBid-description_1")
        .assertStartBidIs("$2.00")
        .assertCurrentBidIs("$12.00")
        .assertBidsCountIs(3)
        .assertHasBid("admin", "2021-05-10T10:11:00", "$2.00")
        .assertHasBid("user1", "2021-05-19T10:11:00", "$10.00")
        .assertHasBid("user2", "2021-05-20T10:11:00", "$12.00");
    assertPageTitleIs("Item Details");
    return page;
}

const makeBid = (bid: number): any => {
    cy.get("[data-test-id='make-bid']").click();
    cy.get("[data-test-id='make-bid-input']").should("have.value", "13");
    cy.get("[data-test-id='make-bid-input']").clear().type("" + bid);
    cy.get("[data-test-id='make-bid-submit']").click();
    cy.wait("@item-bid");
}

describe("Make a bid", () => {
    beforeEach(() => {
        cy.visit("/");
        login("user3");

        cy.intercept("**/item?pageIndex=0", req => {
            req.reply(
                {
                    totalCount: 100,
                    items:
                        [
                            {
                                id: "itemBidId1",
                                name: "itemBid-name_1",
                                description: "itemBid-description_1",
                                bids: [
                                    {
                                        user: {
                                            username: "admin"
                                        },
                                        time: "2021-05-10T10:11:00",
                                        amount: {
                                            value: 2,
                                            currency: "USD"
                                        },
                                    },
                                    {
                                        user: {
                                            username: "user1",
                                        },
                                        time: "2021-05-19T10:11:00",
                                        amount: {
                                            value: 10,
                                            currency: "USD"
                                        },
                                    },
                                    {
                                        user: {
                                            username: "user2",
                                        },
                                        time: "2021-05-20T10:11:00",
                                        amount: {
                                            value: 12,
                                            currency: "USD"
                                        },
                                    }
                                ],
                            },
                            {
                                id: "itemBidId2",
                                name: "itemBid-name_2",
                                description: "itemBid-description_2",
                                bids: [
                                    {
                                        user: {
                                            username: "admin"
                                        },
                                        time: "2021-05-10T10:11:00",
                                        amount: {
                                            value: 2,
                                            currency: "USD"
                                        },
                                    },
                                    {
                                        user: {
                                            username: "user1",
                                        },
                                        time: "2021-05-19T10:11:00",
                                        amount: {
                                            value: 10,
                                            currency: "USD"
                                        },
                                    },
                                    {
                                        user: {
                                            username: "user2",
                                        },
                                        time: "2021-05-20T10:11:00",
                                        amount: {
                                            value: 12,
                                            currency: "USD"
                                        },
                                    }
                                ],
                            },
                        ]
                })
        }).as("items-data-fetcher");
        cy.intercept("**/item/itemBidId1", req => {
            req.reply(
                {
                    id: "itemBidId1",
                    name: "itemBid-name_1",
                    description: "itemBid-description_1",
                    bids: [
                        {
                            user: {
                                username: "admin"
                            },
                            time: "2021-05-10T10:11:00",
                            amount: {
                                value: 2,
                                currency: "USD"
                            },
                        },
                        {
                            user: {
                                username: "user1",
                            },
                            time: "2021-05-19T10:11:00",
                            amount: {
                                value: 10,
                                currency: "USD"
                            },
                        },
                        {
                            user: {
                                username: "user2",
                            },
                            time: "2021-05-20T10:11:00",
                            amount: {
                                value: 12,
                                currency: "USD"
                            },
                        }
                    ]
                }
            )
        }).as("item-details-fetcher");
    });

    afterEach(() => {
        logout();
    });

    it("make a bid successfully", () => {
        cy.intercept(
            {
                method: "POST",
                url: "**/item/itemBidId1/bid/14",
            },
            {
                statusCode: 200,
                body: "success"
            },
        ).as("item-bid");
        const page = assertInitialState();
        makeBid(14);
        page.assertNameIs("itemBid-name_1")
            .assertDescriptionIs("itemBid-description_1")
            .assertStartBidIs("$2.00")
            .assertCurrentBidIs("$14.00")
            .assertBidsCountIs(4)
            .assertHasBid("admin", "2021-05-10T10:11:00", "$2.00")
            .assertHasBid("user1", "2021-05-19T10:11:00", "$10.00")
            .assertHasBid("user2", "2021-05-20T10:11:00", "$12.00")
            .assertHasBid("user3", moment().format(), "$14.00", true);
    });

    it("make a bid unsuccessfully", () => {
        cy.intercept(
            {
                method: "POST",
                url: "**/item/itemBidId1/bid/14",
            },
            {
                statusCode: 200,
                body: "outbidded"
            },
        ).as("item-bid");
        const page = assertInitialState();
        cy.intercept("**/item/itemBidId1", req => {
            req.reply(
                {
                    id: "itemBidId1",
                    name: "itemBid-name_1",
                    description: "itemBid-description_1",
                    bids: [
                        {
                            user: {
                                username: "admin"
                            },
                            time: "2021-05-10T10:11:00",
                            amount: {
                                value: 2,
                                currency: "USD"
                            },
                        },
                        {
                            user: {
                                username: "user1",
                            },
                            time: "2021-05-19T10:11:00",
                            amount: {
                                value: 10,
                                currency: "USD"
                            },
                        },
                        {
                            user: {
                                username: "user2",
                            },
                            time: "2021-05-20T10:11:00",
                            amount: {
                                value: 12,
                                currency: "USD"
                            },
                        },
                        {
                            user: {
                                username: "user4",
                            },
                            time: "2021-05-20T11:11:00",
                            amount: {
                                value: 20,
                                currency: "USD"
                            },
                        },
                    ]
                }
            )
        }).as("item-details-fetcher2");
        makeBid(14);
        cy.wait("@item-details-fetcher2")
        cy.get("[data-test-id='make-bid-error-message'")
            .should("contain", "!!!You have been outbidded!!!");
        page.assertNameIs("itemBid-name_1")
            .assertDescriptionIs("itemBid-description_1")
            .assertStartBidIs("$2.00")
            .assertCurrentBidIs("$20.00")
            .assertBidsCountIs(4)
            .assertHasBid("admin", "2021-05-10T10:11:00", "$2.00")
            .assertHasBid("user1", "2021-05-19T10:11:00", "$10.00")
            .assertHasBid("user2", "2021-05-20T10:11:00", "$12.00")
            .assertHasBid("user4", "2021-05-20T11:11:00", "$20.00");
    });

});