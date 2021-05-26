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
        cy.intercept("**/item?pageIndex=1", req => {
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
            ])
        }).as("items-data-fetcher");
        cy.wait("@items-data-fetcher");
        assertPageTitleIs("Items");
        goToItemsList()
            .assertHasEntry("itemId1", "item-name_1")
            .assertHasEntry("itemId2", "item-name_2");
    })
});