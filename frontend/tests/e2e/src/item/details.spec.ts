import {assertPageTitleIs, goToItemDetailsIdentifiedBy, goToItemsList, login, logout} from "../common"

describe("Item Details as Admin", () => {
    beforeEach(() => {
        cy.visit("/");
        login("admin");
    });

    afterEach(() => {
        logout();
    });

    it("display the details of an item", () => {
        cy.intercept("**/item?pageIndex=1", req => {
            req.reply(
                {
                    totalCount: 100,
                    items:
                        [
                            {
                                id: "itemId1",
                                name: "item-name_1",
                                description: "item-description_1",
                            },
                            {
                                id: "itemId2",
                                name: "item-name_2",
                                description: "item-description_2",
                            },
                        ]
                })
        }).as("items-data-fetcher");
        cy.intercept("**/item/itemId1", req => {
            req.reply(
                {
                    id: "itemId1",
                    name: "item-name_1",
                    description: "item-description_1",
                    startBid: {
                      value: 2,
                      currency: "USD"
                    },
                    bids: [
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
                })
        }).as("item-details-fetcher");
        goToItemsList();
        assertPageTitleIs("Item Details");
        goToItemDetailsIdentifiedBy("itemId1")
            .assertNameIs("item-name_1")
            .assertDescriptionIs("item-name_1")
            .assertStartBidIs("$ 2.00")
            .assertCurrentBidIs("$ 12.00")
            .assertBidsCountIs(2)
            .assertHasBid("user1", "19.05.2021 10:11", "$ 10.00")
            .assertHasBid("user2", "20.05.2021 10:11", "$ 12.00");
        assertPageTitleIs("Item Details");
    });

});