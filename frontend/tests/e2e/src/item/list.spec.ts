import {assertPageTitleIs, goToItemsList, login, logout} from "../common"

describe("Item Listing as Admin", () => {
    beforeEach(() => {
        cy.visit("/");
        login("admin");
    });

    afterEach(() => {
        logout();
    });

    it("display the list of item", () => {
        cy.intercept("**/item?pageIndex=0", req => {
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
                            {
                                id: "itemId3",
                                name: "item-name_3",
                                description: "item-description_3",
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
                                id: "itemId4",
                                name: "item-name_4",
                                description: "item-description_3",
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
                                id: "itemId5",
                                name: "item-name_5",
                                description: "item-description_5",
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
                                id: "itemId6",
                                name: "item-name_6",
                                description: "item-description_6",
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
                            }
                        ]
                })
        }).as("items-data-fetcher");
        cy.intercept("**/item?pageIndex=2", req => {
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
                            {
                                id: "itemId3",
                                name: "item-name_3",
                                description: "item-description_3",
                            },
                            {
                                id: "itemId4",
                                name: "item-name_4",
                                description: "item-description_3",
                            },
                            {
                                id: "itemId5",
                                name: "item-name_5",
                                description: "item-description_5",
                            },
                            {
                                id: "itemId6",
                                name: "item-name_6",
                                description: "item-description_6",
                            }
                        ]
                })
        }).as("items-data-fetcher");
        assertPageTitleIs("Items");
        goToItemsList()
            .assertHasEntry("itemId1", "item-name_1")
            .assertHasEntry("itemId2", "item-name_2");
    });

    it("filter on list items", () => {
        cy.intercept("**/item?pageIndex=0", req => {
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
                            {
                                id: "itemId3",
                                name: "item-name_3",
                                description: "item-description_3",
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
                                id: "itemId4",
                                name: "item-name_4",
                                description: "item-description_3",
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
                                id: "itemId5",
                                name: "item-name_5",
                                description: "item-description_5",
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
                                id: "itemId6",
                                name: "item-name_6",
                                description: "item-description_6",
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
                            }
                        ]
                })
        }).as("items-data-fetcher");
        assertPageTitleIs("Items");
        goToItemsList()
            .assertEntriesCountIs(6)
            .filterByName("item-name_1")
            .assertEntriesCountIs(1)
            .assertHasEntry("itemId1", "item-name_1");
    })
});