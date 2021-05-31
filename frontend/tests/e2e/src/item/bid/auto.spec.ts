import {assertPageTitleIs, goToItemDetailsIdentifiedBy, goToItemsList, login, logout} from "../../common"

describe("Auto bid support", () => {
    beforeEach(() => {
        cy.visit("/");
        login("user4");
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
                            }
                        ]
                })
        }).as("items-data-fetcher");
    });

    afterEach(() => {
        logout();
    });

    it("Configure Maximum bid amount", () => {
        cy.intercept(
            {
                method: "PUT",
                url: "**/user/user4/settings",
            },
            {
                statusCode: 200,
                body: "success"
            },
        ).as("save-settings");
        cy.intercept(
            {
                method: "GET",
                url: "**/user/user4/settings",
            },
            req => {
                req.reply(
                    {
                        maxBidAmount: {
                            value: 0,
                            currency: "USD"
                        },
                    }
                )
            }).as("settings-fetcher");

        cy.get("[data-test-id='settings']").click();

        cy.wait("@settings-fetcher");
        assertPageTitleIs("Settings");
        setAutoBid(0, 50);
        setAutoBid(50, 100);
    });

    it("Activate auto bid on an item", () => {
        cy.intercept("**/item/itemBidId1", req => {
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
                    ]
                })
        }).as("item-details-fetcher");
        cy.intercept(
            {
                method: "POST",
                url: "**/activate-auto-bid/itemBidId1",
            },
            {
                statusCode: 200,
                body: "success",
            }
        ).as("activate-auto-bid");
        goToItemsList();
        goToItemDetailsIdentifiedBy("itemBidId1");
        assertPageTitleIs("Item Details");
        cy.get("[data-test-id='activate-auto-bid']").click();
        cy.wait("@activate-auto-bid");
    });

});

const setAutoBid = (previousMax: number, nextMax: number) => {
    cy.get("[data-test-id='settings-auto-bid-max-amount']")
        .should("have.value", "" + previousMax);
    cy.get("[data-test-id='settings-auto-bid-max-amount']")
        .clear()
        .type("" + nextMax);
    cy.get("[data-test-id='settings-submit']").click();
    cy.wait("@save-settings").should(({request}) => {
        expect(request.body.maxBidAmount.value).to.equal("" + nextMax);
        expect(request.body.maxBidAmount.currency).to.equal("USD");
    });
}