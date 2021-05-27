import {useDateTimeUtils} from "../../../src/utils";

export const login = (username: string) => {
    cy.intercept(
        {
            method: "POST",
            url: "**/authenticate",
        },
        {
            statusCode: 200,
            body: "token"
        }
    ).as("authenticate");

    cy.get("[data-test-id='username']").type(username);
    cy.get("[data-test-id='password']").type(username);
    cy.get("[data-test-id='submit']").click();
    cy.wait("@authenticate").should(({request}) => {
        expect(request.body.username).to.equal(username);
        expect(request.body.password).to.not.empty;
    });
    cy.wait(250);
};

export function logout() {
    cy.get("[data-test-id='logout']").click();
}

export const assertPageTitleIs = (value: string) => {
    cy.get("[data-test-id='page-title']").should("have.text", value);
}

const {utcDateTimeToLocalString} = useDateTimeUtils();

export const goToItemDetailsIdentifiedBy = (itemId: string): any => {
    cy.get(`[data-test-id='${itemId}']`).click();
    cy.wait("@item-details-fetcher");
    return {
        assertNameIs(value: string) {
            cy.get("[data-test-id='name']")
                .should("contain", value);
            return this;
        },
        assertDescriptionIs(value: string) {
            cy.get("[data-test-id='description']")
                .should("contain", value);
            return this;
        },
        assertStartBidIs(value: string) {
            cy.get("[data-test-id='startBid']")
                .should("contain", value);
            return this;
        },
        assertCurrentBidIs(value: string) {
            cy.get("[data-test-id='currentBid']")
                .should("contain", value);
            return this;
        },
        assertBidsCountIs(value: number) {
            cy.get("[data-test-id='bids'] > tbody > tr")
                .should("have.length", value);
            return this;
        },
        assertHasBid(username: string, time: string, value: string) {
            cy.get(`[data-test-id='bids'] > tbody > [data-test-id='${username}']`)
                .within(() => {
                    cy.get("[data-test-id='bidder']")
                        .should("contain", username);
                    cy.get("[data-test-id='time']")
                        .should("contain", utcDateTimeToLocalString(time));
                    cy.get("[data-test-id='amount']")
                        .should("contain", value);
                });
            return this;
        }
    };
}

export const goToItemsList = (): any => {
    cy.wait("@items-data-fetcher");
    cy.get("[data-test-id='items-list']").should('be.visible');
    return {
        assertHasEntry(id: string, name: string) {
            cy.get(`[data-test-id='items-list'] > [data-test-id='${id}']`)
                .within(() => {
                    cy.get("[data-test-tag='name']")
                        .eq(0)
                        .should("contain", name);
                })
            return this
        },

        assertEntriesCountIs(expectedCount: number) {
            cy.get("[data-test-id='items-list']")
                .children()
                .should("have.length", expectedCount);
            return this;
        },

        filterByName(queryText: string) {
            cy.get("[data-test-id='filterInputId']").type(queryText);
            return this;
        },
    }
}