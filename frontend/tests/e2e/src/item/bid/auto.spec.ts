import {assertPageTitleIs, login, logout} from "../../common"

describe("Auto bid support", () => {
    beforeEach(() => {
        cy.visit("/");
        login("user4");
        cy.intercept("**/item?pageIndex=1", req => {
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
                        maxBidAmount: 0,
                    }
                )
            }).as("settings-fetcher");

        cy.get("[data-test-id='settings']").click();

        cy.wait("@settings-fetcher");
        assertPageTitleIs("Settings");
        setAutoBid(0, 50);
        setAutoBid(50, 100);
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
        expect(request.body.maxBidAmount).to.equal("" + nextMax);
    });
}