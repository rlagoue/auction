import {assertPageTitleIs, goToItemDetailsIdentifiedBy, goToItemsList, login, logout} from "../common"

describe("Add Item as Admin", () => {
    beforeEach(() => {
        cy.visit("/");
        login("admin");
    });

    afterEach(() => {
        logout();
    });

    it("Add a new Item", () => {
        cy.intercept("**/item?pageIndex=1", req => {
            req.reply(
                {
                    totalCount: 100,
                    items: [
                        {
                            id: "itemId1",
                            name: "item-name_1",
                            description: "item-description_1",
                        }
                    ]
                })
        }).as("items-data-fetcher");
        cy.intercept(
            {
                method: "POST",
                url: "**/item",
            },
            {
                statusCode: 200,
                body: "itemAddId"
            },
        ).as("item-add");
        cy.intercept("**/item/itemAddId", req => {
            req.reply(
                {
                    id: "itemAddId",
                    name: "item-name_add",
                    description: "item-description_add",
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
                        }
                    ]
                })
        }).as("item-details-fetcher");
        goToItemsList();
        cy.get("[data-test-id='item-add']").click();
        assertPageTitleIs("Item Add");
        cy.get("[data-test-id='item-add-name']").type("item-name_add");
        cy.get("[data-test-id='item-add-description']").type("item-description_add");
        cy.get("[data-test-id='item-add-bid']").clear().type("2");
        cy.get("[data-test-id='item-add-submit']").click();
        cy.wait("@item-add")
        assertPageTitleIs("Item Details");
        goToItemDetailsIdentifiedBy("itemAddId", false)
            .assertNameIs("item-name_add")
            .assertDescriptionIs("item-description_add")
            .assertStartBidIs("$2.00")
            .assertCurrentBidIs("$2.00")
            .assertBidsCountIs(1)
            .assertHasBid("admin", "2021-05-10T10:11:00", "$2.00")
    });

});