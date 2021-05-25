import {assertPageTitleIs, goToItemsList, login, logout} from "../common"

describe("Item Listing as Admin", () => {
    beforeEach(() => {
        cy.visit("/");
        login("admin");
    });

    afterEach(() => {
        cy.visit("/");
        logout();
    });

    it("display the list of item", () => {
        cy.intercept("**/item", req => {
            req.reply([
                {
                    id: "itemId1",
                    name: "item-name_1",
                    description: "item-description_1",
                },
                {
                    id: "itemId2",
                    name: "item-name_2",
                    description: "item-description_2",
                }
            ])
        }).as("@items-data-fetcher");
        login("admin");
        cy.wait("@items-data-fetcher");
        assertPageTitleIs("Items");
        goToItemsList()
            .assertHasEntry("itemId1", "item-name_1")
            .assertHasEntry("itemId2", "item-name_2");
    })
});