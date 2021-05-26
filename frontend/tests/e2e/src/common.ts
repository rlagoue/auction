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

export const goToItemsList = (): any => {
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