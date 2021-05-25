export const login = (username: string) => {
    cy.get("[data-test-id='username']").type(username);
    cy.get("[data-test-id='password']").type(username);
    cy.get("[data-test-id='submit']").click();
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
            cy.get(`[data-test-id='items-list'] > tbody > [data-test-id='${id}']`).within(() => {
                cy.get("td")
                    .eq(0)
                    .should("contain", name);
            })
            return this
        }
    }
}