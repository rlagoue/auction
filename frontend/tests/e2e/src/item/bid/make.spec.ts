import {assertPageTitleIs, goToItemDetailsIdentifiedBy, goToItemsList, login, logout} from "../../common"

describe("Make a bid", () => {
    beforeEach(() => {
        cy.visit("/");
        login("user1");
    });

    afterEach(() => {
        logout();
    });

    it("make a bid successfully", () => {
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
                                id: "itemId2",
                                name: "item-name_2",
                                description: "item-description_2",
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
        cy.intercept("**/item/itemId1", req => {
            req.reply(
                {
                    id: "itemId1",
                    name: "item-name_1",
                    description: "item-description_1",
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
        cy.intercept()
        goToItemsList();
        goToItemDetailsIdentifiedBy("itemId1")
            .assertNameIs("item-name_1")
            .assertDescriptionIs("item-description_1")
            .assertStartBidIs("$2.00")
            .assertCurrentBidIs("$12.00")
            .assertBidsCountIs(3)
            .assertHasBid("admin", "2021-05-10T10:11:00", "$2.00")
            .assertHasBid("user1", "2021-05-19T10:11:00", "$10.00")
            .assertHasBid("user2", "2021-05-20T10:11:00", "$12.00");
        assertPageTitleIs("Item Details");
    });

});